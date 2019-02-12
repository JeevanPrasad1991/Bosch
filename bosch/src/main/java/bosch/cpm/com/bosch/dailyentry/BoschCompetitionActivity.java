package bosch.cpm.com.bosch.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMaster;
import bosch.cpm.com.bosch.keyboard.BasicOnKeyboardActionListener;
import bosch.cpm.com.bosch.keyboard.CustomKeyboardView;

public class BoschCompetitionActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<CategoryMaster> competition_brandList = new ArrayList<>();
    HashMap<CategoryMaster, List<CategoryMaster>> listDataChild;
    List<CategoryMaster> listDataHeader = new ArrayList<>();
    List<Integer> checkHeaderArray = new ArrayList<Integer>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, visit_date, username;
    CustomKeyboardView mKeyboardView;
    static int currentapiVersion = 1;
    boolean ischangedflag = false;
    Keyboard mKeyboard;
    ExpandableListView competition_explist;
    ExpandableListAdapter listAdapter;
    FloatingActionButton fab;
    boolean checkflag = true;
    String Error_Message;
    Context context;
    BOSCH_DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bosch_competition);
        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        uivalidate();
    }

    private void uivalidate() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        competition_explist = findViewById(R.id.competition_explist);
        fab = findViewById(R.id.fab);
        context = this;
        db = new BOSCH_DB(context);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        getSupportActionBar().setTitle("Competition -" + visit_date);

        mKeyboard = new Keyboard(this, R.xml.keyboard);
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fab.setOnClickListener(this);

        prepareListData();
    }


    private void prepareListData() {
        db.open();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = db.getcategoryMasterforCompetitionList();
        if (listDataHeader.size() > 0) {
            // Adding child data
            for (int i = 0; i < listDataHeader.size(); i++) {
                competition_brandList = db.getboschcompetitionfromdatabase(store_cd, visit_date, listDataHeader.get(i).getCategoryId().toString());
                if (competition_brandList.size() == 0) {
                    competition_brandList = db.getcompetitionChildData(listDataHeader.get(i).getCategoryId().toString());
                } else {
                    fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_txt));
                }

                listDataChild.put(listDataHeader.get(i), competition_brandList); // Header, Child data
            }
        }


        // setting list adapter
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        competition_explist.setAdapter(listAdapter);

        competition_explist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem == 0) {
                    fab.setVisibility(View.VISIBLE);
                } else if (lastItem == totalItemCount) {
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                competition_explist.invalidateViews();
                competition_explist.clearFocus();
                if (mKeyboardView.getVisibility() == View.VISIBLE) {
                    mKeyboardView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Listview Group click listener
        competition_explist.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });


        // Listview Group expanded listener
        competition_explist.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                competition_explist.invalidateViews();
                competition_explist.clearFocus();
            }
        });

        // Listview Group collasped listener
        competition_explist.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (mKeyboardView.getVisibility() == View.VISIBLE) {
                    mKeyboardView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Listview on child click listener
        competition_explist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fab:
                competition_explist.clearFocus();
                listAdapter.notifyDataSetChanged();
                if (validateData(listDataChild, listDataHeader)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(getString(R.string.alertsaveData));
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            db.open();
                            db.insertboschcompetiitonList(store_cd, visit_date, listDataChild, listDataHeader);
                            Toast.makeText(context, "Data has been saved", Toast.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            BoschCompetitionActivity.this.finish();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {
                    Snackbar.make(competition_explist, Error_Message, Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }


    boolean validateData(HashMap<CategoryMaster, List<CategoryMaster>> listDataChild2, List<CategoryMaster> listDataHeader2) {
        checkflag = true;
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader2.get(i)).size(); j++) {
                String Category = listDataHeader2.get(i).getCategory();
                String Brand_name = listDataChild2.get(listDataHeader2.get(i)).get(j).getBrand_name();
                String brandofsku_count = listDataChild2.get(listDataHeader2.get(i)).get(j).getSkuQTY();
                if (brandofsku_count.equals("")) {
                    Error_Message = ("Please fill " + Category + " of " + Brand_name + " of SKU count value.");
                    checkHeaderArray.add(i);
                    checkflag = false;
                    break;
                } else {
                    checkflag = true;
                }
            }
            if (!checkflag) {
                break;
            }
        }

        return checkflag;
    }


    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void showKeyboardWithAnimation() {
        if (mKeyboardView.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            mKeyboardView.showWithAnimation(animation);
        } else if (mKeyboardView.getVisibility() == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        mKeyboardView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mKeyboardView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        } else {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            if (mKeyboardView.getVisibility() == View.VISIBLE) {
                mKeyboardView.setVisibility(View.INVISIBLE);
            } else {
                if (ischangedflag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE).setCancelable(false)
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
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }


            }


        }
        return super.onOptionsItemSelected(item);
    }


    public class ViewHolder {
        CardView cardView;
        TextView comp_brand;
        EditText cometition_edt;

    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<CategoryMaster> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<CategoryMaster, List<CategoryMaster>> _listDataChild;

        public ExpandableListAdapter(Context context, List<CategoryMaster> listDataHeader,
                                     HashMap<CategoryMaster, List<CategoryMaster>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final CategoryMaster childText = (CategoryMaster) getChild(groupPosition, childPosition);
            final CategoryMaster headerTitle = (CategoryMaster) getGroup(groupPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.comptetition_row, null);
                holder = new ViewHolder();

                holder.cardView = (CardView) convertView.findViewById(R.id.card_competition);
                holder.comp_brand = (TextView) convertView.findViewById(R.id.comp_brand);
                holder.cometition_edt = (EditText) convertView.findViewById(R.id.cometition_edt);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();

            if (currentapiVersion >= 11) {
                holder.cometition_edt.setTextIsSelectable(true);
                holder.cometition_edt.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                holder.cometition_edt.setInputType(0);
            }

            holder.comp_brand.setText(childText.getBrand_name().trim());
            holder.comp_brand.setId(childPosition);

            final ViewHolder finalHolder = holder;
            holder.cometition_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    setHideSoftKeyboard(finalHolder.cometition_edt);
                    if (hasFocus) {
                        showKeyboardWithAnimation();
                    }
                    if (!hasFocus) {
                        hide();
                        final EditText Caption = (EditText) v;
                        ischangedflag = true;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            childText.setSkuQTY(value1);
                        } else {
                            childText.setSkuQTY("");
                        }
                    }
                }
            });

            holder.cometition_edt.setText(childText.getSkuQTY());
            holder.cometition_edt.setId(childPosition);


            if (!checkflag) {
                boolean flag = false;
                if (holder.cometition_edt.getText().toString().isEmpty()) {
                    flag = true;
                    holder.cometition_edt.setHint("EMPTY");
                    holder.cometition_edt.setHintTextColor(Color.WHITE);
                }

                if (flag) {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                }
            } else {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final CategoryMaster headerTitle = (CategoryMaster) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.comptition_header, null);
            }
            CardView card_view = (CardView) convertView.findViewById(R.id.card_view);
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);

            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle.getCategory().trim());

            if (!checkflag) {
                if (checkHeaderArray.contains(groupPosition)) {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.forexpandible));
                }
            } else {
                card_view.setCardBackgroundColor(getResources().getColor(R.color.forexpandible));
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }


}
