package com.example.yrmultimediaco.fileexplorer;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationAdapter extends AbstractItem<NavigationAdapter, NavigationAdapter.ViewHolder> {


    private String folderPath;
    private String folderName;


    public NavigationAdapter(String folderPath, String folderName) {
        this.folderPath = folderPath;
        this.folderName = folderName;

    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @NonNull
    @Override
    public NavigationAdapter.ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.navRecView;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.nav_path_rv_sample;
    }

    static class ViewHolder extends FastAdapter.ViewHolder {

        TextView navText;
        IconicsImageView navImage;  //iconic image view like that " > "

        public ViewHolder(View itemView) {
            super(itemView);
            navText = itemView.findViewById(R.id.path);
            navText.setSingleLine(false);
            navImage = itemView.findViewById(R.id.navIcon);

        }

        @Override
        public void unbindView(IItem item) {

        }

        @Override
        public void bindView(IItem item, List payloads) {

            NavigationAdapter navigationAdapter = (NavigationAdapter) item;
            Log.d("DEBUG", "NavigationAdapter folderPath: " + navigationAdapter.getFolderPath());
            navText.setText(navigationAdapter.getFolderName());

// Determine if this item should show the ">" icon or not
            /*if (headerItemPaths.containsValue(navigationAdapter.getFolderPath())) {
                navImage.setVisibility(View.VISIBLE);
            } else {
                navImage.setVisibility(View.GONE);
            }*/

            //int position = getAdapterPosition();

            // Determine if this item should show the ">" icon or not



            if (navImage != null) {
                int position = getAdapterPosition();
                if (position < getItemViewType() - 1) {
                    navImage.setIcon(new IconicsDrawable(navImage.getContext())
                            .icon(CommunityMaterial.Icon.cmd_greater_than)
                            .colorRes(com.mikepenz.materialize.R.color.md_grey_400)
                            .sizeDp(10));
                } else {
                    // If it's the last item, set a different icon (e.g., folder icon)
                    navImage.setIcon(new IconicsDrawable(navImage.getContext())
                            .icon(CommunityMaterial.Icon2.cmd_not_equal)
                            .colorRes(com.mikepenz.materialize.R.color.md_grey_400)
                            .sizeDp(10));
                }

                if (position == 0) {
                    navImage.setVisibility(View.GONE);
                } else {
                    navImage.setVisibility(View.VISIBLE);
                }
            }


        }
    }
}
