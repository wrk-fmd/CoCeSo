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
 *	knockout-2.3.0.js
 *	jquery.js
 *	jquery.ui.js
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
 * Generate Accordion from loop
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.accordion = uiBindingHandler("accordion");

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
 * Generate Tabs from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.tabber = uiBindingHandler("tabber");

/**
 * Generate Tabs from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.buttonset = uiBindingHandler("buttonset");

/**
 * Generate Tabs from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.button = uiBindingHandler("button");

