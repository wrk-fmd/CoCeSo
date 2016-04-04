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

define(["knockout"], function(ko) {
  "use strict";

  ko.bindingHandlers.file = {
    init: function(element, valueAccessor) {
      var fileContents = valueAccessor(), reader = new FileReader();
      reader.onloadend = function() {
        fileContents(reader.result);
      };

      ko.utils.registerEventHandler(element, 'change', function() {
        fileContents(null);
        var file = element.files[0];
        if (file) {
          reader.readAsText(file);
        }
      });
    },
    update: function(element, valueAccessor) {
      if (!ko.utils.unwrapObservable(valueAccessor())) {
        try {
          element.value = null;
          if (element.value) {
            element.parentNode.replaceChild(element.cloneNode(true), element);
          }
        } catch (ex) {
        }
      }
    }
  };
});
