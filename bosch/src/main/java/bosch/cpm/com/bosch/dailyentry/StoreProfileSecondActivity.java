package bosch.cpm.com.bosch.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreDimension;
import bosch.cpm.com.bosch.multiselectionspin.MultiSpinnerSearch;
import bosch.cpm.com.bosch.multiselectionspin.ProductSpinnerListener;
import bosch.cpm.com.bosch.multiselectionspin.SpinnerListener;
import bosch.cpm.com.bosch.multiselectionspin.MultiProductSpinnerSearch;

public class StoreProfileSecondActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ArrayList<BrandMaster> selectedBrandList = new ArrayList<>();
    ArrayList<BrandMaster> brandList = new ArrayList<>();
    ArrayList<CategoryMaster> categoryList = new ArrayList<>();
    ArrayList<CategoryMaster> selectedProductList = new ArrayList<>();
    BrandMaster profileDimension = new BrandMaster();
    boolean update_flag = false;
    private static final String TAG = StoreProfileActivity.class.getSimpleName();
    SharedPreferences preferences;
    String visit_date, userId, user_type, store_cd,
            store_N, store_address;
    TextView storeProfileN, storeProfile_addres;
    ArrayList<StoreDimension> dimensionsList = new ArrayList<>();
    BOSCH_DB db;
    Context context;
    Spinner spin_dimension /*new changes*/, spin_tabavaileble, spin_scpresent;
    String[] scpresentArray = {"-Select-", "Yes", "No"};
    ArrayAdapter scpresentadapeter, tabAvAdapter;
    LinearLayout rl_tabavaileble;
    MultiSpinnerSearch multiSpin_brand;
    MultiProductSpinnerSearch multiSpin_product;
    FloatingActionButton btn_save, btn_right;
    ArrayAdapter dimentionAdapter;
    String dimention = "", SCPresentSpinValue = "0", tabAvailebleSpinValue = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile_second);
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

        ///for spinner
        spin_dimension = findViewById(R.id.spin_dimension);
        multiSpin_product = findViewById(R.id.multiSpin_product);
        multiSpin_brand = findViewById(R.id.multiSpin_brand);

        spin_tabavaileble = (Spinner) findViewById(R.id.spin_tabavaileble);
        spin_scpresent = (Spinner) findViewById(R.id.spin_scpresent);
        rl_tabavaileble = (LinearLayout) findViewById(R.id.rl_tabavaileble);

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
        dimensionsList = db.getprofileDimensionList();
        if (dimensionsList.size() > 0) {
            StoreDimension sd = new StoreDimension();
            sd.setStoreDimension("-Select dimension-");
            dimensionsList.add(0, sd);
            dimentionAdapter = new ArrayAdapter(context, R.layout.spinner_custom_item);
            for (int i = 0; i < dimensionsList.size(); i++) {
                dimentionAdapter.add(dimensionsList.get(i).getStoreDimension());
            }
            dimentionAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
            spin_dimension.setAdapter(dimentionAdapter);
        }

        //for SC Present spinner array Adapter..........
        scpresentadapeter = new ArrayAdapter(context, R.layout.spinner_custom_item, scpresentArray);
        scpresentadapeter.setDropDownViewResource(R.layout.spinner_custom_item);
        spin_scpresent.setAdapter(scpresentadapeter);
        spin_scpresent.setOnItemSelectedListener(this);

        tabAvAdapter = new ArrayAdapter(context, R.layout.spinner_custom_item, scpresentArray);
        tabAvAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
        spin_tabavaileble.setAdapter(tabAvAdapter);
        spin_tabavaileble.setOnItemSelectedListener(this);

        spin_dimension.setOnItemSelectedListener(this);
        btn_save.setOnClickListener(this);
        btn_right.setOnClickListener(this);

        storeProfileN.setText(store_N);
        storeProfile_addres.setText(store_address);

        profileDimension = db.getstoreprofileDimensionData(store_cd, visit_date);

        if (profileDimension.getKey_Id() != null) {
            update_flag = true;
            btn_right.setVisibility(View.VISIBLE);
            dimention = profileDimension.getProfile_dimension();
            brandList = db.getprofiledimensionBrandList(profileDimension.getKey_Id());
            if (brandList.size() > 0) {
                for (int i = 0; i < brandList.size(); i++) {
                    if (brandList.get(i).isSelected()) {
                        selectedBrandList.add(brandList.get(i));
                    }
                }
            }

            categoryList = db.getprofiledimensionproductList(profileDimension.getKey_Id());
            if (categoryList.size() > 0) {
                for (int k = 0; k < categoryList.size(); k++) {
                    if (categoryList.get(k).isSelected()) {
                        selectedProductList.add(categoryList.get(k));
                    }
                }
            }
            ///for spinner selection
            for (int k = 0; k < dimensionsList.size(); k++) {
                if (dimensionsList.get(k).getStoreDimension().equalsIgnoreCase(profileDimension.getProfile_dimension())) {
                    spin_dimension.setSelection(k);
                    break;
                }
            }

            if (profileDimension.getSc_present().equalsIgnoreCase("Yes")) {
                spin_scpresent.setSelection(1);
                rl_tabavaileble.setVisibility(View.VISIBLE);
                if (profileDimension.getTab_availeble().equalsIgnoreCase("Yes")) {
                    spin_tabavaileble.setSelection(1);
                } else {
                    spin_tabavaileble.setSelection(2);
                }
            } else {
                spin_scpresent.setSelection(2);
                rl_tabavaileble.setVisibility(View.GONE);
            }


            btn_save.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
        } else {
            btn_right.setVisibility(View.GONE);
            brandList = db.getbrandMasterData();
            categoryList = db.getcategoryMasterList();
        }

        //for brand
        brandListInterface();
        //for productttttttttttt
        productListInterface();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (spin_dimension.getSelectedItemId() == 0) {
                    snackmsg("Please select dimension dropdown");
                } else if (selectedProductList.size() == 0) {
                    snackmsg("Please select atleast one product");
                } else if (selectedBrandList.size() == 0) {
                    snackmsg("Please select atleast one brand");
                } else if (spin_scpresent.getSelectedItemId() == 0) {
                    snackmsg("Please select SC Present dropdown");
                } else if (spin_scpresent.getSelectedItemId() == 1 && spin_tabavaileble.getSelectedItemId() == 0) {
                    snackmsg("Please select Tab Available dropdown");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(getString(R.string.alertsaveData));
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.open();
                            db.insertsecondstoreprofiledatawithProduct(store_cd, visit_date, dimention, SCPresentSpinValue, tabAvailebleSpinValue,
                                    brandList, categoryList);
                            startActivity(new Intent(context, StoreEntryActivity.class));
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreProfileSecondActivity.this.finish();
                            dialog.dismiss();
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

            case R.id.btn_right:
                startActivity(new Intent(context, StoreEntryActivity.class));
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreProfileSecondActivity.this.finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_dimension:
                if (position != 0) {
                    dimention = dimensionsList.get(position).getStoreDimension();
                    if (!db.getstoreprofileDimensionData(store_cd, visit_date).getProfile_dimension().equals("")
                            && !db.getstoreprofileDimensionData(store_cd, visit_date).getProfile_dimension().equals(dimention)) {
                        btn_save.setImageDrawable(ContextCompat.getDrawable(StoreProfileSecondActivity.this, R.drawable.save_icon));
                        btn_right.setVisibility(View.GONE);
                    }
                    break;
                } else {
                    dimention = "";
                }
            case R.id.spin_scpresent:
                if (position != 0) {
                    SCPresentSpinValue = parent.getSelectedItem().toString();
                    if (parent.getSelectedItem().toString().equalsIgnoreCase("Yes")) {
                        rl_tabavaileble.setVisibility(View.VISIBLE);
                    } else {
                        spin_tabavaileble.setSelection(0);
                        rl_tabavaileble.setVisibility(View.GONE);
                    }
                } else {
                    SCPresentSpinValue = "0";
                    spin_tabavaileble.setSelection(0);
                    rl_tabavaileble.setVisibility(View.GONE);
                }
                break;

            case R.id.spin_tabavaileble:
                if (position != 0) {
                    tabAvailebleSpinValue = parent.getSelectedItem().toString();
                } else {
                    tabAvailebleSpinValue = "0";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void brandListInterface() {
        multiSpin_brand.setItems(brandList, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(ArrayList<BrandMaster> items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        if (update_flag) {
                            btn_save.setImageDrawable(ContextCompat.getDrawable(StoreProfileSecondActivity.this, R.drawable.save_icon));
                            btn_right.setVisibility(View.GONE);
                        }
                        Log.i(TAG, i + " : " + items.get(i).getBrand() + " : " + items.get(i).isSelected());
                        selectedBrandList.add(items.get(i));
                    }
                }
            }
        });
    }

    private void productListInterface() {
        multiSpin_product.setItemsProduct(categoryList, -1, new ProductSpinnerListener() {
            @Override
            public void onItemsSelectedProduct(ArrayList<CategoryMaster> items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        if (update_flag) {
                            btn_save.setImageDrawable(ContextCompat.getDrawable(StoreProfileSecondActivity.this, R.drawable.save_icon));
                            btn_right.setVisibility(View.GONE);
                        }
                        Log.i(TAG, i + " : " + items.get(i).getCategory() + " : " + items.get(i).isSelected());
                        selectedProductList.add(items.get(i));
                    }
                }
            }
        });
    }

    private void snackmsg(String msg) {
        Snackbar.make(btn_save, msg, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!update_flag) {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                finish();
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
            StoreProfileSecondActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreProfileSecondActivity.this.finish();
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

}
