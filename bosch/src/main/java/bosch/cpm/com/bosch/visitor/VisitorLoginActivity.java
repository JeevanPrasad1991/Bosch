package bosch.cpm.com.bosch.visitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MultipartBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.AlertandMessages;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.dailyentry.StoreimageActivity;
import bosch.cpm.com.bosch.gsonGetterSetter.VisitorDetailGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.VisitorSearchGetterSetter;
import bosch.cpm.com.bosch.retrofit.PostApi;
import bosch.cpm.com.bosch.retrofit.PostApiForUpload;
import bosch.cpm.com.bosch.retrofit.StringConverterFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static bosch.cpm.com.bosch.upload.UploadDataActivity.saveBitmapToFile;

public class VisitorLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private BOSCH_DB database;
    LinearLayout heading;
    FloatingActionButton fab_save;
    RecyclerView recyclerView;
    VisitorDetailGetterSetter visitorLoginGetterSetter;
    ArrayList<VisitorDetailGetterSetter> visitorLoginData = new ArrayList<>();
    TextView tv_in_time, tv_out_time;
    String empid, emp_name, name, designation;
    boolean isUpdate = false;
    String intime_img, outtime_img;
    TextView tvname, tvdesignation;
    String error_msg = "";
    String _pathforcheck = "", _pathforcheck2 = "";
    protected String _path, str;
    Activity activity;
    ProgressBar progressBar;
    String visit_date, username;
    RelativeLayout rel_intime, rel_outtime;
    ImageView img_intime, img_outtime;
    SharedPreferences preferences;
    String Path;
    ImageView imgcam_in, imgcam_out;
    Context context;
    Button btnclear_saved_data, btngo, btncleardata;
    ArrayList<VisitorSearchGetterSetter> visitordata = new ArrayList<>();
    EditText et_emp_name;
    TextView tv_user_id;
    RecyclerAdapter recyclerAdapter;
    String visitorFolderName = "VisitorImages";
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        declaration();
    }

    void declaration() {
        activity = this;
        context = this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        btngo = (Button) findViewById(R.id.btngo);
        btnclear_saved_data = (Button) findViewById(R.id.btnClear_saved);
        btncleardata = (Button) findViewById(R.id.btncleardata);
        et_emp_name = (EditText) findViewById(R.id.et_emp_name);
        tvname = (TextView) findViewById(R.id.tv_name);
        tvdesignation = (TextView) findViewById(R.id.tv_designation);
        imgcam_in = (ImageView) findViewById(R.id.imgcam_intime);
        imgcam_out = (ImageView) findViewById(R.id.imgcam_outtime);
        tv_in_time = (TextView) findViewById(R.id.tvintime);
        tv_out_time = (TextView) findViewById(R.id.tvouttime);
        rel_intime = (RelativeLayout) findViewById(R.id.rel_intime);
        rel_outtime = (RelativeLayout) findViewById(R.id.rel_outtime);
        fab_save = (FloatingActionButton) findViewById(R.id.fab);
        img_intime = (ImageView) findViewById(R.id.img_intime);
        img_outtime = (ImageView) findViewById(R.id.img_outtime);
        progressBar = (ProgressBar) findViewById(R.id.progress_empid);
        heading = (LinearLayout) findViewById(R.id.lay_heading);
        recyclerView = (RecyclerView) findViewById(R.id.rv_visitor);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        context = this;
        database = new BOSCH_DB(context);
        database.open();
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        tv_user_id.setText("Current User - " + username);
        str = CommonString.FILE_PATH;
        setTitle("Visitor Login - " + visit_date);
        fab_save.setOnClickListener(this);
        rel_intime.setOnClickListener(this);
        rel_outtime.setOnClickListener(this);
        btnclear_saved_data.setOnClickListener(this);
        btncleardata.setOnClickListener(this);
        btngo.setOnClickListener(this);
        Path = CommonString.FILE_PATH;

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fab:
                name = tvname.getText().toString();
                designation = tvdesignation.getText().toString();
                if (check_condition()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorLoginActivity.this).setTitle("Parinaam");
                    builder.setMessage("Are you sure you want to save data ?").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isUpdate) {
                                String out_time = tv_out_time.getText().toString();
                                visitorLoginGetterSetter.setOut_time(out_time);
                                database.updateOutTimeVisitorLoginData(visitorLoginGetterSetter.getOut_time_img(), out_time, String.valueOf(visitorLoginGetterSetter.getEmpId()), visit_date);
                                if (CheckNetAvailability()) {
                                    jsonData();
                                } else {
                                    AlertandMessages.showAlertlogin((Activity) context, "No internet connection! try again later.");
                                    clearVisitorData();
                                    setLoginData();
                                }
                            } else {
                                et_emp_name.setEnabled(true);
                                et_emp_name.setFocusable(true);
                                visitorLoginGetterSetter = new VisitorDetailGetterSetter();
                                visitorLoginGetterSetter.setEmpId(Integer.valueOf(empid));
                                visitorLoginGetterSetter.setName(name);
                                visitorLoginGetterSetter.setDesignation(designation);
                                visitorLoginGetterSetter.setVisit_date(visit_date);
                                visitorLoginGetterSetter.setIn_time_img(intime_img);
                                visitorLoginGetterSetter.setIn_time(tv_in_time.getText().toString());
                                String out_time = tv_out_time.getText().toString();
                                if (out_time.equalsIgnoreCase("Out Time")) {
                                    visitorLoginGetterSetter.setOut_time("");
                                } else {
                                    visitorLoginGetterSetter.setOut_time(out_time);
                                }
                                visitorLoginGetterSetter.setEmp_code(emp_name);
                                if (outtime_img == null) {
                                    visitorLoginGetterSetter.setOut_time_img("");
                                } else {
                                    visitorLoginGetterSetter.setOut_time_img(outtime_img);
                                }
                                visitorLoginData.add(visitorLoginGetterSetter);
                                database.open();
                                database.InsertVisitorLogindata(visitorLoginData);
                                clearVisitorData();
                                setLoginData();
                            }

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (error_msg.equals("Employee ID already entered.")) {
                        clearVisitorData();
                    }
                    AlertandMessages.showAlertlogin((Activity) context, error_msg);
                }

                break;
            case R.id.rel_intime:
                if (et_emp_name.getText().toString() != null && !et_emp_name.getText().toString().equals("")) {
                    _pathforcheck = empid + "_" + username + "_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + "_visitor_intime" + ".jpg";
                    _path = str + _pathforcheck;
                    startCameraActivity();
                } else {
                    AlertandMessages.showAlertlogin((Activity) context, "Please fill employee code first.");
                }
                break;

            case R.id.rel_outtime:
                if (!isUpdate) {
                    error_msg = "Please click Out Time image at out time.";
                    AlertandMessages.showAlertlogin((Activity) context, error_msg);
                } else {
                    _pathforcheck2 = empid + "_" + username + "_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + "_visitor_outtime" + ".jpg";
                    _path = str + _pathforcheck2;
                    empid = null;
                    startCameraActivity();
                }
                break;
            case R.id.btngo:
                emp_name = et_emp_name.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", "");
                if (emp_name.equals("")) {
                    AlertandMessages.showAlertlogin((Activity) context, "Please fill employee code first.");
                } else if (CheckNetAvailability()) {
                    hideKeyboardFrom(context, btngo);
                    GetCredentials(emp_name);
                } else {
                    AlertandMessages.showAlertlogin((Activity) context, "No internet connection! try again later.");
                }
                break;

            case R.id.btnClear_saved:
                clearVisitorData();
                break;

            case R.id.btncleardata:
                clearVisitorData();

                break;
        }
    }

    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void GetCredentials(String emp_name) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Downloadtype", "Visitor_Detail");
            jsonObject.put("Username", emp_name);
            loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<String> call = api.getDownloadAll(jsonData);
            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                    String responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body();
                            if (data.equals("")) {
                                AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
                            } else {
                                if (!data.contains("No Data")) {
                                    VisitorSearchGetterSetter visitorSearchObject = new Gson().fromJson(data, VisitorSearchGetterSetter.class);
                                    if (visitorSearchObject != null) {
                                        visitordata = (ArrayList<VisitorSearchGetterSetter>) visitorSearchObject.getVisitorDetail();
                                        tvname.setText(visitordata.get(0).getName());
                                        tvdesignation.setText(visitordata.get(0).getDesignation());
                                        empid = String.valueOf(visitordata.get(0).getEmpId().toString());
                                        name = visitordata.get(0).getName();
                                        designation = visitordata.get(0).getDesignation();
                                        btngo.setVisibility(View.GONE);
                                        btncleardata.setVisibility(View.VISIBLE);
                                    } else {
                                        AlertandMessages.showAlertlogin((Activity) context, "Please enter valid Employee Code.");
                                        et_emp_name.setText("");
                                        et_emp_name.setHint("Employee Code");
                                    }
                                } else {
                                    AlertandMessages.showAlertlogin((Activity) context, "No data found for this employee.");
                                    et_emp_name.setText("");
                                    et_emp_name.setHint("Employee Code");
                                }

                            }
                            loading.dismiss();

                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + e.toString());
                            loading.dismiss();
                        }
                    }

                }

                @Override
                public void onFailure(retrofit2.Call<String> call, Throwable t) {
                    loading.dismiss();
                    AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + e.toString());
            loading.dismiss();
        }
    }


    public void clearVisitorData() {
        et_emp_name.setText("");
        tvname.setText("");
        tvdesignation.setText("");
        tv_in_time.setText("");
        tv_out_time.setText("");
        img_intime.setVisibility(View.GONE);
        img_outtime.setVisibility(View.GONE);

        rel_intime.setVisibility(View.VISIBLE);
        rel_outtime.setVisibility(View.VISIBLE);
        empid = null;
        name = null;
        designation = null;
        intime_img = null;
        outtime_img = null;
        fab_save.setClickable(true);
        btngo.setVisibility(View.VISIBLE);
        fab_save.setVisibility(View.VISIBLE);
        rel_intime.setClickable(true);
        rel_outtime.setClickable(true);
        btncleardata.setVisibility(View.GONE);
        btnclear_saved_data.setVisibility(View.GONE);
        et_emp_name.setFocusable(true);
        isUpdate = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    try {
                        if (new File(str + _pathforcheck).exists()) {
                            Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck);
                            Bitmap dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                            Canvas cs = new Canvas(dest);
                            Paint tPaint = new Paint();
                            tPaint.setTextSize(70);
                            tPaint.setColor(Color.RED);
                            tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            cs.drawBitmap(bmp, 0f, 0f, null);
                            float height = tPaint.measureText("yY");
                            cs.drawText(dateTime, 20f, height + 15f, tPaint);
                            try {
                                dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(str + _pathforcheck)));
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            BitmapFactory.decodeFile(str + _pathforcheck);
                            intime_img = _pathforcheck;
                            tv_in_time.setText(getCurrentTimeforimage());
                            _pathforcheck = "";
                            rel_intime.setVisibility(View.GONE);
                            rel_intime.setClickable(false);
                            setScaledImage(img_intime, _path);
                            img_intime.setVisibility(View.VISIBLE);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
                if (_pathforcheck2 != null && !_pathforcheck2.equals("")) {
                    try {
                        if (new File(str + _pathforcheck2).exists()) {
                            Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck2);
                            Bitmap dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                            Canvas cs = new Canvas(dest);
                            Paint tPaint = new Paint();
                            tPaint.setTextSize(70);
                            tPaint.setColor(Color.RED);
                            tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            cs.drawBitmap(bmp, 0f, 0f, null);
                            float height = tPaint.measureText("yY");
                            cs.drawText(dateTime, 20f, height + 15f, tPaint);
                            try {
                                dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(str + _pathforcheck2)));
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            BitmapFactory.decodeFile(str + _pathforcheck2);

                            visitorLoginGetterSetter.setOut_time_img(_pathforcheck2);
                            tv_out_time.setText(getCurrentTimeforimage());
                            _pathforcheck2 = "";
                            rel_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(false);
                            img_outtime.setVisibility(View.VISIBLE);
                            setScaledImage(img_outtime, _path);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }

                break;
        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String hours = String.valueOf(m_cal.get(Calendar.HOUR_OF_DAY));
        String minutes = String.valueOf(m_cal.get(Calendar.MINUTE));
        String seconds = String.valueOf(m_cal.get(Calendar.SECOND));

        if (hours.toString().length() == 1) {
            hours = "0" + hours;
        }
        if (minutes.toString().length() == 1) {
            minutes = "0" + minutes;
        }
        if (seconds.toString().length() == 1) {
            seconds = "0" + seconds;
        }
        String intime = hours + "" + minutes + "" + seconds;
        return intime;
    }

    public String getCurrentTimeforimage() {
        Calendar m_cal = Calendar.getInstance();
        String hours = String.valueOf(m_cal.get(Calendar.HOUR_OF_DAY));
        String minutes = String.valueOf(m_cal.get(Calendar.MINUTE));
        String seconds = String.valueOf(m_cal.get(Calendar.SECOND));

        if (hours.toString().length() == 1) {
            hours = "0" + hours;
        }
        if (minutes.toString().length() == 1) {
            minutes = "0" + minutes;
        }
        if (seconds.toString().length() == 1) {
            seconds = "0" + seconds;
        }
        String intime = hours + ":" + minutes + ":" + seconds;
        return intime;
    }


    private void setScaledImage(ImageView imageView, final String path) {
        final ImageView iv = imageView;
        ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = iv.getMeasuredHeight();
                int imageViewWidth = iv.getMeasuredWidth();
                iv.setImageBitmap(decodeSampledBitmapFromPath(path, imageViewWidth, imageViewHeight));
                return true;
            }
        });
    }

    private static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public boolean check_condition() {
        if (isUpdate) {
            if (visitorLoginGetterSetter.getOut_time_img().equals("")) {
                error_msg = "Please click out time image.";
                return false;
            } else {
                return true;
            }
        } else {
            if (empid == null || name == null || designation == null) {
                error_msg = "Please Select Employee.";
                return false;
            } else if (intime_img == null) {
                error_msg = "Please click in time image.";
                return false;
            } else if (database.isVistorDataExists(empid, visit_date)) {
                error_msg = "Employee already entered.";
                return false;
            } else {
                return true;
            }
        }
    }

    public void setLoginData() {
        visitorLoginData = database.getVisitorLoginData(visit_date);
        if (visitorLoginData.size() > 0) {
            heading.setVisibility(View.VISIBLE);
            recyclerAdapter = new RecyclerAdapter(context);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            heading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        Context context;
        LayoutInflater inflater;

        RecyclerAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.child_visitor_login, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tv_name.setText(visitorLoginData.get(position).getName());
            holder.tv_intime.setText(visitorLoginData.get(position).getIn_time());
            holder.tv_outtime.setText(visitorLoginData.get(position).getOut_time());
            if (visitorLoginData.get(position).getUpload_status() != null && visitorLoginData.get(position).getUpload_status().equals("U")) {
                holder.img_upload_tick.setVisibility(View.VISIBLE);
            } else {
                holder.img_upload_tick.setVisibility(View.INVISIBLE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    visitorLoginGetterSetter = visitorLoginData.get(position);
                    if (visitorLoginGetterSetter.getUpload_status() != null && visitorLoginGetterSetter.getUpload_status().equalsIgnoreCase("U")) {
                        AlertandMessages.showAlertlogin((Activity) context, "Data already uploaded.");
                    } else {
                        btnclear_saved_data.setVisibility(View.VISIBLE);
                        tvname.setText(visitorLoginGetterSetter.getName());
                        tvdesignation.setText(visitorLoginGetterSetter.getDesignation());
                        et_emp_name.setText(visitorLoginGetterSetter.getEmp_code());
                        tv_in_time.setText(visitorLoginGetterSetter.getIn_time());
                        et_emp_name.setFocusable(true);
                        String intime_img = visitorLoginGetterSetter.getIn_time_img();
                        if (intime_img != null && !intime_img.equals("")) {
                            rel_intime.setVisibility(View.GONE);
                            rel_intime.setClickable(false);
                            setScaledImage(img_intime, str + intime_img);
                            img_intime.setVisibility(View.VISIBLE);
                        }

                        String outtime_img = visitorLoginGetterSetter.getOut_time_img();
                        if (outtime_img != null && !outtime_img.equals("")) {
                            rel_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(false);
                            tv_out_time.setText(visitorLoginGetterSetter.getOut_time());
                            setScaledImage(img_outtime, str + outtime_img);
                            img_outtime.setVisibility(View.VISIBLE);
                        } else {
                            tv_out_time.setText(visitorLoginGetterSetter.getOut_time());
                            rel_outtime.setVisibility(View.VISIBLE);
                            img_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(true);
                        }

                        if (!visitorLoginGetterSetter.getOut_time().equals("")) {
                            fab_save.setClickable(true);
                            fab_save.setVisibility(View.VISIBLE);
                        }
                        empid = String.valueOf(visitorLoginGetterSetter.getEmpId());
                        isUpdate = true;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return visitorLoginData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;
            TextView tv_intime;
            TextView tv_outtime;
            LinearLayout layout;
            ImageView img_upload_tick;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_intime = (TextView) itemView.findViewById(R.id.tv_intime);
                tv_outtime = (TextView) itemView.findViewById(R.id.tv_outtime);
                layout = (LinearLayout) itemView.findViewById(R.id.ll_item);
                img_upload_tick = (ImageView) itemView.findViewById(R.id.img_upload_tick);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setLoginData();
    }


    //upload Visitor data
    private void jsonData() {
        try {
            if (visitorLoginGetterSetter.getEmp_code() != null) {
                JSONArray topUpArray = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("Created_By", username);
                obj.put("Visit_Date", visit_date);
                obj.put("Emp_Id", visitorLoginGetterSetter.getEmpId());
                obj.put("InTime_Image", visitorLoginGetterSetter.getIn_time_img());
                obj.put("OutTime_Image", visitorLoginGetterSetter.getOut_time_img());
                obj.put("InTime", visitorLoginGetterSetter.getIn_time());
                obj.put("OutTime", visitorLoginGetterSetter.getOut_time());
                topUpArray.put(obj);


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MID", "0");
                jsonObject.put("Keys", "Mer_Visitor_Data");
                jsonObject.put("JsonData", topUpArray.toString());
                jsonObject.put("UserId", username);
                String jsonString2 = jsonObject.toString();
                uploadvisitorDetailData(jsonString2);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    protected void uploadvisitorDetailData(String jsonString) {
        try {
            loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            Retrofit adapter;
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<JsonObject> call = api.getGeotag(jsonData);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    String responseBody = response.body().get("UploadJsonDetailResult").toString();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().get("UploadJsonDetailResult").toString();
                            if (data.equals("")) {
                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                if (data.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    database.updateVisitorUploadData(visitorLoginGetterSetter.getEmpId().toString(), visit_date);
                                    clearVisitorData();
                                    setLoginData();
                                    AlertandMessages.showAlertlogin((Activity) context, "Visitor Data Upload Successfully.");
                                    loading.dismiss();
                                } else {
                                    AlertandMessages.showAlertlogin((Activity) context, "Data not uploaded.");
                                    loading.dismiss();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
                            loading.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (t != null && t instanceof SocketTimeoutException || t != null && t instanceof IOException || t != null && t instanceof Exception) {
                        AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + t.toString() + ")");
                    } else {
                        AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                    loading.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
            loading.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            VisitorLoginActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        VisitorLoginActivity.this.finish();
    }



    public boolean uploadImageRecursiveWithIndex(final String filelist) {
        final boolean[] status = {true};
        File originalFile = new File(CommonString.FILE_PATH + filelist);
        final File finalFile = saveBitmapToFile(originalFile);

        String date = visit_date.replace("/", "");
        com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);

        com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
        com.squareup.okhttp.RequestBody body1 = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("file", finalFile.getName(), photo).addFormDataPart("FolderName", visitorFolderName).addFormDataPart("Path", date).build();
        retrofit.Retrofit adapter = new retrofit.Retrofit.Builder().baseUrl(CommonString.URLGORIMAG).client(okHttpClient).addConverterFactory(new StringConverterFactory()).build();
        PostApiForUpload api = adapter.create(PostApiForUpload.class);

        retrofit.Call<String> call = api.getUploadImageRetrofitOne(body1);

        call.enqueue(new retrofit.Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response) {
                if (response.isSuccess() && response.body().contains("Success") && response.code() == 200) {
                    finalFile.delete();
                } else {
                    status[0] = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                    status[0] = false;
                }
            }
        });

        return status[0];
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
