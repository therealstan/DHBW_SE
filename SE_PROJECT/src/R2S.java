import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dom on 11.11.2014.
 */
public class R2S {

    private H2 h2;

    private H2 getH2()
    {
        /*
        Todo:
        Datenbankabfrage für H2 Objekt
         */
        return new H2();
    }

    private List<Double> impacts;
    private List<Double> rate;

    public R2S()
    {
        this.h2 = getH2();
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

    public double getScore(boolean b) throws Exception {
        double score = 0;
        if(!rate.isEmpty()){
            score = h2.boolToScore(b);
            /*
            ToDo:
            Formel für Refinement
             */
        }
        return score;
    }


}
