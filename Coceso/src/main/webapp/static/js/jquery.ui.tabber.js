/**
 * CoCeSo
 * jQuery UI Tabber Extension
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
 *	jquery.js
 *	jquery.ui.tabs.js
 */

$.widget("ui.tabber", $.ui.tabs, {
  delay: 50,
  _panelKeydown: function(event) {
    return;
  },
  _createPanel: function(id) {
    return $();
  },
  _setupHeightStyle: function(heightStyle) {
    return;
  },
  _eventHandler: function(event) {
    var options = this.options,
      active = this.active,
      anchor = $(event.currentTarget),
      tab = anchor.closest("li"),
      clickedIsActive = tab[ 0 ] === active[ 0 ],
      collapsing = clickedIsActive && options.collapsible,
      eventData = {
      oldTab: active,
      oldPanel: $(),
      newTab: collapsing ? $() : tab,
      newPanel: $()
    };

    event.preventDefault();

    if (tab.hasClass("ui-state-disabled") ||
      // tab is already loading
      tab.hasClass("ui-tabs-loading") ||
      // can't switch durning an animation
      this.running ||
      // click on active header, but not collapsible
      (clickedIsActive && !options.collapsible) ||
      // allow canceling activation
      (this._trigger("beforeActivate", event, eventData) === false)) {
      return;
    }

    options.active = collapsing ? false : this.tabs.index(tab);
    this.active = clickedIsActive ? $() : tab;
    this._toggle(event, eventData);
  },
  // handles show/hide for selecting tabs
  _toggle: function(event, eventData) {
    // If we're collapsing, then keep the collapsing tab in the tab order.
    if (eventData.newTab.length) {
      this.tabs.filter(function() {
        return $(this).attr("tabIndex") == 0;
      }).attr("tabIndex", -1);
    }

    this._super(event, eventData);
  },
  load: function(index, event) {
    return;
  },
  _ajaxSettings: function(anchor, event, eventData) {
    return;
  },
  _getPanelForTab: function(tab) {
    return $();
  }
});
