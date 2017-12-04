package edu.uw.info448.indiceision;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Anirudh Subramanyam on 11/28/2017.
 */

public class userRestaurantViewHolder extends RecyclerView.ViewHolder {

    public TextView restaurantName;
    public TextView restaurantLiked;

    public userRestaurantViewHolder(View itemView) {
        super(itemView);

        restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
        restaurantLiked = (TextView) itemView.findViewById(R.id.liked);
    }
}
