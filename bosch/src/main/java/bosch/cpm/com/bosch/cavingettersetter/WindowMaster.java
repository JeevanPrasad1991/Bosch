
package bosch.cpm.com.bosch.cavingettersetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WindowMaster {

    @SerializedName("Window_Id")
    @Expose
    private Integer windowId;
    @SerializedName("Window")
    @Expose
    private String window;

    public Integer getWindowId() {
        return windowId;
    }

    public void setWindowId(Integer windowId) {
        this.windowId = windowId;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    String brand_Id;

    public String getBrand_Id() {
        return brand_Id;
    }

    public void setBrand_Id(String brand_Id) {
        this.brand_Id = brand_Id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    String brand;

    public boolean isIntiativepresent() {
        return intiativepresent;
    }

    public void setIntiativepresent(boolean intiativepresent) {
        this.intiativepresent = intiativepresent;
    }

    boolean intiativepresent = true;

    public String getInitiativeIMG() {
        return initiativeIMG;
    }

    public void setInitiativeIMG(String initiativeIMG) {
        this.initiativeIMG = initiativeIMG;
    }

    String initiativeIMG = "";
    String checklist_Id;
    String checklist;

    public String getChecklist_Id() {
        return checklist_Id;
    }

    public void setChecklist_Id(String checklist_Id) {
        this.checklist_Id = checklist_Id;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getChecklistRightAns() {
        return checklistRightAns;
    }

    public void setChecklistRightAns(String checklistRightAns) {
        this.checklistRightAns = checklistRightAns;
    }

    String checklistRightAns = "";

    public String getChecklistRightAns_Id() {
        return checklistRightAns_Id;
    }

    public void setChecklistRightAns_Id(String checklistRightAns_Id) {
        this.checklistRightAns_Id = checklistRightAns_Id;
    }

    String checklistRightAns_Id = "";

    public ArrayList<WindowMaster> getAnsList() {
        return ansList;
    }

    public void setAnsList(ArrayList<WindowMaster> ansList) {
        this.ansList = ansList;
    }

    ArrayList<WindowMaster> ansList = new ArrayList<>();

    String ckecklist_anserId;

    public String getCkecklist_anserId() {
        return ckecklist_anserId;
    }

    public void setCkecklist_anserId(String ckecklist_anserId) {
        this.ckecklist_anserId = ckecklist_anserId;
    }

    public String getChecklist_answer() {
        return checklist_answer;
    }

    public void setChecklist_answer(String checklist_answer) {
        this.checklist_answer = checklist_answer;
    }

    String checklist_answer;

    public String getInitiativeRightAns() {
        return initiativeRightAns;
    }

    public void setInitiativeRightAns(String initiativeRightAns) {
        this.initiativeRightAns = initiativeRightAns;
    }

    String initiativeRightAns="";

    public String getInitiativeRightAns_Id() {
        return initiativeRightAns_Id;
    }

    public void setInitiativeRightAns_Id(String initiativeRightAns_Id) {
        this.initiativeRightAns_Id = initiativeRightAns_Id;
    }

    String initiativeRightAns_Id="";

    public String getKey_Id() {
        return key_Id;
    }

    public void setKey_Id(String key_Id) {
        this.key_Id = key_Id;
    }

    String key_Id;

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    String temp;
}
