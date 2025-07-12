package com.sonic.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;

public class Trash extends Sprite {
    private World world;
    private PlayScreen screen;
    public Body b2Body;
    private TextureRegion trashTexture;

    public boolean setToDestroy;
    private boolean destroyed;

    public Trash(PlayScreen screen, TextureAtlas trashAtlas, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        if (trashAtlas == null) {
            Gdx.app.error("Trash", "ERROR: El TextureAtlas de la basura es NULL al crear Trash.");
        } else {
            Gdx.app.log("Trash", "TextureAtlas de la basura cargado. Intentando encontrar región 'trash_item_1'.");
        }
        //trashTexture = screen.getAtlas().findRegion("trash_item_09");
        trashTexture = trashAtlas.findRegion("trash_robot",9);
        if (trashTexture == null) {
            Gdx.app.error("Trash", "ERROR: No se encontró la región 'trash_item_1' en el atlas. Asegúrate del nombre y la existencia.");
            // Opcional: Lanzar una excepción para detener el juego inmediatamente y ver el stack trace.
            // throw new RuntimeException("Región 'trash_item_1' no encontrada.");
        } else {
            Gdx.app.log("Trash", "Región 'trash_item_1' encontrada exitosamente.");
        }

        setBounds(getX(), getY(), 60f / Main.PPM, 66f / Main.PPM);
        setRegion(trashTexture); // Establece la textura inicial del sprite

        defineTrash(); // Crea el cuerpo Box2D
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2Body); // Elimina el cuerpo de Box2D
            destroyed = true;
            //b2Body = null;
            // Opcional: Podrías hacer que el sprite desaparezca o cambie a una animación de "desaparecer"
        } else if (!destroyed) {
            // Posiciona el sprite en base al cuerpo de Box2D
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            if (b2Body.getPosition().y < 0 && !setToDestroy) { // Si cae por debajo de Y=0 y no está ya marcada
                setToDestroy = true; // Marca para destruir en el próximo ciclo
                Gdx.app.log("Trash", "Basura se salió de la pantalla, marcada para destruir.");
            }
        }
    }

    private void defineTrash() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY()); // Usa la posición pasada al constructor
        bDef.type = BodyDef.BodyType.DynamicBody; // Dinámico para que caiga
        b2Body = world.createBody(bDef);
        b2Body.setUserData(this); // Asocia este sprite al cuerpo

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        // Define la forma de la basura, similar al pie de Sonic, pero para el objeto
        // Ajusta estos valores según el tamaño y forma de tu sprite de basura
        //shape.setAsBox((trashTexture.getRegionWidth() / 2f) / Main.PPM, (trashTexture.getRegionHeight() / 2f) / Main.PPM);
        shape.setAsBox((60f / 2f) / Main.PPM, (66f / 2f) / Main.PPM);

        fDef.shape = shape;
        fDef.restitution = 0.1f; // Pequeño rebote al caer
        fDef.friction = 0.8f; // Fricción para que no se deslice mucho
        fDef.filter.categoryBits = Main.TRASH_BIT; // Una nueva categoría para la basura
        fDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT | Main.SONIC_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT; // Colisiona con suelo, ladrillos, Sonic

        b2Body.createFixture(fDef).setUserData("trash_item"); // UserData para identificar en contactos
        shape.dispose();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        setToDestroy = true;
    }

}
