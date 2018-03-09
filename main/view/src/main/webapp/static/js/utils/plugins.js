/**
 * CoCeSo
 * Client JS - utils/plugin
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "utils/conf"], function(ko, conf) {
  "use strict";

  return function(key, callback) {
    var plugins = conf.get("plugins")[key];
    if (plugins instanceof Array && plugins.length > 0) {
      require(plugins, function() {
        ko.utils.arrayForEach(arguments, function(plugin) {
          callback(plugin);
        });
      });
    }
  };
});
