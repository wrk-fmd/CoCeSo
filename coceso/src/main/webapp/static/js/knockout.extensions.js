/**
 * CoCeSo
 * Client JS - Extensions for knockout.js
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */

/**
 * Generate the binding to a jQuery UI widget
 *
 * @param {String} widget The jQuery UI widget constructor
 * @return {BindingHandler}
 */
function uiBindingHandler(widget) {
  return {
    init: function(element) {
      ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
        var $element = $(element);
        if ($element.data("ui-" + widget)) {
          $element[widget]("destroy");
        }
      });
    },
    update: function(element, valueAccessor) {
      var $element = $(element), options = ko.utils.unwrapObservable(valueAccessor()) || {};
      if ($element.data("ui-" + widget)) {
        $element[widget]("destroy");
      }
      $element[widget](options);
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
ko.bindingHandlers.accordionRefresh = {
  update: function(element, valueAccessor) {
    ko.utils.unwrapObservable(valueAccessor());
    if ($(element).data("ui-accordion")) {
      $(element)["accordion"]("refresh");
    }
  }
};

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
 * Generate Popover from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.popover = {
  init: function(element, valueAccessor) {
    var $element = $(element);
    $element.popover(ko.utils.unwrapObservable(valueAccessor()) || {});
    ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
      $element.popover("destroy");
    });
  },
  update: function(element, valueAccessor) {
    var $element = $(element), data = $element.data("bs.popover");
    ko.utils.objectForEach(ko.utils.unwrapObservable(valueAccessor()) || {}, function(key, val) {
      data.options[key] = val;
    });
    if (data.tip().hasClass("in")) {
      $element.popover("show");
    }
  }
};

ko.bindingHandlers.visibleAndSelect = {
  update: function(element, valueAccessor) {
    ko.bindingHandlers.visible.update(element, valueAccessor);
    if (ko.utils.unwrapObservable(valueAccessor())) {
      $(element).focus().select();
    }
  }
};

ko.bindingHandlers.file = {
  init: function(element, valueAccessor) {
    var fileContents = valueAccessor(), reader = new FileReader();
    reader.onloadend = function() {
      fileContents(reader.result);
    };

    ko.utils.registerEventHandler(element, 'change', function() {
      fileContents(null);
      var file = element.files[0];
      if (file) {
        reader.readAsBinaryString(file);
      }
    });
  },
  update: function(element, valueAccessor) {
    if (!ko.utils.unwrapObservable(valueAccessor())) {
      try {
        element.value = null;
        if (element.value) {
          element.parentNode.replaceChild(element.cloneNode(true), element);
        }
      } catch (ex) {
      }
    }
  }
};

/**
 * Allow change detection on observable
 *
 * @param {ko.observable} target
 * @param {Object} options
 * @returns {ko.computed}
 */
ko.extenders.observeChanges = function(target, options) {
  target.server = options.server;
  target.orig = (typeof options.orig !== "undefined") ? ko.observable(options.orig) : ko.observable(ko.utils.unwrapObservable(target.server));

  target.changed = ko.computed(function() {
    return (typeof this.orig() !== "undefined" && this() !== this.orig());
  }, target);

  target.serverChange = ko.computed(function() {
    var server = ko.utils.unwrapObservable(this.server), orig = this.orig();
    if (typeof server !== "undefined" && server !== orig) {
      return server;
    }
    return null;
  }, target);

  target.valid = ko.computed(options.validate instanceof Function ? options.validate : function() {
    return true;
  }, target);

  target.formcss = ko.computed(function() {
    if (!this.valid()) {
      return "has-error";
    }
    if (this.changed()) {
      return "has-change";
    }
    return "";
  }, target);

  target.reset = function() {
    if (target.changed()) {
      target(target.orig());
    }
  };

  target.setServer = function(server) {
    target.server = server;
    target.orig.valueHasMutated();
  };

  target.tmp = ko.computed(function() {
    var server = ko.utils.unwrapObservable(this.server), orig = this.orig();
    if (typeof server !== "undefined" && server !== orig) {
      if (!options.keepChanges || !this.changed() || server === this()) {
        this.orig(server);
        this(server);
      }
    }
  }, target);

  return target;
};

/**
 * Allow change detection on array
 *
 * @param {ko.observableArray} target
 * @param {Object} options
 * @returns {ko.observableArray}
 */
