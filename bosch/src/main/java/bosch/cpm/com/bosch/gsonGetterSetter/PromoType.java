package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PromoType {
    @SerializedName("Promo_Type_Id")
    @Expose
    private Integer promoTypeId;
    @SerializedName("Promo_Type")
    @Expose
    private String promoType;

    @SerializedName("Ptype")
    @Expose
    private String ptype;

    public Integer getPromoTypeId() {
        return promoTypeId;
    }

    public void setPromoTypeId(Integer promoTypeId) {
        this.promoTypeId = promoTypeId;
    }

    public String getPromoType() {
        return promoType;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }
    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }
}
