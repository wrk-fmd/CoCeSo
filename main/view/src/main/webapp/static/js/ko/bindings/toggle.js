/**
 * CoCeSo
 * Client JS - ko/bindings/toggle
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
 * @module ko/bindings/toggle
 * @param {module:jquery} $
 * @param {module:knockout} ko
 */
define(["jquery", "knockout"], function($, ko) {
  "use strict";

  ko.bindingHandlers.toggle = {
    init: function(element, valueAccessor) {
      var options = valueAccessor(), id = ko.utils.unwrapObservable(options.id), $el = $(element);

      if (options.isHidden(id)) {
        $el.find(".toggle-body").hide();
        $el.find(".toggle-indicator").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
        options.hide(id, true);
      }

      $el.on("click", ".toggle-handle", function() {
        $el.find(".toggle-body").slideToggle();
        $el.find(".toggle-indicator").toggleClass("glyphicon-chevron-down glyphicon-chevron-up");
        options.hide(id, $el.find(".toggle-indicator").hasClass("glyphicon-chevron-down"));
      });
    }
  };
});
