/**
 * CoCeSo
 * Client JS - data/stomp
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
 * @module {Object} data/stomp
 * @param {module:utils/conf} conf
 */
define(["utils/conf", "stomp"], function(conf, Stomp) {
  "use strict";

  /**
   * @alias module:data/stomp
   */
  var obj = {};
  var subscriptions = {}, client = null;

  function getWSUrl(url) {
    if (url.substr(0, 4) !== "http") {
      if (url.charAt(0) !== "/") {
        url = window.location.pathname + "/" + url;
      }
      url = window.location.origin + url;
    }
    return url.replace(/^http/, "ws");
  }

  function getClient() {
    if (!client && Stomp) {
      client = Stomp.client(getWSUrl(conf.get("jsonBase") + "socket"));
      client.debug = null;
      client.connect({}, onConnected, onError);
    }
    return client;
  }

  function onConnected() {
    var topic;
    conf.get("error")(false);
    for (topic in subscriptions) {
      subscriptions[topic].subscription = client.subscribe(topic, subscriptions[topic].callback);
    }
  }

  function onError() {
    if (!client || !client.connected) {
      // Error connecting or connection lost
      client = null;
      setTimeout(getClient, 5000);
    }
    conf.get("error")(true);
  }

  /**
   * @callback onReceive
   * @param {Object} data
   */

  /**
   * Subscribe to a topic
   * @param {string} topic
   * @param {onReceive} callback
   */
  obj.subscribe = function(topic, callback) {
    if (subscriptions[topic]) {
      obj.unsubscribe(topic);
    }
    subscriptions[topic] = {
      callback: callback
    };
    var _client = getClient();
    if (_client.connected) {
      subscriptions[topic].subscription = _client.subscribe(topic, callback);
    }
  };

  /**
   * Unsubscribe from a topic
   * @param {string} topic
   */
  obj.unsubscribe = function(topic) {
    var s = subscriptions[topic];
    delete subscriptions[topic];
    if (s && s.subscription && client && client.connected) {
      s.subscription.unsubscribe();
    }
  };

  return obj;
});
