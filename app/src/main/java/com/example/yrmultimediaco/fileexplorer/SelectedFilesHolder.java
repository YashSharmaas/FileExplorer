package com.example.yrmultimediaco.fileexplorer;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilesHolder {
    private static SelectedFilesHolder instance;
    private List<FileName> selectedFilesToCopy;

    private SelectedFilesHolder() {
        selectedFilesToCopy = new ArrayList<>();
    }

    public static SelectedFilesHolder getInstance() {
        if (instance == null) {
            instance = new SelectedFilesHolder();
        }
        return instance;
    }

    public List<FileName> getSelectedFilesToCopy() {
        return selectedFilesToCopy;
    }

    public void setSelectedFilesToCopy(List<FileName> selectedFilesToCopy) {
        this.selectedFilesToCopy = selectedFilesToCopy;
    }
}
