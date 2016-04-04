/**
 * CoCeSo
 * Client JS - ko/bindings/patient
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
 * @module ko/bindings/patient
 * @param {module:jquery} $
 * @param {module:bloodhound} Bloodhound
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 * @param {module:utils/i18n} _
 */
define(["jquery", "bloodhound", "knockout", "utils/conf", "utils/i18n", "typeahead.js"],
  function($, Bloodhound, ko, conf, _) {
    "use strict";

    function buildDisplayName(obj) {
      return "<p>" + obj.lastname + " " + obj.firstname + (obj.externalId ? " (" + obj.externalId + ")" : "") + "</p>";
    }

    function getSource(type, key) {
      var bloodhound = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.whitespace,
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
          url: conf.get("jsonBase") + 'patadmin/triage/' + type + '.json?f=' + key + '&q=%QUERY',
          wildcard: "%QUERY"
        }
      });

      return {
        displayKey: key,
        templates: {
          suggestion: buildDisplayName,
          header: "<h4 class='tt-suggestion'>" + _(type) + "</h4>"
        },
        limit: 20,
        source: bloodhound
      };
    }

    ko.bindingHandlers.patient = {
      init: function(element, valueAccessor) {
        var options = valueAccessor();
        if (options.types.length > 0) {
          var $element = $(element),
            args = $.map(options.types, function(type) {
              return getSource(type, options.key);
            });
          args.unshift({minLength: 2, highlight: true});
          $element.typeahead.apply($element, args);
          $element.on("typeahead:selected", options.callback);
        }
      }
    };
  }
);
