package bosch.cpm.com.bosch.constant;

import android.os.Environment;

/**
 * Created by jeevanp on 14-12-2017.
 */

public class CommonString {
    //preference
    public static final String KEY_USERNAME = "USERNAME";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_YYYYMMDD_DATE = "yyyymmddDate";
    public static final String KEY_STOREVISITED_STATUS = "STOREVISITED_STATUS";
    public static String URL = "http://bosch.parinaam.in/webservice/Boschwebservice.svc/";
    public static String URLGORIMAG = "http://bosch.parinaam.in/webservice/Imageupload.asmx/";

    public static final String KEY_PATH = "PATH";
    public static final String KEY_VERSION = "APP_VERSION";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_N = "N";
    public static final String KEY_FAILURE = "Failure";
    public static final String MESSAGE_INTERNET_NOT_AVALABLE = "No Internet Connection.Please Check Your Network Connection";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Please Check Your Network Connection";
    public static final String MESSAGE_INVALID_JSON = "Problem Occured while parsing Json : invalid json data";
    public static final String STORE_VISIT_LATER = "VL";
    public static final String TABLE_JCP_DATA = "Journey_Plan";

    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "C";
    public static final String KEY_Y = "Y";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_CHECK_IN = "I";
    public static final String KEY_GEO_Y = "Y";
    ///all service key

    public static final String KEY_LOGIN_DETAILS = "LoginDetaillatest";
    public static final String KEY_DOWNLOAD_INDEX = "download_Index";
    public static final int TAG_FROM_CURRENT = 1;
    public static final int DOWNLOAD_ALL_SERVICE = 2;
    public static final int COVERAGE_DETAIL = 3;
    public static final int UPLOADJsonDetail = 5;
    //File Path
    public static final String BACKUP_FILE_PATH = Environment.getExternalStorageDirectory() + "/BOSCH_BACKUP/";
    ////for insert data key
    public static final String KEY_STORE_CD = "STORE_CD";

    public static final String KEY_JOURNEY_PLANSEARCH = "Journey_Plan";
    public static final String KEY_STORE_PROFILE_BRANDS = "Store_Profile_LastVisit_Brand";

    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/.BOSCH_IMAGES/";
    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final String KEY_USER_TYPE = "RIGHTNAME";

    //jeevan
    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_JOURNEY_PLAN_OBJECT = "JOURNEY_PLAN_OBJECT";

    public static final String KEY_STATE_ID = "STATE_ID";
    public static final String KEY_STORE_TYPEID = "STORE_TYPEID";
    public static final String KEY_DISTRIBUTOR_ID = "DISTRIBUTOR_ID";
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_STORE_ADDRESS = "STORE_ADDRESS";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_IMAGE = "STORE_IMAGE";
    public static final String KEY_COVERAGE_REMARK = "REMARK";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_ID = "ID";
    public static final String KEY_GEO_TAG = "GEO_TAG";

    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_REASON_ID + " INTEGER," + KEY_COVERAGE_REMARK
            + " VARCHAR," + KEY_REASON + " VARCHAR)";


    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";
    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "STORE_ID"
            + " INTEGER,"

            + "LATITUDE"
            + " VARCHAR,"

            + "LONGITUDE"
            + " VARCHAR,"

            + "GEO_TAG"
            + " VARCHAR,"

            + "STATUS"
            + " VARCHAR,"

            + "FRONT_IMAGE" + " VARCHAR)";


    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_LOGIN_NO_DATA = "Data mapping error.";
    public static final String KEY_NOTICE_BOARD_LINK = "NOTICE_BOARD_LINK";
    ///for newwwwwwwwwwwwwwwww
    public static final String KEY_CATEGORY = "CATEGORY";
    public static final String KEY_CATEGORY_CD = "CATEGORY_CD";

    public static final String TABLE_STORE_PROFILE = "TABLE_STORE_PROFILE";
    public static final String CREATE_TABLE_STORE_PROFILE = "CREATE TABLE IF NOT EXISTS TABLE_STORE_PROFILE(KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, STORE_CD INTEGER, VISIT_DATE VARCHAR, PROFILE_IMG VARCHAR,SPIN_VALUE VARCHAR)";

