/**
 * CoCeSo
 * Client JS - main/viewmodels/hierarchy
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
 * @module main/viewmodels/hierarchy
 * @param {module:knockout} ko
 * @param {module:main/models/container} Container
 * @param {module:data/load} load
 * @param {module:data/stomp} stomp
 * @param {module:data/store/hierarchy} store
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "../models/container", "data/load", "data/stomp", "data/store/hierarchy", "utils/destroy", "utils/i18n"],
  function(ko, Container, load, stomp, store, destroy, _) {
    "use strict";

    var options = {
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
    };

    /**
     * ViewModel for hierarchical view in Unit Window
     *
     * @constructor
     */
    var Hierarchy = function() {
      if (store.count <= 0) {
        store.count++;
        load(options);
      }

      this.top = store.root;

      var title = _("units") + ": " + _("main.unit.hierarchy");
      this.dialogTitle = ko.pureComputed(function() {
        return {dialog: title + (this.top() ? " (" + this.top().availableCounter() + "/" + this.top().totalCounter() + ")" : ""), button: title};
      }, this);
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
            stomp.unsubscribe(options.stomp.replace(/{c}/, localStorage.concern));
          }
        }
      }
    });

    return Hierarchy;
  }
);
