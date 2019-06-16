var checked_value;

function viewQuestions(testId) 
{
    document.getElementById("viewTestDiv").style.display = "none";
    document.getElementById("createTestDiv").style.display = "none";
    document.getElementById("viewQuestionDiv").style.display = "block";
    
    $.ajax({
        url: 'Controller?operation=viewQuestions&testId=' + testId,
        type: 'GET',
        success: function(questionArray)
        {
            var data = '<br><br><br><br><br><input type="button" class="Btn" id="BackBtn" value="Back" onclick="back()"><h1>Question Details</h1><div  id="question">';
            var len = questionArray.length;
            for( var i = 1; i <= len; i++)
            {
                data += '<div id="questionDiv'+i+'" class="question" style="border: 1px solid #C0C0C0; border-radius: 20px;padding: 30px 20px 20px 20px;margin-top: '+10+'px;margin-right: 10px;">\n\
                        <p id="p">Question '+i+'</p><br>\n\
                        <span class="question_span" id="question_value'+i+'">'+questionArray[i-1].question.question+'</span><br>\n\
                        <input type="hidden" id="questionId'+i+'" value="'+questionArray[i-1].question.questionId+'">\n\
                        <input type="text" id="questionText'+i+'" style="display: none" ><br>';
                               
                var n = questionArray[i-1].option.length;
                for(var j = 1; j <= n; j++)
                {
                    data += '<label class="container" id="label'+i+'-'+j+'">';
                
                    if(questionArray[i-1].correctOption === questionArray[i-1].option[j-1].option)
                    {
                        data += '<input type="radio" name="correctOption'+i+'" id="option_value'+j+'-'+i+'" value="'+questionArray[i-1].option[j-1].optionId+'" checked disabled>';
                    }
                    else
                    {
                        data += '<input type="radio" name="correctOption'+i+'" id="option_value'+j+'-'+i+'" value="'+questionArray[i-1].option[j-1].optionId+'" disabled>';
                    }
                    data += '<span class="option" name="option_value'+i+'" id="span_option'+j+'-'+i+'">'+questionArray[i-1].option[j-1].option+'</span>\n\
                            <input type="hidden" id="optionId'+j+'-'+i+'" value="'+questionArray[i-1].option[j-1].optionId+'">\n\
                            <input type="text" id="optionText'+j+'-'+i+'" style="display: none" ><br><br></label>';
                }               
                data += '<input type="button" class="Btn" id="editBtn'+i+'" value="Edit" onclick="editQuestion('+i+','+n+','+len+')" style="display: block">';
                data += '<input type="button" class="Btn" id="deleteBtn'+i+'" value="Delete" onclick="deleteQuestion('+i+')" style="display: block">';
                data += '<input type="button" class="Btn" id="submitBtn'+i+'" value="Submit" onclick="submitQuestion('+i+','+n+')" style="display: none">';
                data += '<input type="button" class="Btn" id="cancelBtn'+i+'" value="Cancel" onclick="cancelQuestion('+i+','+n+')" style="display: none">';
                data += '</div>';
                
                $('#questionDiv'+(i-1)).after(data);
                data ="";
            }
            data += '<input type="button" id="quesAddBtn" value="Add Question" onclick="addNewQuestion('+(len+1)+','+testId+')" style="margin-top: '+30+'px;margin-left: 85%;" ">';
            $('#questionDiv'+(len)).after(data);
            
        }
    });
}

function editQuestion(i, n, total)
{
    document.getElementById("question_value"+i).style.display = "none";
    document.getElementById("editBtn"+i).style.display = "none";
    document.getElementById("deleteBtn"+i).style.display = "none";
    document.getElementById("submitBtn"+i).style.display = "block";
    document.getElementById("cancelBtn"+i).style.display = "block";
    document.getElementById("questionText"+i).style.display = "block";
    
    document.getElementById("questionText"+i).value=document.getElementById("question_value"+i).innerHTML;
    
    for( var j = 1; j <= n; j++)
    {
        if(document.getElementById("option_value"+j+"-"+i).checked)
        {
            checked_value = j;
        }
    }

    for( var j = 1; j <= n; j++)
    {
        document.getElementById("span_option"+j+"-"+i).style.display = "none";
        document.getElementById("option_value"+j+"-"+i).disabled=false;
        
        document.getElementById("optionText"+j+"-"+i).style.display="block";
        document.getElementById("optionText"+j+"-"+i).value=document.getElementById("span_option"+j+"-"+i).innerHTML;
    }
}

function deleteQuestion(i)
{
    var retVal = confirm("Do you want to delete the question ?");
    if(retVal === true)
    {
        var questionId = document.getElementById("questionId"+i).value;
        $.ajax({
            url: 'Controller?operation=deleteQuestion',
            type: 'POST',
            contentType: 'text/plain; charset=utf-8',
            dataType: 'text',
            data: questionId,
            success: function(resultStatus)
            {
                alert(resultStatus);
                if(Number(resultStatus == 1))
                {
                    alert('Deleted Successfully');
                    var input = document.getElementById('questionDiv'+i);
                    input.parentNode.removeChild(input);
                }
                else
                {
                    alert('Invalid Deletion');
                }

            }
        }); 
        
    }
}

