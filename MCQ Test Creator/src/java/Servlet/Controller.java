package Servlet;

import Database.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Controller extends HttpServlet 
{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {

        String operation = request.getParameter("operation");
        
        if(operation.equals("viewTestDetails"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            try 
            {
                JSONArray jsArray = new ExaminerTest().viewTestDetails(examinerId);
                if(jsArray.length() > 0)
                {
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println(jsArray);
                }
            }
            catch (SQLException | JSONException e) 
            {
                System.out.println("Exception in View Test - Controller "+e);
            }
        }
        
        else if(operation.equals("viewQuestions"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int testId = Integer.parseInt(request.getParameter("testId"));
            
            try 
            {
                JSONArray jsArray = new ExaminerTest().viewQuestionDetails(testId, examinerId);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(jsArray);
            }
            catch (SQLException | JSONException e) 
            {
                System.out.println("Exception in View Question - Controller "+e);
            }
        }
        
        else if(operation.equals("viewTestResult"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int testId = Integer.parseInt(request.getParameter("testId"));
            try 
            {
                JSONArray candidateDetails = new ExaminerTest().viewTestResult(testId, examinerId);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                
                if(candidateDetails.length() > 0)
                {
                    msg.put("message", "Success");
                    msg.put("details", candidateDetails);
                }
                else
                {
                    msg.put("message", "No One has Started the Test Found");
                }
                out.println(msg);
            }
            catch (SQLException | JSONException e) 
            {
                System.out.println("Exception in Finish Test - Controller "+e);
            }
        }
        
        else if(operation.equals("viewCandidateAnswer"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int candidateId = Integer.parseInt(request.getParameter("candidateId"));
            int testId = Integer.parseInt(request.getParameter("testId"));
            try 
            {
                JSONArray candidateAnswers = new ExaminerTest().viewCandidateAnswer(candidateId, testId, examinerId);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                
                if(candidateAnswers.length() > 0)
                {
                    msg.put("message", "Success");
                    msg.put("answers", candidateAnswers);
                }
                else
                {
                    msg.put("message", "Not Completed Yet");
                }
                out.println(msg);
            }
            catch (SQLException | JSONException e) 
            {
                System.out.println("Exception in Finish Test - Controller "+e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        String operation = request.getParameter("operation");
        
        if (operation.equals("login")) 
        {	            
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            try
            {
                int examinerId = new Login().validateLogin(username, password);
                if (examinerId != 0)
                {
                    HttpSession session = request.getSession();  
                    session.setAttribute("examinerId",examinerId);
                    RequestDispatcher rd = request.getRequestDispatcher("index.jsp"); 
                    rd.forward(request,response);
                } 
                else
                {
                    PrintWriter out = response.getWriter();
                    out.println("<script>alert('Invalid Credentials');</script>");
                    RequestDispatcher rd = request.getRequestDispatcher("login.jsp"); 
                    rd.include(request,response);
                }
            } 
            catch (SQLException e) 
            {
                System.out.println("Execption in Login - Controller "+e);
            }
        }
        
        else if(operation.equals("logout"))
        {
            HttpSession session = request.getSession(false);  
            session.invalidate();
            
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("1");
        }
        
        else if (operation.equals("register")) 
        {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String examinerName = request.getParameter("examinerName");
            String phoneNo = request.getParameter("phoneNo");

            try 
            {
                String message = new Register().register(username, password, examinerName, phoneNo);
                if(message.equals("Success"))
                {
                    RequestDispatcher rd = request.getRequestDispatcher("index.jsp"); 
                    rd.forward(request,response);
                }
                else
                {
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert(\""+message+"\");");
                    out.println("</script>");
                    RequestDispatcher rd = request.getRequestDispatcher("login.jsp"); 
                    rd.include(request,response);
                }
            } 
            catch (IOException | SQLException | ServletException e) 
            {
                System.out.println("Exception in Register - Controller "+e);
            }
        }
        
        else if (operation.equals("insertTestDetails")) 
        {
            try {
                String json = "";
                String str;
            
                BufferedReader br = request.getReader();
                while ((str = br.readLine()) != null) {
                    json += str;
                }
                
                JSONObject data = new JSONObject(json);

                String testName = data.getString("testName");
                String testLink = data.getString("testLink");
                String testDate = data.getString("testDate");
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                
                if(testDate.equals(""))
                {
                    msg.put("message","Enter a Valid Date");
                }
                else
                {
                    JSONArray questionJSON = data.getJSONArray("questions");

                    String[] questions = new String[questionJSON.length()];
                    String[][] options = new String[questionJSON.length()][];
                    String[] correctOptions = new String[questionJSON.length()];

                    for (int i = 0; i < questionJSON.length(); i++) 
                    {
                        JSONObject quesObject = questionJSON.getJSONObject(i);

                        questions[i] = quesObject.getString("question");

                        correctOptions[i] = quesObject.getString("correctOption");

                        JSONArray optionJSON = quesObject.getJSONArray("options");

                        options[i] = new String[optionJSON.length()];

                        for(int j = 0;j < optionJSON.length();j++)
                        {
                            options[i][j] = optionJSON.getString(j);
                        }
                    }

                    HttpSession session = request.getSession(true);
                    int examinerId = (Integer)session.getAttribute("examinerId");

                    String message = new ExaminerTest().insertTestDetails(testName,examinerId,testDate,testLink,questions,options,correctOptions);

                    msg.put("message",message);
                }
                out.println(msg);
            }
            catch(JSONException | SQLException e)
            {
                System.out.println("Exception in Inserting Test Details - Controller "+e);
            }
        }
        
        else if(operation.equals("startTest"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int testId = Integer.parseInt(request.getParameter("testId"));
            
            try 
            {
                if(new ExaminerTest().startTest(testId, examinerId))
                {
                    response.setContentType("text/plain");
                    PrintWriter out = response.getWriter();
                    out.println("1");
                }
            }
            catch (SQLException e) 
            {
                System.out.println("Exception in Start Test - Controller "+e);
            }
        }
        
        else if(operation.equals("finishTest"))
        {
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int testId = Integer.parseInt(request.getParameter("testId"));
            
            try 
            {
                if(new ExaminerTest().finishTest(testId, examinerId))
                {
                    response.setContentType("text/plain");
                    PrintWriter out = response.getWriter();
                    out.println("1");
                }
            }
            catch (SQLException e) 
            {
                System.out.println("Exception in Finish Test - Controller "+e);
            }
        }
        
        else if (operation.equals("AddNewQuestion")) 
        {
            try {
                String json = "";
                String str;
            
                BufferedReader br = request.getReader();
                while ((str = br.readLine()) != null) {
                    json += str;
                }
                
                HttpSession session = request.getSession(true);
                int examinerId = (Integer)session.getAttribute("examinerId");
                
                JSONObject data = new JSONObject(json);
                JSONArray optionJSON = data.getJSONArray("options");
                
                int testId = Integer.parseInt(data.getString("testId"));
                String question = data.getString("question");
                String[] options = new String[optionJSON.length()];
                String correctOption = data.getString("correctOption");
                         
                for (int i = 0; i < optionJSON.length(); i++) 
                {
                    options[i] = optionJSON.getString(i);
                }
                
                String message = new ExaminerTest().AddNewQuestion(examinerId, testId, question, options, correctOption);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                msg.put("message",message);
                out.println(msg);
            }
            catch(JSONException | SQLException e)
            {
                System.out.println("Exception in Add New Question - Controller "+e);
            }
        }
        
        else if(operation.equals("updateQuestion")) 
        {

            String questionJSON = "";
            String str;
            try 
            {
                BufferedReader br = request.getReader();
                while((str = br.readLine()) != null) 
                {
                    questionJSON += str;
                }
                
                HttpSession session = request.getSession(true);
                int examinerId = (Integer)session.getAttribute("examinerId");
                
                JSONObject data = new JSONObject(questionJSON);
                
                int questionId = Integer.parseInt(data.getString("questionId"));
                String question = data.getString("question");
                int correctOptionId = Integer.parseInt(data.getString("correctOption"));
                
                JSONArray optionJSON = data.getJSONArray("option");
                JSONArray optionIdJSON = data.getJSONArray("optionId");
                
                String option[] = new String[optionJSON.length()];
                int optionId[] = new int[optionJSON.length()];
                
                for( int i = 0; i < optionJSON.length(); i++)
                {
                    option[i] = optionJSON.getString(i);
                    optionId[i] = Integer.parseInt(optionIdJSON.getString(i));
                }
                
                String message = new ExaminerTest().updateQuestion(examinerId, questionId, question, optionId, option, correctOptionId);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                msg.put("message",message);
                out.println(msg);
            }
            catch(SQLException | JSONException e)
            {
                System.out.println("Exception in Update Question - Controller "+e);
            }        
        }
        
        else if(operation.equals("deleteQuestion"))
        {
            String data = "";
            String str;

            BufferedReader br = request.getReader();
            while ((str = br.readLine()) != null) {
                data += str;
            }
            HttpSession session = request.getSession(true);
            int examinerId = (Integer)session.getAttribute("examinerId");
            int questionId = Integer.parseInt(data);
            
            try 
            {
                boolean isDeleted = new ExaminerTest().deleteQuestion(examinerId, questionId);
                
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                if(isDeleted)
                {
                    out.println("1");
                }
                else
                {
                    out.println("0");
                }
            }
            catch (IOException | SQLException e) 
            {
                System.out.println("Exception in Delete Question - Controller "+e);
            }
        }
    }
}