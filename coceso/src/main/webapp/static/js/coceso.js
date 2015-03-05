/**
 * CoCeSo
 * Client JS - global objects and methods
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * Alias for the localization method
 *
 * @global
 * @function
 * @param {String} key
 * @returns {String}
 */
var _ = $.i18n.prop;

/**
 * Object containing the main code
 *
 * @global
 * @namespace Coceso
 * @type Object
 */
var Coceso = {};

/**
 * Some global settings
 *
 * @type Object
 */
Coceso.Conf = {
  interval: 10000,
  logEntryLimit: 30,
  layerBase: "",
  contentBase: "",
  jsonBase: "",
  langBase: "",
  language: "en",
  debug: true,
  keyboardControl: true,
  keyMapping: {
    // 32: Space
    openIncidentKey: 32,
    // 89: Y, 74: J
    yesKey: 89,
    // 78: N
    noKey: 78
  },
  confirmStatusUpdate: true
};

/**
 * Set error handling
 *
 * @param {String} msg Error msg
 * @param {String} url URL of JS file
 * @param {Integer} line Line with error
 * @param {Integer} col Column with error
 * @param {Object} error Error information
 * @returns {void}
 */
window.onerror = function(msg, url, line, col, error) {
  $.ajax({
    type: "POST",
    url: Coceso.Conf.jsonBase + "jslog",
    dataType: "json",
    contentType: "application/json",
    data: JSON.stringify({
      msg: msg,
      url: url,
      line: line,
      col: col,
      stack: error ? error.stack : null
    }),
    processData: false
  });

  return !Coceso.Conf.debug;
};

/**
 * Initialize i18n
 *
 * @returns {void}
 */
Coceso.initi18n = function() {
  $.i18n.properties({
    name: "messages",
    path: Coceso.Conf.langBase,
    mode: "map",
    cache: true,
    language: Coceso.Conf.language
  });
};

/**
 * Initialize autocomplete
 *
 * @returns {void}
 */
Coceso.initAutocomplete = function() {
  //Initialize autocomplete handler
  Coceso.poiAutocomplete = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    limit: 20,
    remote: {
      url: Coceso.Conf.jsonBase + 'poiAutocomplete.json?q=%QUERY'
    }
  });
  Coceso.poiAutocomplete.initialize();
};

/**
 * Contains helpers
 *
 * @type Object
 */
Coceso.Helpers = {
  /**
   * Create error observables
   *
   * @param {Object} obj The object to add the properties to
   * @param {Integer} error Initialize with error
   * @param {Function} load Reloading function called on error
   * @returns {void}
   */
  initErrorHandling: function(obj, error, load) {
    obj.error = ko.observable(error || false);
    obj.errorText = ko.pureComputed(Coceso.Helpers.errorText, obj);

    obj.saveError = function(response) {
      obj.error(response.error || 8);
      if (load instanceof Function) {
        load.call(obj);
      }
    };

    obj.httpError = function() {
      obj.error(7);
      if (load instanceof Function) {
        load.call(obj);
      }
    };
  },
  errorText: function() {
    var error = this.error();

    if (error >= 1 && error <= 8) {
      return _("label.error." + error);
    }

    return "";
  },
  /**
   * Helper function to destroy all computated observables in an object
   *
   * @param {Object} obj
   * @returns {void}
   */
  destroyComputed: function(obj) {
    var i;
    for (i in obj) {
      if (obj.hasOwnProperty(i) && ko.isComputed(obj[i])) {
        obj[i].dispose();
        delete obj[i];
      }
    }
  },
  /**
   * Helper function to delete all properties in an object
   *
   * @param {Object} obj
   * @returns {void}
   */
  cleanObj: function(obj) {
    var i;
    for (i in obj) {
      if (obj.hasOwnProperty(i)) {
        delete obj[i];
      }
    }
  },
  /**
   * Helper function to generate WebSocket url from url
   *
   * @param {String} url HTTP url, may be absolute or relative to page or domain
   * @returns {String} Absolute WebSocket url
   */
  getWSUrl: function(url) {
    if (url.substr(0, 4) !== "http") {
      if (url.charAt(0) !== "/") {
        url = window.location.pathname + "/" + url;
      }
      url = window.location.origin + url;
    }
    return url.replace(/^http/, "ws");
  },
  /**
   * Helper function to format a timestamp
   *
   * @param {String} timestamp
   * @returns {String} Localized datetime string
   */
  fmtTime: function(timestamp) {
    return new Date(timestamp).toLocaleTimeString();
  },
  fmtInterval: function(interval) {
    interval = Math.round(interval / 1000);
    if (interval <= 0) {
      return "0:00";
    }

    var string = "", val;

    val = interval % 60;
    interval = Math.floor(interval / 60);
    string = ":" + (val < 10 ? "0" : "") + val + string;

    if (interval === 0) {
      return "0" + string;
    }
    val = interval % 60;
    interval = Math.floor(interval / 60);

    if (interval === 0) {
      return val + string;
    }
    return interval + ":" + (val < 10 ? "0" : "") + val + string;
  }
};

