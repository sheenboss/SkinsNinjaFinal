package ggappsdev.skinsninja;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import ggappsdev.Holders.RedeemHistoryViewHolder;
import ggappsdev.Holders.RewardHistoryViewHolder;
import ggappsdev.Models.ModelRedeemHistory;
import ggappsdev.Models.ModelRewardHistory;

public class UserRewardHistory extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    FirebaseRecyclerAdapter<ModelRewardHistory, RewardHistoryViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    ViewSwitcher mViewSwitcher;
    protected String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_redeem_history);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        userID = currentUser.getUid();
        mDatabaseReference = mFirebaseDatabase.getReference("RewardsHistory").child(userID);
        mDatabaseReference.keepSynced(true);
        mLayoutManager = new LinearLayoutManager(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);
        LoadSkinsFromDb();
        setupToolbar();

    }

    public void LoadSkinsFromDb() {

        mAdapter = new FirebaseRecyclerAdapter<ModelRewardHistory, RewardHistoryViewHolder>(
                ModelRewardHistory.class,
                R.layout.item_redeem_history,
                RewardHistoryViewHolder.class,
                mDatabaseReference.orderByKey()) {
            @Override
            protected void populateViewHolder(RewardHistoryViewHolder viewHolder, final ModelRewardHistory model, int postion) {
                //viewHolder.txt_name.setText(model.getUserName());
                viewHolder.mSkinName.setText(model.getAwardname());
                viewHolder.mSkinPrice.setText("+" + String.valueOf(model.getAward()));
                viewHolder.mSkinDate.setText(String.valueOf(model.getDate()));

                String type = model.getAwardname().toString();
                switch (type) {
                    case "Daily Check-in Reward":
                        Picasso.get()
                                .load(R.drawable.dailycheckdark)
                                .into(viewHolder.mSkinImage);
                        break;
                    case "Referral commission":
                        Picasso.get()
                                .load(R.drawable.referdark)
                                .into(viewHolder.mSkinImage);
                        break;

                    case "Video ads - Unity":
                        Picasso.get()
                                .load(R.drawable.unityads)
                                .into(viewHolder.mSkinImage);
                        break;
                    case "Video ads - IronSource":
                        Picasso.get()
                                .load(R.drawable.ironsource)
                                .into(viewHolder.mSkinImage);
                        break;
                    case "Offerwall - IronSource":
                        Picasso.get()
                                .load(R.drawable.ironsource)
                                .into(viewHolder.mSkinImage);
                        break;
                    case "Offerwall - Tapjoy":
                        Picasso.get()
                                .load(R.drawable.tapjoylogo)
                                .into(viewHolder.mSkinImage);
                        break;
                    case "Offerwall  - OfferToro":
                        Picasso.get()
                                .load(R.drawable.offertorologo)
                                .into(viewHolder.mSkinImage);
                        break;

                    default:
                        Picasso.get()
                                .load(R.drawable.earningsmall)
                                .into(viewHolder.mSkinImage);
                }


            }




        };
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mViewSwitcher = (ViewSwitcher)findViewById(R.id.switcher);
                TextView textView = (TextView)findViewById(R.id.empty_view);
                if (itemCount >= 1){
                    mViewSwitcher.removeView(textView);

                }
            }
        });
    }

    private void setupToolbar(){

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView)findViewById(R.id.toolbar_title);
        toolbartitle.setText("Rewards History");
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

    }



}
