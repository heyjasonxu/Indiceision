package edu.uw.info448.indiceision;


/**
 * A simple struct to hold a shape
 */
public class Ball {

    public float dx;
    public float dy;

    public float cx;
    public float cy;

    public float left;
    public float top;
    public float right;
    public float bottom;
    public boolean moved;


    public float offset = 150;

    public Ball(float cx, float cy) {

        moved = false;
        this.cx = cx;
        this.cy = cy;

        this.left = this.cx - offset;
        this.right = this.cx + offset;
        this.top = this.cy - offset;
        this.bottom = this.cy + offset;

        this.dx = 0;
        this.dy = 0;
    }

    public void updatePosition(float cx, float cy) {

        this.cx = cx;
        this.cy = cy;

        this.left = this.cx - offset;
        this.right = this.cx + offset;
        this.top = this.cy - offset;
        this.bottom = this.cy + offset;

    }


}