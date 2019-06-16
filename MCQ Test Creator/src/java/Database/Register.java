package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register
{
    public String register(String username,String password,String examinerName,String phoneNo) throws SQLException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        try 
        {
            if(conn == null || conn.isClosed())
            {
                conn = DBUtil.getConnection();
            }
            stmt=conn.prepareStatement("insert into examiner_details values(default,?,?,?,?);");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, examinerName);
            stmt.setString(4, phoneNo);
            int status=stmt.executeUpdate();
            
            if(status!=0)
            {
                return "Success";
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Register Record - Database "+ e);
            String exception = e.getMessage();
            
            if(exception.contains("examiner_details_username_key"))
            {
                return "Username Already Exists";
            }
            else if(exception.contains("examiner_details_phone_no_key"))
            {
                return "Phone Number Already Exists";
            }
            else if(exception.contains("examiner_details_password_check"))
            {
                return "Password length must be between 8 to 15 Characters";
            }
            else if(exception.contains("examiner_details_username_check"))
            {
                return "Username must be less than 15 Characters";
            }
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt);
        }
        return "Failure";
    }
}