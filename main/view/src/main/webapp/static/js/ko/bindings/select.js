/**
 * CoCeSo
 * Client JS - ko/extenders/paginate
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout"], function($, ko) {
  "use strict";

  ko.bindingHandlers.visibleAndSelect = {
    update: function(element, valueAccessor) {
      ko.bindingHandlers.visible.update(element, valueAccessor);
      if (ko.utils.unwrapObservable(valueAccessor())) {
        $(element).focus().select();
      }
    }
  };
});
