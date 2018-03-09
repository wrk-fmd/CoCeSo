/**
 * CoCeSo
 * Client JS - ko/bindings/date
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
 * @module ko/bindings/date
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "utils/conf", "bootstrap-datepicker", "bootstrap-datepicker.de"],
  function($, ko, conf) {
    "use strict";

    var input = $("<input type='date' value='foo'/>")[0];
    if (input.type === "date" && input.value !== "foo") {
      // Native browser support
      ko.bindingHandlers.date = ko.bindingHandlers.value;
      return;
    }

    var lang = conf.get("language");
    if ($.fn.datepicker.dates[lang]) {
      $.fn.datepicker.defaults.language = lang;
    }
    $.fn.datepicker.defaults.autoclose = true;
    $.fn.datepicker.defaults.clearBtn = true;

    function utcToLocal(utc) {
      // Dates here are midnight UTC, but datepicker assumes local dates
      return new Date(utc.getTime() + (utc.getTimezoneOffset() * 60000));
    }

    ko.bindingHandlers.date = {
      init: function(element, valueAccessor) {
        var $el = $(element), opt = {};

        if ($el.attr("min")) {
          opt.startDate = utcToLocal(new Date($el.attr("min")));
        }
        if ($el.attr("max")) {
          opt.endDate = utcToLocal(new Date($el.attr("max")));
        }

        $el.datepicker(opt).on("changeDate clearDate", function(e) {
          valueAccessor()($el.datepicker("getFormattedDate", "yyyy-mm-dd"));
        });
      },
      update: function(element, valueAccessor) {
        var $el = $(element), date = ko.utils.unwrapObservable(valueAccessor());
        if ($el.datepicker("getFormattedDate", "yyyy-mm-dd") !== date) {
          if (date) {
            $el.datepicker("update", utcToLocal(new Date(date))).datepicker("setValue");
          } else {
            $el.datepicker("clearDates");
          }
        }
      }
    };
  }
);
