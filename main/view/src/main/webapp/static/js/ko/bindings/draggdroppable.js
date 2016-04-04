/**
 * CoCeSo
 * Client JS - ko/bindings/draggdroppable
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
 * @module ko/bindings/draggdroppable
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:ko/bindings/uibinding} uiBindingHandler
 */
define(["jquery", "knockout", "./uibinding", "jquery-ui/draggable", "jquery-ui/droppable"],
  function($, ko, uiBindingHandler) {
    "use strict";

    //Fix issue with droppables in background
    var _intersect = $.ui.intersect;
    $.ui.intersect = function(draggable, droppable, toleranceMode, event) {
      if (toleranceMode === "pointer" && !$.contains(droppable.element[0], document.elementFromPoint(event.pageX, event.pageY))) {
        return false;
      }
      return _intersect(draggable, droppable, toleranceMode, event);
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
  }
);
