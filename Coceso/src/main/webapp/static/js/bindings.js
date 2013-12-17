/**
 * CoCeSo
 * KnockoutJS Custom Bindings
 * Copyright (c) WRK\Daniel Rohr
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright (c) 2013 Daniel Rohr
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.client.js
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Dependencies:
 *	knockout.js
 *	jquery.ui.accordion.js
 *	jquery.ui.draggable.js
 *	jquery.ui.dropable.js
 */

/**
 * Generate the binding to a jQuery UI widget
 *
 * @param {String} widget The jQuery UI widget constructor
 * @return {BindingHandler}
 */
function uiBindingHandler(widget) {
  return {
    init: function(element, valueAccessor) {
      var options = ko.utils.unwrapObservable(valueAccessor()) || {};
      setTimeout(function() {
        $(element)[widget](options);
      }, 0);

      ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
        if ($(element).data("ui-" + widget)) {
          $(element)[widget]("destroy");
        }
      });
    },
    update: function(element, valueAccessor) {
      var options = ko.utils.unwrapObservable(valueAccessor()) || {};
      setTimeout(function() {
        $(element)[widget]("destroy")[widget](options);
      }, 0);
    }
  };
}

/**
 * Generate the binding to refresh a jQuery UI widget
 *
 * @param {String} widget The jQuery UI widget constructor
 * @return {BindingHandler}
 */
function uiBindingHandlerRefresh(widget) {
  return {
    init: function(element, valueAccessor) {
      ko.utils.unwrapObservable(valueAccessor());
    },
    update: function(element, valueAccessor) {
      ko.utils.unwrapObservable(valueAccessor());
      if ($(element).data("ui-" + widget)) {
        $(element)[widget]("refresh");
      }
    }
  };
}

/**
 * Generate Accordion from loop
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.accordion = uiBindingHandler("accordion");

/**
 * Subscribe to refresh accordion
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.accordionRefresh = uiBindingHandlerRefresh("accordion");

/**
 * Generate Draggable from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.draggable = uiBindingHandler("draggable");

/**
 * Generate Droppable from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.droppable = uiBindingHandler("droppable");

/**
 * Force value to be an integer
 *
 * @param {ko.observable} target
 * @param {void} active
 * @returns {ko.computed}
 */
ko.extenders.integer = function(target, active) {
  //create a writeable computed observable to intercept writes to our observable
  var result = ko.computed({
    read: target, //always return the original observables value
    write: function(newValue) {
      var current = target(),
        newValueInt = (newValue && !isNaN(newValue)) ? parseInt(newValue) : 0;

      //only write if it changed
      if (newValueInt !== current) {
        target(newValueInt);
      } else if (newValue !== current) {
        target.notifySubscribers(newValueInt);
      }
    }
  }).extend({notify: 'always'});

  //initialize with current value
  result(target());

  //return the new computed observable
  return result;
};

/**
 * Allow change detection on observable
 *
 * @param {ko.observable} target
 * @param {Object} options
 * @returns {ko.computed}
 */
ko.extenders.observeChanges = function(target, options) {
  target.orig = options.orig ? ko.observable(options.orig) : ko.observable(ko.utils.unwrapObservable(target));
  target.serverChange = ko.observable(null);

  target.equals = function (a, b) {
    if (typeof b === "undefined") {
      b = this;
    }

    a = ko.utils.unwrapObservable(a);
    b = ko.utils.unwrapObservable(b);

    return (typeof options.equals === "function") ? options.equals(a, b) : (a === b);
  };

  target.localChange = ko.computed(function() {
    return !this.equals(this.orig);
  }, target);

  if (options.notify) {
    options.notify.push(target);
  }

  return target;
};

/**
 * Watch multiple observables for changes
 *
 * @param {ko.observable} target
 * @param {Object} options
 * @returns {ko.computed}
 */
ko.extenders.multipleChanges = function(target, options) {
  if (!options.active) {
    target.push = function() {
    };
    return target;
  }

  target.dependencies = ko.observableArray(options.dependencies || []);
  var result = ko.computed(function() {
    var i, dependencies = ko.utils.unwrapObservable(this.dependencies);
    for (i = 0; i < dependencies.length; i++) {
      if (ko.utils.unwrapObservable(dependencies[i].localChange)) {
        return true;
      }
    }
    return false;
  }, target);
  result.dependencies = target.dependencies;
  result.push = function() {
    target.dependencies.push.apply(target.dependencies, arguments);
  };

  return result;
};

/**
 * Get a filtered selection of an array
 *
 * @param {ko.observableArray} target
 * @param {Object} options A filterObject (documented below)
 * @returns {ko.computed}
 * @see applyFilter
 */
ko.extenders.filtered = function(target, options) {
  var compare = function(op, a, b) {
    if (a instanceof Object) {
      var i;
      for (i in a) {
        if (compare(op, a[i], b)) {
          return true;
        }
      }
      return false;
    } else {
      if (op === "not") {
        return (a !== b);
      }

      return (a === b);
    }
  };

  /**
   * Recursively apply filters
   *
   * filterObj has one of the following formats or may even be a mix of both:
   *
   *  {
   *    conn: "and"/"or" (optional, define the logical connection of filters, "and" is default)
   *    filter: {
   *      key1: val1, (matching is done with ===)
   *      key2: val2
   *    }
   *  }
   *
   *  {
   *    conn: "and"/"or"
   *    filter: { (can also be an array)
   *      someKey: filterObj1,
   *      anotherKey: filterObj2,
   *    }
   *  }
   *
   * Each filter may also be an object like
   *  {op: "operator", val: "value"}
   *
   * @param {Object} filterObj
   * @param {ViewModelSingle} val The ViewModel to check
   * @return {boolean} True if
   */
  var applyFilter = function(filterObj, val) {
    var and = (filterObj.conn === "or") ? false : true;
    var i;
    for (i in filterObj.filter) {
      //Check all objects in filter
      if (typeof filterObj.filter[i] !== "undefined") {
        var ret;
        if (typeof filterObj.filter[i].filter !== "undefined") {
          //Checked filter is actually another filterObj: Recursive call
          ret = applyFilter(filterObj.filter[i], val);
        } else {
          //Compare
          if (typeof val[i] === "undefined") {
            ret = false;
          } else {
            var filter = filterObj.filter[i], op = "equal";
            if ((typeof filter.op !== "undefined") && (typeof filter.val !== "undefined")) {
              op = filter.op;
              filter = filter.val;
            } else if (typeof filter.val !== "undefined") {
              filter = filter.val;
            }

            ret = compare(op, filter, ko.utils.unwrapObservable(val[i]));
          }
        }

        if (ret !== and) {
          //"and" connection and this result is false => return false
          //"or" connection and this result is true => return true
          return ret;
        }
      }
    }
    //"and" connection: no result was false, so return true
    //"or" connection: no result was true, so return false
    return and;
  };

  return ko.computed(function() {
    var data = ko.utils.unwrapObservable(target) || [],
      filters = ko.utils.unwrapObservable(options) || {};

    if (!filters.filter || !data.length) {
      //No filters or no data: Return an empty array
      return data;
    }

    return ko.utils.arrayFilter(data, function(val) {
      //Apply the filters to all child elements
      return applyFilter(filters, val);
    });
  });
};
