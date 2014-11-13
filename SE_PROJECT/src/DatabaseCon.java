import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dom on 31.10.2014.
 */
public class DatabaseCon {

    DataSource ds;

    public DatabaseCon() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/database");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public DataSource getDs() {
        return ds;
    }

    public void setGrade(long courseID, long studentID, double grade) {
        if (getUserRole(studentID) == userRole.STUDENT) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO grade(courseID, userID, grade) VALUES (?,?,?)";
                        ps = con.prepareStatement(sql);
                        ps.setLong(1, courseID);
                        ps.setLong(2, studentID);
                        ps.setDouble(3, grade);
                        ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public double getGrade(long studentID) {
        double grade = -1;
        if (getUserRole(studentID) == userRole.STUDENT) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "SELECT grade FROM grade WHERE userID = (?)";
                        ps = con.prepareStatement(sql);
                        ps.setLong(1, studentID);
                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        grade = rs.getDouble("grade");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        return grade;
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
                e.printStackTrace();
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

    public long serializeObject(Object object) {
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
                e.printStackTrace();
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

    public Object deSerializeObject(long id) {
        Object object = new Object();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "SELECT object FROM data WHERE id = (?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, id);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public boolean addUser(String name, String password) {
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
                e.printStackTrace();
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
        return i > 0;
    }

    public long getRoleID(userRole role) {
        long roleID = 0;

        PreparedStatement ps;
        Connection con;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "SELECT roleID FROM userRole WHERE roleName = (?)";
                ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, role.toString());
                ps.executeQuery();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    roleID = rs.getInt(1);
                }
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return roleID;
    }

    public void setUserRole(long userID, userRole role) {
        if (userID != 0) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO userUserRole(userID,roleID) VALUES (?,?)";
                        ps = con.prepareStatement(sql);
                        long roleID = getRoleID(role);
                        ps.setLong(1, userID);
                        ps.setLong(2, roleID);
                        ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public void changeUserRole(long userID, userRole role) {
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
                        long roleID = getRoleID(role);
                        ps.setLong(1, userID);
                        ps.setLong(2, roleID);
                        ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public userRole getUserRole(long userID) {
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
                sql = "select roleName from userRole where roleID = '" + roleID + "'";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                rs.next();
                String s_roleID = rs.getString("roleName");
                for (userRole r : userRole.values()) {
                    if (r.toString().equalsIgnoreCase(s_roleID)) {
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

    public int getUserID(String name) {
        int userID = 0;

        if (name != null) {
            PreparedStatement ps;
            Connection con;
            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select id from user where name = '"
                                + name + "'";
                        ps = con.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery();
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

    public String getUserName(long userID) {
        String userName = null;

        if (userID != 0) {
            PreparedStatement ps;
            Connection con;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select name from user where id = '"
                                + userID + "'";
                        ps = con.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery();
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

    public String getPasswordHash(long userID) {
        String passwordHash = null;

        if (userID != 0) {
            PreparedStatement ps;
            Connection con;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select password from user where id = '"
                                + userID + "'";
                        ps = con.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery();
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

    public static enum userRole {
        ADMIN, TUTOR, STUDENT, ACCESSOR, DEFAULT
    }

}
