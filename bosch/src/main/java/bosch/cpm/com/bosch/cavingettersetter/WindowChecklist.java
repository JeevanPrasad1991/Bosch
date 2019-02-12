
package bosch.cpm.com.bosch.cavingettersetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WindowChecklist {


    @SerializedName("Checklist_Id")
    @Expose
    private Integer checklistId;
    @SerializedName("Checklist")
    @Expose
    private String checklist;

    public Integer getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Integer checklistId) {
        this.checklistId = checklistId;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

}
