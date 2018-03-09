/**
 * CoCeSo
 * Client JS - patadmin/registration/form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module patadmin/registration/form
 * @param {module:knockout} ko
 */
define(["knockout", "bootstrap/modal", "ko/bindings/patient"], function(ko) {
  "use strict";

  /**
   * @alias module:patadmin/registration/form
   * @constructor
   */
  var Form = function() {
    var self = this;

    this.patient = ko.observable();
    this.lastname = ko.observable();
    this.firstname = ko.observable();
    this.externalId = ko.observable();
    this.birthday = ko.observable();
    this.group = ko.observable();
    this.naca = ko.observable();
    this.diagnosis = ko.observable();
    this.info = ko.observable();

    this.callback = function(element, data) {
      if (data.lastname) {
        self.lastname(data.lastname);
      }
      if (data.firstname) {
        self.firstname(data.firstname);
      }
      if (data.externalId) {
        self.externalId(data.externalId);
      }
      if (data.birthday) {
        self.birthday(data.birthday);
      }
      if (data.id) {
        self.patient(data.id);
      }
      if (data.group && data.group.length > 0) {
        self.group(data.group[0].id);
      }
      if (data.naca) {
        self.naca(data.naca);
      }
      if (data.diagnosis) {
        self.diagnosis(data.diagnosis);
      }
      if (data.info) {
        self.info(data.info);
      }
    };
  };

  return Form;
});
