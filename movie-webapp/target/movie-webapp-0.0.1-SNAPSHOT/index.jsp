<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie App Home</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            text-align: center; 
            margin-top: 50px; 
        }
        .links {
            display: flex;
            justify-content: center;
            gap: 20px;
        }
        .links a {
            text-decoration: none;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <h1>Welcome to Movie App</h1>
    <div class="links">
        <a href="home">Home</a>
        <a href="movies">Movie List</a>
        <a href="#">Link 3</a>
        <a href="#">Link 4</a>
        <a href="#">Link 5</a>
    </div>
</body>
</html>