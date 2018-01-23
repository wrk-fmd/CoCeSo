/**
 * CoCeSo
 * Client JS - data/store/sections
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module {Class} data/store/sections
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 * @param {module:utils/i18n} _
 */
define(["jquery", "knockout", "utils/conf", "utils/i18n"],
  function($, ko, conf, _) {
    "use strict";

    var defaultOption = _("concern.section") + "...";

    /**
     * Edit concern properties
     *
     * @constructor
     */
    var Sections = function() {
      var self = this;

      this.sections = ko.observableArray([]);
      this.selectSections = ko.observableArray([{value: "", name: defaultOption}]);
      this.hasSections = ko.computed(function() {
        return this.sections().length > 0;
      }, this);
      this.filter = ko.observable(null);

      this.showAll = function() {
        this.filter(null);
      };

      this.load = function() {
        $.getJSON(conf.get("jsonBase") + "concern/get.json", function(data, status) {
          if (status !== "notmodified" && data) {
            self.sections($.map(data.sections || [], function(name) {
              return {
                name: name,
                select: function() {
                  self.filter(name);
                },
                active: ko.computed(function() {
                  return self.filter() === name;
                })
              };
            }));

            // Dirty fix, because knockout doesn't allow specifying the default options value in select
            self.selectSections($.merge([{value: "", name: defaultOption}], $.map(data.sections || [], function(section) {
              return {value: section, name: section};
            })));
          }
        });
      };

      this.load();
    };

    return new Sections();
  }
);
