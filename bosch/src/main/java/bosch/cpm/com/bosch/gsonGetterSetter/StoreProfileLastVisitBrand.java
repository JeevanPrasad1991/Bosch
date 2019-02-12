package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreProfileLastVisitBrand {
    @SerializedName("Store_Id")
    @Expose
    private Integer storeId;
    @SerializedName("Company_Id")
    @Expose
    private Integer companyId;
    @SerializedName("Profile_Image")
    @Expose
    private String profileImage;
    @SerializedName("Profile_Type")
    @Expose
    private String profileType;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }
}
