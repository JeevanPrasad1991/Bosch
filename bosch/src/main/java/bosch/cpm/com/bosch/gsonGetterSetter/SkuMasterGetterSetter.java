
package bosch.cpm.com.bosch.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkuMasterGetterSetter {

    @SerializedName("Sku_Master")
    @Expose
    private List<SkuMaster> skuMaster = null;

    public List<SkuMaster> getSkuMaster() {
        return skuMaster;
    }

    public void setSkuMaster(List<SkuMaster> skuMaster) {
        this.skuMaster = skuMaster;
    }

}
