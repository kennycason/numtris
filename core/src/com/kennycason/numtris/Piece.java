package com.kennycason.numtris;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by kenny on 12/26/15.
 */
public class Piece {
    public final int value;
    public final Texture texture;
    public int x;
    public int y;
    public long lastDropped;
    public long lastMoved;

    public Piece(final int value, final Texture texture) {
        this.value = value;
        this.texture = texture;
        x = Numtris.WIDTH / 2;
        y = Numtris.HEIGHT - 1;
        lastDropped = TimeUtils.millis();
    }
}