    public static final String TABLE_STORE_PROFILE_BRAND_DATA = "TABLE_STORE_PROFILE_BRAND";
    public static final String CREATE_TABLE_STORE_PROFILE_BRAND_DATA = "CREATE TABLE IF NOT EXISTS TABLE_STORE_PROFILE_BRAND(COMMON_Id INTEGER, STORE_CD INTEGER, BRAND VARCHAR,BRAND_Id INTEGER,iSselecteD INTEGER)";

    public static final String TABLE_STORE_PROFILE_DIMENSION = "TABLE_STORE_PROFILE_DIMENSION";
    public static final String CREATE_TABLE_STORE_PROFILE_DIMENSION = "CREATE TABLE IF NOT EXISTS TABLE_STORE_PROFILE_DIMENSION(KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, STORE_CD INTEGER, VISIT_DATE VARCHAR, STORE_DIMENSION VARCHAR,SC_PRESENT VARCHAR,TAB_AVAILEBLE VARCHAR)";

    public static final String TABLE_STORE_PROFILE_BRAND_DIMENSION = "TABLE_STORE_PROFILE_BRAND_DIMENSION";
    public static final String CREATE_TABLE_STORE_PROFILE_BRAND_DIMENSION = "CREATE TABLE IF NOT EXISTS TABLE_STORE_PROFILE_BRAND_DIMENSION(COMMON_Id INTEGER, STORE_CD INTEGER, BRAND VARCHAR,BRAND_Id INTEGER,iSselecteD INTEGER,DIMENSION VARCHAR)";

    public static final String TABLE_STORE_PROFILE_PRODUCT_DIMENSION = "TABLE_STORE_PROFILE_PRODUCT_DIMENSION";
    public static final String CREATE_TABLE_STORE_PROFILE_PRODUCT_DIMENSION = "CREATE TABLE IF NOT EXISTS TABLE_STORE_PROFILE_PRODUCT_DIMENSION(COMMON_Id INTEGER, STORE_CD INTEGER, PRODUCT VARCHAR,PRODUCT_Id INTEGER,iSselecteD INTEGER,DIMENSION VARCHAR)";


    public static final String TABLE_INSERT_VISIINITIATIVEHEADER_DATA = "initiativeHeader_data";


    public static final String TABLE_INSERT_VISIINITIATIVE_DATA = "initiativeVisibility_data";


    public static final String TABLE_INSERT_VISIINITIATIV_CHECKLIST_DATA = "initiativeVisibility_Checklist";


    public static final String TABLE_INSERT_POSMHEADER = "POSMHEADER_TABLE";

    public static final String CREATE_TABLE_insert_POSMHEADER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_POSMHEADER
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "STORE_CD"
            + " INTEGER,"
            + "SKU_CD"
            + " INTEGER,"
            + "SKU"
            + " VARCHAR,"
            + "SKU_QUANTITY"
            + " INTEGER,"
            + "CATEGORY_Id" + " INTEGER)";

    public static final String TABLE_INSERT_POSM_TABLE = "POSM_TABLE";

    public static final String CREATE_TABLE_INSERT_POSM_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_POSM_TABLE
            + " ("
            + "Common_Id"
            + " INTEGER,"
            + "CATEGORY_Id"
            + " INTEGER,"
            + "STORE_CD"
            + " INTEGER,"
            + "POSM_Id"
            + " INTEGER,"
            + "POSM"
            + " VARCHAR,"
            + "OLD_DEPLOYMENT"
            + " INTEGER,"

            + "SKU_CD"
            + " INTEGER,"
            + "SKU"
            + " VARCHAR,"
            + "SKU_QUANTITY"
            + " INTEGER,"

            + "NEW_DEPLOYMENT"
            + " INTEGER)";

    public static final String TABLE_INSERT_POSM_IMAGES_TABLE = "POSM_IMAGES_TABLE";

