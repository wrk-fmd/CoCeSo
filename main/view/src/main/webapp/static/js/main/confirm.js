/**
 * CoCeSo
 * Client JS - main/navigation
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout", "bootstrap/modal"], function($, ko) {
  "use strict";

  var confirm = {
    data: ko.observable({title: "", info_text: "", button_text: "", elements: [], save: null}),
    show: function(data) {
      confirm.data(data);
      $("#next-state-confirm").modal({backdrop: true, keyboard: true, show: true});
    }
  };

  return confirm;
});