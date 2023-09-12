package com.example.yrmultimediaco.fileexplorer;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class SearchViewAdapter extends AbstractItem<SearchViewAdapter, SearchViewAdapter.ViewHolder> {

    private String searchQuery;

    public SearchViewAdapter(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @NonNull
    @Override
    public SearchViewAdapter.ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.text1;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.custom_search_suggestion_item;
    }

    static class ViewHolder extends FastAdapter.ViewHolder {

        private TextView searchQueryTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.text1);
        }

        @Override
        public void unbindView(IItem item) {
            searchQueryTextView.setText(null);
        }

        @Override
        public void bindView(IItem item, List payloads) {
            if (item instanceof SearchViewAdapter){
                SearchViewAdapter searchViewAdapter = (SearchViewAdapter) item;
                searchQueryTextView.setText(searchViewAdapter.getSearchQuery());
            }

        }
    }

}
