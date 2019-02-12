package bosch.cpm.com.bosch.download;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import org.json.JSONObject;

import java.util.ArrayList;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;

import bosch.cpm.com.bosch.retrofit.DownloadAllDatawithRetro;

public class DownloadActivity extends AppCompatActivity {
    BOSCH_DB db;
    String userId, date;
    private SharedPreferences preferences = null;
    Toolbar toolbar;
    Context context;
    int downloadindex = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        context = this;
        db = new BOSCH_DB(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        date = preferences.getString(CommonString.KEY_DATE, "");
        downloadindex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        getSupportActionBar().setTitle("Download - " + date);
        toolbar.setTitle("Download - " + date);
        UploadDataTask();
    }

    public void UploadDataTask() {
        try {
            ArrayList<String> keysList = new ArrayList<>();
            ArrayList<String> jsonList = new ArrayList<>();
            ArrayList<String> KeyNames = new ArrayList<>();
            KeyNames.clear();
            keysList.clear();
            keysList.add("Table_Structure");
            keysList.add("City_Master");
            keysList.add("Non_Working_Reason");
            keysList.add("Brand_Master");
            keysList.add("Category_Master");
            keysList.add("Sku_Master");
            keysList.add("Posm_Master");
            keysList.add("Company_Master");
            keysList.add("Mapping_Posm");
            keysList.add("Store_Dimension");
            keysList.add("Store_Type_Master");
            keysList.add("Promo_Type");

            if (keysList.size() > 0) {
                for (int i = 0; i < keysList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Downloadtype", keysList.get(i));
                    jsonObject.put("Username", userId);
                    jsonList.add(jsonObject.toString());
                    KeyNames.add(keysList.get(i));
                }
                if (jsonList.size() > 0) {
                    ProgressDialog pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setMessage("Downloading Data" + "(" + "/" + ")");
                    pd.show();
                    DownloadAllDatawithRetro downloadData = new DownloadAllDatawithRetro(context, db, pd, CommonString.TAG_FROM_CURRENT);
                    downloadData.listSize = jsonList.size();
                    downloadData.downloadDataUniversalWithoutWait(jsonList, KeyNames, downloadindex, CommonString.DOWNLOAD_ALL_SERVICE, date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
