package bosch.cpm.com.bosch.dailyentry;

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
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import bosch.cpm.com.bosch.Database.BOSCH_DB;
import bosch.cpm.com.bosch.R;
import bosch.cpm.com.bosch.constant.CommonString;
import bosch.cpm.com.bosch.gsonGetterSetter.PosmMaster;
import bosch.cpm.com.bosch.keyboard.BasicOnKeyboardActionListener;
import bosch.cpm.com.bosch.keyboard.CustomKeyboardView;

public class PosmActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView posm_imgone, posm_imgtwo, posm_imgthree;
    ExpandableListView lvExp;
    String store_cd, visit_date, user_type, username, category_cd, category, state_Id, storeType_Id;
    boolean checkflag = true, updateFlag = false;
    List<Integer> checkHeaderArray = new ArrayList<Integer>();
    private SharedPreferences.Editor editor = null;
    private SharedPreferences preferences;
    ExpandableListAdapter listAdapter;
    PosmMaster posm_images = new PosmMaster();
    HashMap<PosmMaster, List<PosmMaster>> listDataChild;
    List<PosmMaster> listDataHeader = new ArrayList<>();
    List<PosmMaster> posmList = new ArrayList<>();
    CustomKeyboardView mKeyboardView;
    static int currentapiVersion = 1;
    Keyboard mKeyboard;
    BOSCH_DB db;
    Context context;
    FloatingActionButton fab;
    boolean ischangedflag = false;
    String _pathforcheck, _path;
    boolean isDialogOpen = true;
    String Error_Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posm);
        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        uidata();
    }

    void uidata() {
        lvExp = findViewById(R.id.lvExp);
        fab = findViewById(R.id.fab);
        //for imagessss
        posm_imgone = findViewById(R.id.posm_imgone);
        posm_imgtwo = findViewById(R.id.posm_imgtwo);
        posm_imgthree = findViewById(R.id.posm_imgthree);
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
        state_Id = preferences.getString(CommonString.KEY_STATE_ID, "");
        storeType_Id = preferences.getString(CommonString.KEY_STORE_TYPEID, "");
        category_cd = getIntent().getStringExtra(CommonString.KEY_CATEGORY_CD);
        category = getIntent().getStringExtra(CommonString.KEY_CATEGORY);
        setTitle(category);
        mKeyboard = new Keyboard(this, R.xml.keyboard);
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        db = new BOSCH_DB(this);
        db.open();
        ///prepared data
        prepareListData();
        setsrconimageposs(store_cd, category_cd);

        // setting list adapter
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
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
                if (mKeyboardView.getVisibility() == View.VISIBLE) {
                    mKeyboardView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Listview Group click listener
        lvExp.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
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
                if (mKeyboardView.getVisibility() == View.VISIBLE) {
                    mKeyboardView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Listview on child click listener
        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        fab.setOnClickListener(this);
        posm_imgone.setOnClickListener(this);
        posm_imgtwo.setOnClickListener(this);
        posm_imgthree.setOnClickListener(this);

    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = db.getposmheaderdatafromdatabase(store_cd, category_cd);
        if (listDataHeader.size() == 0) {
            listDataHeader = db.getposmHeaderData(category_cd, state_Id, storeType_Id);
        }
        if (listDataHeader.size() > 0) {
            // Adding child data
            for (int i = 0; i < listDataHeader.size(); i++) {
                posmList = db.getposmdatafromdatabase(store_cd, category_cd, listDataHeader.get(i).getSku_Id().toString());
                if (posmList.size() == 0) {
                    posmList = db.getposmdatausingSkuId(listDataHeader.get(i).getSku_Id(), state_Id, storeType_Id);
                } else {
                    updateFlag = true;
                    fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
                }

                listDataChild.put(listDataHeader.get(i), posmList); // Header, Child data
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.posm_imgone:
                _pathforcheck = store_cd + "_" + category_cd + "_POSMIMGONE_" + visit_date.replace("/", "")
                        + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;
            case R.id.posm_imgtwo:
                _pathforcheck = store_cd + "_" + category_cd + "_POSMIMGTWO_" + visit_date.replace("/", "")
                        + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;
            case R.id.posm_imgthree:
                _pathforcheck = store_cd + "_" + category_cd + "_POSMIMGTHREE_" + visit_date.replace("/", "")
                        + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;

            case R.id.fab:
                lvExp.clearFocus();
                listAdapter.notifyDataSetChanged();
                if (validateImg(category_cd)) {
                    if (validateData(listDataChild, listDataHeader)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.alertsaveData))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                db.open();
                                                db.inserposmimages(store_cd, category_cd, posm_images);
                                                db.insertposmdata(store_cd, visit_date, category_cd,
                                                        listDataChild, listDataHeader);
                                                Toast.makeText(getApplicationContext(), "Data has been saved", Toast.LENGTH_SHORT).show();
                                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                PosmActivity.this.finish();
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Snackbar.make(lvExp, Error_Message, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(lvExp, Error_Message, Snackbar.LENGTH_LONG).show();
                }
        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<PosmMaster> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<PosmMaster, List<PosmMaster>> _listDataChild;

        public ExpandableListAdapter(Context context, List<PosmMaster> listDataHeader,
                                     HashMap<PosmMaster, List<PosmMaster>> listChildData) {
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

            final PosmMaster childText = (PosmMaster) getChild(groupPosition, childPosition);
            final PosmMaster headerTitle = (PosmMaster) getGroup(groupPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.posm_entry, null);
                holder = new ViewHolder();
                holder.cardView = convertView.findViewById(R.id.card_view);
                holder.txt_posm = convertView.findViewById(R.id.txt_posm);
                holder.edtDeployment = convertView.findViewById(R.id.edtDeployment);
                holder.edtNEWdeploym = convertView.findViewById(R.id.edtNEWdeploym);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            if (currentapiVersion >= 11) {
                holder.edtDeployment.setTextIsSelectable(true);
                holder.edtNEWdeploym.setTextIsSelectable(true);
                holder.edtDeployment.setRawInputType(InputType.TYPE_CLASS_TEXT);
                holder.edtNEWdeploym.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                holder.edtDeployment.setInputType(0);
                holder.edtNEWdeploym.setInputType(0);
            }


            holder.txt_posm.setText(childText.getPosm());

            if (!headerTitle.getPosmsku_value().equals("") && headerTitle.getPosmsku_value().equals("0")) {
                holder.edtDeployment.setText("0");
                holder.edtNEWdeploym.setText("0");
                holder.edtDeployment.setEnabled(false);
                holder.edtNEWdeploym.setEnabled(false);
                childText.setOldDeployed_value("0");
                childText.setNew_deployed("0");
            } else {
                holder.edtDeployment.setEnabled(true);
                holder.edtNEWdeploym.setEnabled(true);
            }


            final ViewHolder finalHolder = holder;
            holder.edtDeployment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    setHideSoftKeyboard(finalHolder.edtDeployment);
                    if (hasFocus) {
                        showKeyboardWithAnimation();
                    }
                    if (!hasFocus) {
                        hide();
                        final EditText Caption = (EditText) v;
                        ischangedflag = true;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            childText.setOldDeployed_value(value1);
                            lvExp.invalidate();
                        } else {
                            childText.setOldDeployed_value("");
                        }
                    }
                }
            });

            holder.edtDeployment.setText(childText.getOldDeployed_value());


            holder.edtNEWdeploym.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    setHideSoftKeyboard(finalHolder.edtNEWdeploym);
                    if (hasFocus) {
                        showKeyboardWithAnimation();
                    }
                    if (!hasFocus) {
                        hide();
                        final EditText Caption = (EditText) v;
                        ischangedflag = true;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            childText.setNew_deployed(value1);
                            lvExp.invalidate();
                        } else {
                            childText.setNew_deployed("");
                        }
                    }
                }
            });

            if (!headerTitle.getPosmsku_value().equals("") && !childText.getOldDeployed_value().equals("")) {
                int skuCount = Integer.parseInt(headerTitle.getPosmsku_value());
                int olddeployment = Integer.parseInt(childText.getOldDeployed_value());
                if (olddeployment > skuCount) {
                    if (isDialogOpen) {
                        isDialogOpen = !isDialogOpen;
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context).setTitle("Parinaam");
                        builder.setMessage("Old Deployment value should be less than OR equal to '" + headerTitle.getSku() + "' count value .").setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        isDialogOpen = !isDialogOpen;
                                        finalHolder.edtDeployment.setText("");
                                        childText.setOldDeployed_value("");
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }

            if (!headerTitle.getPosmsku_value().equals("") && !childText.getNew_deployed().equals("")) {
                int skuCount = Integer.parseInt(headerTitle.getPosmsku_value());
                int newDeployed = Integer.parseInt(childText.getNew_deployed());
                if (newDeployed > skuCount) {
                    if (isDialogOpen) {
                        isDialogOpen = !isDialogOpen;
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context).setTitle("Parinaam");
                        builder.setMessage("New Deployment value should be less than OR equal to '" + headerTitle.getSku() + "' count value .").setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        isDialogOpen = !isDialogOpen;
                                        finalHolder.edtNEWdeploym.setText("");
                                        childText.setNew_deployed("");
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }

            holder.edtNEWdeploym.setText(childText.getNew_deployed());

            if (!checkflag) {
                boolean flag = false;
                if (holder.edtDeployment.getText().toString().equals("")) {
                    flag = true;
                    holder.edtDeployment.setHint("EMPTY");
                    holder.edtDeployment.setHintTextColor(Color.WHITE);
                }

                if (holder.edtNEWdeploym.getText().toString().equals("")) {
                    flag = true;
                    holder.edtNEWdeploym.setHint("EMPTY");
                    holder.edtNEWdeploym.setHintTextColor(Color.WHITE);
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
            final PosmMaster headerTitle = (PosmMaster) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.posm_listgroup, null);
            }
            CardView card_view = (CardView) convertView.findViewById(R.id.card_view);
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            final EditText catfacing = (EditText) convertView.findViewById(R.id.catfacing);

            if (currentapiVersion >= 11) {
                catfacing.setTextIsSelectable(true);
                catfacing.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                catfacing.setInputType(0);
            }


            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!catfacing.getText().toString().equals("")) {
                        if (lvExp.isGroupExpanded(groupPosition)) {
                            lvExp.collapseGroup(groupPosition);
                        } else {
                            lvExp.expandGroup(groupPosition);
                        }
                    } else {
                        if (isDialogOpen) {
                            isDialogOpen = !isDialogOpen;
                            AlertDialog.Builder builder = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam));
                            builder.setMessage("First fill '" + headerTitle.getSku() + "' Quantity .").setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            isDialogOpen = !isDialogOpen;
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }
            });


            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle.getSku());

            catfacing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    setHideSoftKeyboard(catfacing);
                    if (hasFocus) {
                        showKeyboardWithAnimation();
                    }
                    if (!hasFocus) {
                        hide();
                        final EditText Caption = (EditText) v;
                        String value = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value.equals("")) {
                            ischangedflag = true;
                            catfacing.setText(value);
                            headerTitle.setPosmsku_value(value);
                            lvExp.invalidate();
                        } else {
                            headerTitle.setPosmsku_value("");
                        }
                    }
                }
            });

            catfacing.setText(headerTitle.getPosmsku_value());


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

    public class ViewHolder {
        CardView cardView;
        TextView txt_posm;
        EditText edtDeployment, edtNEWdeploym;

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
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                _pathforcheck = "";
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
                                Crashlytics.logException(e);
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (_pathforcheck.contains("_POSMIMGONE_")) {
                                posm_imgone.setImageResource(R.mipmap.camera_green);
                                posm_images.setPosmIMG_one(_pathforcheck);
                                _pathforcheck = "";

                            } else if (_pathforcheck.contains("_POSMIMGTWO_")) {
                                posm_imgtwo.setImageResource(R.mipmap.camera_green);
                                posm_images.setPosmIMG_two(_pathforcheck);
                                _pathforcheck = "";
                            } else if (_pathforcheck.contains("_POSMIMGTHREE_")) {
                                posm_imgthree.setImageResource(R.mipmap.camera_green);
                                posm_images.setPosmIMG_three(_pathforcheck);
                                _pathforcheck = "";
                            }

                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    boolean validateData(HashMap<PosmMaster, List<PosmMaster>> listDataChild2, List<PosmMaster> listDataHeader2) {
        checkflag = true;
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader2.get(i)).size(); j++) {
                int skuCount = 0, olddeployment = 0, newDeployed = 0;
                String skuposmQTY = listDataHeader2.get(i).getPosmsku_value();
                String sku = listDataHeader2.get(i).getSku();
                String olddeployment_value = listDataChild2.get(listDataHeader2.get(i)).get(j).getOldDeployed_value();
                String newdeployment_value = listDataChild2.get(listDataHeader2.get(i)).get(j).getNew_deployed();

                if (!skuposmQTY.equals("")) {
                    skuCount = Integer.parseInt(listDataHeader2.get(i).getPosmsku_value());
                }

                if (!olddeployment_value.equals("")) {
                    olddeployment = Integer.parseInt(listDataChild2.get(listDataHeader2.get(i)).get(j).getOldDeployed_value());
                }
                if (!newdeployment_value.equals("")) {
                    newDeployed = Integer.parseInt(listDataChild2.get(listDataHeader2.get(i)).get(j).getNew_deployed());
                }

                if (!skuposmQTY.equals("")) {
                    if (olddeployment_value.equals("")) {
                        Error_Message = ("Please fill Old Deployment Value .");
                        checkHeaderArray.add(i);
                        checkflag = false;
                        break;
                    } else if (olddeployment > skuCount) {
                        Error_Message = ("Old Deployment value should be less than OR equal to '" + sku + "' count value .");
                        checkHeaderArray.add(i);
                        checkflag = false;
                        break;
                    } else if (newdeployment_value.equals("")) {
                        Error_Message = ("Please fill New Deployment Value .");
                        checkHeaderArray.add(i);
                        checkflag = false;
                        break;
                    } else if (newDeployed > skuCount) {
                        Error_Message = ("New Deployment value should be less than OR equal to '" + sku + "' count value .");
                        checkHeaderArray.add(i);
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


    public boolean validateImg(String category_cd) {
        boolean status = true;
        if (!db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_one().equals("") || !db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_three().equals("") ||
                !db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_three().equals("")) {
            status = true;
        } else if (!posm_images.getPosmIMG_one().equals("") || !posm_images.getPosmIMG_two().equals("") || !posm_images.getPosmIMG_three().equals("")) {
            status = true;
        } else {
            Error_Message = "Please Capture atlease One image of category .";
            status = false;
        }
        return status;
    }

    public void setsrconimageposs(String store_cd, String category_cd) {
        if (!db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_one().equals("")) {
            posm_images.setPosmIMG_one(db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_one());
            posm_imgone.setImageResource(R.mipmap.camera_green);
        } else {
            posm_imgone.setImageResource(R.mipmap.camera);
        }
        if (!db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_two().equals("")) {
            posm_imgtwo.setImageResource(R.mipmap.camera_green);
            posm_images.setPosmIMG_two(db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_two());
        } else {
            posm_imgtwo.setImageResource(R.mipmap.camera);
        }
        if (!db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_three().equals("")) {
            posm_images.setPosmIMG_three(db.getposmimgescategorywise(store_cd, category_cd).getPosmIMG_three());
            posm_imgthree.setImageResource(R.mipmap.camera_green);

        } else {
            posm_imgthree.setImageResource(R.mipmap.camera);
        }

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


        }
        return super.onOptionsItemSelected(item);
    }

}
