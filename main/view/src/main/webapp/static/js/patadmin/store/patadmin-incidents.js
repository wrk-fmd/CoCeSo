/**
 * CoCeSo
 * Client JS - patadmin/store/patadmin-incidents
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2026 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "data/load"], function(ko, load) {
  "use strict";

  var Incident = function(data) {
    var self = this;
    data = data || {};

    this.id = data.id;

    this.type = ko.observable(data.type || "");
    this.state = ko.observable(data.state || "");
    this.ao = ko.observable(data.ao || null);
    this.bo = ko.observable(data.bo || null);
    this.units = ko.observable(data.units || {});
    this.patient = ko.observable(data.patient || null);
    this.casusNr = ko.observable(data.casusNr || "");
    this.info = ko.observable(data.info || "");

    this.setData = function(d) {
      self.type(d.type || "");
      self.state(d.state || "");
      self.ao(d.ao || null);
      self.bo(d.bo || null);
      self.units(d.units || {});
      self.patient(d.patient || null);
      self.casusNr(d.casusNr || "");
      self.info(d.info || "");
    };

    this.setData(data);

    this.destroy = function() {};
  };

  var store = {
    models: ko.observable({}),
    get: function(id) {
      return store.models()[id] || null;
    }
  };

  load({
    url: "patadmin/incidents",
    stomp: "/topic/incident/main/{c}",
    model: Incident,
    store: store.models
  });

  return store;
});
