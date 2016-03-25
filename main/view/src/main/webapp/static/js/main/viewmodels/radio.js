/**
 * CoCeSo
 * Client JS - main/viewmodels/radio
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
 * @module main/viewmodels/radio
 * @param {module:knockout} ko
 * @param {module:main/models/call} Call
 * @param {module:data/load} load
 * @param {module:data/stomp} stomp
 * @param {module:data/store/radio} store
 * @param {module:utils/conf} conf
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "../models/call", "data/load", "data/stomp", "data/store/radio",
  "utils/conf", "utils/destroy", "utils/i18n", "ko/bindings/accordion"],
  function(ko, Call, load, stomp, store, conf, destroy, _) {
    "use strict";

    var options = {
      url: "radio/getLast/5.json",
      stomp: "/topic/radio/incoming",
      cbFull: function(data) {
        ko.utils.arrayForEach(data, function(item) {
          store.calls.unshift(new Call(item));
        });
      },
      cbSet: function(data) {
        store.calls.unshift(new Call(data));
        return false;
      },
      cbDelete: function() {}
    };


    /**
     * ViewModel for incoming radio calls
     *
     * @alias module:main/viewmodels/radio
     * @constructor
     */
    var Radio = function() {
      var self = this, minutes = 5;


      if (store.count <= 0) {
        store.count++;
        load(options);

        // Load available ports
        (function getPorts() {
          $.ajax({
            dataType: "json",
            url: conf.get("jsonBase") + "radio/ports.json",
            success: function(data) {
              store.ports(data);
            },
            error: function() {
              // Error loading ports, try again
              window.setTimeout(getPorts, 5000);
            }
          });
        })();

        // Remove all entries older than 10 minutes
        store.interval = window.setInterval(function() {
          var time = new Date() - minutes * 60000;
          store.calls.remove(function(call) {
            return call.timestamp < time;
          });
        }, 60000);
      }

      var radio = _("radio");

      this.port = ko.observable();
      this.dialogTitle = ko.computed(function() {
        var p = this.port();
        return p ? radio + ": " + p.name : radio;
      }, this);

      this.calls = ko.computed(function() {
        var data = store.calls().sort(function(a, b) {
          return b.timestamp - a.timestamp;
        }), calls = [], last = null, port = self.port();
        ko.utils.arrayForEach(data, function(item) {
          if (port && item.port && port.path !== item.port) {
            return;
          }
          if (last && last.call.ani === item.ani) {
            last.additional.push(item);
            if (item.emergency) {
              last.emergency = true;
            }
          } else {
            last = {call: item, additional: [], emergency: item.emergency};
            calls.push(last);
          }
        });
        return calls;
      }, this);

      this.ports = store.ports;

      this.accordionOptions = {
        active: false,
        collapsible: true,
        heightStyle: "content",
        beforeActivate: function(event, ui) {
          return (!ui.newHeader || !ui.newHeader.hasClass("no-open"));
        }
      };
    };
    Radio.prototype = Object.create({}, /** @lends Radio.prototype */ {
      /**
       * Destroy the ViewModel
       *
       * @function
       * @return {void}
       */
      destroy: {
        value: function() {
          destroy(this);
          store.count--;
          if (store.count <= 0) {
            stomp.unsubscribe(options.stomp);
            window.clearInterval(store.interval);

            ko.utils.arrayForEach(store.calls(), store.calls(), function(call) {
              destroy(call);
            });
            store.calls([]);

            store.aniMap = {};
            store.ports([]);
          }
        }
      }
    });

    return Radio;
  }
);
