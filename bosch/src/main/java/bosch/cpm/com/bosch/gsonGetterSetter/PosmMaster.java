package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PosmMaster {
    String posmIMG_one="";
    String posmIMG_two="";
    String posmIMG_three="";
    String oldDeployed_value="";
    String new_deployed="";
    String posmsku_value="";

    public String getCategory_Id() {
        return category_Id;
    }

    public void setCategory_Id(String category_Id) {
        this.category_Id = category_Id;
    }

    String category_Id="";

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    String sku;

    public String getSku_Id() {
        return sku_Id;
    }

    public void setSku_Id(String sku_Id) {
        this.sku_Id = sku_Id;
    }

    String sku_Id;


    public String getPosmIMG_one() {
        return posmIMG_one;
    }

    public void setPosmIMG_one(String posmIMG_one) {
        this.posmIMG_one = posmIMG_one;
    }

    public String getPosmIMG_two() {
        return posmIMG_two;
    }

    public void setPosmIMG_two(String posmIMG_two) {
        this.posmIMG_two = posmIMG_two;
    }

    public String getPosmIMG_three() {
        return posmIMG_three;
    }

    public void setPosmIMG_three(String posmIMG_three) {
        this.posmIMG_three = posmIMG_three;
    }

    public String getOldDeployed_value() {
        return oldDeployed_value;
    }

    public void setOldDeployed_value(String oldDeployed_value) {
        this.oldDeployed_value = oldDeployed_value;
    }

    public String getNew_deployed() {
        return new_deployed;
    }

    public void setNew_deployed(String new_deployed) {
        this.new_deployed = new_deployed;
    }

    public String getPosmsku_value() {
        return posmsku_value;
    }

    public void setPosmsku_value(String posmsku_value) {
        this.posmsku_value = posmsku_value;
    }

    @SerializedName("Posm_Id")
    @Expose
    private Integer posmId;
    @SerializedName("Posm")
    @Expose
    private String posm;

    public Integer getPosmId() {
        return posmId;
    }

    public void setPosmId(Integer posmId) {
        this.posmId = posmId;
    }

    public String getPosm() {
        return posm;
    }

    public void setPosm(String posm) {
        this.posm = posm;
    }
}
