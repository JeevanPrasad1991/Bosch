package bosch.cpm.com.bosch.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.AlertandMessages;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreCategoryMaster;
import bosch.cpm.com.bosch.multiselectionspin.MultiSpinnerSearch;
import bosch.cpm.com.bosch.multiselectionspin.SpinnerListener;

public class StoreProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<BrandMaster> selectedProfileList = new ArrayList<>();
    ArrayList<BrandMaster> brandList = new ArrayList<>();
    private static final String TAG = StoreProfileActivity.class.getSimpleName();
    SharedPreferences preferences;
    String visit_date, userId, user_type, store_cd, store_N, _path, store_address, _pathforcheck = "", image1 = "", profileValue = "";
    TextView storeProfileN, storeProfile_addres;
    BrandMaster store_profile = new BrandMaster();
    String[] storeprofile_array = {"-Select-", "Store", "Brand"};
    FloatingActionButton btn_save, btn_right;
    boolean update_flag = false, spinner_touch = false;
    LinearLayout RLPL_IMG, RLPL_spinselectedV;
    ImageView storep_img, edt_img;
    Spinner spin_strbrd;
    Context context;
    BOSCH_DB db;
    //
    private ArrayAdapter storep_adapter;
    private int selected = 0;
    private String defaultText = "";
    TextView selcted_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        uidata();
    }

    void uidata() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storeProfileN = findViewById(R.id.storeProfileN);
        storeProfile_addres = findViewById(R.id.storeProfile_addres);
        btn_save = findViewById(R.id.btn_save);
        btn_right = findViewById(R.id.btn_right);

        RLPL_IMG = findViewById(R.id.RLPL_IMG);
        storep_img = findViewById(R.id.storep_img);
        spin_strbrd = findViewById(R.id.spin_strbrd);
        selcted_text = findViewById(R.id.selcted_text);
        RLPL_spinselectedV = findViewById(R.id.RLPL_spinselectedV);
        edt_img = findViewById(R.id.edt_img);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        store_N = preferences.getString(CommonString.KEY_STORE_NAME, "");
        store_address = preferences.getString(CommonString.KEY_STORE_ADDRESS, "");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Store Profile - " + visit_date);
        context = this;
        db = new BOSCH_DB(context);
        db.open();

        //for spinner
        storep_adapter = new ArrayAdapter(context, R.layout.spinner_custom_item, storeprofile_array);
        storep_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        spin_strbrd.setAdapter(storep_adapter);
        db.open();
        store_profile = db.getstoreProfileData(store_cd, visit_date);
        if (!store_profile.getSpin_value().equals("")) {
            if (store_profile.getSpin_value().equalsIgnoreCase("Store")) {
                spin_strbrd.setSelection(1);
            } else {
                spin_strbrd.setSelection(2);
            }
            if (!store_profile.getProfile_img().equals("")) {
                update_flag = true;
                RLPL_spinselectedV.setVisibility(View.GONE);
                RLPL_IMG.setVisibility(View.VISIBLE);
                storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera_green));
                image1 = store_profile.getProfile_img();
                brandList = db.getbrandMasterData();
            } else {
                RLPL_spinselectedV.setVisibility(View.VISIBLE);
                RLPL_IMG.setVisibility(View.GONE);
                brandList = db.getstoreProfileBrandData(store_cd);
                storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
                setspinValue();
                update_flag = true;
            }
            btn_save.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
            btn_right.setVisibility(View.VISIBLE);
        } else if (!db.getstoreProfileServerdata(store_cd).getSpin_value().equals("")) {
            calculateUploadedData();
        } else {
            brandList = db.getbrandMasterData();
            btn_right.setVisibility(View.GONE);
            setspinValue();
        }

        btn_save.setOnClickListener(this);
        storep_img.setOnClickListener(this);
        edt_img.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        storeProfileN.setText(store_N);
        storeProfile_addres.setText(store_address);


        spin_strbrd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Real touch felt.");
                spinner_touch = true;
                return false;
            }
        });

        spin_strbrd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner_touch) {
                    btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save_icon));
                    btn_right.setVisibility(View.GONE);
                    if (position != 0) {
                        profileValue = parent.getSelectedItem().toString();
                        if (parent.getSelectedItemPosition() == 2) {
                            storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
                            RLPL_spinselectedV.setVisibility(View.VISIBLE);
                            formultiselection(brandList, true);
                            RLPL_IMG.setVisibility(View.GONE);
                            image1 = "";
                        } else {
                            update_flag = false;
                            selectedProfileList.clear();
                            db.open();
                            brandList = db.getbrandMasterData();
                            RLPL_spinselectedV.setVisibility(View.GONE);
                            RLPL_IMG.setVisibility(View.VISIBLE);
                        }
                    } else {
                        selectedProfileList.clear();
                        RLPL_spinselectedV.setVisibility(View.GONE);
                        RLPL_IMG.setVisibility(View.GONE);
                        storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
                        btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save_icon));
                        btn_right.setVisibility(View.GONE);
                        image1 = "";
                        profileValue = "";
                    }
                }
                spinner_touch = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_img:
                ////for again open brand dialog
                formultiselection(brandList, false);
                update_flag = false;
                break;

            case R.id.btn_save:
                if (spin_strbrd.getSelectedItemId() == 0) {
                    Snackbar.make(btn_save, "Please select spinner dropdown value", Snackbar.LENGTH_LONG).show();
                } else if (spin_strbrd.getSelectedItemId() == 2 && selectedProfileList.size() == 0) {
                    Snackbar.make(btn_save, "Please select atleast one brand", Snackbar.LENGTH_LONG).show();
                } else if (spin_strbrd.getSelectedItemId() == 1 && image1.equals("")) {
                    Snackbar.make(btn_save, "Please capture store profile image", Snackbar.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setCancelable(true).setMessage(getString(R.string.alertsaveData));
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.open();
                            db.insertstoreprofiledata(store_cd, visit_date, spin_strbrd.getSelectedItem().toString(), image1, brandList);
                            Intent in = new Intent(context, StoreProfileSecondActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreProfileActivity.this.finish();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;

            case R.id.storep_img:
                _pathforcheck = store_cd + "_PROFILEIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;

            case R.id.btn_right:
                Intent in = new Intent(context, StoreProfileSecondActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreProfileActivity.this.finish();
                break;
        }
    }


//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.spin_strbrd:
//                if (position != 0) {
//                    profileValue = parent.getSelectedItem().toString();
//                    if (parent.getSelectedItemPosition() == 2) {
//                        if (!update_flag) {
//                            formultiselection(brandList, true);
//                        }
//                        RLPL_spinselectedV.setVisibility(View.VISIBLE);
//                        RLPL_IMG.setVisibility(View.GONE);
//                        storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
//                        image1 = "";
//                    } else {
//                        update_flag = false;
//                        selectedProfileList.clear();
//                        db.open();
//                        brandList = db.getbrandMasterData();
//                        RLPL_spinselectedV.setVisibility(View.GONE);
//                        RLPL_IMG.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    selectedProfileList.clear();
//                    RLPL_spinselectedV.setVisibility(View.GONE);
//                    RLPL_IMG.setVisibility(View.GONE);
//                    storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
//                    btn_save.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.save_icon));
//                    btn_right.setVisibility(View.GONE);
//                    image1 = "";
//                    profileValue = "";
//                }
//                break;
//        }
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }


    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
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
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            Bitmap bmp = BitmapFactory.decodeFile(CommonString.FILE_PATH + _pathforcheck);
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
                                dest.compress(Bitmap.CompressFormat.JPEG, 100,
                                        new FileOutputStream(new File(CommonString.FILE_PATH + _pathforcheck)));
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera_green));
                            image1 = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Resources.NotFoundException e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!update_flag) {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreProfileActivity.this.finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                StoreProfileActivity.this.finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!update_flag) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreProfileActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreProfileActivity.this.finish();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


    private void formultiselection(final ArrayList<BrandMaster> brandList, boolean flag_forspin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCancelable(false);
        builder.setTitle("Brand List");
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.alert_dialog_listview_search, null);
        builder.setView(view);
        final ListView listView = view.findViewById(R.id.alertSearchListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(false);
        listView.setAdapter(new MultipleSelectionSpinAdapter(context, brandList));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, " ITEMS : " + brandList.size());
                selectedProfileList.clear();
                boolean flag = false;
                for (int i = 0; i < brandList.size(); i++) {
                    if (brandList.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + brandList.get(i).getBrand() + " : " + brandList.get(i).isSelected());
                        selectedProfileList.add(brandList.get(i));
                        flag = true;
                    }
                }
                if (selectedProfileList.size() > 0) {
                    setspinValue();
                } else {
                    RLPL_spinselectedV.setVisibility(View.GONE);
                }
                if (!flag) {
                    AlertandMessages.showAlertlogin((Activity) context, "Please select atleast one brand.");
                    spin_strbrd.setSelection(0);
                }
                dialog.cancel();

            }
        });
        if (!flag_forspin) {
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, " ITEMS : " + brandList.size());
                    selectedProfileList.clear();
                    for (int i1 = 0; i1 < brandList.size(); i1++) {
                        if (brandList.get(i1).isSelected()) {
                            Log.i(TAG, i1 + " : " + brandList.get(i1).getBrand() + " : " + brandList.get(i1).isSelected());
                            selectedProfileList.add(brandList.get(i1));
                        }
                    }

                    if (selectedProfileList.size() > 0) {
                        setspinValue();
                    } else {
                        RLPL_spinselectedV.setVisibility(View.GONE);
                    }
                    dialogInterface.dismiss();
                }
            });
        }
        builder.show();
    }


    public class MultipleSelectionSpinAdapter extends BaseAdapter {
        ArrayList<BrandMaster> arrayList;
        LayoutInflater inflater;

        public MultipleSelectionSpinAdapter(Context context, ArrayList<BrandMaster> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_listview_multiple, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.alertTextView);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.alertCheckbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BrandMaster data = arrayList.get(position);

            holder.textView.setText(data.getBrand());
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.checkBox.setChecked(data.isSelected());

            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (data.isSelected()) { // deselect
                        selected--;
                    } else { // selected
                        selected++;
                    }
                    final ViewHolder temp = (ViewHolder) v.getTag();
                    temp.checkBox.setChecked(!temp.checkBox.isChecked());
                    storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera));
                    btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save_icon));
                    btn_right.setVisibility(View.GONE);
                    store_profile.setProfile_img("");
                    image1 = "";
                    data.setSelected(!data.isSelected());
                    Log.i("", "On Click Selected Item : " + data.getBrand() + " : " + data.isSelected());
                    notifyDataSetChanged();
                }
            });
            if (data.isSelected()) {
                holder.textView.setTypeface(null, Typeface.NORMAL);
            }
            holder.checkBox.setTag(holder);
            return convertView;
        }
    }

    private void setspinValue() {
        if (brandList.size() > 0) {
            defaultText = "";
            StringBuilder spinnerBuffer = new StringBuilder();
            for (int i = 0; i < brandList.size(); i++) {
                if (brandList.get(i).isSelected()) {
                    selectedProfileList.add(brandList.get(i));
                    spinnerBuffer.append(brandList.get(i).getBrand());
                    spinnerBuffer.append(", ");
                }
            }

            if (spinnerBuffer.length() > 2)
                defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);

            if (!defaultText.equals("")) {
                RLPL_spinselectedV.setVisibility(View.VISIBLE);
                selcted_text.setText(defaultText);
            }
        }
    }

    private void calculateUploadedData() {
        db.open();
        store_profile = db.getstoreProfileServerdata(store_cd);
        if (!store_profile.getSpin_value().equals("")) {
            update_flag = true;
            if (!store_profile.getProfile_img().equals("")) {
                storep_img.setImageDrawable(getResources().getDrawable(R.mipmap.camera_green));
                image1 = store_profile.getProfile_img();
            } else {
                brandList = db.getbrandMasterData();
                if (brandList.size() > 0) {
                    for (int i = 0; i < brandList.size(); i++) {
                        db.open();
                        if (db.getstoreProfilebrandServerdata(Integer.parseInt(store_cd), brandList.get(i).getBrandId()).size() > 0) {
                            brandList.get(i).setSelected(true);
                        }
                    }
                }
            }
            if (!store_profile.getSpin_value().equals("") && store_profile.getSpin_value().equalsIgnoreCase("Store")) {
                spin_strbrd.setSelection(1);
            } else {
                spin_strbrd.setSelection(2);
            }
            btn_save.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
            btn_right.setVisibility(View.VISIBLE);
            setspinValue();
        }
    }
}
