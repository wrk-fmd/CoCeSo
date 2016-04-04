/**
 * CoCeSo
 * Client JS - map/incident
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
 * @module map/incident
 * @param {module:knockout} ko
 * @param {module:leaflet} L
 * @param {module:destroy} destroyComputed
 */
define(["knockout", "./leaflet", "utils/destroy"], function(ko, L, destroyComputed) {
  "use strict";

  /**
   * Show incident on map
   *
   * @constructor
   * @param {module:map/models/incident} inc
   * @param {module:map/markerlayer} layer
   */
  var Incident = function(inc, layer) {
    this.id = inc.id;

    this.type = inc.type;
    this.blue = inc.blue;
    this.isNewOrOpen = inc.isNewOrOpen;
    this.disableBO = inc.disableBO;

    /**
     * Get the content to show in popup
     *
     * @function
     * @type ko.computed
     * @returns {Array} Content to show in incident and unit lists
     */
    this.popupContent = ko.computed(function() {
      var incidents = "", units = "";

      if (inc.isStandby() || inc.isHoldPosition() || inc.isToHome()) {
        var tasks = inc.units();
        if (tasks.length > 0 && tasks[0].unit()) {
          units += "<li><strong>" + inc.typeChar() + "</strong>: " + tasks[0].unit().call.escapeHTML();
          if (tasks[0].isAssigned()) {
            units += " (" + tasks[0].localizedTaskState() + ")";
          }
          units += "</li>";
        }
      } else {
        incidents += "<li>" + inc.assignedTitle();
        if (inc.unitCount()) {
          incidents += "<dl class='dl-horizontal list-narrower'>";
          ko.utils.arrayForEach(inc.units(), function(task) {
            incidents += "<dt>" + (task.unit() && task.unit().call.escapeHTML()) + "</dt><dd>" + task.localizedTaskState() + "</dd>";
          });
          incidents += "</dl>";
        }
        incidents += "</li>";
      }

      return [incidents, units];
    }, this);

    this._updateBo = ko.computed(function() {
      layer.moveBo(this, inc.bo.getStatic());
    }, this);

    this._updateAo = ko.computed(function() {
      layer.moveAo(this, inc.ao.getStatic());
    }, this);

    var line = new L.Polyline([[0, 0], [0, 0]]);

    this._updateLine = ko.computed(function() {
      var aLat = inc.ao.lat(), aLng = inc.ao.lng(),
        bLat = inc.bo.lat(), bLng = inc.bo.lng();

      if (aLat && aLng && bLat && bLng) {
        line.setLatLngs([[bLat, bLng], [aLat, aLng]]);
        layer.addLayer(line);
      } else {
        layer.removeLayer(line);
      }
    }, this);

    this._updateLineColor = ko.computed(function() {
      if (inc.isTask() || inc.isTransport()) {
        line.setStyle({color: inc.blue() ? "#0064cd" : "#9999ff"});
      } else if (inc.isToHome()) {
        line.setStyle({color: "#99ff99"});
      } else {
        line.setStyle({color: "#03f"});
      }
    }, this);

    /**
     * Destroy the object
     */
    this.destroy = function() {
      destroyComputed(this);
      layer.removeLayer(line);
      layer.moveBo(this, null);
      layer.moveAo(this, null);
    };
  };

  return Incident;
});
