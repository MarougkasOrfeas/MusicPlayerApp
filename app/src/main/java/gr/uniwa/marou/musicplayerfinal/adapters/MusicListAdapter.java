package gr.uniwa.marou.musicplayerfinal.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import gr.uniwa.marou.musicplayerfinal.model.AudioModel;
import gr.uniwa.marou.musicplayerfinal.media.MyMediaPlayer;
import gr.uniwa.marou.musicplayerfinal.R;
import gr.uniwa.marou.musicplayerfinal.activities.MusicPlayerActivity;

/**
 * MusicListAdapter is a RecyclerView adapter for displaying a list of music tracks.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicViewHolder>{

    private ArrayList<AudioModel> songsList;
    private final Context context;
    private final int selectedColor;
    private final int defaultColor;

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;

        // Initialize colors (consider defining them in resources)
        selectedColor = Color.parseColor("#00FF00");
        defaultColor = Color.parseColor("#000000");
    }

    /**
     * Called when the RecyclerView needs a new {@link MusicViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@link MusicViewHolder} that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MusicViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_songs_list, parent, false);
        return new MusicViewHolder(view);
    }

    /**
     * Called to display the data at the specified position. This method updates the contents of
     * the {@link MusicViewHolder#titleTextView} and handles item click events to navigate to
     * the {@link MusicPlayerActivity}.
     *
     * @param holder   The {@link MusicViewHolder} to bind the data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MusicViewHolder  holder, int position) {
        AudioModel songData = songsList.get(holder.getAdapterPosition());
        holder.titleTextView.setText(songData.getTitle());

        if(MyMediaPlayer.getCurrentIndex() == holder.getAdapterPosition()){
            holder.titleTextView.setTextColor(selectedColor);
        }else{
            holder.titleTextView.setTextColor(defaultColor);
        }

        holder.itemView.setOnClickListener(v -> {
            //navigate to another activity
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.setCurrentIndex(holder.getAdapterPosition());
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("LIST", songsList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    /**
     * Returns the number of items in the list of songs.
     * @return the size of the list of songs
     */
    @Override
    public int getItemCount() {
        return songsList.size();
    }

}
