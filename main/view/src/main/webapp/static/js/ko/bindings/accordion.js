/**
 * CoCeSo
 * Client JS - ko/bindings/accordion
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
 * @module ko/bindings/accordion
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:ko/bindings/uibinding} uiBindingHandler
 */
define(["jquery", "knockout", "./uibinding", "jquery-ui/widgets/accordion"], function($, ko, uiBindingHandler) {
  "use strict";

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
});
