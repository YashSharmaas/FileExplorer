package com.example.yrmultimediaco.fileexplorer;

import static com.example.yrmultimediaco.fileexplorer.MainActivity.SORT_BY_DATE_ASCENDING;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.SORT_BY_DATE_DESCENDING;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.SORT_BY_NAME_ASCENDING;
import static com.example.yrmultimediaco.fileexplorer.MainActivity.SORT_BY_NAME_DESCENDING;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "SortBottomSheetFragment";

    public static final String PREFS_NAME = "SortPrefs";
    public static final String SORT_OPTION_KEY = "SortOption";
    private SharedPreferences sharedPreferences;

    public static SortBottomSheetFragment newInstance(){
        return new SortBottomSheetFragment();
    }
    public interface SortOptionSelectedListener {
        void onSortOptionSelected(int sortOption);
    }
    private SortOptionSelectedListener sortOptionSelectedListener;

    public void setSortOptionSelectedListener(SortOptionSelectedListener listener) {
        this.sortOptionSelectedListener = listener;
    }
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int lastSelectedSortOption = sharedPreferences.getInt(SORT_OPTION_KEY, SORT_BY_NAME_ASCENDING);



        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        view = LayoutInflater.from(requireContext()).inflate(R.layout.botton_sheet_sort,null);
        dialog.setContentView(view);

        TextView sortNameByAscending = view.findViewById(R.id.sortByNameAscending);
        sortNameByAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOptionSelectedListener != null) {
                    sortOptionSelectedListener.onSortOptionSelected(SORT_BY_NAME_ASCENDING);
                }
                dismiss();
            }
        });

        TextView sortNameBYDescending = view.findViewById(R.id.sortByNameDescending);
        sortNameBYDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOptionSelectedListener != null){
                    sortOptionSelectedListener.onSortOptionSelected(SORT_BY_NAME_DESCENDING);
                }
                dismiss();
            }
        });


        TextView sortDateByAscending = view.findViewById(R.id.sortByDateAscending);
        sortDateByAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOptionSelectedListener != null){
                    sortOptionSelectedListener.onSortOptionSelected(SORT_BY_DATE_ASCENDING);
                }
                dismiss();
            }
        });

        TextView sortDateByDescending = view.findViewById(R.id.sortByDateDescending);
        sortDateByDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOptionSelectedListener != null){
                    sortOptionSelectedListener.onSortOptionSelected(SORT_BY_DATE_DESCENDING);
                }
                dismiss();
            }
        });

        updateUIWithSortingOption(lastSelectedSortOption);

        return dialog;
    }

    private void updateUIWithSortingOption(int sortOption) {

        if (view == null) {
            return; // Return early if rootView is null
        }

        TextView sortNameByAscending = view.findViewById(R.id.sortByNameAscending);
        TextView sortNameBYDescending = view.findViewById(R.id.sortByNameDescending);
        TextView sortDateByAscending = view.findViewById(R.id.sortByDateAscending);
        TextView sortDateByDescending = view.findViewById(R.id.sortByDateDescending);

        // Set the appropriate style (e.g., bold, color, etc.) for the selected option
        switch (sortOption) {
            case SORT_BY_NAME_ASCENDING:
                sortNameByAscending.setTypeface(null, Typeface.BOLD);
                sortNameBYDescending.setTypeface(null, Typeface.NORMAL);
                sortDateByAscending.setTypeface(null, Typeface.NORMAL);
                sortDateByDescending.setTypeface(null, Typeface.NORMAL);
                break;
            case SORT_BY_NAME_DESCENDING:
                sortNameByAscending.setTypeface(null, Typeface.NORMAL);
                sortNameBYDescending.setTypeface(null, Typeface.BOLD);
                sortDateByAscending.setTypeface(null, Typeface.NORMAL);
                sortDateByDescending.setTypeface(null, Typeface.NORMAL);
                break;
            case SORT_BY_DATE_ASCENDING:
                sortNameByAscending.setTypeface(null, Typeface.NORMAL);
                sortNameBYDescending.setTypeface(null, Typeface.NORMAL);
                sortDateByAscending.setTypeface(null, Typeface.BOLD);
                sortDateByDescending.setTypeface(null, Typeface.NORMAL);
                break;
            case SORT_BY_DATE_DESCENDING:
                sortNameByAscending.setTypeface(null, Typeface.NORMAL);
                sortNameBYDescending.setTypeface(null, Typeface.NORMAL);
                sortDateByAscending.setTypeface(null, Typeface.NORMAL);
                sortDateByDescending.setTypeface(null, Typeface.BOLD);
                break;
            default:
                break;
        }
    }


}
