package com.sonic.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sonic.Main;
import com.sonic.scenes.Hud;
import com.sonic.sprites.*;
import com.sonic.tools.B2WorldCreator;
import com.sonic.tools.WorldContactListener;

public class PlayScreen extends ScreenAdapter {
    private Main game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d Variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private Knuckles player;

    private Array<Ring> coins;
    private boolean wasTPressedLastFrame = false;


    public PlayScreen(Main game) {
        this.game = game;
        atlas = new TextureAtlas("SonicGame.atlas");
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Main.V_WIDTH / Main.PPM, Main.V_HEIGHT / Main.PPM, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(this);

        //create sonic in game world
        //player=new Tails(this,1.0f, 8.0f);
        player = new Knuckles(this);


        world.setContactListener(new WorldContactListener(this));
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }


//    public Sonic getPlayer() {
//        return player;
//    }

    @Override
    public void show() {
    }

    public void handleInput(float dt) {
        //If user is holding down mouse move the camera through the game world
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.onGround) {
            player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
            //player.jump();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x <= 2) {
            player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x >= -2) {
            player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
        }

        boolean isTPressedCurrentFrame = Gdx.input.isKeyPressed(Input.Keys.T);
        if (isTPressedCurrentFrame && !wasTPressedLastFrame) {
            if (!player.isSuperHitModeActive) { // Si el modo dron no está activo, activarlo
                player.activateSuperHitMode(); //
            } else {
                player.deactivateSuperHitMode();
            }

//        if(!isTPressedCurrentFrame && wasTPressedLastFrame){
//            player.deactivateSuperHitMode();
//        }
//        if (isTPressedCurrentFrame && !wasTPressedLastFrame) {
//            if (player.isDronModeActive) {
//                player.deactivateDronMode();
//            } else {
//                player.activateDronMode();
//            }
//        }
//        if (player.isDronModeActive) {
//            // Movimiento ascendente
//            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//                player.b2Body.setLinearVelocity(player.b2Body.getLinearVelocity().x, 1.5f); // Ajusta la velocidad Y
//            }
//            // Movimiento descendente
//            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//                player.b2Body.setLinearVelocity(player.b2Body.getLinearVelocity().x, -1.5f);
//            }
//        }
//        if (isTPressedCurrentFrame && !wasTPressedLastFrame) {
//            if (!player.isDronModeActive) { // Si el modo dron no está activo, activarlo
//                player.activateDronMode(); //
//                // Opcional: Aquí podrías ajustar la gravedad de Box2D si el dron flota.
//                // player.b2Body.setGravityScale(0);
//            } else { // Si el modo dron ya está activo, desactivarlo
//                player.deactivateDronMode(); //
//                // Opcional: Aquí podrías restaurar la gravedad si la cambiaste.
//                // player.b2Body.setGravityScale(1);
//            }
//            //player.activateTornado();
//            //player.activateHit();
//        }

//        if(!isTPressedCurrentFrame && wasTPressedLastFrame){
//            //player.deactivateTornado();
//            //player.desactivateHit();
//        }

            wasTPressedLastFrame = isTPressedCurrentFrame;
        }
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        if (player.isSuperHitModeActive && player.isOnGround()) {
            player.deactivateSuperHitMode();
            Gdx.app.log("Fix", "Desactivando modo dron porque Tails tocó el suelo");
        }

        gameCam.position.x = player.b2Body.getPosition().x; // La cámara sigue a Robotnik en X
        gameCam.update(); // Actualiza la cámara después de cambiar su posición.
        renderer.setView(gameCam); // Actualiza la vista del renderizador del mapa.

    }

    @Override
    public void render(float delta) {
        //separate update logic from render
        update(delta);
        //handleInput(delta);

        //Clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //world.step(1 / 60f, 6, 2);
        //render game map
        renderer.render();
        //render BOX2DDebugLines
        b2dr.render(world, gameCam.combined);
        //player.update(delta);
        //set our batch to draw what the game camera sees for Sonic
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);


        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height, true);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Hud getHud() {
        return hud;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        atlas.dispose();
    }
}
