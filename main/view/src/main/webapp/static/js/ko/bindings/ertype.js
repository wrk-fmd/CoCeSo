/**
 * CoCeSo
 * Client JS - ko/bindings/ertype
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
 * @module ko/bindings/ertype
 * @param {module:jquery} $
 * @param {module:bloodhound} Bloodhound
 * @param {module:knockout} ko
 */
define(["jquery", "bloodhound", "knockout", "typeahead.js"],
  function($, Bloodhound, ko) {
    "use strict";

    var ertypes = ["Intern", "Unfall", "Chirurgie", "Intern Kind", "Unfall Kind", "Chirurgie Kind",
      "PTCA", "Überwachung", "Intensiv", "Stroke", "Schockraum", "Arbeitsunfall"];
    var bloodhound = new Bloodhound({
      datumTokenizer: Bloodhound.tokenizers.whitespace,
      queryTokenizer: Bloodhound.tokenizers.whitespace,
      local: ertypes
    });

    $("#ertype").typeahead({
      highlight: true,
      minLength: 0
    }, {
      limit: 20,
      source: function(q, sync) {
        if (q === "") {
          sync(bloodhound.get.apply(bloodhound, ertypes));
        } else {
          bloodhound.search(q, sync);
        }
      }
    });

    ko.bindingHandlers.ertype = {
      init: function(element) {
        var $element = $(element);
        $element.typeahead({
          highlight: true,
          minLength: 0
        }, {
          limit: 20,
          source: function(q, sync) {
            if (q === "") {
              sync(bloodhound.get.apply(bloodhound, ertypes));
            } else {
              bloodhound.search(q, sync);
            }
          }
        });
        $element.on("typeahead:selected", function() {
          $element.trigger("input");
        });
      }
    };
  }
);
