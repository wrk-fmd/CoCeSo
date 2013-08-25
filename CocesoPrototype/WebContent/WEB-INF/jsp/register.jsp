<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Neuen Vorfall anlegen</title>
<style>
body {
	font-size: 20px;
	color: teal;
	font-family: Calibri;
}

td {
	font-size: 15px;
	color: black;
	width: 100px;
	height: 22px;
	text-align: left;
}

.heading {
	font-size: 18px;
	color: white;
	font: bold;
	background-color: orange;
	border: thick;
}
</style>
</head>
<body>
	<br />
	<br />
	<br />
	<b>Neuer Vorfall</b>
	<br />
	<br />
	<div>
		<form:form method="post" action="/insert" modelAttribute="vorfall">
			<table>
				<tr>
					<td>Start:</td>
					<td><form:input path="start" /></td>
				</tr>
				<tr>
					<td>Ende:</td>
					<td><form:input path="end" /></td>
				</tr>
				<tr>
					<td>Typ:</td>
					<td><form:radiobuttons path="typ" items="${map.vorfallTypList}" /></td>
				</tr>
				<tr>
					<td>Text:</td>
					<td><form:textarea path="text" /></td>
				</tr>
				<tr>
					<td>Status:</td>
					<td><form:select path="status" items="${map.statusList}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="submit" value="Speichern" /></td>
				</tr>
				<tr>

					<td colspan="2"><a href="getVorfallList">Klicken Sie Hier um alle Vorfälle zu sehen</a></td> 
				</tr>
			</table>
		</form:form>
	</div>
</body>
</html>