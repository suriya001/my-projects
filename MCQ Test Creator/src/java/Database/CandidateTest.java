package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CandidateTest 
{

    public String insertCandidateDetails(String testLink, String candidateName, String regno, String dept, String dob) throws SQLException 
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date=LocalDate.parse(dob,formatter);
            
            stmt=conn.prepareStatement("insert into test_candidate_details(test_id, candidate_name, register_no, department, dob) select test_id, ?, ?, ?, ? from test_details where link = ?;");
            stmt.setString(1, candidateName);
            stmt.setString(2, regno);
            stmt.setString(3, dept);
            stmt.setObject(4, date);
            stmt.setString(5, testLink);
            int status=stmt.executeUpdate();

            if(status!=0)
            {
                return "Success";
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Insert Candidate Details - Database "+ e);
            String exception = e.getMessage();
            
            if(exception.contains("test_candidate_details_test_id_register_no_key"))
            {
                return "You have already taken the test";
            }
            else if(exception.contains("test_candidate_details_register_no_check"))
            {
                return "Register Number must be of 5-10 Characters";
            }
            else if(exception.contains("test_candidate_details_candidate_name_check"))
            {
                return "Candidate Name is Empty";
            }
            else if(exception.contains("test_candidate_details_department_check"))
            {
                return "Department is empty";
            }
            else if(exception.contains("test_candidate_details_dob_check"))
            {
                return "Enter a valid date before 01-01-2001";
            }
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt);
        }
        return "Test Link is Invlaid";
    }
    
    public JSONArray getQuestionDetails(String testLink) throws SQLException, JSONException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        JSONArray questionArray=null;
        ResultSet rs = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            stmt=conn.prepareStatement("select question_id, question from test_questions where test_id = (select test_id from test_details where link = ? );");
            stmt.setString(1, testLink);
            rs=stmt.executeQuery();
            
            questionArray=new JSONArray();
            
            while(rs.next())
            {
                JSONObject obj=new JSONObject();
                
                JSONObject question=new JSONObject();
                question.put("questionId", rs.getInt(1));
                question.put("question", rs.getString(2));
                obj.put("question",question);
                
                stmt=conn.prepareStatement("select option_id, option from options where question_id = ? order by option_id;");
                stmt.setInt(1, rs.getInt(1));
                ResultSet rsOption=stmt.executeQuery();
                
                JSONArray optionArray=new JSONArray();
            
                while(rsOption.next())
                {
                    JSONObject option=new JSONObject();
                    option.put("optionId",rsOption.getInt(1));
                    option.put("option",rsOption.getString(2));
                    optionArray.put(option);
                }
                obj.put("option",optionArray);
                questionArray.put(obj);
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Get Question Details - Database "+ e);
        } 
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return questionArray;
    }
    
    public String getTestStatus(String testLink) throws SQLException 
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            
            stmt = conn.prepareStatement("select status from test_details where link = ?;");
            stmt.setString(1, testLink);
            rs = stmt.executeQuery();

            if(rs.next())
            {
                return rs.getString(1);
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Get Status - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return "Test Link is Invlaid";
    }

    public int insertCandidateAnswer(String testLink, String registerNo, int[] question, int[] correctOption) throws SQLException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            for(int i = 0; i < question.length; i++)
            {
                stmt = conn.prepareStatement("insert into candidate_answer select t.test_id, c.candidate_id, ?, ? from test_details t join test_candidate_details c on t.test_id = (select test_id from test_details where link = ? ) where register_no = ? ;");
                stmt.setInt(1, question[i]);
                stmt.setInt(2, correctOption[i]);
                stmt.setString(3, testLink);
                stmt.setString(4, registerNo);
                stmt.executeUpdate();
            }
            
            stmt = conn.prepareStatement("with cte_test_id as(select test_id from test_details where link = ? ), cte_candidate_id as(select candidate_id from test_candidate_details where register_no = ? and test_id = (select test_id from cte_test_id)) update test_candidate_details set score = (select count(option_id) from correct_option o join candidate_answer a on o.correct_option_id = a.option_id where test_id = (select test_id from cte_test_id) and candidate_id = (select candidate_id from cte_candidate_id)), status = 'completed' where candidate_id = (select candidate_id from cte_candidate_id);");
            stmt.setString(1, testLink);
            stmt.setString(2, registerNo);
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("select score from test_candidate_details where register_no = ? and test_id = (select test_id from test_details where link = ?);");
            stmt.setString(1, registerNo);
            stmt.setString(2, testLink);
            rs = stmt.executeQuery();
            
            rs.next();
            return rs.getInt(1);
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Insert Candidate Answer - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return 0;
    }
}
