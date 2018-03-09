/**
 * CoCeSo
 * Client JS - edit/viewmodels/user
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
 * @module {Class} edit/viewmodels/user
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/user} User
 * @param {module:edit/models/editableuser} EditableUser
 * @param {module:data/paginate} paginate
 * @param {module:data/save} ajaxSave
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["jquery", "knockout", "../models/user", "../models/editableuser",
  "data/paginate", "data/save", "utils/conf", "utils/errorhandling",
  "bootstrap/modal", "ko/bindings/file", "ko/extenders/paginate"],
  function($, ko, User, EditableUser, paginate, ajaxSave, conf, initErrorHandling) {
    "use strict";

    /**
     * Viewmodel for the user list
     *
     * @alias module:edit/viewmodels/user
     * @constructor
     */
    var EditUser = function() {
      conf.set("rootModel", this);
      var self = this;

      // Error Handling
      initErrorHandling(this);

      this.reload = function(target) {
        paginate("user/getFiltered.json", target || self.users, User);
      };

      this.users = ko.observableArray([]).extend({
        paginate: {
          field: "personnelId",
          callback: this.reload
        }
      });

      // User to edit
      this.edit = new EditableUser();

      // Show the edit form
      this.showForm = function(user) {
        this.edit.load(user && user.id);
        this.edit.form.reset();
        $("#edit_user").modal("show");
      };

      // Show an empty edit form
      this.create = function() {
        this.showForm();
      };

      this.csv = ko.observable(null);

      this.upload = function() {
        ajaxSave(this.csv(), "user/upload.json", function() {
          self.csv(null);
          self.error(false);
          self.reload();
        }, self.saveError, self.httpError, null, "text/csv");
      };
    };

    return EditUser;
  }
);
