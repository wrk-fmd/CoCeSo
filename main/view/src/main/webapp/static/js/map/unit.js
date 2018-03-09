/**
 * CoCeSo
 * Client JS - map/unit
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module map/unit
 * @param {module:knockout} ko
 * @param {module:utils/destroy} destroyComputed
 */
define(["knockout", "utils/destroy"], function(ko, destroyComputed) {
  "use strict";

  /**
   * Show unit on map
   *
   * @void
   * @param {module:map/models/unit} unit
   * @param {module:map/markerlayer} layer
   */
  var Unit = function(unit, layer) {
    this.id = unit.id;

    this.portable = unit.portable;
    this.isFree = unit.isFree;
    this.isHome = unit.isHome;

    /**
     * Get the content to show in popup
     *
     * @function
     * @type ko.computed
     * @returns {String} Content to show in unit list
     */
    this.popupContent = ko.computed(function() {
      var content = "<li>";
      if (unit.portable) {
        if (unit.isFree()) {
          content += "<span class='glyphicon glyphicon-exclamation-sign'></span>";
        } else if (unit.isHome()) {
          content += "<span class='glyphicon glyphicon-home'></span>";
        } else {
          content += "<span class='glyphicon glyphicon-map-marker'></span>";
        }
      } else {
        content += "<span class='glyphicon glyphicon-record'></span>";
      }
      content += ": " + unit.call.escapeHTML() + "</li>";

      return content;
    }, this);

    this._updatePoint = ko.computed(function() {
      var point = unit.mapPosition();
      layer.moveUnit(this, point ? point.getStatic() : null);
    }, this);

    /**
     * Destroy the object
     */
    this.destroy = function() {
      destroyComputed(this);
      layer.moveUnit(this, null);
    };
  };

  return Unit;
});
