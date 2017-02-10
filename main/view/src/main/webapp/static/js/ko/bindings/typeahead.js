/**
 * CoCeSo
 * Client JS - ko/bindings/typeahead
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module ko/bindings/typeahead
 * @param {module:jquery} $
 * @param {module:bloodhound} Bloodhound
 */
define(["jquery", "bloodhound", "typeahead.js"], function($, Bloodhound) {
  "use strict";

  var typeahead = function(element, url, displayKey, buildDisplayName, callback) {
    var bloodhound = new Bloodhound({
      datumTokenizer: Bloodhound.tokenizers.whitespace,
      queryTokenizer: Bloodhound.tokenizers.whitespace,
      remote: {
        url: url,
        wildcard: "%QUERY"
      }
    });

    var $element = $(element);
    $element.typeahead({
      minLength: 2,
      highlight: true
    }, {
      displayKey: displayKey,
      templates: {
        suggestion: buildDisplayName
      },
      limit: 20,
      source: bloodhound
    });

    if (callback instanceof Function) {
      $element.on("typeahead:selected typeahead:cursorchanged", function(event, data) {
        callback(element, data);
      });
    }
    $element.on("typeahead:selected", function() {
      $element.trigger("input");
    });
  };

  return typeahead;
});
