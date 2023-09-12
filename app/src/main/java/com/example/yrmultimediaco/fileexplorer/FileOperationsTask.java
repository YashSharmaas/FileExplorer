package com.example.yrmultimediaco.fileexplorer;

import android.os.AsyncTask;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileOperationsTask extends AsyncTask<Void, Void, List<AbstractItem>> {

    private File[] filesAndFolders;
    private FileItemsListener listener;

    public FileOperationsTask(File[] filesAndFolders, FileItemsListener listener) {
        this.filesAndFolders = filesAndFolders;
        this.listener = listener;
    }

    @Override
    protected List<AbstractItem> doInBackground(Void... voids) {
        List<AbstractItem> fileItems = new ArrayList<>();

        for (File file : filesAndFolders) {
            if (file.isDirectory()) {
                FileName fileName = new FileName(file);

                // Set the item count based on the number of files in the folder
                if (file.isDirectory() || isImageFile(file)) {
                    File[] filesInDir = file.listFiles();
                    if (filesInDir != null) {
                        fileName.setItemCount(filesInDir.length);
                    } else {
                        fileName.setItemCount(0);
                    }
                } else {
                    fileName.setItemCount(1);
                }

                // Set the last modified date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                fileName.setModifiedDate(dateFormat.format(new Date(file.lastModified())));

                fileItems.add(fileName);
            }
        }

        return fileItems;
    }

    @Override
    protected void onPostExecute(List<AbstractItem> fileItems) {
        listener.onFileItemsLoaded(fileItems);
    }

    public interface FileItemsListener {
        void onFileItemsLoaded(List<AbstractItem> fileItems);
    }

    private boolean isImageFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif"};
        for (String imageExtension : imageExtensions) {
            if (extension.equalsIgnoreCase(imageExtension)) {
                return true;
            }
        }
        return false;
    }

}

