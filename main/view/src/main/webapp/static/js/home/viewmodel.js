/**
 * CoCeSo
 * Client JS - home/viewmodel
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
 * @module {Class} home/viewmodel
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:home/concern} Concern
 * @param {module:home/createconcern} CreateConcern
 * @param {module:utils/conf} conf
 * @param {module:utils/lock} lock
 * @param {module:utils/errorhandling} initErrorHandling
 * @param {module:js-cookie} Cookies
 */
define(["jquery", "knockout", "./concern", "./createconcern",
  "utils/conf", "utils/lock", "utils/errorhandling", "js-cookie", "ko/extenders/list"],
  function($, ko, Concern, CreateConcern, conf, lock, initErrorHandling, Cookies) {
    "use strict";

    /**
     * Model for the home view
     *
     * @alias module:home/viewmodel
     * @constructor
     */
    var Home = function() {
      var self = this;

      // Concern locking
      this.locked = ko.observable(lock.isLocked());
      $(window).on("storage", function(e) {
        if (e.originalEvent.key === "locks") {
          self.locked(lock.isLocked());
        }
      });

      this.forceUnlock = function() {
        delete localStorage.locks;
        this.locked(false);
      };

      // Active concern
      var concernId = parseInt(Cookies.get("concern"));
      if (!concernId || isNaN(concernId)) {
        concernId = null;
      }
      localStorage.concern = concernId || "";
      this.concernId = ko.observable(concernId);
      $(window).on("storage", function(e) {
        if (e.originalEvent.key === "concern") {
          var newId = parseInt(e.originalEvent.newValue);
          if (!newId || isNaN(newId)) {
            newId = null;
          }
          self.concernId(newId);
        }
      });

      // Concern lists
      this.concerns = ko.observableArray([]);
      this.open = this.concerns.extend({list: {filter: {closed: false}}});
      this.closed = this.concerns.extend({list: {filter: {closed: true}}});

      this.concernName = ko.computed(function() {
        var id = this.concernId();
        if (!id) {
          return "";
        }
        var concern = ko.utils.arrayFirst(this.concerns(), function(item) {
          return item.id === id;
        });
        return (concern ? concern.name : "");
      }, this);

      // Model for the create form
      this.create = new CreateConcern(this);

      // Load concern lists
      this.load = function() {
        $.getJSON(conf.get("jsonBase") + "concern/getAll", function(data, status) {
          if (status !== "notmodified") {
            self.concerns($.map(data, function(item) {
              return new Concern(item, self);
            }));
          }
        });
      };
      this.load();

      // Error Handling
      initErrorHandling(this, conf.get("initialError"), this.load);
    };

    return Home;
  }
);
