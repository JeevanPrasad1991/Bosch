package bosch.cpm.com.bosch.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;


public class BoschCometitionOffersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String user_Id, store_Id, visit_date, category = "", category_Id = "0", brand = "", brand_Id = "0", promoType = "", promoType_Id = "0",
            competition_Img = "", _pathforcheck, _path;
    Spinner spin_category, spin_brand, spin_promotype;
    LinearLayout rl_allcomp_data, competitionoffer_rl_bottom, rl_remarkwith_img, rl_promotype, rl_brand;
    FloatingActionButton btn_Add, fab;
    SharedPreferences preferences;
    RecyclerView compOFF_recycle;
    CheckBox compoffer_exists;
    ImageView img_compoffer;
    boolean ischangedflag = false;
    EditText et_remark;
    Context context;
    ArrayAdapter<CharSequence> category_adapter, brands_adapter, promoType_adapter;
    ArrayList<CategoryMaster> categoryList = new ArrayList<>();
    ArrayList<CategoryMaster> brandList = new ArrayList<>();
    ArrayList<CategoryMaster> promoTypeList = new ArrayList<>();
    ArrayList<CategoryMaster> comptetionCompleteList = new ArrayList<>();
    CategoryMaster categoryMObject;
    MyAdapter myAdapter;
    private BOSCH_DB db;
    boolean addflag = false;
    String promoTypreSlection = "General";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bosch_cometition_offers);
        validateUIIdData();
        setDataToListView();
        calculateallSpinData();
    }

    private void validateUIIdData() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_Add = (FloatingActionButton) findViewById(R.id.btn_Add);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rl_allcomp_data = (LinearLayout) findViewById(R.id.rl_allcomp_data);
        competitionoffer_rl_bottom = (LinearLayout) findViewById(R.id.competitionoffer_rl_bottom);
        // rl_remarkwith_img,rl_promotype,rl_brand
        rl_remarkwith_img = (LinearLayout) findViewById(R.id.rl_remarkwith_img);
        rl_promotype = (LinearLayout) findViewById(R.id.rl_promotype);
        rl_brand = (LinearLayout) findViewById(R.id.rl_brand);
        compoffer_exists = (CheckBox) findViewById(R.id.compoffer_exists);

        spin_category = (Spinner) findViewById(R.id.spin_category);
        spin_brand = (Spinner) findViewById(R.id.spin_brand);
        spin_promotype = (Spinner) findViewById(R.id.spin_promotype);

        compOFF_recycle = (RecyclerView) findViewById(R.id.compOFF_recycle);
        img_compoffer = (ImageView) findViewById(R.id.img_compoffer);
        et_remark = (EditText) findViewById(R.id.et_comp_remark);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_Id = preferences.getString(CommonString.KEY_USER_ID, null);
        store_Id = preferences.getString(CommonString.KEY_STORE_CD, null);
        setTitle("Competition Offer - " + visit_date);
        db = new BOSCH_DB(context);
        db.open();
    }

    private void calculateallSpinData() {
        db.open();
        categoryList = db.getcategoryMasterforCompetitionList();

        category_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
        category_adapter.add("-Select Category-");
        for (int i = 0; i < categoryList.size(); i++) {
            category_adapter.add(categoryList.get(i).getCategory());
        }
        spin_category.setAdapter(category_adapter);
        category_adapter.setDropDownViewResource(R.layout.spinner_custom_item);

        ///get promo type type data fromm
        promoTypeList = db.getpromoTypeData(promoTypreSlection);
        if (promoTypeList.size() > 0) {
            promoType_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
            promoType_adapter.add("-Select PromoType-");
            for (int i = 0; i < promoTypeList.size(); i++) {
                promoType_adapter.add(promoTypeList.get(i).getPromoType());
            }
            spin_promotype.setAdapter(promoType_adapter);
            promoType_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            spin_promotype.setOnItemSelectedListener(this);
        }

        spin_category.setOnItemSelectedListener(this);
        compoffer_exists.setOnClickListener(this);
        img_compoffer.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_category:
                if (position != 0) {
                    ischangedflag = true;
                    category = categoryList.get(position - 1).getCategory();
                    category_Id = categoryList.get(position - 1).getCategoryId().toString();
                    brandList.clear();
                    brandList = db.getcompetitionChildData(category_Id);
                    if (brandList.size() > 0) {
                        rl_brand.setVisibility(View.VISIBLE);
                        brands_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
                        brands_adapter.add("-Select Brand-");
                        for (int i = 0; i < brandList.size(); i++) {
                            brands_adapter.add(brandList.get(i).getBrand_name());
                        }
                        spin_brand.setAdapter(brands_adapter);
                        brands_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
                        spin_brand.setOnItemSelectedListener(this);
                    }
                } else {
                    category = "";
                    category_Id = "0";
                }
                break;

            case R.id.spin_brand:
                if (position != 0) {
                    rl_promotype.setVisibility(View.VISIBLE);
                    brand = brandList.get(position - 1).getBrand_name();
                    brand_Id = brandList.get(position - 1).getBrand_Id();
                } else {
                    brand = "";
                    brand_Id = "0";
                    rl_promotype.setVisibility(View.GONE);
                }
                break;
            case R.id.spin_promotype:
                if (position != 0) {
                    // rl_remarkwith_img,rl_promotype,rl_brand
                    rl_remarkwith_img.setVisibility(View.VISIBLE);
                    promoType = promoTypeList.get(position - 1).getPromoType();
                    promoType_Id = promoTypeList.get(position - 1).getPromoType_Id();
                } else {
                    promoType = "";
                    promoType_Id = "0";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compoffer_exists:
                if (compoffer_exists.isChecked()) {
                    compoffer_exists.setChecked(true);
                    compOFF_recycle.setVisibility(View.VISIBLE);
                    rl_allcomp_data.setVisibility(View.VISIBLE);
                    competitionoffer_rl_bottom.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage("If checkbox unchecked Competition Offers data will be lost ?").setTitle(getString(R.string.parinaam)).setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    comptetionCompleteList.clear();
                                    compoffer_exists.setChecked(false);
                                    rl_allcomp_data.setVisibility(View.GONE);
                                    compOFF_recycle.setVisibility(View.GONE);
                                    competitionoffer_rl_bottom.setVisibility(View.GONE);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    compoffer_exists.setChecked(true);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.btn_Add:
                if (ValidateCondition()) {
                    if (duplicateValue(category_Id, brand_Id, promoType_Id)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).
                                setMessage(getString(R.string.alertaddcompdata)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addallData();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        Snackbar.make(btn_Add, "This Category of Brand and PromoType already exist. Please select another.", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.fab:
                if (compoffer_exists.isChecked()) {
                    if (comptetionCompleteList.size() > 0 && addflag) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(getString(R.string.alertsaveData));
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.open();
                                db.insertcompetitionOfferData(store_Id, visit_date, comptetionCompleteList);
                                Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                BoschCometitionOffersActivity.this.finish();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                        builder.show();
                    } else {
                        Snackbar.make(fab, "Please add first.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(getString(R.string.alertsaveData));
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            comptetionCompleteList.clear();
                            categoryMObject = new CategoryMaster();
                            categoryMObject.setCompOfferExists(false);
                            categoryMObject.setCategory("");
                            categoryMObject.setCategoryId(0);
                            categoryMObject.setBrand_name("");
                            categoryMObject.setBrand_Id("0");
                            categoryMObject.setPromoType("");
                            categoryMObject.setPromoType_Id("0");
                            categoryMObject.setCompOfferRemark("");
                            categoryMObject.setCompOffer_img("");
                            comptetionCompleteList.add(categoryMObject);
                            db.open();
                            db.insertcompetitionOfferData(store_Id, visit_date, comptetionCompleteList);
                            Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            BoschCometitionOffersActivity.this.finish();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();

                }

                break;
            case R.id.img_compoffer:
                _pathforcheck = store_Id + "_" + category_Id + "_" + brand_Id + "_" + promoType_Id + "_COMPOFFERIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;
        }

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(CommonString.FILE_PATH + _pathforcheck);
                        Bitmap dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                        Canvas cs = new Canvas(dest);
                        Paint tPaint = new Paint();
                        tPaint.setTextSize(100);
                        tPaint.setColor(Color.RED);
                        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        cs.drawBitmap(bmp, 0f, 0f, null);
                        float height = tPaint.measureText("yY");
                        cs.drawText(dateTime, 20f, height + 15f, tPaint);
                        try {
                            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(CommonString.FILE_PATH + _pathforcheck)));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        img_compoffer.setImageResource(R.mipmap.camera_green);
                        competition_Img = _pathforcheck;
                        _pathforcheck = "";

                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean ValidateCondition() {
        boolean status = true;
        if (compoffer_exists.isChecked()) {
            if (category_Id.equals("0")) {
                SnackbarShowText("Please select Category dropdown spinner");
                status = false;
            } else if (status && brand_Id.equals("0")) {
                SnackbarShowText("Please select Brand dropdown spinner");
                status = false;
            } else if (status && promoType_Id.equals("0")) {
                SnackbarShowText("Please select PromoType dropdown spinner");
                status = false;
            } else if (status && et_remark.getText().toString().isEmpty()) {
                SnackbarShowText("Please enter Promo Details");
                status = false;
            } else if (status && competition_Img.equals("")) {
                SnackbarShowText("Please take a picture");
                status = false;
            } else {
                status = true;
            }

        } else {
            status = true;
        }
        return status;
    }

    private void SnackbarShowText(String msg) {
        Snackbar.make(btn_Add, msg, Snackbar.LENGTH_LONG).show();
    }

    private void addallData() {
        if (comptetionCompleteList.size() > 0) {
            if (!comptetionCompleteList.get(0).isCompOfferExists()) {
                comptetionCompleteList.clear();
            }
        }
        categoryMObject = new CategoryMaster();
        categoryMObject.setCompOfferExists(true);
        categoryMObject.setCategory(category);
        categoryMObject.setCategoryId(Integer.valueOf(category_Id));
        categoryMObject.setBrand_name(brand);
        categoryMObject.setBrand_Id(brand_Id);
        categoryMObject.setPromoType(promoType);
        categoryMObject.setPromoType_Id(promoType_Id);
        categoryMObject.setCompOfferRemark(et_remark.getText().toString().trim().replaceAll("[(!@#$%^&*?')\"]", ""));
        categoryMObject.setCompOffer_img(competition_Img);
        comptetionCompleteList.add(categoryMObject);
        addflag = true;
        rl_remarkwith_img.setVisibility(View.GONE);
        rl_promotype.setVisibility(View.GONE);
        rl_brand.setVisibility(View.GONE);

        myAdapter = new MyAdapter(context, comptetionCompleteList);
        compOFF_recycle.setAdapter(myAdapter);
        compOFF_recycle.setLayoutManager(new LinearLayoutManager(context));
        myAdapter.notifyDataSetChanged();
        compOFF_recycle.invalidate();
        et_remark.setText(et_remark.getText().toString().trim());
        competition_Img = "";
        spin_brand.setSelection(0);
        spin_category.setSelection(0);
        spin_promotype.setSelection(0);
        img_compoffer.setImageResource(R.mipmap.camera);
        Snackbar.make(btn_Add, "Data has been added", Snackbar.LENGTH_SHORT).show();
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<CategoryMaster> insertedlist_Data;

        MyAdapter(Context context, ArrayList<CategoryMaster> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.insertedlist_Data = insertedlist_Data;
        }

        @Override
        public int getItemCount() {
            return insertedlist_Data.size();

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondaryplac_mt_adapter, parent, false);
            MyAdapter.MyViewHolder holder = new MyAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            CategoryMaster object = insertedlist_Data.get(position);
            holder.txt_category.setText(object.getCategory());
            holder.txt_category.setId(position);
            holder.txt_promotype.setText(object.getPromoType());
            holder.txt_promotype.setId(position);
            holder.txt_brand.setText(object.getBrand_name());
            holder.txt_brand.setId(position);

            holder.delRow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (insertedlist_Data.get(position).getID() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("Parinaam");
                        builder.setMessage("Are you sure you want to Delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                insertedlist_Data.remove(position);
                                                notifyDataSetChanged();
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, insertedlist_Data);
                                                    compOFF_recycle.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("Parinaam");
                        builder.setMessage("Are you sure you want to Delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                String listid = insertedlist_Data.get(position).getID();
                                                db.remove(listid);
                                                insertedlist_Data.remove(position);
                                                notifyDataSetChanged();
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, insertedlist_Data);
                                                    compOFF_recycle.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }

                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            });

            holder.txt_category.setId(position);
            holder.txt_brand.setId(position);
            holder.delRow.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_category, txt_brand, txt_promotype;
            ImageView delRow;
            //  ImageURL

            public MyViewHolder(View convertView) {
                super(convertView);
                txt_category = (TextView) convertView.findViewById(R.id.txt_category);
                // ImageURL = (ImageView) convertView.findViewById(R.id.comp_img);
                txt_promotype = (TextView) convertView.findViewById(R.id.txt_promotype);
                txt_brand = (TextView) convertView.findViewById(R.id.txt_brand);
                delRow = (ImageView) convertView.findViewById(R.id.imgDelRow);
            }
        }
    }


    private boolean duplicateValue(String category_Id, String brand_Id, String promoType_Id) {
        boolean status = true;
        if (comptetionCompleteList.size() > 0) {
            for (int i = 0; i < comptetionCompleteList.size(); i++) {
                if (comptetionCompleteList.get(i).getCategoryId().toString().equals(category_Id) && comptetionCompleteList.get(i).getBrand_Id().equals(brand_Id)
                        && comptetionCompleteList.get(i).getPromoType_Id().equals(promoType_Id)) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }

    public void setDataToListView() {
        try {
            comptetionCompleteList = db.getinsertedcompetitionOfferData(store_Id, visit_date);
            if (comptetionCompleteList.size() > 0) {
                if (comptetionCompleteList.get(0).isCompOfferExists()) {
                    compoffer_exists.setChecked(true);
                    compOFF_recycle.setVisibility(View.VISIBLE);
                    rl_allcomp_data.setVisibility(View.VISIBLE);
                    competitionoffer_rl_bottom.setVisibility(View.VISIBLE);
                    Collections.reverse(comptetionCompleteList);
                    myAdapter = new MyAdapter(context, comptetionCompleteList);
                    compOFF_recycle.setAdapter(myAdapter);
                    compOFF_recycle.setLayoutManager(new LinearLayoutManager(context));
                    myAdapter.notifyDataSetChanged();
                } else {
                    compoffer_exists.setChecked(false);
                    compOFF_recycle.setVisibility(View.GONE);
                    rl_allcomp_data.setVisibility(View.GONE);
                    competitionoffer_rl_bottom.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (ischangedflag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);

            if (ischangedflag) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        finish();
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                finish();

                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }


        }
        return super.onOptionsItemSelected(item);
    }


}