function submitQuestion(i, n)
{
    var retVal = confirm("Do you want to submit the question ?");
    if(retVal === true)
    {
        var questionId = document.getElementById("questionId"+i).value;
        var question = document.getElementById("questionText"+i).value;

        var option_length = document.getElementsByName("option_value"+i).length;
        var optionId = [option_length];
        var option = [option_length];
        var correctOption;
        for(var j = 1; j <= option_length; j++) 
        {
            optionId[j-1] = document.getElementById('option_value'+j+'-'+i).value;
            option[j-1] = document.getElementById("optionText"+j+"-"+i).value;

            if(document.getElementById("option_value"+j+'-'+i).checked) 
            {
                 correctOption = document.getElementById("option_value"+j+'-'+i).value;
            }

        }
        
        var data = { "questionId" : questionId, "question" : question, "optionId" : optionId, "option" : option, "correctOption" : correctOption};

        $.ajax({
            url: 'Controller?operation=updateQuestion',
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            data: JSON.stringify(data),
            success: function(msg)
            {
                if(msg.message === "Success")
                {
                    document.getElementById("question_value"+i).style.display="block";
                    document.getElementById("editBtn"+i).style.display="block";
                    document.getElementById("deleteBtn"+i).style.display="block";
                    document.getElementById("submitBtn"+i).style.display="none";
                    document.getElementById("cancelBtn"+i).style.display = "none";
                    document.getElementById("questionText"+i).style.display="none";

                    document.getElementById("question_value"+i).innerHTML=document.getElementById("questionText"+i).value;

                    for( var j = 1; j <= n; j++)
                    {
                        document.getElementById("span_option"+j+"-"+i).style.display="block";
                        document.getElementById("option_value"+j+"-"+i).disabled=true;
                        document.getElementById("optionText"+j+"-"+i).style.display="none";
                        document.getElementById("span_option"+j+"-"+i).innerHTML=document.getElementById("optionText"+j+"-"+i).value;
                    }
                    alert('Updated Successfully');
                }
                else
                {
                    document.getElementById("question_value"+i).style.display="none";
                    document.getElementById("editBtn"+i).style.display="none";
                    document.getElementById("deleteBtn"+i).style.display="none";
                    document.getElementById("submitBtn"+i).style.display="block";
                    document.getElementById("cancelBtn"+i).style.display = "block";
                    document.getElementById("questionText"+i).style.display="block";
                    
                    document.getElementById("option_value"+checked_value+"-"+i).checked=true;
                    
                    for( var j = 1; j <= n; j++)
                    {
                        document.getElementById("span_option"+j+"-"+i).style.display="none";
                        document.getElementById("option_value"+j+"-"+i).disabled=false;
                        document.getElementById("optionText"+j+"-"+i).style.display="block";
                    }
                    alert(msg.message);
                }

            }
        }); 
    }    
    else
    {
        document.getElementById("question_value"+i).style.display="block";
        document.getElementById("editBtn"+i).style.display="block";
        document.getElementById("deleteBtn"+i).style.display="block";
        document.getElementById("submitBtn"+i).style.display="none";
        document.getElementById("cancelBtn"+i).style.display = "none";
        document.getElementById("questionText"+i).style.display="none";

        document.getElementById("option_value"+checked_value+"-"+i).checked=true;

        for( var j = 1; j <= n; j++)
        {
            document.getElementById("span_option"+j+"-"+i).style.display="block";
            document.getElementById("option_value"+j+"-"+i).disabled=true;
            document.getElementById("optionText"+j+"-"+i).style.display="none";
        }
    }
}

