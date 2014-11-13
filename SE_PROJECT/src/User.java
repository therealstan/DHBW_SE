import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ManagedBean(name = "user")
@SessionScoped
public class User {

    boolean isLoginPage = (FacesContext.getCurrentInstance().getViewRoot()
            .getViewId().lastIndexOf("login.xhtml") > -1);
    private String name;
    private String password;
    private long userID;
    private double grade;
    private DatabaseCon.userRole role;
    private DatabaseCon dbCon;
    private boolean isLoggedIn = false;

    public User() {
        dbCon = new DatabaseCon();
    }

    public DatabaseCon getDbCon() {
        return dbCon;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String add() {
        if (dbCon.addUser(name, password)) {
            return "success";
        } else
            return "unsuccess";
    }

    public String login() throws InvalidKeySpecException, NoSuchAlgorithmException {
        userID = dbCon.getUserID(name);
        String dbName = dbCon.getUserName(userID);
        String dbPasswordHash = dbCon.getPasswordHash(userID);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (isLoginPage && (name.equals(dbName) && PasswordHash.validatePassword(password,dbPasswordHash))) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("username", name);
            if (session == null) {
                FacesContext
                        .getCurrentInstance()
                        .getApplication()
                        .getNavigationHandler()
                        .handleNavigation(FacesContext.getCurrentInstance(),
                                null, "/login.xhtml");
            } else {
                Object currentUser = session.getAttribute("name");
                if (!isLoginPage && (currentUser == null || currentUser == "")) {
                    FacesContext
                            .getCurrentInstance()
                            .getApplication()
                            .getNavigationHandler()
                            .handleNavigation(
                                    FacesContext.getCurrentInstance(), null,
                                    "/login.xhtml");
                }
            }
            isLoggedIn = true;
            this.role = dbCon.getUserRole(userID);
            return role.toString().toLowerCase();
        } else {
            return "invalid";
        }
    }

    public void logout() {
        FacesContext.getCurrentInstance().getExternalContext()
                .invalidateSession();
        FacesContext
                .getCurrentInstance()
                .getApplication()
                .getNavigationHandler()
                .handleNavigation(FacesContext.getCurrentInstance(), null,
                        "/login.xhtml");
    }

    public DatabaseCon.userRole getRole() {
        return role;
    }

    public double getGrade() {
        return dbCon.getGrade(this.userID);
    }
}