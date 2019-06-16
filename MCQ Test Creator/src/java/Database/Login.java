package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login
{
    public int validateLogin(String username,String password) throws SQLException
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
            stmt=conn.prepareStatement("select examiner_id from examiner_details where username=? and password=?;");
            stmt.setString(1, username);
            stmt.setString(2, password);	
            rs=stmt.executeQuery();
            if(rs.next()) 
            {
                return rs.getInt(1);
            }
        } 
        catch ( SQLException e ) 
        {	 
            System.out.println("Exception in Validate Login - Database "+ e);
        }
        finally
        {
            DBUtil.closeConnection(conn, stmt, rs);
        }
        return 0;
    }
}
