package ggappsdev.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ggappsdev.skinsninja.AboutAppActivity;
import ggappsdev.skinsninja.ChatRoom;
import ggappsdev.skinsninja.GetSkinsActivity;
import ggappsdev.skinsninja.MissingPointsActivity;
import ggappsdev.skinsninja.PrivacyPolicyActivity;
import ggappsdev.skinsninja.R;
import ggappsdev.skinsninja.SteamUrlActivity;
import ggappsdev.skinsninja.UserRedeemHistory;
import ggappsdev.skinsninja.UserRewardHistory;

public class NavigationViewAdapterSkins extends RecyclerView.Adapter<NavigationViewAdapterSkins.MyViewHolder> {

    Context ctx;
    String[] list;
    int[] imageIds;
    public NavigationViewAdapterSkins(Context ctx,String[] list, int[] imageIds) {
        this.ctx=ctx;
        this.list=list;
        this.imageIds=imageIds;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.navigationitemscardview,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.address.setText(list[position]);
        holder.address.setCompoundDrawablesWithIntrinsicBounds(imageIds[position],0,0,0);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ctx,position+"",Toast.LENGTH_SHORT).show();
                if(position == 0)
                {
                    Intent i = new Intent(ctx, AboutAppActivity.class);
                    ctx.startActivity(i);
                }
                if(position == 1)
                {
                    Intent i = new Intent(ctx, MissingPointsActivity.class);
                    ctx.startActivity(i);
                }
                if(position == 2)
                {
                    Intent i = new Intent(ctx, UserRewardHistory.class);
                    ctx.startActivity(i);
                }
                if(position == 3)
                {
                    Intent i = new Intent(ctx, UserRedeemHistory.class);
                    ctx.startActivity(i);
                }
                if(position == 4)
                {
                    Intent i = new Intent(ctx, PrivacyPolicyActivity.class);
                    ctx.startActivity(i);
                }
                if(position == 5)
                {
                    Intent i = new Intent(ctx, SteamUrlActivity.class);
                    ctx.startActivity(i);
                }
                if(position == 6)
                {
                    Intent i = new Intent(ctx, ChatRoom.class);
                    ctx.startActivity(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView address;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            address=(TextView)itemView.findViewById(R.id.emailaddress);
            view=itemView;
        }
    }

}
