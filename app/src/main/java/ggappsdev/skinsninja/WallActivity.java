package ggappsdev.skinsninja;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyLog;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import ggappsdev.Adapters.NavigationViewAdapterSkins;
import ggappsdev.Custom.ConnectionStatus;
import ggappsdev.Models.GetServerTime;
import ggappsdev.Models.ModelUser;
import hotchemi.android.rate.AppRate;

public class WallActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView list;
    private String TAG = "LOGWALL";
    private boolean doubleBackToExitPressedOnce = false;

    //BUTTON TIMER SETUP
    private boolean earnedCurrency = false;
    private static final long START_TIME_IN_MILLIS = 86400000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;

    //FIREBASE SETUP
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mServerTimeReference;
    DatabaseReference mUsersReference;
    DatabaseReference mUsersReference2;
    DatabaseReference mReferenceToHistory;
    DatabaseReference mReferenceForReferal;
    DatabaseReference mReferralsNumber;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private Long mServerTime;
    private String userID;


    //ADS
    final private UnityAdsListener unityAdsListener = new UnityAdsListener();
    //IRON
    final private IronSoruceListener mIronSoruceListener = new IronSoruceListener();
    private int IronAward;
    //TAPJOU
    private TJPlacement directPlayPlacement;
    private TJPlacement examplePlacement;
    private TJPlacement offerwallPlacement;
    final private TapjoyLisener mTapjoyLisener = new TapjoyLisener();


    //FOR CONNECTION STATUS
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private boolean internetConnected = true;

    //REFERRAL
    private String REFERAL_ID;
    protected int referralBonus = 10;
    private int referralsNumber;
    private String MY_REF_CODE;


    //UI SETUP
    private TextView USER_POINTS, USER_EMAIL, USER_NAME, INVITED_NUMBER, REFERRAL_EARNING;
    private ImageView USER_AVATAR;
    private Button REF_BUTTON, DAILY_BUTTON, GET_SKINS_BUTTON;
    private ImageButton UNITY_ADS_BUTTON, IRON_WALL_BUTTON, IRON_ADS_BUTTON,TAPJOY_WALL_BUTTON,OFFERTORO_WALL_BUTTON;

    String[] emailaddresses = {
            "About App", "Missing Points?",
            "Rewards History", "Redeem History",
            "Privacy Policy", "Steam Trade URL",
            "Chat Room"};


    int[] drawableids = {R.drawable.aboutsmall, R.drawable.missingsmall,
            R.drawable.earningsmall, R.drawable.redeemsmall,
            R.drawable.policy, R.drawable.steamicon,
            R.drawable.chaticon};


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(2)
                .setRemindInterval(2)
                .monitor();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        mUsersReference = mFirebaseDatabase.getReference().child("Users");
        mUsersReference2 = mFirebaseDatabase.getReference().child("Users");
        mReferenceToHistory = mFirebaseDatabase.getReference("RewardsHistory");
        mReferenceForReferal = mFirebaseDatabase.getReference("RewardsHistory");
        mServerTimeReference = mFirebaseDatabase.getReference().child("ServerTime");
        mReferralsNumber = mFirebaseDatabase.getReference().child("Referrals");


        USER_POINTS = (TextView) findViewById(R.id.user_poits);
        USER_EMAIL = (TextView) findViewById(R.id.user_email);
        USER_NAME = (TextView) findViewById(R.id.user_name);
        USER_AVATAR = (ImageView) findViewById(R.id.profile_image);
        DAILY_BUTTON = (Button) findViewById(R.id.dailybutton);
        REF_BUTTON = (Button) findViewById(R.id.ref_btn);
        GET_SKINS_BUTTON = (Button) findViewById(R.id.get_skins_btn);
        INVITED_NUMBER = (TextView) findViewById(R.id.ivited_txt);
        REFERRAL_EARNING = (TextView) findViewById(R.id.refferal_earning);
        UNITY_ADS_BUTTON = (ImageButton) findViewById(R.id.unity_video);
        IRON_WALL_BUTTON = (ImageButton) findViewById(R.id.ironsource_wall_btn);
        IRON_ADS_BUTTON = (ImageButton) findViewById(R.id.is_video);
        TAPJOY_WALL_BUTTON = (ImageButton) findViewById(R.id.tapjoy_btn);
        OFFERTORO_WALL_BUTTON = (ImageButton) findViewById(R.id.offetoro_wall_btn);

        currentTime();
        setFireBase();
        setToolbar();
        setNavigationMenu();
        getReferaralID();
        initialiseOnlinePresence();
        //getReferralsNumber();

        //INSTALL ADS

        //UNITY
        UnityAds.initialize(this, AppSettings.UnityAdsID, unityAdsListener);
        //IRON
        IronSource.init(this, AppSettings.ironSoruce_Key);
        IronSource.setRewardedVideoListener(mIronSoruceListener);
        IronSource.setOfferwallListener(mIronSoruceListener);
        IronSource.setUserId(userID);
        //TAPJOY
        connectToTapjoy();
        //OFFERTORO
        connectOfferToro();


        OFFERTORO_WALL_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OffersInit.getInstance().showOfferWall(WallActivity.this);
            }
        });

        TAPJOY_WALL_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callShowOffers();
            }
        });

        IRON_WALL_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IronSource.showOfferwall();

            }
        });
        IRON_ADS_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IronSource.isRewardedVideoAvailable()) {
                    IronSource.showRewardedVideo();
                } else {

                }
            }
        });

        UNITY_ADS_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isReady()) {
                    UnityAds.show(WallActivity.this);
                } else {
                    UnityAds.initialize(WallActivity.this, AppSettings.UnityAdsID, unityAdsListener);
                }
            }
        });

        GET_SKINS_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WallActivity.this, GetSkinsActivity.class));
            }
        });
        DAILY_BUTTON.setText("Daily Reward   +" + 25);
        DAILY_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ConnectionStatus.getConnectivityStatus(WallActivity.this) == true) {

                    dailyReward(25);
                    resetTimer();
                    startTimer();

                } else {

                    final AlertDialog alert = new AlertDialog.Builder(WallActivity.this).create();
                    alert.setTitle(getString(R.string.opps));
                    alert.setMessage(getString(R.string.connection));
                    alert.setCanceledOnTouchOutside(false);
                    alert.setButton(getString(R.string.okbutton), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    });

                    alert.show();
                }

            }
        });
        REF_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WallActivity.this, ReferralActivity.class));
            }
        });


    }

    private void setToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void setNavigationMenu() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_DrawerLayout);
        mActionBarDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Disables the burger/arrow animation by default
                        super.onDrawerSlide(drawerView, 0);
                    }
                };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mActionBarDrawerToggle.syncState();
        // Navigation Drawer layout width
        TextView ver_code = (TextView) findViewById(R.id.appver);
        final TextView signout = (TextView) findViewById(R.id.signout);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(WallActivity.this);
                builder1.setMessage("Are you sure you want to Sign out?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               sendToLogin();

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        final Button rateBtn=(Button)findViewById(R.id.ratebtn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" +getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        //SHARE BTN

        final Button shareBtn=(Button)findViewById(R.id.shareappbtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "http://play.google.com/store/apps/details?id=" + getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SkinsNinja: Skins For PUBG and CS:GO");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));


            }
        });




        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        ver_code.setText("ver. " + versionName);

        setNavigationItems();
    }

    private void setNavigationItems() {
        invalidateOptionsMenu();
        list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        NavigationViewAdapterSkins adapter = new NavigationViewAdapterSkins(this, emailaddresses, drawableids);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setFireBase() {


        final String userID = mFirebaseUser.getUid();
        final String userName = mFirebaseUser.getDisplayName();
        final String userEmail = mFirebaseUser.getEmail();
        final Uri userAvatar = mFirebaseUser.getPhotoUrl();


        //USER AVATAR + NAME + EMAIL
        USER_NAME.setText(userName);
        USER_EMAIL.setText(userEmail);
        Picasso.get()
                .load(userAvatar)
                .into(USER_AVATAR);

        mUsersReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                MY_REF_CODE = modelUser.getRefCode();
                int pointFromRef = modelUser.getPointsReferral();
                Log.d("REF", MY_REF_CODE);
                USER_POINTS.setText(String.valueOf(modelUser.getPoints()));
                String sourceString = "Referral commission: " + "<b>" + pointFromRef + "</b>" + " points";
                REFERRAL_EARNING.setText(Html.fromHtml(sourceString));

                getReferralsNumber();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void dailyReward(final int reward) {


        mUsersReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                final long lastDaily = modelUser.getDailyDate();

                if (lastDaily == 0) {
                    Log.d("LOGGER", "MOZNA");
                    mUsersReference.child(userID).child("dailyDate").setValue(ServerValue.TIMESTAMP);
                    awardUser(reward, "Daily Check-in Reward");
                    awardReferral(reward);
                    successDialog();



                } else {


                    final long checkDaily = modelUser.getDailyDate();
                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sfd.format(new Date(checkDaily));


                    String date1 = sfd.format(new Date(checkDaily));
                    String date2 = sfd.format(new Date(mServerTime));

                    Log.d("LOG_CURRENT_TIME", date2);
                    Log.d("LOG_LASTCLICK_TIME", date1);

                    try {
                        Date oldDate = sfd.parse(date1);
                        Date currentDate = sfd.parse(date2);

                        long diff = currentDate.getTime() - oldDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;

                        if (oldDate.before(currentDate)) {

                            Log.d("oldDate", "is previous date");
                            Log.d("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                                    + " hours: " + hours + " days: " + days);

                            if (hours >= 20) {

                                Log.d("LOGGER", "MOZNA");
                                mUsersReference.child(userID).child("dailyDate").setValue(ServerValue.TIMESTAMP);
                                awardUser(reward, "Daily Check-in Reward");
                                awardReferral(reward);
                                successDialog();

                            } else {
                                Log.d("LOGGER", "NIEMOZNA");
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void currentTime() {

        mServerTimeReference.child("currentTime").setValue(ServerValue.TIMESTAMP);
        mServerTimeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GetServerTime getServerTime = dataSnapshot.getValue(GetServerTime.class);
                mServerTime = getServerTime.getCurrentTime();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void awardUser(final int points, final String message) {

        mUsersReference.child(userID).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ModelUser modelUser = mutableData.getValue(ModelUser.class);

                if (modelUser == null) {
                    return Transaction.success(mutableData);
                }

                modelUser.points = modelUser.points + points;


                mutableData.setValue(modelUser);
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("LOGGER", "postTransaction:onComplete:" + databaseError);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
                final String Current_Date = dd.format(c.getTime());

                userAwardtoHistory(points, message, Current_Date);
                Toast.makeText(getApplicationContext(), "You have received " + points + " points",
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    private void awardReferral(final int points) {
        if (REFERAL_ID != null) {
            mUsersReference2.child(REFERAL_ID).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    ModelUser modelUser = mutableData.getValue(ModelUser.class);
                    if (modelUser == null) {
                        return Transaction.success(mutableData);
                    }

                    modelUser.points = modelUser.points + (points / referralBonus);
                    modelUser.pointsReferral = modelUser.pointsReferral + (points / referralBonus);


                    mutableData.setValue(modelUser);
                    return Transaction.success(mutableData);

                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    Log.d("LOGGER", "postTransaction:onComplete:" + databaseError);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
                    final String Current_Date = dd.format(c.getTime());

                    referallAwardtoHistory(points, "Referral commission", Current_Date);


                }
            });

        }
    }

    private void userAwardtoHistory(final int points, final String message, final String date) {


        mReferenceToHistory.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> value = new HashMap<>();
                value.put("award", points);
                value.put("awardname", message);
                value.put("date", date);

                mReferenceToHistory.child(userID).push().setValue(value);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void referallAwardtoHistory(final int points, final String message, final String date) {


        mReferenceForReferal.child(REFERAL_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> value = new HashMap<>();
                value.put("award", points / referralBonus);
                value.put("awardname", message);
                value.put("date", date);


                mReferenceForReferal.child(REFERAL_ID).push().setValue(value);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getReferaralID() {

        mUsersReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                if (modelUser.getReferredBy() != null) {
                    REFERAL_ID = modelUser.getReferredBy();
                    Log.d("USERID", REFERAL_ID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getReferralsNumber() {

        mReferralsNumber.child(MY_REF_CODE).child("referredUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                referralsNumber = (int) dataSnapshot.getChildrenCount();
                String sourceString = "Invited: " + "<b>" + referralsNumber + "</b> ";
                INVITED_NUMBER.setText(Html.fromHtml(sourceString));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void successDialog() {

        final Dialog dialog = new Dialog(WallActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_redeem_success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView dialog_text = dialog.findViewById(R.id.dialog_text);
        TextView dialog_text2 = dialog.findViewById(R.id.dialog_text2);
        dialog_text2.setText("Come back tomorrow for new reward");
        dialog_text.setText("Daily Check-In Reward");
        Button dialogBtn_ok = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                AppRate.showRateDialogIfMeetsConditions(WallActivity.this);
                //AppRate.with(WallActivity.this).showRateDialog(WallActivity.this);
            }
        });

        dialog.show();


    }

    private void initialiseOnlinePresence() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("/presence/" + mFirebaseAuth.getCurrentUser().getUid());
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();
                    currentUserRef.setValue(true);
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
        final DatabaseReference onlineViewersCountRef = FirebaseDatabase.getInstance().getReference("/presence");
        onlineViewersCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                TextView online_users;
                online_users = (TextView) findViewById(R.id.online_users);
                online_users.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
    }

    private void sendToLogin() { //funtion
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(WallActivity.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(),GoogleLoginActivity.class /*To ur activity calss*/);
                        Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }



    //ADS
    private class UnityAdsListener implements IUnityAdsListener {
        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            if (finishState != UnityAds.FinishState.SKIPPED) {


                if (AppSettings.video_reward != 0) {
                    awardUser(AppSettings.video_reward, "Video ads - Unity");
                }
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }

    private class IronSoruceListener implements RewardedVideoListener, OfferwallListener {
        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoAdClosed() {

            awardUser(AppSettings.video_reward, "Video ads - IronSource");
        }

        @Override
        public void onRewardedVideoAvailabilityChanged(boolean b) {

        }

        @Override
        public void onRewardedVideoAdStarted() {

        }

        @Override
        public void onRewardedVideoAdEnded() {

        }

        @Override
        public void onRewardedVideoAdRewarded(Placement placement) {

        }

        @Override
        public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {

        }

        @Override
        public void onRewardedVideoAdClicked(Placement placement) {

        }

        @Override
        public void onOfferwallAvailable(boolean b) {

        }

        @Override
        public void onOfferwallOpened() {

        }

        @Override
        public void onOfferwallShowFailed(IronSourceError ironSourceError) {
            Log.d("IRONSOURCE", "onOfferwallShowFailed" + " " + ironSourceError);
        }

        @Override
        public boolean onOfferwallAdCredited(int i, final int i1, boolean b) {

            mUsersReference.child(userID).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    ModelUser modelUser = mutableData.getValue(ModelUser.class);

                    if (modelUser == null) {
                        return Transaction.success(mutableData);
                    }
                    int total_points = modelUser.getIronPoints();
                    if (i1 > total_points) {
                        IronAward = i1 - total_points;
                        modelUser.ironPoints = i1;
                    }


                    mutableData.setValue(modelUser);
                    return Transaction.success(mutableData);

                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (IronAward > 0) {
                        awardUser(IronAward, "Offerwall - IronSource");
                        awardReferral(IronAward);
                        IronAward = 0;

                    }

                }
            });

            return true;
        }

        @Override
        public void onGetOfferwallCreditsFailed(IronSourceError ironSourceError) {

        }

        @Override
        public void onOfferwallClosed() {

        }
    }

    private class TapjoyLisener implements TJGetCurrencyBalanceListener, TJPlacementListener, TJPlacementVideoListener {
        @Override
        public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
            Log.i(TAG, "currencyName: " + currencyName);
            Log.i(TAG, "balance: " + balance);

            if (earnedCurrency) {
                Log.i(TAG,"\n" + currencyName + ": " + balance);
                earnedCurrency = false;
            } else {
                Log.i(TAG,currencyName + ": " + balance);
            }
        }

        @Override
        public void onGetCurrencyBalanceResponseFailure(String s) {

        }

        @Override
        public void onRequestSuccess(TJPlacement tjPlacement) {

        }

        @Override
        public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

        }

        @Override
        public void onContentReady(TJPlacement tjPlacement) {

        }

        @Override
        public void onContentShow(TJPlacement tjPlacement) {

        }

        @Override
        public void onContentDismiss(TJPlacement tjPlacement) {
            Log.i(TAG, "Tapjoy direct play content did disappear");

            // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user's balance is always up-to-date.
            Tapjoy.getCurrencyBalance(mTapjoyLisener);

            // Begin preloading the next placement after the previous one is dismissed
            directPlayPlacement = Tapjoy.getPlacement("video_unit", this);

            // Set Video Listener to anonymous callback
            directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
                @Override
                public void onVideoStart(TJPlacement placement) {
                    Log.i(TAG, "Video has started has started for: " + placement.getName());
                }

                @Override
                public void onVideoError(TJPlacement placement, String errorMessage) {
                    Log.i(TAG, "Video error: " + errorMessage +  " for " + placement.getName());
                }

                @Override
                public void onVideoComplete(TJPlacement placement) {
                    Log.i(TAG, "Video has completed for: " + placement.getName());

                    // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
                    Tapjoy.getCurrencyBalance(mTapjoyLisener);
                }
            });

            directPlayPlacement.requestContent();
        }

        @Override
        public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

        }

        @Override
        public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

        }

        @Override
        public void onVideoStart(TJPlacement tjPlacement) {

        }

        @Override
        public void onVideoError(TJPlacement tjPlacement, String s) {

        }

        @Override
        public void onVideoComplete(TJPlacement tjPlacement) {

        }
    }
    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = AppSettings.tapjoysdk;


        // NOTE: This is the only step required if you're an advertiser.
        Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                WallActivity.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                WallActivity.this.onConnectFail();
            }
        });


    }
    private void onConnectFail() {
        Log.e(TAG, "Tapjoy connect call failed");
        Log.i(TAG,"Tapjoy connect failed!");
    }
    private void onConnectSuccess() {
        Log.i(TAG,"Tapjoy SDK connected");

        // Start preloading direct play event upon successful connect
        directPlayPlacement = Tapjoy.getPlacement("video_unit", mTapjoyLisener);

        // Set Video Listener to anonymous callback
        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
            @Override
            public void onVideoStart(TJPlacement placement) {
                Log.i(TAG, "Video has started has started for: " + placement.getName());
            }

            @Override
            public void onVideoError(TJPlacement placement, String message) {
                Log.i(TAG, "Video error: " + message + " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                Log.i(TAG, "Video has completed for: " + placement.getName());

                // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
                Tapjoy.getCurrencyBalance(mTapjoyLisener);
            }

        });

        directPlayPlacement.requestContent();
        Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                earnedCurrency = true;

                if (amount > 1){
                    awardUser(amount,"Offerwall - Tapjoy");
                }
                if (amount >= 10){
                    awardReferral(amount);
                }


                Log.i(TAG,"You've just earned " + amount + " " + currencyName);
                Log.i(TAG,"You've just earned " + amount + " " + currencyName);
            }
        });

    }
    private void callShowOffers() {
        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = Tapjoy.getPlacement("offer_wall", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                Log.i(TAG,"onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {

                }

            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {

                Log.i(TAG,"Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

                placement.showContent();
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
            }
        });

        // Add this class as a video listener
        offerwallPlacement.setVideoListener(mTapjoyLisener);
        offerwallPlacement.requestContent();
    }

    void connectOfferToro(){

        OTOfferWallSettings.getInstance().configInit(AppSettings.OfferToro_AppId,
                AppSettings.OfferToro_Secret_Key, userID);

        OffersInit.getInstance().create(this);
        OffersInit.getInstance().setOfferWallListener(new OfferWallListener() {
            @Override
            public void onOTOfferWallInitSuccess() {

            }

            @Override
            public void onOTOfferWallInitFail(String s) {

            }

            @Override
            public void onOTOfferWallOpened() {

            }

            @Override
            public void onOTOfferWallCredited(double v, double v1) {
                Double P = v;
                int integer = Integer.valueOf(P.intValue());
                if(integer > 0) {
                    awardUser(integer, "Offerwall  - OfferToro");
                }
                if (integer >= 10){
                    awardReferral(integer);
                }
            }

            @Override
            public void onOTOfferWallClosed() {

            }
        });
    }

    //BUTTON TIMER
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses_off);
                DAILY_BUTTON.setEnabled(false);
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                DAILY_BUTTON.setText("Daily Reward  +"+ 25);
                DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses);
                DAILY_BUTTON.setEnabled(true);

            }
        }.start();

        mTimerRunning = true;

    }
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }
    private void updateCountDownText() {
        int hours = (int) ((mTimeLeftInMillis / (1000 * 60 * 60)) % 24);
        int minutes = (int) ((mTimeLeftInMillis / (1000 * 60)) % 60);
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;


        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",hours, minutes, seconds);

        DAILY_BUTTON.setText(timeLeftFormatted);
    }

    public void onDestroy() {

        super.onDestroy();
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("/presence/" + mFirebaseAuth.getCurrentUser().getUid());

        currentUserRef.removeValue();
        Log.d("nice","NICE");
    }

    @Override
    public void onStart() {
        super.onStart();

        Tapjoy.onActivityStart(this);
        Tapjoy.getCurrencyBalance(mTapjoyLisener);
        IronSource.getOfferwallCredits();


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        DAILY_BUTTON.setText("Daily Reward   +" + 25);
        DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses);
        DAILY_BUTTON.setEnabled(true);

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            DAILY_BUTTON.setText("Daily Reward  +"+ 25);
            DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses);
            DAILY_BUTTON.setEnabled(true);
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                DAILY_BUTTON.setText("Daily Reward  +"+ 25);
                DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses);
                DAILY_BUTTON.setEnabled(true);
            } else {
                DAILY_BUTTON.setText("Daily Reward  +"+ 25);
                DAILY_BUTTON.setBackgroundResource(R.drawable.button_for_bonuses);
                DAILY_BUTTON.setEnabled(true);
                startTimer();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Tapjoy.onActivityStart(this);
        Tapjoy.getCurrencyBalance(mTapjoyLisener);
        IronSource.onResume(this);
        IronSource.getOfferwallCredits();
        currentTime();
        updateCountDownText();
        registerInternetCheckReceiver();
        DAILY_BUTTON.setText("Daily Reward   +" + 25);
        if (DAILY_BUTTON.getText().equals("00:00:01") || DAILY_BUTTON.getText().equals("00:00:00") ) {
            DAILY_BUTTON.setText("Daily Reward  +" + 25);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();



        IronSource.onPause(this);
        unregisterReceiver(broadcastReceiver);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }


    }
    @Override
    protected void onStop() {
        super.onStop();
        Tapjoy.onActivityStop(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

    }
    @Override
    public void onBackPressed() {


        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_DrawerLayout);

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        }
    }


    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus="";
        if(status.equalsIgnoreCase("Wifi enabled")||status.equalsIgnoreCase("Mobile data enabled")){
            internetStatus="Internet Connected";
        }else {
            internetStatus="Lost Internet Connection";
        }

        if(internetStatus.equalsIgnoreCase("Lost Internet Connection")){
            if(internetConnected){
                snackbar = Snackbar
                        .make(mDrawerLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
                        .setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);

                snackbar.show();
                internetConnected=false;
            }
        }else{
            if(!internetConnected){
                snackbar = Snackbar
                        .make(mDrawerLayout, internetStatus, Snackbar.LENGTH_LONG)
                        .setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);


                internetConnected=true;
                snackbar.show();
            }
        }
    }



}