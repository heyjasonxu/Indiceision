package edu.uw.info448.indiceision;

import java.util.ArrayList;

/**
 * Created by anirudhsubramanyam on 11/29/17.
 */

public class Restaurant {

    private String id;
    private String restaurantName;
    private ArrayList<String> numberSuggested;
    private ArrayList<String> numberVisited;
    private ArrayList<String> numberLiked;

    public Restaurant(){}

    public Restaurant(String id, String restaurantName, ArrayList<String> numberSuggested, ArrayList<String> numberVisited, ArrayList<String> numberLiked){
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

    public ArrayList<String> getNumberSuggested(){
        return this.numberSuggested;
    }

    public void setNumberSuggested(ArrayList<String> numberSuggested){
        this.numberSuggested = numberSuggested;
    }

    public ArrayList<String> getNumberVisited(){
        return this.numberVisited;
    }

    public void setNumberVisited(ArrayList<String> numberVisited){
        this.numberVisited = numberVisited;
    }

    public ArrayList<String> getNumberLiked(){
        return this.numberLiked;
    }

    public void setNumberLiked(ArrayList<String> numberLiked){
        this.numberLiked = numberLiked;
    }

}
