/**
 * CoCeSo
 * Client JS - ko/bindings/point
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
 * @module ko/bindings/point
 * @param {module:knockout} ko
 * @param {module:ko/bindings/typeahead} typeahead
 * @param {module:utils/conf} conf
 */
define(["knockout", "./typeahead", "utils/conf"], function(ko, typeahead, conf) {
  "use strict";

  ko.bindingHandlers.point = {
    init: function(element) {
      typeahead(element, conf.get("jsonBase") + "poiAutocomplete.json?q=%QUERY",
        function(str) {
          return str;
        },
        function(str) {
          return "<p>" + str.replace(/\n/g, ", ") + "</p>";
        },
        function(element, data) {
          if (data) {
            var index = data.indexOf("\n");
            if (index < 0) {
              index = data.length;
            }
            element.setSelectionRange(index, index);
          }
        }
      );
    }
  };
});
