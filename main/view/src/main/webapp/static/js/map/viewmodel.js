/**
 * CoCeSo
 * Client JS - main/viewmodels/incidents
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout", "./leaflet", "./layers", "./legend", "./markerlayer", "./nocoords",
  "./incident", "./unit", "data/store/incidents", "data/store/units",
  "utils/conf", "utils/clean", "utils/destroy", "utils/plugins", "utils/i18n", "ko/extenders/list"],
    function($, ko, L, layers, Legend, MarkerLayer, NoCoordsControl, Incident, Unit, incidentsStore, unitsStore, conf, clean, destroyComputed, getPlugins, _) {
      "use strict";

      /**
       * Constructor for the situation map
       *
       * @constructor
       * @param {Object} options
       */
      var Map = function(options) {
        var self = this;
        options = options || {};
        this.dialogTitle = options.title || _("map");

        var locate = true;
        if (options.c) {
          if (!(options.c instanceof Array)) {
            options.c = options.c.split(",");
          }
          if (options.c.length >= 2) {
            options.c = options.c.slice(0, 2);
            options.c = $.map(options.c, window.parseFloat);
            if (!isNaN(options.c[0]) && !isNaN(options.c[1])) {
              locate = false;
            }
          }
        }
        if (locate) {
          options.c = [48.2, 16.35];
        }
        if (options.z) {
          options.z = parseInt(options.z);
          if (isNaN(options.z)) {
            options.z = 13;
          }
        } else {
          options.z = 13;
        }

        // Create marker layer
        var noCoordsControl = new NoCoordsControl(),
            markerLayer = new MarkerLayer(noCoordsControl),
            incidentMarkers = {},
            unitMarkers = {};

        // Add markers to layer
        var incidents = incidentsStore.list.extend({list: {filter: {isDone: false}}});
        this._updateIncidentList = ko.computed(function() {
          var found = {}, id;
          ko.utils.arrayForEach(incidents(), function(inc) {
            if (!incidentMarkers[inc.id]) {
              incidentMarkers[inc.id] = new Incident(inc, markerLayer);
            }
            found[inc.id] = true;
          });
          for (id in incidentMarkers) {
            if (!found[id]) {
              incidentMarkers[id].destroy();
              delete incidentMarkers[id];
            }
          }
        }, this);

        this._updateUnitList = ko.computed(function() {
          var found = {}, id;
          ko.utils.arrayForEach(unitsStore.list(), function(unit) {
            if (!unitMarkers[unit.id]) {
              unitMarkers[unit.id] = new Unit(unit, markerLayer);
            }
            found[unit.id] = true;
          });
          for (id in unitMarkers) {
            if (!found[id]) {
              unitMarkers[id].destroy();
              delete unitMarkers[id];
            }
          }
        }, this);

        // Initalize map after UI is loaded
        var map;

        /**
         * Initialize the ViewModel
         *
         * @returns {void}
         */
        this.init = function() {
          map = L.map(this.ui ? this.ui + "-map-container" : "map-container", {
            center: options.c, zoom: options.z,
            minZoom: 7, maxZoom: 19,
            maxBounds: [[46.358770, 8.782379], [49.037872, 17.189532]]
          });

          // Center map on current position
          if (locate) {
            map.locate({setView: true});
          }

          // Set query string based on events
          var baseOption = options.b || "basemap", overlayOption = options.o || [], names = {};
          if (!(overlayOption instanceof Array)) {
            overlayOption = [overlayOption];
          }
          delete options.b;
          delete options.o;

          // Set link to full version
          var fullLink = $("<a href='" + conf.get("contentBase") + "map' target='_blank'>" + _("map.full") + "</a>");
          if (this.ui) {
            map.attributionControl.setPrefix(fullLink.prop("outerHTML"));
          } else {
            map.attributionControl.setPrefix(false);
          }

          function setQuery() {
            for (var key in options) {
              if (!options[key]) {
                delete options[key];
              }
            }
            if (self.ui) {
              fullLink.prop("href", conf.get("contentBase") + "map?" + $.param(options, true));
              map.attributionControl.setPrefix(fullLink.prop("outerHTML"));
            } else {
              window.history.replaceState(null, null, "?" + $.param(options, true));
            }
          }

          function findName(name) {
            for (var key in names) {
              if (names[key] === name) {
                return key;
              }
            }
            return null;
          }

          map.on("moveend", function() {
            var c = map.getCenter();
            options.c = c ? [c.lat, c.lng] : null;
            options.z = map.getZoom();
            setQuery();
          });
          map.on("baselayerchange", function(e) {
            options.b = findName(e.name);
            setQuery();
          });
          map.on("overlayadd", function(e) {
            var name = findName(e.name);
            if (name) {
              if (options.o instanceof Array) {
                options.o.push(name);
              } else {
                options.o = [name];
              }
            }
            setQuery();
          });
          map.on("overlayremove", function(e) {
            if (options.o instanceof Array) {
              var name = findName(e.name);
              if (name) {
                for (var i = 0; i <= options.o.length; i++) {
                  if (options.o[i] === name) {
                    options.o.splice(i, 1);
                  }
                }
              }
            }
            setQuery();
          });

          // Add layers
          var layersControl = L.control.layers();
          map.addControl(layersControl);

          function addLayers(layers) {
            $.extend(names, layers.names);

            ko.utils.objectForEach(layers.base, function(name, layer) {
              layersControl.addBaseLayer(layer, name);
              if (layers.names[baseOption] === name) {
                map.addLayer(layer);
              }
            });

            ko.utils.objectForEach(layers.overlay, function(name, layer) {
              layersControl.addOverlay(layer, name);
              if (ko.utils.arrayFirst(overlayOption, function(item) {
                return (layers.names[item] === name);
              }) !== null) {
                map.addLayer(layer);
              }
            });
          }

          addLayers(layers());
          getPlugins("layers", function(plugin) {
            addLayers(plugin());
          });

          // Add controls
          map.addControl(L.control.scale({imperial: false, maxWidth: 300}));
          map.addControl(new Legend());
          map.addControl(noCoordsControl);
          map.addLayer(markerLayer);

          // Listen to resize of UI container
          if (this.ui) {
            $("#" + this.ui).on("dialogresizestop dialogopen", function() {
              map.invalidateSize();
            });
          }
        };

        /**
         * Destroy the viewmodel
         *
         * @returns {void}
         */
        this.destroy = function() {
          destroyComputed(this);
          var i;
          for (i in incidentMarkers) {
            incidentMarkers[i].destroy();
            delete incidentMarkers[i];
          }
          for (i in unitMarkers) {
            unitMarkers[i].destroy();
            delete unitMarkers[i];
          }

          if (this.ui) {
            $("#" + this.ui).off("dialogresizestop");
          }

          map.remove();
          clean(map);
          map = markerLayer = noCoordsControl = null;
        };
      };

      return Map;
    });
