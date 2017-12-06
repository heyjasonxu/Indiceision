package edu.uw.info448.indiceision;

/**
 * Created by anirudhsubramanyam on 11/28/17.
 */

public class userRestaurant {

    private String restaurantName;
    private String liked;

    public userRestaurant(){}

    public userRestaurant(String restaurantName, String liked){
        this.restaurantName = restaurantName;
        this.liked = liked;
    }

    public String getRestaurantName(){
        return this.restaurantName;
    }

    public void setRestaurantName(String restaurantName){
        this.restaurantName = restaurantName;
    }

    public String getLiked(){
        return this.liked;
    }

    public void setLiked(String liked){
        this.liked = liked;
    }

}
