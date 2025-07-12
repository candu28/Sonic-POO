package com.sonic.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;

public class Ring extends InteractiveTileObject{
    private Animation<TextureRegion> coinAnimation;
    private float stateTime = 0f;
    private boolean collected = false;
    private TextureAtlas coinAtlas;
    private PlayScreen screen;
    public Ring(PlayScreen screen, Rectangle bounds,TextureAtlas atlas){
        //super(screen, bounds);
        //fixture.setUserData(this);
        super(screen, bounds);
        this.screen = screen;



        fixture.setUserData(this);
        coinAtlas = atlas;

        Filter filter = new Filter();
        filter.categoryBits = Main.COIN_BIT;
        fixture.setFilterData(filter);

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 8; i++) {
            TextureRegion frame = coinAtlas.findRegion("ring", i);
            if (frame == null) {
                Gdx.app.log("Coin", "NO Frame 'ring " + i + "' no encontrado");
            } else {
                Gdx.app.log("Coin", "SI Frame 'ring " + i + "' cargado");
                frames.add(frame);
            }
        }

        coinAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    public void update(float dt) {
        stateTime += dt;
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = coinAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame,
            body.getPosition().x - 32 / Main.PPM,
            body.getPosition().y - 32 / Main.PPM,
            64 / Main.PPM,
            64 / Main.PPM);


    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Ring","Collision");
        if (!collected) {
            collected = true;
            screen.getHud().addScore(5); //
            setCategoryFilter(Main.NOTHING_BIT); // elimina colisión
        }
    }

    private void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }
}
