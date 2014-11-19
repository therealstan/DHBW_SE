import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Dom on 11.11.2014.
 */
@ManagedBean(name = "bewerten")
@SessionScoped
public class Bewerten {

    private DatabaseCon dbCon;

    public String start(DatabaseCon dbCon) {
        this.dbCon = dbCon;
        /*
        ToDo:
        Ablauf für die Bewertung implementieren
         */

//        H2 h2 = new H2();
//        h2.setP(0.9);
//
//
//
//        R2S r2s = new R2S();
//        r2sN.addImpact(0.5);
//        r2s.addImpact(0.5);
//        r2s.addImpact(0.5);
//        r2s.addRate(2.0,0);
//        r2s.addRate(1.38,1);
//        r2s.addRate(1.0,2);
//
//        long templateID = dbCon.addTemplate("test", h2, r2s, s2g);
//
//        try {
//            if(templateID == -1)
//                throw new Exception("id out of range");
//
//            R2S test = (R2S) dbCon.deSerializeObject(R2S.getID(dbCon, templateID));
//            double d = test.getScore(true, (H2) dbCon.deSerializeObject(H2.getID(dbCon,templateID)));
//            S2G s2G = (S2G) dbCon.deSerializeObject(S2G.getID(dbCon, templateID));
//            double note = s2g.getGrade(d);
//
//            dbCon.setGrade(2, dbCon.getUserID("louis"), note);
//            System.out.println(d);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


        return "success";
    }

}
