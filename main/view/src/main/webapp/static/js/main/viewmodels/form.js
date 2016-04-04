/**
 * CoCeSo
 * Client JS - main/viewmodels/form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "utils/errorhandling", "ko/extenders/form"],
    function(ko, initErrorHandling) {
      "use strict";

      /**
       * Base class for all Form ViewModels
       *
       * @constructor
       */
      var Form = function() {
        /**
         * Watch dependencies
         *
         * @type ko.observableArray
         */
        this.form = ko.observableArray().extend({form: {}});

        initErrorHandling(this);

        /**
         * Save modified data and close the window
         *
         * @function
         * @returns {boolean}
         */
        this.ok = function() {
          this.save();
          $("#" + this.ui).dialog("destroy");
        };
      };

      return Form;
    });
