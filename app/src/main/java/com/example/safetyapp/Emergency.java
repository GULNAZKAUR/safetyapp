package com.example.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Emergency extends Fragment {

TextView tv11;
ImageView imv11;
    MediaPlayer mPlayer;
   MediaPlayer mediaPlayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_emergency, container, false);
        imv11= v.findViewById(R.id.imv11);
        imv11.setClickable(true);
        imv11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked",Toast.LENGTH_SHORT).show();

                mPlayer = new MediaPlayer();

                // Set the media player audio stream type
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try{
                    //following 2 lines of code aim to maximise the vol of audio being played even if phone's vol is set low.
                    AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
                    mediaPlayer  = MediaPlayer.create(getContext(), R.raw.police_siren);
                    mediaPlayer.start(); // no need to call prepare(); create() does that for you

//                    Intent in = new Intent(getContext(), MyService.class);
//                    in.setAction("EMERGENCY SITUATION");
//                    getContext().startService(in);
//                    Intent in = new Intent(getContext(), MyService.class);
//                    in.setAction("STOP EMERGENCY SITUATION");
//                    getContext().startService(in);

                }
                catch (Exception e){
                    Log.d("MYMSG: ",e.getMessage());
                }
            }

        });



        return  v; //returns fragment's view created above.
    }

}