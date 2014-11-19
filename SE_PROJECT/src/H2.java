import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dom on 11.11.2014.
 */
public class H2 implements Serializable {

    private double p;
    private String name;

    public static long getID(DatabaseCon dbCon, long templateID) {
        long id = -1;

        DataSource ds = dbCon.getDs();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "SELECT h2_ID FROM template WHERE id = (?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, templateID);
                rs = ps.executeQuery();
                rs.next();

                id = rs.getInt("h2_ID");
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setP(double p) {
        if(p >= 0 && p <= 1)
            this.p = p;
        else
            throw new IllegalArgumentException("p is out of range");
    }

    public double boolToScore(boolean b) throws Exception {
        if(Double.isNaN(p))
            throw new Exception("set p first");
        if(b)
            return p;
        else
            return 1-p;
    }
}
