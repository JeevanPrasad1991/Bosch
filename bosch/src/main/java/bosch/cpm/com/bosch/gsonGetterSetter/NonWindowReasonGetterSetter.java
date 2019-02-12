package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jeevanp on 7/9/2018.
 */

public class NonWindowReasonGetterSetter {

    @SerializedName("Non_Window_Reason")
    @Expose
    private List<NonWindowReason> nonWindowReason = null;

    public List<NonWindowReason> getNonWindowReason() {
        return nonWindowReason;
    }

    public void setNonWindowReason(List<NonWindowReason> nonWindowReason) {
        this.nonWindowReason = nonWindowReason;
    }

    @SerializedName("Non_Promotion_Reason")
    @Expose
    private List<NonPromotionReason> nonPromotionReason = null;
    public List<NonPromotionReason> getNonPromotionReason() {
        return nonPromotionReason;
    }

    public void setNonPromotionReason(List<NonPromotionReason> nonPromotionReason) {
        this.nonPromotionReason = nonPromotionReason;
    }
}
