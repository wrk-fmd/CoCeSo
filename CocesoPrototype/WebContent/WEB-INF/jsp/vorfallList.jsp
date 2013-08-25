<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Vorfall Liste</title>
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
	text-align: center;
}

.heading {
	font-size: 18px;
	color: white;
	font: bold;
	background-color: orange;
	border: thick;
}
</style>

<!-- <link href="/design/voerfalllist.css" rel="stylesheet" type="text/css">  -->

</head>
<body>
	<br />
	<br />
	<br />
	<b>Vorfall Liste</b>
	<br />
	<br />

	<table border="1">
		<tr>
			<td class="heading">ID</td>
			<td class="heading">Start</td>
			<td class="heading">Ende</td>
			<td class="heading">Typ</td>
			<td class="heading">Text</td>
			<td class="heading">Status</td>
			<td class="heading">Edit</td>
			<td class="heading">Delete</td>
		</tr>
		<c:forEach var="vorfall" items="${map.vorfallList}">
			<tr>
				<td>${vorfall.id}</td>
				<td>${vorfall.start}</td>
				<td>${vorfall.end}</td>
				<td>${vorfall.typ}</td>
				<td>${vorfall.text}</td>
				<td>${vorfall.status}</td>
				<td><a href="">Edit</a></td> <!--edit?id=${vorfall.id}-->
				<td><a href="delete?id=${vorfall.id}">Delete</a></td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="7"><a href="register">Neuen Vorfall Anlegen</a></td>
		</tr>
	</table>
	
	<br />
	<br />
	<b>Einheit Liste</b>
	<br />
	<br />
	 
	<table border="1">
		<tr>
			<td class="heading">ID</td>
			<td class="heading">Name</td>
			<td class="heading">Typ</td>
			<td class="heading">Status</td>
			<td class="heading">Edit</td>
			<td class="heading">Delete</td>
		</tr>
		<c:forEach var="einheit" items="${map.einheitList}">
			<tr>
				<td>${einheit.id}</td>
				<td>${einheit.name}</td>
				<td>${einheit.typ}</td>
				<td>${einheit.status}</td>
				<td><a href="">Edit</a></td> <!--edit?id=${vorfall.id}-->
				<td><a href="">Delete</a></td><!--delete?id=${einheit.id}-->
			</tr>
		</c:forEach>
		<tr>
			<td colspan="7"><a href="register">Neue Einheit Anlegen</a></td>
		</tr>
	</table>

</body>
</html>