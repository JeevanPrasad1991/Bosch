package bosch.cpm.com.bosch.cavingettersetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 6/11/2018.
 */

public class AssetORPromotionReasonGetter {
    String assetreasonTable,promotionreasonTable;
    ArrayList<String> AREASON_CD=new ArrayList<>();

    public String getAssetreasonTable() {
        return assetreasonTable;
    }

    public void setAssetreasonTable(String assetreasonTable) {
        this.assetreasonTable = assetreasonTable;
    }

    public String getPromotionreasonTable() {
        return promotionreasonTable;
    }

    public void setPromotionreasonTable(String promotionreasonTable) {
        this.promotionreasonTable = promotionreasonTable;
    }

    public ArrayList<String> getAREASON_CD() {
        return AREASON_CD;
    }

    public void setAREASON_CD(String AREASON_CD) {
        this.AREASON_CD.add(AREASON_CD);
    }

    public ArrayList<String> getASSET_REASON() {
        return ASSET_REASON;
    }

    public void setASSET_REASON(String ASSET_REASON) {
        this.ASSET_REASON.add(ASSET_REASON);
    }

    ArrayList<String> ASSET_REASON=new ArrayList<>();

    ArrayList<String> PREASON_CD=new ArrayList<>();

    public ArrayList<String> getPREASON_CD() {
        return PREASON_CD;
    }

    public void setPREASON_CD(String PREASON_CD) {
        this.PREASON_CD.add(PREASON_CD);
    }

    public ArrayList<String> getPROMOTION_REASON() {
        return PROMOTION_REASON;
    }

    public void setPROMOTION_REASON(String PROMOTION_REASON) {
        this.PROMOTION_REASON.add(PROMOTION_REASON);
    }

    ArrayList<String> PROMOTION_REASON=new ArrayList<>();

}
