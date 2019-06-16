function viewTest()
{
    $.ajax({
            url: 'Controller?operation=viewTestDetails',
            type: 'GET',
            success: function(obj)
            {   
                var data = "";
                if(obj.length > 0)
                {
                    data = "<div id='viewtable'><br><br><br><br><br><br><h1>Test Details</h1><br><table><tr><th>Test Name</th><th>Test Date</th><th>Test link</th><th>Status</th><th>View Questions</th><th>Start Test</th><th>Finish Test</th><th>Result</th></tr>";
                    for(var i = 0; i < obj.length; i++)
                    {
                        data += '<tr id="row'+(i+1)+'">';
                        data += "<td>"+obj[i].testName+"</td>";
                        data += "<td>"+obj[i].testDate+"</td>";
                        data += "<td>"+obj[i].testLink+"</td>";
                        data += "<td>"+obj[i].testStatus+"</td>";
                        data += "<td><input type='button' class='testBtn' id='viewQuestions"+obj[i].testId+"' name='questions' value='Click Here' onclick='viewQuestions("+obj[i].testId+")' ></td>";
                        data += "<td><input type='button' class='testBtn' name='start' value='Click Here' onclick='startTest("+obj[i].testId+",\""+obj[i].testStatus+"\")' ></td>";
                        data += "<td><input type='button' class='testBtn' name='finish' value='Click Here' onclick='finishTest("+obj[i].testId+",\""+obj[i].testStatus+"\")' ></td>";
                        data += "<td><input type='button' class='testBtn' name='result' value='Click Here' onclick='viewResult("+obj[i].testId+")' ></td></tr>";
                    }
                    data += "</table>";
                    data += "<br><br><input type='button' name='createTest' value='Create Test' onclick='createTest()'></div>";
                }
                else
                {
                    data += "<br><br><input type='button' name='createTest' value='Create Test' onclick='createTest()' style='margin-top: 100px;'></div>";
                }
                var div = document.getElementById('viewTestDiv');
                div.innerHTML += data;
            }
    });
}

function startTest(testId, status)
{
    if(status === "completed")
    {
        alert('Test is already Completed');
    }
    else if(status === "live")
    {
        alert('Test is already Started');
    }
    else
    {
        var retVal = confirm("Do you want to start the test ?");
        if(retVal === true)
        {
            $.ajax({
                    url: 'Controller?operation=startTest&testId='+testId,
                    type: 'POST',
                    contentType: 'text/plain; charset=utf-8',

                    success: function(resultStatus)
                    {
                        if(Number(resultStatus) === 1)
                        {
                            location.reload();
                        }
                    }
            });
        }
    }
}

function finishTest(testId, status)
{
    if(status === "completed")
    {
        alert('Test is already Completed');
    }
    else if(status === "pending")
    {
        alert('Test is not started yet');
    }
    else
    {
        var retVal = confirm("Do you want to finish the test ?");
        if(retVal === true)
        {
            $.ajax({
                    url: 'Controller?operation=finishTest&testId='+testId,
                    type: 'POST',
                    contentType: 'text/plain; charset=utf-8',

                    success: function(resultStatus)
                    {
                        if(Number(resultStatus) === 1)
                        {
                            location.reload();
                        }
                    }
            });
        }
    }
}

function viewResult(testId)
{
    document.getElementById("viewTestDiv").style.display = "none";
    document.getElementById("viewCandidateDetailsDiv").style.display = "block";
    
    $.ajax({
            url: 'Controller?operation=viewTestResult&testId='+testId,
            type: 'GET',
            contentType: 'text/plain; charset=utf-8',

            success: function(msg)
            {
                if(msg.message === "Success")
                {
                    var candidateDetails = msg.details;
                    var data = "<div id='candidateDetails'><br><br><input type='button' class='Btn' id='BackBtn' value='Back' onclick='back()'><h1>Canidate Details</h1><br><table><tr><th>Candidate Name</th><th>Register No</th><th>Department</th><th>DOB</th><th>Score</th><th>Status</th><th>View Answers</th></tr>";
                    for(var i = 0; i < candidateDetails.length; i++)
                    {
                        data += '<tr id="details_row'+(i+1)+'">';
                        data += "<td>"+candidateDetails[i].candidateName+"</td>";
                        data += "<td>"+candidateDetails[i].registerNo+"</td>";
                        data += "<td>"+candidateDetails[i].department+"</td>";
                        data += "<td>"+candidateDetails[i].dob+"</td>";
                        data += "<td>"+candidateDetails[i].score+"</td>";
                        data += "<td>"+candidateDetails[i].status+"</td>";
                        data += "<td><input type='button' class='testBtn' name='candidateDetailsBtn' value='Click Here' onclick='viewAnswers("+candidateDetails[i].candidateId+","+testId+")' ></td></tr>";
                    }
                    data += "</table>";
                    var div = document.getElementById('viewCandidateDetailsDiv');
                    div.innerHTML += data;
                }
                else
                {
                    alert(msg.message);
                    back();
                }
            }
    });
}

function viewAnswers(candidateId, testId)
{
    document.getElementById("viewCandidateDetailsDiv").style.display = "none";
    document.getElementById("viewCandidateAnswersDiv").style.display = "block";
    
    $.ajax({
            url: 'Controller?operation=viewCandidateAnswer&candidateId='+candidateId+'&testId='+testId,
            type: 'GET',
            contentType: 'text/plain; charset=utf-8',

            success: function(msg)
            {
                if(msg.message === "Success")
                {
                    var candidateAnswers = msg.answers;
                    var data = "<div id='candidateAnswers'><br><br><input type='button' class='Btn' id='BackBtn' value='Back' onclick='backResult("+testId+")'><h1>Canidate Answers</h1><br><table><tr><th>Question</th><th>Candidate's Answer</th><th>Correct Answer</th></tr>";
                    for(var i = 0; i < candidateAnswers.length; i++)
                    {
                        data += '<tr id="answers_row'+(i+1)+'">';
                        data += "<td>"+candidateAnswers[i].question+"</td>";
                        data += "<td>"+candidateAnswers[i].candidateAnswer+"</td>";
                        data += "<td>"+candidateAnswers[i].correctAnswer+"</td></tr>";
                    }
                    data += "</table></div>";
                    var div = document.getElementById('viewCandidateAnswersDiv');
                    div.innerHTML += data;
                }
                else
                {
                    alert(msg.message);
                    backResult(testId);
                }
            }
    });
}

function backResult(testId)
{
    $('#candidateAnswers').remove();
    $('#candidateDetails').remove();
    viewResult(testId);
}

function createTest()
{
    document.getElementById("viewTestDiv").style.display = "none";
    document.getElementById("createTestDiv").style.display = "block";
}

