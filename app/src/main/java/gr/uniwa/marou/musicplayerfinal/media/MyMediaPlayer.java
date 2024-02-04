package gr.uniwa.marou.musicplayerfinal.media;

import android.media.MediaPlayer;

import java.util.ArrayList;

import gr.uniwa.marou.musicplayerfinal.model.AudioModel;

/**
 * Singleton class for managing a MediaPlayer instance and current playback index.
 */
public class MyMediaPlayer {

    private static MediaPlayer instance;
    private static int currentIndex = -1;
    private static int playlistSize = 0;
    private static ArrayList<AudioModel> playlist;

    /**
     * Get the MediaPlayer instance. If it doesn't exist, a new instance will be created.
     *
     * @return The MediaPlayer instance.
     */
    public static synchronized MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int getCurrentIndex(){
        return currentIndex;
    }

    public static void setCurrentIndex(int newIndex){
        currentIndex = newIndex;
    }

    public static int getPlaylistSize() {
        return playlistSize;
    }

    public static void setPlaylistSize(int size) {
        playlistSize = size;
    }

    public static ArrayList<AudioModel> getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(ArrayList<AudioModel> newPlaylist) {
        playlist = newPlaylist;
    }

    /**
     * Increment the current playback index by one.
     */
    public static void incrementCurrentIndex() {
        currentIndex++;
    }

    /**
     * Decrement the current playback index by one.
     */
    public static void decrementCurrentIndex(){
        currentIndex --;
    }

    /**
     * Get the last index in the playlist.
     *
     * @param playlistSize The total size of the playlist.
     * @return The last index in the playlist.
     */
    public static int getLastIndex(int playlistSize) {
        if (playlistSize > 0) {
            return playlistSize - 1;
        } else {
            return -1;
        }
    }
}
