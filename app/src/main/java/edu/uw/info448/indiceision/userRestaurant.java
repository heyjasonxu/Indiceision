package edu.uw.info448.indiceision;

/**
 * Created by anirudhsubramanyam on 11/28/17.
 */

public class userRestaurant {

    private String restaurantName;
    private int visted;
    private int liked;

    public userRestaurant(){}

    public userRestaurant(String restaurantName, int visted, int liked){
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

    public int getVisted(){
        return this.visted;
    }

    public void setVisted(int visted){
        this.visted = visted;
    }

    public int getLiked(){
        return this.liked;
    }

    public void setLiked(int liked){
        this.liked = liked;
    }

}
