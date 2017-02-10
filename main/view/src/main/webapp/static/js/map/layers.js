/**
 * CoCeSo
 * Client JS - map/layers
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module map/layers
 * @param {module:map/leaflet} L
 * @param {module:map/ajaxgeojson} AjaxGeoJSON
 * @param {module:map/popup} Popup
 * @param {module:map/wfs} getWfsUrl
 * @param {module:utils/i18n} _
 */
define(["./leaflet", "./ajaxgeojson", "./popup", "./wfs", "utils/i18n"], function(L, AjaxGeoJSON, Popup, getWfsUrl, _) {
  "use strict";

  return function() {
    // Define Layers
    var base = {}, overlay = {}, names = {
      basemap: _("map.basemap"),
      ortho: _("map.ortho"),
      osm: _("map.osm"),
      hospitals: _("map.hospitals"),
      oneway: _("map.oneway"),
      defi: _("map.defi")
    };

    base[names.basemap] = L.tileLayer("https://{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpeg", {
      maxZoom: 19,
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      attribution: _("map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
        "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
    });
    base[names.ortho] = L.layerGroup([
      L.tileLayer("https://{s}.wien.gv.at/basemap/bmaporthofoto30cm/normal/google3857/{z}/{y}/{x}.jpeg", {
        maxZoom: 19,
        subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
        bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
        attribution: _("map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
          "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
      }),
      L.tileLayer("https://{s}.wien.gv.at/basemap/bmapoverlay/normal/google3857/{z}/{y}/{x}.png", {
        maxZoom: 19,
        subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
        bounds: [[46.358770, 8.782379], [49.037872, 17.189532]]
      })
    ]);
    base[names.osm] = L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: _("map.source") + ": © <a href='https://openstreetmap.org'>OpenStreetMap</a> contributors"
    });

    overlay[names.hospitals] = new AjaxGeoJSON(getWfsUrl("https://data.wien.gv.at/daten/geo", "ogdwien:KRANKENHAUSOGD"), {
      pointToLayer: function(feature, latlng) {
        return L.marker(latlng, {
          icon: L.icon({
            iconUrl: 'https://data.wien.gv.at/katalog/images/krankenhaus.png',
            iconSize: [16, 16]
          })
        });
      },
      onEachFeature: function(feature, layer) {
        if (feature.properties) {
          layer.bindPopup(new Popup(feature.properties.BEZEICHNUNG, feature.properties.ADRESSE));
        }
      }
    });

    overlay[names.defi] = new AjaxGeoJSON(getWfsUrl("https://data.wien.gv.at/daten/geo", "ogdwien:DEFIBRILLATOROGD"), {
      pointToLayer: function(feature, latlng) {
        return L.marker(latlng, {
          icon: L.icon({
            iconUrl: 'https://data.wien.gv.at/katalog/images/defibrillator.png',
            iconSize: [16, 16]
          })
        });
      },
      onEachFeature: function(feature, layer) {
        if (feature.properties) {
          layer.bindPopup(new Popup(feature.properties.ADRESSE, feature.properties.INFO));
        }
      }
    });

    return {
      base: base,
      overlay: overlay,
      names: names
    };
  };
});
