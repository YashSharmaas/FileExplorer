package com.example.yrmultimediaco.fileexplorer;

import static com.example.yrmultimediaco.fileexplorer.SortBottomSheetFragment.PREFS_NAME;
import static com.example.yrmultimediaco.fileexplorer.SortBottomSheetFragment.SORT_OPTION_KEY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SortBottomSheetFragment.SortOptionSelectedListener, FileName.OnBtnDetailsClickListener, FolderDetailsBottomSheetFragment.FolderBottomSelectedListner, ItemTouchCallback {


    private static final int REQUEST_CODE_FOLDER_SELECTION = 100;
    private Parcelable recyclerState;
    private List<SearchViewAdapter> searchViewAdapters;
    private SharedPreferences sharedPreferences;
    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        Collections.swap(itemAdapter.getAdapterItems(), oldPosition, newPosition);
        fastAdapter.notifyAdapterItemMoved(oldPosition, newPosition);

        updateItemOrderInSharedPreferences();


        return true;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {
        updateItemOrderInSharedPreferences();

    }

    public interface SelectedFilesCallback {
        List<FileName> getSelectedFiles();
    }
    DBHelper mDBHelper;
    LottieAnimationView mLottieAnimationView;
    private static final long ITEM_SETTINGS_ID = 3;
    private int lastVisibleItemPosition = 0;
    private static final String SCROLL_POSITION_KEY = "scroll_position";
    private static String selectedFolderPath;
    private ExtendedFloatingActionButton newFolderFab;
    private ProgressDialog progressDialog;
    private static final String STATE_IS_GRID_VIEW = "state_is_grid_view";
    private boolean isItemSelected = false;
    private static final String PREF_LAYOUT_MODE = "layout_mode";
    private static final String STATE_IS_THEME_MODE = "isDarkMode";
    private static final String PREF_THEME_MODE = "theme_mode";
    private static final int REQUEST_PERMISSION_CODE = 100;
    private int lastSortOption = -1;
    private boolean isDarkModeEnabled = false;
    private boolean isGridView = false;
    private ExtendedFloatingActionButton fabAddFolder;
    private ActionModeHelper<AbstractItem> mActionModeHelper;

    static final List<AbstractItem> fileItems = new ArrayList<>();
    public static List<AbstractItem> getFileItemsList() {
        return fileItems;
    }
    static final int SORT_BY_NAME_ASCENDING = 1;
    static final int SORT_BY_NAME_DESCENDING = 2;
    static final int SORT_BY_DATE_ASCENDING = 3;
    static final int SORT_BY_DATE_DESCENDING = 4;
    private static final int DEFAULT_SORT_OPTION = SORT_BY_DATE_ASCENDING;
    static final int FOLDER_RENAME = 11;
    static final int FOLDER_INFO = 22;
    static final int FOLDER_DELTE = 33;
    static final int FOLDER_COPY = 44;
    static final int FOLDER_MOVE = 55;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private int permissionRequestCount = 0;
    static RecyclerView recView;
    static TextView notxtView;
    static ImageView noFileImage;
    TextView clearRecentSearches;
    static FastAdapter<AbstractItem> fastAdapter;
    static ItemAdapter<AbstractItem> itemAdapter;
    static File currentDirectory;
    static final int REQUEST_CHILD_DIRECTORY = 1;
    Toolbar mToolbar;
    RecyclerView headerRecView;
    static ItemAdapter<AbstractItem> pathAdapter;
    static FastAdapter<AbstractItem> pathFastAdapter;
    static List<AbstractItem> headerItems = new ArrayList<>();
    private int selectedItemCount = 0;
    HashSet<AbstractItem> selectedItemsPositions = new HashSet<AbstractItem>();
    RecyclerView recyclerViewSearch;
    FastItemAdapter<SearchViewAdapter> fastItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*isDarkModeEnabled = loadThemeMode();

        // Set the appropriate theme based on the loaded theme mode
        if (isDarkModeEnabled) {
            setThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
        }*/

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isLightTheme = getThemePreference(); // Get the theme preference from SharedPreferences
        int themeResId = isLightTheme ? R.style.Base_Theme_FileExplorer : R.style.CustomTheme_Dark;
        setTheme(themeResId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);


        headerRecView = findViewById(R.id.navRecView);
        headerRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        pathAdapter = new ItemAdapter<>();
        pathFastAdapter = FastAdapter.with(pathAdapter);

        headerRecView.setAdapter(pathFastAdapter);
        mShimmerFrameLayout = findViewById(R.id.shimmer);
        mLottieAnimationView = findViewById(R.id.noFilesFound);

        FileName fileNameItem = new FileName(Environment.getExternalStorageDirectory());

        fileNameItem.setOnBtnDetailsClickListener(this);
        mDBHelper = new DBHelper(MainActivity.this);

        // Update header RecyclerView with the current folder path
        //updateFolderPathRecView();

        //fabAddFolder = findViewById(R.id.fabAddFolder);
        recView = findViewById(R.id.filesRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setLayoutManager(layoutManager);
        recView.setAdapter(fastAdapter);

//        recentSearch = findViewById(R.id.rectSec);


        clearRecentSearches = findViewById(R.id.clearAlltxt);
        recyclerViewSearch = findViewById(R.id.searchViewRec);

        List<String> searchHistoryList = mDBHelper.getAllSearchHistory();
        searchViewAdapters = new ArrayList<>();

        for (String searchQuery : searchHistoryList) {
            searchViewAdapters.add(new SearchViewAdapter(searchQuery));
        }

        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.set(searchViewAdapters);
        fastItemAdapter.notifyAdapterDataSetChanged();

        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setAdapter(fastItemAdapter);

        clearRecentSearches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Clear Searches!");
                builder.setMessage("Are you sure to clear your all recent searches?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.clearAllSearchHistory();

                        searchViewAdapters.clear();
                        fastItemAdapter.set(searchViewAdapters);
                        fastItemAdapter.notifyAdapterDataSetChanged();
                        clearRecentSearches.setVisibility(View.GONE);
                        recyclerViewSearch.setVisibility(View.GONE);
                        //openDirectory(currentDirectory);kjhui
                        Toast.makeText(MainActivity.this, "Search Cleared", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

               AlertDialog alertDialog = builder.create();
               alertDialog.show();
            }
        });

        //this code works on when my layout is in Constraint layout
       /* recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                int totalItemCount = layoutManager.getItemCount();

                Guideline guideline = findViewById(R.id.guideline);
                float guidePosition = (float) (lastVisibleItemPosition + 1) / totalItemCount;

                // Update the guideline position using layout params
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
                params.guidePercent = guidePosition;
                guideline.setLayoutParams(params);
            }
        });*/

        newFolderFab = findViewById(R.id.fabAddFolder);

        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && newFolderFab.getVisibility() == View.VISIBLE) {
                    newFolderFab.hide();
                } else if (dy < 0 && newFolderFab.getVisibility() != View.VISIBLE) {
                    newFolderFab.show();
                }

            }
        });

        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);

       /* AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        ExtendedFloatingActionButton fabAddFolder = findViewById(R.id.fabAddFolder);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

                // Adjust the visibility of the fabAddFolder button based on the scroll percentage
                if (percentage >= 0.8) { // Adjust this threshold as needed
                    fabAddFolder.show();
                } else {
                    fabAddFolder.hide();
                }
            }
        });*/

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY + 12 && newFolderFab.isExtended()) {
                    newFolderFab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !newFolderFab.isExtended()) {
                    newFolderFab.extend();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    newFolderFab.extend();
                }
            }
        });

        notxtView = findViewById(R.id.noText);
        noFileImage = findViewById(R.id.noFiles);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        newFolderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.MANAGE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

                    createNewFolder();
                }*/
                addNewFolder();

            }
        });

        final ViewStub shimmerStub = findViewById(R.id.shimmerStub);
        shimmerStub.setLayoutResource(R.layout.shimmer_layout);
        final View shimmerLayout = shimmerStub.inflate();
        shimmerLayout.setVisibility(View.VISIBLE);

        mShimmerFrameLayout.startShimmer();
        mShimmerFrameLayout.setVisibility(View.VISIBLE);

        if (checkPermission()) {

            if (currentDirectory == null) {
                currentDirectory = Environment.getExternalStorageDirectory();
                openDirectory(currentDirectory);
                //toogleShimmerAndRecyclerView(false);
                //fetchingAllFiles();
            }
        } else {

                requestPermission();

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.setVisibility(View.GONE);

                // Hide the inflated shimmer layout
                shimmerLayout.setVisibility(View.GONE);
            }
        },1000);

        fastAdapter.withOnClickListener(new OnClickListener<AbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item, int position) {

                if (mActionModeHelper.isActive()) {

                    if (item instanceof FileName){
                        FileName fileNameItem = (FileName) item;
                        if (fileNameItem.isSelected()) {
                            fileNameItem.setSelected(false);
                            selectedItemCount--;
                        } else {
                            fileNameItem.setSelected(true);
                            selectedItemCount++;
                        }

                        //fileNameItem.setSelected(!item.isSelected());

                    }

                    updateActionModeTitle(selectedItemCount);
                    fastAdapter.notifyAdapterDataSetChanged();

                    return true;
                } else {
                    if (item instanceof FileName) {
                        FileName fileName = (FileName) item;
                        //showBottomSheet(fileName);
                        File clickedFile = fileName.getFile();
                        if (clickedFile.isDirectory()) {
                            openDirectory(clickedFile);
                        } else {
                            openFile(clickedFile);
                        }
                        //return true;

                    }
                    //File clickedFile = item.getFile();


                    return false;
                }
            }



        });

        fastAdapter.withSelectable(true);
        fastAdapter.withMultiSelect(true);
        fastAdapter.withSelectOnLongClick(true);
        fastAdapter.withSelectWithItemUpdate(true);

        fastAdapter.withOnPreLongClickListener(new OnLongClickListener<AbstractItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item, int position) {
                ActionMode actionMode = mActionModeHelper.onLongClick(MainActivity.this, position);
                if (actionMode != null){
                    RecyclerView.ViewHolder viewHolder = recView.findViewHolderForAdapterPosition(position);
                    if (viewHolder instanceof FileName.ViewHolder){
                        FileName.ViewHolder filenaneViewHolder = (FileName.ViewHolder) viewHolder;
                        if (item instanceof FileName){
                            FileName fileNameItem = (FileName) item;
                            fileNameItem.setSelected(!item.isSelected());
                            filenaneViewHolder.clickImage.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
                        }
                    }

                    selectedItemCount++;
                    updateActionModeTitle(selectedItemCount);
                    fastAdapter.notifyAdapterDataSetChanged();
                }
                //toggleSelection(item);
                return actionMode != null;
            }
        });

        /*fastAdapter.withOnLongClickListener(new OnLongClickListener<AbstractItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item, int position) {

                if (item instanceof NavigationAdapter) {
                    NavigationAdapter nav = (NavigationAdapter) item;
                    *//*if (position > 0) {
                        // Get the clicked folder's path from the NavigationAdapter
                        String folderPath = nav.getFolderPath();

                        // Open the clicked folder by calling openDirectory with the new folder's path
                        openDirectory(new File(folderPath));
                    }*//*
                }
                ActionMode actionMode = mActionModeHelper.onLongClick(MainActivity.this, position);
                if (actionMode != null){
                    RecyclerView.ViewHolder viewHolder = recView.findViewHolderForAdapterPosition(position);
                    if (viewHolder instanceof FileName.ViewHolder){
                        FileName.ViewHolder filenaneViewHolder = (FileName.ViewHolder) viewHolder;
                        if (item instanceof FileName){
                            FileName fileNameItem = (FileName) item;
                            fileNameItem.setSelected(!item.isSelected());
                            filenaneViewHolder.clickImage.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
                        }
                    }

                    selectedItemCount++;
                    updateActionModeTitle(selectedItemCount);
                    fastAdapter.notifyAdapterDataSetChanged();
                }
                //toggleSelection(item);
                return actionMode != null;
            }
        });
*/

        SimpleDragCallback dragCallback;
        dragCallback = new SimpleDragCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
        touchHelper.attachToRecyclerView(recView);

        mActionModeHelper = new ActionModeHelper<>(fastAdapter, R.menu.cab, new ActionBarCallback());


        pathFastAdapter.withOnClickListener(new OnClickListener<AbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item, int position) {
                //Log.d("DEBUG", "onClick: Item clicked!");
                if (item instanceof NavigationAdapter) {
                    NavigationAdapter navigationAdapter = (NavigationAdapter) item;
                    String folderPath = navigationAdapter.getFolderPath();

                   // Log.d("DEBUG", "Selected Folder: " + folderPath);

                    // If the clicked item is the root "Internal Storage", set the currentDirectory to root
                    if (folderPath.equals("Internal Storage")) {
                        currentDirectory = Environment.getExternalStorageDirectory();
                    } else {
                        currentDirectory = new File(folderPath);
                    }


                    int clickedPosition = -1;
                    for (int i = 0; i < headerItems.size(); i++) {
                        AbstractItem headerItem = headerItems.get(i);
                        if (headerItem instanceof NavigationAdapter) {
                            NavigationAdapter navItem = (NavigationAdapter) headerItem;
                            if (navItem.getFolderPath().equals(folderPath)) {
                                clickedPosition = i;
                                break;
                            }
                        }
                    }

                    if (clickedPosition > 0) {
                        int itemToRemove = headerItems.size() - clickedPosition;
                        for (int i = 0; i < itemToRemove; i++) {
                            headerItems.remove(headerItems.size() - 1);
                        }
                    }

                    pathAdapter.set(headerItems);
                    pathFastAdapter.notifyAdapterDataSetChanged();

                    if (currentDirectory.exists() && currentDirectory.isDirectory()) {
                        openDirectory(currentDirectory);

                        //Toast.makeText(MainActivity.this, "Clicked Folder: " + folderPath, Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("DEBUG", "Directory not found or is not a directory");
                        Toast.makeText(MainActivity.this, "Folder not found or is not a directory", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        boolean isRestoredFromInstanceState = false;

        // Load the layout mode from shared preferences
        isGridView = loadLayoutMode();


        if (savedInstanceState != null) {
            isGridView = savedInstanceState.getBoolean(STATE_IS_GRID_VIEW, false);

            isDarkModeEnabled = savedInstanceState.getBoolean(STATE_IS_THEME_MODE, false);

            recyclerState = savedInstanceState.getParcelable("recycler_state");

            isRestoredFromInstanceState = true;
        } else {
            // If there's no saved instance state, load the layout mode from SharedPreferences
            isGridView = loadLayoutMode();
        }

        /*if (!isRestoredFromInstanceState) {
            // If not restored from instance state, load the theme mode from SharedPreferences
            isDarkModeEnabled = loadThemeMode();
        }*/


       // isDarkModeEnabled = loadThemeMode();




        // Set the layout manager based on the loaded layout mode
        setLayoutManager();

        restoreItemOrderFromSharedPreferences();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int lastSortOption = sharedPreferences.getInt(SORT_OPTION_KEY, DEFAULT_SORT_OPTION);

        // Apply the last selected sorting option
        applySorting(lastSortOption);

        //setThemeMode(isDarkModeEnabled);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateHomeAsUpIndicator();
        //getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_arrow_bottom_left).sizeDp(24));

        EventBus.getDefault().register(this);
        SingltonDataManager.getInstance().getFileItems();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("File Explorer").withIcon(getResources().getDrawable(R.drawable.file_explorer))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home).withIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_home));
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Sorting Items").withIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_sort));
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName("Create Folder").withIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_plus_box));
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName("Bookmarks").withIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_star_box));
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_settings).withIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_settings));


        //View stickyFooterView = LayoutInflater.from(this).inflate(R.layout.sticky_footer_layout, null);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(mToolbar)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4,
                        item5,
                        new SecondaryDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        long identifier = drawerItem.getIdentifier();
                        if (identifier == 1){
                            currentDirectory = Environment.getExternalStorageDirectory();
                            openDirectory(currentDirectory);
                        }
                        else if (identifier == 2) {
                            showSortBottomSheet();
                        } else if (identifier == 3) {
                            addNewFolder();

                        } else if (identifier == 4) {
                            startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                        } else if (identifier == 5) {
                            openSettings();
                        }
                        return false;
                    }
                })
                //.withFooter(stickyFooterView)
                .build();

