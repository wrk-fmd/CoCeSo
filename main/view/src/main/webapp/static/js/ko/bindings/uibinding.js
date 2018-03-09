/**
 * CoCeSo
 * Client JS - ko/bindings/uibinding
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
 * @module ko/bindings/uibinding
 * @param {module:jquery} $
 * @param {module:knockout} ko
 */
define(["jquery", "knockout"], function($, ko) {
  "use strict";

  /**
   * Generate the binding to a jQuery UI widget
   *
   * @alias module:ko/bindings/uibinding
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

  return uiBindingHandler;
});
