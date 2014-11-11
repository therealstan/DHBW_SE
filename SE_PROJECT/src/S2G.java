/**
 * Created by Dom on 11.11.2014.
 */
public class S2G {

    private int studID;
    private int kursID;

    private double curvature;
    private double polarity;
    private double gMin;
    private double gMax;


    public S2G(int studID, int kursID)
    {
        this.studID = studID;
        this.kursID = kursID;
    }

    public void setFormel(double curvature, double polarity, double gMin, double gMax) {
        /*
        ToDo:
        Erstellung der Formel
         */
    }

    public double getGrade(double score)
    {
        double grade = 0;

        //DHBW
        grade = Math.min(5, gMin + (gMax + gMin) * (1-score));
        return grade;
    }
}
