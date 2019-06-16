package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtil 
{

    public static Connection getConnection()
    {
        Connection connection = null;
        try
        {
            Context context = new InitialContext();
            DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/TestDB");
            connection = ds.getConnection();
        }
        catch (SQLException | NamingException e)
        {
            System.out.println("Exception in get Connection - DBUtil "+e);
        }
        return connection;
    }
    
    public static void closeConnection(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException
    {
        if(rs!= null && !rs.isClosed())
        {
            rs.close();
        }
        if(stmt != null && !stmt.isClosed())
        {
            stmt.close();
        }
        if(conn != null && !conn.isClosed())
        {
            conn.close();
        }
    }
    
    public static void closeConnection(Connection conn, PreparedStatement stmt) throws SQLException
    {
        closeConnection(conn, stmt, null);
    }
}
