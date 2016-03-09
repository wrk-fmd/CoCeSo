/**
 * CoCeSo
 * Client JS - build config
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

({
  dir: "../js-dist/",
  skipDirOptimize: true,
  preserveLicenseComments: false,
  mainConfigFile: "config.js",
  findNestedDependencies: true,
  wrapShim: true,
  modules: [
    {name: "assets/requirejs/require"},
    {name: "dashboard",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "edit_concern",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "edit_user",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "home",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "main",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "map",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "patadmin_form",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "patadmin_transport",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]},
    {name: "patadmin_triage",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead.js", "stomp"]}
  ]
})
