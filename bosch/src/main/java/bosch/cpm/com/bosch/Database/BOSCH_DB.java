package bosch.cpm.com.bosch.Database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bosch.cpm.com.bosch.cavingettersetter.AssetORPromotionReasonGetter;
import bosch.cpm.com.bosch.cavingettersetter.PromotionInsertDataGetterSetter;
import bosch.cpm.com.bosch.cavingettersetter.WindowMaster;
import bosch.cpm.com.bosch.constant.CommonString;

import bosch.cpm.com.bosch.gettersetter.GeotaggingBeans;

import bosch.cpm.com.bosch.delegates.CoverageBean;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMasterForCompGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CityMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CompanyMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.MappingPosm;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMasterGettterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.ProfileLastVisitGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.PromoType;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreDimension;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreProfileLastVisitBrand;
import bosch.cpm.com.bosch.gsonGetterSetter.JourneyPlan;
import bosch.cpm.com.bosch.gsonGetterSetter.NonWorkingReason;
import bosch.cpm.com.bosch.gsonGetterSetter.NonWorkingReasonGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.SkuMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.SkuMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreTypeMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.VisitorDetailGetterSetter;

/**
 * /**
 * Created by jeevanp on 15-12-2017.
 */

@SuppressLint("LongLogTag")
public class BOSCH_DB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BOSCH_Databa";
    public static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;
    Context context;

    public BOSCH_DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //jeevan
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE_BRAND_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE_DIMENSION);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE_BRAND_DIMENSION);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE_PRODUCT_DIMENSION);
            db.execSQL(CommonString.CREATE_TABLE_insert_POSMHEADER);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_POSM_TABLE);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_POSM_IMAGES_TABLE);
            db.execSQL(CommonString.CREATE_TABLE_insert_COMPETITIONHEADER);
            db.execSQL(CommonString.CREATE_TABLE_STORE_COMPETITION);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_COMPETITION_OFFERS_TABLE);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE);
            db.execSQL(CommonString.CREATE_TABLE_VISITOR_LOGIN);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteSpecificStoreData(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_DIMENSION, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DIMENSION, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_PRODUCT_DIMENSION, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_COMPETITION, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_POSMHEADER, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_POSM_TABLE, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_POSM_IMAGES_TABLE, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_COMPETITION_HEADER, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        db.delete(CommonString.TABLE_INSERT_COMPETITION_OFFERS_TABLE, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

    }


    public void deleteAllTables() {
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
        db.delete(CommonString.TABLE_STORE_PROFILE, null, null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DATA, null, null);
        db.delete(CommonString.TABLE_STORE_PROFILE_DIMENSION, null, null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DIMENSION, null, null);
        db.delete(CommonString.TABLE_STORE_PROFILE_PRODUCT_DIMENSION, null, null);
        db.delete(CommonString.TABLE_STORE_COMPETITION, null, null);
        db.delete(CommonString.TABLE_INSERT_POSMHEADER, null, null);
        db.delete(CommonString.TABLE_INSERT_POSM_TABLE, null, null);
        db.delete(CommonString.TABLE_INSERT_POSM_IMAGES_TABLE, null, null);
        db.delete(CommonString.TABLE_INSERT_COMPETITION_HEADER, null, null);
        db.delete(CommonString.TABLE_INSERT_COMPETITION_OFFERS_TABLE, null, null);
        db.delete(CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE, null, null);

    }


    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE_DIMENSION, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DIMENSION, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE_PRODUCT_DIMENSION, null, null);
                    db.delete(CommonString.TABLE_STORE_COMPETITION, null, null);
                    db.delete(CommonString.TABLE_INSERT_POSMHEADER, null, null);
                    db.delete(CommonString.TABLE_INSERT_POSM_TABLE, null, null);
                    db.delete(CommonString.TABLE_INSERT_POSM_IMAGES_TABLE, null, null);
                    db.delete(CommonString.TABLE_JCP_DATA, null, null);

                    db.delete(CommonString.TABLE_INSERT_COMPETITION_HEADER, null, null);
                    db.delete(CommonString.TABLE_INSERT_COMPETITION_OFFERS_TABLE, null, null);
                    db.delete(CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE, null, null);
                    db.delete(CommonString.TABLE_VISITOR_LOGIN, null, null);
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    public void remove(String user_id) {
        db.execSQL("DELETE FROM COMPETITION_OFFERS_TABLE WHERE KEY_ID = '" + user_id + "'");
    }

    public void removecompFinancialOffer(String brand_Id) {
        db.execSQL("DELETE FROM " + CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE + " WHERE BRAND_Id = '" + brand_Id + "'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int createtable(String sqltext) {
        try {
            db.execSQL(sqltext);
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    public void deleteOLDJCP(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan where Visit_Date < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_JCP_DATA, null, null);
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean insertJCPData(JourneyPlan data) {
        db.delete("Journey_Plan", "Store_Id='" + data.getStoreId() + "' and Visit_Date='" + data.getVisitDate() + "'", null);
        ContentValues values = new ContentValues();
        try {
            if (data.getStoreId() == null) {
                return false;
            }

            values.put("Store_Id", data.getStoreId());
            values.put("EmpId", data.getEmpId());
            values.put("Visit_Date", data.getVisitDate());
            values.put("Distributor", data.getDistributor());
            values.put("Store_Name", data.getStoreName());
            values.put("Address1", data.getAddress1());
            values.put("Address2", data.getAddress2());
            values.put("City", data.getCity());
            values.put("Store_Type", data.getStoreType());
            values.put("Latitude", data.getLatitude());
            values.put("Longitude", data.getLongitude());

            values.put("City_Id", data.getCityId());
            values.put("Distributor_Id", data.getDistributorId());
            values.put("Store_Type_Id", data.getStoreTypeId());
            values.put("Reason_Id", data.getReasonId());
            values.put("Upload_Status", data.getUploadStatus());
            values.put("GeoTag", data.getGeoTag());
            values.put("State_Id", data.getStateId());
            values.put("Store_Profile", data.getStoreProfile());
            values.put("dimension", data.getDimension());

            long id = db.insert("Journey_Plan", null, values);
            if (id == -1) {
                throw new Exception();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Jcp", ex.toString());
            return false;
        }
    }

    public boolean insertNonWorkingData(NonWorkingReasonGetterSetter nonWorkingdata) {
        db.delete("Non_Working_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonWorkingReason> data = nonWorkingdata.getNonWorkingReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Reason_Id", data.get(i).getReasonId());
                values.put("Reason", data.get(i).getReason());
                values.put("Entry_Allow", data.get(i).getEntryAllow());
                values.put("Image_Allow", data.get(i).getImageAllow());
                values.put("GPS_Mandatory", data.get(i).getGPSMandatory());

                long id = db.insert("Non_Working_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertCategoryMasterData(CategoryMasterGetterSetter CategoryMaster) {
        db.delete("Category_Master", null, null);
        ContentValues values = new ContentValues();
        List<bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster> data = CategoryMaster.getCategoryMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Category", data.get(i).getCategory());
                values.put("Category_Id", data.get(i).getCategoryId());
                values.put("Category_Sequence", data.get(i).getCategorySequence());
                long id = db.insert("Category_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertcityMaster(CategoryMasterGetterSetter cityMaster, String visit_date) {
        db.delete("City_Master", null, null);
        ContentValues values = new ContentValues();
        List<bosch.cpm.com.bosch.gsonGetterSetter.CityMaster> data = cityMaster.getCityMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("City_Id", data.get(i).getCityId());
                values.put("City", data.get(i).getCity());
                values.put("Visit_Date", visit_date);
                long id = db.insert("City_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public ArrayList<CityMaster> getcitymasterList(String visit_date) {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<CityMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select * from City_Master where visit_date='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CityMaster sb = new CityMaster();
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!", e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public boolean insertBrandMasterData(BrandMasterGetterSetter BrandMaster) {
        db.delete("Brand_Master", null, null);
        ContentValues values = new ContentValues();
        List<bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster> data = BrandMaster.getBrandMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Brand", data.get(i).getBrand());
                values.put("Brand_Id", data.get(i).getBrandId());
                values.put("Brand_Sequence", data.get(i).getBrandSequence());
                values.put("Category_Id", data.get(i).getCategoryId());
                values.put("Company_Id", data.get(i).getCompanyId());
                long id = db.insert("Brand_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public ArrayList<StoreTypeMaster> getstoreTypeList() {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<StoreTypeMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select * from Store_Type_Master", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    StoreTypeMaster sb = new StoreTypeMaster();
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!", e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public boolean insertSkuMasterData(SkuMasterGetterSetter BrandMaster) {
        db.delete("Sku_Master", null, null);
        ContentValues values = new ContentValues();
        List<SkuMaster> data = BrandMaster.getSkuMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Brand_Id", data.get(i).getBrandId());
                values.put("Sku", data.get(i).getSku());
                values.put("Sku_Id", data.get(i).getSkuId());
                values.put("Sku_Sequence", data.get(i).getSkuSequence());

                long id = db.insert("Sku_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }


    public boolean insertposm(PosmMasterGettterSetter BrandMaster) {
        db.delete("Posm_Master", null, null);
        ContentValues values = new ContentValues();
        List<PosmMaster> data = BrandMaster.getPosmMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Posm_Id", data.get(i).getPosmId());
                values.put("Posm", data.get(i).getPosm());

                long id = db.insert("Posm_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }


    public boolean insetcompanyMaster(PosmMasterGettterSetter BrandMaster) {
        db.delete("Company_Master", null, null);
        ContentValues values = new ContentValues();
        List<CompanyMaster> data = BrandMaster.getCompanyMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Company_Id", data.get(i).getCompanyId());
                values.put("Company", data.get(i).getCompany());
                values.put("Is_Competitor", data.get(i).getIsCompetitor());

                long id = db.insert("Company_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }


    public boolean insertmappingposm(PosmMasterGettterSetter BrandMaster) {
        db.delete("Mapping_Posm", null, null);
        ContentValues values = new ContentValues();
        List<MappingPosm> data = BrandMaster.getMappingPosm();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Category_Id", data.get(i).getCategoryId());
                values.put("Sku_Id", data.get(i).getSkuId());
                values.put("Posm_Id", data.get(i).getPosmId());

                values.put("state_Id", data.get(i).getStateId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());

                long id = db.insert("Mapping_Posm", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertdimension(BrandMasterGetterSetter BrandMaster) {
        db.delete("Store_Dimension", null, null);
        ContentValues values = new ContentValues();
        List<StoreDimension> data = BrandMaster.getStoreDimension();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Store_Dimension", data.get(i).getStoreDimension());
                long id = db.insert("Store_Dimension", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }


    public boolean insertstoretype(BrandMasterGetterSetter BrandMaster) {
        db.delete("Store_Type_Master", null, null);
        ContentValues values = new ContentValues();
        List<StoreTypeMaster> data = BrandMaster.getStoreTypeMaster();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Store_Type", data.get(i).getStoreType());
                long id = db.insert("Store_Type_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertpromoTypeData(BrandMasterGetterSetter BrandMaster) {
        db.delete("Promo_Type", null, null);
        ContentValues values = new ContentValues();
        List<PromoType> data = BrandMaster.getPromoType();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Promo_Type_Id", data.get(i).getPromoTypeId());
                values.put("Promo_Type", data.get(i).getPromoType());
                values.put("Ptype", data.get(i).getPtype());
                long id = db.insert("Promo_Type", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }


    public ArrayList<JourneyPlan> getStoreData(String date) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setEmpId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EmpId"))));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setLatitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude")));
                    sb.setLongitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude")));
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreProfile(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Profile")));
                    sb.setDimension(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("dimension")));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    //jeevan   nmjnmn,
    @SuppressLint("LongLogTag")
    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    //jeevan   nmjnmn,
    @SuppressLint("LongLogTag")
    public long InsertCoverageData(CoverageBean data) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, "STORE_ID" + "='" + data.getStoreId() + "' AND VISIT_DATE='" + data.getVisitDate() + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_ID, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ", ex.toString());
        }
        return l;
    }

    //jeevan   nmjnmn,
    public ArrayList<CoverageBean> getSpecificCoverageData(String visitdate, String store_cd) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' AND " +
                    CommonString.KEY_STORE_ID + "='" + store_cd + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    //jeevan   nmjnmn,
    public long updateJaurneyPlanSpecificStoreStatus(String storeid, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("Upload_Status", status);
            l = db.update("Journey_Plan", values, " Store_Id ='" + storeid + "' AND Visit_Date ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    //jeevan   nmjnmn,
    public ArrayList<JourneyPlan> getSpecificStoreData(String store_cd) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan  " + "where Store_Id ='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setEmpId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EmpId"))));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setLatitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude")));
                    sb.setLongitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude")));
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreProfile(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Profile")));
                    sb.setDimension(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("dimension")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }


    public ArrayList<NonWorkingReason> getNonWorkingDataByFlag(boolean flag) {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<NonWorkingReason> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (flag) {
                        NonWorkingReason sb = new NonWorkingReason();
                        String entry_allow_fortest = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"));
                        if (entry_allow_fortest.equals("1")) {
                            sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                            sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                            String entry_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"));
                            if (entry_allow.equals("1")) {
                                sb.setEntryAllow(true);
                            } else {
                                sb.setEntryAllow(false);
                            }
                            String image_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"));
                            if (image_allow.equals("1")) {
                                sb.setImageAllow(true);
                            } else {
                                sb.setImageAllow(false);
                            }
                            String gps_mendtry = dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"));
                            if (gps_mendtry.equals("1")) {
                                sb.setGPSMandatory(true);
                            } else {
                                sb.setGPSMandatory(false);
                            }

                            list.add(sb);
                        }


                    } else {
                        NonWorkingReason sb = new NonWorkingReason();
                        sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                        sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                        String entry_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"));
                        if (entry_allow.equals("1")) {
                            sb.setEntryAllow(true);
                        } else {
                            sb.setEntryAllow(false);
                        }
                        String image_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"));
                        if (image_allow.equals("1")) {
                            sb.setImageAllow(true);
                        } else {
                            sb.setImageAllow(false);
                        }
                        String gps_mendtry = dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"));
                        if (gps_mendtry.equals("1")) {
                            sb.setGPSMandatory(true);
                        } else {
                            sb.setGPSMandatory(false);
                        }

                        list.add(sb);
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    @SuppressLint("LongLogTag")
    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_COVERAGE_DATA + " WHERE " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public ArrayList<PosmMaster> getposmforupload(String storeId) {
        Log.d("FetchingPromotionuploaddata--------------->Start<------------",
                "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select distinct PT.CATEGORY_Id,PT.POSM_Id,PT.SKU_CD,PT.SKU_QUANTITY,PT.OLD_DEPLOYMENT,PT.NEW_DEPLOYMENT from POSM_TABLE PT WHERE PT.STORE_CD== '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    if (!dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_QUANTITY")).equals("")) {
                        sb.setCategory_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY_Id")));
                        sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("POSM_Id")));
                        sb.setSku_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_CD")));
                        sb.setPosmsku_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_QUANTITY")));
                        sb.setOldDeployed_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("OLD_DEPLOYMENT")));
                        sb.setNew_deployed(dbcursor.getString(dbcursor.getColumnIndexOrThrow("NEW_DEPLOYMENT")));
                        list.add(sb);
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingStoredat---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<JourneyPlan> getSpecificStoreDatawithdate(String visit_date, String store_cd) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from Journey_Plan  " +
                    "where Visit_Date ='" + visit_date + "' AND Store_Id='" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setEmpId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EmpId"))));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setLatitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude")));
                    sb.setLongitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude")));
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag")));
                    sb.setStoreProfile(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Profile")));
                    sb.setDimension(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("dimension")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    @SuppressLint("LongLogTag")
    public ArrayList<CoverageBean> getcoverageDataPrevious(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " +
                    CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "<>'" + visitdate + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }

    public JourneyPlan getSpecificStoreDataPrevious(String date, String store_id) {
        JourneyPlan sb = new JourneyPlan();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT * from Journey_Plan  " +
                    "where Visit_Date <> '" + date + "' AND Store_Id='" + store_id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setEmpId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EmpId"))));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setLatitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude")));
                    sb.setLongitude(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude")));
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag")));
                    sb.setStoreProfile(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Profile")));
                    sb.setDimension(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("dimension")));

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return sb;
        }

        return sb;
    }


    public long updateStatus(String id, String status) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("GeoTag", status);
            l = db.update("Journey_Plan", values, "Store_Id='" + id + "'", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return l;
    }

    public long deletestorewisejourneyplandata(String store_Id, String visit_date) {
        long l = 0;
        try {
            l = db.delete("Journey_Plan", "Store_Id='" + store_Id + "' and Visit_Date='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long InsertSTOREgeotag(String storeid, double lat, double longitude, String path, String status) {
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("STORE_ID", storeid);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put("GEO_TAG", status);
            values.put("STATUS", status);

            l = db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
        }
        return l;
    }

    public ArrayList<GeotaggingBeans> getinsertGeotaggingData(String storeid, String status) {
        ArrayList<GeotaggingBeans> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            if (status == null) {
                dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_GEOTAGGING + "" +
                        " where " + CommonString.KEY_STORE_ID + " ='" + storeid + "'", null);

            } else {
                dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_GEOTAGGING + "" +
                        " where " + CommonString.KEY_STORE_ID + " ='" + storeid + "' and " + CommonString.KEY_STATUS + " = '" + status + "'", null);

            }

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    GeotaggingBeans geoTag = new GeotaggingBeans();
                    geoTag.setStoreid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    geoTag.setLatitude(Double.parseDouble(
                            dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE))));
                    geoTag.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE))));
                    geoTag.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    geoTag.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));

                    list.add(geoTag);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception Brands",
                    e.toString());
            return list;
        }
        return list;

    }

    public long updateInsertedGeoTagStatus(String id, String status) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("GEO_TAG", status);
            values.put("STATUS", status);
            l = db.update(CommonString.TABLE_STORE_GEOTAGGING, values, CommonString.KEY_STORE_ID + "='" + id + "'", null);
        } catch (Exception ex) {
            return 0;
        }
        return l;
    }


    public boolean isPromotionDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT * FROM PROMOTION_DATA WHERE STORE_CD= '" + storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

