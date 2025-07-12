package com.sonic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sonic.screens.PlayScreen;

public class Main extends Game {
    public static final int V_WIDTH=400;
    public static final int V_HEIGHT=240;
    public static final float PPM = 100;

    public static final short GROUND_BIT=1;
    public static final short SONIC_BIT=2;
    public static final short BRICK_BIT=4;
    public static final short COIN_BIT=8;
    public static final short DESTROYED_BIT=16;
    public static final short OBJECT_BIT=32;
    public static final short ENEMY_BIT=64;
    public static final short TRASH_BIT=128;
    public static final short NOTHING_BIT = 0;

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