/**
 * AJAX related functions and data
 *
 * @namespace Coceso.Ajax
 * @type Object
 */
Coceso.Ajax = {
  loadOptions: {},
  /**
   * Load the specified data
   *
   * @param {String} type The data type
   * @returns {void}
   */
  load: function(type) {
    if (!Coceso.Ajax.loadOptions[type]) {
      return false;
    }

    var options = Coceso.Ajax.loadOptions[type];
    if (options.id) {
      window.clearTimeout(options.id);
      options.id = null;
    }
    if (options.interval === null) {
      options.interval = Coceso.Conf.interval;
    }

    $.ajax({
      dataType: "json",
      url: Coceso.Conf.jsonBase + options.url,
      ifModified: true,
      success: function(data, status) {
        if (status !== "notmodified" && Coceso.Models[options.model] instanceof Function) {
          var mutated = false, found = {};
          ko.utils.arrayForEach(data, function(item) {
            if (!item.id) {
              return;
            }
            found[item.id] = true;
            if (Coceso.Data[type].models()[item.id] instanceof Coceso.Models[options.model]) {
              Coceso.Data[type].models()[item.id].setData(item);
            } else {
              Coceso.Data[type].models()[item.id] = new Coceso.Models[options.model](item);
              mutated = true;
            }
          });
          for (var i in Coceso.Data[type].models()) {
            if (!found[i]) {
              Coceso.Data[type].models()[i].destroy();
              delete Coceso.Data[type].models()[i];
              mutated = true;
            }
          }
          if (mutated) {
            Coceso.Data[type].models.valueHasMutated();
          }
        }
        if (Coceso.UI && Coceso.UI.Notifications) {
          Coceso.UI.Notifications.connectionError(false);
        }
      },
      complete: function() {
        if (options.interval) {
          options.id = window.setTimeout(Coceso.Ajax.load, options.interval, type);
        }
      },
      error: function(xhr) {
        // 404: not found, 0: no connection to server, 200: error is thrown, because response is not a json (not authenticated)
        if (Coceso.UI && Coceso.UI.Notifications && (xhr.status === 404 || xhr.status === 0 || xhr.status === 200 || xhr.status === 403)) {
          Coceso.UI.Notifications.connectionError(true);
        }
      }
    });
  },
  /**
   * Save entries with POST
   *
   * @param {Object} data
   * @param {String} url
   * @param {Function} success
   * @param {Function} error
   * @param {Function} httperror
   * @param {String} contentType Optional content type
   * @returns {void}
   */
  save: function(data, url, success, error, httperror, contentType) {
    $.ajax({
      type: "POST",
      url: Coceso.Conf.jsonBase + url,
      dataType: "json",
      contentType: contentType || ((typeof data === "string") ? "application/json" : "application/x-www-form-urlencoded"),
      data: data,
      processData: (typeof data !== "string"),
      success: function(data) {
        if (data.success) {
          if (success instanceof Function) {
            success(data);
          }
        } else {
          if (error instanceof Function) {
            error(data);
          }
        }
      },
      error: function(jqXHR) {
        if (httperror instanceof Function) {
          httperror(jqXHR);
        }
      },
      complete: function() {
        var i;
        for (i in Coceso.Ajax.loadOptions) {
          Coceso.Ajax.load(i);
        }
      }
    });
  }
};

/**
 * WebSocket/STOMP related functions and data
 *
 * @namespace Coceso.Socket
 * @type Object
 */
