import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Dom on 18.11.2014.
 */

@ManagedBean(name = "accessor")
@SessionScoped
public class AccessorBean {

    private boolean h2Boolean;

    public boolean getH2Boolean() {
        return h2Boolean;
    }

    public void setH2Boolean(boolean h2Boolean) {
        this.h2Boolean = h2Boolean;
    }


}
