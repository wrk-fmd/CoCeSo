/**
 * CoCeSo
 * Client JS - utils/clock
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "utils/conf", "jquery-i18n"], function($, conf) {
  "use strict";

  $.i18n.properties({
    name: "messages",
    path: conf.get("langBase"),
    mode: "map",
    cache: true,
    async: false,
    checkAvailableLanguages: false,
    language: conf.get("language")
  });

  return $.i18n.prop;
});
