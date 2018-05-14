package ggappsdev.skinsninja;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import io.fabric.sdk.android.Fabric;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GoogleLoginActivity extends AppCompatActivity {

    //Google
    private static final int RC_SIGN_IN = 0;
    GoogleApiClient mGoogleApiClient;
    private Button mGoogleLogin;
    private TextView mPolicyLink;

    private ProgressBar spinner;

    String TAG = "LOGIN_ACTIVITY";

    //ForRefCode
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_google_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference("Users");
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        mPolicyLink = (TextView)findViewById(R.id.policy_text_button);
        mPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });

        mGoogleLogin = (Button)findViewById(R.id.glogin_button) ;
        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() !=null){

                    spinner.setVisibility(View.VISIBLE);
                    UpdateUserInfo();
                }
                else spinner.setVisibility(View.GONE);
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(),"check your internet connection",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }


                    }
                });
    }


    private void UpdateUserInfo(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userID = currentUser.getUid();
        final String userName = currentUser.getDisplayName();
        final String userEmail = currentUser.getEmail();
        final String countryCode = getUserCountry(getApplicationContext());
        final String refCode = randomString(6);

        //uri to string
        final Uri userPhotoUrl = currentUser.getPhotoUrl();
        final String userPhoto = String.valueOf(userPhotoUrl);


        mUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){


                    mUsers.child(userID).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue().equals("ban")){

                                Log.d(TAG,"Ban");

                            }
                            else {
                                Map<String, Object> value = new HashMap<>();
                                value.put("name", userName);
                                value.put("loginDate", ServerValue.TIMESTAMP);
                                value.put("photo",userPhoto);
                                mUsers.child(userID).updateChildren(value);
                                startActivity(new Intent(GoogleLoginActivity.this, WallActivity.class));
                                finish();
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    Map<String, Object> value = new HashMap<>();
                    value.put("name", userName);
                    value.put("signDate", ServerValue.TIMESTAMP);
                    value.put("loginDate", ServerValue.TIMESTAMP);
                    value.put("photo",userPhoto);
                    value.put("country",countryCode);
                    value.put("points",0);
                    value.put("status","active");
                    value.put("email",userEmail);
                    value.put("refCode",refCode);
                    value.put("dailyDate",0);
                    mUsers.child(userID).setValue(value);
                    startActivity(new Intent(GoogleLoginActivity.this, WallActivity.class));
                    finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }

        return "earth";
    }


    private String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


}