ko.extenders.arrayChanges = function(target, options) {
  //Include those for matching interface with observeChanges
  target.orig = ko.observable(null);
  target.serverChange = ko.observable(null);

  target.changed = ko.computed(function() {
    var items = ko.utils.unwrapObservable(this);
    if (!items instanceof Array) {
      return false;
    }
    return !!ko.utils.arrayFirst(items, function(item) {
      return ko.utils.unwrapObservable(item.changed);
    });
  }, target);

  target.valid = ko.computed(function() {
    var items = ko.utils.unwrapObservable(this);
    if (!items instanceof Array) {
      return false;
    }

    return !ko.utils.arrayFirst(items, function(item) {
      return typeof item.valid === "undefined" ? false : !ko.utils.unwrapObservable(item.valid);
    });
  }, target);

  target.enable = ko.computed(function() {
    return (this.changed() && this.valid());
  }, target);

  target.reset = function() {
    var items = ko.utils.unwrapObservable(target);
    if (!items instanceof Array) {
      return;
    }
    ko.utils.arrayForEach(items, function(item) {
      if (item.reset instanceof Function) {
        item.reset();
      }
    });
  };

  return target;
};

/**
 * Get a filtered and sorted selection of an array
 *
 * @param {ko.observableArray} target
 * @param {Object} options
 * @returns {ko.computed}
 * @see applyFilter
 */
ko.extenders.list = function(target, options) {
  /**
   * Compare values
   *
   * @param {String} op Comparison operator ("equal"/"not" - defaults to "equal")
   * @param {mixed|Array|RegExp} a Value, Array of values or RexExp
   * @param {mixed} b Value to compare to
   * @returns {Boolean}
   */
  var compare = function(op, a, b) {
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
  };

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
  var applyFilter = function(filterObj, val) {
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
        if ((typeof filter.op !== "undefined") && (typeof filter.val !== "undefined")) {
          op = filter.op;
          filter = filter.val;
        } else if (typeof filter.val !== "undefined") {
          filter = filter.val;
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
  };

  var field = null, asc = null;
  if (options.sort === true) {
    field = ko.observable(options.field || null);
    asc = ko.observable(options.asc || true).extend({boolean: true});
    options.sort = function(a, b) {
      var f = field();
      if (f && typeof a[f] !== "undefined" && typeof b[f] !== "undefined") {
        a = ko.utils.unwrapObservable(a[f]);
        b = ko.utils.unwrapObservable(b[f]);
        if (typeof a === "string") {
          a = a.toLowerCase();
        }
        if (typeof b === "string") {
          b = b.toLowerCase();
        }
        if (a === null || a < b) {
          return asc() ? -1 : 1;
        }
        if (b === null || a > b) {
          return asc() ? 1 : -1;
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

/**
 * Helper for comparing (isValue methods)
 *
 * @param {ko.observable} target
 * @param {mixed} value
 * @returns {ko.computed}
 */
ko.extenders.isValue = function(target, value) {
  var ret = ko.computed(function() {
    return (target() === ko.utils.unwrapObservable(value));
  });

  ret.state = ko.computed(function() {
    return this() ? "active" : "";
  }, ret);

  ret.set = function() {
    target(ko.utils.unwrapObservable(value));
  };

  return ret;
};

/**
 * Helper for boolean values
 *
 * @param {ko.observable} target
 * @returns {ko.computed}
 */
ko.extenders.boolean = function(target) {
  var ret = ko.computed({
    read: target,
    write: function(val) {
      target(!!val);
    }
  });

  ret.state = ko.computed(function() {
    return this() ? "active" : "";
  }, ret);

  ret.toggle = function() {
    target(!target());
  };

  ret.set = function() {
    target(true);
  };

  ret.unset = function() {
    target(false);
  };

  return ret;
};

/**
 * Force value to be an integer
 *
 * @param {ko.observable} target
 * @param {Integer} length
 * @returns {ko.computed}
 */
ko.extenders.integer = function(target, length) {
  var ret = ko.computed({
    read: target,
    write: function(newValue) {
      var current = target(),
          newValueInt = (newValue && !isNaN(newValue)) ? parseInt(newValue) : 0;

      if (newValue === null || newValue === "") {
        newValueInt = null;
      } else if (length) {
        newValueInt = newValueInt.toString();
        while (newValueInt.length < length) {
          newValueInt = "0" + newValueInt;
        }
      }

      if (newValueInt !== current) {
        target(newValueInt);
      } else if (newValue !== current) {
        target.notifySubscribers(newValueInt);
      }
    }
  }).extend({notify: 'always'});

  ret.valueHasMutated = target.valueHasMutated;
  ret(target());

  return ret;
};
