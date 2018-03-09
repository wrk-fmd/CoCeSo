/**
 * CoCeSo
 * Client JS - edit_user/user
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
 * @module {Class} edit_user/user
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["knockout", "utils/conf"], function(ko, conf) {
  "use strict";

  /**
   * User model
   *
   * @alias module:edit_user/user
   * @constructor
   * @param {Object} data
   */
  var User = function(data) {
    var self = this;
    data = data || {};

    this.id = data.id;
    this.firstname = ko.observable("");
    this.lastname = ko.observable("");
    this.personnelId = ko.observable(null);
    this.contact = ko.observable("");
    this.info = ko.observable("");
    this.username = ko.observable("");
    this.allowlogin = ko.observable(false);

    // Authorities
    this.authorities = ko.observableArray([]);

    this.fullname = ko.computed(function() {
      return this.firstname() + " " + this.lastname();
    }, this);

    this.setData = function(data) {
      self.firstname(data.firstname || "");
      self.lastname(data.lastname || "");
      self.personnelId(data.personnelId || null);
      self.contact(data.contact || "");
      self.info(data.info || "");
      self.username(data.username || "");
      self.allowlogin(data.allowLogin || false);
      self.authorities(data.internalAuthorities || []);
    };

    this.setData(data);

    this.edit = function() {
      conf.get("rootModel").showForm(this);
    };
  };

  return User;
});
