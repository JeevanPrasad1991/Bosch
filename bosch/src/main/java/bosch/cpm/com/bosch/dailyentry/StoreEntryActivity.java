package bosch.cpm.com.bosch.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.GeoTag.GeoTaggingActivity;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.delegates.NavMenuItemGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.JourneyPlan;

public class StoreEntryActivity extends AppCompatActivity {
    String store_cd, visit_date, user_type, username, geoTag, state_Id, storeType_Id;
    ArrayList<JourneyPlan> specificStoreDATA = new ArrayList<>();
    ArrayList<CategoryMaster> categoryList = new ArrayList<>();
    BOSCH_DB db;
    ValueAdapter adapter;
    RecyclerView recyclerView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_entry);
        uivalidate();
    }


    private void uivalidate() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.drawer_layout_recycle_store);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        geoTag = preferences.getString(CommonString.KEY_GEO_TAG, null);
        state_Id = preferences.getString(CommonString.KEY_STATE_ID, "");
        storeType_Id = preferences.getString(CommonString.KEY_STORE_TYPEID, "");
        getSupportActionBar().setTitle("Store Entry - " + visit_date);
        db = new BOSCH_DB(this);
        db.open();

    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        specificStoreDATA = db.getSpecificStoreData(store_cd);
        adapter = new ValueAdapter(getApplicationContext(), getdata());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        ////////////Start SERVICE for uploading Images
        //   Intent svc = new Intent(this, BackgroundService.class);
        //startService(svc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<NavMenuItemGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<NavMenuItemGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final NavMenuItemGetterSetter current = data.get(position);
            viewHolder.icon.setImageResource(current.getIconImg());
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (current.getIconImg() == R.drawable.posm || current.getIconImg() == R.drawable.posm_done) {
                        startActivity(new Intent(StoreEntryActivity.this, PosmCategoryActivity.class));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                    if (current.getIconImg() == R.drawable.competition || current.getIconImg() == R.drawable.competition_done) {
                        Intent in4 = new Intent(getApplicationContext(), BoschCompetitionActivity.class);
                        startActivity(in4);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }


                    if (current.getIconImg() == R.drawable.geotag || current.getIconImg() == R.drawable.geotag_done) {
                        if (geoTag != null && geoTag.equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                            Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                        } else if (db.getinsertGeotaggingData(store_cd, null).size() > 0
                                && db.getinsertGeotaggingData(store_cd, null).get(0).getStatus().equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                            Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent in4 = new Intent(getApplicationContext(), GeoTaggingActivity.class);
                            startActivity(in4);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }

                    }

                    if (current.getIconImg() == R.drawable.competition_offer || current.getIconImg() == R.drawable.competition_offer_done) {
                        Intent in4 = new Intent(getApplicationContext(), BoschCometitionOffersActivity.class);
                        startActivity(in4);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }


                    if (current.getIconImg() == R.drawable.competition_financial_offer || current.getIconImg() == R.drawable.competition_financial_offer_done) {
                        Intent in4 = new Intent(getApplicationContext(), CompetitionFinencialOfferActivity.class);
                        startActivity(in4);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
            }
        }
    }

    public List<NavMenuItemGetterSetter> getdata() {
        List<NavMenuItemGetterSetter> data = new ArrayList<>();
        int closingImg, intiativeImg = 0, comp_offerImg,comp_financial_offer, geo_Tag;
        ///for posm category wise
        if (posmmasterData()) {
            closingImg = R.drawable.posm_done;
        } else {
            closingImg = R.drawable.posm;
        }

        ///for comptition data
        if (db.getboschcompetitionfromdatabase(store_cd, visit_date, "").size() > 0) {
            intiativeImg = R.drawable.competition_done;
        } else {
            intiativeImg = R.drawable.competition;
        }

        //for geotag
        if (db.getinsertGeotaggingData(store_cd, null).size() > 0) {
            if (db.getinsertGeotaggingData(store_cd, null).get(0).getStatus().equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                geo_Tag = R.drawable.geotag_done;
            } else {
                geo_Tag = R.drawable.geotag;
            }
        } else {
            if (geoTag != null && geoTag.equalsIgnoreCase(CommonString.KEY_GEO_Y)) {
                geo_Tag = R.drawable.geotag_done;
            } else {
                geo_Tag = R.drawable.geotag;
            }
        }

/////competition offereeeeeeee
        if (db.getinsertedcompetitionOfferData(store_cd, visit_date).size() > 0) {
            comp_offerImg = R.drawable.competition_offer_done;
        } else {
            comp_offerImg = R.drawable.competition_offer;
        }


/////competition financial offereeeeeeee
        if (db.getinsertedcompetitionFinancialOfferData(store_cd, visit_date).size() > 0) {
            comp_financial_offer = R.drawable.competition_financial_offer_done;
        } else {
            comp_financial_offer = R.drawable.competition_financial_offer;
        }


        int img[] = {closingImg, intiativeImg, comp_offerImg, comp_financial_offer,geo_Tag};

        for (int i = 0; i < img.length; i++) {
            NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
            recData.setIconImg(img[i]);
            data.add(recData);
        }

        return data;
    }


    boolean posmmasterData() {
        boolean Status = false;
        db.open();
        categoryList = db.getcategotyListforposm();
        if (categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
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
}
