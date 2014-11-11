/**
 * Created by Dom on 11.11.2014.
 */
public class H2 {

    private double p;


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
