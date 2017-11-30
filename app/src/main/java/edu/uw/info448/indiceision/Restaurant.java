package edu.uw.info448.indiceision;

/**
 * Created by anirudhsubramanyam on 11/29/17.
 */

public class Restaurant {

    private String id;
    private String restaurantName;
    private int numberSuggested;
    private int numberVisited;
    private int numberLiked;

    public Restaurant(){}

    public Restaurant(String id, String restaurantName, int numberSuggested, int numberVisited, int numberLiked){
        this.id = id;
        this.restaurantName = restaurantName;
        this.numberSuggested = numberSuggested;
        this.numberVisited = numberVisited;
        this.numberLiked = numberLiked;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getRestaurantName(){
        return this.restaurantName;
    }

    public void setRestaurantName(String restaurantName){
        this.restaurantName = restaurantName;
    }

    public int getNumberSuggested(){
        return this.numberSuggested;
    }

    public void setNumberSuggested(int numberSuggested){
        this.numberSuggested = numberSuggested;
    }

    public int getNumberVisited(){
        return this.numberVisited;
    }

    public void setNumberVisited(int numberVisited){
        this.numberVisited = numberVisited;
    }

    public int getNumberLiked(){
        return this.numberLiked;
    }

    public void setNumberLiked(int numberLiked){
        this.numberLiked = numberLiked;
    }

}
