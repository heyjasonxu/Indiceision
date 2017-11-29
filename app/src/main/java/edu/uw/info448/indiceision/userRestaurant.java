package edu.uw.info448.indiceision;

/**
 * Created by anirudhsubramanyam on 11/28/17.
 */

public class userRestaurant {

    private String restaurantName;
    private String visted;
    private String liked;

    public userRestaurant(){}

    public userRestaurant(String restaurantName, String visted, String liked){
        this.restaurantName = restaurantName;
        this.visted = visted;
        this.liked = liked;
    }

    public String getRestaurantName(){
        return this.restaurantName;
    }

    public void setRestaurantName(String restaurantName){
        this.restaurantName = restaurantName;
    }

    public String getVisted(){
        return this.visted;
    }

    public void setVisted(String visted){
        this.visted = visted;
    }

    public String getLiked(){
        return this.liked;
    }

    public void setLiked(String liked){
        this.liked = liked;
    }

}
