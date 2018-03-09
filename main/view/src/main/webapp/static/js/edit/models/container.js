/**
 * CoCeSo
 * Client JS - edit/models/container
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module {Class} edit/models/container
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/slimunit} SlimUnit
 * @param {module:data/save} ajaxSave
 * @param {module:data/store/hierarchy} store
 * @param {module:utils/conf} conf
 * @param {module:utils/destroy} destroy
 */
define(["jquery", "knockout", "./slimunit", "data/save", "data/store/hierarchy", "utils/conf", "utils/destroy","ko/extenders/boolean", "ko/extenders/list"],
  function($, ko, SlimUnit, ajaxSave, store, conf, destroy) {
    "use strict";

    /**
     * Container model
     *
     * @alias module:edit/models/container
     * @constructor
     * @param {Object} data
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

      this.selected = ko.observable(false).extend({"boolean": true});

      this.spare = ko.observableArray([]);
      this.units = ko.observableArray([]);
      this.subContainer = ko.observableArray([]);

      /**
       * Method to set data
       *
       * @param {Object} data
       */
      this.setData = function(data) {
        data = data || {};

        self.name(data.name || "");
        self.parent(data.parent || null);
        self.ordering(data.ordering || null);
        self.spare(data.spare || []);

        this.units($.map(data.units || {}, function(ordering, id) {
          return new SlimUnit(id, ordering);
        }).sort(function(a, b) {
          return a.ordering - b.ordering;
        }));
      };

      // Set the initial data
      this.setData(data);

      this.setSubContainer = ko.computed(function() {
        this.subContainer(this.id ? store.list.extend({
          list: {
            filter: {parent: this.id},
            sort: true,
            field: "ordering"
          }
        })() : []);
      }, this);


      this.subContainer.model = this;
      this.units.model = this;

      this.save = function() {
        var root = conf.get("containerViewModel");

        ajaxSave(JSON.stringify({
          id: self.id,
          name: self.name(),
          parent: self.parent(),
          ordering: self.ordering()
        }), "container/updateContainer.json", function() {
          root.error(false);
        }, root.saveError, root.httpError);
      };

      this.update = function() {
        this.save();
        this.selected.unset();
      };

      this.remove = function() {
        var root = conf.get("containerViewModel");

        ajaxSave({
          container_id: this.id
        }, "container/removeContainer.json", function() {
          root.error(false);
        }, root.saveError, root.httpError);
      };

      this.add = function() {
        var newcont = new Container({
          name: "New",
          ordering: this.subContainer().length ? this.subContainer()[this.subContainer().length - 1].ordering() + 10.0 : 10.0,
          parent: this.id
        });
        newcont.save();
      };
    };
    Container.prototype = Object.create({}, /** @lends Container.prototype */ {
      /**
       * Destroy the ViewModel
       *
       * @function
       */
      destroy: {
        value: function() {
          ko.utils.arrayForEach(this.units(), function(item) {
            item.destroy();
          });
          destroy(this);
        }
      }
    });

    return Container;
  }
);
