import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Dom on 11.11.2014.
 */
public class S2G implements Serializable{

    public boolean isSerialized = false;
    private double curvature;
    private double polarity;
    private double gMin;
    private double gMax;
    private long id;
    private String description;

    public S2G(double curvature, double polarity, double gMin, double gMax) {
        this.curvature = curvature;
        this.polarity = polarity;
        this.gMin = gMin;
        this.gMax = gMax;
    }

    public static long getID(DatabaseCon dbCon, long templateID) {
        long id = -1;

        DataSource ds = dbCon.getDs();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "SELECT s2g_ID FROM template WHERE id = (?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, templateID);
                rs = ps.executeQuery();
                rs.next();

                id = rs.getInt("s2g_ID");
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return id;
    }

    public long getID() {
        return id;
    }

    private long previousID(DatabaseCon dbCon) {
        long id = 0;
        PreparedStatement ps;
        Connection con = null;
        try {
            con = dbCon.getDs().getConnection();
            if (con != null) {
                String sql = "SELECT id FROM data WHERE id=(SELECT max(id) FROM data)";
                ps = con.prepareStatement(sql);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return id;
    }

    public void serialize(DatabaseCon dbCon) throws Exception {
        isSerialized = true;
        this.id = previousID(dbCon) + 1;
        if (id == 0)
            throw new Exception("Error");
        dbCon.serializeObject(this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getGrade(double score) {
        double grade;

        //DHBW
        grade = Math.min(5, gMin + (gMax + gMin) * (1 - score));
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setDecimalSeparator('.');
        DecimalFormat newFormat = new DecimalFormat("#.##", dfs);

        return Double.valueOf(newFormat.format(grade));
    }
}
