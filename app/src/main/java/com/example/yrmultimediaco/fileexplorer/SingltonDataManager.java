package com.example.yrmultimediaco.fileexplorer;

import java.util.ArrayList;
import java.util.List;

public class SingltonDataManager {

    private static SingltonDataManager instance;
    private List<FileName> fileItems;

    private SingltonDataManager() {
        // Private constructor to prevent instantiation from other classes.
        fileItems = new ArrayList<>();
    }

    public static SingltonDataManager getInstance() {
        if (instance == null) {
            instance = new SingltonDataManager();
        }
        return instance;
    }

    public List<FileName> getFileItems() {
        return fileItems;
    }

    public void setFileItems(List<FileName> fileItems) {
        this.fileItems = fileItems;
    }
}
