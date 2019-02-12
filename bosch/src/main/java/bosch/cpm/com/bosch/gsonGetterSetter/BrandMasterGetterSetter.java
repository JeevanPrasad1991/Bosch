
package bosch.cpm.com.bosch.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandMasterGetterSetter {

    @SerializedName("Brand_Master")
    @Expose
    private List<BrandMaster> brandMaster = null;

    public List<BrandMaster> getBrandMaster() {
        return brandMaster;
    }

    public void setBrandMaster(List<BrandMaster> brandMaster) {
        this.brandMaster = brandMaster;
    }


    @SerializedName("Store_Dimension")
    @Expose
    private List<StoreDimension> storeDimension = null;

    public List<StoreDimension> getStoreDimension() {
        return storeDimension;
    }

    public void setStoreDimension(List<StoreDimension> storeDimension) {
        this.storeDimension = storeDimension;
    }


    @SerializedName("Store_Type_Master")
    @Expose
    private List<StoreTypeMaster> storeTypeMaster = null;

    public List<StoreTypeMaster> getStoreTypeMaster() {
        return storeTypeMaster;
    }

    public void setStoreTypeMaster(List<StoreTypeMaster> storeTypeMaster) {
        this.storeTypeMaster = storeTypeMaster;
    }

    @SerializedName("Promo_Type")
    @Expose
    private List<PromoType> promoType = null;

    public List<PromoType> getPromoType() {
        return promoType;
    }

    public void setPromoType(List<PromoType> promoType) {
        this.promoType = promoType;
    }
}
