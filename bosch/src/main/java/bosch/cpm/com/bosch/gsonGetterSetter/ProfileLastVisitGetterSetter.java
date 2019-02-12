package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileLastVisitGetterSetter {
    @SerializedName("Store_Profile_LastVisit_Brand")
    @Expose
    private List<StoreProfileLastVisitBrand> storeProfileLastVisitBrand = null;

    public List<StoreProfileLastVisitBrand> getStoreProfileLastVisitBrand() {
        return storeProfileLastVisitBrand;
    }
    public void setStoreProfileLastVisitBrand(List<StoreProfileLastVisitBrand> storeProfileLastVisitBrand) {
        this.storeProfileLastVisitBrand = storeProfileLastVisitBrand;
    }

}
