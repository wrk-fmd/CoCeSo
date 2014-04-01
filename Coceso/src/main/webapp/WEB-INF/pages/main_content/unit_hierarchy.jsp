<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
/**
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @link          https://sourceforge.net/projects/coceso/
* @package       coceso.client.html
* @since         Rev. 1
* @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
*	coceso.css
*/
-->
<html>
<head>
    <title><spring:message code="label.units" /> / <spring:message code="label.main.list" /></title>

    <meta charset="utf-8" />
    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css">
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

<div class="ajax_content">

    <a href="#" class="ui-helper-hidden-accessible">&nbsp;</a>

    <div data-bind="template: { name: 'container-template', data: top }"></div>

</div>


</body>
</html>
