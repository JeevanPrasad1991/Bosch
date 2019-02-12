package bosch.cpm.com.bosch.dailyentry;

import android.app.Activity;
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
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMasterForCompGetterSetter;
import bosch.cpm.com.bosch.multiselectionspin.CategorySpinnerInterface;
import bosch.cpm.com.bosch.multiselectionspin.MultiselectionCategorySpinnerSearch;
import bosch.cpm.com.bosch.multiselectionspin.ProductSpinnerListener;

public class CompetitionFinencialOfferActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String user_Id, store_Id, visit_date, brand = "", brand_Id = "0", promoType = "", promoType_Id = "0",
            competition_Img = "", _pathforcheck, _path;
    Spinner spin_brand, spin_promotype;
    MultiselectionCategorySpinnerSearch spin_category;
    private static final String TAG = CompetitionFinencialOfferActivity.class.getSimpleName();
    LinearLayout rl_allcomp_data, competitionoffer_rl_bottom, rl_remarkwith_img, rl_promotype, rl_multiplecategory;
    FloatingActionButton btn_Add, fab;
    SharedPreferences preferences;
    RecyclerView compOFF_recycle;
    CheckBox compoffer_exists;
    ImageView img_compoffer;
    boolean ischangedflag = false;
    EditText etfinac_comp_remark;
    Context context;
    ArrayAdapter<CharSequence> brands_adapter, promoType_adapter;
    ArrayList<CategoryMasterForCompGetterSetter> categoryList = new ArrayList<>();
    ArrayList<CategoryMasterForCompGetterSetter> brandList = new ArrayList<>();
    ArrayList<CategoryMasterForCompGetterSetter> promoTypeList = new ArrayList<>();
    ArrayList<CategoryMasterForCompGetterSetter> comptetionCompleteList = new ArrayList<>();
    ArrayList<CategoryMasterForCompGetterSetter> insertedcompetitionTempList = new ArrayList<>();
    ArrayList<CategoryMasterForCompGetterSetter> selected_categoryList = new ArrayList<>();
    CategoryMasterForCompGetterSetter categoryMObject;
    String promoTypreSlection = "Financial";
    MyAdapter myAdapter;
    private BOSCH_DB db;
    DialogCategoryAdapter rspDataAdapter;
    boolean addflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_finencial_offer);
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
        rl_multiplecategory = (LinearLayout) findViewById(R.id.rl_multiplecategory);
        compoffer_exists = (CheckBox) findViewById(R.id.compoffer_exists);

        spin_category = (MultiselectionCategorySpinnerSearch) findViewById(R.id.spin_category);

        spin_brand = (Spinner) findViewById(R.id.spin_brand);
        spin_promotype = (Spinner) findViewById(R.id.spin_promotype);

        compOFF_recycle = (RecyclerView) findViewById(R.id.compOFF_recycle);
        img_compoffer = (ImageView) findViewById(R.id.img_compoffer);
        etfinac_comp_remark = (EditText) findViewById(R.id.etfinac_comp_remark);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle("Competition Financial Offer");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_Id = preferences.getString(CommonString.KEY_USER_ID, null);
        store_Id = preferences.getString(CommonString.KEY_STORE_CD, null);
        db = new BOSCH_DB(context);
        db.open();
    }

    private void calculateallSpinData() {
        db.open();
        brandList = db.getcompetitionChildData();
        if (brandList.size() > 0) {
            brands_adapter = new ArrayAdapter<>(context, R.layout.spinner_custom_item);
            brands_adapter.add("-Select Brand-");
            for (int i = 0; i < brandList.size(); i++) {
                brands_adapter.add(brandList.get(i).getBrand_name());
            }
            spin_brand.setAdapter(brands_adapter);
            brands_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            spin_brand.setOnItemSelectedListener(this);
        }

        ///get promo type type data fromm
        promoTypeList = db.getpromoTypeforfinancialData(promoTypreSlection);
        if (promoTypeList.size() > 0) {
            promoType_adapter = new ArrayAdapter<>(context, R.layout.spinner_custom_item);
            promoType_adapter.add("-Select PromoType-");
            for (int i = 0; i < promoTypeList.size(); i++) {
                promoType_adapter.add(promoTypeList.get(i).getPromoType());
            }
            spin_promotype.setAdapter(promoType_adapter);
            promoType_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            spin_promotype.setOnItemSelectedListener(this);
        }

        compoffer_exists.setOnClickListener(this);
        img_compoffer.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        fab.setOnClickListener(this);

        ///multiselection category
        categoryListinterface();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_brand:
                if (position != 0) {
                    ischangedflag = true;
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
                    rl_remarkwith_img.setVisibility(View.VISIBLE);
                    rl_multiplecategory.setVisibility(View.VISIBLE);
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
                    comptetionCompleteList.clear();
                    selected_categoryList.clear();
                    insertedcompetitionTempList.clear();
                    compoffer_exists.setChecked(true);
                    compOFF_recycle.setVisibility(View.VISIBLE);
                    rl_allcomp_data.setVisibility(View.VISIBLE);
                    competitionoffer_rl_bottom.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage("If checkbox unchecked Competition Financial Offers data will be lost ?").setTitle(getString(R.string.parinaam)).setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    comptetionCompleteList.clear();
                                    selected_categoryList.clear();
                                    insertedcompetitionTempList.clear();
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
                    if (duplicateValue(brand_Id, promoType_Id)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).
                                setMessage(getString(R.string.alertaddcompfidata)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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
                        Snackbar.make(btn_Add, "This Brand and PromoType already exist. Please select another.", Snackbar.LENGTH_LONG).show();
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
                                db.insertcompetitionFinancialOfferData(store_Id, visit_date, comptetionCompleteList);
                                Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                CompetitionFinencialOfferActivity.this.finish();
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
                            categoryMObject = new CategoryMasterForCompGetterSetter();
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
                            db.insertcompetitionFinancialOfferData(store_Id, visit_date, comptetionCompleteList);
                            Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            CompetitionFinencialOfferActivity.this.finish();
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
                _pathforcheck = store_Id + "_" + brand_Id + "_" + promoType_Id + "_COMP_FOF_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
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
            Crashlytics.logException(e);
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
                    try {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean ValidateCondition() {
        boolean status = true;
        if (compoffer_exists.isChecked()) {
            if (brand_Id.equals("0")) {
                SnackbarShowText("Please select Brand dropdown spinner");
                status = false;
            } else if (status && promoType_Id.equals("0")) {
                SnackbarShowText("Please select PromoType dropdown spinner");
                status = false;
            } else if (status && etfinac_comp_remark.getText().toString().isEmpty()) {
                SnackbarShowText("Please enter Promo Details");
                status = false;
            } else if (status && competition_Img.equals("")) {
                SnackbarShowText("Please take a picture");
                status = false;
            } else if (status && selected_categoryList.size() == 0) {
                SnackbarShowText("Please select atleast One Category dropdown spinner");
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
        try {
            if (selected_categoryList.size() > 0) {
                if (selected_categoryList.get(0).getCategoryId().toString().equals("0")) {
                    selected_categoryList.remove(0);
                }
                for (int k = 0; k < selected_categoryList.size(); k++) {
                    categoryMObject = new CategoryMasterForCompGetterSetter();
                    categoryMObject.setCompOfferExists(true);
                    categoryMObject.setCategory(selected_categoryList.get(k).getCategory());
                    categoryMObject.setCategoryId(selected_categoryList.get(k).getCategoryId());
                    categoryMObject.setBrand_name(brand);
                    categoryMObject.setBrand_Id(brand_Id);
                    categoryMObject.setPromoType(promoType);
                    categoryMObject.setPromoType_Id(promoType_Id);
                    categoryMObject.setCompOfferRemark(etfinac_comp_remark.getText().toString().trim().replaceAll("[(!@#$%^&*?')\"]", ""));
                    categoryMObject.setCompOffer_img(competition_Img);
                    comptetionCompleteList.add(categoryMObject);
                }

                //remove duplicate value frpm list
                Set<CategoryMasterForCompGetterSetter> set = new HashSet<>();
                set.addAll(comptetionCompleteList);
                ArrayList<CategoryMasterForCompGetterSetter> removeDuplicateJcp = new ArrayList<>();
                removeDuplicateJcp.clear();
                removeDuplicateJcp.addAll(set);
                insertedcompetitionTempList.clear();
                insertedcompetitionTempList.addAll(removeDuplicateJcp);

                addflag = true;
                myAdapter = new MyAdapter(context, insertedcompetitionTempList);
                compOFF_recycle.setAdapter(myAdapter);
                compOFF_recycle.setLayoutManager(new LinearLayoutManager(context));
                myAdapter.notifyDataSetChanged();
                compOFF_recycle.invalidate();

                competition_Img = "";
                spin_brand.setSelection(0);
                spin_promotype.setSelection(0);
                etfinac_comp_remark.setText(etfinac_comp_remark.getText().toString().trim());
                img_compoffer.setImageResource(R.mipmap.camera);
                rl_remarkwith_img.setVisibility(View.GONE);
                rl_promotype.setVisibility(View.GONE);
                rl_multiplecategory.setVisibility(View.GONE);

                ///for refress multiselection listt
                uncheckcategoryList();
                categoryListinterface();
                selected_categoryList.clear();
                Snackbar.make(btn_Add, "Data has been added", Snackbar.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<CategoryMasterForCompGetterSetter> adapter_List;

        MyAdapter(Context context, ArrayList<CategoryMasterForCompGetterSetter> adapter_List) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.adapter_List = adapter_List;
        }

        @Override
        public int getItemCount() {
            return adapter_List.size();

        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondaryplac_compf_adapter, parent, false);
            MyAdapter.MyViewHolder holder = new MyAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            CategoryMasterForCompGetterSetter object = adapter_List.get(position);

            holder.txt_brand.setText(object.getBrand_name());
            holder.txt_brand.setId(position);

            holder.txt_promotype.setText(object.getPromoType());
            holder.txt_promotype.setId(position);

            holder.delRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter_List.get(position).getID() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                if (comptetionCompleteList.size() > 0) {
                                                    for (int i = 0; i < comptetionCompleteList.size(); i++) {
                                                        if (comptetionCompleteList.get(i).getBrand_Id().equals(adapter_List.get(position).getBrand_Id())) {
                                                            comptetionCompleteList.remove(i--);
                                                        }
                                                    }
                                                }
                                                adapter_List.remove(position);

                                                if (adapter_List.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, adapter_List);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                if (comptetionCompleteList.size() > 0) {
                                                    for (int i = 0; i < comptetionCompleteList.size(); i++) {
                                                        if (comptetionCompleteList.get(i).getBrand_Id().equals(adapter_List.get(position).getBrand_Id())) {
                                                            comptetionCompleteList.remove(i--);
                                                        }
                                                    }
                                                }

                                                String brand_Idselected = adapter_List.get(position).getBrand_Id();
                                                db.removecompFinancialOffer(brand_Idselected);
                                                adapter_List.remove(position);
                                                notifyDataSetChanged();
                                                if (adapter_List.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, adapter_List);
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


            holder.txt_calegorylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setTitle("Selected Category List");
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_rsplist);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                    window.setAttributes(wlp);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ArrayList<CategoryMasterForCompGetterSetter> rsptempList = new ArrayList<>();
                    if (comptetionCompleteList.size() > 0) {
                        rsptempList.clear();
                        for (int i = 0; i < comptetionCompleteList.size(); i++) {
                            if (comptetionCompleteList.get(i).getBrand_Id().equals(adapter_List.get(position).getBrand_Id())) {
                                rsptempList.add(comptetionCompleteList.get(i));
                            }
                        }
                    }
                    RecyclerView recyclerRSP = dialog.findViewById(R.id.dialog_category_list);
                    rspDataAdapter = new DialogCategoryAdapter(context, rsptempList);
                    recyclerRSP.setAdapter(rspDataAdapter);
                    recyclerRSP.setLayoutManager(new LinearLayoutManager((Activity) context));
                    Button ok = dialog.findViewById(R.id.dialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            //  holder.ImageURL.setId(position);
            holder.txt_brand.setId(position);
            holder.delRow.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_brand, txt_promotype;
            ImageView delRow, txt_calegorylist;
            //  ImageURL

            public MyViewHolder(View convertView) {
                super(convertView);
                //  ImageURL = (ImageView) convertView.findViewById(R.id.comp_img);
                txt_brand = (TextView) convertView.findViewById(R.id.txt_brand);
                txt_promotype = (TextView) convertView.findViewById(R.id.txt_promotype);
                delRow = (ImageView) convertView.findViewById(R.id.imgDelRow);
                txt_calegorylist = (ImageView) convertView.findViewById(R.id.txt_calegorylist);
            }
        }
    }


    private boolean duplicateValue(String brand_Id, String promoType_Id) {
        boolean status = true;
        if (comptetionCompleteList.size() > 0) {
            for (int i = 0; i < comptetionCompleteList.size(); i++) {
                if (comptetionCompleteList.get(i).getBrand_Id().equals(brand_Id) && comptetionCompleteList.get(i).getPromoType_Id().equals(promoType_Id)) {
                    status = false;
                    break;
                } else if (comptetionCompleteList.get(i).getBrand_Id().equals(brand_Id)) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }

    public void setDataToListView() {
        try {
            comptetionCompleteList = db.getinsertedcompetitionFinancialOfferData(store_Id, visit_date);
            if (comptetionCompleteList.size() > 0) {
                if (comptetionCompleteList.get(0).isCompOfferExists()) {

                    //remove duplicate value frpm list
                    Set<CategoryMasterForCompGetterSetter> set = new HashSet<>();
                    set.addAll(comptetionCompleteList);
                    ArrayList<CategoryMasterForCompGetterSetter> removeDuplicateJcp = new ArrayList<>();
                    removeDuplicateJcp.clear();
                    removeDuplicateJcp.addAll(set);
                    insertedcompetitionTempList.clear();
                    insertedcompetitionTempList.addAll(removeDuplicateJcp);

                    compoffer_exists.setChecked(true);
                    compOFF_recycle.setVisibility(View.VISIBLE);
                    rl_allcomp_data.setVisibility(View.VISIBLE);
                    competitionoffer_rl_bottom.setVisibility(View.VISIBLE);
                    Collections.reverse(insertedcompetitionTempList);
                    myAdapter = new MyAdapter(context, insertedcompetitionTempList);
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

    private void categoryListinterface() {
        db.open();
        categoryList = db.getcategoryMasterforCompetitionFList();
        CategoryMasterForCompGetterSetter cm = new CategoryMasterForCompGetterSetter();
        cm.setCategoryId(0);
        cm.setCategory("All");
        categoryList.set(0, cm);
        spin_category.setItemsProduct(categoryList, -1, new CategorySpinnerInterface() {
            @Override
            public void onItemsSelectedCategory(ArrayList<CategoryMasterForCompGetterSetter> items) {
                selected_categoryList.clear();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getCategory() + " : " + items.get(i).isSelected());
                        selected_categoryList.add(items.get(i));
                    }
                }
            }
        });
    }

    private void uncheckcategoryList() {
        if (categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                categoryList.get(i).setSelected(false);
            }
        }
    }

    private class DialogCategoryAdapter extends RecyclerView.Adapter<DialogCategoryAdapter.RspHolder> {
        Context context;
        ArrayList<CategoryMasterForCompGetterSetter> rspList;
        LayoutInflater inflater;

        DialogCategoryAdapter(Context context, ArrayList<CategoryMasterForCompGetterSetter> rspList) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.rspList = rspList;
        }

        @Override
        public RspHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.secondary_adapter_categorylist, parent, false);
            RspHolder holder = new RspHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RspHolder holder, int position) {
            holder.rsp_txt.setText(rspList.get(position).getCategory().trim());
            holder.rsp_txt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.rsp_txt.setTextColor(Color.BLACK);
        }

        @Override
        public int getItemCount() {
            return rspList.size();
        }

        class RspHolder extends RecyclerView.ViewHolder {
            TextView rsp_txt;

            public RspHolder(View itemView) {
                super(itemView);
                rsp_txt = itemView.findViewById(R.id.rsp_txt);
            }
        }
    }


}