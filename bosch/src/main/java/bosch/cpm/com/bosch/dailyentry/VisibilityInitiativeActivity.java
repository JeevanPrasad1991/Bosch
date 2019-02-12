package bosch.cpm.com.bosch.dailyentry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.cavingettersetter.AssetORPromotionReasonGetter;
import bosch.cpm.com.bosch.cavingettersetter.WindowMaster;
import bosch.cpm.com.bosch.constant.CommonString;


public class VisibilityInitiativeActivity extends AppCompatActivity implements View.OnClickListener {
    String store_cd, visit_date, user_type, username, state_Id, distributor_Id, storeType_Id;
    List<WindowMaster> listDataHeader;
    HashMap<WindowMaster, List<WindowMaster>> listDataChild;
    boolean checkflag = true, updateFlag = false;
    List<Integer> checkHeaderArray = new ArrayList<Integer>();
    ArrayList<WindowMaster> checklist_data = new ArrayList<>();
    private SharedPreferences.Editor editor = null;
    private SharedPreferences preferences;
    ExpandableListAdapter listAdapter;
    BOSCH_DB db;
    Context context;
    ExpandableListView lvExp;
    FloatingActionButton fab;
    String _pathforcheck, _path;
    String img1 = "";
    int grp_position = -1;
    int child_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visibility_initiative);
        uidata();
    }


    void uidata() {
        lvExp = findViewById(R.id.lvExp);
        fab = findViewById(R.id.fab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);

        state_Id = preferences.getString(CommonString.KEY_STATE_ID, null);
        distributor_Id = preferences.getString(CommonString.KEY_DISTRIBUTOR_ID, null);
        storeType_Id = preferences.getString(CommonString.KEY_STORE_TYPEID, null);
        getSupportActionBar().setTitle("Paid Visibility Initiative");
        context = this;
        db = new BOSCH_DB(this);
        db.open();
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        lvExp.setAdapter(listAdapter);


        lvExp.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                lvExp.invalidateViews();
                lvExp.clearFocus();
            }
        });


        // Listview Group click listener
        lvExp.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                lvExp.invalidateViews();
                lvExp.clearFocus();

            }
        });

        // Listview Group collasped listener
        lvExp.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        fab.setOnClickListener(this);

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = db.getvisibilityinitiativeheaderData(state_Id, distributor_Id, storeType_Id);
        if (listDataHeader.size() > 0) {
            // Adding child data
            for (int i = 0; i < listDataHeader.size(); i++) {
                checklist_data = db.getinitiativechilddatafromdatabase(store_cd, listDataHeader.get(i).getWindowId().toString());
                if (checklist_data.size() > 0) {
                    checklist_data.get(0).setAnsList(db.getchecklistdatafromdatabase(store_cd, checklist_data.get(0).getKey_Id()));
                    if (checklist_data.get(0).getAnsList().size() == 0) {
                        checklist_data.get(0).setAnsList(db.getinitiativeChecklistData(checklist_data.get(0).getWindowId().toString()));
                    }
                }
                if (checklist_data.size() == 0) {
                    checklist_data = db.getinitiativechilddata(listDataHeader.get(i).getWindowId().toString());
                    ///addd checklist data in list
                    checklist_data.get(0).setAnsList(db.getinitiativeChecklistData(checklist_data.get(0).getWindowId().toString()));
                } else {
                    updateFlag = true;
                    fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
                }


                listDataChild.put(listDataHeader.get(i), checklist_data); // Header, Child data
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                lvExp.clearFocus();
                listAdapter.notifyDataSetChanged();
                if (validateData(listDataChild, listDataHeader)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.parinaam));
                    builder.setMessage(getString(R.string.alertsaveData))
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            db.insertinitiativeVisibilityData(store_cd, listDataChild, listDataHeader);
                                            Toast.makeText(getApplicationContext(), "Data has been saved", Toast.LENGTH_SHORT).show();
                                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                            VisibilityInitiativeActivity.this.finish();
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
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<WindowMaster> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<WindowMaster, List<WindowMaster>> _listDataChild;

        public ExpandableListAdapter(Context context, List<WindowMaster> listDataHeader,
                                     HashMap<WindowMaster, List<WindowMaster>> listChildData) {
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

        @SuppressLint("NewApi")
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final WindowMaster childText = (WindowMaster) getChild(groupPosition, childPosition);
            final WindowMaster headerTitle = (WindowMaster) getGroup(groupPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.visibility_initiative_entry, null);
                holder = new ViewHolder();
                holder.cardView = convertView.findViewById(R.id.card_view);
                holder.intitativeCkeck = convertView.findViewById(R.id.intitativeCkeck);
                holder.intitativeIMG = convertView.findViewById(R.id.intitativeIMG);
                holder.selfRecycler = convertView.findViewById(R.id.selfRecycler);

                holder.initiarlRECYCL = convertView.findViewById(R.id.initiarlRECYCL);
                holder.initiarl_reason = convertView.findViewById(R.id.initiarl_reason);
                holder.initiarl_img = convertView.findViewById(R.id.initiarl_img);
                holder.reasonSPIN = convertView.findViewById(R.id.reasonSPIN);
                convertView.setTag(holder);
            }

            List<WindowMaster> list = _listDataChild.get(headerTitle).get(childPosition).getAnsList();

            holder = (ViewHolder) convertView.getTag();
            final ViewHolder finalHolder = holder;

            if (childText.isIntiativepresent()) {
                holder.intitativeCkeck.setChecked(true);
            } else {
                holder.intitativeCkeck.setChecked(false);
            }

            holder.intitativeCkeck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalHolder.intitativeCkeck.isChecked()) {
                        childText.setIntiativepresent(true);
                        finalHolder.initiarlRECYCL.setVisibility(View.VISIBLE);
                        finalHolder.initiarl_reason.setVisibility(View.GONE);
                        finalHolder.initiarl_img.setVisibility(View.VISIBLE);
                        finalHolder.intitativeCkeck.setChecked(true);
                        childText.setInitiativeRightAns_Id("0");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context).setTitle("Parinaam")
                                .setMessage("Are you sure you want to close the window");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                childText.setIntiativepresent(false);
                                finalHolder.initiarlRECYCL.setVisibility(View.GONE);
                                finalHolder.initiarl_reason.setVisibility(View.VISIBLE);
                                finalHolder.initiarl_img.setVisibility(View.GONE);
                                finalHolder.intitativeCkeck.setChecked(false);
                                childText.setInitiativeIMG("");
                                dialogInterface.cancel();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                    finalHolder.intitativeCkeck.setChecked(true);
                    lvExp.invalidateViews();
                }

            });


            //for reason spinner
            final ArrayList<AssetORPromotionReasonGetter> reason_list = db.getPromotionReasonList();
            AssetORPromotionReasonGetter non = new AssetORPromotionReasonGetter();
            non.setPROMOTION_REASON("-Select Reason-");
            non.setPREASON_CD("0");
            reason_list.add(0, non);
            holder.reasonSPIN.setAdapter(new ReasonSpinnerAdapter(_context, R.layout.spinner_text_view, reason_list));

            holder.reasonSPIN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        AssetORPromotionReasonGetter ans = reason_list.get(pos);
                        childText.setInitiativeRightAns_Id(ans.getPREASON_CD().get(0));
                        childText.setInitiativeRightAns(ans.getPROMOTION_REASON().get(0));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            for (int i = 0; i < reason_list.size(); i++) {
                if (reason_list.get(i).getPREASON_CD().get(0).toString().equals(childText.getInitiativeRightAns_Id().toString())) {
                    holder.reasonSPIN.setSelection(i);
                    break;
                }
            }

            holder.intitativeIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grp_position = groupPosition;
                    child_position = childPosition;
                    _pathforcheck = store_cd + "_" + childText.getWindowId().toString() + "_INITIATIVEIMG_" + visit_date.replace("/",
                            "") + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    startCameraActivity();
                }
            });


            if (!img1.equals("")) {
                if (groupPosition == grp_position) {
                    if (childPosition == child_position) {
                        childText.setInitiativeIMG(img1);
                        img1 = "";
                    }
                }
            }


            if (!childText.getInitiativeIMG().equals("")) {
                holder.intitativeIMG.setImageResource(R.mipmap.camera_green);
                holder.intitativeIMG.setId(childPosition);
            } else {
                holder.intitativeIMG.setImageResource(R.mipmap.camera);
                holder.intitativeIMG.setId(childPosition);
            }

            holder.selfRecycler.setAdapter(new SelfAdapter(context, list));
            holder.selfRecycler.setLayoutManager(new LinearLayoutManager(_context));

            if (!childText.isIntiativepresent()) {
                finalHolder.initiarlRECYCL.setVisibility(View.GONE);
                finalHolder.initiarl_reason.setVisibility(View.VISIBLE);
                finalHolder.initiarl_img.setVisibility(View.GONE);
            } else {
                finalHolder.initiarlRECYCL.setVisibility(View.VISIBLE);
                finalHolder.initiarl_reason.setVisibility(View.GONE);
                finalHolder.initiarl_img.setVisibility(View.VISIBLE);
            }


            if (!checkflag) {
                boolean flag = false;
                if (childText.isIntiativepresent()) {
                    if (childText.getInitiativeIMG().equals("")) {
                        flag = true;
                    } else if (childText.getAnsList().size() > 0) {
                        for (int k = 0; k < childText.getAnsList().size(); k++) {
                            if (childText.getAnsList().get(k).getChecklistRightAns_Id().equals("0") ||
                                    childText.getAnsList().get(k).getChecklistRightAns_Id().equals("")) {
                                flag = true;
                                break;
                            }
                        }
                    }
                } else if (!childText.isIntiativepresent()) {
                    if (childText.getInitiativeRightAns_Id().equals("") || childText.getInitiativeRightAns_Id().equals("0")) {
                        flag = true;
                    }
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
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final WindowMaster headerTitle = (WindowMaster) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }


            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);

            lblListHeader.setTypeface(null, Typeface.BOLD);

            lblListHeader.setText(headerTitle.getWindow() + " - " + headerTitle.getBrand());

            if (!checkflag) {
                if (checkHeaderArray.contains(groupPosition)) {
                    lblListHeader.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    lblListHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            } else {
                lblListHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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

    public class ViewHolder {
        LinearLayout initiarlRECYCL, initiarl_reason, initiarl_img;
        Spinner reasonSPIN;
        RecyclerView selfRecycler;
        CheckBox intitativeCkeck;
        ImageView intitativeIMG;
        CardView cardView;
    }


    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Crashlytics.logException(e);
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
                try {
                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
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
                                Crashlytics.logException(e);
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            img1 = _pathforcheck;
                            lvExp.invalidateViews();
                            _pathforcheck = "";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    class SelfAdapter extends RecyclerView.Adapter<SelfAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<WindowMaster> data = Collections.emptyList();

        public SelfAdapter(Context context, List<WindowMaster> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_self, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.mItem = data.get(position);
            holder.tv_checklist.setText(holder.mItem.getChecklist());

            final ArrayList<WindowMaster> checklistans = db.getchecklistAnswerData(holder.mItem.getChecklist_Id().toString());
            WindowMaster a = new WindowMaster();
            a.setChecklist_answer("-Select Checklist-");
            a.setCkecklist_anserId("0");
            checklistans.add(0, a);

            holder.customSpinnerAdapter = new CustomSpinnerAdapter(context, checklistans);
            holder.spinner.setAdapter(holder.customSpinnerAdapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        WindowMaster ans = checklistans.get(pos);
                        holder.mItem.setChecklistRightAns_Id(ans.getCkecklist_anserId());
                        holder.mItem.setChecklistRightAns(ans.getChecklist_answer());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            for (int i = 0; i < checklistans.size(); i++) {
                if (holder.mItem.getChecklistRightAns_Id().equals(checklistans.get(i).getCkecklist_anserId())) {
                    holder.spinner.setSelection(i);
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_checklist;
            Spinner spinner;
            WindowMaster mItem;
            CustomSpinnerAdapter customSpinnerAdapter;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_checklist = (TextView) itemView.findViewById(R.id.tv_checklist);
                spinner = (Spinner) itemView.findViewById(R.id.spin_checklist_ans);
            }
        }
    }

    public class CustomSpinnerAdapter extends BaseAdapter {
        Context context;
        ArrayList<WindowMaster> ans;
        LayoutInflater inflter;

        public CustomSpinnerAdapter(Context applicationContext, ArrayList<WindowMaster> ans) {
            this.context = applicationContext;
            this.ans = ans;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return ans.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_item, null);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            names.setText(ans.get(i).getChecklist_answer());
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return (super.getDropDownView(position, convertView, parent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    public class ReasonSpinnerAdapter extends ArrayAdapter<AssetORPromotionReasonGetter> {
        List<AssetORPromotionReasonGetter> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<AssetORPromotionReasonGetter> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            AssetORPromotionReasonGetter cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(list.get(position).getPROMOTION_REASON().get(0));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            AssetORPromotionReasonGetter cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(cm.getPROMOTION_REASON().get(0));

            return view;
        }

    }

    boolean validateData(
            HashMap<WindowMaster, List<WindowMaster>> listDataChild2,
            List<WindowMaster> listDataHeader2) {
        checkflag = true;
        boolean ansflag = true;
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader.get(i)).size(); j++) {
                boolean present = listDataChild.get(listDataHeader.get(i)).get(j).isIntiativepresent();
                String initiative_spin = listDataChild.get(listDataHeader.get(i)).get(j).getInitiativeRightAns_Id();
                String initiative_img = listDataChild.get(listDataHeader.get(i)).get(j).getInitiativeIMG();
                String window = listDataChild.get(listDataHeader.get(i)).get(j).getWindow();
                if (present) {
                    if (initiative_img.equals("")) {
                        if (!checkHeaderArray.contains(i)) {
                            checkHeaderArray.add(i);
                        }
                        SnakbarMSG("Please capture visibility" + " image");
                        checkflag = false;
                        break;
                    } else if (listDataChild.get(listDataHeader.get(i)).get(j).getAnsList().size() > 0) {
                        for (int k = 0; k < listDataChild.get(listDataHeader.get(i)).get(j).getAnsList().size(); k++) {
                            if (listDataChild.get(listDataHeader.get(i)).get(j).getAnsList().get(k).getChecklistRightAns_Id().equals("0")
                                    || listDataChild.get(listDataHeader.get(i)).get(j).getAnsList().get(k).getChecklistRightAns_Id().equals("")) {
                                if (!checkHeaderArray.contains(i)) {
                                    checkHeaderArray.add(i);
                                }
                                SnakbarMSG("Please select checklist answer dropdown");
                                ansflag = false;
                                break;
                            }
                        }

                        if (!ansflag) {
                            checkflag = false;
                            break;
                        }
                    }
                } else if (!present) {
                    if (initiative_spin.equals("0") || initiative_spin.equals("")) {
                        if (!checkHeaderArray.contains(i)) {
                            checkHeaderArray.add(i);
                        }
                        SnakbarMSG("Please select " + " reason dropdown");
                        checkflag = false;
                        break;
                    }
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

    void SnakbarMSG(String msg) {
        Snackbar.make(lvExp, msg, Snackbar.LENGTH_LONG).show();

    }
}
