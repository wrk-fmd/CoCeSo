/**
 * CoCeSo
 * Client JS - ko/extenders/list
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
 * @module ko/extenders/list
 * @param {module:knockout} ko
 */
define(["knockout", "./boolean"], function(ko) {
  "use strict";

  /**
   * Compare values
   *
   * @param {String} op Comparison operator ("equal"/"not" - defaults to "equal")
   * @param {mixed|Array|RegExp} a Value, Array of values or RexExp
   * @param {mixed} b Value to compare to
   * @returns {Boolean}
   */
  function compare(op, a, b) {
    if (a instanceof RegExp) {
      return a.test(b);
    }

    if (a instanceof Array) {
      if (!a.length) {
        return true;
      }
      var i;
      for (i = 0; i < a.length; i++) {
        if (compare(op, a[i], b)) {
          return true;
        }
      }
      return false;
    } else {
      if (typeof b === "boolean") {
        if (a === "true") {
          a = true;
        } else if (a === "false") {
          a = false;
        }
      }
      if (op === "not") {
        return (a !== b);
      }

      return (a === b);
    }
  }



  /**
   * Recursively apply filters
   *
   * A filterObject has the following format:
   *  {
   *    conn: "and"/"or", (optional, define the logical connection of filters, "and" is default)
   *    key1: filter1, (matching is done with ===)
   *    key2: filter2
   *  }
   *
   * Each filter may be just a value to compare to or an object like
   *  {val: "value", op: "not"}
   * where value can also be an array of possible values and op is optional
   *
   * filterObj can also be an Array (with optional property "conn") containing multiple filterObjects
   *
   * @param {Object|Array} filterObj
   * @param {ViewModelSingle} val The ViewModel to check
   * @return {boolean} True if
   */
  function applyFilter(filterObj, val) {
    var and = (filterObj.conn === "or") ? false : true, i;

    //Array of filterObj
    if (filterObj instanceof Array) {
      for (i = 0; i < filterObj.length; i++) {
        if (applyFilter(filterObj[i], val) !== and) {
          return !and;
        }
      }
      return and;
    }

    //Object with filter options
    for (i in filterObj) {
      if (!filterObj.hasOwnProperty(i) || (i === "conn")) {
        continue;
      }

      var filter = filterObj[i], ret;
      if (filter instanceof Function && !ko.isObservable(filter)) {
        ret = filter(val);
      } else if (typeof val[i] === "undefined") {
        ret = false;
      } else {
        var op = null;
        if (filter) {
          if ((typeof filter.op !== "undefined") && (typeof filter.val !== "undefined")) {
            op = filter.op;
            filter = filter.val;
          } else if (typeof filter.val !== "undefined") {
            filter = filter.val;
          }
        }

        ret = compare(op, ko.utils.unwrapObservable(filter), ko.utils.unwrapObservable(val[i]));
      }

      if (ret !== and) {
        //"and" connection and this result is false => return false
        //"or" connection and this result is true => return true
        return ret;
      }
    }

    //"and" connection: no result was false, so return true
    //"or" connection: no result was true, so return false
    return and;
  }
  ;


  /**
   * Get a filtered and sorted selection of an array
   *
   * @param {ko.observableArray} target
   * @param {Object} options
   * @returns {ko.computed}
   * @see applyFilter
   */
  ko.extenders.list = function(target, options) {
    var field = null, asc = null;
    if (options.sort === true) {
      field = ko.observable(options.field || []);
      asc = ko.observable(options.asc !== false).extend({"boolean": true});
      options.sort = function(a, b) {
        var fields = field();
        if (!(fields instanceof Array)) {
          fields = [fields];
        }
        for (var i = 0; i < fields.length; i++) {
          var f = fields[i];
          if (f && typeof a[f] !== "undefined" && typeof b[f] !== "undefined") {
            var va = ko.utils.unwrapObservable(a[f]);
            var vb = ko.utils.unwrapObservable(b[f]);
            if (typeof va === "string") {
              va = va.toLowerCase();
            }
            if (typeof vb === "string") {
              vb = vb.toLowerCase();
            }
            if (va === null || va < vb) {
              return asc() ? -1 : 1;
            }
            if (b === null || va > vb) {
              return asc() ? 1 : -1;
            }
          }
        }
        return 0;
      };
    }

    var ret = ko.computed(function() {
      var data = ko.utils.unwrapObservable(target) || [];

      if (!data.length) {
        return data;
      }

      var filter = options.filter ? ko.utils.unwrapObservable(options.filter) : null,
        sort = options.sort ? ko.utils.unwrapObservable(options.sort) : null;

      if (filter) {
        data = ko.utils.arrayFilter(data, function(val) {
          //Apply the filters to all child elements
          return applyFilter(filter, val);
        });
      }

      if (sort) {
        data = data.sort(sort);
      }

      return data;
    });

    if (field && asc) {
      ret.field = field;
      ret.asc = asc;
      ret.sort = function(field) {
        if (this.field() === field) {
          this.asc.toggle();
        } else {
          this.field(field);
          this.asc(true);
        }
      };
      ret.icon = function(f) {
        if (field() === f) {
          return asc() ? "glyphicon-sort-by-attributes" : "glyphicon-sort-by-attributes-alt";
        }
        return "glyphicon-sort";
      };
    }

    return ret;
  };
});
