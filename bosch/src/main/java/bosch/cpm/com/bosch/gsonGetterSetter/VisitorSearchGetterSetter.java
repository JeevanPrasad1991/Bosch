package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeevanp on 2/13/2018.
 */

public class VisitorSearchGetterSetter {
    @SerializedName("Emp_Id")
    @Expose
    private Integer empId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Designation")
    @Expose
    private String designation;

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @SerializedName("Visitor_Detail")
    @Expose
    private List<VisitorSearchGetterSetter> visitorDetail = null;

    public List<VisitorSearchGetterSetter> getVisitorDetail() {
        return visitorDetail;
    }
}
