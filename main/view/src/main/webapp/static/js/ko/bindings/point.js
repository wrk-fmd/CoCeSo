/**
 * CoCeSo
 * Client JS - ko/bindings/point
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module ko/bindings/point
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:bloodhound} Bloodhound
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "bloodhound", "utils/conf", "typeahead.js"], function($, ko, Bloodhound, conf) {
  "use strict";

  var bloodhound = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
      url: conf.get("jsonBase") + 'poiAutocomplete.json?q=%QUERY',
      wildcard: "%QUERY"
    }
  });

  ko.bindingHandlers.point = {
    init: function(element) {
      var $element = $(element);
      $element.typeahead({
        minLength: 2,
        highlight: true
      }, {
        displayKey: function(str) {
          return str;
        },
        templates: {
          suggestion: function(str) {
            return "<p>" + str.replace(/\n/g, ", ") + "</p>";
          }
        },
        limit: 20,
        source: bloodhound
      });

      $element.on("typeahead:selected typeahead:cursorchanged", function(event, data) {
        if (data) {
          var index = data.indexOf("\n");
          if (index < 0) {
            index = data.length;
          }
          element.setSelectionRange(index, index);
        }
      });
      $element.on("typeahead:selected", function() {
        $element.trigger("input");
      });
    }
  };
});