Coceso.Socket = {
  Client: (function() {
    var obj = {}, subscriptions = {}, client = null;

    function getClient() {
      if (!client && Stomp) {
        client = Stomp.client(Coceso.Helpers.getWSUrl(Coceso.Conf.jsonBase + "socket"));
        client.debug = null;
        client.connect({}, onConnected, onError);
      }
      return client;
    }

    function onConnected() {
      var topic;
      if (Coceso.UI && Coceso.UI.Notifications) {
        Coceso.UI.Notifications.connectionError(false);
      }
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
      if (Coceso.UI && Coceso.UI.Notifications) {
        Coceso.UI.Notifications.connectionError(true);
      }
    }

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
      return 1;
    };

    obj.unsubscribe = function(topic) {
      var s = subscriptions[topic];
      delete subscriptions[topic];
      if (s && s.subscription && client && client.connected) {
        s.subscription.unsubscribe();
      }
    };

    return obj;
  })()
};

/**
 * Constructor for the notification viewmodel
 *
 * @constructor
 */
Coceso.Clock = (function() {
  var obj = {};

  /**
   * Current timestamp
   *
   * @function
   * @type ko.observable
   * @returns {String}
   */
  obj.timestamp = ko.observable(new Date());

  /**
   * Current clock time
   *
   * @function
   * @type ko.observable
   * @returns {String}
   */
  obj.time = ko.pureComputed(function() {
    return Coceso.Helpers.fmtTime(this.timestamp());
  }, obj);

  /**
   * Local clock offset to correct time
   *
   * @type integer
   */
  var offset = 0;

  /**
   * Initialize the clock
   *
   * @returns {void}
   */
  obj.init = function() {
    //Start clock
    $.get(Coceso.Conf.jsonBase + "timestamp", function(data) {
      if (data.time) {
        offset = new Date() - data.time;
      }
    });
    setInterval(function() {
      obj.timestamp(new Date() - offset);
    }, 1000);
  };

  return obj;
})();

/**
 * Contains all methods for the concern locking
 *
 * @type Object
 */
Coceso.Lock = {
  /**
   * Get all existing locks
   *
   * @param {String} json Optional JSON string. If empty localStorage.locks is used
   * @returns {Object}
   */
  getLocks: function(json) {
    var locks = {};
    json = json || localStorage.locks;
    if (locks) {
      try {
        locks = JSON.parse(localStorage.locks) || {};
      } catch (e) {
        locks = {};
      }
    }
    return locks;
  },
  /**
   * Lock the concern selection while this page is open
   *
   * @returns {void}
   */
  lock: function() {
    var locks = Coceso.Lock.getLocks();
    var i = 1;
    while (typeof locks[i] !== "undefined") {
      i++;
    }

    locks[i] = true;
    localStorage.locks = JSON.stringify(locks);

    $(window).on("beforeunload", function() {
      var locks = Coceso.Lock.getLocks();
      delete locks[i];
      localStorage.locks = JSON.stringify(locks);
    });

    $(window).on("storage", function(e) {
      if (e.originalEvent.key === "concern") {
        if (e.originalEvent.newValue) {
          if (window.confirm(_("label.pagereload"))) {
            location.reload();
          }
        } else {
          if (window.confirm(_("label.pageclose"))) {
            window.close();
          }
        }
      }
    });
  },
  /**
   * Determine if concern is locked
   *
   * @param {type} json Optional JSON string with locks. If empty localStorage.locks is used
   * @returns {Boolean}
   */
  isLocked: function(json) {
    var locks = Coceso.Lock.getLocks(json);
    var i;
    for (i in locks) {
      if (locks[i] === true) {
        return true;
      }
    }
    return false;
  }
};

/**
 * Contains all the models
 *
 * @namespace Coceso.Models
 * @type Object
 */
Coceso.Models = {};

/**
 * Contains all ViewModels (including baseclasses)
 *
 * @namespace Coceso.ViewModels
 * @type Object
 */
Coceso.ViewModels = {};

RegExp.escape = function(s) {
  return s.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
};

String.escapeChars = {
  "&": "amp",
  "<": "lt",
  ">": "gt"
};
String.prototype.escapeHTML = function() {
  return this.replace(/[&<>]/g, function(m) {
    return '&' + String.escapeChars[m] + ';';
  });
};