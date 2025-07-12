package com.sonic.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;
import com.sonic.sprites.Ring;

public class B2WorldCreator {
    public B2WorldCreator(PlayScreen screen){
        //create body and fixture variables
        World world=screen.getWorld();
        TiledMap map=screen.getMap();
        BodyDef bdef=new BodyDef();
        PolygonShape shape=new PolygonShape();
        FixtureDef fdef=new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object:map.getLayers().get("suelocol").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect=((RectangleMapObject)object).getRectangle();
            bdef.type=BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);
            body= world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
            fdef.shape=shape;

            fdef.filter.categoryBits = Main.GROUND_BIT; // Ahora el suelo es GROUND_BIT
            fdef.filter.maskBits = Main.SONIC_BIT | Main.ENEMY_BIT | Main.TRASH_BIT; // El suelo colisiona con Sonic, Enemigos y Basura
            // Puedes añadir más bits si hay otros objetos que deben colisionar con el suelo
            // <<<<<<<<<<<<<<<< FIN DEL CAMBIO >>>>>>>>>>>>>>>>>>
            body.createFixture(fdef);
//            fdef.filter.categoryBits=Main.OBJECT_BIT;
//            body.createFixture(fdef);
        }

        //create ring bodies/fixtures
//        for(MapObject object:map.getLayers().get("anillos").getObjects().getByType(RectangleMapObject.class)){
//            Rectangle rect=((RectangleMapObject)object).getRectangle();
//            new Ring(screen,rect);
//        }

    }
}
