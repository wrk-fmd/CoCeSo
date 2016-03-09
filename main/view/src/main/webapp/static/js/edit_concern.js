/**
 * CoCeSo
 * Client JS - edit_concern
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

require(["config"], function() {
  require(["jquery", "knockout", "edit/viewmodels/units", "edit/viewmodels/container",
    "edit/viewmodels/batch", "edit/models/editableconcern", "utils/lock",
    "bootstrap/dropdown", "bootstrap/tab", "bootstrap/tooltip", "utils/misc"],
    function($, ko, Units, Container, Batch, Concern, lock) {
      "use strict";

      lock.lock();

      var viewmodel = {
        units: new Units(),
        container: new Container(),
        batch: new Batch(),
        concern: new Concern()
      };

      ko.applyBindings(viewmodel);
      $(".tooltipped").tooltip();
    }
  );
});
