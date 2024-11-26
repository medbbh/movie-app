<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie List</title>
    <style>
        /* Previous styles remain the same */
    </style>
</head>
<body>
    <h1>Popular Movies</h1>
    <table>
        <thead>
            <tr>
                <th>Title</th>
                <th>Year</th>
                <th>Rating</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="movie" items="${movies}">
                <tr>
                    <td>${movie.title}</td>
                    <td>${movie.year}</td>
                    <td>${movie.rating}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>
    <a href="index.jsp">Back to Home</a>
</body>
</html>