//set the selection to the item with the identifier 1
        //result.setSelection(1);

        result.addItem(new DividerDrawerItem());
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("Privacy Policy"));
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("Terms of Service"));

/*//set the selection to the item with the identifier 2
        result.setSelection(item2);
//set the selection and also fire the `onItemClick`-listener
        result.setSelection(1, true);*/
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
    private boolean getThemePreference() {
        // Replace "theme_key" with the actual preference key for the theme
        return sharedPreferences.getBoolean("theme_key", true);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_GRID_VIEW, isGridView);

        outState.putBoolean(STATE_IS_THEME_MODE, isDarkModeEnabled);

        outState.putParcelable("recycler_state", recView.getLayoutManager().onSaveInstanceState());


    }

    private void updateItemOrderInSharedPreferences() {
        List<AbstractItem> items = itemAdapter.getAdapterItems();

        // Create a string representation of the item order
        StringBuilder orderStringBuilder = new StringBuilder();


        for (AbstractItem item : items) {
            if (item instanceof FileName) {
                FileName fileNameItem = (FileName) item;
                // Append the name of the file to the order string
                orderStringBuilder.append(fileNameItem.mFile.getName()).append(",");
            }
        }
        Log.d("ItemOrder", "Generated Order: " + orderStringBuilder);

        // Save the item order string to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("drag_drop_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("item_order", orderStringBuilder.toString());
        editor.apply();


    }

    private void restoreItemOrderFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("drag_drop_prefs", MODE_PRIVATE);
        String itemOrderString = sharedPreferences.getString("item_order", "");

        Log.d("ItemOrder", "Restored Order: " + itemOrderString);

        if (!itemOrderString.isEmpty()) {
            String[] itemOrderArray = itemOrderString.split(",");
            List<AbstractItem> items = itemAdapter.getAdapterItems();
            List<AbstractItem> newOrder = new ArrayList<>(items.size());

            for (String identifier : itemOrderArray) {
                for (AbstractItem item : items) {
                    if (item instanceof FileName){
                        FileName fileNameItem = (FileName) item;
                        if (fileNameItem.mFile.getName().equals(identifier)){
                            newOrder.add(item);
                            break;
                        }
                    }
                }
            }

            // Update the item adapter with the restored item order
            itemAdapter.set(newOrder);
        }
    }



    private void toggleSelection(AbstractItem item) {

        if (selectedItemsPositions.contains(item)) {
            selectedItemsPositions.remove(item);
        } else {
            selectedItemsPositions.add(item);
        }

        item.withSetSelected(!item.isSelected());
        fastAdapter.notifyAdapterDataSetChanged();
        updateActionModeTitle(fastAdapter.getSelectedItems().size());
    }



    /*private void updateFolderPathRecView() {
        if (currentDirectory != null) {
            String currentPath = currentDirectory.getAbsolutePath();
            String[] pathComponents = currentPath.split(File.separator);

            StringBuilder folderPathBuilder = new StringBuilder();
            headerRecView.setVisibility(View.VISIBLE);

            // Clear the headerItems list and the headerItemPaths HashMap
            headerItems.clear();
            headerItemPaths.clear();

            // Get the root directory path, which is "/storage/emulated/0" for most devices
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

            // Check if the current path contains the root directory path and set it as "Internal Storage"
            if (currentPath.contains("/storage") || currentPath.contains("emulated") || currentPath.contains("0")) {
                // Add the root folder item (storage) at the beginning
                headerItems.add(new NavigationAdapter(rootPath, "Internal Storage"));
                headerItemPaths.put(headerItems.size() - 1, rootPath); // Put the root folder item's position and path in the HashMap
            }

            String accumulatedPath = "";
            int currentIndex = -1;

            for (int i = 1; i < pathComponents.length; i++) {
                folderPathBuilder.append(File.separator).append(pathComponents[i]);
                String folderPath = folderPathBuilder.toString();

                // Check if the current path matches the current directory's path
                if (folderPath.equals(currentPath)) {
                    currentIndex = i;
                }

                // Only add items to the headerItems starting from the currentIndex
                if (currentIndex != -1) {
                    String folderName = new File(folderPath).getName();
                    headerItems.add(new NavigationAdapter(accumulatedPath + File.separator + folderName, folderPath));
                    headerItemPaths.put(headerItems.size() - 1, folderPath); // Put the item's position and path in the HashMap
                }

                accumulatedPath = folderPath;
            }

            // Update the pathAdapter with the headerItems and notify the adapter
            pathAdapter.set(headerItems);
            pathFastAdapter.notifyAdapterDataSetChanged();
        }
    }*/

    /*private void updateFolderPathRecView() {
        if (currentDirectory != null) {
            String currentPath = currentDirectory.getAbsolutePath();
            String[] pathComponents = currentPath.split(File.separator);

            headerRecView.setVisibility(View.VISIBLE);

            // Clear the headerItems list
            headerItems.clear();

            // Get the root directory path, which is "/storage/emulated/0" for most devices
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

            // Add the root path (Internal Storage) to the headerItems list
            NavigationAdapter rootAdapter = new NavigationAdapter("Internal Storage", rootPath);
            headerItems.add(rootAdapter);

            // Keep track of the accumulated path
            StringBuilder accumulatedPath = new StringBuilder(rootPath);

            // Loop through each path component and add them to headerItems
            for (int i = 1; i < pathComponents.length; i++) {
                String pathComponent = pathComponents[i];
                if (!pathComponent.isEmpty()) {
                    accumulatedPath.append(File.separator).append(pathComponent);
                    File componentFile = new File(accumulatedPath.toString());
                    if (componentFile.exists() && componentFile.isDirectory()) {
                        String folderName = componentFile.getName();
                        NavigationAdapter adapterItem = new NavigationAdapter(folderName, accumulatedPath.toString());
                        headerItems.add(adapterItem);

                    }
                }
            }

            // Update the pathAdapter with the headerItems and notify the adapter
            pathAdapter.set(headerItems);
            pathFastAdapter.notifyAdapterDataSetChanged();
        }
    }*/


    public void updateHeaderView(String path) {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            if (path.equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
                actionBar.setTitle(R.string.app_name);
            } else{
                actionBar.setTitle(new File(path).getName());
            }
        }

        String[] pathComponents = path.split(File.separator);
        String currentPath = "";

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        headerItems.clear();

        // Check if "Internal Storage" item is already present in headerItems
        boolean isInternalStoragePresent = false;
        for (AbstractItem item : headerItems) {
            if (item instanceof NavigationAdapter) {
                NavigationAdapter navItem = (NavigationAdapter) item;
                if (navItem.getFolderPath().equals(rootPath)) {
                    isInternalStoragePresent = true;
                    break;
                }
            }
        }

        // If "Internal Storage" is not present, add it as the first item in the header view
        if (!isInternalStoragePresent) {
            headerItems.add(new NavigationAdapter(rootPath, "Internal Storage"));
        }

        for (String component : pathComponents) {
            if (!component.isEmpty()) {
                currentPath += File.separator + component;

                if (component.equals("storage") || component.equals("emulated") || component.equals("0")) {
                    currentPath = rootPath; // Replace with Internal Storage path
                }

                boolean isPathPresent = false;

                // Check if the current path is already present in headerItems
                for (AbstractItem item : headerItems) {
                    if (item instanceof NavigationAdapter) {
                        NavigationAdapter navItem = (NavigationAdapter) item;
                        if (navItem.getFolderPath().equals(currentPath)) {
                            isPathPresent = true;
                            break;
                        }
                    }
                }

                // If the current path is not present, add it to the header view
                if (!isPathPresent) {
                    headerItems.add(new NavigationAdapter(currentPath, component));
                }
            }
        }

        pathAdapter.set(headerItems);
        pathFastAdapter.notifyDataSetChanged();
    }

    private void showDetailsDialog(FileName fileNameItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_folder_sheet, null);

        // Initialize and set up the views in the dialog layout
        TextView creationDateTextView = dialogView.findViewById(R.id.txtCreationDate);
        TextView modifiedDateTextView = dialogView.findViewById(R.id.txtModifiedDate);
        ImageView thumbnailImageView = dialogView.findViewById(R.id.imgThumbnail);
        TextView itemCountTextView = dialogView.findViewById(R.id.txtItemCount);

        long firstCreationTime = firstCreationTime(fileNameItem.getFile());

        // Update the views with the file details
        creationDateTextView.setText("Creation Date: " + getFormattedDate(firstCreationTime));
        modifiedDateTextView.setText("Modified Date: " + fileNameItem.getModifiedDate());
        thumbnailImageView.setImageDrawable(fileNameItem.getThumbnailImageView());
        itemCountTextView.setText("Total Number of Files: " + fileNameItem.getItemCount());



        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getFormattedDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
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

    private void openBottomSheet(FileName fileNameItem) {
        // Check if the bottom sheet fragment is already added
        FragmentManager fragmentManager = getSupportFragmentManager();
        FolderDetailsBottomSheetFragment bottomSheetFragment = (FolderDetailsBottomSheetFragment) fragmentManager.findFragmentByTag(FolderDetailsBottomSheetFragment.TAG);

        if (bottomSheetFragment == null) {
            // The bottom sheet fragment is not added, so create and show it
            bottomSheetFragment = FolderDetailsBottomSheetFragment.newInstance();
            bottomSheetFragment.setFileNameItem(fileNameItem);
            bottomSheetFragment.setFolderBottomSelectedListner(this);
            bottomSheetFragment.show(fragmentManager, FolderDetailsBottomSheetFragment.TAG);
        }
    }





    @Override
    public void onFolderOptionSelected(int folderOption) {
        switch (folderOption) {
            case FOLDER_COPY:
                List<FileName> selectedFilesToCopy = getSelectedFiles();

               ((MyApplication) getApplicationContext()).setSelectedFiles(selectedFilesToCopy);

                for (FileName fileName : selectedFilesToCopy) {
                    Log.d("DEBUG", "Selected File: " + fileName.getFile().getAbsolutePath());
                }

                Log.d("DEBUG", "Selected Files To Copy: " + selectedFilesToCopy);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        if (!selectedFilesToCopy.isEmpty()) {

                            //showFolderToast("Move operation clicked");

                            String destinationFolderPath = currentDirectory.getAbsolutePath();

                            // When starting the PasteActivity
                            Intent moveIntent = new Intent(MainActivity.this, PasteActivity.class);
                            //moveIntent.putExtra("folderOption", FOLDER_MOVE);
                           // moveIntent.putParcelableArrayListExtra("selectedFilesToCopy", (ArrayList<? extends Parcelable>) selectedFilesToCopy);
                            moveIntent.putExtra("isCopyOperation", true);
                            //moveIntent.putExtra("operationType", "move");
                            moveIntent.putExtra("destinationFolderPath", destinationFolderPath);
                            startActivity(moveIntent);
                            startActivityForResult(moveIntent, REQUEST_CODE_FOLDER_SELECTION);
                            clearSelectionItem();
                        }  else {
                            //showFolderToast("Selected File is null");
                            Toast.makeText(this, "Selected File is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    permissionDialog();
                    }
                } else {
                    if (!selectedFilesToCopy.isEmpty()) {

                        //showFolderToast("Move operation clicked");

                        String destinationFolderPath = currentDirectory.getAbsolutePath();

                        // When starting the PasteActivity
                        Intent moveIntent = new Intent(MainActivity.this, PasteActivity.class);
                        //moveIntent.putExtra("folderOption", FOLDER_MOVE);
                        //moveIntent.putParcelableArrayListExtra("selectedFilesToCopy", (ArrayList<? extends Parcelable>) selectedFilesToCopy);
                        moveIntent.putExtra("isCopyOperation", true);
                        //moveIntent.putExtra("operationType", "move");
                        moveIntent.putExtra("destinationFolderPath", destinationFolderPath);
                        startActivity(moveIntent);
                        startActivityForResult(moveIntent, REQUEST_CODE_FOLDER_SELECTION);
                        clearSelectionItem();
                    }  else {
                        Toast.makeText(this, "Selected File is null", Toast.LENGTH_SHORT).show();
                    }
                }
                clearSelectionItem();
                applySorting(lastSortOption);

                break;
            case FOLDER_DELTE:
                //showFolderToast("Delete operation clicked");

                List<FileName> selectedFiles = getSelectedFiles();
                //deleteSelectedFiles(selectedFiles);


                if (selectedFiles.isEmpty()) {
                    // No files selected, show a message to the user
                    //showFolderToast("No files selected for deletion.");
                } else {
                    // Call the deleteSelectedFiles() method to perform the deletion
                    deleteSelectedFiles(selectedFiles);
                    clearSelectionItem();
                }

                break;
            case FOLDER_INFO:
                List<FileName> selectedFilesInfo = getSelectedFiles();
                if (!selectedFilesInfo.isEmpty()){
                    for (FileName selectedFileNames : selectedFilesInfo){
                        showDetailsDialog(selectedFileNames);
                        clearSelectionItem();
                    }
                } else {
                    Toast.makeText(this, "No Selected Files", Toast.LENGTH_SHORT).show();
                }

                //showFolderToast("info operation clicked");
                break;
            case FOLDER_MOVE:


                    List<FileName> selectedFilesToMove = getSelectedFiles();

                    ((MyApplication) getApplicationContext()).setSelectedFiles(selectedFilesToMove);

                    for (FileName fileName : selectedFilesToMove) {
                        Log.d("DEBUG", "Selected File: " + fileName.getFile().getAbsolutePath());
                    }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        if (!selectedFilesToMove.isEmpty()) {

                            //showFolderToast("Move operation clicked");

                            String destinationFolderPath = currentDirectory.getAbsolutePath();

                            // When starting the PasteActivity
                            Intent moveIntent = new Intent(MainActivity.this, PasteActivity.class);
                            //moveIntent.putExtra("folderOption", FOLDER_MOVE);
                            //moveIntent.putParcelableArrayListExtra("selectedFilesToMove", (ArrayList<? extends Parcelable>) selectedFilesToMove);
                            moveIntent.putExtra("isMoveOperation", true);
                            //moveIntent.putExtra("operationType", "move");
                            moveIntent.putExtra("destinationFolderPath", destinationFolderPath);
                            startActivity(moveIntent);
                            startActivityForResult(moveIntent, REQUEST_CODE_FOLDER_SELECTION);
                            clearSelectionItem();
                        }  else {
                            Toast.makeText(this, "Selected File is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                    permissionDialog();
                    }
                } else {
                    if (!selectedFilesToMove.isEmpty()) {

                        //showFolderToast("Move operation clicked");

                        String destinationFolderPath = currentDirectory.getAbsolutePath();

                        // When starting the PasteActivity
                        Intent moveIntent = new Intent(MainActivity.this, PasteActivity.class);
                        //moveIntent.putExtra("folderOption", FOLDER_MOVE);
                        //moveIntent.putParcelableArrayListExtra("selectedFilesToMove", (ArrayList<? extends Parcelable>) selectedFilesToMove);
                        moveIntent.putExtra("isMoveOperation", true);
                        //moveIntent.putExtra("operationType", "move");
                        moveIntent.putExtra("destinationFolderPath", destinationFolderPath);
                        startActivity(moveIntent);
                        startActivityForResult(moveIntent, REQUEST_CODE_FOLDER_SELECTION);
                        clearSelectionItem();
                    }  else {
                        Toast.makeText(this, "Selected File is null", Toast.LENGTH_SHORT).show();
                    }
                }

                    clearSelectionItem();

                applySorting(lastSortOption);

                break;
            case FOLDER_RENAME:
                //showFolderToast("Rename operation clicked");

                List<FileName> selectedFiless = getSelectedFiles();
                for (FileName fileName : selectedFiless) {
                    File file = fileName.getFile();
                    showRenameDialog(file);
                    clearSelectionItem();
                }

                break;
            default:
                break;
        }
    }

    private void pasteFileOrFolder(File sourceFile, File destFolder) {
        if (sourceFile.isFile()) {
            // It's a file, so copy the file to the destination folder
            File destFile = new File(destFolder, sourceFile.getName());
            int count = 1;
            while (destFile.exists()) {
                // Handle naming conflicts by appending (1), (2), etc. to the file name
                String newName = sourceFile.getName() + " (" + count + ")";
                destFile = new File(destFolder, newName);
                count++;
            }
            try {
                // Perform the actual file copy
                FileInputStream in = new FileInputStream(sourceFile);
                FileOutputStream out = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (sourceFile.isDirectory()) {
            // It's a directory, so create the corresponding directory in the destination folder
            File newDestFolder = new File(destFolder, sourceFile.getName());
            newDestFolder.mkdirs();
            // Recursively copy the contents of the source directory to the new destination directory
            for (File file : sourceFile.listFiles()) {
                pasteFileOrFolder(file, newDestFolder);
            }
        }
    }

    private final SelectedFilesCallback selectedFilesCallback = new SelectedFilesCallback() {
        @Override
        public List<FileName> getSelectedFiles() {
            List<FileName> selectedFiles = new ArrayList<>();
            for (AbstractItem item : fastAdapter.getSelectedItems()) {
                if (item instanceof FileName && item.isSelected()) {
                    selectedFiles.add((FileName) item);
                }
            }
            return selectedFiles;
        }
    };

    private void showRenameDialog(File fileToRename) {
        if (fileToRename == null) {
            Toast.makeText(this, "Empty file to rename", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename File/Folder");

       final TextInputLayout textInputLayout = new TextInputLayout(this);
        int paddingdp = getResources().getDimensionPixelSize(R.dimen.text_input_layout_padding);

        textInputLayout.setPadding(paddingdp, paddingdp, paddingdp, paddingdp);

        // Create an EditText to allow the user to enter the new file/folder name
        final TextInputEditText input = new TextInputEditText(textInputLayout.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(fileToRename.getName()); // Set the current file/folder name as the default text

        textInputLayout.addView(input);
        builder.setView(textInputLayout);



        // Set the positive button for renaming the file/folder
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFileName = input.getText().toString().trim();
                if (!newFileName.isEmpty()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            renameFileOrFolder(fileToRename, newFileName);
                            openDirectory(currentDirectory);
                            fastAdapter.notifyAdapterDataSetChanged();
                            applySorting(lastSortOption);
                        } else {
                           // requiresAllFilesAccessPermission();
                            permissionDialog();
                        }
                    } else {
                        renameFileOrFolder(fileToRename, newFileName);
                        openDirectory(currentDirectory);
                        fastAdapter.notifyAdapterDataSetChanged();
                        applySorting(lastSortOption);
                    }
                }
            }
        });

        // Set the negative button for canceling the rename operation
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        builder.show();
    }

    private void renameFileOrFolder(File folder, String newFolderName) {
        File newFile = new File(folder.getParentFile(), newFolderName);
        if (folder.renameTo(newFile)) {

                    // Folder renamed successfully
                    Toast.makeText(this, "Folder renamed successfully", Toast.LENGTH_SHORT).show();
                    // Update the folder name and refresh the view
                    FileName renamedFileName = new FileName(newFile);
                    int index = fileItems.indexOf(folder);
                    if (index != -1) {
                        fileItems.set(index, renamedFileName);
                        fastAdapter.notifyAdapterDataSetChanged();
                        //openDirectory(currentDirectory);
                        applySorting(lastSortOption);
                    } else {
                        // Failed to rename the folder
                        Toast.makeText(this, "Failed to rename the folder", Toast.LENGTH_SHORT).show();
                    }

        }
    }
    /*private void showFolderToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }*/
    @Override
    public void onBtnDetailsClicked(FileName fileNameItem) {
       // openBottomSheet(fileNameItem);
        if (this != null) {
            openBottomSheet(fileNameItem);
            // Show the Toast with a longer duration to ensure it is visible
            //Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
        } else {
            // Log or display a message if the context is null
            Log.e("MainActivity", "Context is null!");
        }    }

    class ActionBarCallback implements ActionMode.Callback{


        ActionMode currentActionMode;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            currentActionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.action_close){
                clearSelectionItem();
                mode.finish();
            } else if (item.getItemId() == android.R.id.home) {
                clearSelectionItem();
                onBackPressed();
                selectedItemCount = 0;
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                List<FileName> selectedFiles = getSelectedFiles();
                deleteSelectedFiles(selectedFiles);
                mode.finish();
                } else if (item.getItemId() == R.id.action_share) {

                List<FileName> selectedFiles = getSelectedFiles();
                for (FileName fileName : selectedFiles) {
                    File file = fileName.getFile();
                    shareFileOrFolder(file);
                }

                mode.finish();
            } else if (item.getItemId() == R.id.bookmark) {

                List<FileName> selectedFiles = getSelectedFiles();
                for (FileName fileName : selectedFiles) {
                    File file = fileName.getFile();
                    sendSelectedFileFolderBookmark(file);
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelectionItem();
        }
    }

    private void sendSelectedFileFolderBookmark(File file) {
        if (file.isFile()) {
            List<FileName> selectedFiles = getSelectedFiles();

            for (FileName selectedFileName : selectedFiles) {
                if (selectedFileName.getFile().equals(file)) {
                    byte[] thumbnailBytes = getBytesFromDrawable(selectedFileName.getThumbnailImageView());
                    String fileNamee = file.getName();
                    String currentDate = selectedFileName.getModifiedDate();

                    Log.d("DEBUG", "Adding to database: FileName: " + fileNamee + " | Thumbnail: " + thumbnailBytes);

                    mDBHelper.addSelectedItems(fileNamee, thumbnailBytes, currentDate);
                }
            }

            if (!selectedFiles.isEmpty()) {
                Toast.makeText(MainActivity.this, "Selected items bookmarked", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "No valid files selected for bookmarking", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "No valid files selected for bookmarking", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] getBytesFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return getBytesFromBitmap(bitmap);
        }
        return null;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private List<FileName> getSelectedFiles() {
        List<FileName> selectedFiles = new ArrayList<>();

        // Loop through all the items in the adapter to find the selected ones
        for (AbstractItem item : fastAdapter.getSelectedItems()) {
            if (item instanceof FileName && item.isSelected()) {
                FileName fileName = (FileName) item;
                if (fileName.getFile() != null) {
                    selectedFiles.add(fileName);
                } else {
                    Log.d("DEBUG", "Found null FileName object: " + fileName);
                }
            }
        }

        return selectedFiles;
    }

    private void clearSelectionItem() {

            for (AbstractItem item : itemAdapter.getAdapterItems()){

                if (item instanceof FileName) {

                FileName fileName = (FileName) item;
                fileName.setSelected(false);
            }
            fastAdapter.notifyAdapterDataSetChanged();
            selectedItemCount = 0;
        }
    }

    private void updateActionModeTitle(int count){

    if (mActionModeHelper.isActive()){
        if (count == 0){
            mActionModeHelper.getActionMode().finish();
        } else {
            mActionModeHelper.getActionMode().setTitle(getString(R.string.action_mode_title,count));
        }
    }

    }

    @Override
    public void onBackPressed() {
        if (currentDirectory != null && !currentDirectory.equals(Environment.getExternalStorageDirectory())) {
            File parentDirectory = currentDirectory.getParentFile();
            if (parentDirectory != null && parentDirectory.isDirectory()) {
                recyclerState = recView.getLayoutManager().onSaveInstanceState(); // Save the recycler view state
                int lastSelectedSortOption = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(SORT_OPTION_KEY, SORT_BY_NAME_ASCENDING);
                applySorting(lastSelectedSortOption);
                if (recyclerState != null) {
                    recView.getLayoutManager().onRestoreInstanceState(recyclerState); // Restore the recycler view state
                }
                openDirectory(parentDirectory);
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        /*if (recView != null && recView.getLayoutManager() != null){
            int scrollPosition = savedInstanceState.getInt("recycler_view_scroll_position", 0);
            ((LinearLayoutManager) recView.getLayoutManager()).scrollToPositionWithOffset(scrollPosition, 0);
        }*/

        //recyclerViewScrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY, 0);
    }

    private void updateHomeAsUpIndicator() {
        if (currentDirectory != null && !currentDirectory.equals(Environment.getExternalStorageDirectory())) {
            getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_arrow_left).sizeDp(20));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if(isItemSelected) {
            getMenuInflater().inflate(R.menu.menu_items, menu);

            MenuItem layoutItem = menu.findItem(R.id.layoutMode);
            layoutItem.setChecked(isGridView);

           // isGridView = isChecked;
            saveLayoutMode(isGridView);
            if (isGridView) {
                layoutItem.setIcon(R.drawable.baseline_grid_view_24);
                recView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            } else {
                layoutItem.setIcon(R.drawable.baseline_menu_24);
                recView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
            recView.getAdapter().notifyDataSetChanged();

        } else {

            getMenuInflater().inflate(R.menu.menu_items, menu);

            /*MenuItem darkModeMenuItem = menu.findItem(R.id.themeToggle);
            View actionView = darkModeMenuItem.getActionView();
            SwitchCompat toggleSwitch = actionView.findViewById(R.id.switch_toggle_layout);

            isDarkModeEnabled = loadThemeMode();

            // Set the initial state of the toggle switch based on the loaded theme mode
            toggleSwitch.setChecked(isDarkModeEnabled);

            toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int nightMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                    setThemeMode(nightMode);

                    // Save the new theme mode in SharedPreferences
                    saveThemeMode(isChecked);
                    applySorting(lastSortOption);
                }
            });*/


            MenuItem searchdata = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) searchdata.getActionView();



            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mDBHelper.addSearchHistory(query);

                    List<String> updatedSearchHistory = mDBHelper.getAllSearchHistory();

                    searchViewAdapters.clear();

                    for (String searchQuery : updatedSearchHistory) {
                        searchViewAdapters.add(new SearchViewAdapter(searchQuery));
                    }

                    // Notify the adapter about the changes
                    fastItemAdapter.set(searchViewAdapters);

                    fastItemAdapter.withOnClickListener(new OnClickListener<SearchViewAdapter>() {
                        @Override
                        public boolean onClick(View v, IAdapter<SearchViewAdapter> adapter, SearchViewAdapter item, int position) {
                            String clickedQuery = searchViewAdapters.get(position).getSearchQuery();

                            searchView.setQuery(clickedQuery, true);

                            return false;
                        }
                    });

                    fastItemAdapter.notifyDataSetChanged();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                   /* List<AbstractItem> filteredItems = filterItems(fileItems, newText);
                    itemAdapter.set(filteredItems);
                    itemAdapter.filter(newText);*/

                    //fastAdapter.notifyAdapterDataSetChanged();

                    if (newText.isEmpty() && !searchView.isFocused()){
                        mLottieAnimationView.setVisibility(View.GONE);
                        recView.setVisibility(View.GONE);
                        clearRecentSearches.setVisibility(View.VISIBLE);
                        recyclerViewSearch.setVisibility(View.VISIBLE);
                        mDBHelper.getAllSearchHistory();
                        fastItemAdapter.notifyAdapterDataSetChanged();
                    }else {
                        List<AbstractItem> filteredItems = filterItems(fileItems, newText);
                        itemAdapter.set(filteredItems);
                        itemAdapter.filter(newText);
                        fastItemAdapter.notifyAdapterDataSetChanged();

                        if (filteredItems.isEmpty() && !newText.isEmpty()) {
                            mLottieAnimationView.setVisibility(View.VISIBLE);
                        } else {
                            mLottieAnimationView.setVisibility(View.GONE);
                        }

                        clearRecentSearches.setVisibility(View.GONE);
                        recyclerViewSearch.setVisibility(View.GONE);
                        recView.setVisibility(View.VISIBLE);
                    }

                    return true;
                }

            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.d("SearchView", "onClose called");
                    mLottieAnimationView.setVisibility(View.GONE);
                    recView.setVisibility(View.VISIBLE);
                    clearRecentSearches.setVisibility(View.GONE);
                    recyclerViewSearch.setVisibility(View.GONE);
                    return false;
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        mLottieAnimationView.setVisibility(View.GONE);
                        recView.setVisibility(View.GONE);
                        clearRecentSearches.setVisibility(View.VISIBLE);
                        recyclerViewSearch.setVisibility(View.VISIBLE);
                        openDirectory(currentDirectory);
                    }
                }
            });


        }

        return super.onCreateOptionsMenu(menu);
    }

    private List<AbstractItem> filterItems(List<AbstractItem> items, String query) {
        List<AbstractItem> filteredList = new ArrayList<>();
        for (AbstractItem item : items) {
            if (item instanceof FileName) {
                FileName fileNameItem = (FileName) item;
                String fileName = fileNameItem.getFile().getName().toLowerCase();
                if (fileName.contains(query.toLowerCase())) {
                    filteredList.add(fileNameItem);

                }
            }
        }
        return filteredList;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.sort_bottom_sheet) {
            showSortBottomSheet();
            //Toast.makeText(this, "You pressed on Botton sheet", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.add_folder) {
            addNewFolder();
        }/* else if (item.getItemId() == R.id.themeToggle) {
            //switchToogle();
            return true;
        }*/ else if (item.getItemId() == R.id.layoutMode){

            setLayoutMode();

            if (isGridView) {
                item.setIcon(R.drawable.baseline_grid_view_24);
            } else {
                item.setIcon(R.drawable.baseline_menu_24);
            }
            return true;
        } else if (item.getItemId() == R.id.search) {
            //searchStorageData();
        } else if (item.getItemId() == R.id.setting) {
            openSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /*private void saveThemeMode(boolean isDarkModeEnabled) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_THEME_MODE, isDarkModeEnabled);
        editor.apply();
    }

    private boolean loadThemeMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_THEME_MODE, false);
    }*/


    /*private void switchToogle(){

        Log.d("SwitchToggle", "Toggling theme");

        isDarkModeEnabled = !isDarkModeEnabled;

        // Save the theme mode in SharedPreferences
        saveThemeMode(isDarkModeEnabled);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d("SwitchToggle", "Setting dark theme");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d("SwitchToggle", "Setting light theme");
        }

        //setThemeMode(isDarkModeEnabled);

         invalidateOptionsMenu();
//
//        // Recreate the activity to apply the new theme
        recreate();


    }*/

   /* private void switchToogle() {

    }*/

    private void setThemeMode(int nightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            UiModeManager uiModeManager = getSystemService(UiModeManager.class);
            uiModeManager.setApplicationNightMode(nightMode);
            applySorting(lastSortOption);
        } else {
            AppCompatDelegate.setDefaultNightMode(nightMode);
            applySorting(lastSortOption);
        }

        recreate();
    }


    private void setLayoutManager(){
        if (isGridView){

            recView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recView.setLayoutManager(new LinearLayoutManager(this));
        }
    }


    private void saveLayoutMode(boolean isGridView){

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_LAYOUT_MODE, isGridView);
        editor.apply();

    }

    private boolean loadLayoutMode(){

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefes", MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_LAYOUT_MODE, false);

    }

    private void setLayoutMode() {

        isGridView = !isGridView;
        saveLayoutMode(isGridView);
        setLayoutManager();
/*
        invalidateOptionsMenu();
        recreate();*/
    }


    /*    private void addNewFolder() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return;
        }

        // Continue with folder creation
        if (currentDirectory == null) {
            Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newFolderName = "New Folder" + newFolderCounter;
        File newFolder = new File(currentDirectory, newFolderName);

        while (newFolder.exists()) {
            newFolderCounter++;
            newFolderName = "New Folder" + newFolderCounter;
            newFolder = new File(currentDirectory, newFolderName);
        }

      */

    /*  String folderPath = Environment.getExternalStorageDirectory() + "/Android";
        File newFolder = new File(folderPath, newFolderName);

        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean isFolderCreated = folder.mkdirs();
            if (isFolderCreated) {
                Log.d("DEBUG", "Directory created: " + folderPath);
            } else {
                Log.d("DEBUG", "Failed to create directory: " + folderPath);
            }
        }

        while (newFolder.exists()) {
            newFolderCounter++;
            newFolderName = "New Folder" + newFolderCounter;
            newFolder = new File(folderPath, newFolderName);
        }*/

   /* private void addNewFolder() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            // Permission granted, create the folder
            if (currentDirectory == null) {
                openDirectory(Environment.getExternalStorageDirectory());

                Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
            } else {

                createNewFolder();
            }
        }
    }
*/

    private void addNewFolder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above, check for MANAGE_EXTERNAL_STORAGE permission
            if (Environment.isExternalStorageManager()) {
                // Permission granted, create the folder
                if (currentDirectory == null) {
                    openDirectory(Environment.getExternalStorageDirectory());
                    Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
                } else {
                    createNewFolder();
                }
            } else {
                // Permission not granted, request it from the user
               /* Intent manageStorageIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                manageStorageIntent.setData(uri);
                startActivity(manageStorageIntent);
                Toast.makeText(this, "Please grant manage storage permission to create folders.", Toast.LENGTH_LONG).show();
*/
            permissionDialog();
            }
        } else {
            // For Android versions prior to 11, check WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                // Permission granted, create the folder
                if (currentDirectory == null) {
                    openDirectory(Environment.getExternalStorageDirectory());
                    Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
                } else {
                    createNewFolder();
                }
            }
        }
    }



    /*

        boolean isFolderCreated = newFolder.mkdir();
        if (isFolderCreated) {
            EventBus.getDefault().post(new FolderCreatedEvent(newFolderName));
            newFolderCounter++;
            Toast.makeText(this, "Folder created: " + newFolderName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to create folder", Toast.LENGTH_SHORT).show();
        }
    }*/

    // Example method to create a new folder
    private boolean shouldOpenInMainDirectory() {
        // Define your custom condition here
        // For example, let's say you want to create the folder in the main directory
        // when the current directory is the internal storage root ("/storage/emulated/0")
        return currentDirectory.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath());
    }


    private void createNewFolder() {

        if (isExternalStorageWritable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Create New Folder");

            // Use TextInputLayout to wrap the TextInputEditText
            final TextInputLayout inputLayout = new TextInputLayout(this);

            int paddingDp = getResources().getDimensionPixelOffset(R.dimen.text_input_layout_padding);
            inputLayout.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

            inputLayout.setHint("Folder Name");

            final TextInputEditText input = new TextInputEditText(inputLayout.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            inputLayout.addView(input);

            builder.setView(inputLayout);

                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String folderName = input.getText().toString().trim();
                                if (!folderName.isEmpty()) {
                                    File newFolder;

                                    // Check if the currentDirectory is null or not
                                    if (currentDirectory != null) {
                                        // Create a folder in the currentDirectory
                                        newFolder = new File(currentDirectory, folderName);
                                    } else {
                                        // If currentDirectory is null, create a folder in the app's internal storage
                                        newFolder = new File(getFilesDir(), folderName);
                                    }

                                    // Check if the app has the MANAGE_EXTERNAL_STORAGE permission
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        if (Environment.isExternalStorageManager()) {
                                            // App has permission, proceed with folder creation
                                            if (!newFolder.exists()) {
                                                if (newFolder.mkdirs()) {
                                                    // Folder created successfully
                                                    Toast.makeText(MainActivity.this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                                                    if (currentDirectory != null) {
                                                        itemAdapter.setNewList(fileItems);
                                                        fastAdapter.notifyAdapterDataSetChanged();
                                                        openDirectory(currentDirectory.getAbsoluteFile());
                                                        //applySorting(lastSortOption);
                                                    } else {
                                                        openDirectory(newFolder);
                                                        //applySorting(lastSortOption);
                                                    }
                                                } else {
                                                    Log.d("DEBUG", "Current Directory: " + currentDirectory.getAbsolutePath());
                                                    Log.e("DEBUG", "Failed to create folder: " + newFolder.getAbsolutePath());
                                                    Toast.makeText(MainActivity.this, "Failed to create folder", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(MainActivity.this, "Folder already exists", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // App does not have the permission, request it from the user
                                            /*Intent manageStorageIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            manageStorageIntent.setData(uri);
                                            startActivity(manageStorageIntent);
                                            Toast.makeText(MainActivity.this, "Please grant manage storage permission to create folders.", Toast.LENGTH_LONG).show();
*/
                                        permissionDialog();
                                        }
                                    }  else {
                                        // For Android versions prior to 11, the permission is not required
                                        if (!newFolder.exists()) {
                                            if (newFolder.mkdirs()) {
                                                // Folder created successfully
                                                Toast.makeText(MainActivity.this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                                                if (currentDirectory != null) {
                                                    openDirectory(currentDirectory.getAbsoluteFile());
                                                    applySorting(lastSortOption);
                                                } else {
                                                    openDirectory(newFolder);
                                                    applySorting(lastSortOption);
                                                }
                                            } else {
                                                Log.d("DEBUG", "Current Directory: " + currentDirectory.getAbsolutePath());
                                                Log.e("DEBUG", "Failed to create folder: " + newFolder.getAbsolutePath());
                                                Toast.makeText(MainActivity.this, "Failed to create folder", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Folder already exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });



                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            Log.e("DEBUG", "External storage not writable.");
            Toast.makeText(this, "Not Writable", Toast.LENGTH_SHORT).show();
        }
    }

    private void permissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("This permission is required for create, delete, rename, copy, move, share for folder/file. \n\n" +
                "So Please grant this permission for the better UI experience and also it's features");
        builder.setCancelable(false);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent manageStorageIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                manageStorageIntent.setData(uri);
                startActivity(manageStorageIntent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

   /* private void createNewFolder() {
        String newFolderName = "New Folder" + newFolderCounter;
        File newFolder = new File(currentDirectory, newFolderName);

        while (newFolder.exists()) {
            newFolderCounter++;
            newFolderName = "New Folder" + newFolderCounter;
            newFolder = new File(currentDirectory, newFolderName);
        }

        boolean isFolderCreated = newFolder.mkdir();
        if (isFolderCreated) {
            EventBus.getDefault().post(new FolderCreatedEvent(newFolderName));
            newFolderCounter++;

            Toast.makeText(this, "Folder created: " + newFolderName, Toast.LENGTH_SHORT).show();
        } else {
            if (currentDirectory == null) {
                Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Failed to create folder", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Subscribe
    public void onFolderCreated(FolderCreatedEvent event) {
        String folderName = event.getFolderName();
        File newFolder = new File(currentDirectory, folderName);

        if (newFolder.exists() && newFolder.isDirectory()) {
            fileItems.add(new FileName(newFolder));
            itemAdapter.setNewList(fileItems);
            fastAdapter.notifyAdapterDataSetChanged();
            loadThumbnails(fileItems);
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void showSortBottomSheet() {
        SortBottomSheetFragment sortBottomSheetFragment = SortBottomSheetFragment.newInstance();
        sortBottomSheetFragment.setSortOptionSelectedListener(this);
        sortBottomSheetFragment.show(getSupportFragmentManager(), SortBottomSheetFragment.TAG);
    }

   /* private String getCurrentFolderName(String path){
        for (Map.Entry<String, String> entry : folderPaths.entrySet()){
            if (path.startsWith(entry.getValue())){
                return entry.getKey();
            }
        }
        return "Unknown";
    }*/

    private ArrayList<AbstractItem> navigationPath = new ArrayList<>();
    private String internalStoragePath = "/Internal Storage";
    private String currentFolderPath = "";

    /*private void toogleShimmerAndRecyclerView(boolean showShimmer){
        if (showShimmer){
            mShimmerFrameLayout.startShimmer();
            mShimmerFrameLayout.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
        } else {
            mShimmerFrameLayout.stopShimmer();
            mShimmerFrameLayout.setVisibility(View.INVISIBLE);
            recView.setVisibility(View.VISIBLE);
        }
    }*/

    public void openDirectory(File directory) {

        clearRecentSearches.setVisibility(View.GONE);
        recyclerViewSearch.setVisibility(View.GONE);
        // Call the helper method without showing hidden files by default
        openDirectory(directory, true);
    }

    public void openDirectory(File directory, boolean showHiddenFiles) {


        updateHeaderView(directory.getAbsolutePath());

        updateItemOrderInSharedPreferences();

        restoreItemOrderFromSharedPreferences();

        currentDirectory = directory;


//        Log.d("DEBUG", "Opening directory: " + directory.getAbsolutePath());
//
//        Log.d("DEBUG", "Data loaded successfully from directory: " + directory.getAbsolutePath());


        if (currentDirectory == null || !currentDirectory.exists() || !currentDirectory.isDirectory()) {
            Log.e("DEBUG", "Invalid current directory: " + currentDirectory);
            return;
        }

        //toogleShimmerAndRecyclerView(true);
        File[] filesAndFolders = directory.listFiles();

        if (filesAndFolders == null || filesAndFolders.length == 0) {
            updateHomeAsUpIndicator();
//            notxtView.setVisibility(View.VISIBLE);
//            noFileImage.setVisibility(View.VISIBLE);
            mLottieAnimationView.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            itemAdapter.clear();

            //toogleShimmerAndRecyclerView(false);
            return;
        }

//        notxtView.setVisibility(View.GONE);
//        noFileImage.setVisibility(View.GONE);
        mLottieAnimationView.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);

        updateHomeAsUpIndicator();

        String folderPath = currentDirectory.getAbsolutePath();


        //If user clicks on directory it clears the list n jumped into the direectory
        int clickedPosition = -1;
        String clickedFolderPath = currentDirectory.getAbsolutePath();
        for (int i = 0; i < headerItems.size(); i++) {
            AbstractItem item = headerItems.get(i);
            if (item instanceof NavigationAdapter) {
                NavigationAdapter navigationAdapter = (NavigationAdapter) item;
                folderPath = navigationAdapter.getFolderPath();
                if (folderPath.equals(clickedFolderPath)) {
                    clickedPosition = i;
                    break;
                }
            }
        }
//        Log.d("DEBUG", "Clicked FolderPath: " + clickedFolderPath);
//        Log.d("DEBUG", "Clicked Position: " + clickedPosition);

        //this code is for when user clicks on any of the position then It wil jump there
        //like that > storage> emulated > 0> Android > media> Download
        //if uer clicks on Android then it close media and download and jumped into Android
        if (clickedPosition != -1) {
            int itemToRemove = headerItems.size() - 1 - clickedPosition;
            for (int i = 0; i < itemToRemove; i++) {
                headerItems.remove(headerItems.size() - 1);
            }
        }

        String[] pathComponents = folderPath.split(File.separator);
        StringBuilder accumulatedPath = new StringBuilder();
        for (String pathComponent : pathComponents) {
            if (!pathComponent.isEmpty()){
                accumulatedPath.append(File.separator).append(pathComponent);
                String componentPath = accumulatedPath.toString();
                String folderName = new File(componentPath).getName();
                if (!headerItems.contains(new NavigationAdapter(folderName, componentPath))) {
                    headerItems.add(new NavigationAdapter(folderName, componentPath));
                }
            }
        }
        updateHeaderView(folderPath);

        pathAdapter.set(headerItems);
        pathFastAdapter.notifyAdapterDataSetChanged();

        selectedFolderPath = directory.getAbsolutePath();

        //List<AbstractItem> fileItems = new ArrayList<>();
        fileItems.clear();

        for (File file : filesAndFolders) {
            if (file.isDirectory() || isImageFile(file)) {
                FileName fileName = new FileName(file);

                if (!showHiddenFiles && file.isHidden()){
                    //fileName.setIconColor(com.mikepenz.materialize.R.color.md_grey_300);
                    fileName.setIconicsDrawable(new IconicsDrawable(this,CommunityMaterial.Icon.cmd_folder).colorRes(com.mikepenz.materialize.R.color.md_grey_400));
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

                fileName.setOnBtnDetailsClickListener(MainActivity.this);

                // Set the last modified date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fileName.setModifiedDate(dateFormat.format(new Date(file.lastModified())));
                fileItems.add(fileName);

            }
        }

        itemAdapter.setNewList(fileItems);

        fastAdapter.notifyAdapterDataSetChanged();
        //pathFastAdapter.notifyAdapterDataSetChanged();

        loadThumbnails(fileItems);

        if (recyclerState != null) {
            recView.getLayoutManager().onRestoreInstanceState(recyclerState);
        }

        applySorting(lastSortOption);
        updateHomeAsUpIndicator();

        //updateFolderPathRecView();
        //showToast(getFolderPath());

        //progressDialog.dismiss();

    }

    /*private String getFolderPath() {
        if (currentDirectory != null) {
            String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String currentPath = currentDirectory.getAbsolutePath();

            // Check if the current directory is the internal storage root
            if (currentPath.equals(internalStoragePath)) {
                return "Internal Storage";
            }

            // Remove the internal storage root path from the current path to get the relative path
            String relativePath = currentPath.substring(internalStoragePath.length());

            // Remove any leading File.separator if present
            if (relativePath.startsWith(File.separator)) {
                relativePath = relativePath.substring(1);
            }

            // Replace File.separator with ' > ' to represent the path hierarchy
            return "Internal Storage > " + relativePath;
        }

        return "";
    }*/


    private void showToast(String folderPath) {
        Toast.makeText(this, folderPath, Toast.LENGTH_SHORT).show();
    }

    private void loadThumbnails(List<AbstractItem> fileNames) {
        for (AbstractItem items : fileNames)
            if (items instanceof FileName) {
                FileName fileName = (FileName) items;
                File file = fileName.getFile();
                if (isImageFile(file)) {
                    Glide.with(this)
                            .load(file)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(1000, 1000)
                            .thumbnail(0.1f)
                            .centerCrop()
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                    fileName.setThumbnailImageView(resource);
                                    fastAdapter.notifyAdapterDataSetChanged();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    fileName.setThumbnailImageView(null);
                                    fastAdapter.notifyAdapterDataSetChanged();
                                }
                            });
                } else {
                    // Clear thumbnail image if not an image file
                    fileName.setThumbnailImageView(new IconicsDrawable(this)
                            .icon(CommunityMaterial.Icon.cmd_file_image)
                            .colorRes(com.mikepenz.materialize.R.color.md_blue_400)
                            .sizeDp(24));

                }
            }
        fastAdapter.notifyAdapterDataSetChanged();
    }

    static boolean isImageFile(File file) {
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


    private void openFile(File file) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            String type = "image/*"; // Set the appropriate MIME type for your image file
            intent.setDataAndType(fileUri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Connot open file" + e, Toast.LENGTH_SHORT).show();
        }


    }


    /*private void requestPermission()

        {

        String[] requiredPermissions = new String[]{
//                Manifest.permission.READ_MEDIA_IMAGES,
//                Manifest.permission.READ_MEDIA_VIDEO,
//                Manifest.permission.READ_MEDIA_AUDIO
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Requesting READ_EXTERNAL_STORAGE permission for Android 11 and newer
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // Permission is already granted, open the initial directory
                currentDirectory = Environment.getExternalStorageDirectory();
                openDirectory(currentDirectory);
            } else {
                // Check if the user previously denied the permissions
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (showRationale) {
                    // Show a custom explanation dialog before requesting the permissions again
                    showPermissionReqquireDialog();
                } else {
                    // Request the permission using the ActivityResultLauncher
                    requestPermissionLauncher.launch(requiredPermissions);
                }

                //requestPermissionLauncher.launch(requiredPermissions);

            }
        } else {
            // Request WRITE_EXTERNAL_STORAGE permission for Android versions prior to 11
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSION_CODE);
        }
    }*/



    private void requestPermission() {
        String[] requiredPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Requesting READ_EXTERNAL_STORAGE permission for Android 11 and newer
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, open the initial directory
                currentDirectory = Environment.getExternalStorageDirectory();
                openDirectory(currentDirectory);
            } else {
                // Request the permission using the ActivityResultLauncher
                requestPermissionLauncher.launch(requiredPermissions);
            }
        } else {
            // Request WRITE_EXTERNAL_STORAGE permission for Android versions prior to 11
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSION_CODE);
        }
    }

    private boolean checkPermission() {
        return
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


  /*  @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //                ||
//                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
//                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED  );

            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }*/


    /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            boolean r = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            return r;
        } else {
            int readExtStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && readExtStorage == PackageManager.PERMISSION_GRANTED;

        }*//*
        return true;
    }*/

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {


                boolean allPermissionsGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }



                if (allPermissionsGranted) {
                    openDirectory(currentDirectory != null ? currentDirectory : Environment.getExternalStorageDirectory());
                    if (currentDirectory == null) {
                        Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    // Check if the permissions were actually requested by the user
                    boolean permissionsRequested = false;
                    for (String permission : permissions.keySet()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            permissionsRequested = true;
                            //Toast.makeText(this, "Directory open", Toast.LENGTH_SHORT).show();
                            break;

                        }
                    }

                    if (permissionsRequested) {
                        // Permissions not granted, show the permission dialog again
                        showPermissionReqquireDialog();
                    }
                }
            });



    /*private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(this, "Storage Permission requires Please allow from the settings", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }*/


   /* if (!allPermissionResultCheck()) {
            requestPermissionLauncherStorage.launch(requiredPermissions);
        } else {
            Toast.makeText(this, "All Storage Permissions Granted.", Toast.LENGTH_SHORT).show();
            if (currentDirectory == null) {
                currentDirectory = Environment.getExternalStorageDirectory();
                openDirectory(currentDirectory);
            }
        }


    }*/

    /*    private boolean allPermissionResultCheck() {
        return isStorageImagePermitted && isStorageVideoPermitted && isStorageAudioPermitted;
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncherStorage =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        for (String permission : requiredPermissions) {
                            if (permissions.get(permission) != null && permissions.get(permission)) {
                                // Permission granted
                                handlePermissionGranted(permission);
                            } else {
                                // Permission denied
                                handlePermissionDenied(permission);
                            }
                        }
                    });

    private void handlePermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.READ_MEDIA_IMAGES:
                isStorageImagePermitted = true;
                break;
            case Manifest.permission.READ_MEDIA_VIDEO:
                isStorageVideoPermitted = true;
                break;
            case Manifest.permission.READ_MEDIA_AUDIO:
                isStorageAudioPermitted = true;
                break;
        }
    }

    private void handlePermissionDenied(String permission) {
        switch (permission) {
            case Manifest.permission.READ_MEDIA_IMAGES:
                isStorageImagePermitted = false;
                break;
            case Manifest.permission.READ_MEDIA_VIDEO:
                isStorageVideoPermitted = false;
                break;
            case Manifest.permission.READ_MEDIA_AUDIO:
                isStorageAudioPermitted = false;
                break;
        }
        showPermissionReqquireDialog();
    }*/

    /*private boolean checkPermission() {

//        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//
//            return false;
//        }

        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionsGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    // Permissions granted, open the initial directory
                    currentDirectory = Environment.getExternalStorageDirectory();
                    openDirectory(currentDirectory);
                }

                else {
                    // Permissions not granted, show the permission dialog again
                    showPermissionReqquireDialog();
                }
            });*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHILD_DIRECTORY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri childUri = data.getData();
                if (childUri != null) {
                    String childPath = childUri.getLastPathSegment();
                    File childDirectory = new File(currentDirectory, childPath);
                    if (childDirectory.exists() && childDirectory.isDirectory() && Objects.equals(childDirectory.getParentFile(), currentDirectory)) {
                        // The selected child directory is a subdirectory of the currentDirectory
                        currentDirectory = childDirectory;
                        //fetchingAllFiles();
                        openDirectory(currentDirectory);
                    } else {
                        // The selected child directory is not a valid subdirectory of the currentDirectory
                        // Handle this situation accordingly (e.g., show an error message)
                    }
                }
            }
        }
    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {

            //openDirectory(currentDirectory != null ? currentDirectory : Environment.getExternalStorageDirectory());

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                //openFilePicker();
                //openDirectoryPicker();
                openDirectory(currentDirectory != null ? currentDirectory : Environment.getExternalStorageDirectory());

                if (currentDirectory == null) {
                    Toast.makeText(this, "Current directory is null.", Toast.LENGTH_SHORT).show();
                    return;
                }
               // openDirectory(currentDirectory);
                //fetchingAllFiles();
                //createNewFolder();
            }

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionReqquireDialog();
                } else {
                    Toast.makeText(this, "Storage Permission requires Please allow from the settingssss", Toast.LENGTH_SHORT).show();
                }


        }


    }*/

 /*  @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       if (requestCode == REQUEST_PERMISSION_CODE) {
           if (grantResults.length > 0) {
               boolean allPermissionsGranted = true;

               for (int result : grantResults) {
                   if (result != PackageManager.PERMISSION_GRANTED) {
                       allPermissionsGranted = false;
                       break;
                   }
               }

               if (allPermissionsGranted) {
                   // Permissions granted, open the initial directory
                   currentDirectory = Environment.getExternalStorageDirectory();
                   openDirectory(currentDirectory);
               } else {
                   // Permissions not granted, show the permission dialog again
                   showPermissionReqquireDialog();
               }
           } else {
               // grantResults array is empty, something went wrong, show the permission dialog again
               showPermissionReqquireDialog();
           }
       }
   }*/


    private void showPermissionReqquireDialog() {
        Log.d("PermissionDialog", "Showing permission dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("This app requires the permission to access your storage for the better experience. Please allow the required permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (permissionRequestCount < 2) {
                            permissionRequestCount++;
                            Log.d("PermissionDialog", "Requesting permission");
                            requestPermission();
                        } else {

                            Log.d("PermissionDialog", "Permission limit reached");
                            Toast.makeText(MainActivity.this, "Storage Permission requires Please allow from the settings", Toast.LENGTH_SHORT).show();
                        }
                        //requestPermission();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("PermissionDialog", "Permission denied");
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /*private void fetchingAllFiles() {
        File root = Environment.getExternalStorageDirectory();
        File[] filesAndFolder = root.listFiles();

        if (filesAndFolder == null || filesAndFolder.length == 0) {
            notxtView.setVisibility(View.VISIBLE);
            noFileImage.setVisibility(View.VISIBLE);
            itemAdapter.clear();
            currentDirectory = null;
            return;
        }

        notxtView.setVisibility(View.GONE);
        noFileImage.setVisibility(View.GONE);

        if (selectedFolderPath != null && currentDirectory != null) {
            // Check if the selectedFolderPath is the parent of the currentDirectory
            if (currentDirectory.getAbsolutePath().startsWith(selectedFolderPath)) {
                String[] pathComponents = selectedFolderPath.split(File.separator);
                String folderName = new File(pathComponents[pathComponents.length - 1]).getName();

                // Remove items from the headerItems list until the parent folder
                for (int i = headerItems.size() - 1; i >= 0; i--) {
                    if (headerItems.get(i) instanceof NavigationAdapter) {
                        NavigationAdapter headerPathItem = (NavigationAdapter) headerItems.get(i);
                        if (headerPathItem.getFolderPath().equals(folderName)) {
                            break;
                        }
                    }
                    headerItems.remove(i);
                }
            } else {
                // If the selectedFolderPath is not the parent, set the selectedFolderPath as the new currentDirectory
                currentDirectory = new File(selectedFolderPath);
                updateFolderPathRecView();
            }
        }

        selectedFolderPath = currentDirectory.getAbsolutePath();
        // List<AbstractItem> fileItems = new ArrayList<>();
        fileItems.clear();
        for (File file : filesAndFolder) {

            FileName fileName = new FileName(file);
            Log.d("FileNameDebug", "File Name: " + fileName.getFile().getName());
            Log.d("FileNameDebug", "Last Modified Date: " + fileName.getModifiedDate());
            Log.d("FileNameDebug", "Item Count: " + fileName.getItemCount());
            if (file.isDirectory()){
                 File[] filesInDir = file.listFiles();
                 if (filesInDir != null){
                     fileName.setItemCount(filesInDir.length);
                 } else {
                     fileName.setItemCount(0);
                 }
            } else {
                fileName.setItemCount(1);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            fileName.setModifiedDate(dateFormat.format(new Date(file.lastModified())));

            fileItems.add(fileName);
        }

        // Update the selectedFolderPath to the currentDirectory
        selectedFolderPath = currentDirectory.getAbsolutePath();

        updateFolderPathRecView();
        loadThumbnails(fileItems);

        itemAdapter.set(fileItems); // Update the itemAdapter with the fileItems
        fastAdapter.notifyAdapterDataSetChanged();
        pathFastAdapter.notifyAdapterDataSetChanged();
    }*/


  /*  private void onFolderClicked(File clickedFolder) {
   //   fetchingAllFiles();
    }*/


   /* private void fetchingAllFiles(File directory) {
        File[] filesAndFolder = directory.listFiles();

        if (filesAndFolder == null || filesAndFolder.length == 0) {
            notxtView.setVisibility(View.VISIBLE);
            itemAdapter.clear();
            currentDirectory = null;
            return;
        }

        notxtView.setVisibility(View.GONE);

        if (directory.equals(Environment.getExternalStorageDirectory())) {
            // When in Internal Storage, show only "Internal Storage" in the header
            updateHeaderPathView(Environment.getExternalStorageDirectory());
        } else {
            // For other folders, update the header path accordingly
            updateHeaderPathView(directory);
        }

        currentDirectory = directory;
        pathAdapter.set(headerItems);

        fileItems.clear();
        for (File file : filesAndFolder) {
            fileItems.add(new FileName(file));
        }

        loadThumbnails(fileItems);
        setupRecyclerView();

        itemAdapter.clear();
        itemAdapter.add(fileItems);
        fastAdapter.notifyAdapterDataSetChanged();
    }


    private void setupRecyclerView() {
        recView = findViewById(R.id.filesRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(fastAdapter);


    }*/

    @Override
    public void onSortOptionSelected(int sortOption) {

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(SORT_OPTION_KEY, sortOption);
        editor.apply();

        applySorting(sortOption);

    }

    private void applySorting(int sortOption) {
        switch (sortOption) {
            case SORT_BY_NAME_ASCENDING:
                sortByNameAscending();
                break;
            case SORT_BY_NAME_DESCENDING:
                sortByNameDescending();
                break;
            case SORT_BY_DATE_ASCENDING:
                sortByDateAscending();
                break;
            case SORT_BY_DATE_DESCENDING:
                sortByDateDescending();
                break;
            default:
                break;
        }

        lastSortOption = sortOption;

    }

    private void sortByNameAscending() {
        //Toast.makeText(this, "List sorted in Ascending with Name", Toast.LENGTH_SHORT).show();
        Collections.sort(fileItems, new Comparator<AbstractItem>() {
            @Override
            public int compare(AbstractItem o1, AbstractItem o2) {
                if (o1 instanceof FileName && o2 instanceof FileName) {
                    String name1 = ((FileName) o1).getFile().getName();
                    String name2 = ((FileName) o2).getFile().getName();
                    return name1.compareTo(name2);
                }
                return 0;
            }
        });

        itemAdapter.setNewList(fileItems);
        fastAdapter.notifyAdapterDataSetChanged();

        loadThumbnails(fileItems);
    }

    private void sortByNameDescending() {
        //Toast.makeText(this, "List sorted in Descending with Name", Toast.LENGTH_SHORT).show();
        Collections.sort(fileItems, new Comparator<AbstractItem>() {
            @Override
            public int compare(AbstractItem o1, AbstractItem o2) {
                if (o1 instanceof FileName && o2 instanceof FileName) {
                    String name1 = ((FileName) o1).getFile().getName();
                    String name2 = ((FileName) o2).getFile().getName();
                    return name2.compareTo(name1);
                }
                return 0;
            }
        });

        itemAdapter.setNewList(fileItems);
        fastAdapter.notifyAdapterDataSetChanged();

        loadThumbnails(fileItems);
    }

    private void sortByDateAscending() {
        //Toast.makeText(this, "List sorted in Ascending with Date", Toast.LENGTH_SHORT).show();
        Collections.sort(fileItems, new Comparator<AbstractItem>() {
            @Override
            public int compare(AbstractItem o1, AbstractItem o2) {
                if (o1 instanceof FileName && o2 instanceof FileName) {
                    File file1 = ((FileName) o1).getFile();
                    File file2 = ((FileName) o2).getFile();
                    long time1 = file1.lastModified();
                    long time2 = file2.lastModified();
                    return Long.compare(time1, time2);
                }
                return 0;
            }
        });
        itemAdapter.setNewList(fileItems);
        fastAdapter.notifyAdapterDataSetChanged();

        loadThumbnails(fileItems);
    }

    private void sortByDateDescending() {
        //Toast.makeText(this, "List sorted in Descending with Date", Toast.LENGTH_SHORT).show();
        Collections.sort(fileItems, new Comparator<AbstractItem>() {
            @Override
            public int compare(AbstractItem o1, AbstractItem o2) {
                if (o1 instanceof FileName && o2 instanceof FileName) {
                    File file1 = ((FileName) o1).getFile();
                    File file2 = ((FileName) o2).getFile();
                    long time1 = file1.lastModified();
                    long time2 = file2.lastModified();
                    return Long.compare(time2, time1);
                }
                return 0;
            }
        });
        itemAdapter.setNewList(fileItems);
        fastAdapter.notifyAdapterDataSetChanged();

        loadThumbnails(fileItems);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This is for setting the layouwhich last set
        setLayoutManager();

        //applySorting(lastSortOption);
        onSortOptionSelected(lastSortOption);

//        saveThemeMode(isDarkModeEnabled);
//        loadThemeMode();
//        setThemeMode(isDarkModeEnabled);



        /*SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!sharedPreferences.contains(SORT_OPTION_KEY)) {
            // If the key is not present (first launch), set a default sorting option
            int defaultSortOption = SORT_BY_NAME_ASCENDING;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SORT_OPTION_KEY, defaultSortOption);
            editor.apply();
        }

        // Retrieve the last selected sorting option from SharedPreferences
        int currentSortOption = sharedPreferences.getInt(SORT_OPTION_KEY, SORT_BY_NAME_ASCENDING);

        // Check if the sorting option has changed since the last time the app was resumed
        if (lastSortOption != -1 && lastSortOption != currentSortOption) {
            // Apply the last selected sorting option to your data list
            applySorting(currentSortOption);



            // Display the Toast message for the new sorting option
            switch (currentSortOption) {
                case SORT_BY_NAME_ASCENDING:
                   // Toast.makeText(this, "List sorted in Ascending with Name", Toast.LENGTH_SHORT).show();
                    break;
                case SORT_BY_NAME_DESCENDING:
                    //Toast.makeText(this, "List sorted in Descending with Name", Toast.LENGTH_SHORT).show();
                    break;
                case SORT_BY_DATE_ASCENDING:
                    //Toast.makeText(this, "List sorted in Ascending with Date", Toast.LENGTH_SHORT).show();
                    break;
                case SORT_BY_DATE_DESCENDING:
                    //Toast.makeText(this, "List sorted in Descending with Date", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }*/

            /*if (checkPermission()) {

                if (currentDirectory == null) {
                    currentDirectory = Environment.getExternalStorageDirectory();
                    openDirectory(currentDirectory);
                    //toogleShimmerAndRecyclerView(false);
                    //fetchingAllFiles();
                }
            } else {

                requestPermission();

            }*/

        }

        // Update the lastSortOption to the currentSortOption
        //lastSortOption = currentSortOption;


    private void shareFileOrFolder(File file) {
        if (file.isFile()) {
            // If it's a single file and sharable, share it
            if (isFileSharable(file)) {
                shareSingleFile(file);
            } else {
                // File is not sharable, show a message
                Toast.makeText(this, "File is not sharable.", Toast.LENGTH_SHORT).show();
            }
        } else if (file.isDirectory()) {

            Toast.makeText(this, "Folder is not Sharable", Toast.LENGTH_SHORT).show();
            /*// If it's a folder, get all files inside the folder and share them together if the folder is sharable
            List<FileName> filesInFolder = getAllFilesInFolder(file);
            if (!filesInFolder.isEmpty() && isFolderSharable(filesInFolder)) {
                shareMultipleFiles(filesInFolder);
            }
            else {
                // Folder is not sharable or empty, show a message
                Toast.makeText(this, "Folder is not sharable or empty.", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private void shareSingleFile(File file) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(getFileMimeType(file));
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sendIntent, "Share File via..."));
    }

    private void shareMultipleFiles(List<FileName> files) {
        ArrayList<Uri> fileUris = new ArrayList<>();
        for (FileName fileName : files) {
            File file = fileName.getFile();
            if (file != null) {
                if (isFileSharable(file)) {
                    fileUris.add(FileProvider.getUriForFile(this, getPackageName() + ".provider", file));
                }
            }
        }

        if (!fileUris.isEmpty()) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.setType("*/*");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sendIntent, "Share Files via..."));
        } else {
            // No sharable files found in the folder, show a message
            Toast.makeText(this, "No sharable files found in the folder.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFileSharable(File file) {
        if (file.isFile()) {
            String extension = getFileExtension(file);
            return extension.equals("jpg") || extension.equals("png") || extension.equals("mp4") || extension.equals("avi");
        }
        return false;
    }

    private String getFileMimeType(File file) {
        String extension = getFileExtension(file);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return (mimeType != null) ? mimeType : "*/*";
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexDot = name.lastIndexOf(".");
        return (lastIndexDot != -1) ? name.substring(lastIndexDot + 1).toLowerCase() : "";
    }

    private void deleteSelectedFiles(List<FileName> selectedFiles) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                showDeleteConfirmationDialog(selectedFiles);
            } else {
                //requestAllFilesAccessPermissionForDeletion();
                permissionDialog();
            }
        } else {
            showDeleteConfirmationDialog(selectedFiles);
        }
    }

    private void showDeleteConfirmationDialog(List<FileName> selectedFiles) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the selected file(s)?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performDeletion(selectedFiles);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        Set<AbstractItem> dataSource = fastAdapter.getSelectedItems();
        dataSource.removeAll(selectedFiles);

        openDirectory(currentDirectory);
        applySorting(lastSortOption);
        fastAdapter.notifyAdapterDataSetChanged();
    }

    private void performDeletion(List<FileName> selectedFiles) {
        for (FileName fileToDelete : selectedFiles) {
            if (fileToDelete != null) {
                File file = fileToDelete.getFile();
                if (file != null) {
                    if (isFileDeletable(file)) {
                        // Check if it's the main directory (selected directory)
                        if (file.getAbsolutePath().equals(selectedFolderPath)) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirm Delete")
                                    .setMessage("Are you sure you want to delete the selected folder?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteFileOrFolder(file);
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();

                        } else {
                            // For other files or directories, directly proceed with the deletion
                            deleteFileOrFolder(file);
                        }
                    } else {
                        if (isFileSharable(file)) {
                            shareFileOrFolder(file);
                        } else {
                            // File is neither deletable nor sharable, show a message or handle accordingly
                            Toast.makeText(this, "File/Folder is neither deletable nor sharable.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

        // Clear the selection in the adapter
        for (FileName fileToDelete : selectedFiles) {
            fileToDelete.setSelected(false);
        }


        clearSelectionItem();
        openDirectory(currentDirectory);
        applySorting(lastSortOption);
        fastAdapter.notifyAdapterDataSetChanged();
    }


    private boolean isFileDeletable(File file) {

            return file.canWrite();
        }

        private void deleteFileOrFolder(File file) {
            if (file.isFile()) {
                // It's a file, so simply delete it
                if (file.delete()) {
                    // File deleted successfully
                    Toast.makeText(this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to delete the file
                    Toast.makeText(this, "Failed to delete the file", Toast.LENGTH_SHORT).show();
                }
            } else if (file.isDirectory()) {
                // It's a directory, so recursively delete all files and sub-folders inside it
                boolean deleteSuccess = deleteDirectory(file);
                if (deleteSuccess) {
                    // Directory deleted successfully
                    Toast.makeText(this, "Folder deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to delete the folder
                    Toast.makeText(this, "Failed to delete the folder", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private boolean deleteDirectory(File directory) {
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            // Recursively delete sub-folders and their contents
                            deleteDirectory(file);
                        } else {
                            // Delete individual files
                            if (!file.delete()) {
                                // Failed to delete a file
                                return false;
                            }
                        }
                    }
                }
                // Delete the empty folder once all contents are deleted
                return directory.delete();
            }
            return false;
        }

    }



