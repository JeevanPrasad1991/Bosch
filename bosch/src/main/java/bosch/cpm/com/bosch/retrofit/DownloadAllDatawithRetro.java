package bosch.cpm.com.bosch.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import bosch.cpm.com.bosch.Database.BOSCH_DB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bosch.cpm.com.bosch.constant.AlertandMessages;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gettersetter.ReferenceVariablesForDownloadActivity;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMasterGettterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.NonWorkingReasonGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.SkuMasterGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.TableStructure;
import bosch.cpm.com.bosch.gsonGetterSetter.TableStructureGetterSetter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by upendrak on 15-12-2017.
 */

public class DownloadAllDatawithRetro extends ReferenceVariablesForDownloadActivity {
    boolean isvalid;
    private Retrofit adapter;
    Context context;
    public int listSize = 0;
    int status = 0;
    BOSCH_DB db;
    ProgressDialog pd;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int from;

    public DownloadAllDatawithRetro(Context context) {
        this.context = context;
    }

    public DownloadAllDatawithRetro(Context context, BOSCH_DB db, ProgressDialog pd, int from) {

        this.context = context;
        this.db = db;
        this.pd = pd;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        this.from = from;
        db.open();
    }


    public void downloadDataUniversalWithoutWait(final ArrayList<String> jsonStringList, final ArrayList<String> KeyNames, int downloadindex, int type, final String visit_date) {
        status = 0;
        isvalid = false;
        final String[] data_global = {""};
        String jsonString = "", KeyName = "";
        int jsonIndex = 0;

        if (jsonStringList.size() > 0) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            jsonString = jsonStringList.get(downloadindex);
            KeyName = KeyNames.get(downloadindex);
            jsonIndex = downloadindex;
            pd.setMessage("Downloading (" + downloadindex + "/" + listSize + ") \n" + KeyName + "");
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<String> call = api.getDownloadAll(jsonData);
            final int[] finalJsonIndex = {jsonIndex};
            final String finalKeyName = KeyName;

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body();
                            if (data.equalsIgnoreCase("")) {
                                data_global[0] = "";
                            } else {
                                data_global[0] = data;
                                if (finalKeyName.equalsIgnoreCase("Table_Structure")) {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    tableStructureObj = new Gson().fromJson(data, TableStructureGetterSetter.class);
                                    String isAllTableCreated = createTable(tableStructureObj);
                                    if (isAllTableCreated != CommonString.KEY_SUCCESS) {
                                        pd.dismiss();
                                        AlertandMessages.showAlert((Activity) context, isAllTableCreated + " not created", true);
                                    }
                                } else {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    switch (finalKeyName) {
                                        case "Non_Working_Reason":
                                            if (!data.contains("No Data")) {
                                                nonWorkingObj = new Gson().fromJson(data, NonWorkingReasonGetterSetter.class);
                                                if (nonWorkingObj != null && !db.insertNonWorkingData(nonWorkingObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Non Working Reason data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                        case "City_Master":
                                            if (!data.contains("No Data")) {
                                                cityMasterObject = new Gson().fromJson(data, CategoryMasterGetterSetter.class);
                                                if (cityMasterObject != null && !db.insertcityMaster(cityMasterObject, visit_date)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "City Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Brand_Master":
                                            if (!data.contains("No Data")) {
                                                brandMasterObject = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                                if (brandMasterObject != null && !db.insertBrandMasterData(brandMasterObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Brand Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }

                                            break;
                                        case "Category_Master":
                                            if (!data.contains("No Data")) {
                                                categoryMasterObject = new Gson().fromJson(data, CategoryMasterGetterSetter.class);
                                                if (categoryMasterObject != null && !db.insertCategoryMasterData(categoryMasterObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Category Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Sku_Master":
                                            if (!data.contains("No Data")) {
                                                skuMasterObject = new Gson().fromJson(data, SkuMasterGetterSetter.class);
                                                if (skuMasterObject != null && !db.insertSkuMasterData(skuMasterObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sku Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Posm_Master":
                                            if (!data.contains("No Data")) {
                                                posmObject = new Gson().fromJson(data, PosmMasterGettterSetter.class);
                                                if (posmObject != null && !db.insertposm(posmObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Posm Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Company_Master":
                                            if (!data.contains("No Data")) {
                                                companyMasterObject = new Gson().fromJson(data, PosmMasterGettterSetter.class);
                                                if (companyMasterObject != null && !db.insetcompanyMaster(companyMasterObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Company Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Mapping_Posm":
                                            if (!data.contains("No Data")) {
                                                mappingPosmObject = new Gson().fromJson(data, PosmMasterGettterSetter.class);
                                                if (mappingPosmObject != null && !db.insertmappingposm(mappingPosmObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Mapping Posm data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                        case "Store_Dimension":
                                            if (!data.contains("No Data")) {
                                                dimensionObject = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                                if (dimensionObject != null && !db.insertdimension(dimensionObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Store Dimension data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                            case "Store_Type_Master":
                                            if (!data.contains("No Data")) {
                                                storeTypeObject = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                                if (storeTypeObject != null && !db.insertstoretype(storeTypeObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Store Type Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                            case "Promo_Type":
                                            if (!data.contains("No Data")) {
                                                promoTypeObject = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                                if (promoTypeObject != null && !db.insertpromoTypeData(promoTypeObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Store Promo Type Master data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                    }
                                }
                            }


                            finalJsonIndex[0]++;
                            if (finalJsonIndex[0] != KeyNames.size()) {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                editor.apply();
                                downloadDataUniversalWithoutWait(jsonStringList, KeyNames, finalJsonIndex[0],
                                        CommonString.DOWNLOAD_ALL_SERVICE, visit_date);
                            } else {
                                db.open();
                                //delete old jcp table data
                                db.deleteOLDJCP(visit_date);
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
                                editor.apply();
                                pd.setMessage("Downloading Images");
                                new DownloadImageTask().execute();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                            editor.apply();
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, finalKeyName + " Data not found ", true);
                        }
                    } else {
                        editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                        editor.apply();
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    isvalid = true;
                    pd.dismiss();
                    if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof SocketException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + t.getMessage().toString() + ")", true);
                    } else {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }
                }
            });
        } else {
            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
            editor.apply();
            pd.setMessage("Downloading Images");
            //delete old jcp table data
            db.deleteOLDJCP(visit_date);
            new DownloadImageTask().execute();
        }
    }

    String createTable(TableStructureGetterSetter tableGetSet) {
        List<TableStructure> tableList = tableGetSet.getTableStructure();
        for (int i = 0; i < tableList.size(); i++) {
            String table = tableList.get(i).getSqlText();
            if (db.createtable(table) == 0) {
                return table;
            }
        }
        return CommonString.KEY_SUCCESS;
    }


    class DownloadImageTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                downloadImages();
                return CommonString.KEY_SUCCESS;
            } catch (FileNotFoundException ex) {
                return CommonString.KEY_FAILURE;
            } catch (IOException ex) {
                return CommonString.KEY_FAILURE;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
            } else {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "Error in downloading", true);
            }

        }

    }

    void downloadImages() throws IOException, FileNotFoundException {
    }


}