    public static final String CREATE_TABLE_INSERT_POSM_IMAGES_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_POSM_IMAGES_TABLE
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "CATEGORY_Id"
            + " INTEGER,"
            + "STORE_CD"
            + " INTEGER,"
            + "POSM_IMG_ONE"
            + " VARCHAR,"
            + "POSM_IMG_TWO"
            + " VARCHAR,"
            + "POSM_IMG_THREE"
            + " VARCHAR)";


    public static final String TABLE_INSERT_COMPETITION_HEADER = "COMPETITIONHEADER_TABLE";

    public static final String CREATE_TABLE_insert_COMPETITIONHEADER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_COMPETITION_HEADER
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "STORE_CD"
            + " INTEGER,"
            + "CATEGORY"
            + " VARCHAR,"
            + "CATEGORY_Id" + " INTEGER)";

    public static final String TABLE_INSERT_COMPETITION_OFFERS_TABLE = "COMPETITION_OFFERS_TABLE";

    public static final String CREATE_TABLE_INSERT_COMPETITION_OFFERS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_COMPETITION_OFFERS_TABLE
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "VISIT_DATE" + " VARCHAR,"
            + "STORE_CD" + " INTEGER,"
            + "COMP_OFFER_EXISTS" + " INTEGER,"
            + "CATEGORY" + " VARCHAR,"
            + "CATEGORY_Id" + " INTEGER,"
            + "BRAND" + " VARCHAR,"
            + "BRAND_Id" + " INTEGER,"
            + "PROMOTYPE" + " VARCHAR,"
            + "PROMOTYPE_Id" + " INTEGER,"
            + "PROMOTYPE_DETAILS" + " VARCHAR,"
            + "COMP_OFFER_IMG" + " VARCHAR)";


    public static final String TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE = "COMPETITION_FINANCIAL_OFFERS_TABLE";

    public static final String CREATE_TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_COMPETITION_FINANCIAL_OFFERS_TABLE
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "VISIT_DATE" + " VARCHAR,"
            + "STORE_CD" + " INTEGER,"
            + "COMP_OFFER_EXISTS" + " INTEGER,"
            + "CATEGORY" + " VARCHAR,"
            + "CATEGORY_Id" + " INTEGER,"
            + "BRAND" + " VARCHAR,"
            + "BRAND_Id" + " INTEGER,"
            + "PROMOTYPE" + " VARCHAR,"
            + "PROMOTYPE_Id" + " INTEGER,"
            + "PROMOTYPE_DETAILS" + " VARCHAR,"
            + "COMP_OFFER_IMG" + " VARCHAR)";


    public static final String TABLE_STORE_COMPETITION = "TABLE_STORE_COMPETITION";
    public static final String CREATE_TABLE_STORE_COMPETITION = "CREATE TABLE IF NOT EXISTS TABLE_STORE_COMPETITION(Common_Id INTEGER, STORE_CD INTEGER, VISIT_DATE VARCHAR, CATEGORY_Id INTEGER,CATEGORY VARCHAR, BRAND_Id INTEGER,BRAND VARCHAR,BRANDOF_SKU_QTY INTEGER)";

    public static final String TABLE_VISITOR_LOGIN = "TABLE_VISITOR_LOGIN";
    public static final String KEY_DESIGNATION = "DESIGNATION";
    public static final String KEY_EMP_CODE = "EMP_CODE";
    public static final String KEY_IN_TIME_IMAGE = "IN_TIME_IMAGE";
    public static final String KEY_OUT_TIME_IMAGE = "OUT_TIME_IMAGE";
    public static final String KEY_EMP_CD = "EMP_CD";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_UPLOADSTATUS = "UPLOAD_STATUS";
    public static final int TIMEOUT = 30000;
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";

    public static final String CREATE_TABLE_VISITOR_LOGIN = "CREATE TABLE "
            + TABLE_VISITOR_LOGIN + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_EMP_CD + " VARCHAR,"
            + KEY_EMP_CODE + " VARCHAR,"
            + KEY_NAME + " VARCHAR,"
            + KEY_DESIGNATION + " VARCHAR,"
            + KEY_UPLOADSTATUS + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_IN_TIME_IMAGE + " VARCHAR,"
            + KEY_OUT_TIME_IMAGE + " VARCHAR)";

}
