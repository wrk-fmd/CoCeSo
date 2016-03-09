<!DOCTYPE html>
<%--
/**
 * CoCeSo
 * Client HTML unit hierarchy window
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <title>No direct access</title>
  </head>
  <body style="display: none">
    <div class="ajax_content">
      <!-- ko if: top -->
      <div data-bind="template: {name: 'container-template', data: top}"></div>
      <!-- /ko -->
    </div>
  </body>
</html>
