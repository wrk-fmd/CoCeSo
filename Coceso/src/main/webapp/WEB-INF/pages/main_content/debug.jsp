<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
/**
 * CoCeSo
 * Client HTML Debug
 * Copyright (c) WRK\Daniel Rohr
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright (c) 2013 Daniel Rohr
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.client.html
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Dependencies:
 *	coceso.client.css
 */
-->
<html lang="en">
  <head>
    <title>Debug</title>
    <meta charset="utf-8" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css" />
  </head>
  <body>
    <div class="alert alert-danger"><strong>No direct access</strong><br/>Use the main interface.</div>

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
