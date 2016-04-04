/**
 * CoCeSo
 * Client JS - map/legend
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "./leaflet", "./point", "utils/i18n"], function(ko, L, Point, _) {
  "use strict";

  /**
   * Show legend
   * in part taken from L.Control.Layers
   *
   * @constructor
   * @extends L.Control
   */
  var Legend = L.Control.extend({
    options: {
      collapsed: true,
      position: "bottomright"
    },
    onAdd: function() {
      var className = 'map-legend',
          container = this._container = L.DomUtil.create('div', className);

      //Makes this work on IE10 Touch devices by stopping it from firing a mouseout event when the touch is released
      container.setAttribute('aria-haspopup', true);

      if (!L.Browser.touch) {
        L.DomEvent
            .disableClickPropagation(container)
            .disableScrollPropagation(container);
      } else {
        L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);
      }
      if (this.options.collapsed) {
        if (!L.Browser.android) {
          L.DomEvent
              .on(container, 'mouseover', this._expand, this)
              .on(container, 'mouseout', this._collapse, this);
        }
        var link = L.DomUtil.create('a', className + '-toggle', container);
        link.href = '#';
        link.title = _("key");
        L.DomUtil.create("span", "glyphicon glyphicon-question-sign", link);

        if (L.Browser.touch) {
          L.DomEvent
              .on(link, 'click', L.DomEvent.stop)
              .on(link, 'click', this._expand, this);
        } else {
          L.DomEvent.on(link, 'focus', this._expand, this);
        }

        this._map.on('click', this._collapse, this);
      } else {
        this._expand();
      }

      var list = L.DomUtil.create('div', className + '-list', container),
          content = "<ul class='list-unstyled'>";
      ko.utils.arrayForEach(Point.prototype.types, function(type) {
        content += "<li class='clearfix'>"
            + "<div class='leaflet-marker-icon leaflet-div-icon icon-" + type + "'></div>"
            + "<div>" + _("map.legend." + type) + "</div>"
            + "</li>";
      });
      content += "<li class='clearfix'>"
          + "<div class='leaflet-marker-icon leaflet-div-icon'>"
          + "<span class='glyphicon glyphicon-plus'></span></div>"
          + "<div>" + _("map.legend.multiple") + "</div>"
          + "</li>";
      content += "</ul>";
      list.innerHTML = content;

      return container;
    },
    _expand: function() {
      L.DomUtil.addClass(this._container, 'map-legend-expanded');
    },
    _collapse: function() {
      this._container.className = this._container.className.replace(' map-legend-expanded', '');
    }
  });

  return Legend;
});
