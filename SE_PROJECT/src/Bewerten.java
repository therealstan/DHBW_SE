import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Dom on 11.11.2014.
 */

@ManagedBean(name = "bewerten")
@SessionScoped
public class Bewerten {

    public String start()
    {
        /*
        ToDo:
        Ablauf für die Bewertung implentieren
         */
        return "success";
    }

}
