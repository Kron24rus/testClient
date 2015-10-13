package server;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerDataBaseHelper {

    private static final String DATABASE_NAME = "users.db";
    public static final String TABLE_NAME_USERS = "users";
    public static final String LOGIN = "login";
    public static final String PASS = "pass";
    public static final String LAST_IP = "last_ip";

    private static final String SQL_CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_NAME_USERS +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LOGIN + " TEXT NOT NULL," +
            PASS + " TEXT NOT NULL," +
            LAST_IP + " CHAR(15))";
    private static final String SQL_DROP_TABLE_USERS = "DROP TABLE IF EXISTS " + TABLE_NAME_USERS;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            Statement statement = connection.createStatement();
//            statement.executeUpdate(SQL_DROP_TABLE_USERS);
  //          statement.executeUpdate(SQL_CREATE_TABLE_USERS);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String in;

            while ((in = reader.readLine()) != null) {
                String s[] = in.split(" ");
                if (s.length < 2) break;
                String sqlIn = "INSERT INTO " + TABLE_NAME_USERS + "(" + LOGIN + "," + PASS + "," + LAST_IP + ")" +
                        "VALUES ('" + s[0] + "','" + md5Apache(s[1]) + "'," + "'222.231.214.21');";
                statement.executeUpdate(sqlIn);
            }
            statement.close();
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public static String md5Apache(String st) {
        String md5Hex = DigestUtils.md5Hex(st);

        return md5Hex;
    }
}
