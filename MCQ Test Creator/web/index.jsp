<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    if (session.getAttribute("examinerId") == null)
    {
%>
        <jsp:forward page="login.jsp" />
<%
    }
    else
    {
%>
        <!DOCTYPE html>
        <html>
            <head>
                <title>Online MCQ Test</title>
                <link rel="stylesheet" type="text/css" href="CSS/LogoutStyle.css">
                <link rel="stylesheet" type="text/css" href="CSS/ViewTestStyle.css">
                <link rel="stylesheet" type="text/css" href="CSS/ViewQuestionStyle.css">
                <link rel="stylesheet" type="text/css" href="CSS/CreateTestStyle.css">
                <script src="JS/jquery-3.4.1.js"></script>
                <script src="JS/logout.js"></script>
                <script src="JS/createTest.js"></script>
                <script src="JS/viewTest.js"></script>
                <script src="JS/viewQuestion.js"></script>
            </head>

            <body onload="viewTest()">
                <div id="logoutDiv">
                    <ul>
                        <li style="float:right"><input type="button" value="Log Out" id="logoutBtn" name="logout"></li>
                    </ul>
                </div>
                
                <div id="viewTestDiv"></div>
                
                <div id="createTestDiv" style="display: none">
                    <input type="button" class="Btn" id="BackBtn" value="Back" onclick="back()">
                    
                    <form id="createTestForm" method="POST">
                        
                        <div class="test_details-box">
                            
                            <h1>Enter the Test Details</h1><br><br>
                            
                            <p>Test Name</p>
                                <input type="text" id="testName" placeholder="Enter Test Name" required>
                            <p>Test Date</p>
                                <input type="date" id="testDate" placeholder="Enter Test Date" required>
                            <p>Test Link</p>
                                <input type="text" id="testLink" placeholder="Enter Test Link" required>
                        </div>
                        
                        <div class="question-box">
                            
                            <h1>Question Details</h1>
                            
                            <div id="questiondiv1" class="question" style="border: 1px solid #C0C0C0; border-radius: 20px;padding: 0px 20px 20px 20px;margin-top: 10px;">

                                <p id="p1">Question 1</p>
                                    <input type="text" id="question1" name="question" placeholder="Enter Question 1" required/>

                                <p id="p1-1">Option 1</p>

                                <label class="container" id="label1-1">
                                    <input type="radio" id="correct_option1-1" name="correct_option1" value="1" required>
                                        <input type="text" id="option1-1" name="option1" placeholder="Enter Option 1" required/>
                                    </label>

                                <p id="p2-1">Option 2</p>

                                <label class="container" id="label2-1">
                                    <input type="radio" id="correct_option2-1" name="correct_option1" value="2" required>
                                        <input type="text" id="option2-1" name="option1" placeholder="Enter Option 2" required/>
                                    </label>

                                <p id="p3-1">Option 3</p>

                                <label class="container" id="label3-1">
                                    <input type="radio" id="correct_option3-1" name="correct_option1" value="3" required>
                                        <input type="text" id="option3-1" name="option1" placeholder="Enter Option 3" required/>
                                    </label>

                                <input type="button" id="optAdd1" name="optAdd" value="Add Option" onclick="addOption(1)"/>
                                <input type="button" id="optRemove1" name="optRemove" value="Remove Option" onclick="removeOption(1)"/><br>

                            </div>
                            <input type="button" id="quesAdd" name="quesAdd" value="Add Question" onclick="addQuestion(1)"/>
                            <input type="button" id="quesRemove" name="quesRemove" value="Remove Question" onclick="removeQuestion(1)"/><br>
                            <input type="button" id="quesSubmit" name="quesSubmit" onclick="processQuestion()" value="Submit Questions"/><br>

                        </div>
                    </form>
                </div>
                
                <div id="viewQuestionDiv" style="display: none">
                    <div id="questionDiv0"></div>
                </div>
                <div id="viewCandidateDetailsDiv" style="display: none"></div>
                <div id="viewCandidateAnswersDiv" style="display: none"></div>
            </body>
        </html>
<%
    }
%>
