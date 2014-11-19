import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dom on 18.11.2014.
 */

@ManagedBean(name = "accessor")
@SessionScoped
public class AccessorBean {

    /*
    ToDo:
    Kurszuweisung
     */
    long courseID = 1;
    String name1;
    String name2;
    String name3;
    String rate1;
    String rate2;
    String rate3;
    String impact1;
    String impact2;
    String impact3;
    long templateID;
    H2 h2;
    S2G s2g;
    R2S r2s;
    List<Long> studentID;
    UIOutput gradeOutputText;
    private boolean h2Boolean;
    private String selectedCourse;
    private String selectedStudent;
    private String grade;

    public AccessorBean() {

    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean getH2Boolean() {
        return h2Boolean;
    }

    public void setH2Boolean(boolean h2Boolean) {
        this.h2Boolean = h2Boolean;
    }

    public String getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(String selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public List<String> getCourseNameList(User user) {
        List<String> list = new ArrayList<String>();

        PreparedStatement ps;
        Connection con;
        DatabaseCon dbCon = user.getDbCon();
        DataSource ds = dbCon.getDs();

        if (ds != null) {
            try {
                con = ds.getConnection();
                if (con != null) {
                    String sql = "SELECT name FROM course WHERE accessorID = (?)";
                    ps = con.prepareStatement(sql);
                    ps.setLong(1, user.getUserID());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        list.add(rs.getString(1));
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Kann mich nicht verbinden");
                sqle.printStackTrace();
            }
        }
        return list;
    }

    public String getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(String selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public List<String> getStudentList(User user) {
        /*
        ToDO
         */
        List<String> list = new ArrayList<String>();
        studentID = new ArrayList<Long>();

        PreparedStatement ps;
        Connection con;
        DatabaseCon dbCon = user.getDbCon();
        DataSource ds = dbCon.getDs();

        if (ds != null) {
            try {
                con = ds.getConnection();
                if (con != null) {
                    String sql = "SELECT studentID FROM courseStudent WHERE courseID = (?)";
                    ps = con.prepareStatement(sql);
                    ps.setLong(1, courseID);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        long userID = rs.getLong(1);
                        studentID.add(userID);
                        list.add(dbCon.getFirstName(userID) + " " + dbCon.getLastName(userID));
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Kann mich nicht verbinden");
                sqle.printStackTrace();
            }
        }
        return list;
    }

    public String getName2() {
        return name2;
    }

    public String getName1() {
        return name1;
    }

    public String getName3() {
        return name3;
    }

    public String getImpact3() {
        return impact3;
    }

    public void setImpact3(String impact3) {
        this.impact3 = impact3;
    }

    public String getRate1() {
        return rate1;
    }

    public void setRate1(String rate1) {
        this.rate1 = rate1;
    }

    public String getRate2() {
        return rate2;
    }

    public void setRate2(String rate2) {
        this.rate2 = rate2;
    }

    public String getRate3() {
        return rate3;
    }

    public void setRate3(String rate3) {
        this.rate3 = rate3;
    }

    public String getImpact1() {
        return impact1;
    }

    public void setImpact1(String impact1) {
        this.impact1 = impact1;
    }

    public String getImpact2() {
        return impact2;
    }

    public void setImpact2(String impact2) {
        this.impact2 = impact2;
    }

    private long getTemplateID(DatabaseCon dbCon, long courseID) {
        PreparedStatement ps;
        Connection con;
        DataSource ds = dbCon.getDs();

        if (ds != null) {
            try {
                con = ds.getConnection();
                if (con != null) {
                    String sql = "SELECT templateID FROM course WHERE courseID = (?)";
                    ps = con.prepareStatement(sql);
                    ps.setLong(1, courseID);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        templateID = rs.getInt(1);
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Kann mich nicht verbinden");
                sqle.printStackTrace();
            }
        }
        return templateID;
    }

    private void getObjects(DatabaseCon dbCon, long templateID) {
        PreparedStatement ps;
        Connection con;
        DataSource ds = dbCon.getDs();

        if (ds != null) {
            try {
                con = ds.getConnection();
                if (con != null) {
                    String sql = "SELECT h2_ID,r2s_ID,s2g_ID FROM template WHERE id = (?)";
                    ps = con.prepareStatement(sql);
                    ps.setLong(1, templateID);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        h2 = (H2) dbCon.deSerializeObject(rs.getInt(1));
                        r2s = (R2S) dbCon.deSerializeObject(rs.getInt(2));
                        s2g = (S2G) dbCon.deSerializeObject(rs.getInt(3));
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Kann mich nicht verbinden");
                sqle.printStackTrace();
            }
        }
    }

    public void onPageLoaded(User user) {
        DatabaseCon dbCon = user.getDbCon();

        getTemplateID(dbCon, courseID);

        getObjects(dbCon, templateID);
        List<String> names = r2s.getName();
        List<Double> impacts = r2s.getImpacts();

        name1 = names.get(0);
        name2 = names.get(1);
        name3 = names.get(2);

        impact1 = impacts.get(0).toString();
        impact2 = impacts.get(1).toString();
        impact3 = impacts.get(2).toString();
    }

    private long getStudentID(DatabaseCon dbCon) {
        for (Long aLong : studentID) {
            if (selectedStudent.contains(dbCon.getFirstName(aLong)) && selectedStudent.contains(dbCon.getLastName(aLong))) {
                return aLong;
            }
        }
        return -1;
    }

    public UIOutput getGradeOutputText() {
        return gradeOutputText;
    }

    public void setGradeOutputText(UIOutput gradeOutputText) {
        this.gradeOutputText = gradeOutputText;
    }

    public void setGrade() {

        r2s.addRate(Double.parseDouble(rate1), 0);
        r2s.addRate(Double.parseDouble(rate2), 1);
        r2s.addRate(Double.parseDouble(rate3), 2);

        double score = 0;
        try {
            score = r2s.getScore(h2Boolean, h2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        grade = String.valueOf(s2g.getGrade(score));
        gradeOutputText.setValue(grade);
    }

    public String submitGrade(User user) {
        if (grade != null) {
            DatabaseCon dbCon = user.getDbCon();
            dbCon.setGrade(courseID, getStudentID(dbCon), Double.parseDouble(grade));
            return "success";
        } else
            return "error";
    }
}
