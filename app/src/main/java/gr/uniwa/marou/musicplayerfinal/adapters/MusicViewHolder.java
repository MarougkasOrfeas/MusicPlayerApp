package gr.uniwa.marou.musicplayerfinal.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import gr.uniwa.marou.musicplayerfinal.R;

/**
 * ViewHolder class for representing a single item view in the {@link MusicListAdapter}.
 * It holds references to the title TextView and icon ImageView.
 */
public class MusicViewHolder extends RecyclerView.ViewHolder{
    TextView titleTextView;
    ImageView iconImageView;

    public MusicViewHolder(View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.listedMusicTitle);
        iconImageView = itemView.findViewById(R.id.icon_view);
    }

}
