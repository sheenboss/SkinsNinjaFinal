package ggappsdev.skinsninja;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ggappsdev.Models.ModelUser;

public class SteamUrlActivity extends AppCompatActivity {


    private Button save_btn;
    private EditText steam_url;
    FirebaseDatabase mDatabase;
    DatabaseReference mUsers;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steam_url);

        save_btn = (Button) findViewById(R.id.save_button);
        steam_url = (EditText) findViewById(R.id.steam_url);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userID = currentUser.getUid();
        mUsers = mDatabase.getReference("Users").child(userID);

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser id = dataSnapshot.getValue(ModelUser.class);
                if (dataSnapshot.exists()) {
                    if (id.getSteamUrl() != null) {

                        steam_url.setText(id.getSteamUrl());
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) findViewById(R.id.toolbar_title);
        toolbartitle.setText("Steam Trade Url");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUsers.child("steamUrl").setValue(steam_url.getText().toString());
                mUsers.child("steamUrl").toString();
                dialog();

            }
        });


    }

    public void dialog() {

        final Dialog dialog = new Dialog(SteamUrlActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_redeem_success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView dialog_text = dialog.findViewById(R.id.dialog_text);
        TextView dialog_text2 = dialog.findViewById(R.id.dialog_text2);
        dialog_text2.setText(steam_url.getText().toString());
        dialog_text.setText("Steam Trade URL saved.");
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
}
