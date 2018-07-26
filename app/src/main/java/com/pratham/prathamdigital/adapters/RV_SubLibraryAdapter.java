package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.prathamdigital.activities.Activity_Main.hasRealRemovableSdCard;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_SubLibraryAdapter extends RecyclerView.Adapter<RV_SubLibraryAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private ArrayList<Modal_ContentDetail> sub_content;
    private int selectedIndex;
    private int progress = 0;
    private static final int SECOND_MS = 1000;
    private boolean isPlaying = false;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            progress += 1;
            mHandler.postDelayed(mRunnable, SECOND_MS);
        }
    };

    public RV_SubLibraryAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                                ArrayList<Modal_ContentDetail> sub_content) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.sub_content = sub_content;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_library, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // TODO set Font here
        PD_Utility.setFont(context, holder.sub_lib_name);
        holder.sub_lib_name.setText(sub_content.get(holder.getAdapterPosition()).getNodetitle());
        String fileName = null;
        if (sub_content.get(holder.getAdapterPosition()).getNodeserverimage() != null) {
            fileName = sub_content.get(holder.getAdapterPosition()).getNodeserverimage()
                    .substring(sub_content.get(holder.getAdapterPosition()).getNodeserverimage().lastIndexOf('/') + 1);
        }
        //path to /data/data/yourapp/app_data/dirName
        ContextWrapper cw = new ContextWrapper(context);
        //TODO change path
        File directory = cw.getDir("PrathamImages", Context.MODE_PRIVATE);
        if (directory == null || directory.listFiles().length == 0) {
            String path = "";
            // Check folder exists on Internal
            File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
            if (intPradigi.exists()) {
                // Data found on Internal Storage
                path = Environment.getExternalStorageDirectory() + "/PraDigi/app_PrathamImages";
            }
            // Check extSDCard present or not
            else if (hasRealRemovableSdCard(context)) {
                // SD Card Available
                // SD Card Path
                String uri = PreferenceManager.getDefaultSharedPreferences(context).getString("URI", "");
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uri));
                DocumentFile tmp = pickedDir.findFile("PraDigi");
                DocumentFile tmp1 = tmp.findFile("app_PrathamImages");
//                        DocumentFile tmp2 = tmp1.findFile(subContents.get(position).getResourcepath());
                path = SDCardUtil.getRealPathFromURI(context, tmp1.getUri());
                if (path == null) {
                    path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), context) + "/PraDigi/app_PrathamImages";
                }
            } else {
                // Data Not Available anywhere
            }
            directory = new File(path);
        }
        if (fileName != null) {
            File filepath = new File(directory, fileName);
            Log.d("adapter_filename:::", fileName);
            Log.d("adapter_filepath:::", filepath.toString());
            holder.sub_lib_content_img.setImageDrawable(Drawable.createFromPath(filepath.toString()));
        }
        holder.card_sub_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.contentButtonClicked(holder.getAdapterPosition(), holder.itemView);
            }
        });
        if (sub_content.get(holder.getAdapterPosition()).getNodetype().equalsIgnoreCase("Resource")) {
            holder.sub_lib_content_img.setScaleType(ImageView.ScaleType.FIT_XY);
//            holder.c_delete.setVisibility(View.VISIBLE);
//            holder.c_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    browseAdapter_clicks.onContentDelete(holder.getAdapterPosition());
//                }
//            });
        } else {
            holder.sub_lib_content_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            holder.c_delete.setVisibility(View.GONE);
//            holder.c_delete.setOnClickListener(null);
        }
    }

    public void setSelectedIndex(int position) {
        selectedIndex = position;
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Modal_ContentDetail> arrayList_content) {
        this.sub_content = arrayList_content;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sub_content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sub_lib_name)
        TextView sub_lib_name;
        @BindView(R.id.sub_lib_content_img)
        ImageView sub_lib_content_img;
        @BindView(R.id.card_sub_lib)
        RelativeLayout card_sub_lib;
        @BindView(R.id.c_delete)
        ImageView c_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
