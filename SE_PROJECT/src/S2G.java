import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dom on 11.11.2014.
 */
public class S2G implements Serializable{

    private int studID;
    private int kursID;

    private double curvature;
    private double polarity;
    private double gMin;
    private double gMax;

    public static long getID(DatabaseCon dbCon, long templateID){
        long id = -1;

        DataSource ds = dbCon.getDs();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "select s2g_ID from template where id = (?)";
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


    public S2G(double curvature, double polarity, double gMin, double gMax)
    {
        this.curvature = curvature;
        this.polarity = polarity;
        this.gMin = gMin;
        this.gMax = gMax;
    }

    public void assignStudent(int studID, int kursID) {
        this.studID = studID;
        this.kursID = kursID;
    }

    public double getGrade(double score)
    {
        double grade = 0;

        if(studID != 0 && kursID != 0) {
            //DHBW
            grade = Math.min(5, gMin + (gMax + gMin) * (1 - score));
        }
        return grade;
    }
}