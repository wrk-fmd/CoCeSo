/**
 * CoCeSo
 * Client JS - edit/models/editableuser
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
 * @module {Class} edit/models/editableuser
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/user} User
 * @param {module:data/save} ajaxSave
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["jquery", "knockout", "./user", "data/save", "utils/conf", "utils/errorhandling",
  "ko/extenders/changes", "ko/extenders/form"],
    function($, ko, User, ajaxSave, conf, initErrorHandling) {
      "use strict";

      /**
       * Editable User model
       *
       * @alias module:edit/models/editableuser
       * @constructor
       */
      var EditableUser = function() {
        var self = this;

        /**
         * Observable for the ID (needed to update form after initial saving)
         *
         * @function
         * @type ko.observable
         * @returns {Integer}
         */
        this.idObs = ko.observable(null);

        /**
         * Used Model (reference)
         *
         * @function
         * @type ko.observable
         * @returns {module:edit/models/user}
         */
        this.model = ko.observable(null);

        //Call parent constructors
        User.call(this, {id: this.idObs()});

        //Initialize change detection
        this.firstname.extend({observeChanges: {}});
        this.lastname.extend({observeChanges: {}});
        this.personnelId = this.personnelId.extend({integer: 0, observeChanges: {}});
        this.contact.extend({observeChanges: {}});
        this.info.extend({observeChanges: {}});
        this.username.extend({observeChanges: {}});
        this.allowlogin = this.allowlogin.extend({"boolean": true, observeChanges: {}});
        this.password = ko.observable("").extend({observeChanges: {server: ""}});
        this.password2 = ko.observable("").extend({
          observeChanges: {
            server: "",
            validate: function() {
              return (this() === self.password());
            }
          }
        });

        // Authorities
        this.authorities.orig = ko.observableArray([]);
        this.authorities.changed = ko.computed(function() {
          var a = this(), b = this.orig();
          return (a.length !== b.length || $(a).not(b).length !== 0 || $(b).not(a).length !== 0);
        }, this.authorities);
        this.authorities.reset = function() {
          self.authorities(self.authorities.orig());
        };

        this.form = ko.observableArray([
          this.firstname, this.lastname, this.personnelId, this.contact, this.info, this.username, this.allowlogin, this.authorities, this.password, this.password2
        ]).extend({form: true});

        // Error Handling
        initErrorHandling(this);

        this.load = function(id) {
          if (id) {
            $.ajax({
              type: "GET",
              url: conf.get("jsonBase") + "user/get/" + id,
              dataType: "json",
              success: function(data) {
                self.idObs(data.id);
                self.firstname.server(data.firstname);
                self.lastname.server(data.lastname);
                self.personnelId.server(data.personnelId);
                self.contact.server(data.contact);
                self.info.server(data.info);
                self.username.server(data.username);
                self.allowlogin.server(data.allowLogin);
                self.authorities(data.internalAuthorities);
                self.authorities.orig(data.internalAuthorities);
              }
            });
          } else {
            self.idObs(null);
            self.firstname.server("");
            self.lastname.server("");
            self.personnelId.server(null);
            self.contact.server("");
            self.info.server("");
            self.username.server("");
            self.allowlogin.server(false);
            self.authorities([]);
            self.authorities.orig([]);
          }
        };

        this.fullname = ko.computed(function() {
          return this.firstname.orig() + " " + this.lastname.orig();
        }, this);

        this.save = function() {
          ajaxSave(JSON.stringify({
            id: this.idObs(),
            firstname: this.firstname(),
            lastname: this.lastname(),
            personnelId: this.personnelId(),
            contact: this.contact(),
            info: this.info(),
            username: this.username(),
            allowLogin: this.allowlogin(),
            internalAuthorities: this.authorities()
          }), "user/update", function(response) {
            self.error(false);
            if (response.id) {
              self.idObs(response.id);
            }
            self.load(self.idObs());
            conf.get("rootModel").reload();

            if (self.password.changed() && self.idObs()) {
              ajaxSave(JSON.stringify({
                id: self.idObs(),
                username: self.username(),
                password: self.password()
              }), "user/setPassword", function() {
                self.password("");
                self.password2("");
              }, self.saveError, self.httpError, self.form.saving);
            }
          }, this.saveError, this.httpError, this.form.saving);
        };
      };

      return EditableUser;
    });