//get Facing Competitor Data


    //get Competition Company Data

    public ArrayList<BrandMaster> getbrandMasterData() {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from Company_Master", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster df = new BrandMaster();
                    df.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Company_Id")));
                    df.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Company")));
                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public long insertstoreprofiledata(String store_Id, String visit_Date, String spin_Value, String profile_img, ArrayList<BrandMaster> brandList) {
        db.delete(CommonString.TABLE_STORE_PROFILE, "STORE_CD" + "='" + store_Id + "' and VISIT_DATE='" + visit_Date + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DATA, "STORE_CD" + "='" + store_Id + "'", null);
        ContentValues values = new ContentValues();
        ContentValues valuesbrand = new ContentValues();
        long l = 0, l2 = 0;
        try {
            values.put("STORE_CD", store_Id);
            values.put("VISIT_DATE", visit_Date);
            values.put("SPIN_VALUE", spin_Value);
            values.put("PROFILE_IMG", profile_img);
            l = db.insert(CommonString.TABLE_STORE_PROFILE, null, values);
            if (spin_Value.equalsIgnoreCase("Brand")) {

                for (int k = 0; k < brandList.size(); k++) {
                    valuesbrand.put("COMMON_Id", l);
                    valuesbrand.put("STORE_CD", store_Id);
                    valuesbrand.put("BRAND", brandList.get(k).getBrand());
                    valuesbrand.put("BRAND_Id", brandList.get(k).getBrandId());
                    if (brandList.get(k).isSelected()) {
                        valuesbrand.put("iSselecteD", "1");
                    } else {
                        valuesbrand.put("iSselecteD", "0");
                    }
                    l2 = db.insert(CommonString.TABLE_STORE_PROFILE_BRAND_DATA, null, valuesbrand);
                }
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }

        return l2;
    }


    public ArrayList<BrandMaster> getstoreProfileBrandDatawithCommon_Id(String common_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_BRAND where COMMON_Id='" + common_Id + "' and iSselecteD='1" + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster br = new BrandMaster();
                    br.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    br.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public BrandMaster getstoreProfileData(String store_Id, String visit_date) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        BrandMaster list = new BrandMaster();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE where STORE_CD='" + store_Id + "' and VISIT_DATE='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    list.setProfile_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROFILE_IMG")));
                    list.setSpin_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SPIN_VALUE")));
                    list.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------", "-------------------");
        return list;

    }

    public ArrayList<BrandMaster> getstoreProfileBrandData(String store_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_BRAND where STORE_CD='" + store_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster br = new BrandMaster();
                    br.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    br.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<PromotionInsertDataGetterSetter> getpromotionheaderdata(String state_Id, String distributor_Id, String storeType_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<PromotionInsertDataGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("select distinct sm.Sku_Id,sm.Sku from Sku_Master sm inner join Mapping_Promotion mp on mp.Sku_Id=sm.Sku_Id " +
                    "where mp.State_Id ='" + state_Id + "' and mp.Distributor_Id='" + distributor_Id + "' and mp.Store_Type_Id='" + storeType_Id + "'", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PromotionInsertDataGetterSetter sb = new PromotionInsertDataGetterSetter();
                    sb.setSku_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Sku_Id")));
                    sb.setSku(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Sku")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<AssetORPromotionReasonGetter> getPromotionReasonList() {
        ArrayList<AssetORPromotionReasonGetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM Non_Window_Reason ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AssetORPromotionReasonGetter sb = new AssetORPromotionReasonGetter();
                    sb.setPREASON_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WReason_Id")));
                    sb.setPROMOTION_REASON(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WReason")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<CategoryMaster> getCategoryList(String state_Id, String distributor_Id, String storeType_Id) {
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("Select distinct ca.Category_Id, ca.Category from Mapping_Stock m inner join Sku_Master sk on m.Sku_Id = sk.Sku_Id " +
                    "inner join Brand_Master br on sk.Brand_Id = br.Brand_Id " +
                    "inner join Sub_Category_Master sb on br.Sub_Category_Id = sb.Sub_Category_Id " +
                    "inner join Category_Master ca on ca.Category_Id = sb.Category_Id " +
                    "Where m.State_Id ='" + state_Id + "' and m.Distributor_Id ='" + distributor_Id + "' and m.Store_Type_Id ='" + storeType_Id + "' order by ca.Category ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster sb = new CategoryMaster();
                    sb.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    sb.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<WindowMaster> getinitiativeChecklistData(String window_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select distinct wc.Checklist_Id,wc.Checklist from Window_Checklist wc inner join Mapping_Window_Checklist mc " +
                    "on mc.Checklist_Id=wc.Checklist_Id  inner join Window_Master wm on mc.Window_Id=mc.Window_Id " +
                    "Where mc.Window_Id='" + window_Id + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setChecklist_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Checklist_Id")));
                    sb.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Checklist")));
                    sb.setChecklistRightAns_Id("0");
                    sb.setChecklistRightAns("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<WindowMaster> getchecklistAnswerData(String checklist_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select * from Window_Checklist_Answer Where Checklist_Id='" + checklist_Id + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setChecklist_answer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Answer")));
                    sb.setCkecklist_anserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Answer_Id")));
                    sb.setChecklist_Id(checklist_Id);
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<WindowMaster> getinitiativechilddata(String window_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("Select distinct wb.Window,wb.Window_Id from Window_Master wb " +
                    "inner join Mapping_Visibility_Initiatives mi on mi.Window_Id=wb.Window_Id " +
                    " where mi.Window_Id ='" + window_Id + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setWindow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Window")));
                    sb.setWindowId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Window_Id")));
                    sb.setIntiativepresent(true);
                    sb.setInitiativeIMG("");
                    sb.setInitiativeRightAns("0");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<WindowMaster> getvisibilityinitiativeheaderData(String state_id, String dist_id, String storeTyp_cd) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("  Select distinct wb.Window,wb.Window_Id,br.Brand_Id,br.Brand from Window_Master wb " +
                    "inner join Mapping_Visibility_Initiatives mi on mi.Window_Id=wb.Window_Id " +
                    "inner join Brand_Master br on mi.Brand_Id=br.Brand_Id " +
                    "Where mi.State_Id ='" + state_id + "' And mi.Distributor_Id ='" + dist_id +
                    "' And mi.Store_Type_Id ='" + storeTyp_cd + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setWindow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Window")));
                    sb.setWindowId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Window_Id")));
                    sb.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand_Id")));
                    sb.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand")));
                    sb.setTemp("0");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<PosmMaster> getposmHeaderData(String category_Id, String state_Id, String storeType_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("Select distinct sm.Sku,sm.Sku_Id from Sku_Master sm " +
                    "inner join Mapping_Posm mp on mp.Sku_Id=sm.Sku_Id where mp.Category_Id='" + category_Id + "' and mp.state_Id='" + state_Id + "' and mp.Store_Type_Id='" + storeType_Id + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setSku_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Sku_Id")));
                    sb.setSku(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Sku")));
                    sb.setPosmsku_value("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public boolean Isposminseteddata(String storeId, String cat) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            if (cat == null) {
                dbcursor = db.rawQuery("SELECT * FROM POSM_TABLE WHERE STORE_CD= '" + storeId + "'", null);
            } else {
                dbcursor = db.rawQuery("SELECT * FROM POSM_TABLE WHERE STORE_CD= '" + storeId + "' AND CATEGORY_Id='" + cat + "'", null);
            }
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_Id")).equals("") ||
                            dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_Id")) == null) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }

        return filled;
    }

    public long inserposmimages(String store_Id, String cat, PosmMaster posmimages) {
        db.delete(CommonString.TABLE_INSERT_POSM_IMAGES_TABLE, CommonString.KEY_STORE_CD + "='" + store_Id + "' AND CATEGORY_Id='" + cat + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("STORE_CD", store_Id);
            values.put("CATEGORY_Id", cat);
            values.put("POSM_IMG_ONE", posmimages.getPosmIMG_one());
            values.put("POSM_IMG_TWO", posmimages.getPosmIMG_two());
            values.put("POSM_IMG_THREE", posmimages.getPosmIMG_three());
            l = db.insert(CommonString.TABLE_INSERT_POSM_IMAGES_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public PosmMaster getposmimgescategorywise(String storeId, String category_Id) {
        Log.d("FetchingOpening Stock data--------------->Start<------------", "------------------");
        PosmMaster list = new PosmMaster();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery(" SELECT * FROM POSM_IMAGES_TABLE WHERE STORE_CD= '" + storeId + "' AND CATEGORY_Id = '" + category_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    list.setPosmIMG_one(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_ONE")));
                    list.setPosmIMG_two(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_TWO")));
                    list.setPosmIMG_three(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_THREE")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingOPening midday---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<PosmMaster> getposmimagesforupload(String storeId) {
        Log.d("FetchingOpening Stock data--------------->Start<------------", "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery(" SELECT * FROM POSM_IMAGES_TABLE WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster posmimgs = new PosmMaster();
                    posmimgs.setPosmIMG_one(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_ONE")));
                    posmimgs.setPosmIMG_two(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_TWO")));
                    posmimgs.setPosmIMG_three(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG_THREE")));
                    posmimgs.setCategory_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY_Id")));
                    list.add(posmimgs);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingOPening midday---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<PosmMaster> getposmheaderdatafromdatabase(String store_Id, String category_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from POSMHEADER_TABLE where STORE_CD='" + store_Id + "' AND CATEGORY_Id='" + category_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster br = new PosmMaster();
                    br.setSku_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_CD")));
                    br.setSku(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU")));
                    br.setPosmsku_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SKU_QUANTITY")));
                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<PosmMaster> getposmdatafromdatabase(String store_Id, String category_Id, String sku_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from POSM_TABLE where STORE_CD='" + store_Id + "' AND CATEGORY_Id='" + category_Id + "' AND SKU_CD='" + sku_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster br = new PosmMaster();
                    br.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("POSM_Id")));
                    br.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM")));
                    br.setOldDeployed_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("OLD_DEPLOYMENT")));
                    br.setNew_deployed(dbcursor.getString(dbcursor.getColumnIndexOrThrow("NEW_DEPLOYMENT")));
                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public long insertposmdata(String storeid, String visit_date, String cat,
                               HashMap<PosmMaster, List<PosmMaster>> data, List<PosmMaster> save_listDataHeader) {
        db.delete(CommonString.TABLE_INSERT_POSMHEADER, CommonString.KEY_STORE_CD + "='" + storeid + "' AND CATEGORY_Id='" + cat + "'", null);
        db.delete(CommonString.TABLE_INSERT_POSM_TABLE, CommonString.KEY_STORE_CD + "='" + storeid + "' AND CATEGORY_Id='" + cat + "'", null);
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        long l2 = 0;
        try {

            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {
                values.put("STORE_CD", storeid);
                values.put("VISIT_DATE", visit_date);
                values.put("CATEGORY_Id", cat);
                values.put("SKU_CD", save_listDataHeader.get(i).getSku_Id().toString());
                values.put("SKU", save_listDataHeader.get(i).getSku());
                values.put("SKU_QUANTITY", save_listDataHeader.get(i).getPosmsku_value());

                long l = db.insert(CommonString.TABLE_INSERT_POSMHEADER, null, values);

                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {

                    values1.put("Common_Id", (int) l);
                    values1.put("STORE_CD", storeid);
                    values1.put("CATEGORY_Id", cat);
                    values1.put("SKU", save_listDataHeader.get(i).getSku());
                    values1.put("SKU_CD", save_listDataHeader.get(i).getSku_Id().toString());
                    values1.put("SKU_QUANTITY", save_listDataHeader.get(i).getPosmsku_value());
                    values1.put("POSM_Id", data.get(save_listDataHeader.get(i)).get(j).getPosmId());
                    values1.put("POSM", data.get(save_listDataHeader.get(i)).get(j).getPosm());
                    values1.put("OLD_DEPLOYMENT", data.get(save_listDataHeader.get(i)).get(j).getOldDeployed_value());
                    values1.put("NEW_DEPLOYMENT", data.get(save_listDataHeader.get(i)).get(j).getNew_deployed());
                    l2 = db.insert(CommonString.TABLE_INSERT_POSM_TABLE, null, values1);

                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Posm Master Data ", ex.toString());
        }
        return l2;
    }


    public long insertinitiativeVisibilityData(String storeid,
                                               HashMap<WindowMaster, List<WindowMaster>> data, List<WindowMaster> save_listDataHeader) {
        db.delete(CommonString.TABLE_INSERT_VISIINITIATIVEHEADER_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_VISIINITIATIVE_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_VISIINITIATIV_CHECKLIST_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        long l2 = 0;
        long l3 = 0;
        try {

            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {

                values.put("STORE_CD", storeid);
                values.put("BRAND_CD", save_listDataHeader.get(i).getBrand_Id());
                values.put("BRAND", save_listDataHeader.get(i).getBrand());
                values.put("WINDOW", save_listDataHeader.get(i).getWindow());
                values.put("WINDOW_CD", save_listDataHeader.get(i).getWindowId().toString());

                long l = db.insert(CommonString.TABLE_INSERT_VISIINITIATIVEHEADER_DATA, null, values);

                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {

                    values1.put("Common_Id", (int) l);
                    values1.put("STORE_CD", storeid);
                    values1.put("WINDOW_CD", data.get(save_listDataHeader.get(i)).get(j).getWindowId());
                    values1.put("WINDOW", data.get(save_listDataHeader.get(i)).get(j).getWindow());
                    values1.put("INITIATIVE_PRESENT", data.get(save_listDataHeader.get(i)).get(j).isIntiativepresent());
                    values1.put("INITIATIVE_ANS_ID", data.get(save_listDataHeader.get(i)).get(j).getInitiativeRightAns_Id());
                    values1.put("INITIATIVE_IMG", data.get(save_listDataHeader.get(i)).get(j).getInitiativeIMG());
                    l2 = db.insert(CommonString.TABLE_INSERT_VISIINITIATIVE_DATA, null, values1);

                    if (data.get(save_listDataHeader.get(i)).get(j).isIntiativepresent()) {

                        if (data.get(save_listDataHeader.get(i)).get(j).getAnsList().size() > 0) {
                            for (int m = 0; m < data.get(save_listDataHeader.get(i)).get(j).getAnsList().size(); m++) {
                                values2.put("Common_Id", (int) l);
                                values2.put("STORE_CD", storeid);
                                values2.put("WINDOW_CD", data.get(save_listDataHeader.get(i)).get(j).getWindowId());
                                values2.put("CHECKLIST", data.get(save_listDataHeader.get(i)).get(j).getAnsList().get(m).getChecklist());
                                values2.put("CHECKLIST_CD", data.get(save_listDataHeader.get(i)).get(j).getAnsList().get(m).getChecklist_Id());
                                values2.put("CHECKLIST_ANS_CD", data.get(save_listDataHeader.get(i)).get(j).getAnsList().get(m).getChecklistRightAns_Id());
                                values2.put("CHECKLIST_ANS", data.get(save_listDataHeader.get(i)).get(j).getAnsList().get(m).getChecklistRightAns());
                                l3 = db.insert(CommonString.TABLE_INSERT_VISIINITIATIV_CHECKLIST_DATA, null, values2);

                            }
                        }
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Posm Master Data ", ex.toString());
        }
        return l2;
    }

    public ArrayList<WindowMaster> getinitiativechilddatafromdatabase(String storeId, String window_Id) {
        Log.d("FetchingOpening Stock data--------------->Start<------------", "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery(" Select * from initiativeVisibility_data where WINDOW_CD='" + window_Id + "' AND STORE_CD ='" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setWindow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WINDOW")));
                    sb.setWindowId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("WINDOW_CD")));
                    int present_value = dbcursor.getInt(dbcursor.getColumnIndexOrThrow("INITIATIVE_PRESENT"));
                    if (present_value == 0) {
                        sb.setIntiativepresent(false);
                    } else {
                        sb.setIntiativepresent(true);
                    }
                    sb.setInitiativeRightAns(dbcursor.getString(dbcursor.getColumnIndexOrThrow("INITIATIVE_ANS_ID")));

                    sb.setInitiativeIMG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("INITIATIVE_IMG")));
                    sb.setInitiativeRightAns_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("INITIATIVE_ANS_ID")));
                    sb.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Common_Id")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingOPening midday---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<WindowMaster> getchecklistdatafromdatabase(String storeId, String common_Id) {
        Log.d("FetchingOpening Stock data--------------->Start<------------", "------------------");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from initiativeVisibility_Checklist WHERE Common_Id='" + common_Id + "' AND STORE_CD ='" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster sb = new WindowMaster();
                    sb.setChecklist_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CD")));
                    sb.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST")));
                    sb.setChecklistRightAns_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_ANS_CD")));
                    sb.setChecklistRightAns(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_ANS")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingOPening midday---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public long insertsecondstoreprofiledatawithProduct(String store_Id, String visit_Date, String Dimension, String sc_present, String tab_availeble
            , ArrayList<BrandMaster> brandList, ArrayList<CategoryMaster> productlist) {
        db.delete(CommonString.TABLE_STORE_PROFILE_DIMENSION, "STORE_CD" + "='" + store_Id + "' and VISIT_DATE='" + visit_Date + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_BRAND_DIMENSION, "STORE_CD" + "='" + store_Id + "'", null);
        db.delete(CommonString.TABLE_STORE_PROFILE_PRODUCT_DIMENSION, "STORE_CD" + "='" + store_Id + "'", null);
        ContentValues values = new ContentValues();
        ContentValues valuesbrand = new ContentValues();
        ContentValues valuesproduct = new ContentValues();
        long l = 0, l2 = 0, l3 = 0;

        try {
            values.put("STORE_CD", store_Id);
            values.put("VISIT_DATE", visit_Date);
            values.put("STORE_DIMENSION", Dimension);
            values.put("SC_PRESENT", sc_present);
            values.put("TAB_AVAILEBLE", tab_availeble);

            l = db.insert(CommonString.TABLE_STORE_PROFILE_DIMENSION, null, values);


            for (int k2 = 0; k2 < productlist.size(); k2++) {
                valuesproduct.put("COMMON_Id", l);
                valuesproduct.put("STORE_CD", store_Id);
                values.put("STORE_DIMENSION", Dimension);
                valuesproduct.put("PRODUCT", productlist.get(k2).getCategory());
                valuesproduct.put("PRODUCT_Id", productlist.get(k2).getCategoryId());
                if (productlist.get(k2).isSelected()) {
                    valuesproduct.put("iSselecteD", "1");
                } else {
                    valuesproduct.put("iSselecteD", "0");
                }
                l2 = db.insert(CommonString.TABLE_STORE_PROFILE_PRODUCT_DIMENSION, null, valuesproduct);
            }

            for (int k = 0; k < brandList.size(); k++) {
                valuesbrand.put("COMMON_Id", l);
                valuesbrand.put("STORE_CD", store_Id);
                values.put("STORE_DIMENSION", Dimension);
                valuesbrand.put("BRAND", brandList.get(k).getBrand());
                valuesbrand.put("BRAND_Id", brandList.get(k).getBrandId());
                if (brandList.get(k).isSelected()) {
                    valuesbrand.put("iSselecteD", "1");
                } else {
                    valuesbrand.put("iSselecteD", "0");
                }
                l3 = db.insert(CommonString.TABLE_STORE_PROFILE_BRAND_DIMENSION, null, valuesbrand);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }

        return l3;
    }


    public BrandMaster getstoreprofileDimensionData(String store_Id, String visit_date) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        BrandMaster list = new BrandMaster();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_DIMENSION where STORE_CD='" + store_Id + "' and VISIT_DATE='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    list.setProfile_dimension(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_DIMENSION")));
                    list.setSc_present(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SC_PRESENT")));
                    list.setTab_availeble(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TAB_AVAILEBLE")));
                    list.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------", "-------------------");
        return list;

    }

    public ArrayList<BrandMaster> getprofiledimensionBrandList(String common_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_BRAND_DIMENSION where COMMON_Id='" + common_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster br = new BrandMaster();
                    br.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    br.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<BrandMaster> getprofiledimensionBrandListforupload(String common_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_BRAND_DIMENSION where COMMON_Id='" + common_Id + "' and iSselecteD='1" + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster br = new BrandMaster();
                    br.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    br.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<CategoryMaster> getprofiledimensionproductList(String common_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_PRODUCT_DIMENSION where COMMON_Id='" + common_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster br = new CategoryMaster();
                    br.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PRODUCT")));
                    br.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("PRODUCT_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<CategoryMaster> getprofiledimensionproductListforupload(String common_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from TABLE_STORE_PROFILE_PRODUCT_DIMENSION where COMMON_Id='" + common_Id + "' and iSselecteD='1" + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster br = new CategoryMaster();
                    br.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PRODUCT")));
                    br.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("PRODUCT_Id")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("iSselecteD")).equals("1")) {
                        br.setSelected(true);
                    } else {
                        br.setSelected(false);
                    }

                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<CategoryMaster> getcategoryMasterList() {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select * from Category_Master order by Category_Sequence", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster df = new CategoryMaster();
                    df.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    df.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<CategoryMaster> getcategoryMasterforCompetitionList() {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select distinct cm.Category_Id,cm.Category from Category_Master cm inner join Brand_Master br " +
                    "on cm.Category_Id=br.Category_Id where br.Company_Id <>1 order by cm.Category", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster df = new CategoryMaster();
                    df.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    df.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<CategoryMasterForCompGetterSetter> getcategoryMasterforCompetitionFList() {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMasterForCompGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select distinct cm.Category_Id,cm.Category from Category_Master cm inner join Brand_Master br " +
                    "on cm.Category_Id=br.Category_Id where br.Company_Id <>1 order by cm.Category", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMasterForCompGetterSetter df = new CategoryMasterForCompGetterSetter();
                    df.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    df.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<CategoryMaster> getpromoTypeData(String types) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from Promo_Type where Ptype='" + types + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster df = new CategoryMaster();
                    df.setPromoType_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Promo_Type_Id")));
                    df.setPromoType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Promo_Type")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<CategoryMasterForCompGetterSetter> getpromoTypeforfinancialData(String types) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMasterForCompGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from Promo_Type where Ptype='" + types + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMasterForCompGetterSetter df = new CategoryMasterForCompGetterSetter();
                    df.setPromoType_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Promo_Type_Id")));
                    df.setPromoType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Promo_Type")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<CategoryMaster> getcategotyListforposm() {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("Select distinct cm.Category,cm.Category_Id from Category_Master cm " +
                    "inner join Mapping_Posm mp on mp.Category_Id=cm.Category_Id order by cm.Category_Sequence", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster df = new CategoryMaster();
                    df.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    df.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Company!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingCategory data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public long insertboschcompetiitonList(String storeid, String visit_date,
                                           HashMap<CategoryMaster, List<CategoryMaster>> data, List<CategoryMaster> save_listDataHeader) {
        db.delete(CommonString.TABLE_INSERT_COMPETITION_HEADER, CommonString.KEY_STORE_CD + "='" + storeid + "' and VISIT_DATE='" + visit_date + "'", null);
        db.delete(CommonString.TABLE_STORE_COMPETITION, "STORE_CD" + "='" + storeid + "' and VISIT_DATE='" + visit_date + "'", null);
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        long l2 = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {
                values.put("STORE_CD", storeid);
                values.put("VISIT_DATE", visit_date);
                values.put("CATEGORY_Id", save_listDataHeader.get(i).getCategoryId().toString());
                values.put("CATEGORY", save_listDataHeader.get(i).getCategory());

                long l = db.insert(CommonString.TABLE_INSERT_COMPETITION_HEADER, null, values);

                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {

                    values1.put("Common_Id", (int) l);
                    values1.put("STORE_CD", storeid);
                    values1.put("VISIT_DATE", visit_date);
                    values1.put("CATEGORY_Id", save_listDataHeader.get(i).getCategoryId().toString());
                    values1.put("CATEGORY", save_listDataHeader.get(i).getCategory());

                    values1.put("BRAND_Id", data.get(save_listDataHeader.get(i)).get(j).getBrand_Id());
                    values1.put("BRAND", data.get(save_listDataHeader.get(i)).get(j).getBrand_name());
                    values1.put("BRANDOF_SKU_QTY", data.get(save_listDataHeader.get(i)).get(j).getSkuQTY());
                    l2 = db.insert(CommonString.TABLE_STORE_COMPETITION, null, values1);

                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Posm Master Data ", ex.toString());
        }
        return l2;
    }


    public ArrayList<CategoryMaster> getboschcompetitionfromdatabase(String store_Id, String visit_date, String category_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            if (category_Id.equals("")) {
                dbcursor = db.rawQuery("select * from TABLE_STORE_COMPETITION where STORE_CD='" + store_Id + "' AND VISIT_DATE='" + visit_date + "'", null);
            } else {
                dbcursor = db.rawQuery("select * from TABLE_STORE_COMPETITION where STORE_CD='" + store_Id + "' AND VISIT_DATE='" + visit_date + "'AND CATEGORY_Id='" + category_Id + "'", null);

            }
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster br = new CategoryMaster();
                    br.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY")));
                    br.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("CATEGORY_Id")));
                    br.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    br.setBrand_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    br.setSkuQTY(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRANDOF_SKU_QTY")));
                    list.add(br);

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;

    }


    public ArrayList<StoreDimension> getprofileDimensionList() {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<StoreDimension> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select * from Store_Dimension", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    StoreDimension sb = new StoreDimension();
                    sb.setStoreDimension(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Dimension")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<PosmMaster> getposmdatausingSkuId(String sku_Id, String state_Id, String storeType_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("Select distinct pm.Posm_Id ,pm.Posm from Posm_Master pm " +
                    "inner join Mapping_Posm mp on " +
                    "mp.Posm_Id=pm.Posm_Id inner join Sku_Master sm on " +
                    "sm.Sku_Id=mp.Sku_Id  where mp.Sku_Id='" + sku_Id + "' and mp.state_Id='" + state_Id + "' and mp.Store_Type_Id='" + storeType_Id + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Posm_Id")));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Posm")));
                    sb.setOldDeployed_value("");
                    sb.setNew_deployed("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<CategoryMaster> getcompetitionChildData(String category_Id) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            if (category_Id.equals("")) {
                dbcursor = db.rawQuery("select distinct Brand_Id,Brand from Brand_Master where Company_Id <>1 Order by Brand_Sequence", null);
            } else {
                dbcursor = db.rawQuery("select distinct Brand_Id,Brand from Brand_Master where Category_Id='" + category_Id + "' and Company_Id <>1 Order by Brand_Sequence", null);
            }
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster sb = new CategoryMaster();
                    sb.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand_Id")));
                    sb.setBrand_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand")));
                    sb.setSkuQTY("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<CategoryMasterForCompGetterSetter> getcompetitionChildData() {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<CategoryMasterForCompGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("select distinct Company_Id,Company from Company_Master where Is_Competitor<>0 Order by Company", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMasterForCompGetterSetter sb = new CategoryMasterForCompGetterSetter();
                    sb.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Company_Id")));
                    sb.setBrand_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Company")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching opening stock---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public boolean insertprofileBrandList(ProfileLastVisitGetterSetter BrandMaster) {
        db.delete("Store_Profile_LastVisit_Brand", null, null);
        ContentValues values = new ContentValues();
        List<StoreProfileLastVisitBrand> data = BrandMaster.getStoreProfileLastVisitBrand();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Store_Id", data.get(i).getStoreId());
                values.put("Company_Id", data.get(i).getCompanyId());
                values.put("Profile_Image", data.get(i).getProfileImage());
                values.put("Profile_Type", data.get(i).getProfileType());

                long id = db.insert("Store_Profile_LastVisit_Brand", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public BrandMaster getstoreProfileServerdata(String store_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        BrandMaster list = new BrandMaster();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select distinct sb.Profile_Image,sb.Profile_Type from Store_Profile_LastVisit_Brand sb where sb.Store_Id='" + store_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    list.setProfile_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Profile_Image")));
                    list.setSpin_value(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Profile_Type")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<BrandMaster> getstoreProfilebrandServerdata(int store_Id, int company_Id) {
        Log.d("FetchingCategoryType--------------->Start<------------", "------------------");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT distinct Company_Id FROM Store_Profile_LastVisit_Brand WHERE Store_Id=" + store_Id + " and Company_Id=" + company_Id + " ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster sb = new BrandMaster();
                    sb.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Company_Id")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Category!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }


    public long insertcompetitionOfferData(String store_id, String visit_date, ArrayList<CategoryMaster> secPlaceData) {
        db.delete(CommonString.TABLE_INSERT_COMPETITION_OFFERS_TABLE, "STORE_CD" + "='" + store_id + "' AND VISIT_DATE='" + visit_date + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < secPlaceData.size(); i++) {
                values.put("STORE_CD", store_id);
                values.put("VISIT_DATE", visit_date);
                values.put("COMP_OFFER_EXISTS", secPlaceData.get(i).isCompOfferExists());
                values.put("CATEGORY", secPlaceData.get(i).getCategory());
                values.put("CATEGORY_Id", secPlaceData.get(i).getCategoryId().toString());
                values.put("BRAND", secPlaceData.get(i).getBrand_name());
                values.put("BRAND_Id", secPlaceData.get(i).getBrand_Id());
                values.put("PROMOTYPE", secPlaceData.get(i).getPromoType());
                values.put("PROMOTYPE_Id", secPlaceData.get(i).getPromoType_Id());
                values.put("PROMOTYPE_DETAILS", secPlaceData.get(i).getCompOfferRemark().replaceAll("\"|'\"", ""));
                values.put("COMP_OFFER_IMG", secPlaceData.get(i).getCompOffer_img());

                l = db.insert(CommonString.TABLE_INSERT_COMPETITION_OFFERS_TABLE, null, values);

            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ", ex.toString());
        }
        return l;
    }


    public long insertcompetitionFinancialOfferData(String store_id, String visit_date, ArrayList<CategoryMasterForCompGetterSetter> secPlaceData) {
        db.delete(CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE, "STORE_CD" + "='" + store_id + "' AND VISIT_DATE='" + visit_date + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < secPlaceData.size(); i++) {
                values.put("STORE_CD", store_id);
                values.put("VISIT_DATE", visit_date);
                values.put("COMP_OFFER_EXISTS", secPlaceData.get(i).isCompOfferExists());
                values.put("CATEGORY", secPlaceData.get(i).getCategory());
                values.put("CATEGORY_Id", secPlaceData.get(i).getCategoryId().toString());
                values.put("BRAND", secPlaceData.get(i).getBrand_name());
                values.put("BRAND_Id", secPlaceData.get(i).getBrand_Id());
                values.put("PROMOTYPE", secPlaceData.get(i).getPromoType());
                values.put("PROMOTYPE_Id", secPlaceData.get(i).getPromoType_Id());
                values.put("PROMOTYPE_DETAILS", secPlaceData.get(i).getCompOfferRemark().replaceAll("\"|'\"", ""));
                values.put("COMP_OFFER_IMG", secPlaceData.get(i).getCompOffer_img());
                l = db.insert(CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE, null, values);

            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ", ex.toString());
        }
        return l;
    }


    public ArrayList<CategoryMaster> getinsertedcompetitionOfferData(String store_cd, String visit_date) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM COMPETITION_OFFERS_TABLE WHERE STORE_CD ='" + store_cd + "' AND VISIT_DATE='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster sb = new CategoryMaster();
                    sb.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY")));
                    sb.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("CATEGORY_Id")));
                    sb.setBrand_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    sb.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    sb.setPromoType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE")));
                    sb.setPromoType_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE_Id")));
                    sb.setID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    sb.setCompOfferRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE_DETAILS")));
                    sb.setCompOffer_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow("COMP_OFFER_IMG")));
                    String value = dbcursor.getString(dbcursor.getColumnIndexOrThrow("COMP_OFFER_EXISTS"));
                    if (value.equals("1")) {
                        sb.setCompOfferExists(true);
                    } else {
                        sb.setCompOfferExists(false);
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching Asset brand---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    public ArrayList<CategoryMasterForCompGetterSetter> getinsertedcompetitionFinancialOfferData(String store_cd, String visit_date) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<CategoryMasterForCompGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE + " WHERE STORE_CD ='" + store_cd + "' AND VISIT_DATE='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMasterForCompGetterSetter sb = new CategoryMasterForCompGetterSetter();
                    sb.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY")));
                    sb.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("CATEGORY_Id")));
                    sb.setBrand_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND")));
                    sb.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND_Id")));
                    sb.setPromoType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE")));
                    sb.setPromoType_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE_Id")));
                    sb.setID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    sb.setCompOfferRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("PROMOTYPE_DETAILS")));
                    sb.setCompOffer_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow("COMP_OFFER_IMG")));
                    String value = dbcursor.getString(dbcursor.getColumnIndexOrThrow("COMP_OFFER_EXISTS"));
                    if (value.equals("1")) {
                        sb.setCompOfferExists(true);
                    } else {
                        sb.setCompOfferExists(false);
                    }


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching opening stock!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching Asset brand---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public long InsertVisitorLogindata(ArrayList<VisitorDetailGetterSetter> visitorLoginGetterSetter) {
        db.delete(CommonString.TABLE_VISITOR_LOGIN, null, null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < visitorLoginGetterSetter.size(); i++) {
                values.put(CommonString.KEY_EMP_CD, visitorLoginGetterSetter.get(i).getEmpId());
                values.put(CommonString.KEY_NAME, visitorLoginGetterSetter.get(i).getName());
                values.put(CommonString.KEY_DESIGNATION, visitorLoginGetterSetter.get(i).getDesignation());
                values.put(CommonString.KEY_IN_TIME_IMAGE, visitorLoginGetterSetter.get(i).getIn_time_img());
                values.put(CommonString.KEY_OUT_TIME_IMAGE, visitorLoginGetterSetter.get(i).getOut_time_img());
                values.put(CommonString.KEY_EMP_CODE, visitorLoginGetterSetter.get(i).getEmp_code());
                values.put(CommonString.KEY_VISIT_DATE, visitorLoginGetterSetter.get(i).getVisit_date());
                values.put(CommonString.KEY_IN_TIME, visitorLoginGetterSetter.get(i).getIn_time());
                values.put(CommonString.KEY_OUT_TIME, visitorLoginGetterSetter.get(i).getOut_time());
                values.put(CommonString.KEY_UPLOADSTATUS, visitorLoginGetterSetter.get(i).getUpload_status());
                l = db.insert(CommonString.TABLE_VISITOR_LOGIN, null, values);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return l;

    }


    public long updateOutTimeVisitorLoginData(String out_time_image, String out_time, String emp_id, String visit_date) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_OUT_TIME_IMAGE, out_time_image);
            values.put(CommonString.KEY_OUT_TIME, out_time);
            l = db.update(CommonString.TABLE_VISITOR_LOGIN, values, CommonString.KEY_EMP_CD + "='" + emp_id + "' and " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);

        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return l;
    }


    public boolean isVistorDataExists(String emp_id, String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where EMP_CD = '" + emp_id + "' and VISIT_DATE='" + visit_date + "'", null);
            int count = 0;
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    count++;

                    dbcursor.moveToNext();
                }
                dbcursor.close();

                if (count > 0) {
                    return true;
                } else {
                    return false;
                }

            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            return false;
        }

        return false;

    }

    public ArrayList<VisitorDetailGetterSetter> getVisitorLoginData(String visitdate) {
        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where VISIT_DATE = '" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();
                    sb.setEmpId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD))));
                    sb.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    sb.setDesignation(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_DESIGNATION)));
                    sb.setIn_time_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE)));
                    sb.setOut_time_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE)));
                    sb.setEmp_code(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CODE)));
                    sb.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setIn_time(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOut_time(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setUpload_status(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_UPLOADSTATUS)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return list;

    }

    public long updateVisitorUploadData(String empid, String visit_date) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_UPLOADSTATUS, "U");
            l = db.update(CommonString.TABLE_VISITOR_LOGIN, values, CommonString.KEY_EMP_CD + "='" + empid + "'", null);
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return l;
    }

}
