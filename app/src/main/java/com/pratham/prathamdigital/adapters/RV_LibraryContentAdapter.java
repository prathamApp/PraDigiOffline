package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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

public class RV_LibraryContentAdapter extends RecyclerView.Adapter<RV_LibraryContentAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private int selectedIndex;
    private ArrayList<Modal_ContentDetail> downloadContents;

    public RV_LibraryContentAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                                    ArrayList<Modal_ContentDetail> downloadContents) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.downloadContents = downloadContents;
        selectedIndex = -1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_filter, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            if (downloadContents.size() > 0) {
                // TODO font set
                PD_Utility.setFont(context, holder.l_c_age);

                holder.l_c_age.setText(downloadContents.get(position).getNodetitle());
                String fileName = null;
                if (downloadContents.get(position).getNodeserverimage() != null) {
                    fileName = downloadContents.get(position).getNodeserverimage()
                            .substring(downloadContents.get(position).getNodeserverimage().lastIndexOf('/') + 1);
                }

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
                        // SD Card P`ath
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
                    holder.l_child_avatar.setImageDrawable(Drawable.createFromPath(filepath.toString()));
                }
//                holder.l_child_avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            holder.l_card_age.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browseAdapter_clicks.browserButtonClicked(holder.getAdapterPosition());
                }
            });
            if (selectedIndex != -1 && selectedIndex == position) {
                holder.l_card_age.setBackgroundColor(context.getResources().getColor(R.color.ghost_white));
            } else {
                holder.l_card_age.setBackground(context.getResources().getDrawable(R.drawable.pink_blue_gradient));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return downloadContents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_child_avatar)
        ImageView l_child_avatar;
        @BindView(R.id.l_card_age)
        RelativeLayout l_card_age;
        @BindView(R.id.l_c_age)
        TextView l_c_age;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
