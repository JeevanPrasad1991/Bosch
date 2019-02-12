
package bosch.cpm.com.bosch.cavingettersetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WindowMasterGetterSetter {

    @SerializedName("Window_Master")
    @Expose
    private List<WindowMaster> windowMaster = null;

    public List<WindowMaster> getWindowMaster() {
        return windowMaster;
    }

    public void setWindowMaster(List<WindowMaster> windowMaster) {
        this.windowMaster = windowMaster;
    }


}
