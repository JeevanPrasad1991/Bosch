package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingPosm {
    @SerializedName("Category_Id")
    @Expose
    private Integer categoryId;
    @SerializedName("Sku_Id")
    @Expose
    private Integer skuId;
    @SerializedName("Posm_Id")
    @Expose
    private Integer posmId;

    @SerializedName("State_Id")
    @Expose
    private Integer stateId;
    @SerializedName("Store_Type_Id")
    @Expose
    private Integer storeTypeId;

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(Integer storeTypeId) {
        this.storeTypeId = storeTypeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getPosmId() {
        return posmId;
    }

    public void setPosmId(Integer posmId) {
        this.posmId = posmId;
    }
}
