/**
 * CoCeSo
 * Client JS - ko/bindings/patient
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
 * @module ko/bindings/patient
 * @param {module:knockout} ko
 * @param {module:ko/bindings/typeahead} typeahead
 * @param {module:utils/conf} conf
 */
define(["knockout", "./typeahead", "utils/conf"], function(ko, typeahead, conf) {
  "use strict";

  ko.bindingHandlers.patient = {
    init: function(element, valueAccessor) {
      var options = valueAccessor();
      typeahead(element, conf.get("jsonBase") + 'patadmin/registration/patients.json?f=' + options.key + '&q=%QUERY', options.key,
        function(obj) {
          return "<p>" + obj.lastname + " " + obj.firstname + (obj.externalId ? " (" + obj.externalId + ")" : "") + "</p>";
        }, options.callback);
    }
  };
});
