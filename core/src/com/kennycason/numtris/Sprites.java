package com.kennycason.numtris;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by kenny on 12/26/15.
 */
public class Sprites {
    public Texture zero;
    public Texture one;
    public Texture two;
    public Texture three;
    public Texture four;
    public Texture five;
    public Texture six;
    public Texture seven;
    public Texture eight;
    public Texture nine;

//    public Texture add;
//    public Texture sub;
//    public Texture mul;
//    public Texture div;

//    public Texture eq;
//    public Texture eq2;

    public Texture empty;

    private boolean loaded = false;

    public void load() {
        if (loaded) { return; }
        zero = new Texture("0.png");
        one = new Texture("1.png");
        two = new Texture("2.png");
        three = new Texture("3.png");
        four = new Texture("4.png");
        five = new Texture("5.png");
        six = new Texture("6.png");
        seven = new Texture("7.png");
        eight = new Texture("8.png");
        nine = new Texture("9.png");

//        add = new Texture("add.png");
//        sub = new Texture("sub.png");
//        mul = new Texture("mul.png");
//        div = new Texture("div.png");

//        eq = new Texture("eq.png");
//        eq2 = new Texture("eq2.png");

        empty = new Texture("empty.png");

        loaded = true;
    }

}
