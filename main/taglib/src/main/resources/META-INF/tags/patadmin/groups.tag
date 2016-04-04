<%@tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML treatment groups tag
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<div id="treatment_groups" class="table-responsive">
  <table class="groups table table-striped table-condensed">
    <tbody data-bind="foreach: list">
      <tr>
        <td data-bind="if: image">
          <a data-bind="attr: {href: url}"><img alt="" src="#" data-bind="attr: {src: image}"/></a>
        </td>
        <td>
          <a data-bind="attr: {href: url}, text: call"></a>
        </td>
        <td>
          <span data-bind="text: patients"></span>&nbsp;<spring:message code="patadmin.group.of"/>&nbsp;<span data-bind="text: capacity"></span>
        </td>
        <td data-bind="html: occupation"></td>
      </tr>
    </tbody>
  </table>
</div>
