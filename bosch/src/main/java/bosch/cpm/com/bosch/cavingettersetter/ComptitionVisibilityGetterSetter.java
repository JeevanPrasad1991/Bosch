package bosch.cpm.com.bosch.cavingettersetter;

/**
 * Created by jeevanp on 6/9/2018.
 */

public class ComptitionVisibilityGetterSetter {
    String branDNM;
    String brand_cd;
    String assetNM;
    String assetCD;
    String visibilityQTY;
    String visibilityREMARK;
    String category_cd,category;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    boolean isExist=true;

    public String getCategory_cd() {
        return category_cd;
    }

    public void setCategory_cd(String category_cd) {
        this.category_cd = category_cd;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBranDNM() {
        return branDNM;
    }

    public void setBranDNM(String branDNM) {
        this.branDNM = branDNM;
    }

    public String getBrand_cd() {
        return brand_cd;
    }

    public void setBrand_cd(String brand_cd) {
        this.brand_cd = brand_cd;
    }

    public String getAssetNM() {
        return assetNM;
    }

    public void setAssetNM(String assetNM) {
        this.assetNM = assetNM;
    }

    public String getAssetCD() {
        return assetCD;
    }

    public void setAssetCD(String assetCD) {
        this.assetCD = assetCD;
    }

    public String getVisibilityQTY() {
        return visibilityQTY;
    }

    public void setVisibilityQTY(String visibilityQTY) {
        this.visibilityQTY = visibilityQTY;
    }

    public String getVisibilityREMARK() {
        return visibilityREMARK;
    }

    public void setVisibilityREMARK(String visibilityREMARK) {
        this.visibilityREMARK = visibilityREMARK;
    }

    public String getVisibilityIMG() {
        return visibilityIMG;
    }

    public void setVisibilityIMG(String visibilityIMG) {
        this.visibilityIMG = visibilityIMG;
    }

    String visibilityIMG="";

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    String key_id;
}
