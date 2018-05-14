package ggappsdev.skinsninja;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import ggappsdev.Holders.RedeemHistoryViewHolder;
import ggappsdev.Holders.SkinsViewHolder;
import ggappsdev.Interface.ItemClick;
import ggappsdev.Models.ModelRedeemHistory;
import ggappsdev.Models.ModelSkins;

public class UserRedeemHistory extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    FirebaseRecyclerAdapter<ModelRedeemHistory, RedeemHistoryViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    protected String userID;
    ViewSwitcher mViewSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_redeem_history);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        userID = currentUser.getUid();
        mDatabaseReference = mFirebaseDatabase.getReference("RedeemHistory").child(userID);
        mDatabaseReference.keepSynced(true);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);

        setupToolbar();
        LoadSkinsFromDb();



    }

    public void LoadSkinsFromDb() {

        mAdapter = new FirebaseRecyclerAdapter<ModelRedeemHistory, RedeemHistoryViewHolder>(
                ModelRedeemHistory.class,
                R.layout.item_redeem_history,
                RedeemHistoryViewHolder.class,
                mDatabaseReference.orderByKey()) {
            @Override
            protected void populateViewHolder(RedeemHistoryViewHolder viewHolder, final ModelRedeemHistory model, int postion) {
                //viewHolder.txt_name.setText(model.getUserName());
                viewHolder.mSkinName.setText(model.getSkinName());
                viewHolder.mSkinPrice.setText("-"+String.valueOf(model.getSkinPrice()));
                viewHolder.mSkinDate.setText(String.valueOf(model.getRedeemDate()));

                Picasso.get()
                        .load(model.getSkinImage())
                        .into(viewHolder.mSkinImage);

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
        toolbartitle.setText("Redeem History");
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
