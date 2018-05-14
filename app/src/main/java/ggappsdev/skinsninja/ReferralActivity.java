package ggappsdev.skinsninja;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import ggappsdev.Models.GetRefOwner;
import ggappsdev.Models.ModelUser;

public class ReferralActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUsersReference;
    FirebaseUser mFirebaseUser;
    String userID;
    String referralCode;

    TextView REF_TEXT;
    EditText REF_INPUT;
    Button USE_CODE_BUTTON, INVITE_BUTTON;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        REF_TEXT = (TextView)findViewById(R.id.ref_txt);
        REF_INPUT = (EditText) findViewById(R.id.ref_input_txt);
        USE_CODE_BUTTON = (Button)findViewById(R.id.use_code_btn);
        INVITE_BUTTON = (Button)findViewById(R.id.invite_btn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        mDatabaseReference = mDatabase.getReference().child("Referrals");
        mUsersReference = mDatabase.getReference().child("Users");

        getReferalCode();
        verifyReferal();

        REF_TEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", REF_TEXT.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ReferralActivity.this,R.string.code_copied,Toast.LENGTH_SHORT).show();
            }
        });

        INVITE_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareAPP();
            }
        });

        USE_CODE_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String user_input = REF_INPUT.getText().toString();
               if(user_input.length() == 6){
                   if(user_input.equals(referralCode)){
                       Toast.makeText(getApplicationContext(), "Can't refer yourself!", Toast.LENGTH_LONG).show();
                   }else {
                       useRefCode(user_input);
                   }
               } else {
                   Toast.makeText(getApplicationContext(), "Code should have 6 characters", Toast.LENGTH_LONG).show();
               }
            }
        });

    }



    private void rewardPoints(final int points){

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
                successDialog();

            }
        });

    }
    private void successDialog(){

        final Dialog dialog = new Dialog(ReferralActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_redeem_success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView dialog_text = dialog.findViewById(R.id.dialog_text);
        TextView dialog_text2 = dialog.findViewById(R.id.dialog_text2);
        dialog_text2.setText("Invite your friends and get 10% of their lifetime earning!");
        dialog_text.setText("+75 points bonus reward");
        Button dialogBtn_ok = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        dialog.show();


    }
    private void getReferalCode(){
        mDatabaseReference = mDatabase.getReference("Users").child(userID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);

                final String refCode =  modelUser.getRefCode();
                mDatabaseReference = mDatabase.getReference("Referrals");
                mDatabaseReference.child(refCode).child("codeOwner").setValue(userID);
                REF_TEXT.setText(refCode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void useRefCode(String referralCode){

        mDatabaseReference.child(referralCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    GetRefOwner getRefOwner = dataSnapshot.getValue(GetRefOwner.class);
                    final String codeOwnerID = getRefOwner.getCodeOwner();

                    Log.d("LOGER",codeOwnerID);
                    mDatabaseReference.child("referredUsers").child(userID).setValue(mFirebaseUser.getEmail());
                    Map<String, Object> value = new HashMap<>();
                    value.put("referredBy", codeOwnerID);
                    mDatabaseReference = mDatabase.getReference().child("Users");
                    mDatabaseReference.child(userID).updateChildren(value);
                    REF_INPUT.setVisibility(View.INVISIBLE);
                    USE_CODE_BUTTON.setVisibility(View.INVISIBLE);
                    rewardPoints(75);

                }else {

                    Toast.makeText(getApplicationContext(), "Entered code is wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void verifyReferal(){
        mDatabaseReference = mDatabase.getReference().child("Users");
        mDatabaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser  = dataSnapshot.getValue(ModelUser.class);

                if(modelUser.getReferredBy() == null){

                    REF_INPUT.setVisibility(View.VISIBLE);
                    USE_CODE_BUTTON.setVisibility(View.VISIBLE);
                    Log.d("LOGGER","CODE NOT USED");


                } else {
                    REF_INPUT.setVisibility(View.INVISIBLE);
                    USE_CODE_BUTTON.setVisibility(View.INVISIBLE);
                    Log.d("LOGGER","CODE USED");


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Refer
    public void ShareAPP() {
        try
        { Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String invtxt = getString(R.string.ref_text)+REF_TEXT.getText().toString()+"\n";
            invtxt = invtxt + "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()+"\n";
            i.putExtra(Intent.EXTRA_TEXT, invtxt);
            startActivity(Intent.createChooser(i, getString(R.string.select)));
        }
        catch(Exception e)
        {
        }
    }

}
