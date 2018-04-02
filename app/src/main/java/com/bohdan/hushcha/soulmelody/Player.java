package com.bohdan.hushcha.soulmelody;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

/**
 * Created by bohdan on 30.04.17.
 */

public class Player {

    private String[] STAR = {"*"};
    private Activity activity;


    public Player(Activity activity) {
        if (activity == null)
            throw  new IllegalArgumentException("Activity cannot be null");

        this.activity = activity;
    }

    public ArrayList<Song> listAllSongs()
    { //Fetch path to all the files from internal & external storage n store it in songList
        Cursor cursor;
        ArrayList<Song> songList = new ArrayList<Song>();
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        if (isSdPresent()) {
            cursor = activity.managedQuery(allSongsUri, STAR, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst())
                {
                    do {
                        Song song = new Song();

                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String[] res = data.split("\\.");
                        song.setSongName(res[0]);
                        //Log.d("test",res[0] );
                        song.setSongFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                        song.setSongId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        song.setSongFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                        song.setSongAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                        song.setSongUri(ContentUris.withAppendedId(
                                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
                        String duration = getDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        song.setSongDuration(duration);

                        songList.add(song);
                    }
                    while (cursor.moveToNext());
                    return songList;
                }
                cursor.close();
            }
        }
        return null;
    }


    //Check whether sdcard is present or not
    private static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    //Method to convert the millisecs to min & sec
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private static String getDuration(long millis)
    {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(6);
        sb.append(minutes < 10 ? "0" + minutes : minutes);
        sb.append(":");
        sb.append(seconds < 10 ? "0" + seconds : seconds);
        return sb.toString();
    }






}
