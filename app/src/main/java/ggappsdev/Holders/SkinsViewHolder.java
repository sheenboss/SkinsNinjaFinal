package ggappsdev.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ggappsdev.Interface.ItemClick;
import ggappsdev.skinsninja.R;

public class SkinsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ImageView mSkinImage;
    private ItemClick mItemClick;
    public ProgressBar mProgressBar;
    public TextView mSkinQuality, mSkinPrice, mSkinName, mProgressTxt;


    public SkinsViewHolder(View itemView) {
        super(itemView);
        //txt_name = (TextView)itemView.findViewById(R.id.txt_name);
        mSkinName = (TextView)itemView.findViewById(R.id.name_skin_txt);
        mSkinQuality = (TextView)itemView.findViewById(R.id.quality_txt);
        mSkinPrice = (TextView)itemView.findViewById(R.id.price_skin_txt);
        mSkinImage = (ImageView)itemView.findViewById(R.id.image_skin_txt);
        mProgressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        mProgressTxt = (TextView)itemView.findViewById(R.id.progressbar_txt);

        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClick itemClick) {
        this.mItemClick = itemClick;
    }

    @Override
    public void onClick(View view) {
        mItemClick.onClick(view,getAdapterPosition(),false);


    }
}
