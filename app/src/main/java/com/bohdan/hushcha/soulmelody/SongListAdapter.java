package com.bohdan.hushcha.soulmelody;

/**
 * Created by Bohdan on 4/13/2017.
 */

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class SongListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<Song> songList;//Data Source for ListView

    public SongListAdapter(Context context, ArrayList<Song> list) {
        mContext = context;
        this.songList = list;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Song getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.song_list_item, null);
        }

        ViewHolder holder = new ViewHolder();
        holder.mImgSong = (ImageView) convertView.findViewById(R.id.img_listitem_file);
        holder.mtxtSongName = (TextView) convertView.findViewById(R.id.txt_listitem_filename);
        holder.mTxtSongAlbumName = (TextView) convertView.findViewById(R.id.txt_listitem_albumname);
        holder.mTxtSongDuration = (TextView) convertView.findViewById(R.id.txt_listitem_duration);


        holder.mImgSong.setImageResource(R.drawable.no_clipart);
        holder.mtxtSongName.setText(songList.get(position).getSongName());
        holder.mTxtSongAlbumName.setText(songList.get(position).getSongAlbumName());
        holder.mTxtSongDuration.setText(songList.get(position).getSongDuration());
        convertView.setTag(holder);
        return convertView;
    }

    public void setSongsList(ArrayList<Song> list) {
        songList = list;
        this.notifyDataSetChanged();
    }
}