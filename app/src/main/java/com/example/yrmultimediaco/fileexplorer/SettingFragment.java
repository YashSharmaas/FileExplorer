package com.example.yrmultimediaco.fileexplorer;

import static com.example.yrmultimediaco.fileexplorer.MainActivity.currentDirectory;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.fastAdapter;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.fileItems;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.isImageFile;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.itemAdapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangedListner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        preferenceChangedListner = (sharedPreferences, key) -> {
            if (key.equals("key_folder")){
                boolean showHiddenFiles = sharedPreferences.getBoolean(key, false);
                refereshFileListing(showHiddenFiles);
            } else if (key.equals("theme_key")) {
                boolean isLightTheme = sharedPreferences.getBoolean(key, true);
                int themeMode = isLightTheme
                        ? AppCompatDelegate.MODE_NIGHT_NO
                        : AppCompatDelegate.MODE_NIGHT_YES;
                AppCompatDelegate.setDefaultNightMode(themeMode);
                getActivity().recreate();
            }
        };

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangedListner);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangedListner);
    }

    private void refereshFileListing(boolean showHiddenFiles){
        openDirectory(currentDirectory, showHiddenFiles);
    }

    public void openDirectory(File directory, boolean showHiddenFiles){
        currentDirectory = directory;

        if (currentDirectory == null || !currentDirectory.exists() || !currentDirectory.isDirectory()) {
            Log.e("DEBUG", "Invalid current directory: " + currentDirectory);
            return;
        }
        File[] filesAndFolders = directory.listFiles();
        fileItems.clear();

        for (File file : filesAndFolders) {
            if (file.isDirectory() || isImageFile(file)) {
                FileName fileName = new FileName(file);

                if (!showHiddenFiles && file.isHidden()){
                    //fileName.setIconColor(com.mikepenz.materialize.R.color.md_grey_300);
                    //fileName.setIconicsDrawable(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_folder).colorRes(com.mikepenz.materialize.R.color.md_grey_400));
                    Log.d("DEBUG", "Hidden File: " + fileName.mFile.getName());
                    continue;
                }

                // Set the item count based on the number of files in the folder
                if (file.isDirectory()) {
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fileName.setModifiedDate(dateFormat.format(new Date(file.lastModified())));
                fileItems.add(fileName);

            }
        }

        itemAdapter.setNewList(fileItems);

        fastAdapter.notifyAdapterDataSetChanged();

    }

}
