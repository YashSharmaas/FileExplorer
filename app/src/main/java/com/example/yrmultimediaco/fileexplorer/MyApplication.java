package com.example.yrmultimediaco.fileexplorer;

import android.app.Application;

import java.util.List;

public class MyApplication extends Application {
    private List<FileName> selectedFiles;
    public List<FileName> getSelectedFiles() {
        return selectedFiles;
    }
    public void setSelectedFiles(List<FileName> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

}

