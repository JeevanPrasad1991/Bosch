package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreDimension {
    @SerializedName("Store_Dimension")
    @Expose
    private String storeDimension;

    public String getStoreDimension() {
        return storeDimension;
    }

    public void setStoreDimension(String storeDimension) {
        this.storeDimension = storeDimension;
    }
}
