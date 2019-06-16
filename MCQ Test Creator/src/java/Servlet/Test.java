package Servlet;

import Database.CandidateTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Test extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String operation = request.getParameter("operation");
        
        if (operation.equals("insertCandidatetDetails")) 
        {
            try 
            {
                String json = "";
                String str;
            
                BufferedReader br = request.getReader();
                while ((str = br.readLine()) != null) 
                {
                    json += str;
                }
                
                JSONObject data = new JSONObject(json);
                
                String testLink = data.getString("testLink");
                String candidateName = data.getString("candidateName");
                String regno = data.getString("regno");
                String dept = data.getString("dept");
                String dob = data.getString("dob");
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                
                String testStatus = new CandidateTest().getTestStatus(testLink);
                System.out.println(testStatus);
                if(dob.equals(""))
                {
                    msg.put("message", "Enter a valid date");
                }
                else
                {
                    if(testStatus.equals("pending"))
                    {
                        msg.put("message","Test is not Started Yet");
                    }
                    else if(testStatus.equals("completed"))
                    {
                        msg.put("message","Test is Already Completed");
                    }
                    else
                    {
                        String message = new CandidateTest().insertCandidateDetails(testLink, candidateName, regno, dept, dob);
                        System.out.println(message);
                        msg.put("message",message);

                        if(message.equals("Success"))
                        {
                            JSONArray questionArray = new CandidateTest().getQuestionDetails(testLink);
                            msg.put("questionArray",questionArray);
                        }
                    }
                }
                out.println(msg);
            }
            catch(JSONException | SQLException e)
            {
                System.out.println("Exception in Insert Candidate Details - Test "+e);
            }
        }
        
        if(operation.equals("insertCandidateAnswer"))
        {
            try 
            {
                String json = "";
                String str;
            
                BufferedReader br = request.getReader();
                while ((str = br.readLine()) != null) 
                {
                    json += str;
                }
                
                JSONObject data = new JSONObject(json);
                
                String testLink = data.getString("testLink");
                String registerNo = data.getString("registerNo");
                
                JSONArray questionJSON = data.getJSONArray("question");
                JSONArray correctOptionJSON = data.getJSONArray("correctOption");

                int len = questionJSON.length();
                int question[] = new int[len];
                int correctOption[] = new int[len];
                
                for( int i = 0; i < len; i++)
                {
                    question[i] = Integer.parseInt(questionJSON.getString(i));
                    correctOption[i] = Integer.parseInt(correctOptionJSON.getString(i));
                }
                
                int score = new CandidateTest().insertCandidateAnswer(testLink, registerNo, question, correctOption);
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                JSONObject msg = new JSONObject();
                msg.put("score", score);
                out.println(msg);
                
            }
            catch(JSONException | SQLException e)
            {
                System.out.println("Exception in Insert Candidate Answer - Test "+e);
            }    
        }
    }
}
