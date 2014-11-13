import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.Statement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dom on 31.10.2014.
 */
public class DatabaseCon {

    public static enum userRole {
        ADMIN , TUTOR, STUDENT, ACCESSOR, DEFAULT
    }

    public DataSource getDs() {
        return ds;
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

    public long addTemplate(String name, H2 h2, R2S r2s, S2G s2g){
        int id = -1;

        if (h2 != null && r2s != null && s2g != null) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO template(name, h2_ID, s2g_ID, r2s_ID) VALUES(?,?,?,?)";
                        ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, name);
                        ps.setLong(2, serializeObject(h2));
                        ps.setLong(3, serializeObject(s2g));
                        ps.setLong(4, serializeObject(r2s));
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            id = rs.getInt(1);
                        }
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
        return id;
    }

    public long serializeObject(Object object){
        int id = -1;

        if (object != null) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO data(name, object) VALUES(?,?)";
                        ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, object.getClass().getName());
                        ps.setObject(2, object);
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            id = rs.getInt(1);
                        }
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
        return id;
    }

    public Object deSerializeObject(long id){
        Object object = new Object();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "select object from data where id = (?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1,id);
                rs = ps.executeQuery();
                rs.next();
                // object = rs.getObject(1);

                byte[] buf = rs.getBytes(1);
                ObjectInputStream objectIn = null;
                if (buf != null)
                    objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

                object = objectIn.readObject();
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = object.getClass().getName();
        return object;
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
                        setUserRole(getUserID(name), userRole.DEFAULT);
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

    public void changeUserRole(int userID, userRole role){
        if (userID != 0) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "DELETE from userUserRole where userID = '" + userID + "'";
                        ps = con.prepareStatement(sql);
                        ps.executeUpdate();
                        sql = "INSERT INTO userUserRole(userID,roleID) VALUES (?,?)";
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

    public userRole getUserRole(int userID){
        userRole role = null;

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "select roleID from userUserRole where userID = '"
                        + userID + "'";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                rs.next();
                int roleID = rs.getInt("roleID");
                sql = "select roleName from userRole where roleID = '"+roleID+"'";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                rs.next();
                String s_roleID = rs.getString("roleName");
                for (userRole r : userRole.values() ) {
                    if(r.toString().equalsIgnoreCase(s_roleID))
                    {
                        role = r;
                    }
                }
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return role;
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
