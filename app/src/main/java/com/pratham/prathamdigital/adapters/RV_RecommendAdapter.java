package com.pratham.prathamdigital.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.prathamdigital.activities.Activity_Main.db;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_RecommendAdapter extends RecyclerView.Adapter<RV_RecommendAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private ArrayList<Modal_ContentDetail> sub_content;
    private int selectedIndex;
    private int progress = 0;

    public RV_RecommendAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                               ArrayList<Modal_ContentDetail> sub_content) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.sub_content = sub_content;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Todo set font
        PD_Utility.setFont(context, holder.recom_name);
        holder.recom_name.setText(sub_content.get(holder.getAdapterPosition()).getNodetitle());
        Picasso.with(context).load(sub_content.get(holder.getAdapterPosition()).getNodeserverimage()).into(holder.recommend_content_img);
        if (sub_content.get(holder.getAdapterPosition()).isDownloading()) {
            holder.rl_reveal.setVisibility(View.VISIBLE);
        } else if (sub_content.get(holder.getAdapterPosition()).getNodetype().equalsIgnoreCase("Resource")) {
            holder.recommend_content_img.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.rl_reveal.setVisibility(View.INVISIBLE);
            if (selectedIndex != -1 && selectedIndex == holder.getAdapterPosition()) {         //sub_content.get(position).getResourcetype().equalsIgnoreCase("Video")
            } else {
                holder.fab_download.setVisibility(View.VISIBLE);
            }
            holder.card_recom.setOnClickListener(null);
        } else {
            holder.recommend_content_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.rl_reveal.setVisibility(View.INVISIBLE);
            holder.fab_download.setVisibility(View.GONE);
            holder.card_recom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browseAdapter_clicks.contentButtonClicked(holder.getAdapterPosition(), null);
                }
            });
        }
        holder.fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.downloadClick(holder.getAdapterPosition(), holder.rl_reveal);
            }
        });
    }

    public void setSelectedIndex(int position, ViewHolder holder) {
        if (selectedIndex != position) {
            selectedIndex = position;
            progress = 0;
        }
        notifyDataSetChanged();
    }

    public void setProgress(int pro) {
        progress = pro;
        notifyDataSetChanged();
    }

    public void reveal(View view) {
        // previously invisible view
        int centerX = view.getWidth();
        int centerY = view.getHeight();
        int startRadius = 0;
        int endRadius = (int) Math
                .hypot(view.getWidth(), view.getHeight());
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(300);
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void updateData(ArrayList<Modal_ContentDetail> arrayList_content) {
        this.sub_content = arrayList_content;
        progress = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sub_content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_progress)
        ProgressBar item_progress;
        @BindView(R.id.recom_name)
        TextView recom_name;
        @BindView(R.id.rl_reveal)
        RelativeLayout rl_reveal;
        @BindView(R.id.recommend_content_img)
        ImageView recommend_content_img;
        @BindView(R.id.card_recom)
        RelativeLayout card_recom;
        @BindView(R.id.fab_download)
        FloatingActionButton fab_download;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}