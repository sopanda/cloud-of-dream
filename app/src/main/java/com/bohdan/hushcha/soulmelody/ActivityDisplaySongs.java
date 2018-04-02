package com.bohdan.hushcha.soulmelody;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;


public class ActivityDisplaySongs extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton mBtnImport;
    private ListView mListSongs;
    private LinearLayout mLinearListImportedFiles;
    private RelativeLayout mRelativeBtnImport;
    private SongListAdapter mAdapterListFile;

    private ArrayList<Song> mSongList;

    public static final String SONG_ID = "songId";

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_songs);
        init();
    }

    private void init()
    {
        getActionBar();

        player = new Player(this);

        mBtnImport = (ImageButton) findViewById(R.id.btn_import_files);
        mLinearListImportedFiles = (LinearLayout) findViewById(R.id.linear_list_imported_files);
        mRelativeBtnImport = (RelativeLayout) findViewById(R.id.relative_btn_import);
        mListSongs = (ListView) findViewById(R.id.list_songs_actimport);

        mBtnImport.setOnClickListener(this);

        mSongList = new ArrayList<Song>();
        mAdapterListFile = new SongListAdapter(ActivityDisplaySongs.this, mSongList);
        mListSongs.setAdapter(mAdapterListFile);

        mListSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ActivityDisplaySongs.this, PlayerActivity.class);
                intent.putExtra(SONG_ID, position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        mSongList = player.listAllSongs();
        mAdapterListFile.setSongsList(mSongList);
        mLinearListImportedFiles.setVisibility(View.VISIBLE);
        mRelativeBtnImport.setVisibility(View.GONE);

    }

}