package com.example.yrmultimediaco.fileexplorer;

import static android.icu.util.ULocale.getBaseName;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.currentDirectory;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.fastAdapter;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.fileItems;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.isImageFile;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.itemAdapter;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.noFileImage;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.notxtView;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.recView;
import static com.example.yrmultimediaco.fileexplorer.SortBottomSheetFragment.PREFS_NAME;
import static com.example.yrmultimediaco.fileexplorer.SortBottomSheetFragment.SORT_OPTION_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PasteActivity extends AppCompatActivity {

    private RecyclerView recViewPaste;
    //File currentDirectory;
    private Button pasteBtn;
    private MainActivity.SelectedFilesCallback selectedFilesCallback;
    List<AbstractItem> fileItemsFromMainActivity = fileItems;
    private ArrayList<String> sourcePaths;
    //private String destFolderPath;
    //private List<FileName> selectedFilesToCopy;
    private String destinationFolderPath;
    private boolean isMoveOperation;
    private boolean isCopyOperation;
    ImageView closeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste);

        recViewPaste = findViewById(R.id.pasteRecView);
        recViewPaste.setLayoutManager(new LinearLayoutManager(this));

        closeActivity = findViewById(R.id.closeBtn);

        isMoveOperation = getIntent().getBooleanExtra("isMoveOperation", false);
        isCopyOperation = getIntent().getBooleanExtra("isCopyOperation", false);


        Log.d("DEBUG", "isCopyOperation: " + isCopyOperation);
        Log.d("DEBUG", "isMoveOperation: " + isMoveOperation);

        List<FileName> selectedFilesToCopy = ((MyApplication) getApplicationContext()).getSelectedFiles();
        //List<FileName> selectedFilesToMove = ((MyApplication) getApplicationContext()).getSelectedFiles();

        pasteBtn = findViewById(R.id.onCopyButtonClicked);

        if (isMoveOperation){
            pasteBtn.setText("Move To Here");
        } else if (isCopyOperation) {
            pasteBtn.setText("Copy To Here");
        }

        pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("DEBUG", "Selected Files To Copy: " + selectedFilesToCopy);
                if (selectedFilesToCopy != null && !selectedFilesToCopy.isEmpty()) {

                    File destFolder = new File(destinationFolderPath);

                    if (!destFolder.exists()) {
                        destFolder.mkdirs();
                    }

                    // Add a flag to check if the paste operation is successful
                    boolean isPasteSuccessful = true;

                    for (FileName selectedFile : selectedFilesToCopy) {
                        File sourceFile = selectedFile.getFile();
                        Log.d("DEBUG", "Source File: " + sourceFile.getAbsolutePath());
                        if (sourceFile.exists()) {
                            try {
                                if (sourceFile.isFile()) {

                                    if (isMoveOperation) {
                                        moveFile(sourceFile, destFolder);
                                    } else if(isCopyOperation) {
                                        copyFile(sourceFile, destFolder);
                                    }
                                    ///moveFile(sourceFile, destFolder);
                                } else if (sourceFile.isDirectory()) {
                                    // It's a directory, so copy the entire directory to the destination folder
                                    if (isMoveOperation) {
                                        moveDirectory(sourceFile, destFolder);
                                    } else if (isCopyOperation){
                                        copyDirectory(sourceFile, destFolder);
                                    }

                                } else {
                                    Toast.makeText(PasteActivity.this, "Source file does not exist: " + sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                // If an exception occurs, set the flag to false
                                isPasteSuccessful = false;
                                Toast.makeText(PasteActivity.this, "Error copying file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(PasteActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(PasteActivity.this, "Selected file is nullllll", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (isPasteSuccessful) {
                        String operationMessage = "";
                        if (isMoveOperation) {
                            operationMessage = "Move operation successful";
                        } else if (isCopyOperation) {
                            operationMessage = "Copy operation successful";
                        }

                        Toast.makeText(PasteActivity.this, operationMessage + " operation successful", Toast.LENGTH_SHORT).show();

                        Intent mainIntent = new Intent(PasteActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    }
                } else {
                    Toast.makeText(PasteActivity.this, "Selected files/folders list is empty", Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK);
                finish();
            }
        });




        fastAdapter = FastAdapter.with(itemAdapter);

        recViewPaste.setAdapter(fastAdapter);

        fastAdapter.withOnClickListener(new OnClickListener<AbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item, int position) {
                if (item instanceof FileName) {
                    FileName fileName = (FileName) item;
                    File clickedFile = fileName.getFile();
                    if (clickedFile.isDirectory()) {
                        openDirectory(clickedFile);
                    } else {

                    }

                }

                return false;
            }
        });

      /*  Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sourcePaths = extras.getStringArrayList("sourcePaths");
            destFolderPath = extras.getString("destFolderPath");
            selectedFilesToCopy = extras.getParcelableArrayList("selectedFilesToCopy");
            Log.d("DEBUG", "sourcePaths: " + sourcePaths);
            Log.d("DEBUG", "destFolderPath: " + destFolderPath);
            Log.d("DEBUG", "selectedFilesToCopy: " + selectedFilesToCopy);
        }*/

closeActivity.setOnClickListener(v -> {
    startActivity(new Intent(PasteActivity.this, MainActivity.class));
    finish();
});



    }

    /*public void onPasteButtonClicked() {
        if (selectedFilesToCopy != null && !selectedFilesToCopy.isEmpty()) {
            for (FileName selectedFile : selectedFilesToCopy) {
                File sourceFile = selectedFile.getFile();
                // Set the destination folder path here (where you want to paste the file/folder)
                File destFolder = new File(destFolderPath);

                if (isMoveOperation) {
                    //moveFileOrFolder(sourceFile, destFolder);
                } else {
                    pasteFileOrFolder(sourceFile, destFolder);
                }
            }
        } else {
            Toast.makeText(this, "Selected Folder File is Null", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }*/

    @Override
    public void onBackPressed() {
        if (currentDirectory != null && !currentDirectory.equals(Environment.getExternalStorageDirectory())) {
            File parentDirectory = currentDirectory.getParentFile();
            if (parentDirectory != null && parentDirectory.isDirectory()) {
                openDirectory(parentDirectory);

            }
        } else {
            super.onBackPressed();
        }
    }

    private void navigateToDirectory(File directory) {
        openDirectory(directory);
    }


    public void onCopyButtonClicked(View view) {


    }

    private void copyFile(File sourceFile, File destFile) throws IOException {

        //if (destFile.exists()) {
            String baseName = getBaseName(sourceFile.getName());
            String extension = getFileExtension(sourceFile.getName());
            int count = 1;


            File newDestFile = new File(destFile, sourceFile.getName());
            // Keep incrementing the count until we find a unique name
            while (newDestFile.exists()) {
                String newName = baseName + " (" + count + ")" + extension;
                newDestFile = new File(destFile, newName);
                count++;
            }
        //}
        //File newDestFile = destFile;

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(newDestFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    private void copyDirectory(File sourceDir, File destDir) throws IOException {

        if (!sourceDir.exists()) {
            Log.e("DEBUG", "Source file/directory does not exist: " + sourceDir.getAbsolutePath());
            return;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    File newDestDir = new File(destDir, file.getName());
                    copyDirectory(file, newDestDir);
                } else {
                    copyFile(file, destFile);
                }
            }
        }
    }

    private void moveDirectory(File sourceDir, File destDir) throws IOException {
        if (!sourceDir.isDirectory() && !sourceDir.exists() && !destDir.getParentFile().exists()) {
            throw new IllegalArgumentException("Source is not a directory: " + sourceDir.getAbsolutePath());
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File newDestFile = new File(destDir, file.getName());

                Log.d("DEBUG", "Moving: " + sourceDir.getAbsolutePath() + " to " + newDestFile.getAbsolutePath());

                if (file.isDirectory()) {
                    moveDirectory(file, newDestFile);
                } else {
                    moveFile(file, newDestFile);
                }
            }
        }

        sourceDir.delete();
    }


    private void moveFile(File sourceFile, File destFile) throws IOException {

        //if (destFile.exists()) {
        String baseName = getBaseName(sourceFile.getName());
        String extension = getFileExtension(sourceFile.getName());
        int count = 1;


        File newDestFile = new File(destFile, sourceFile.getName());
        // Keep incrementing the count until we find a unique name
        while (newDestFile.exists()) {
            String newName = baseName + " (" + count + ")" + extension;
            newDestFile = new File(destFile, newName);
            count++;
        }
        //}
        //File newDestFile = destFile;

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(newDestFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }

        sourceFile.delete();
    }


    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex != -1 ? fileName.substring(dotIndex) : "";
    }

        /*if (sourcePaths != null && !sourcePaths.isEmpty()) {
            for (String sourcePath : sourcePaths) {
                File sourceFile = new File(sourcePath);
                pasteFileOrFolder(sourceFile, new File(destFolderPath));
                Toast.makeText(this, "FIle/Folder pasted successfully", Toast.LENGTH_SHORT).show();
            }
        }*/

        // Handle selected files/folders from MainActivity
       /* if (selectedFilesToCopy != null) {
            for (FileName selectedFile : selectedFilesToCopy) {
                File sourceFile = selectedFile.getFile();
                // Set the destination folder path here (where you want to copy the file/folder)
                File destFolder = new File(destFolderPath);
                pasteFileOrFolder(sourceFile, destFolder);
                Toast.makeText(this, "FIle/Folder pasted successfully", Toast.LENGTH_SHORT).show();

            }
        }

        setResult(RESULT_OK);
        finish();*/



  /*  private void pasteFileOrFolder(File sourceFile, File destFolder) {
        if (sourceFile.isFile()) {
            // It's a file, so copy the file to the destination folder
            File destFile = new File(destFolder, sourceFile.getName());
            int count = 1;
            while (destFile.exists()) {
                // Handle naming conflicts by appending (1), (2), etc. to the file name
                String newName = getUniqueFileName(sourceFile.getName(), count);
                destFile = new File(destFolder, newName);
                count++;
            }
            try {
                // Perform the actual file copy
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error copying file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // Handle any errors that occur during the file copy
                // For example, you can show a toast or log a message to indicate the error.
            }
        } else if (sourceFile.isDirectory()) {
            // It's a directory, so create the corresponding directory in the destination folder
            File newDestFolder = new File(destFolder, sourceFile.getName());
            int count = 1;
            while (newDestFolder.exists()) {
                String newName = getUniqueDirectoryName(sourceFile.getName(), count);
                newDestFolder = new File(destFolder, newName);
                count++;
            }
            newDestFolder.mkdirs();
            // Recursively copy the contents of the source directory to the new destination directory
            for (File file : sourceFile.listFiles()) {
                pasteFileOrFolder(file, newDestFolder);
            }

        }
    }*/

    private String getUniqueFileName(String fileName, int count) {
        int dotIndex = fileName.lastIndexOf(".");
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        String extension = dotIndex != -1 ? fileName.substring(dotIndex) : "";
        return baseName + " (" + count + ")" + extension;
    }

    private String getUniqueDirectoryName(String directoryName, int count) {
        if (count == 1) {
            return directoryName + " (" + count + ")";
        } else {
            int index = directoryName.lastIndexOf(" (");
            if (index == -1) {
                return directoryName + " (" + count + ")";
            } else {
                return directoryName.substring(0, index) + " (" + count + ")";
            }
        }
    }


    private void fetchExternalStorageData() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        traverseDirectories(externalStorageDirectory);
    }
    private void traverseDirectories(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] filesAndFolders = directory.listFiles();
        if (filesAndFolders == null || filesAndFolders.length == 0) {
            return;
        }

        for (File file : filesAndFolders) {
            if (file.isDirectory()) {
                FileName fileName = new FileName(file);

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

               // fileName.setOnBtnDetailsClickListener(PasteActivity.this);

                // Set the last modified date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fileName.setModifiedDate(dateFormat.format(new Date(file.lastModified())));
                fileItemsFromMainActivity.add(fileName);

                // Recursively traverse subdirectories
                if (file.isDirectory()) {
                    traverseDirectories(file);
                }
            }
        }
    }

    public void openDirectory(File directory) {

        currentDirectory = directory;

        destinationFolderPath = currentDirectory.getAbsolutePath();

        if (currentDirectory == null || !currentDirectory.exists() || !currentDirectory.isDirectory()) {
            Log.e("DEBUG", "Invalid current directory: " + currentDirectory);
            return;
        }

        File[] filesAndFolders = directory.listFiles();

        if (filesAndFolders == null || filesAndFolders.length == 0) {
            //updateHomeAsUpIndicator();
            notxtView.setVisibility(View.VISIBLE);
            noFileImage.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            itemAdapter.clear();
            return;
        }


        notxtView.setVisibility(View.GONE);
        noFileImage.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);

        //updateHomeAsUpIndicator();

        fileItems.clear();
        for (File file : filesAndFolders) {
            if (file.isDirectory() || isImageFile(file)) {
                FileName fileName = new FileName(file);

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

                //fileName.setOnBtnDetailsClickListener(MainActivity.this);

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