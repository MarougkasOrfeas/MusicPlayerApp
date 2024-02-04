package gr.uniwa.marou.musicplayerfinal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import gr.uniwa.marou.musicplayerfinal.media.MyMediaPlayer;
import gr.uniwa.marou.musicplayerfinal.model.AudioModel;
import gr.uniwa.marou.musicplayerfinal.adapters.MusicListAdapter;
import gr.uniwa.marou.musicplayerfinal.R;
import gr.uniwa.marou.musicplayerfinal.utils.PermissionConfig;

/**
 * MainActivity is the main entry point of the Music Player application.
 * It displays a list of music tracks retrieved from the device's external storage.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noMusicTextView;
    private ArrayList<AudioModel> songsList = new ArrayList<>();

    /**
     * Called when the activity is first created. Initializes the activity layout,
     * finds views by their respective IDs, and checks for external storage read permission.
     * If permission is granted, it loads music data; otherwise, it requests the necessary permission.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);

        if(!PermissionConfig.checkPermission(this)){
            PermissionConfig.requestPermission(this);
        }else{
            loadMusicData();
        }
    }

    public ArrayList<AudioModel> getSongsList() {
        return songsList;
    }

    /**
     * Retrieves music data from the external storage and populates the songsList.
     */
    private void loadMusicData(){
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        //Check external storage for music files
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null, null
        );

        if(cursor != null){
            while(cursor.moveToNext()){
                AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                Log.d("AudioModel", "Song Data: " + songData.toString());
                if(new File(songData.getPath()).exists()){
                    songsList.add(songData); // Add valid music tracks to the songsList
                }else{
                    Log.d("InvalidFile", "File does not exist: " + songData.getPath());
                }
            }
            cursor.close();
            MyMediaPlayer.setPlaylist(songsList);
            MyMediaPlayer.setPlaylistSize(songsList.size());
        }
        updateRecyclerView();
    }

    /**
     * Updates the RecyclerView based on the contents of the songsList.
     * If the list is empty, displays a message indicating no music tracks are available.
     */
    private void updateRecyclerView(){
        if(songsList.isEmpty()){
            noMusicTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noMusicTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (recyclerView.getAdapter() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
            }
        }
    }

    /**
     * Called when the activity is resumed. It is used to refresh the RecyclerView
     * with the latest data when the user returns to the MainActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView != null){
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

}