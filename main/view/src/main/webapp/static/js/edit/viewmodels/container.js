/**
 * CoCeSo
 * Client JS - edit_user/viewmodel
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
 * @module {Class} edit/viewmodels/container
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/container} Container
 * @param {module:edit/models/slimunit} SlimUnit
 * @param {module:data/load} load
 * @param {module:data/save} ajaxSave
 * @param {module:data/store/hierarchy} store
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["jquery", "knockout", "../models/container", "../models/slimunit", "data/load", "data/save",
  "data/store/hierarchy", "utils/conf", "utils/errorhandling",
  "knockout-sortable", "ko/bindings/select"],
  function($, ko, Container, SlimUnit, load, ajaxSave, store, conf, initErrorHandling) {
    "use strict";

    function computeOrdering(array, index) {
      if (!(array instanceof Array) || index < 0 || index >= array.length) {
        return 0.0;
      }
      if (index === 0) {
        return (array.length > 1) ? ko.utils.unwrapObservable(array[1].ordering) / 2.0 : 10.0;
      }
      if (index === array.length - 1) {
        return ko.utils.unwrapObservable(array[array.length - 2].ordering) + 10.0;
      }
      return (ko.utils.unwrapObservable(array[index - 1].ordering) + ko.utils.unwrapObservable(array[index + 1].ordering)) / 2.0;
    }

    load({
      url: "container/getAll.json",
      stomp: "/topic/container/{c}",
      model: Container,
      store: store.models,
      cbSet: function(data) {
        if (!data.id) {
          store.dummy(new Container(data));
          return false;
        }
        if (store.models()[data.id] instanceof Container) {
          store.models()[data.id].setData(data);
          return false;
        }
        store.models()[data.id] = new Container(data);
        return true;
      }
    });

    /**
     * Viewmodel for editing containers
     *
     * @alias module:edit/viewmodels/container
     * @constructor
     */
    var EditContainer = function() {
      var self = this;

      conf.set("containerViewModel", this);

      //Error handling for the hierarchy view
      initErrorHandling(this);

      this.top = store.root;
      this.spare = ko.observableArray([]);

      this.setSpare = ko.computed(function() {
        this.spare(store.root() ? $.map(store.root().spare(), function(item) {
          return new SlimUnit(item);
        }) : []);
      }, this);

      this.hasRoot = ko.computed(function() {
        return this.top() && this.top().id;
      }, this);

      this.createRoot = function() {
        var newcont = new Container({
          name: "Root",
          ordering: 0
        });
        newcont.save();
      };

      this.drop = function(data) {
        if (!(data.targetParent.model instanceof Container && data.item instanceof Container)) {
          return;
        }
        data.item.ordering(computeOrdering(data.targetParent(), data.targetIndex));
        data.item.parent(data.targetParent.model.id);
        data.item.save();
      };

      this.dropUnit = function(data) {
        if (!(data.item instanceof SlimUnit)) {
          return;
        }

        if (data.targetParent === self.spare) {
          ajaxSave({
            unit_id: data.item.id
          }, "container/removeUnit.json", function() {
            self.error(false);
          }, self.saveError, self.httpError);
        } else if (data.targetParent.model instanceof Container) {
          data.item.ordering = computeOrdering(data.targetParent(), data.targetIndex);
          ajaxSave({
            container_id: data.targetParent.model.id,
            unit_id: data.item.id,
            ordering: data.item.ordering
          }, "container/updateUnit.json", function() {
            self.error(false);
          }, self.saveError, self.httpError);
        }
      };
    };

    return EditContainer;
  }
);
