<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Test</title>
        <link rel="stylesheet" type="text/css" href="CSS/TestStyle.css">
        
        <script src="JS/jquery-3.4.1.js"></script>
        
        <script type="text/javascript">
            
            function insertCandidateDetails()
            {
                var candidateName = document.getElementById("candidateName").value;
                var regno = document.getElementById("regno").value;
                var dept = document.getElementById("dept").value;
                var dob = document.getElementById("dob").value;
                var testLink = "<%= request.getParameter("testLink") %>";
                
                var obj = {"testLink" : testLink, "candidateName" : candidateName, "regno" : regno, "dept" : dept, "dob" : dob};
                
                $.ajax({
                        url: 'Test?operation=insertCandidatetDetails',
                        type: 'POST',
                        contentType: 'application/json; charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify(obj),
                        success: function(msg)
                        {
                            if(msg.message === "Success")
                            {
                                alert('Start Test');
                                test(testLink, regno, msg.questionArray);
                            }
                            else
                            {
                                alert(msg.message);
                            }
                        }
                });
            }
            
            function test(testLink, regno, questionArray)
            {
                document.getElementById("candidate_detailsDiv").style.display = "none";
                document.getElementById("testDiv").style.display = "block";
                
                var data = '<br><br><br><br><br><h1>Answer All Questions</h1><div id="question">';
                var len = questionArray.length; 
                for( var i = 1; i <= len; i++)
                {

                    data +='<div id="testDiv'+i+'" class="question" style="border: 1px solid #C0C0C0; border-radius: 20px;padding: 30px 20px 20px 20px;margin: 10px 50px 10px 50px;">\n\
                            <p class="test_p">Question '+i+'</p><br>\n\
                                <span class="test_question_span" name="test_question" id="test_question_value'+i+'">'+questionArray[i-1].question.question+'</span><br><br>\n\
                                <input type="hidden" id="test_questionId'+i+'" value="'+questionArray[i-1].question.questionId+'">';

                    var n = questionArray[i-1].option.length;
                    for(var j = 1; j <= n; j++)
                    {
                        data += '<label class="container" id="label'+i+'-'+j+'">';
                        data += '<input type="radio" class="test_radio" name="test_correctOption'+i+'" id="test_correctOption'+j+'-'+i+'" value="'+questionArray[i-1].option[j-1].optionId+'">\n\
                                <span class="test_option_span" name="test_option_value'+i+'" id="test_span_option'+j+'-'+i+'">'+questionArray[i-1].option[j-1].option+'</span><br></label>';
                    }               
                    data += '</div>';
                }
                data += '<input type="button" class="Btn" id="test_submitBtn" value="Submit" onclick="submitTest('+len+',\''+testLink+'\',\''+regno+'\')">';
                $('#testDiv').append(data);
            }
            
            function submitTest(len, testLink, regno)
            {
                var question = [];
                var correctOption = [];
                for( var i = 1; i <=len; i++)
                {
                    question[i-1] = document.getElementById("test_questionId"+i).value;
                    var option_length = document.getElementsByName("test_option_value"+i).length;
                    
                    var correctOptionId = getCorrectOptionId(i, option_length);
                    if(correctOptionId === 0)
                    {
                        alert('Select a Correct Option for Question '+i);
                        return;
                    }
                    correctOption[i-1] = correctOptionId;
                }
                
                var obj = {"testLink" : testLink, "registerNo" : regno, "question" : question, "correctOption" : correctOption};
                
                $.ajax({
                        url: 'Test?operation=insertCandidateAnswer',
                        type: 'POST',
                        contentType: 'application/json; charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify(obj),
                        success: function(msg)
                        {
                            document.getElementById("testDiv").style.display = "none";
                            document.getElementById("scoreDiv").style.display = "block";
                            $('#scoreDiv').append("<h1>You Have Completed the Test</h1><br><h1>Your Score Is "+msg.score+"</h1>");
                        }
                });
            }
            
            function getCorrectOptionId(i, len) 
            {
                for(var j= 1; j <= len; j++) 
                {
                    if(document.getElementById("test_correctOption"+j+"-"+i).checked) 
                    {
                        return document.getElementById("test_correctOption"+j+"-"+i).value;
                    }
                }
                return 0;
            }
        </script>
    </head>
    <body>
        <div class="candidate_details-box" id="candidate_detailsDiv" style="display: block;">
            <form id="candidateForm" method="POST" action="CandidateTest">
                <h1>Fill the Details</h1>
                <p>Candidate Name</p>
                <input type="text" name="candidateName" id="candidateName" placeholder="Enter Your Name" required>
                <p>Register No</p>
                <input type="text" name="regno" id="regno" placeholder="Enter Your Register No" required>
                <p>Department</p>
                <input type="text" name="dept" id="dept" placeholder="Enter Your Department" required>
                <p>Date Of Birth</p>
                <input type="date" name="dob" id="dob" required>

                <input type="button" id="submitCandidateDetails" value="Submit" onclick="insertCandidateDetails()" >
            </form>
        </div>
        <div id="testDiv" style="display: none; "></div>
        <div id="scoreDiv" style="display: none; "></div>
    </body>
</html>
