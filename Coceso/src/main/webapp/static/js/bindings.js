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
 * Generate Accordion from loop
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.accordion = {
  init: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).accordion(options);
    }, 0);

    ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
      $(element).accordion("destroy");
    });
  },
  update: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).accordion("destroy").accordion(options);
    }, 0);
  }
};

/**
 * Generate Draggable from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.draggable = {
  init: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).draggable(options);
    }, 0);

    ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
      $(element).draggable("destroy");
    });
  },
  update: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).draggable("destroy").draggable(options);
    }, 0);
  }
};

/**
 * Generate Droppable from element
 *
 * @type {BindingHandler}
 */
ko.bindingHandlers.droppable = {
  init: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).droppable(options);
    }, 0);

    ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
      $(element).droppable("destroy");
    });
  },
  update: function(element, valueAccessor) {
    var options = valueAccessor() || {};
    setTimeout(function() {
      $(element).droppable("destroy").droppable(options);
    }, 0);
  }
};
