
package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryMaster {

    @SerializedName("Category_Id")
    @Expose
    private Integer categoryId;
    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("Category_Sequence")
    @Expose
    private Integer categorySequence;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategorySequence() {
        return categorySequence;
    }

    public void setCategorySequence(Integer categorySequence) {
        this.categorySequence = categorySequence;
    }


    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getSkuQTY() {
        return skuQTY;
    }

    public void setSkuQTY(String skuQTY) {
        this.skuQTY = skuQTY;
    }

    String skuQTY = "";
    String brand_Id;

    public String getBrand_Id() {
        return brand_Id;
    }

    public void setBrand_Id(String brand_Id) {
        this.brand_Id = brand_Id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    String brand_name;
    String promoType;

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public String getPromoType_Id() {
        return promoType_Id;
    }

    public void setPromoType_Id(String promoType_Id) {
        this.promoType_Id = promoType_Id;
    }

    String promoType_Id;

    public String getCompOfferRemark() {
        return compOfferRemark;
    }

    public void setCompOfferRemark(String compOfferRemark) {
        this.compOfferRemark = compOfferRemark;
    }

    public String getCompOffer_img() {
        return compOffer_img;
    }

    public void setCompOffer_img(String compOffer_img) {
        this.compOffer_img = compOffer_img;
    }

    String compOfferRemark = "";
    String compOffer_img = "";

    public boolean isCompOfferExists() {
        return compOfferExists;
    }

    public void setCompOfferExists(boolean compOfferExists) {
        this.compOfferExists = compOfferExists;
    }

    boolean compOfferExists = true;

    String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
