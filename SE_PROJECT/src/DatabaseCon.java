import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dom on 31.10.2014.
 */
public class DatabaseCon {

    public static enum userRole {
        ADMIN, TUTOR, STUDENT, ACCESSOR
    }

    DataSource ds;

    public DatabaseCon() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource)ctx.lookup("java:comp/env/jdbc/database");
        } catch (NamingException e) {

            e.printStackTrace();
        }
    }

    public int addUser(String name, String password) {
        int i = 0;
        if (name != null) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO user(name, password) VALUES(?,?)";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, name);
                        ps.setString(2, PasswordHash.createHash(password));
                        i = ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return i;

    }

    public int getRoleID(userRole role){
        int roleID = 0;

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "select roleID from userRole where roleName = '"
                        + role.toString() + "'";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                rs.next();
                roleID = rs.getInt("roleID");
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return roleID;
    }

    public void setUserRole(int userID, userRole role){
        if (userID != 0) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO userUserRole(userID,roleID) VALUES (?,?)";
                        ps = con.prepareStatement(sql);
                        int roleID = getRoleID(role);
                        ps.setInt(1,userID);
                        ps.setInt(2,roleID);
                        ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getUserID(String name){
        int userID = 0;

        if (name != null) {
            PreparedStatement ps;
            Connection con;
            ResultSet rs;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select id from user where name = '"
                                + name + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        userID = rs.getInt("id");
                    }
                } catch (SQLException sqle) {
                    System.out.println("Kann mich nicht verbinden");
                    sqle.printStackTrace();
                }
            }
        }
        return userID;
    }

    public String getUserName(int userID){
        String userName = null;

        if (userID != 0) {
            PreparedStatement ps;
            Connection con;
            ResultSet rs;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select name from user where id = '"
                                + userID + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                       userName = rs.getString("name");
                    }
                } catch (SQLException sqle) {
                    System.out.println("Kann mich nicht verbinden");
                    sqle.printStackTrace();
                }
            }
        }
        return userName;
    }

    public String getPasswordHash(int userID){
        String passwordHash = null;

        if (userID != 0) {
            PreparedStatement ps;
            Connection con;
            ResultSet rs;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select password from user where id = '"
                                + userID + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        passwordHash = rs.getString("password");
                    }
                } catch (SQLException sqle) {
                    System.out.println("Kann mich nicht verbinden");
                    sqle.printStackTrace();
                }
            }
        }
        return passwordHash;
    }

}
