/**
 * CoCeSo
 * Client JS - build config
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
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
    {name: "edit_concern",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "edit_user",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "home",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "main",
      include: [
        "main/viewmodels/incidents", "main/viewmodels/incident", "main/viewmodels/units", "main/viewmodels/hierarchy",
        "main/viewmodels/unit", "main/viewmodels/unitdetail", "main/viewmodels/customlog", "main/viewmodels/logs",
        "main/viewmodels/patients", "main/viewmodels/patient", "main/viewmodels/radio", "map/viewmodel"],
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "map",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "navbar",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "patadmin_form",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "patadmin_transport",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]},
    {name: "patadmin_registration",
      excludeShallow: ["bloodhound", "jquery", "jquery-i18n", "leaflet", "knockout", "knockout-sortable", "typeahead", "stomp"]}
  ]
})
