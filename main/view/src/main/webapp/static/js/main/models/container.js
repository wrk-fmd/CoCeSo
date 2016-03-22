/**
 * CoCeSo
 * Client JS - models/main/container
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
 * @module main/models/container
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:data/store/hierarchy} store
 * @param {module:data/store/units} unitsStore
 * @param {module:utils/destroy} destroy
 */
define(["jquery", "knockout", "data/store/hierarchy", "data/store/units", "utils/destroy", "ko/extenders/list"],
  function($, ko, store, unitsStore, destroy) {
    "use strict";

    /**
     * @constructor
     * @alias module:main/models/container
     * @param {Object} data Initial data for the container
     */
    var Container = function(data) {
      var self = this;

      data = data || {};

      /** @type Integer */
      this.id = data.id || null;
      /** @type String */
      this.name = ko.observable("");
      /** @type Integer */
      this.parent = ko.observable(null);
      /** @type Float */
      this.ordering = ko.observable(null);

      /**
       * @function
       * @type ko.observableArray
       */
      this.spare = ko.observableArray([]);

      var units = ko.observable({}),
        filter = ko.computed(function() {
          var f = $.map(Object.keys(units()), function(str) {
            return parseInt(str);
          }).concat(this.spare());

          return f.length > 0 ? f : false;
        }, this);

      /**
       * Method to set data
       *
       * @param {Object} data
       */
      this.setData = function(data) {
        self.name(data.name || "");
        self.parent(data.parent || null);
        self.ordering(data.ordering || null);
        self.spare(data.spare || []);
        units(data.units || {});
      };

      // Set the initial data
      this.setData(data);

      /**
       * @function
       * @type ko.computed
       * @returns {Container[]} Subcontainers of the container
       */
      this.subContainer = this.id  ? store.list.extend({
        list: {
          filter: {parent: this.id},
          sort: true,
          field: "ordering"
        }
      }) : ko.observable([]);

      /**
       * @function
       * @type ko.pureComputed
       * @returns {module:main/models/unit[]} Units in this container
       */
      this.units = unitsStore.list.extend({
        list: {
          filter: {id: filter},
          sort: function(a, b) {
            var u = units();
            return u[a.id]
              ? (u[b.id] ? u[a.id] - u[b.id] : -1)
              : (u[b.id] ? 1 : a.id - b.id);
          }
        }
      });

      /**
       * @function
       * @type ko.computed
       * @returns {Integer} Recursive count of available units
       */
      this.availableCounter = ko.computed(function() {
        var count = ko.utils.arrayFilter(this.units(), function(unit) {
          return unit.isAvailable() || (!unit.portable && unit.isEB());
        }).length;
        ko.utils.arrayForEach(this.subContainer(), function(item) {
          count += item.availableCounter();
        });
        return count;
      }, this);

      /**
       * @function
       * @type ko.computed
       * @returns {Integer} Recursive count of units
       */
      this.totalCounter = ko.computed(function() {
        var count = this.units().length;
        ko.utils.arrayForEach(this.subContainer(), function(item) {
          count += item.totalCounter();
        });
        return count;
      }, this);
    };
    Container.prototype = Object.create({}, /** @lends Container#prototype */ {
      /**
       * @function
       * @return {void}
       */
      destroy: {
        value: function() {
          console.log(this.id);
          destroy(this);
        }
      }
    });

    return Container;
  });