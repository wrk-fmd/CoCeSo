/**
 * CoCeSo
 * Client JS - patadmin/registration/patient
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2026 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout"], function(ko) {
  "use strict";

  var Patient = function(data) {
    var self = this;
    data = data || {};

    this.id = data.id;
    this.externalId = data.externalId || "";

    this.firstname = ko.observable(data.firstname || "");
    this.lastname = ko.observable(data.lastname || "");
    this.group = ko.observable(data.group || null);

    this.setData = function(d) {
      self.firstname(d.firstname || "");
      self.lastname(d.lastname || "");
      self.group(d.group || null);
    };

    this.setData(data);

    this.destroy = function() {};
  };

  return Patient;
});
