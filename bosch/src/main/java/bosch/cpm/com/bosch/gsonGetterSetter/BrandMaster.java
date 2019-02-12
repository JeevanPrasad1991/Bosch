
package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandMaster {
    @SerializedName("Brand_Id")
    @Expose
    private Integer brandId;
    @SerializedName("Brand")
    @Expose
    private String brand;
    @SerializedName("Category_Id")
    @Expose
    private Integer categoryId;
    @SerializedName("Brand_Sequence")
    @Expose
    private Integer brandSequence;

    @SerializedName("Company_Id")
    @Expose
    private Integer companyId;


    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandSequence() {
        return brandSequence;
    }

    public void setBrandSequence(Integer brandSequence) {
        this.brandSequence = brandSequence;
    }

    boolean isSelected=false;
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    String profile_img="";

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getSpin_value() {
        return spin_value;
    }

    public void setSpin_value(String spin_value) {
        this.spin_value = spin_value;
    }

    String spin_value="";
    String profile_dimension="";

    public String getSc_present() {
        return sc_present;
    }

    public void setSc_present(String sc_present) {
        this.sc_present = sc_present;
    }

    public String getTab_availeble() {
        return tab_availeble;
    }

    public void setTab_availeble(String tab_availeble) {
        this.tab_availeble = tab_availeble;
    }

    String sc_present="0";
    String tab_availeble="0";

    public String getKey_Id() {
        return key_Id;
    }

    public void setKey_Id(String key_Id) {
        this.key_Id = key_Id;
    }

    String key_Id;

    public String getProfile_dimension() {
        return profile_dimension;
    }

    public void setProfile_dimension(String profile_dimension) {
        this.profile_dimension = profile_dimension;
    }
}
