package ggappsdev.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ggappsdev.Interface.ItemClick;
import ggappsdev.skinsninja.R;

public class RedeemHistoryViewHolder extends RecyclerView.ViewHolder {


    public ImageView mSkinImage;

    public TextView mSkinDate, mSkinPrice, mSkinName;


    public RedeemHistoryViewHolder(View itemView) {
        super(itemView);
        //txt_name = (TextView)itemView.findViewById(R.id.txt_name);
        mSkinName = (TextView)itemView.findViewById(R.id.name_skin_txt);
        mSkinDate= (TextView)itemView.findViewById(R.id.date);
        mSkinPrice = (TextView)itemView.findViewById(R.id.points);
        mSkinImage = (ImageView)itemView.findViewById(R.id.item_image);




    }
}
