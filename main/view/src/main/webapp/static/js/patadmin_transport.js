/**
 * CoCeSo
 * Client JS - patadmin_transport
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

require(["config"], function() {
  require(["jquery", "knockout", "ko/bindings/ertype", "bootstrap/collapse", "bootstrap/dropdown"], function($, ko) {
    "use strict";
    ko.applyBindings({}, $("#ertype")[0]);
  });
});
