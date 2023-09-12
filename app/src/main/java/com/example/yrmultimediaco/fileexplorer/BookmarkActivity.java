package com.example.yrmultimediaco.fileexplorer;

import static com.example.yrmultimediaco.fileexplorer.MainActivity.isImageFile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private FastAdapter<BookmarkAdapter> fastAdapter;
    private ItemAdapter<BookmarkAdapter> itemAdapter;
    RecyclerView bookmarkRecView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        bookmarkRecView = findViewById(R.id.bookmarkRecView);
        bookmarkRecView.setLayoutManager(new LinearLayoutManager(this));
        bookmarkRecView.setAdapter(fastAdapter);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bookmarks");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<BookmarkAdapter> bookmarkItems = getBookmarkItemsFromDatabase();

        for (BookmarkAdapter item : bookmarkItems) {
            byte[] thumbnail = item.getThumbnail();
            itemAdapter.add(new BookmarkAdapter(item.getFileName(), thumbnail, item.getDateTime()));

        }


        }

    private List<BookmarkAdapter> getBookmarkItemsFromDatabase() {
        DBHelper dbHelper = new DBHelper(this);
        List<BookmarkAdapter> bookmarkItems = dbHelper.getAllBookmarkItems();
        dbHelper.close();
        return bookmarkItems;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}