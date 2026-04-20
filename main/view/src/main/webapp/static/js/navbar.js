/**
 * CoCeSo
 * Client JS - dashboard
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

require(["config"], function() {
  require(["jquery", "utils/conf", "data/stomp", "bootstrap/collapse", "bootstrap/dropdown"], function($, conf, stomp) {
    $(".autofocus").first().focus();

    function hasTables() {
      return $("table").length > 0;
    }

    var concernId = localStorage.concern;
    if (!concernId) {
      return;
    }

    var navbar = $("#patadmin-navbar");
    var jsonBase = navbar.data("jsonbase");
    if (jsonBase) {
      conf.set("jsonBase", jsonBase);
    }

    var reloadTimer = null;
    var isReloading = false;

    var viewType = navbar.data("viewtype");

    function hasKnockoutBindings() {
      // Knockout uses data-bind attributes. If we replace DOM fragments via
      // jQuery.load(), bindings get detached and the UI degrades.
      return $("[data-bind]").length > 0;
    }

    function scheduleReload() {
      if (isReloading) {
        return;
      }
      clearTimeout(reloadTimer);
      reloadTimer = setTimeout(function() {
        if (isReloading) {
          return;
        }

        // For most patadmin views, do a full reload to keep Knockout-based
        // pages (registration) stable and avoid fragile table fragment swaps.
        if (viewType !== "transport" || hasKnockoutBindings()) {
          isReloading = true;
          window.location.reload();
          return;
        }

        // Transport is historically safe to partially refresh.
        var $tables = $("table");
        if ($tables.length !== 1) {
          isReloading = true;
          window.location.reload();
          return;
        }
        $tables.first().load(window.location.href + " table:eq(0) > *");
      }, 300);
    }

    if (!hasTables()) {
      return;
    }

    stomp.subscribe("/topic/patient/main/" + concernId, scheduleReload);
    stomp.subscribe("/topic/incident/main/" + concernId, scheduleReload);

  });
});
