package ggappsdev.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ggappsdev.Custom.ConnectionStatus;
import ggappsdev.Holders.SkinsViewHolder;
import ggappsdev.Interface.ItemClick;
import ggappsdev.Models.ModelSkins;
import ggappsdev.Models.ModelUser;
import ggappsdev.Custom.CustomLinearLayoutManager;
import ggappsdev.skinsninja.R;


public class FragmentCSGO extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUsersDataBase;
    private DatabaseReference mRequestReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    FirebaseRecyclerAdapter<ModelSkins, SkinsViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    CustomLinearLayoutManager mCustomLinearLayoutManager;
    View myFragment;

    protected int userPoints;
    protected String userID;
    protected String userEmai;
    protected String userName;
    private int mProgress;
    ProgressDialog progressDialog;

    public FragmentCSGO() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_csgo, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Csgoskins");
        mUsersDataBase = mFirebaseDatabase.getReference("Users");
        mRequestReference = mFirebaseDatabase.getReference("PendingRequests");
        mDatabaseReference.keepSynced(true);
        currentUser = mFirebaseAuth.getCurrentUser();
        userID = currentUser.getUid();
        userEmai = currentUser.getEmail();
        userName = currentUser.getDisplayName();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCustomLinearLayoutManager = new CustomLinearLayoutManager(getActivity());



        mRecyclerView = (RecyclerView) myFragment.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);



        LoadSkinsFromDb();
        setFireBase();


        return myFragment;

    }


    public void LoadSkinsFromDb() {

        mAdapter = new FirebaseRecyclerAdapter<ModelSkins, SkinsViewHolder>(
                ModelSkins.class,
                R.layout.skin_item_cardview,
                SkinsViewHolder.class,
                mDatabaseReference.orderByChild("price").limitToLast(100)) {
            @Override
            protected void populateViewHolder(SkinsViewHolder viewHolder, final ModelSkins model, int postion) {
                //viewHolder.txt_name.setText(model.getUserName());
                viewHolder.mSkinName.setText(model.getName());
                viewHolder.mSkinPrice.setText(String.valueOf(model.getPrice()));
                viewHolder.mSkinQuality.setText(model.getQuality());

                float price = model.getPrice();
                float total = userPoints;
                mProgress = (int) ((total / price) * 100);


                viewHolder.mProgressTxt.setText(String.valueOf(mProgress + "%"));
                viewHolder.mProgressBar.setProgress(mProgress);
                //viewHolder.progress.setProgress(progress);


                Picasso.get()
                        .load(model.getImage())
                        .into(viewHolder.mSkinImage);

                if (userPoints > model.getPrice()) {
                    viewHolder.mSkinPrice.setBackgroundResource(R.drawable.button_gradient_on);
                } else {
                    viewHolder.mSkinPrice.setBackgroundResource(R.drawable.button_gradient_off);
                }

                viewHolder.setItemClickListener(new ItemClick() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if (ConnectionStatus.getConnectivityStatus(getActivity()) == true){
                            RedeemSkin(model.getName(), model.getPrice(), model.getImage());
                        } else {
                            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();

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
            }

        };
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        /*mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
               ;
                final int speedScroll = 1500;
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    int count = 0;
                    boolean flag = true;
                    @Override
                    public void run() {
                        if(count < mAdapter.getItemCount()){
                            if(count==mAdapter.getItemCount()-1){
                                flag = false;
                            }else if(count == 0){
                                flag = true;

                            }
                            if(flag) count++;
                            else count--;

                            mCustomLinearLayoutManager.smoothScrollToPosition(mRecyclerView, null, count);
                            handler.postDelayed(this,speedScroll);
                        }
                    }
                };

                handler.postDelayed(runnable,speedScroll);
            }
        });
*/

    }

    private void setFireBase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Users");
        mDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                userPoints = modelUser.getPoints();
                Log.d("LOGER", String.valueOf(userPoints));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void RedeemSkin(final String Skin_Name, final int Skin_Price, String Skin_Image) {

        final String gift_name = Skin_Name;
        final String image = Skin_Image;
        final int points = Skin_Price;


        if (userPoints >= points) {

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.item_redeem_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


            final EditText email = (EditText) dialog.findViewById(R.id.email);
            final EditText steam = (EditText) dialog.findViewById(R.id.steam_url);
            final ImageView gift_image = (ImageView) dialog.findViewById(R.id.skin_image);
            final TextView skin_name = (TextView) dialog.findViewById(R.id.skin_name);
            final TextView skin_price = (TextView) dialog.findViewById(R.id.skin_price);

            Picasso.get()
                    .load(image)
                    .into(gift_image);

            email.setText(currentUser.getEmail());
            skin_name.setText(gift_name);
            skin_price.setText(String.valueOf(points + " "));


            Animation animZoomin = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse_anim);
            animZoomin.setRepeatCount(Animation.INFINITE);
            gift_image.startAnimation(animZoomin);

            mUsersDataBase.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ModelUser id = dataSnapshot.getValue(ModelUser.class);
                    if (dataSnapshot.exists()) {
                        if (id.getSteamUrl() != null) {

                            steam.setText(id.getSteamUrl());

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
            dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            Button dialogBtn_okay = (Button) dialog.findViewById(R.id.btn_okay);
            dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();

                    String user_input = email.getText().toString();
                    String steam_url = steam.getText().toString();

                    mUsersDataBase.child(userID).child("steamUrl").setValue(steam_url);
                    mUsersDataBase.child(userID).child("steamUrl").toString();


                    if (user_input.length() >= 3) {

                        UpdatePointsAfterRedeem(Skin_Name,points,user_input,steam_url,image);
                        dialog.dismiss();


                    } else {

                        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                        alert.setTitle(getString(R.string.problem));
                        alert.setMessage(getString(R.string.enter_smth));
                        alert.setCanceledOnTouchOutside(false);
                        alert.setButton(getString(R.string.okbutton), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // close
                            }

                        });
                        alert.show();
                    }


                }

            });
            dialog.show();
        } else {
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();

            alert.setTitle(getString(R.string.opps));
            alert.setMessage(getString(R.string.no_points));
            alert.setCanceledOnTouchOutside(false);
            alert.setButton(getString(R.string.okbutton), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }

            });
            alert.show();
        }
    }

    private void UpdatePointsAfterRedeem(final String skin_name, final int skin_price, final String email, final String steam_url, final String photo_url) {

        mUsersDataBase.child(userID).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ModelUser modelUser = mutableData.getValue(ModelUser.class);
                if (modelUser == null) {
                    return Transaction.success(mutableData);
                }
                modelUser.points = modelUser.points - skin_price;


                mutableData.setValue(modelUser);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                PutDataToRedeemHistory(skin_name,skin_price,null,null,photo_url);
                CreateRedeemRequest(skin_name,skin_price,email,steam_url);


                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.item_redeem_success_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView dialog_text = dialog.findViewById(R.id.dialog_text);
                dialog_text.setText(skin_name +" redeemed for " + skin_price +" points");
                Button dialogBtn_ok = (Button) dialog.findViewById(R.id.btn_okay);
                dialogBtn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getActivity().recreate();
                    }
                });

                dialog.show();
            }
        });
    }
    private void PutDataToRedeemHistory(final String skin_name, final int skin_price, final String email, final String steam_url, final String photo_url){

        mDatabaseReference = mFirebaseDatabase.getReference("RedeemHistory").child(userID).push();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
        final String Current_Date = dd.format(c.getTime());

        Map<String, Object> value = new HashMap<>();
        value.put("skinName", skin_name);
        value.put("skinPrice", skin_price);
        value.put("redeemDate",Current_Date);
        value.put("skinImage",photo_url);
        mDatabaseReference.updateChildren(value);



    }
    private void CreateRedeemRequest(final String skin_name, final int skin_price, final String email, final String steam_url){
        mRequestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> value = new HashMap<>();
                value.put("skinName", skin_name);
                value.put("skinPrice", skin_price);
                value.put("userEmail",email);
                value.put("steamUrl",steam_url);
                value.put("userID",userID);
                value.put("userName",userName);
                value.put("requestDate",ServerValue.TIMESTAMP);

                mRequestReference.push().updateChildren(value);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}







