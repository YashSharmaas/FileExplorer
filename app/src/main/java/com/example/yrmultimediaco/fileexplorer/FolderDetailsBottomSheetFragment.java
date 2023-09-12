package com.example.yrmultimediaco.fileexplorer;

import static com.example.yrmultimediaco.fileexplorer.MainActivity.FOLDER_COPY;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.FOLDER_DELTE;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.FOLDER_INFO;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.FOLDER_MOVE;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.FOLDER_RENAME;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FolderDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "FolderBottomSheetFragment";

    public static FolderDetailsBottomSheetFragment newInstance(){
        return new FolderDetailsBottomSheetFragment();
    }

    public interface FolderBottomSelectedListner{
        void onFolderOptionSelected(int folderOption);
    }

    private  FolderBottomSelectedListner mFolderBottomSelectedListner;

    public void setFolderBottomSelectedListner(FolderBottomSelectedListner folderBottomSelectedListner) {
        mFolderBottomSelectedListner = folderBottomSelectedListner;
    }

    private View view;
    private FileName fileNameItem;

    public void setFileNameItem(FileName fileNameItem) {
        this.fileNameItem = fileNameItem;
    }

    @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_folder_details,null);
        dialog.setContentView(view);

            ImageView moreInfo = view.findViewById(R.id.moreInfo);
            moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderBottomSelectedListner != null){
                        mFolderBottomSelectedListner.onFolderOptionSelected(FOLDER_INFO);
                        dismiss();
                    }
                }
            });

            ImageView rename = view.findViewById(R.id.renameFolderFile);
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderBottomSelectedListner != null){
                        mFolderBottomSelectedListner.onFolderOptionSelected(FOLDER_RENAME);
                        dismiss();
                    }
                }
            });ImageView move = view.findViewById(R.id.moveFolderFile);
            move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderBottomSelectedListner != null){
                        mFolderBottomSelectedListner.onFolderOptionSelected(FOLDER_MOVE);
                        dismiss();
                    }
                }
            });ImageView copy = view.findViewById(R.id.copyFolderFile);
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderBottomSelectedListner != null){
                        mFolderBottomSelectedListner.onFolderOptionSelected(FOLDER_COPY);
                    dismiss();
                    }
                }
            });ImageView delete = view.findViewById(R.id.deleteFolderFile);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderBottomSelectedListner != null){
                        mFolderBottomSelectedListner.onFolderOptionSelected(FOLDER_DELTE);
                        dismiss();
                    }
                }
            });

/*
            // Get the folder details passed from the adapter
            Bundle args = getArguments();
            if (args != null) {
                String creationDate = args.getString("creationDate");
                String modifiedDate = args.getString("modifiedDate");
                int itemCount = args.getInt("itemCount");
                int thumbnailResourceId = args.getInt("thumbnailResourceId");

                // Set the folder details in the BottomSheetDialogFragment UI
                TextView creationDateTextView = view.findViewById(R.id.txtCreationDate);
                TextView modifiedDateTextView = view.findViewById(R.id.txtModifiedDate);
                TextView itemCountTextView = view.findViewById(R.id.txtItemCount);
                ImageView thumbnailImageView = view.findViewById(R.id.imgThumbnail); // Assuming you have an ImageView with id thumbnailImageView

                creationDateTextView.setText(creationDate);
                modifiedDateTextView.setText(modifiedDate);
                itemCountTextView.setText(String.valueOf(itemCount));
                thumbnailImageView.setImageResource(thumbnailResourceId);
            }*/

            return dialog;
        }

        /*private void showFolderDialog(){

            Dialog folderDetailsDialog = new Dialog(requireContext());
            folderDetailsDialog.setContentView(R.layout.dialog_folder_sheet);

            TextView dialogCreationDate = folderDetailsDialog.findViewById(R.id.txtCreationDate);
            TextView dialogModifiedDate  = folderDetailsDialog.findViewById(R.id.txtModifiedDate);
            TextView dialogItemCount = folderDetailsDialog.findViewById(R.id.itemCount);
            ImageView thumbnaillImageView = folderDetailsDialog.findViewById(R.id.imgThumbnail);


            // Get the folder details passed from the adapter
            Bundle args = getArguments();
            if (args != null) {
                String creationDate = args.getString("creationDate");
                String modifiedDate = args.getString("modifiedDate");
                int itemCount = args.getInt("itemCount");
                int thumbnailResourceId = args.getInt("thumbnailResourceId");

                // Set the folder details in the BottomSheetDialogFragment UI
                TextView creationDateTextView = dialogCreationDate.findViewById(R.id.txtCreationDate);
                TextView modifiedDateTextView = dialogModifiedDate.findViewById(R.id.txtModifiedDate);
                TextView itemCountTextView = dialogItemCount.findViewById(R.id.txtItemCount);
                ImageView thumbnailImageView = thumbnaillImageView.findViewById(R.id.imgThumbnail); // Assuming you have an ImageView with id thumbnailImageView

                creationDateTextView.setText(creationDate);
                modifiedDateTextView.setText(modifiedDate);
                itemCountTextView.setText(String.valueOf(itemCount));
                thumbnailImageView.setImageResource(thumbnailResourceId);

                folderDetailsDialog.show();
            }

        }*/

    }



