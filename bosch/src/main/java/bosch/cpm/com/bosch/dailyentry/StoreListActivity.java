package bosch.cpm.com.bosch.dailyentry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bosch.cpm.com.bosch.BoschLoginActivty;
import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.AlertandMessages;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.delegates.CoverageBean;
import bosch.cpm.com.bosch.download.DownloadActivity;
import bosch.cpm.com.bosch.gpsenable.LocationEnableCommon;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CityMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.JCPGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.JourneyPlan;
import bosch.cpm.com.bosch.gsonGetterSetter.ProfileLastVisitGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreTypeMaster;
import bosch.cpm.com.bosch.retrofit.PostApi;
import bosch.cpm.com.bosch.upload.PreviousDataUploadActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StoreListActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener {
    private Context context;
    private String userId, user_type;
    private ArrayList<JourneyPlan> storelist = new ArrayList<>();
    ArrayList<StoreTypeMaster> storeTypeList = new ArrayList<>();
    private String date;
    private BOSCH_DB db;
    private ValueAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout linearlay;
    private Dialog dialog;
    private FloatingActionButton fab;
    private double lat = 0.0;
    private double lon = 0.0;
    SharedPreferences preferences;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private SharedPreferences.Editor editor = null;
    private LocationRequest mLocationRequest;
    LocationEnableCommon locationEnableCommon;
    private static final String TAG = StoreimageActivity.class.getSimpleName();

    int downloadIndex;
    ProgressDialog loading;
    ///for searched journey data
    private ArrayAdapter<CharSequence> city_adapter, storeTypeAdapter;
    ArrayList<CityMaster> cityMasterArrayList = new ArrayList<>();
    CardView card;
    Spinner city_spin, storeTypeSpin;
    Button btn_searchCity;
    private Retrofit retrofit_Adapter;
    String city_name = "", city_Id = "", storeTypeName = "", storeTypeId = "";
    TextView txt_nodata;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistfablayout);
        declaration();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data
                if (checkNetIsAvailable()) {
                    if (db.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage("Please Upload Previous Data First")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(StoreListActivity.this, PreviousDataUploadActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        try {
                            db.open();
                            db.deletePreviousUploadedData(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent startDownload = new Intent(StoreListActivity.this, DownloadActivity.class);
                        startActivity(startDownload);
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        finish();
                    }
                } else {
                    Snackbar.make(recyclerView, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    protected void onResume() {
        super.onResume();
        db.open();
        storelist = db.getStoreData(date);
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        if (db.getcitymasterList(date).size() > 0 && storelist.size() > 0 && downloadIndex == 0) {
            adapter = new ValueAdapter(StoreListActivity.this, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            linearlay.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            txt_nodata.setVisibility(View.GONE);
            conditionforspin(storelist);
        } else {
            if (db.getcitymasterList(date).size() > 0 && downloadIndex == 0) {
                recyclerView.setVisibility(View.GONE);
                txt_nodata.setVisibility(View.VISIBLE);
                linearlay.setVisibility(View.GONE);
                card.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearlay.setVisibility(View.VISIBLE);
                card.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                txt_nodata.setVisibility(View.GONE);
            }
        }


        ////////////Start SERVICE for uploading Images
        // Intent svc = new Intent(this, BackgroundService.class);
        //   startService(svc);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date_device = new Date();
        if (!dateFormat.format(date_device).equalsIgnoreCase(date)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this).setTitle("Alert Dialog").setCancelable(false);
            builder.setMessage("Your Device date does not match login Date. You will be logged out now");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(context, BoschLoginActivty.class));
                    StoreListActivity.this.finish();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        StoreListActivity.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreListActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_searchCity:
                if (checkNetIsAvailable()) {
                    if (city_Id.equals("")) {
                        Snackbar.make(btn_searchCity, getString(R.string.cityselection), Snackbar.LENGTH_LONG).show();
                    } else if (storeTypeId.equals("")) {
                        Snackbar.make(btn_searchCity, getString(R.string.storeTypeSelection), Snackbar.LENGTH_LONG).show();
                    } else {
                        downloadJourneyPlanData(context, userId, city_Id, storeTypeId);
                    }
                } else {
                    Snackbar.make(btn_searchCity, getString(R.string.nonetwork), Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.city_spin:
                if (position != 0) {
                    city_name = cityMasterArrayList.get(position).getCity();
                    city_Id = cityMasterArrayList.get(position).getCityId().toString();

                } else {
                    city_name = "";
                    city_Id = "";
                }
                break;

            case R.id.storeTypeSpin:
                if (position != 0) {
                    storeTypeName = storeTypeList.get(position).getStoreType();
                    storeTypeId = storeTypeList.get(position).getStoreTypeId().toString();
                } else {
                    storeTypeName = "";
                    storeTypeId = "";
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<JourneyPlan> data = Collections.emptyList();

        public ValueAdapter(Context context, List<JourneyPlan> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final JourneyPlan current = data.get(position);
            viewHolder.chkbtn.setBackgroundResource(R.mipmap.checkout);
            viewHolder.txt.setText(current.getStoreName() + " - " + current.getStoreType() + " : ID : " + current.getStoreId());
            viewHolder.address.setText(current.getAddress1() + " - " + current.getCity());
            if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_u);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            } else if (current.getUploadStatus().equals(CommonString.STORE_VISIT_LATER)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.visit_later);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_d);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_p);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.tick);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                if (chekDataforCheckout(current.getStoreId().toString(), current.getStateId().toString(), current.getStoreTypeId().toString(),
                        current.getGeoTag(), current.getVisitDate())) {
                    viewHolder.chkbtn.setVisibility(View.VISIBLE);
                    viewHolder.imageview.setVisibility(View.INVISIBLE);

                } else {
                    viewHolder.imageview.setVisibility(View.INVISIBLE);
                    viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.green));
                }
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.leave_tick);
            } else {
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.GONE);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int store_id = current.getStoreId();
                    if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_again_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        Snackbar.make(v, R.string.title_store_list_activity_already_store_closed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        boolean entry_flag = true;
                        boolean showdialog = true;
                        for (int j = 0; j < storelist.size(); j++) {
                            if (storelist.get(j).getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                showdialog = false;
                                if (store_id != storelist.get(j).getStoreId()) {
                                    entry_flag = false;
                                    break;
                                } else {
                                    break;
                                }
                            }
                        }
                        if (entry_flag) {
                            boolean fortempdata = false;
                            showMyDialog(current, showdialog, fortempdata);
                        } else {
                            Snackbar.make(v, R.string.title_store_list_checkout_current, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }

                    }
                }
            });
            viewHolder.chkbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                    builder.setMessage(R.string.wantcheckout)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (CheckNetAvailability()) {
                                        Intent intent = new Intent(StoreListActivity.this, CheckoutActivty.class);
                                        intent.putExtra(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    } else {
                                        Snackbar.make(recyclerView, R.string.nonetwork, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }

                                }
                            })
                            .setNegativeButton(R.string.closed, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }


        public boolean CheckNetAvailability() {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                // we are connected to a network
                connected = true;
            }
            return connected;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, address;
            RelativeLayout relativelayout;
            ImageView imageview;
            Button chkbtn;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = itemView.findViewById(R.id.storelistviewxml_storename);
                address = itemView.findViewById(R.id.storelistviewxml_storeaddress);
                relativelayout = itemView.findViewById(R.id.storenamelistview_layout);
                imageview = itemView.findViewById(R.id.storelistviewxml_storeico);
                chkbtn = itemView.findViewById(R.id.chkout);
                Cardbtn = itemView.findViewById(R.id.card_view);
            }
        }

    }


    void showMyDialog(final JourneyPlan current, final boolean isVisitLater, final boolean fortempdata) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_NAME, current.getStoreName());
                    editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                    editor.putString(CommonString.KEY_STORE_ADDRESS, current.getAddress1().toString());
                    editor.putString(CommonString.KEY_DISTRIBUTOR_ID, current.getDistributorId().toString());
                    editor.putString(CommonString.KEY_STORE_TYPEID, current.getStoreTypeId().toString());
                    editor.putString(CommonString.KEY_STATE_ID, current.getStateId().toString());
                    editor.putString(CommonString.KEY_GEO_TAG, current.getGeoTag());
                    editor.commit();
                    dialog.cancel();
                    ArrayList<CoverageBean> specdata;
                    specdata = db.getSpecificCoverageData(current.getVisitDate(), current.getStoreId().toString());
                    if (fortempdata) {
                        if (specdata.size() == 0) {
                            Intent in = new Intent(StoreListActivity.this, StoreimageActivity.class);
                            in.putExtra(CommonString.KEY_JOURNEY_PLAN_OBJECT, (Serializable) current);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    } else {
                        if (specdata.size() == 0 && !isVisitLater) {
                            Intent in = new Intent(StoreListActivity.this, StoreimageActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else if (!isVisitLater) {
                            if (current.getStoreProfile().toString().equals("1")) {
                                Intent in = new Intent(StoreListActivity.this, StoreProfileActivity.class);
                                startActivity(in);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            } else {
                                Intent in = new Intent(StoreListActivity.this, StoreProfileSecondActivity.class);
                                startActivity(in);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }

                        } else {
                            Intent in = new Intent(StoreListActivity.this, StoreimageActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    }
                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    if (fortempdata) {
                        if (current.getUploadStatus().equals(CommonString.KEY_CHECK_IN)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                            builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE).setCancelable(false)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                deletecoverageData(StoreListActivity.this, current, fortempdata);
                                            } catch (Exception e) {
                                                Crashlytics.logException(e);
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            UpdateData(current.getStoreId().toString(), current.getVisitDate());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                            editor.putString(CommonString.KEY_STORE_ADDRESS, current.getAddress1().toString());
                            editor.commit();
                            Intent in = new Intent(StoreListActivity.this, NonWorkingReasonActivity.class);
                            in.putExtra(CommonString.KEY_JOURNEY_PLAN_OBJECT, (Serializable) current);
                            startActivity(in);
                            StoreListActivity.this.finish();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

                        }
                    } else {
                        if (current.getUploadStatus().equals(CommonString.KEY_CHECK_IN)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                            builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE).setCancelable(false)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                deletecoverageData(StoreListActivity.this, current, false);
                                            } catch (Exception e) {
                                                Crashlytics.logException(e);
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            UpdateData(current.getStoreId().toString(), current.getVisitDate());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                            editor.putString(CommonString.KEY_STORE_ADDRESS, current.getAddress1().toString());
                            editor.commit();
                            Intent in = new Intent(StoreListActivity.this, NonWorkingReasonActivity.class);
                            startActivity(in);
                            StoreListActivity.this.finish();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        }
                    }
                }
            }

        });
        dialog.show();
    }

    public void UpdateData(String storeCd, String visit_date) {
        db.open();
        db.deleteSpecificStoreData(storeCd);
        db.updateJaurneyPlanSpecificStoreStatus(storeCd, visit_date, "N");
    }


    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        linearlay = (LinearLayout) findViewById(R.id.no_data_lay);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        ///for
        card = (CardView) findViewById(R.id.card);
        city_spin = (Spinner) findViewById(R.id.city_spin);
        storeTypeSpin = (Spinner) findViewById(R.id.storeTypeSpin);

        btn_searchCity = (Button) findViewById(R.id.btn_searchCity);
        txt_nodata = findViewById(R.id.txt_nodata);

        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        context = this;
        getSupportActionBar().setTitle("Store List - " + date);
        locationEnableCommon = new LocationEnableCommon();
        locationEnableCommon.checkgpsEnableDevice(this);
        db = new BOSCH_DB(this);
        db.open();

        //for spinnerrrrr cityyyyyyyyy
        cityMasterArrayList = db.getcitymasterList(date);

        city_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
        if (cityMasterArrayList.size() > 0) {
            CityMaster ch = new CityMaster();
            ch.setCityId(0);
            ch.setCity("-Select City-");
            cityMasterArrayList.add(0, ch);
            for (int i = 0; i < cityMasterArrayList.size(); i++) {
                city_adapter.add(cityMasterArrayList.get(i).getCity());
            }

            city_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            city_spin.setAdapter(city_adapter);
            city_spin.setOnItemSelectedListener(this);

            db.open();
            storeTypeList = db.getstoreTypeList();
            storeTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
            if (storeTypeList.size() > 0) {
                StoreTypeMaster storeTypeMaster = new StoreTypeMaster();
                storeTypeMaster.setStoreTypeId(-1);
                storeTypeMaster.setStoreType("-Select Store Type-");
                storeTypeList.add(0, storeTypeMaster);

                storeTypeMaster = new StoreTypeMaster();
                storeTypeMaster.setStoreTypeId(0);
                storeTypeMaster.setStoreType("All");
                storeTypeList.add(1, storeTypeMaster);
                for (int i = 0; i < storeTypeList.size(); i++) {
                    storeTypeAdapter.add(storeTypeList.get(i).getStoreType());
                }

                storeTypeAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
                storeTypeSpin.setAdapter(storeTypeAdapter);
                storeTypeSpin.setOnItemSelectedListener(this);
                btn_searchCity.setOnClickListener(this);
            }
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 500;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FATEST_INTERVAL = 100;
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 5;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    private boolean chekDataforCheckout(String store_cd, String State_Id, String Storetype_Id, String GeotagStatus, String visit_date) {
        boolean status = false;
        boolean posm, geo_Tag, comptition,competition_financial_offer,competition_offer;
        ///for posm
        if (posmmasterData(store_cd, State_Id, Storetype_Id)) {
            posm = true;
        } else {
            posm = false;
        }

        //for geotag
        db.open();
        if (db.getinsertGeotaggingData(store_cd, null).size() > 0) {
            db.open();
            if (db.getinsertGeotaggingData(store_cd, null).get(0).getStatus()
                    .equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                geo_Tag = true;
            } else {
                geo_Tag = false;
            }
        } else {
            if (GeotagStatus.equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                geo_Tag = true;
            } else {
                geo_Tag = false;
            }
        }

        ///for comptition data
        db.open();
        if (db.getboschcompetitionfromdatabase(store_cd, visit_date,"").size() > 0) {
            comptition = true;
        } else {
            comptition = false;
        }
        ///for competition offer data
        if (db.getcategoryMasterforCompetitionList().size()>0){
            if (db.getinsertedcompetitionOfferData(store_cd,visit_date).size()>0){
                competition_offer=true;
            }else {
                competition_offer=false;
            }
        }else {
            competition_offer=true;
        }
  ///for competition offer data
        if (db.getcategoryMasterforCompetitionList().size()>0){
            if (db.getinsertedcompetitionFinancialOfferData(store_cd,visit_date).size()>0){
                competition_financial_offer=true;
            }else {
                competition_financial_offer=false;
            }
        }else {
            competition_financial_offer=true;
        }


        ///for all completed data condition flagssssssss
        if (posm && geo_Tag && comptition && competition_offer && competition_financial_offer) {
            status = true;
        }

        return status;
    }

    private void deletecoverageData(final Context context, final JourneyPlan current, final boolean fortempflag) {
        try {
            loading = ProgressDialog.show(StoreListActivity.this, "Processing", "Please wait...",
                    false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("StoreId", current.getStoreId().toString());
            jsonObject.put("VisitDate", current.getVisitDate());
            jsonObject.put("UserId", userId);
            String jsonString2 = jsonObject.toString();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString2);
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call = api.deleteCoverageData(jsonData);
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (data.contains(CommonString.KEY_SUCCESS)) {
                                UpdateData(current.getStoreId().toString(), current.getVisitDate());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                                editor.commit();
                                if (fortempflag) {
                                    Intent in = new Intent(StoreListActivity.this, NonWorkingReasonActivity.class);
                                    in.putExtra(CommonString.KEY_JOURNEY_PLAN_OBJECT, (Serializable) current);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    StoreListActivity.this.finish();
                                } else {
                                    Intent intent = new Intent((Activity) context, NonWorkingReasonActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    StoreListActivity.this.finish();
                                }
                                loading.dismiss();
                            } else {
                                loading.dismiss();
                                AlertandMessages.showAlertlogin((Activity) context, data.toString());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.dismiss();
                            AlertandMessages.showAlertlogin((Activity) context, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                        AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
                    } else {
                        AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
                    }
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            loading.dismiss();
            AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
        }
    }


    boolean posmmasterData(String store_cd, String state_Id, String storeType_Id) {
        boolean Status = false;
        db.open();
        ArrayList<CategoryMaster> categoryList = db.getcategotyListforposm();
        if (categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                db.open();
                if (db.getposmHeaderData(categoryList.get(i).getCategoryId().toString(), state_Id, storeType_Id).size() > 0) {
                    if (db.Isposminseteddata(store_cd, categoryList.get(i).getCategoryId().toString())) {
                        Status = true;
                    } else {
                        Status = false;
                        break;
                    }
                }
            }
        }
        return Status;
    }


    private void downloadJourneyPlanData(final Context context, final String userId,
                                         final String city_Id, final String storeTypeId) {
        loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Downloadtype", CommonString.KEY_JOURNEY_PLANSEARCH);
            jsonObject.put("Username", city_Id + ":" + userId + ":" + storeTypeId);
            String jsonString = jsonObject.toString();
            try {
                final String[] data_global = {""};
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();

                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                retrofit_Adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                PostApi api = retrofit_Adapter.create(PostApi.class);
                Call<String> call = api.getDownloadAll(jsonData);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body();
                                if (data.equals("")) {
                                    data_global[0] = "";
                                } else {
                                    if (!data.contains("No Data")) {
                                        final JCPGetterSetter jcpObject = new Gson().fromJson(data, JCPGetterSetter.class);
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("Downloadtype", CommonString.KEY_STORE_PROFILE_BRANDS);
                                            jsonObject.put("Username", city_Id + ":" + userId);
                                            String jsonString = jsonObject.toString();
                                            final String[] data_global = {""};
                                            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
                                            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                                            retrofit_Adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
                                            PostApi api = retrofit_Adapter.create(PostApi.class);
                                            call = api.getDownloadAll(jsonData);
                                            call.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    String responseBody = response.body();
                                                    String data = null;
                                                    if (responseBody != null && response.isSuccessful()) {
                                                        data = response.body();
                                                        if (data.equals("")) {
                                                            data_global[0] = "";
                                                        } else {
                                                            if (!data.contains("No Data")) {
                                                                final ProfileLastVisitGetterSetter profileBrandList = new Gson().fromJson(data, ProfileLastVisitGetterSetter.class);
                                                                if (profileBrandList != null && !db.insertprofileBrandList(profileBrandList)) {

                                                                }
                                                            }
                                                        }

                                                        if (jcpObject != null) {
                                                            List<JourneyPlan> jcpList = jcpObject.getJourneyPlan();
                                                            searchStorePopup(context, jcpList);
                                                            loading.dismiss();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    loading.dismiss();
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        loading.dismiss();
                                        throw new java.lang.Exception();
                                    }
                                }
                            } catch (Exception e) {
                                loading.dismiss();
                                AlertandMessages.showAlertlogin((Activity) context, CommonString.KEY_JOURNEY_PLANSEARCH + " Data not found for '" + city_name + "' and '" + storeTypeName + "' Store Type.");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loading.dismiss();
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE);
                        }
                    }
                });

            } catch (Exception e) {
                Crashlytics.logException(e);
                loading.dismiss();
                e.printStackTrace();
                AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");
            }
        } catch (JSONException e) {
            loading.dismiss();
            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INVALID_JSON);
        }
    }

    private void searchStorePopup(Context context, List<JourneyPlan> items) {
        final Dialog popup_dialog = new Dialog(context);
        popup_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_dialog.setContentView(R.layout.search_store_popup);
        final RecyclerView dialogRecycler = (RecyclerView) popup_dialog.findViewById(R.id.dialog_list);
        TextView storelist_date = popup_dialog.findViewById(R.id.storelist_date);
        storelist_date.setText("Store List  - " + date);
        popup_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = popup_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        popup_dialog.setCanceledOnTouchOutside(true);
        popup_dialog.show();
        if (items.size() > 0) {
            dialogRecycler.setAdapter(new DialogAdapter(context, items, popup_dialog));
            dialogRecycler.setLayoutManager(new LinearLayoutManager(context));
        }
    }


    public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<JourneyPlan> data = Collections.emptyList();
        Dialog popup_dialog;

        public DialogAdapter(Context context, List<JourneyPlan> data, final Dialog popup_dialog) {
            inflator = LayoutInflater.from(context);
            this.data = data;
            this.popup_dialog = popup_dialog;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final JourneyPlan current = data.get(position);
            viewHolder.txt.setText(current.getStoreName() + " - " + current.getStoreType() + " ( ID : " + current.getStoreId() + ")");
            viewHolder.address.setText(current.getAddress1() + " - " + current.getCity());
            if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_u);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.VISIBLE);

            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_d);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.VISIBLE);

            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                viewHolder.imageview.setBackgroundResource(R.mipmap.tick);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.VISIBLE);

            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                viewHolder.imageview.setBackgroundResource(R.drawable.leave_tick);
                viewHolder.imageview.setVisibility(View.VISIBLE);
            } else {
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.GONE);
            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        Snackbar.make(v, R.string.title_store_list_activity_already_store_closed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        boolean showdialog = false, fortempdata = true;
                        popup_dialog.dismiss();
                        showMyDialog(current, showdialog, fortempdata);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, address;
            RelativeLayout relativelayout;
            ImageView imageview;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = itemView.findViewById(R.id.storelistviewxml_storename);
                address = itemView.findViewById(R.id.storelistviewxml_storeaddress);
                relativelayout = itemView.findViewById(R.id.storenamelistview_layout);
                imageview = itemView.findViewById(R.id.storelistviewxml_storeico);
                Cardbtn = itemView.findViewById(R.id.card_view);
            }
        }
    }

    private void conditionforspin(ArrayList<JourneyPlan> object) {
        int city_Id = 0, storeTypeId = 0;
        if (object.size() > 0) {
            for (int i = 0; i < object.size(); i++) {
                if (object.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                    city_Id = object.get(i).getCityId();
                    storeTypeId = object.get(i).getStoreTypeId();
                    break;
                }
            }

            ///for spinnerrrrrrrrrrr
            for (int k = 0; k < cityMasterArrayList.size(); k++) {
                if (city_Id != 0 && city_Id == cityMasterArrayList.get(k).getCityId()) {
                    city_spin.setSelection(k);
                    btn_searchCity.setEnabled(false);
                    city_spin.setEnabled(false);
                    card.setEnabled(false);
                    break;
                } else {
                    city_spin.setSelection(0);
                    btn_searchCity.setEnabled(true);
                    city_spin.setEnabled(true);
                    card.setEnabled(true);
                }
            }

            ///for store type
            for (int k = 0; k < storeTypeList.size(); k++) {
                if (storeTypeId != 0 && storeTypeId == storeTypeList.get(k).getStoreTypeId()) {
                    storeTypeSpin.setSelection(k);
                    btn_searchCity.setEnabled(false);
                    storeTypeSpin.setEnabled(false);
                    card.setEnabled(false);
                    break;
                } else {
                    storeTypeSpin.setSelection(0);
                    btn_searchCity.setEnabled(true);
                    storeTypeSpin.setEnabled(true);
                    card.setEnabled(true);
                }
            }
        }
    }
}

