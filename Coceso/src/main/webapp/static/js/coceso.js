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
  contentBase: "",
  jsonBase: "",
  langBase: "",
  language: "en",
  debug: false,
  keyboardControl: false,
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
          var mutated = false;
          ko.utils.arrayForEach(data, function(item) {
            if (!item.id) {
              return;
            }

            if (Coceso.Data[type].models()[item.id] instanceof Coceso.Models[options.model]) {
              Coceso.Data[type].models()[item.id].setData(item);
            } else {
              Coceso.Data[type].models()[item.id] = new Coceso.Models[options.model](item);
              mutated = true;
            }
          });
          if (mutated) {
            Coceso.Data[type].models.valueHasMutated();
          }
        }
        Coceso.UI.Notifications.connectionError(false);
      },
      complete: function() {
        if (options.interval) {
          options.id = window.setTimeout(Coceso.Ajax.load, options.interval, type);
        }
      },
      error: function(xhr) {
        // 404: not found, 0: no connection to server, 200: error is thrown, because response is not a json (not authenticated)
        if (xhr.status === 404 || xhr.status === 0 || xhr.status === 200) {
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
   * @returns {void}
   */
  save: function(data, url, success, error, httperror) {
    $.ajax({
      type: "POST",
      url: Coceso.Conf.jsonBase + url,
      dataType: "json",
      contentType: (typeof data === "string") ? "application/json" : "application/x-www-form-urlencoded",
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
        if (Coceso.UI) {
          Coceso.UI.Debug.pushHttpError(jqXHR, url, data);
        }
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

/**
 * Helper function to destroy all computated observables in an object
 *
 * @param {Object} obj
 * @returns {void}
 */
Coceso.ViewModels.destroyComputed = function(obj) {
  var i;
  for (i in obj) {
    if (obj.hasOwnProperty(i) && ko.isComputed(obj[i])) {
      obj[i].dispose();
    }
  }
};
