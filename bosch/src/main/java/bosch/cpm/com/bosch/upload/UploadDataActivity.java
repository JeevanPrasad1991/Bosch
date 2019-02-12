package bosch.cpm.com.bosch.upload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MultipartBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.cavingettersetter.PromotionInsertDataGetterSetter;
import bosch.cpm.com.bosch.cavingettersetter.WindowMaster;
import bosch.cpm.com.bosch.constant.AlertandMessages;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.delegates.CoverageBean;
import bosch.cpm.com.bosch.gettersetter.GeotaggingBeans;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMasterForCompGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.JourneyPlan;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMaster;
import bosch.cpm.com.bosch.retrofit.PostApi;
import bosch.cpm.com.bosch.retrofit.PostApiForUpload;
import bosch.cpm.com.bosch.retrofit.StringConverterFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadDataActivity extends AppCompatActivity {
    BOSCH_DB db;
    Toolbar toolbar;
    com.squareup.okhttp.RequestBody body1;
    private Retrofit adapter;
    int status = 0, statusforimage = 0;
    Context context;
    private SharedPreferences preferences;
    String userId, visit_date, app_version;
    private ProgressDialog pb;
    ArrayList<CoverageBean> coverageList = new ArrayList<>();
    ArrayList<JourneyPlan> specific_uploadStatus;
    public static int uploadedFiles = 0;
    public static int totalFiles = 0;
    int count = 0;
    boolean isvalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        context = this;
        db = new BOSCH_DB(context);
        db.open();
        coverageList = db.getCoverageData(visit_date);
        pb = new ProgressDialog(context);
        pb.setCancelable(false);
        pb.setMessage("Uploading Data");
        pb.show();
        uploadDataUsingCoverageRecursive(coverageList, 0);
        uploadedFiles = 0;
    }

    public void uploadDataUsingCoverageRecursive(ArrayList<CoverageBean> coverageList, int coverageIndex) {
        try {
            ArrayList<String> keyList = new ArrayList<>();
            keyList.clear();
            String store_id = coverageList.get(coverageIndex).getStoreId();
            db.open();
            specific_uploadStatus = db.getSpecificStoreData(store_id);
            String status = null;
            status = specific_uploadStatus.get(0).getUploadStatus();
            pb.setMessage("Uploading store " + (coverageIndex + 1) + "/" + coverageList.size());
            if (!status.equalsIgnoreCase(CommonString.KEY_D)) {
                keyList.add("CoverageDetail_latest");
                keyList.add("Store_Profile_Data");
                keyList.add("Store_ProfileWith_Dimension_And_SC_Data");
                keyList.add("Posm_With_Images_Data");
                keyList.add("Competitior_BrandWise_Data");
                keyList.add("Competitior_Offer_Data");
                keyList.add("Competitior_Financial_Offer_Data");
                keyList.add("GeoTag");
            }

            if (keyList.size() > 0) {
                uploadDataWithoutWait(keyList, 0, coverageList, coverageIndex);
            } else {
                if (++coverageIndex != coverageList.size()) {
                    uploadDataUsingCoverageRecursive(coverageList, coverageIndex);
                } else {
                    ////CHANGESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
                    //  uploadimages();
                    pb.setMessage("uploading images");
                    File dir = new File(CommonString.FILE_PATH);
                    if (getFileNames(dir.listFiles()).size() > 0) {
                        totalFiles = getFileNames(dir.listFiles()).size();
                        uploadImage(visit_date);
                        //UploadImageFileJsonList(context, visit_date);
                        //  uploadImageRecursiveWithIndex(0, list1);
                    } else {
                        pb.setMessage("Updating status");
                        updatestatusforu(coverageList, 0, visit_date, CommonString.KEY_U);
                    }
                }

            }
            //endregion

        } catch (Exception e) {
            e.printStackTrace();
            pb.dismiss();
            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);
        }

    }

    public void uploadDataWithoutWait(final ArrayList<String> keyList,
                                      final int keyIndex, final ArrayList<CoverageBean> coverageList,
                                      final int coverageIndex) {
        try {
            status = 0;
            final String[] data_global = {""};
            String jsonString = "";
            int type = 0;
            JSONObject jsonObject;

            //region Creating json data
            switch (keyList.get(keyIndex)) {
                case "CoverageDetail_latest":
                    //region Coverage Data
                    jsonObject = new JSONObject();
                    jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                    jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                    jsonObject.put("Latitude", coverageList.get(coverageIndex).getLatitude());
                    jsonObject.put("Longitude", coverageList.get(coverageIndex).getLongitude());
                    jsonObject.put("ReasonId", coverageList.get(coverageIndex).getReasonid());
                    jsonObject.put("SubReasonId", "0");
                    jsonObject.put("Remark", "");
                    jsonObject.put("ImageName", coverageList.get(coverageIndex).getImage());
                    jsonObject.put("AppVersion", app_version);
                    jsonObject.put("UploadStatus", CommonString.KEY_P);
                    jsonObject.put("Checkout_Image", "");
                    jsonObject.put("UserId", userId);
                    jsonString = jsonObject.toString();
                    type = CommonString.COVERAGE_DETAIL;
                    break;

                case "Store_Profile_Data":
                    db.open();
                    BrandMaster store_profile = db.getstoreProfileData(coverageList.get(coverageIndex).getStoreId().toString(), coverageList.get(coverageIndex).getVisitDate());
                    if (store_profile.getKey_Id() != null) {
                        JSONArray profileArray = new JSONArray();
                        JSONArray brandArray = new JSONArray();
                        if (store_profile.getSpin_value().equalsIgnoreCase("Brand")) {
                            db.open();
                            ArrayList<BrandMaster> profile_brand = db.getstoreProfileBrandDatawithCommon_Id(store_profile.getKey_Id());
                            if (profile_brand.size() > 0) {
                                for (int i = 0; i < profile_brand.size(); i++) {
                                    JSONObject object = new JSONObject();
                                    object.put("Key_Id", store_profile.getKey_Id());
                                    object.put("Company_Id", profile_brand.get(i).getBrandId().toString());
                                    brandArray.put(object);
                                }
                            }

                        }
                        JSONObject obj = new JSONObject();
                        obj.put("MID", coverageList.get(coverageIndex).getMID());
                        obj.put("UserId", userId);
                        obj.put("Key_Id", store_profile.getKey_Id());
                        obj.put("Store_Id", coverageList.get(coverageIndex).getStoreId().toString());
                        obj.put("Store_IMG", store_profile.getProfile_img());
                        obj.put("Store_Spin", store_profile.getSpin_value());
                        if (store_profile.getSpin_value().equalsIgnoreCase("Brand")) {
                            obj.put("Store_Brand_List", brandArray);
                        } else {
                            obj.put("Store_Brand_List", "");
                        }
                        profileArray.put(obj);


                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_Profile_Data");
                        jsonObject.put("JsonData", profileArray.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;


                case "Store_ProfileWith_Dimension_And_SC_Data":
                    db.open();
                    BrandMaster profile_dimensiondata = db.getstoreprofileDimensionData(coverageList.get(coverageIndex).getStoreId().toString(),
                            coverageList.get(coverageIndex).getVisitDate());

                    if (profile_dimensiondata.getKey_Id() != null) {
                        JSONArray dimensionArray = new JSONArray();
                        JSONArray dimensionproductArray = new JSONArray();
                        JSONArray dimensionBrandArray = new JSONArray();
                        ArrayList<CategoryMaster> productList = db.getprofiledimensionproductListforupload(profile_dimensiondata.getKey_Id());
                        if (productList.size() > 0) {
                            for (int i = 0; i < productList.size(); i++) {
                                if (productList.get(i).isSelected()) {
                                    JSONObject object = new JSONObject();
                                    object.put("UserId", userId);
                                    object.put("Key_Id", profile_dimensiondata.getKey_Id());
                                    object.put("Category_Id", productList.get(i).getCategoryId().toString());
                                    dimensionproductArray.put(object);
                                }
                            }
                        }


                        ArrayList<BrandMaster> dimension_BrandList = db.getprofiledimensionBrandListforupload(profile_dimensiondata.getKey_Id());
                        if (dimension_BrandList.size() > 0) {
                            for (int k = 0; k < dimension_BrandList.size(); k++) {
                                JSONObject obj = new JSONObject();
                                obj.put("UserId", userId);
                                obj.put("Key_Id", profile_dimensiondata.getKey_Id());
                                obj.put("Company_Id", dimension_BrandList.get(k).getBrandId().toString());
                                dimensionBrandArray.put(obj);

                            }
                        }


                        JSONObject obj = new JSONObject();
                        String sc_present = "", tab_available = "";
                        if (profile_dimensiondata.getSc_present().equalsIgnoreCase("Yes")) {
                            sc_present = "1";
                            if (profile_dimensiondata.getTab_availeble().equalsIgnoreCase("Yes")) {
                                tab_available = "1";
                            } else {
                                tab_available = "0";
                            }
                        } else {
                            sc_present = "0";
                            tab_available = "0";
                        }


                        obj.put("MID", coverageList.get(coverageIndex).getMID());
                        obj.put("UserId", userId);
                        obj.put("Key_Id", profile_dimensiondata.getKey_Id());
                        obj.put("Store_Id", coverageList.get(coverageIndex).getStoreId().toString());
                        obj.put("Store_Dimension", profile_dimensiondata.getProfile_dimension());
                        obj.put("Store_SC_Present", sc_present);
                        obj.put("Store_Tab_Available", tab_available);

                        obj.put("Profile_Product", dimensionproductArray);
                        obj.put("Profile_Brand", dimensionBrandArray);
                        dimensionArray.put(obj);

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_ProfileWith_Dimension_And_SC_Data");
                        jsonObject.put("JsonData", dimensionArray.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;

                case "Posm_With_Images_Data":
                    db.open();
                    ArrayList<PosmMaster> posmMasterArrayList = db.getposmforupload(coverageList.get(coverageIndex).getStoreId());
                    if (posmMasterArrayList.size() > 0) {
                        JSONArray compArray = new JSONArray();
                        JSONArray imageARray = new JSONArray();
                        JSONObject completeData = new JSONObject();
                        ArrayList<PosmMaster> posm_imagesList = db.getposmimagesforupload(coverageList.get(coverageIndex).getStoreId());
                        if (posm_imagesList.size() > 0) {
                            for (int j = 0; j < posm_imagesList.size(); j++) {
                                JSONObject obj = new JSONObject();
                                obj.put("MID", coverageList.get(coverageIndex).getMID());
                                obj.put("UserId", userId);
                                obj.put("Category_Id", posm_imagesList.get(j).getCategory_Id());
                                obj.put("Image_One", posm_imagesList.get(j).getPosmIMG_one());
                                obj.put("Image_Two", posm_imagesList.get(j).getPosmIMG_two());
                                obj.put("Image_Three", posm_imagesList.get(j).getPosmIMG_three());
                                imageARray.put(obj);
                            }

                        }
                        for (int j = 0; j < posmMasterArrayList.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Category_Id", posmMasterArrayList.get(j).getCategory_Id());
                            obj.put("Posm_Id", posmMasterArrayList.get(j).getPosmId().toString());
                            obj.put("Sku_Id", posmMasterArrayList.get(j).getSku_Id().toString());
                            obj.put("Sku_Qty", posmMasterArrayList.get(j).getPosmsku_value());
                            obj.put("Deployment", posmMasterArrayList.get(j).getOldDeployed_value());
                            obj.put("New_Deployment", posmMasterArrayList.get(j).getNew_deployed());
                            compArray.put(obj);
                        }


                        completeData.put("Posm_Data", compArray);
                        completeData.put("Posm_Images_Data", imageARray);


                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Posm_With_Images_Data");
                        jsonObject.put("JsonData", completeData.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }

                    break;

                case "Competitior_BrandWise_Data":
                    db.open();
                    ArrayList<CategoryMaster> posmCompetitorList = db.getboschcompetitionfromdatabase(coverageList.get(coverageIndex).getStoreId(),
                            coverageList.get(coverageIndex).getVisitDate(), "");
                    if (posmCompetitorList.size() > 0) {
                        JSONArray compArray = new JSONArray();
                        for (int j = 0; j < posmCompetitorList.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Category_Id", posmCompetitorList.get(j).getCategoryId().toString());
                            obj.put("Brand_Id", posmCompetitorList.get(j).getBrand_Id());
                            obj.put("Sku_count", posmCompetitorList.get(j).getSkuQTY());
                            compArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Competitior_BrandWise_Data");
                        jsonObject.put("JsonData", compArray.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }

                    break;
                case "Competitior_Offer_Data":
                    db.open();
                    ArrayList<CategoryMaster> competitionOfferList = db.getinsertedcompetitionOfferData(coverageList.get(coverageIndex).getStoreId(),
                            coverageList.get(coverageIndex).getVisitDate());
                    if (competitionOfferList.size() > 0) {
                        JSONArray compArray = new JSONArray();
                        for (int j = 0; j < competitionOfferList.size(); j++) {
                            String CompOfferExists = "";
                            if (competitionOfferList.get(j).isCompOfferExists()) {
                                CompOfferExists = "1";
                            } else {
                                CompOfferExists = "0";
                            }
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Comp_Offer_Exist", CompOfferExists);
                            obj.put("Category_Id", competitionOfferList.get(j).getCategoryId().toString());
                            obj.put("Brand_Id", competitionOfferList.get(j).getBrand_Id());
                            obj.put("PROMOTYPE_Id", competitionOfferList.get(j).getPromoType_Id());
                            obj.put("PROMOTYPE_Details", competitionOfferList.get(j).getCompOfferRemark());
                            obj.put("Competition_Offer_Img", competitionOfferList.get(j).getCompOffer_img());
                            compArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Competitior_Offer_Data");
                        jsonObject.put("JsonData", compArray.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }

                    break;
                case "Competitior_Financial_Offer_Data":
                    db.open();
                    ArrayList<CategoryMasterForCompGetterSetter> competitionFinancialOfferList = db.getinsertedcompetitionFinancialOfferData(coverageList.get(coverageIndex).getStoreId(),
                            coverageList.get(coverageIndex).getVisitDate());
                    if (competitionFinancialOfferList.size() > 0) {
                        JSONArray compArray = new JSONArray();
                        for (int j = 0; j < competitionFinancialOfferList.size(); j++) {
                            String financialOfferExists = "";
                            if (competitionFinancialOfferList.get(j).isCompOfferExists()) {
                                financialOfferExists = "1";
                            } else {
                                financialOfferExists = "0";
                            }
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Comp_Financial_Exist", financialOfferExists);
                            obj.put("Category_Id", competitionFinancialOfferList.get(j).getCategoryId().toString());
                            obj.put("Brand_Id", competitionFinancialOfferList.get(j).getBrand_Id());
                            obj.put("PROMOTYPE_Id", competitionFinancialOfferList.get(j).getPromoType_Id());
                            obj.put("PROMOTYPE_Details", competitionFinancialOfferList.get(j).getCompOfferRemark());
                            obj.put("Competition_Financial_Offer_Img", competitionFinancialOfferList.get(j).getCompOffer_img());
                            compArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Competitior_Financial_Offer_Data");
                        jsonObject.put("JsonData", compArray.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }

                    break;


                case "GeoTag":
                    //region GeoTag
                    ArrayList<GeotaggingBeans> geotaglist = db.getinsertGeotaggingData(coverageList.get(coverageIndex).getStoreId(), "N");
                    if (geotaglist.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < geotaglist.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put(CommonString.KEY_STORE_ID, geotaglist.get(j).getStoreid());
                            obj.put(CommonString.KEY_VISIT_DATE, visit_date);
                            obj.put(CommonString.KEY_LATITUDE, geotaglist.get(j).getLatitude());
                            obj.put(CommonString.KEY_LONGITUDE, geotaglist.get(j).getLongitude());
                            obj.put("FRONT_IMAGE", geotaglist.get(j).getImage());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "GeoTag");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }

                    break;
            }
            //endregion

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            final int[] finalJsonIndex = {keyIndex};
            final String finalKeyName = keyList.get(keyIndex);
            if (jsonString != null && !jsonString.equalsIgnoreCase("")) {
                pb.setMessage("Uploading (" + keyIndex + "/" + keyList.size() + ") \n" + keyList.get(keyIndex) + "\n Store uploading " +
                        (coverageIndex + 1) + "/" + coverageList.size());
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = null;
                if (type == CommonString.COVERAGE_DETAIL) {
                    call = api.getCoverageDetail(jsonData);
                } else if (type == CommonString.UPLOADJsonDetail) {
                    call = api.getUploadJsonDetail(jsonData);
                }
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equals("")) {
                                    pb.dismiss();
                                    data_global[0] = "";
                                    AlertandMessages.showAlert((Activity) context, "Invalid Data :" + " problem occured at " + keyList.get(keyIndex), true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    data_global[0] = data;
                                    if (finalKeyName.equalsIgnoreCase("CoverageDetail_latest")) {
                                        try {
                                            coverageList.get(coverageIndex).setMID(Integer.parseInt(data_global[0]));
                                            specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_P);
                                            db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStoreId(),
                                                    coverageList.get(coverageIndex).getVisitDate(),
                                                    CommonString.KEY_P);

                                        } catch (NumberFormatException ex) {
                                            pb.dismiss();
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                                        }
                                    } else if (data_global[0].contains(CommonString.KEY_SUCCESS)) {
                                        if (finalKeyName.equalsIgnoreCase("GeoTag")) {
                                        }
                                    } else {
                                        pb.dismiss();
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName + " : " + data_global[0], true);
                                    }
                                    finalJsonIndex[0]++;
                                    if (finalJsonIndex[0] != keyList.size()) {
                                        uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                                    } else {
                                        pb.setMessage("updating status :" + coverageIndex);
                                        //uploading status D for current store from coverageList
                                        specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_D);
                                        updateStatus(coverageList, coverageIndex, CommonString.KEY_D);
                                    }
                                }

                            } catch (Exception e) {
                                pb.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                            }
                        } else {
                            pb.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pb.dismiss();
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }
                });

            } else {
                finalJsonIndex[0]++;
                if (finalJsonIndex[0] != keyList.size()) {
                    uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                } else {
                    pb.setMessage("updating status :" + coverageIndex);
                    //uploading status D for current store from coverageList
                    specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_D);
                    updateStatus(coverageList, coverageIndex, CommonString.KEY_D);

                }
            }
        } catch (Exception ex) {
            pb.dismiss();
            AlertandMessages.showAlert((Activity) context, ex.toString(), true);
        }
    }

    void updateStatus(final ArrayList<CoverageBean> coverageList, final int coverageIndex,
                      final String status) {
        if (coverageList.get(coverageIndex) != null) {
            try {
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();

                final int[] tempcoverageIndex = {coverageIndex};
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                jsonObject.put("UserId", userId);
                jsonObject.put("Status", status);
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);

                Call<ResponseBody> call = api.getCoverageStatusDetail(jsonData);
                pb.setMessage("Uploading store status " + (coverageIndex + 1) + "/" + coverageList.size());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equals("")) {
                                    pb.dismiss();
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    if (data.contains("1")) {
                                        db.open();
                                        db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStoreId(), coverageList.get(coverageIndex).getVisitDate(), status);
                                        specific_uploadStatus.get(0).setUploadStatus(status);
                                        tempcoverageIndex[0]++;
                                        if (tempcoverageIndex[0] != coverageList.size()) {
                                            uploadDataUsingCoverageRecursive(coverageList, tempcoverageIndex[0]);
                                        } else {
                                            //  uploadimages();
                                            pb.setMessage("uploading images");
                                            File dir = new File(CommonString.FILE_PATH);
                                            if (getFileNames(dir.listFiles()).size() > 0) {
                                                totalFiles = getFileNames(dir.listFiles()).size();
                                                uploadImage(coverageList.get(coverageIndex).getVisitDate());
                                                // UploadImageFileJsonList(context, );
                                                // uploadImageRecursiveWithIndex(0, list1);
                                            } else {
                                                db.open();
                                                db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStoreId(), coverageList.get(coverageIndex).getVisitDate(), status);
                                                updateStatus(coverageList, coverageIndex, status);
                                            }
                                        }
                                    } else {
                                        pb.dismiss();
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                    }

                                }
                            } catch (Exception e) {
                                pb.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                            }
                        } else {
                            pb.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pb.dismiss();
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);

                    }
                });

            } catch (JSONException ex) {
                pb.dismiss();
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON + " (" + ex.toString() + " )", true);

            }
        }

    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }


    public void UploadImageRecursive(final Context context, final String covergeDate) {
        try {
            statusforimage = 0;
            int totalfiles = 0;
            String filename = null, foldername = null;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            count = file.length;
            if (file.length > 0) {
                filename = "";
                totalfiles = f.listFiles().length;
                pb.setMessage("Uploading images" + "(" + uploadedFiles + "/" + totalFiles + ")");
                for (int i = 0; i < file.length; i++) {
                    if (new File(CommonString.FILE_PATH + file[i].getName()).exists()) {

                        if (file[i].getName().contains("_STOREIMG_") || file[i].getName().contains("_NONWORKING_")) {
                            foldername = "CoverageImages";
                        } else if (file[i].getName().contains("_POSMIMGONE_") || file[i].getName().contains("_POSMIMGTWO_") || file[i].getName().contains("_POSMIMGTHREE_")) {
                            foldername = "POSMImages";
                        } else if (file[i].getName().contains("_PROFILEIMG_")) {
                            foldername = "StoreProfileImage";
                        } else if (file[i].getName().contains("_COMPOFFERIMG_") || file[i].getName().contains("_COMP_FOF_IMG_")) {
                            foldername = "CompetitionOfferImages";
                        } else if (file[i].getName().contains("_GeoTag_")) {
                            foldername = "GeoTagImages";
                        } else if (file[i].getName().contains("_visitor_intime") ||file[i].getName().contains("_visitor_outtime")) {
                            foldername = "VisitorImages";
                        }else {
                            foldername="BulkUploads";
                        }
                        filename = file[i].getName();
                    }
                    break;
                }

                File originalFile = new File(CommonString.FILE_PATH + filename);
                File finalFile = saveBitmapToFile(originalFile);
                if (finalFile == null) {
                    finalFile = originalFile;
                }
                String date;
                if (filename.contains("-")) {
                    date = getParsedDate(filename);
                } else {
                    date = visit_date.replace("/", "");
                }

                com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
                okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);

                com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
                body1 = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("file", finalFile.getName(), photo)
                        .addFormDataPart("FolderName", foldername)
                        .addFormDataPart("Path", date)
                        .build();

                retrofit.Retrofit adapter = new retrofit.Retrofit.Builder()
                        .baseUrl(CommonString.URLGORIMAG)
                        .client(okHttpClient)
                        .addConverterFactory(new StringConverterFactory())
                        .build();
                PostApiForUpload api = adapter.create(PostApiForUpload.class);
                retrofit.Call<String> call = api.getUploadImageRetrofitOne(body1);

                final File finalFile1 = finalFile;
                call.enqueue(new retrofit.Callback<String>() {
                    @Override
                    public void onResponse(retrofit.Response<String> response) {
                        if (response.code() == 200 && response.message().equals("OK") && response.isSuccess() && response.body().contains("Success")) {
                            finalFile1.delete();
                            statusforimage = 1;
                            uploadedFiles++;
                        } else {
                            statusforimage = 0;
                        }
                        if (statusforimage == 0) {
                            pb.dismiss();
                            if (!((Activity) context).isFinishing()) {
                                AlertandMessages.showAlert((Activity) context, "Image not uploaded." + "\n" + uploadedFiles + " images uploaded out of " + totalFiles, true);
                            }
                        } else {
                            UploadImageRecursive(context, covergeDate);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                            statusforimage = -1;
                            pb.dismiss();
                            if (!((Activity) context).isFinishing()) {
                                AlertandMessages.showAlert((Activity) context, "Network Error in upload." + "\n" + uploadedFiles + " images uploaded out of " + totalFiles, true);
                            }
                        }
                    }
                });

            } else {
                if (totalFiles == uploadedFiles) {
                    pb.setMessage("Updating Status");
                    specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_U);
                    updatestatusforu(coverageList, 0, visit_date, CommonString.KEY_U);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (totalFiles == uploadedFiles) {
                AlertandMessages.showAlert((Activity) context, "All images uploaded but status not updated", true);
            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.KEY_FAILURE + " (" + e.toString() + " )", true);
            }
        }
    }


    private void updatestatusforu(final ArrayList<CoverageBean> coverageList, int index, final String visit_date, final String status) {
        try {
            db.open();
            final int[] indexlocal = {index};
            final boolean[] status_u = {false};
            final ArrayList<JourneyPlan> store_data = db.getSpecificStoreData(coverageList.get(index).getStoreId().toString());
            if (store_data.size() > 0) {
                if (store_data.get(0).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .build();
                    index++;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("StoreId", store_data.get(0).getStoreId());
                    jsonObject.put("VisitDate", visit_date);
                    jsonObject.put("UserId", userId);
                    jsonObject.put("Status", status);
                    RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    PostApi api = adapter.create(PostApi.class);
                    Call<ResponseBody> call = api.getCoverageStatusDetail(jsonData);
                    pb.setMessage("Uploading store status " + (index) + "/" + coverageList.size());
                    final int finalIndex = index;
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ResponseBody responseBody = response.body();
                            String data = null;
                            if (responseBody != null && response.isSuccessful()) {
                                try {
                                    data = response.body().string();
                                    if (data.equals("")) {
                                        pb.dismiss();
                                        status_u[0] = false;
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                    } else {
                                        data = data.substring(1, data.length() - 1).replace("\\", "");
                                        if (data.contains("1")) {
                                            status_u[0] = true;
                                            db.open();
                                            db.updateJaurneyPlanSpecificStoreStatus(store_data.get(0).getStoreId().toString(),
                                                    store_data.get(0).getVisitDate(), status);
                                            db.deleteSpecificStoreData(store_data.get(0).getStoreId().toString());
                                            indexlocal[0]++;
                                            if (indexlocal[0] != coverageList.size()) {
                                                updatestatusforu(coverageList, indexlocal[0], visit_date, CommonString.KEY_U);
                                            } else {
                                                if (status_u[0] == true) {
                                                    pb.dismiss();
                                                    AlertandMessages.showAlert((Activity) context, "All data and images upload Successfully.", true);
                                                }
                                            }
                                        } else {
                                            status_u[0] = false;
                                            pb.dismiss();
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                        }

                                    }
                                } catch (Exception e) {
                                    status_u[0] = false;
                                    pb.dismiss();
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                }
                            } else {
                                status_u[0] = false;
                                pb.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            status_u[0] = false;
                            pb.dismiss();

                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            pb.dismiss();
            AlertandMessages.showAlert((Activity) context, "Json Persing Error (" + e.getMessage().toString() + " )", true);

        }

    }

    public static File saveBitmapToFile(File file) {
        File file2 = file;
        try {
            int inWidth = 0;
            int inHeight = 0;
            InputStream in = new FileInputStream(file2);
            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;
            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            // decode full image pre-resized
            in = new FileInputStream(file2);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / 800, inHeight / 500);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            if (roughBitmap == null) {
                return null;
            }
            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, 800, 500);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap,
                    (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            // save image
            try {
                FileOutputStream out = new FileOutputStream(file2);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                Log.e("Image", e.toString(), e);
            }
        } catch (IOException e) {
            Log.e("Image", e.toString(), e);
            return file2;
        }
        return file;
    }


    String getParsedDate(String filename) {
        String testfilename = filename;
        testfilename = testfilename.substring(testfilename.indexOf("-") + 1);
        testfilename = testfilename.substring(0, testfilename.indexOf("-"));
        return testfilename;
    }


    void uploadImage(String coverageDate) {
        pb.setMessage("uploading images");
        File f = new File(CommonString.FILE_PATH);
        File file[] = f.listFiles();
        if (file.length > 0) {
            UploadImageRecursive(context, coverageDate);
        } else {
            pb.setMessage("Updating status");
            updatestatusforu(coverageList, 0, coverageDate, CommonString.KEY_U);
        }
    }
}
