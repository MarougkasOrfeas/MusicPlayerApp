package gr.uniwa.marou.musicplayerfinal.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import gr.uniwa.marou.musicplayerfinal.receivers.NotificationReceiver;
import gr.uniwa.marou.musicplayerfinal.model.AudioModel;
import gr.uniwa.marou.musicplayerfinal.media.MyMediaPlayer;
import gr.uniwa.marou.musicplayerfinal.R;

/**
 * MusicPlayerActivity is responsible for playing music tracks, providing controls for playback,
 * and displaying relevant information.
 */
public class MusicPlayerActivity extends AppCompatActivity {

    // UI components
    private TextView titleTv, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageView pausePlay, nextBtn, previousBtn, musicIcon, goBackwards, goForward;

    // Data
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;
    private final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();


    /**
     * Called when the activity is first created. Initializes the activity layout,
     * finds views by their respective IDs, and sets up the initial music resources.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();

        /*
         * Updates the UI components on the main thread periodically to reflect the current
         * playback progress and status.
         */
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.pause);
                    }else{
                        pausePlay.setImageResource(R.drawable.play);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        /*
         * Sets up a listener to handle changes in the seek bar's progress.
         * When the user interacts with the seek bar, seeks the media player to the corresponding position.
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        /*
         * Sets up a listener to handle the completion of media playback.
         * If there are more songs in the playlist, proceeds to the next song; otherwise, starts from the beginning.
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (MyMediaPlayer.getCurrentIndex() < songsList.size() - 1) {
                    // Automatically proceed to the next song
                    playNextSong();
                }else{
                    // If there are no more songs, start from the beginning
                    MyMediaPlayer.setCurrentIndex(0);
                    mediaPlayer.reset();
                    setResourcesWithMusic();
                }
            }
        });
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        /*
            Logic to stop music if minimized
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
         */
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
         /*
            Logic to resume the music when accessed the application again
        if (!mediaPlayer.isPlaying()) {
           mediaPlayer.start();
        }
          */
    }

    /**
     * Initializes the UI components by finding views by their respective IDs.
     */
    private void initViews() {
        titleTv = findViewById(R.id.songTitle);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.musicIcon);
        goBackwards = findViewById(R.id.backwards10);
        goForward = findViewById(R.id.forward30);
        titleTv.setSelected(true);
    }

    /**
     * Sets up UI resources and event handlers related to music playback.
     */
    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.getCurrentIndex());
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        pausePlay.setOnClickListener(v -> pausePlay());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        goForward.setOnClickListener(v -> seekForward(30000));
        goBackwards.setOnClickListener(v -> seekBackward(10000));
        playMusic();

        createNotificationForCurrentSong();
    }

    /**
     * Plays the selected music track using the MediaPlayer.
     * Resets the MediaPlayer, sets the data source, prepares, and starts playback.
     */
    private void playMusic(){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Plays the next song in the playlist.
     */
    private void playNextSong(){
        if(MyMediaPlayer.getCurrentIndex() == songsList.size()-1){
            //MyMediaPlayer.setCurrentIndex(0);
            return;
        }
        MyMediaPlayer.incrementCurrentIndex();
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    /**
     * Plays the previous song in the playlist.
     */
    private void playPreviousSong(){
        if(MyMediaPlayer.getCurrentIndex() == 0){
            return;
        }
        MyMediaPlayer.decrementCurrentIndex();
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    /**
     * Handler for pause play song
     */
    private void pausePlay(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }

    /**
     * Seeks forward in the current song by the specified duration.
     *
     * @param milliseconds The duration to seek backward in milliseconds.
     */
    private void seekForward(int milliseconds) {
        if (mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            int newPosition = currentPosition + milliseconds;

            if (newPosition < duration) {
                mediaPlayer.seekTo(newPosition);
            } else {
                // If seeking forward goes beyond the duration, play next song
                playNextSong();
            }
        }
    }

    /**
     * Seeks backward in the current song by the specified duration.
     *
     * @param milliseconds The duration to seek backward in milliseconds.
     */
    private void seekBackward(int milliseconds) {
        if (mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = currentPosition - milliseconds;

            if (newPosition >= 0) {
                mediaPlayer.seekTo(newPosition);
            } else {
                // If seeking backward goes before the start, play the same song from start
                mediaPlayer.seekTo(0);
                currentTimeTv.setText(convertToMMSS("0"));
            }
        }
    }

    /**
     * Converts the given duration in milliseconds to a string in the format "MM:SS".
     *
     * @param duration The duration in milliseconds to be converted.
     * @return A string representing the duration in the format "MM:SS".
     */
    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(),"%02d:%02d",minutes, seconds);
    }

    /**
     * Creates a notification for the current song.
     */
    private void createNotificationForCurrentSong() {
        NotificationReceiver notificationReceiver = new NotificationReceiver();
        notificationReceiver.createNotification(this, currentSong, R.drawable.play, 0, songsList.size());
    }

}
