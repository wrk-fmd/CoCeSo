/**
 * CoCeSo
 * Client JS - main/viewmodels/incidents
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["./leaflet", "./popup", "utils/i18n", "./wfs"], function(L, Popup, _) {
  "use strict";

  // Define Layers
  var base = {}, overlay = {}, names = {
    basemap: _("map.basemap"),
    ortho: _("map.ortho"),
    hospitals: _("map.hospitals"),
    oneway: _("map.oneway"),
    defi: _("map.defi")
  };

  base[names.basemap] = L.tileLayer("https://{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpeg", {
    maxZoom: 19,
    subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
    bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
    attribution: _("map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
        "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
  });
  base[names.ortho] = L.layerGroup([
    L.tileLayer("https://{s}.wien.gv.at/basemap/bmaporthofoto30cm/normal/google3857/{z}/{y}/{x}.jpeg", {
      maxZoom: 19,
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      attribution: _("map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
          "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
    }),
    L.tileLayer("https://{s}.wien.gv.at/basemap/bmapoverlay/normal/google3857/{z}/{y}/{x}.png", {
      maxZoom: 19,
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]]
    })
  ]);

  overlay[names.hospitals] = new L.GeoJSON.WFS("https://data.wien.gv.at/daten/geo", "ogdwien:KRANKENHAUSOGD", {
    pointToLayer: function(feature, latlng) {
      return L.marker(latlng, {
        icon: L.icon({
          iconUrl: 'https://data.wien.gv.at/katalog/images/krankenhaus.png',
          iconSize: [16, 16]
        })
      });
    },
    onEachFeature: function(feature, layer) {
      if (feature.properties) {
        layer.bindPopup(new Popup(feature.properties.BEZEICHNUNG, feature.properties.ADRESSE));
      }
    }
  });

  overlay[names.defi] = new L.GeoJSON.WFS("https://data.wien.gv.at/daten/geo", "ogdwien:DEFIBRILLATOROGD", {
    pointToLayer: function(feature, latlng) {
      return L.marker(latlng, {
        icon: L.icon({
          iconUrl: 'https://data.wien.gv.at/katalog/images/defibrillator.png',
          iconSize: [16, 16]
        })
      });
    },
    onEachFeature: function(feature, layer) {
      if (feature.properties) {
        layer.bindPopup(new Popup(feature.properties.ADRESSE, feature.properties.INFO));
      }
    }
  });

  return {
    base: base,
    overlay: overlay,
    names: names
  };
});
