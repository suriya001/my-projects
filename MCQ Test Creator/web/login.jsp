<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
  
    if (session.getAttribute("examinerId") != null)
    {
%>
            <jsp:forward page="index.jsp" />
<%
    }
    else
    {
%>
        <!DOCTYPE html>
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="CSS/LoginRegisterStyle.css">
                <script src="JS/login.js"></script>
                <title>LOGIN</title>
            </head>
            <body>
                <div class="login-box" id="loginDiv" style="display: block">
                    <form  id="loginForm" action="Controller" method="POST">
                        <img src="PICS/avatar.png" class="avatar">
                        <h1>Login Here</h1>
                        <p>User Name</p>
                        <input type="text" id="username" name="username" placeholder="Enter User Name" required>
                        <p>Password</p>
                            <input type="password" id="password" name="password" placeholder="Enter Password" required>
                        
                        <input type="hidden" name="operation" value="login">
                        
                        <input type="submit" value="Login">
                        <br><br>
                        <input type="button" value="Register" onclick="gotoRegister()">
                    </form>
                </div>
                
                <div class="register-box" id="registerDiv" style="display: none">
                    <form id="registerForm" method="POST" action="Controller">
                        <h1>Register Here</h1>
                        <p>Examiner Name</p>
                        <input type="text" name="examinerName" id="examainerName" placeholder="Enter Your Name" required>
                        <p>Username</p>
                        <input type="text" name="username" id="registerUsername" placeholder="Enter Username" required>
                        <p>Password</p>
                        <input type="password" name="password" id="registerPassword" placeholder="Enter Password" required>
                        <p>Phone Number</p>
                        <input type="text" name="phoneNo" id="phoneNo" placeholder="Enter Your Phone Number" required>
                        
                        <input type="hidden" name="operation" value="register">
                        
                        <input type="submit" name="registerSubmit" id="registerSubmit" value="Register" >
                        <input type="button" name="login" id="login" value="Login" onclick="gotoLogin()" >
                    </form>
                </div>
                
            </body>
        </html>
<%
    }
%>
