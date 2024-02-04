package gr.uniwa.marou.musicplayerfinal.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;

import gr.uniwa.marou.musicplayerfinal.R;
import gr.uniwa.marou.musicplayerfinal.activities.MusicPlayerActivity;
import gr.uniwa.marou.musicplayerfinal.media.MyMediaPlayer;
import gr.uniwa.marou.musicplayerfinal.model.AudioModel;

/**
 * NotificationReceiver is a BroadcastReceiver responsible for handling media playback
 * actions and creating notifications for the music player.
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_NEXT = "actionNext";
    public static final String ACTION_PLAY = "actionPlay";
    private static final int NOTIFICATION_ID = 3;

    /**
     * Creates a notification for the music player.
     *
     * @param context     The context in which the notification is created.
     * @param audioModel  The currently playing audio model.
     * @param playButton  The resource ID for the play button icon.
     * @param pos          The current position in the playlist.
     * @param size         The total size of the playlist.
     */
    public void createNotification(Context context, AudioModel audioModel,
                                   int playButton, int pos, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "1",
                    "My Playlist",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            PendingIntent titleClickPendingIntent = getPendingIntent(context, "titleClickedAction");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(R.drawable.music_icon_in_list)
                    .setContentTitle("My Playlist")
                    .setContentText("Now playing: " + audioModel.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSilent(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    // actions
                    .addAction(android.R.drawable.ic_media_previous, "Previous", getPendingIntent(context, ACTION_PREVIOUS))
                    .addAction(playButton, "Pause", getPendingIntent(context, ACTION_PLAY))
                    .addAction(android.R.drawable.ic_media_next, "Next", getPendingIntent(context, ACTION_NEXT))
                    .setContentIntent(titleClickPendingIntent);


            // Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }

    /**
     * Receives broadcasted intents and handles music player actions.
     * This method is triggered when the user interacts with the notification actions.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The Intent containing the action to be performed.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_PREVIOUS:
                    handlePreviousAction();
                    break;
                case ACTION_NEXT:
                    handleNextAction();
                    break;
                case ACTION_PLAY:
                    handlePlayAction(mediaPlayer);
                    break;
            }
        }
    }

    /**
     * Handles the "Previous" action for the music player.
     * Decrements the current index in the playlist and resets the MediaPlayer.
     */
    private void handlePreviousAction() {
        int currentIndex = MyMediaPlayer.getCurrentIndex();

        if (currentIndex > 0) {
            MyMediaPlayer.decrementCurrentIndex();
        } else {
            MyMediaPlayer.setCurrentIndex(MyMediaPlayer.getLastIndex(MyMediaPlayer.getPlaylistSize()));
        }

        MyMediaPlayer.getInstance().reset();
        startPlayback();
    }


    /**
     * Handles the "Next" action for the music player.
     * Increments the current index in the playlist and resets the MediaPlayer.
     */
    private void handleNextAction() {
        int currentIndex = MyMediaPlayer.getCurrentIndex();
        int playlistSize = MyMediaPlayer.getPlaylistSize();

        if (currentIndex < playlistSize - 1) {
            MyMediaPlayer.incrementCurrentIndex();
        } else {
            MyMediaPlayer.setCurrentIndex(0); // Wrap around to the first song if at the end of the playlist
        }

        MyMediaPlayer.getInstance().reset();
        startPlayback();
    }

    /**
     * Handles the "Play" action for the music player.
     * Pauses or starts playback based on the current state of the MediaPlayer.
     *
     * @param mediaPlayer The instance of MediaPlayer used for playback.
     */
    private void handlePlayAction(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    /**
     * This method resets the MediaPlayer, sets the data source for the current song, and
     * prepares the MediaPlayer. It sets listeners for when the preparation
     * is complete and when playback completes, allowing you to handle actions like moving to
     * the next song.
     *
     * @throws IOException If an error occurs.
     */
    private void startPlayback() {
        int currentIndex = MyMediaPlayer.getCurrentIndex();
        MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

        if (currentIndex >= 0 && currentIndex < MyMediaPlayer.getPlaylistSize()) {
            AudioModel currentSong = MyMediaPlayer.getPlaylist().get(currentIndex);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepareAsync();

                // Set a listener for when the preparation is complete
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });

                // Set a listener for when playback completes
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handleNextAction();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Get a PendingIntent for the specified action.
     *
     * @param context The context in which the PendingIntent will be created.
     * @param action  The action for which the PendingIntent is needed.
     * @return A PendingIntent for the specified action.
     */
    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent;
        if (action.equals("titleClickedAction")) {
            // Handle title click separately
            intent = new Intent(context, MusicPlayerActivity.class);
            intent.setAction(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        // Handle other actions (play, stop, next, previous)
        intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}
