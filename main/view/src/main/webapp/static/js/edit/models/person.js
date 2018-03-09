/**
 * CoCeSo
 * Client JS - edit/models/person
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
 * @module {Class} edit/models/person
 */
define(function() {
  "use strict";

  /**
   * Person model for adding crew
   *
   * @alias module:edit/models/person
   * @constructor
   * @param {Object} data
   */
  var Person = function(data) {
    this.id = data.id;
    this.fullname = data.lastname + " " + data.firstname;
    this.personnelId = data.personnelId;
  };

  return Person;
});
