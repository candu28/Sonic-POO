package com.sonic.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

public class PlayScreen implements Screen{
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
    private Sonic player;
    private TrashRobot trashRobot;
    private TextureAtlas trashAtlas;

    private Array<Ring> coins;
    private boolean wasTPressedLastFrame = false;
    private Array<TrashRobot> trashRobots;
    private Array<Trash> trashItems; // Lista para almacenar los objetos de basura
    private Array<Body> bodiesToDestroy;
    private Array<Body> worldBodiesCache;


    public PlayScreen(Main game){
        this.game=game;
        atlas=new TextureAtlas("SonicGame.atlas");
        trashAtlas = new TextureAtlas("SonicGame.atlas");
        gameCam=new OrthographicCamera();
        gamePort=new FitViewport(Main.V_WIDTH/Main.PPM,Main.V_HEIGHT/Main.PPM,gameCam);
        hud=new Hud(game.batch);
        mapLoader=new TmxMapLoader();
        map=mapLoader.load("level3.tmx");
        renderer=new OrthogonalTiledMapRenderer(map,1/Main.PPM);
        gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight() /2,0);

        world=new World(new Vector2(0,-10),true);
        b2dr=new Box2DDebugRenderer();
        new B2WorldCreator(this);
        //create sonic in game world
        player=new Sonic(this);
        trashRobot = new TrashRobot(this, 7.0f, 4.0f);
        world.setContactListener(new WorldContactListener(this));
        //trashRobot=new TrashRobot(this,player.b2Body.getPosition().x + 1f,player.b2Body.getPosition().y);
        //trashRobot = new TrashRobot(this,250 / Main.PPM, 180 / Main.PPM);
        //trashRobot = new TrashRobot(this, 1.0f, 4.0f);

//        TextureAtlas coinAtlas = new TextureAtlas("Rings.atlas");
//
//        coins = new Array<>();
//        MapLayer coinLayer = map.getLayers().get("anillos");
//
//        for (MapObject object : coinLayer.getObjects()) {
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            //coins.add(new Coin(world, map, rect, coinAtlas));
//            coins.add(new Ring(this,rect,coinAtlas));
//
//        }
        trashItems = new Array<>();
        bodiesToDestroy = new Array<>();
        //worldBodiesCache = new Array<>();
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    //
    public void createTrash(float x, float y) {
        // Pasa el atlas de basura al constructor de Trash
        //trashItems.add(new Trash(this,trashAtlas, x, y));
//        TextureAtlas atlasToUse = (trashAtlas != null) ? trashAtlas : this.atlas;
//        if (atlasToUse.findRegion("trash_robot", 9) == null) {
//            Gdx.app.error("PlayScreen", "ERROR: La región 'trash_robot',9 no se encontró en el atlas al intentar crear Trash.");
//            // Puedes intentar con otro nombre o simplemente no crear la basura
//            // si el asset no existe.
//            return;
//        }
//
//        Trash newTrash = new Trash(this, atlasToUse, x, y);
//        trashItems.add(newTrash);
//        Gdx.app.log("PlayScreen", "Basura creada en: " + x + ", " + y);
        TextureAtlas atlasToUse = (trashAtlas != null) ? trashAtlas : this.atlas;
        if (atlasToUse == null) {
            Gdx.app.error("PlayScreen", "ERROR: Ningún TextureAtlas disponible para crear Trash.");
            return;
        }

        if (atlasToUse.findRegion("trash_robot", 9) == null) {
            Gdx.app.error("PlayScreen", "ERROR: La región 'trash_robot',9 no se encontró en el atlas al intentar crear Trash.");
            return;
        }

        Trash newTrash = new Trash(this, atlasToUse, x, y);
        trashItems.add(newTrash);
        Gdx.app.log("PlayScreen", "Basura creada en: " + x + ", " + y);
    }

    // << NUEVO: Método para añadir cuerpos a destruir de forma segura >>
    public void addBodyToDestroy(Body body) {
        bodiesToDestroy.add(body);
    }

