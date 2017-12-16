package com.jeve.cr.activity.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jeve.cr.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhangliang on 2017/12/15.
 */

public class AlbumAdapter extends BaseAdapter {
    private ArrayList<File> fileList;
    private LayoutInflater inflater;

    public AlbumAdapter(Context context, ArrayList<File> fileList) {
        this.fileList = fileList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.album_item_layout, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoaderTool.getInstance().loadImage(fileList.get(i).getAbsolutePath(), viewHolder.iv);
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        ViewHolder(View itemView) {
            iv = itemView.findViewById(R.id.album_item_iv);
        }
    }
}
