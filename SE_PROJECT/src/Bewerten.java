import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Dom on 11.11.2014.
 */

@ManagedBean(name = "bewerten")
@SessionScoped
public class Bewerten {

    private DatabaseCon dbCon;

    public String start(DatabaseCon dbCon)
    {
        this.dbCon = dbCon;
        /*
        ToDo:
        Ablauf für die Bewertung implementieren
         */

        H2 h2 = new H2();
        h2.setP(0.9);

        S2G s2g = new S2G(1.0,0,1.0,7.0);
        long s2gID = dbCon.serializeObject(s2g);

        R2S r2s = new R2S();
        r2s.addImpact(0.5);
        r2s.addImpact(0.5);
        r2s.addImpact(0.5);
        r2s.addRate(2.0,0);
        r2s.addRate(1.38,1);
        r2s.addRate(1.0,2);

        long templateID = dbCon.addTemplate("test", h2, r2s, s2g);

        try {
            if(templateID == -1)
                throw new Exception("id out of range");

            R2S test = (R2S) dbCon.deSerializeObject(R2S.getID(dbCon, templateID));
            double d = test.getScore(true, (H2) dbCon.deSerializeObject(H2.getID(dbCon,templateID)));
            System.out.println(d);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return "success";
    }

}
