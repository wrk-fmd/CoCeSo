/**
 * CoCeSo
 * Client JS - ko/bindings/popover
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
 * @module ko/bindings/popover
 * @param {module:jquery} $
 * @param {module:knockout} ko
 */
define(["jquery", "knockout", "bootstrap/popover"], function($, ko) {
  "use strict";

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
});
