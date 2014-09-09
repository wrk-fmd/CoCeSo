<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML debug window
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
      <table class="table table-striped">
        <tr>
          <th>Time</th>
          <th>Type</th>
          <th>Status</th>
          <th>Message</th>
          <th>URL</th>
          <th>Data</th>
        </tr>
        <!-- ko foreach: filtered -->
        <tr>
          <td data-bind="text: time"></td>
          <td data-bind="text: type"></td>
          <td data-bind="text: status"></td>
          <td data-bind="text: message"></td>
          <td data-bind="text: url"></td>
          <td data-bind="text: data"></td>
        </tr>
        <!-- /ko -->
      </table>
    </div>
  </body>
</html>