    // << NUEVO: Método para obtener el jugador (si otros objetos lo necesitan) >>
//    public Sonic getPlayer() {
//        return player;
//    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        //If user is holding down mouse move the camera through the game world
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.onGround){
            player.b2Body.applyLinearImpulse(new Vector2(0,4f),player.b2Body.getWorldCenter(),true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x<=2){
            player.b2Body.applyLinearImpulse(new Vector2(0.1f,0),player.b2Body.getWorldCenter(),true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x>=-2){
            player.b2Body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2Body.getWorldCenter(),true);
        }

        boolean isTPressedCurrentFrame = Gdx.input.isKeyPressed(Input.Keys.T);
        if(isTPressedCurrentFrame && !wasTPressedLastFrame){
            player.activateTornado();
        }

        if(!isTPressedCurrentFrame && wasTPressedLastFrame){
            player.deactivateTornado();
        }

        wasTPressedLastFrame = isTPressedCurrentFrame;
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1 / 60f, 6, 2);

        player.update(dt);
//        trashRobot.update(dt);
//
//        // <<<<<<<<<<<<<<<< ACTUALIZAR Y ELIMINAR OBJETOS DE BASURA >>>>>>>>>>>>>>>>>>
//        // Iterar sobre la lista de basura. Si un objeto de basura está 'destroyed' (marcado para eliminación y su cuerpo
//        // de Box2D ya fue añadido a bodiesToDestroy por su propio método update()), entonces se elimina de esta lista.
//        for (int i = 0; i < trashItems.size; i++) {
//            Trash trash = trashItems.get(i);
//            trash.update(dt); // Llama al update de Trash, que podría marcar el cuerpo para destrucción
//            if (trash.isDestroyed()) { // Si Trash ya ha programado la destrucción de su cuerpo
//                trashItems.removeIndex(i); // Elimina el sprite de la lista de dibujado y actualización
//                i--; // Ajustar el índice debido a la eliminación
//            }
//        }
//        // <<<<<<<<<<<<<<<< FIN DE ACTUALIZAR Y ELIMINAR BASURA >>>>>>>>>>>>>>>>>>
//
//        // Destruir cuerpos Box2D que están en la lista bodiesToDestroy
//        // Esto debe hacerse fuera de world.step() y fuera de cualquier callback de Box2D.
//        for (Body body : bodiesToDestroy) {
//            if (body != null && !world.isLocked() && body.getUserData() != null) {
//                // Solo destruye si el mundo no está bloqueado y el cuerpo no es nulo
//                world.destroyBody(body);
//                Gdx.app.log("PlayScreen", "Cuerpo Box2D destruido: " + body.getUserData());
//            } else if (world.isLocked()) {
//                Gdx.app.log("PlayScreen", "WARN: El mundo está bloqueado, posponiendo la destrucción de cuerpos.");
//            } else if (body == null) {
//                Gdx.app.log("PlayScreen", "WARN: Intentando destruir un cuerpo nulo.");
//            }
//        }
//        bodiesToDestroy.clear(); // Limpiar la lista después de intentar destruir

        gameCam.position.x = player.b2Body.getPosition().x;
        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        //separate update logic from render
        update(delta);

        //Clear the game screen with black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render game map
        renderer.render();

        //render BOX2DDebugLines
        //b2dr.render(world, gameCam.combined);

        //set our batch to draw what the game camera sees for Sonic
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        //trashRobot.draw(game.batch);
        //.batch.end();
        // Dibujar anillos
//        for (Ring ring : coins) {
//            ring.draw(game.batch); // Usa draw en lugar de render si Ring extiende Sprite o tiene un draw(batch)
//        }

//        // << NUEVO: Dibujar los objetos de basura >>
//        for (Trash trash : trashItems) {
//            if (!trash.isDestroyed()) { // Solo dibuja si no ha sido destruida
//                trash.draw(game.batch);
//            }
//        }

        game.batch.end(); // << ÚNICO END AL FINAL DE TODOS LOS DIBUJOS DE JUEGO >>

//        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
//        hud.stage.draw();
//
//        for (Ring ring: coins) {
//            ring.render(game.batch);
//        }
//        trashRobot.render(game.batch);
//        player.draw(game.batch);
//        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height){
        gamePort.update(width, height,true);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
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
//        if (trashAtlas != null) {
//            trashAtlas.dispose();
//        }
  }

    public Enemy getPlayer() {
        return this.trashRobot;
    }
}
