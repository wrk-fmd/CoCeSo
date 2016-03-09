/**
 * CoCeSo
 * Client JS - map/popup
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "./leaflet"], function(ko, L) {
  "use strict";

  /**
   * Modified Leaflet popup to work with bootstrap and observables
   *
   * @constructor
   * @extends L.Popup
   * @param {String|ko.observable} title
   * @param {String|ko.observable} content
   * @param {PopupOptions} options
   * @param {ILayer} source
   */
  var Popup = L.Popup.extend({
    initialize: function(title, content, options, source) {
      L.Popup.prototype.initialize.call(this, options, source);
      if (ko.isObservable(title)) {
        this.setTitle(title());
        title.subscribe(this.setTitle, this);
      } else {
        this.setTitle(title);
      }
      if (ko.isObservable(content)) {
        this.setContent(content());
        content.subscribe(this.setContent, this);
      } else {
        this.setContent(content);
      }
    },
    setTitle: function(title) {
      this._title = title;
      this.update();
      return this;
    },
    _initLayout: function() {
      var containerClass = "leaflet-popover popover top " + this.options.className + " leaflet-zoom-" + (this._animated ? "animated" : "hide"),
          container = this._container = L.DomUtil.create("div", containerClass),
          closeButton;

      if (this.options.closeButton) {
        closeButton = this._closeButton = L.DomUtil.create('a', 'leaflet-popup-close-button', container);
        closeButton.href = '#close';
        closeButton.innerHTML = '&#215;';
        L.DomEvent.disableClickPropagation(closeButton);
        L.DomEvent.on(closeButton, 'click', this._onCloseButtonClick, this);
      }

      L.DomEvent.disableClickPropagation(container);
      this._tipContainer = L.DomUtil.create("div", "arrow", container);
      this._titleNode = L.DomUtil.create("h3", "popover-title", container);
      this._contentNode = L.DomUtil.create("div", "popover-content", container);

      L.DomEvent.disableScrollPropagation(this._contentNode);
      L.DomEvent.on(container, "contextmenu", L.DomEvent.stopPropagation);
    },
    _updateContent: function() {
      if (this._title) {
        this._titleNode.innerHTML = this._title;
      }
      L.Popup.prototype._updateContent.call(this);
    },
    _updatePosition: function() {
      if (!this._map) {
        return;
      }
      L.Popup.prototype._updatePosition.call(this);
      this._containerBottom = this._containerBottom + 20;
      this._container.style.bottom = this._containerBottom + "px";
    }
  });

  return Popup;
});
