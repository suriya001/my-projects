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

public class ExaminerTest 
{
    public String insertTestDetails(String testName,int examinerId, String testDate, String testLink, String[] question, String[][] options, String[] correctOption) throws SQLException
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date=LocalDate.parse(testDate,formatter);
            
            conn.setAutoCommit(false);
            
            stmt=conn.prepareStatement("insert into test_details values(default,?,?,?,?,default) returning test_id;");
            stmt.setString(1, testName);
            stmt.setInt(2, examinerId);
            stmt.setObject(3, date);
            stmt.setString(4, testLink);
            
            rs = stmt.executeQuery();
            rs.next();
            
            int testId = rs.getInt(1);

            for(int i = 0; i < question.length; i++)
            {
                stmt=conn.prepareStatement("insert into test_questions values( ? , default, ? ) returning question_id;");
                stmt.setInt(1, testId);
                stmt.setString(2, question[i]);
                
                rs = stmt.executeQuery();
                rs.next();

                int questionId = rs.getInt(1);

                for (String option : options[i]) 
                {
                    stmt=conn.prepareStatement("insert into options values( ? , default, ? ) ;");
                    stmt.setInt(1, questionId);
                    stmt.setString(2, option);
                    stmt.executeUpdate();
                }

                stmt=conn.prepareStatement(" insert into correct_option(question_id, correct_option_id)  select question_id, o.option_id from options o where option = ? and question_id = ? ;");
                stmt.setString(1, correctOption[i]);
                stmt.setInt(2, questionId);
                stmt.executeUpdate();
            }
            conn.commit();
            return "Success";
        } 
        catch ( SQLException e )
        {	 
            if(conn != null)
            {
                conn.rollback();
            }
            
            System.out.println("Exception in Insert Test Details - Database "+ e);
            String exception = e.getMessage();
            
            if(exception.contains("test_details_examiner_id_test_name_key"))
            {
                return "Test Name Already Exists";
            }
            else if(exception.contains("test_details_test_date_check"))
            {
                return "Enter a valid date";
            }
            else if(exception.contains("test_details_examiner_id_link_key"))
            {
                return "Test Link Already Exists";
            }
            else if(exception.contains("test_questions_test_id_question_key"))
            {
                return "Same Questions are not Allowed";
            }
            else if(exception.contains("test_questions_question_check"))
            {
                return "Empty Question are not Allowed ";
            }
            else if(exception.contains("options_question_id_option_key"))
            {
                return "Same Options are not Allowed";
            }
            else if(exception.contains("options_option_check"))
            {
                return "Empty Options are not Allowed";
            }
        } 
        finally
        {
            if(conn != null)
            {
                conn.setAutoCommit(true);
            }
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return "Failure";
    }

    public JSONArray viewTestDetails(int examinerId) throws SQLException, JSONException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        JSONArray jsArray=null;
        ResultSet rs = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            stmt=conn.prepareStatement("select test_id, test_name, test_date, link, status from test_details where examiner_id = ? order by test_id;");
            stmt.setInt(1, examinerId);
            rs=stmt.executeQuery();
            
            jsArray=new JSONArray();
            
            while(rs.next())
            {
                JSONObject js=new JSONObject();
                js.put("testId", rs.getInt(1));
                js.put("testName", rs.getString(2));
                js.put("testDate", rs.getDate(3));
                js.put("testLink", rs.getString(4));
                js.put("testStatus", rs.getString(5));
                jsArray.put(js);
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in View Test - Database "+ e);
        } 
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return jsArray;
    }
    
    public boolean startTest(int testId, int examinerId) throws SQLException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            
            stmt=conn.prepareStatement("update test_details set status = 'live' where test_id = ? and examiner_id = ? ;");
            stmt.setInt(1, testId);
            stmt.setInt(2, examinerId);
            
            if(stmt.executeUpdate() != 0)
            {
                return true;
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Start Test - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt);
        }
        return false;
    }
    
    public boolean finishTest(int testId, int examinerId) throws SQLException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            stmt=conn.prepareStatement("update test_details set status = 'completed' where test_id = ? and examiner_id = ? ;");
            stmt.setInt(1, testId);
            stmt.setInt(2, examinerId);
            
            if(stmt.executeUpdate() != 0)
            {
                return true;
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Finish Test - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt);
        }
        return false;
    }
    
    public JSONArray viewQuestionDetails(int testId, int examinerId) throws SQLException, JSONException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray questionArray = null;

        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            stmt = conn.prepareStatement("select question_id, question from test_questions where test_id = (select test_id from test_details where test_id = ? and examiner_id = ? ) order by question_id;");
            stmt.setInt(1, testId);
            stmt.setInt(2, examinerId);
            rs = stmt.executeQuery();
            
            questionArray = new JSONArray();
            
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
                
                stmt=conn.prepareStatement("select option from options o join correct_option c on option_id = correct_option_id and c.question_id = ?;");
                stmt.setInt(1, rs.getInt(1));
                ResultSet rsCorrectOption=stmt.executeQuery();
                
                if(rsCorrectOption.next())
                {
                    obj.put("correctOption",rsCorrectOption.getString(1));
                }
                
                questionArray.put(obj);
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in View Question - Database "+ e);
        } 
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return questionArray;
    }
        
    public JSONArray viewTestResult(int testId, int examinerId) throws SQLException, JSONException 
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray candidateDetailsArray = null;

        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            
            stmt=conn.prepareStatement("select candidate_id, candidate_name, register_no, department, dob, score, status from test_candidate_details where test_id = ( select test_id from test_details where test_id = ? and examiner_id = ? ) ;");
            stmt.setInt(1, testId);
            stmt.setInt(2, examinerId);
            
            rs = stmt.executeQuery();
            
            candidateDetailsArray = new JSONArray();
            
            while(rs.next())
            {
                JSONObject data = new JSONObject();
                data.put("candidateId", rs.getInt(1));
                data.put("candidateName", rs.getString(2));
                data.put("registerNo", rs.getString(3));
                data.put("department", rs.getString(4));
                data.put("dob", rs.getDate(5));
                data.put("score", rs.getInt(6));
                data.put("status", rs.getString(7));

                candidateDetailsArray.put(data);
            }
            return candidateDetailsArray;
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in View Test Result - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return candidateDetailsArray;
    }
    
    public JSONArray viewCandidateAnswer(int candidateId, int testId, int examinerId) throws JSONException, SQLException 
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray candidateAnswer = new JSONArray();

        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            
            stmt=conn.prepareStatement("select question, o.option, op.option from test_questions q join candidate_answer a on q.question_id = a.question_id join options o on o.option_id = a.option_id join correct_option c on o.question_id = c.question_id join options op on c.correct_option_id = op.option_id where a.candidate_id = ? and a.test_id = ( select test_id from test_details where test_id = ? and examiner_id = ? );");
            stmt.setInt(1, candidateId);
            stmt.setInt(2, testId);
            stmt.setInt(3, examinerId);
            
            rs = stmt.executeQuery();
            while(rs.next())
            {
                JSONObject data = new JSONObject();
                data.put("question", rs.getString(1));
                data.put("candidateAnswer", rs.getString(2));
                data.put("correctAnswer", rs.getString(3));

                candidateAnswer.put(data);
            }
            return candidateAnswer;
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in View Candidate Answer - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return candidateAnswer;
    }

    public String AddNewQuestion(int examinerId, int testId, String question, String[] options, String correctOption) throws SQLException
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
            
            conn.setAutoCommit(false);
            
            stmt=conn.prepareStatement("select test_id from test_details where test_id = ? and examiner_id = ? ; ");
            stmt.setInt(1, testId);
            stmt.setInt(2, examinerId);
            
            rs = stmt.executeQuery();
            
            if(rs.next())
            {
                stmt=conn.prepareStatement("insert into test_questions(test_id , question) values(?, ?);");
                stmt.setInt(1, testId);
                stmt.setString(2, question);

                stmt.executeUpdate();

                for (String option : options) {
                    stmt=conn.prepareStatement("insert into options(question_id, option) (select question_id, ? from test_questions where question = ? and test_id = ?);");
                    stmt.setString(1, option);
                    stmt.setString(2, question);
                    stmt.setInt(3, testId);
                    int executeUpdate = stmt.executeUpdate();
                }

                stmt=conn.prepareStatement(" insert into correct_option(question_id, correct_option_id)  select question_id, o.option_id from options o where option = ? and question_id = (select question_id from test_questions where question = ? and test_id = ?);");
                stmt.setString(1, correctOption);
                stmt.setString(2, question);
                stmt.setInt(3, testId);
                stmt.executeUpdate();

                conn.commit();
                return "Success";
            }
        } 
        catch ( SQLException e )
        {	 
            if(conn != null)
            {
                conn.rollback();
            }
            
            System.out.println("Exception in Add New Question - Database "+ e);
            String exception = e.getMessage();
            
            if(exception.contains("test_questions_test_id_question_key"))
            {
                return "Same Questions are not Allowed";
            }
            else if(exception.contains("test_questions_question_check"))
            {
                return "Empty Question are not Allowed ";
            }
            else if(exception.contains("options_question_id_option_key"))
            {
                return "Same Options are not Allowed";
            }
            else if(exception.contains("options_option_check"))
            {
                return "Empty Options are not Allowed";
            }
        } 
        finally
        {
            if(conn != null)
            {
                conn.setAutoCommit(true);
            }
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return "Failure";
    }
    
    public String updateQuestion(int examinerId, int questionId, String question, int[] optionId, String[] option, int correctOptionId) throws SQLException
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
            if(conn != null)
            {
                conn.setAutoCommit(false);
            
            
                stmt = conn.prepareStatement("select t.test_id from test_questions q join test_details t on q.test_id = t.test_id where question_id = ? and examiner_id = ? ;");
                stmt.setInt(1, questionId);
                stmt.setInt(2, examinerId);
                rs = stmt.executeQuery();

                while(rs.next())
                {
                    stmt=conn.prepareStatement("update test_questions set question = ? where question_id = ?;");
                    stmt.setString(1, question);
                    stmt.setInt(2, questionId);
                    stmt.execute();

                    for( int i = 0; i < option.length; i++)
                    {
                        stmt=conn.prepareStatement("update options set option = ? where option_id = ?;");
                        stmt.setString(1, option[i]);
                        stmt.setInt(2, optionId[i]);
                        stmt.execute();
                    }

                    stmt=conn.prepareStatement("update correct_option set correct_option_id = ? where question_id = ?;");
                    stmt.setInt(1, correctOptionId);
                    stmt.setInt(2, questionId);
                    stmt.execute();

                    conn.commit();
                    return "Success";
                }
            }
        } 
        catch ( SQLException e ) 
        {	 
            if(conn != null)
            {
                conn.rollback();
            }
            
            System.out.println("Exception in Update Question - Database "+ e);
            String exception = e.getMessage();
            
            if(exception.contains("test_questions_test_id_question_key"))
            {
                return "Same Questions are not Allowed";
            }
            else if(exception.contains("test_questions_question_check"))
            {
                return "Empty Question are not Allowed ";
            }
            else if(exception.contains("options_question_id_option_key"))
            {
                return "Same Options are not Allowed";
            }
            else if(exception.contains("options_option_check"))
            {
                return "Empty Options are not Allowed";
            }
        }
        finally
        {
            if(conn != null)
            {
                conn.setAutoCommit(true);
            }
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return "Failure";
    }
    
    public boolean deleteQuestion(int examinerId, int questionId) throws SQLException
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
            
            stmt = conn.prepareStatement("select t.test_id from test_questions q join test_details t on q.test_id = t.test_id where question_id = ? and examiner_id = ? ;");
            stmt.setInt(1, questionId);
            stmt.setInt(2, examinerId);
            rs = stmt.executeQuery();
            
            while(rs.next())
            {
            
                stmt=conn.prepareStatement("delete from test_questions where question_id = ? ;");
                stmt.setInt(1, questionId);
                int status = stmt.executeUpdate();

                if(status > 0)
                {
                    return true;
                }
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Delete Question - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return false;
    }
}