function cancelQuestion(i, n)
{
    var retVal = confirm("Do you want to Cancel ?");
    if(retVal === true)
    {
        document.getElementById("question_value"+i).style.display="block";
        document.getElementById("editBtn"+i).style.display="block";
        document.getElementById("deleteBtn"+i).style.display="block";
        document.getElementById("submitBtn"+i).style.display="none";
        document.getElementById("cancelBtn"+i).style.display = "none";
        document.getElementById("questionText"+i).style.display="none";

        document.getElementById("option_value"+checked_value+"-"+i).checked=true;

        for( var j = 1; j <= n; j++)
        {
            document.getElementById("span_option"+j+"-"+i).style.display="block";
            document.getElementById("option_value"+j+"-"+i).disabled=true;
            document.getElementById("optionText"+j+"-"+i).style.display="none";
        }
    }
}
function addNewQuestion(len, testId) 
{
    document.getElementById("quesAddBtn").style.display = "none";
    
    $('#quesAddBtn').before('<div id="questiondiv'+len+'" class="question" style="border: 1px solid #C0C0C0; border-radius: 20px;padding: 30px 20px 20px 20px;margin-top: 10px;margin-right: 10px;">\n\
                               <p id="p">Question '+len+'</p>\n\
                                    <input type="text" id="new_question'+len+'" name="newquestion" placeholder="Enter Question '+len+'" required/>\n\
                               <p id="p1-'+len+'">Option 1</p>\n\
                                    <label class="container" id="label1-'+len+'">\n\
                                        <input type="radio" id="new_correct_option1-'+len+'" name="new_correct_option'+len+'" value="1" checked required>\n\
                                        <input type="text"  id="new_option1-'+len+'" name="new_option'+len+'" placeholder="Enter Option 1" required/>\n\
                                    </label>\n\
                               <p id="p2-'+len+'">Option 2</p>\n\
                                    <label class="container" id="label2-'+len+'">\n\
                                        <input type="radio" id="new_correct_option2-'+len+'" name="new_correct_option'+len+'" value="2" required>\n\
                                        <input type="text" id="new_option2-'+len+'" name="new_option'+len+'" placeholder="Enter Option 2" required/>\n\
                                    </label>\n\
                               <p id="p3-'+len+'">Option 3</p>\n\
                                    <label class="container" id="label3-'+len+'">\n\
                                        <input type="radio" id="new_correct_option3-'+len+'" name="new_correct_option'+len+'" value="3" required>\n\
                                        <input type="text" id="new_option3-'+len+'" name="new_option'+len+'" placeholder="Enter Option 3" required/>\n\
                                    </label>\n\
                               <input type="button" id="optAdd'+len+'" name="optAdd" value="Add Option" onclick="addNewOption('+len+')"/>\n\
                               <input type="button" id="optRemove'+len+'" name="optRemove" value="Remove Option" onclick="removeNewOption('+len+')"/><br>\n\
                               <input type="button" id="submit'+len+'" name="submit" value="Submit" onclick="submitNewQuestion('+len+','+testId+')"/>\n\
                               <input type="button" id="cancel'+len+'" name="cancel" value="Cancel" onclick="cancel('+len+')"/><br>\n\
                               </div>');
}

function addNewOption(n) 
{
    var len = document.getElementsByName('new_option'+n).length+1;
    if(len <= 5)
    {
        $('#optAdd'+n).before('<p id="p'+len+'-'+n+'">Option '+len+'</p>\n\
                            <label class="container" id="label'+len+'-'+n+'">\n\
                                 <input type="radio" id="new_correct_option'+len+'-'+n+'" name="new_correct_option'+n+'" value="'+len+'" required>\n\
                                 <input type="text"  id="new_option'+len+'-'+n+'" name="new_option'+n+'" placeholder="Enter Option '+len+'" required/>\n\
                            </label>');
    }
    else
    {
        alert('Maximum 5 Options Only');
    }
}

function removeNewOption(n) 
{
    var len = document.getElementsByName('new_option'+n).length;
    if(len === 3) 
    {
        alert('Alteast provide 3 options');
    }
    else 
    {
        var input = document.getElementById('new_option'+len+'-'+n);
        input.parentNode.removeChild(input);
        var para = document.getElementById('p'+len+'-'+n);
        para.parentNode.removeChild(para);
        var radio = document.getElementById('new_correct_option'+len+'-'+n);
        radio.parentNode.removeChild(radio);    
        var label = document.getElementById('label'+len+'-'+n);
        label.parentNode.removeChild(label);
    }
}

function cancel(len) 
{
    var input = document.getElementById('questiondiv'+len);
    input.parentNode.removeChild(input);
    document.getElementById("quesAddBtn").style.display = "block";
}

function submitNewQuestion(len, testId)
{
    var retVal = confirm("Do you want to edit the question ?");
    if(retVal === true)
    {
        document.getElementById("quesAddBtn").style.display = "block";
        var question = document.getElementById('new_question'+len).value;
        var option_length = document.getElementsByName("new_option"+len).length;
        var option = [option_length];

        for(var j = 1; j <= option_length; j++) 
        {
            option[j-1] = document.getElementById('new_option'+j+'-'+len).value;
        }  

        var correctOptionId = newCorrectOption(len);
        var correct_option = document.getElementById('new_option'+correctOptionId+'-'+len).value;

        var data = {"testId": testId, "question" : question, "options" : option, "correctOption" : correct_option};

        var dataJSON = JSON.stringify(data);

        $.ajax({
                url: 'Controller?operation=AddNewQuestion',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                data: dataJSON,
                success: function(msg)
                {
                    if(msg.message === "Success")
                    {
                        alert('Inserted Successfully');
                        window.location.assign('index.jsp');
                    }
                    else
                    {
                        alert(msg.message);
                    }
                }
        });
    }    
    else
    {
        cancel(len);
    }
}

function newCorrectOption(n) 
{
    for(var i = 1; i <= document.getElementsByName('new_option'+n).length; i++) 
    {
        if(document.getElementById("new_correct_option"+i+'-'+n).checked) 
        {
            return document.getElementById("new_correct_option"+i+'-'+n).value;
        }
    }
}

function back()
{
    location.href = 'index.jsp';
}