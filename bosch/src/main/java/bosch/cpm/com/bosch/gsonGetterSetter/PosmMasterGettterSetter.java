package bosch.cpm.com.bosch.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PosmMasterGettterSetter {
    @SerializedName("Posm_Master")
    @Expose
    private List<PosmMaster> posmMaster = null;

    public List<PosmMaster> getPosmMaster() {
        return posmMaster;
    }


    @SerializedName("Company_Master")
    @Expose
    private List<CompanyMaster> companyMaster = null;

    public List<CompanyMaster> getCompanyMaster() {
        return companyMaster;
    }


    @SerializedName("Mapping_Posm")
    @Expose
    private List<MappingPosm> mappingPosm = null;

    public List<MappingPosm> getMappingPosm() {
        return mappingPosm;
    }

    public void setMappingPosm(List<MappingPosm> mappingPosm) {
        this.mappingPosm = mappingPosm;
    }
}
