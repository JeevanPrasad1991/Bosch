package bosch.cpm.com.bosch.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.delegates.NavMenuItemGetterSetter;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;

public class PosmCategoryActivity extends AppCompatActivity {
    ArrayList<CategoryMaster> categoryList = new ArrayList<>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, visit_date, username, state_Id, storeType_Id;
    RecyclerView posmcatRecycl;
    BOSCH_DB db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posm_category);
        uivalidate();
    }

    private void uivalidate() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posmcatRecycl = findViewById(R.id.posmcatRecycl);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        context = this;
        db = new BOSCH_DB(context);
        db.open();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        state_Id = preferences.getString(CommonString.KEY_STATE_ID, "");
        storeType_Id = preferences.getString(CommonString.KEY_STORE_TYPEID, "");
        getSupportActionBar().setTitle("Category List - " + visit_date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryList = db.getcategotyListforposm();
        posmcatRecycl.setAdapter(new ValueAdapter(context, categoryList));
        posmcatRecycl.setLayoutManager(new LinearLayoutManager(context));
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        ArrayList<CategoryMaster> data;

        public ValueAdapter(Context context, ArrayList<CategoryMaster> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_category_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final CategoryMaster current = data.get(position);

            viewHolder.category.setText(current.getCategory());
            viewHolder.category.setId(position);

            if (db.Isposminseteddata(store_cd, current.getCategoryId().toString())) {
                viewHolder.closebtn.setVisibility(View.VISIBLE);
            } else {
                viewHolder.closebtn.setVisibility(View.GONE);
            }

            viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (db.getposmHeaderData(current.getCategoryId().toString(), state_Id, storeType_Id).size() > 0) {
                        Intent intent = new Intent(context, PosmActivity.class);
                        intent.putExtra(CommonString.KEY_CATEGORY_CD, current.getCategoryId().toString());
                        intent.putExtra(CommonString.KEY_CATEGORY, current.getCategory());
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else {
                        Snackbar.make(posmcatRecycl, current.getCategory() + " Data not found .", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView category;
            ImageView closebtn;
            CardView card_view;

            public MyViewHolder(View itemView) {
                super(itemView);
                category = (TextView) itemView.findViewById(R.id.tvstorename1);
                closebtn = (ImageView) itemView.findViewById(R.id.closechkin1);
                card_view = itemView.findViewById(R.id.card_view);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            PosmCategoryActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        PosmCategoryActivity.this.finish();

    }

}
