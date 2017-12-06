package edu.uw.info448.indiceision;

/**
 * Created by Jason on 12/4/2017.
 */

public class Review {
    private String name;
    private String date;
    private String rating;
    private String review;

    public Review(String name, String date, String rating, String review) {
        this.name = name;
        this.date = date;
        this.rating = rating;
        this.review = review;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public String getRating() {
        return this.rating;
    }

    public String getReview() {
        return this.review;
    }
}
