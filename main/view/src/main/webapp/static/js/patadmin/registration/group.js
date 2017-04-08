/**
 * CoCeSo
 * Client JS - patadmin/registration/group
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
 * @module patadmin/registration/group
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 * @param {module:utils/constants} constants
 */
define(["knockout", "utils/conf", "utils/constants", "ko/extenders/isvalue"],
  function(ko, conf, constants) {
    "use strict";

    /**
     * Single group
     *
     * @constructor
     * @alias module:patadmin/registration/group
     * @param {Object} data Initial data for the group
     */
    var Group = function(data) {
      var self = this;
      data = data || {};

      //Create basic properties
      this.id = data.id;
      this.call = data.call || "";

      this.capacity = ko.observable(0);
      this.patients = ko.observable(0);
      this.state = ko.observable(constants.Unit.state.ad);

      /**
       * Method to set data
       *
       * @param {Object} data
       * @returns {void}
       */
      this.setData = function(data) {
        self.capacity(data.capacity || 0);
        self.patients(data.patients || 0);
        self.state(data.state || constants.Unit.state.ad);
      };

      //Set data
      this.setData(data);

      this.url = conf.get("groupUrl") + this.id;
      this.image = data.imgsrc ? conf.get("imageBase") + "groups/" + data.imgsrc : null;

      this.isActive = this.state.extend({isValue: constants.Unit.state.eb});

      this.occupation = ko.computed(function() {
        var i, html = "", patients = this.patients(), capacity = this.isActive() ? this.capacity() : 0;

        if (patients > 0) {
          for (i = 0; i < Math.min(patients, capacity); i++) {
            html += "<img src='" + conf.get("imageBase") + "capacity/occupied.png' alt='X'/>";
          }
        }
        if (patients > capacity) {
          for (i = 0; i < (patients - capacity); i++) {
            html += "<img src='" + conf.get("imageBase") + "capacity/closed.png' alt='X'/>";
          }
        } else if (patients < capacity) {
          for (i = 0; i < (capacity - patients); i++) {
            html += "<img src='" + conf.get("imageBase") + "capacity/free.png' alt='O'/>";
          }
        }

        return html;
      }, this);

    };

    return Group;
  }
);
