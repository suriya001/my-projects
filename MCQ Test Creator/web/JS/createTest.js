function addOption(n) 
{
    var len = document.getElementsByName('option'+n).length+1;
    if(len <= 5)
    {
        $('#optAdd'+n).before('<p id="p'+len+'-'+n+'">Option '+len+'</p>\n\
                            <label class="container" id="label'+len+'-'+n+'">\n\
                                 <input type="radio" id="correct_option'+len+'-'+n+'" name="correct_option'+n+'" value="'+len+'" required>\n\
                                 <input type="text"  id="option'+len+'-'+n+'" name="option'+n+'" placeholder="Enter Option '+len+'" required/>\n\
                            </label>');
    }
    else
    {
        alert('Maximum 5 Options Only')
    }
}

function removeOption(n) 
{
    var len = document.getElementsByName('option'+n).length;
    if(len === 3) 
    {
        alert('Alteast provide 3 options');
    }
    else 
    {
        var input = document.getElementById('option'+len+'-'+n);
        input.parentNode.removeChild(input);
        var para = document.getElementById('p'+len+'-'+n);
        para.parentNode.removeChild(para);
        var radio = document.getElementById('correct_option'+len+'-'+n);
        radio.parentNode.removeChild(radio);
        var label = document.getElementById('label'+len+'-'+n);
        label.parentNode.removeChild(label);
    }
}

function addQuestion(n) 
{
    var len = document.getElementsByClassName('question').length+1;
    $('#quesAdd').before('<div id="questiondiv'+len+'" class="question" style="border: 1px solid #C0C0C0; border-radius: 20px;padding: 0px 20px 20px 20px;margin-top: 10px;">\n\
                               <p id="p'+n+'">Question '+len+'</p>\n\
                                    <input type="text" id="question'+len+'" name="question" placeholder="Enter Question '+len+'" required/>\n\
                               <p id="p1-'+len+'">Option 1</p>\n\
                                    <label class="container" id="label1-'+len+'">\n\
                                        <input type="radio" id="correct_option1-'+len+'" name="correct_option'+len+'" value="1" required>\n\
                                        <input type="text"  id="option1-'+len+'" name="option'+len+'" placeholder="Enter Option 1" required/>\n\
                                    </label>\n\
                               <p id="p2-'+len+'">Option 2</p>\n\
                                    <label class="container" id="label2-'+len+'">\n\
                                        <input type="radio" id="correct_option2-'+len+'" name="correct_option'+len+'" value="2" required>\n\
                                        <input type="text" id="option2-'+len+'" name="option'+len+'" placeholder="Enter Option 2" required/>\n\
                                    </label>\n\
                               <p id="p3-'+len+'">Option 3</p>\n\
                                    <label class="container" id="label3-'+len+'">\n\
                                        <input type="radio" id="correct_option3-'+len+'" name="correct_option'+len+'" value="3" required>\n\
                                        <input type="text" id="option3-'+len+'" name="option'+len+'" placeholder="Enter Option 3" required/>\n\
                                    </label>\n\
                               <input type="button" id="optAdd'+len+'" name="optAdd" value="Add Option" onclick="addOption('+len+')"/>\n\
                               <input type="button" id="optRemove'+len+'" name="optRemove" value="Remove Option" onclick="removeOption('+len+')"/><br>\n\
                               </div>');
}

function removeQuestion(n) 
{
    var len = document.getElementsByClassName('question').length;
    if(len === 1) 
    {
        alert('Alteast provide one question');
    }
    else 
    {
        var input = document.getElementById('questiondiv'+len);
        input.parentNode.removeChild(input);
    }
}

function processQuestion() 
{
    var testName = document.getElementById("testName").value;
    var testDate = document.getElementById("testDate").value;
    var testLink=document.getElementById("testLink").value;
    
    var question = [];
    var option = [];
    var correct_option = [];

    var question_length = document.getElementsByName("question").length;
    for(var i = 1; i <= question_length; i++) 
    {

        question[i-1] = document.getElementById('question'+i).value;
        var option_length = document.getElementsByName("option"+i).length;
        option[i-1] = new Array(option_length);
        for(var j = 1; j <= option_length; j++) 
        {
            option[i-1][j-1] = document.getElementById('option'+j+'-'+i).value;
        }  
        var correctOptionId = correctOption('correct_option'+i);
        if(correctOptionId === 0)
        {
            alert('Select a Correct Option for Question '+i);
            return;
        }
        correct_option[i-1] = document.getElementById('option'+correctOptionId+'-'+i).value;
        
    }
    var data = { "testName" : testName, "testDate" : testDate, "testLink" : testLink, "questions" : []};

    for(var i = 0; i < question_length; i++) 
    {
        data["questions"].push({"question" : question[i], options : option[i], correctOption : correct_option[i]});
    }
    var dataJSON = JSON.stringify(data);
    
    $.ajax({
            url: 'Controller?operation=insertTestDetails',
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            data: dataJSON,
            success: function(msg)
            {
                if(msg.message === "Success")
                {
                    alert('Success');
                    window.location.assign('index.jsp');
                }
                else
                {
                    alert(msg.message);
                }
            }
    });
}

function correctOption(name) 
{
    var form_id = document.getElementById("createTestForm");
    var radio = form_id.elements[name];
    for(var i = 0; i < radio.length; i++) 
    {
        if(radio[i].checked) 
        {
            return radio[i].value;
        }
    }
    return 0;
}
