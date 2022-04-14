/**
 * CoCeSo
 * Client JS - main/viewmodels/hierarchy
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
 * @module main/viewmodels/hierarchy
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:main/models/container} Container
 * @param {module:data/load} load
 * @param {module:data/stomp} stomp
 * @param {module:data/store/hierarchy} store
 * @param {module:utils/i18n} _
 */
define(["jquery", "knockout", "../models/container", "data/load", "data/stomp", "data/store/hierarchy", "utils/i18n", "ko/bindings/toggle"],
  function($, ko, Container, load, stomp, store, _) {
    "use strict";

    var loadOptions = {
      url: "container/getAll",
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
    };

    /**
     * ViewModel for hierarchical view in Unit Window
     *
     * @constructor
     * @param {Object} options
     */
    var Hierarchy = function(options) {
      if (store.count <= 0) {
        store.count++;
        load(loadOptions);
      }

      this.top = store.root;

      var title = _("units") + ": " + _("main.unit.hierarchy");
      this.dialogTitle = ko.pureComputed(function() {
        return {dialog: title + (this.top() ? " (" + this.top().availableCounter() + "/" + this.top().totalCounter() + ")" : ""), button: title};
      }, this);

      var optHidden = (options && Array.isArray(options.hidden)) ? options.hidden : [],
        hidden = ko.observableArray([]);

      this.dialogState = ko.computed(function() {
        return {
          hidden: hidden()
        };
      }, this);

      this.isHidden = function(id) {
        return $.inArray(id, optHidden) !== -1;
      };

      this.toggleContainer = function(id, hide) {
        if (hide) {
          hidden.push(id);
        } else {
          hidden.remove(id);
        }
      };
    };
    Hierarchy.prototype = Object.create({}, /** @lends Hierarchy.prototype */ {
      /**
       * Destroy the ViewModel
       *
       * @function
       * @return {void}
       */
      destroy: {
        value: function() {
          this.dialogTitle.dispose();
          store.count--;
          if (store.count <= 0) {
            store.models({});
            store.dummy(null);
            stomp.unsubscribe(loadOptions.stomp.replace(/{c}/, localStorage.concern));
          }
        }
      }
    });

    return Hierarchy;
  }
);
