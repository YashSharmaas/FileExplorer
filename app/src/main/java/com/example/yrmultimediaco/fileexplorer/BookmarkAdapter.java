package com.example.yrmultimediaco.fileexplorer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class BookmarkAdapter extends AbstractItem<BookmarkAdapter, BookmarkAdapter.ViewHolder> {

    private String fileName;
    private byte[] thumbnail;
    private String dateTime;

    public BookmarkAdapter(String fileName, byte[] thumbnail, String dateTime) {
        this.fileName = fileName;
        this.thumbnail = thumbnail;
        this.dateTime = dateTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.bookmark_sample;
    }

    static class ViewHolder extends FastAdapter.ViewHolder {

        TextView file_name;
        IconicsImageView iconImageView;
        ImageView thumbnailImageView;
        ImageView clickImage;
        TextView itemCount, lastDate;

        public ViewHolder(View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.file_name);
            iconImageView = itemView.findViewById(R.id.iconS);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImg);
            clickImage = itemView.findViewById(R.id.overlayImg);
            itemCount = itemView.findViewById(R.id.itemCount);
            lastDate = itemView.findViewById(R.id.itemDate);


        }

        @Override
        public void unbindView(IItem item) {

        }

        @Override
        public void bindView(IItem item, List payloads) {

            if (item instanceof BookmarkAdapter){
                BookmarkAdapter adapter = (BookmarkAdapter) item;
                file_name.setText(adapter.getFileName());

                byte[] thumbnail = adapter.getThumbnail();
                if (thumbnail != null){

                    Bitmap bitmapThumbnail = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
                    thumbnailImageView.setImageBitmap(bitmapThumbnail);
                } else{
                    thumbnailImageView.setImageResource(R.drawable.file_explorer);
                }

                Glide.with(itemView.getContext())
                        .load(thumbnail)
                        .apply(RequestOptions.centerCropTransform())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(1000, 1000)
                        .thumbnail(0.1f)
                        .centerCrop()
                        .into(thumbnailImageView);

                lastDate.setText(adapter.getDateTime());

            }



        }
    }

}
