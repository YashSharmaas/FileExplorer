package com.example.yrmultimediaco.fileexplorer;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Placeholder;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileName extends AbstractItem<FileName, FileName.ViewHolder> implements IItem<FileName, FileName.ViewHolder>, Parcelable, IDraggable {

    File mFile;
    IconicsDrawable mIconicsDrawable;
    Drawable thumbnailImageView;
    private String creationDate;
    private String modifiedDate;
    private int itemCount;
    private int iconColor;
    private boolean isSelected = false;

    @NonNull
    @Override
    public String toString() {
        return "FileName{" +
                "fileName= " + mFile
                +
                "}";
    }
    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(creationDate);
        dest.writeString(modifiedDate);
        dest.writeInt(itemCount);
        dest.writeString(mFile.getName());
    }

    private FileName(Parcel in) {
        // Read the data from the parcel in the same order as it was written
        creationDate = in.readString();
        modifiedDate = in.readString();
        itemCount = in.readInt();

    }

    public static final Parcelable.Creator<FileName> CREATOR = new Parcelable.Creator<FileName>() {
        public FileName createFromParcel(Parcel in) {
            return new FileName(in);
        }

        public FileName[] newArray(int size) {
            return new FileName[size];
        }
    };

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public Object withIsDraggable(boolean draggable) {
        return null;
    }


    public interface OnBtnDetailsClickListener {
        void onBtnDetailsClicked(FileName fileNameItem);
    }


    private OnBtnDetailsClickListener btnDetailsClickListener;


    public void setOnBtnDetailsClickListener(OnBtnDetailsClickListener listener) {
        this.btnDetailsClickListener = listener;
    }


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public FileName(File file) {
        mFile = file;
        this.iconColor = com.mikepenz.library_extensions.R.color.md_grey_300;
    }


    public FileName(IconicsDrawable iconicsDrawable) {
        mIconicsDrawable = iconicsDrawable;
    }

   /* public FileName(ImageView thumbnailImageView) {
        this.thumbnailImageView = thumbnailImageView;
    }*/

    public Drawable getThumbnailImageView() {
        return thumbnailImageView;
    }

    public void setThumbnailImageView(Drawable thumbnailImageView) {
        this.thumbnailImageView = thumbnailImageView;
    }

    public IconicsDrawable getIconicsDrawable() {
        return mIconicsDrawable;
    }

    public void setIconicsDrawable(IconicsDrawable iconicsDrawable) {
        mIconicsDrawable = iconicsDrawable;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    @NonNull
    @Override
    public FileName.ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.file_name;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_files;
    }


    class ViewHolder extends FastAdapter.ViewHolder {

        private TextView file_name;
        ImageView btnDetails;
       // ImageView dirImag;
        IconicsImageView iconImageView;
        ImageView thumbnailImageView;
        ImageView clickImage;
        TextView itemCount, lastDate;

        public ViewHolder(View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.file_name);
            iconImageView = itemView.findViewById(R.id.iconS);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImg);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            clickImage = itemView.findViewById(R.id.overlayImg);
            itemCount = itemView.findViewById(R.id.itemCount);
            lastDate = itemView.findViewById(R.id.itemDate);

        }

        @Override
        public void unbindView(IItem item) {
            file_name.setText(null);
        }

        @Override
        public void bindView(IItem item, List payloads) {


            if (item instanceof FileName){
                FileName fileNameItem = (FileName) item;
                file_name.setText(fileNameItem.mFile.getName());

                /*if (fileNameItem.mFile.isHidden()){
                    iconImageView.setVisibility(View.VISIBLE);
                    thumbnailImageView.setVisibility(View.GONE);
                    iconImageView.setIcon(new IconicsDrawable(iconImageView.getContext()).icon(CommunityMaterial.Icon.cmd_folder).colorRes(com.mikepenz.materialize.R.color.md_grey_400).sizeDp(24));
                }*/

                if (fileNameItem.mFile.isDirectory()){
                    iconImageView.setVisibility(View.VISIBLE);
                    thumbnailImageView.setVisibility(View.GONE);
                    iconImageView.setIcon(new IconicsDrawable(iconImageView.getContext()).icon(CommunityMaterial.Icon.cmd_folder).colorRes(com.mikepenz.materialize.R.color.md_yellow_800).sizeDp(24));

                } else {
                    IconicsDrawable iconicsDrawable = fileNameItem.getIconicsDrawable();
                    if (iconicsDrawable != null) {
                        iconImageView.setVisibility(View.VISIBLE);
                        thumbnailImageView.setVisibility(View.GONE);
                        //iconImageView.setIcon(new IconicsDrawable(iconImageView.getContext()).icon(CommunityMaterial.Icon.cmd_exclamation).colorRes(com.mikepenz.materialize.R.color.md_red_500).sizeDp(24));
                        //iconImageView.setIcon(iconicsDrawable);
                    }else if (((FileName) item).getThumbnailImageView() != null) {
                        iconImageView.setVisibility(View.VISIBLE);
                        thumbnailImageView.setVisibility(View.GONE);
                        iconImageView.setImageDrawable(((FileName) item).getThumbnailImageView());
                    } else {
                        iconImageView.setVisibility(View.VISIBLE);
                        iconImageView.setIcon(new IconicsDrawable(iconImageView.getContext()).icon(CommunityMaterial.Icon.cmd_file_image).colorRes(com.mikepenz.materialize.R.color.md_red_500).sizeDp(24));
                        thumbnailImageView.setVisibility(View.GONE);
                    }
                }

                itemCount.setText(fileNameItem.getItemCount() + "  Items");
                lastDate.setText(fileNameItem.getModifiedDate());

                Log.d("FileNameViewHolder", "File Name: " + fileNameItem.getFile().getName());
                Log.d("FileNameViewHolder", "Last Modified Date: " + fileNameItem.getModifiedDate());
                Log.d("FileNameViewHolder", "Item Count: " + fileNameItem.getItemCount());



                btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //fileNameItem.updateDetails();
                        // Call the showBottomSheetDialog method and pass the thumbnailResourceId
                        //showBottomSheetDialog();
                        // bottomSheet(v, fileNameItem);

                        //showFolderDialog(v, fileNameItem);
                        fileNameItem.setSelected(!fileNameItem.isSelected);

                        if (btnDetailsClickListener != null) {
                            btnDetailsClickListener.onBtnDetailsClicked(fileNameItem);
                        }

                }
                });

                if (fileNameItem.isSelected){
                    clickImage.setVisibility(View.VISIBLE);
                } else {
                    clickImage.setVisibility(View.GONE);
                }

            }

        }
    }

 /*   public void bottomSheet(View view, FileName fileNameItem) {

            BottomSheetDialog dialog = new BottomSheetDialog(view.getContext());
            View bottomSheetView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.bottom_sheet_folder_details, null);

            // Get references to the views in the bottom sheet layout
            TextView creationDateTextView = bottomSheetView.findViewById(R.id.txtCreationDate);
            TextView modifiedDateTextView = bottomSheetView.findViewById(R.id.txtModifiedDate);
            ImageView thumbnailImageView = bottomSheetView.findViewById(R.id.imgThumbnail);
            TextView itemCountTextView = bottomSheetView.findViewById(R.id.txtItemCount);

            // Update the views with the loaded data
            creationDateTextView.setText("Creation Date : " + fileNameItem.getCreationDate());
            modifiedDateTextView.setText("Modified Date : " + fileNameItem.getModifiedDate());
            thumbnailImageView.setImageDrawable(fileNameItem.getThumbnailImageView());
            itemCountTextView.setText("Total Number of Files : " + fileNameItem.getItemCount());

            // Set the content view for the bottom sheet
            dialog.setContentView(bottomSheetView);
            dialog.show();


    }
*/
  /*  private void updateDetails(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        if (mFile.exists()) {
            long modifiedTime = mFile.lastModified();
            this.modifiedDate = dateFormat.format(new Date(modifiedTime));

            long creationTime = firstCreationTime(mFile);
            //long creationTime = file.lastModified();
            this.creationDate = dateFormat.format(new Date(creationTime));

            // Set the item count based on the number of files in the folder
            if (mFile.isDirectory()) {
                File[] files = mFile.listFiles();
                if (files != null) {
                    this.itemCount = files.length;
                }
            } else {
                this.itemCount = 1; // If it's a file, itemCount should be 1
            }

        } else {
            this.creationDate = "File not Found!";
            this.modifiedDate = "File not Found!";
        }
    }

    private long firstCreationTime(File folder){

        if (folder.isDirectory()){
            File[] files = folder.listFiles();
            if (files != null && files.length > 0){
                long earliestTime = Long.MAX_VALUE;
                for (File file : files){
                    if (file.exists()){
                        long fileModifiedTime = file.lastModified();
                        if (fileModifiedTime < earliestTime){
                            earliestTime = fileModifiedTime;
                        }
                    }
                }
                return earliestTime;
            }
        }


        return folder.lastModified();
    }
*/

}
