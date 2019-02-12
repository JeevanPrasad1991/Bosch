package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyMaster {
    @SerializedName("Company_Id")
    @Expose
    private Integer companyId;
    @SerializedName("Company")
    @Expose
    private String company;

    @SerializedName("Is_Competitor")
    @Expose
    private Boolean isCompetitor;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getIsCompetitor() {
        return isCompetitor;
    }

    public void setIsCompetitor(Boolean isCompetitor) {
        this.isCompetitor = isCompetitor;
    }
}
