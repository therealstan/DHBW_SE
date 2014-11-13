import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dom on 11.11.2014.
 */
public class R2S implements Serializable {

    private List<Double> impacts;
    private List<Double> rate;

    public static long getID(DatabaseCon dbCon, long templateID){
        long id = -1;

        DataSource ds = dbCon.getDs();

        PreparedStatement ps;
        Connection con;
        ResultSet rs;
        try {
            con = ds.getConnection();
            if (con != null) {
                String sql = "select r2s_ID from template where id = (?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, templateID);
                rs = ps.executeQuery();
                rs.next();

                id = rs.getInt("r2s_ID");
            }
        } catch (SQLException sqle) {
            System.out.println("Kann mich nicht verbinden");
            sqle.printStackTrace();
        }
        return id;
    }

    public R2S()
    {
        this.impacts = new ArrayList<Double>();
        this.rate = new ArrayList<Double>();
    }

    public void addImpact(double impact){
        if(impact >= 0 && impact <= 1)
            this.impacts.add(impact);
    }

    public List<Double> getImpacts() {
        return impacts;
    }

    public void addRate(double rate, int index){
        if(impacts.get(index)!= Double.NaN) {
            this.rate.add(rate);
        }
        else {
            throw new IllegalArgumentException("This rate has no impact");
        }
    }

    public double getScore(boolean b, H2 h2) throws Exception {
        double score = 0;
        if(!rate.isEmpty()){
            score = h2.boolToScore(b);

            int i = 0;
            for (Double d : rate) {
                if(impacts.get(i) != Double.NaN)
                {
                    score = (1-Math.pow((impacts.get(i)*(1-score)),d))/(1-impacts.get(i)*(1-score))*score;
                }
                i++;
            }
        }
        return score;
    }


}
