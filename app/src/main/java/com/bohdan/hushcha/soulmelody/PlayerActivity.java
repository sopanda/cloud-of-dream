package com.bohdan.hushcha.soulmelody;


import android.content.Intent;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;



/**0
 * Created by Bohdan on 4/29/2017.
 */

public class PlayerActivity extends ActivityDisplaySongs implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener
{
    private ImageButton btnPlay;
    //private ImageButton btnForward;
    //private ImageButton btnBackward;
    //private ImageButton btnNext;
    //private ImageButton btnPrevious;
    //private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;

    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler;

    //private SongListAdapter songManager;
    private final int seekForwardTime = 5000; // 5000 milliseconds
    private final int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex;

    private boolean isShuffle = false;
    private boolean isRepeat = false;


    private Player player;
    private ArrayList<Song> mSongList;


    final Runnable play = new Runnable() {   // поток для встановлення прогресу музики
        @Override
        public void run() {
            if(mp != null) {
                if (mp.isPlaying()) {

                    int mCurrentPosition = mp.getCurrentPosition();
                    songProgressBar.setProgress(mCurrentPosition);

                    int min = mCurrentPosition / 60000;
                    int sec = (mCurrentPosition % 60000) / 1000;
                    songCurrentDurationLabel.setText(min + ":" + String.format("%02d", sec));
                }
                mHandler.postDelayed(this, 1000); // затримка на 1 сек
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        songTitleLabel.setText(mSongList.get(currentSongIndex).getSongName());
        songTotalDurationLabel.setText(mSongList.get(currentSongIndex).getSongDuration());
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        init();

        Intent intent = getIntent();
        if(intent != null)
            currentSongIndex = intent.getIntExtra(ActivityDisplaySongs.SONG_ID, 0);

        btnPlay.setOnClickListener(this);

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        btnShuffle.setOnClickListener(this);

        /**
         * Update timer on seekbar
         * */

        /**
         * Background Runnable thread
         * */

        PlayerActivity.this.runOnUiThread(play);

    }


    private void init(){

        player = new Player(this);
        mSongList = player.listAllSongs();

        mHandler = new Handler();

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        //btnForward = (ImageButton) findViewById(R.id.btnForward);
        //btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        //btnNext = (ImageButton) findViewById(R.id.btnNext);
        //btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        //btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        // Mediaplayer
        mp = new MediaPlayer();

        mp.setOnCompletionListener(this);

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.btnPlay: {
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    } else {
                        // Resume song or play
                        playSong(currentSongIndex);
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
                break;
            }
            case R.id.btnShuffle:
            {

                if(isShuffle)
                {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
                else
                {
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
                break;
            }
        }

    }

    /**
     * Function to play a song
     * @param songIndex - index of song
     * */
    public void playSong(int songIndex)
    {
        // Play song
        try
        {
            mp.reset();
            mp.setDataSource(mSongList.get(songIndex).getSongFullPath());
            mp.prepare();
            mp.start();
            // Displaying Song title
            songTitleLabel.setText(mSongList.get(songIndex).getSongName());
            songTotalDurationLabel.setText(mSongList.get(songIndex).getSongDuration());

            songProgressBar.setMax(mp.getDuration());



        } catch (IllegalArgumentException | IllegalStateException | IOException e)
        {
            e.printStackTrace();
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
    {
        if(mp != null && fromTouch)
            mp.seekTo(progress);
    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     * */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF

        if(isRepeat){
            playSong(currentSongIndex);
        }else if(isShuffle)
        {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((mSongList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }
        else
            {
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (mSongList.size() - 1))
            {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }
            else
            {
                // play first song
                currentSongIndex = 0;
                playSong(currentSongIndex);

            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
        mHandler.removeCallbacks(play);
    }

    /**
     * Backward button click event
     * Backward song to specified seconds
     * */
    public void backWardClick(View view) {
        int currentPosition = mp.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mp.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mp.seekTo(0);
        }
    }

    /**
     * Forward button click event
     * Forwards song specified seconds
     * */
    public void forwardClick(View view) {
        // get current song position
        int currentPosition = mp.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mp.getDuration()) {
            // forward song
            mp.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mp.seekTo(mp.getDuration());
        }
    }

    /**
     * Next button click event
     * Plays next song by taking currentSongIndex + 1
     * */
    public void nextClick(View view) {
        // check if next song is there or not
        if(currentSongIndex < mSongList.size() - 1)
        {
            ++currentSongIndex;
            if (mp.isPlaying())
                playSong(currentSongIndex);
            else {
                setViews();
            }
        }
        else
        {
            // play first song
            currentSongIndex = 0;
            if (mp.isPlaying())
                playSong(currentSongIndex);
            else {
                setViews();
            }
        }
    }

    /**
     * Back button click event
     * Plays previous song by currentSongIndex - 1
     * */

    public void previousClick(View view) {
        if (currentSongIndex > 0) {

            --currentSongIndex;
            if (mp.isPlaying())
                playSong(currentSongIndex);
            else {
                setViews();
            }
        } else {
            // play last song
            currentSongIndex = mSongList.size() - 1;
            if (mp.isPlaying())
                playSong(currentSongIndex);
            else {
                setViews();
            }
        }
    }
    private void setViews(){
        songTitleLabel.setText(mSongList.get(currentSongIndex).getSongName());
        songTotalDurationLabel.setText(mSongList.get(currentSongIndex).getSongDuration());
        songProgressBar.setProgress(0);
        songCurrentDurationLabel.setText("0:00");
    }

    /**
     * Button Click event for Play list click event
     * Launches list activity which displays list of songs
     * */
    public void backToListClick(View view) {onBackPressed();}

    /**
     * Button Click event for Repeat button
     * Enables repeat flag to true
     * */
    public void repeatClick(View view) {
        if(isRepeat)
        {
            isRepeat = false;
            Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
            btnRepeat.setImageResource(R.drawable.btn_repeat);
        }
        else
        {
            // make repeat to true
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isShuffle = false;
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }

    }




}