package com.sonic.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;
import com.sonic.sprites.InteractiveTileObject;
import com.sonic.sprites.Sonic;
import com.sonic.sprites.Trash;

public class WorldContactListener implements ContactListener {
    private int footContacts=0;
    private PlayScreen screen;

    public WorldContactListener(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        if (fixA.getUserData() == "foot" || fixB.getUserData() == "foot") {
            Fixture footSensor = fixA.getUserData() == "foot" ? fixA : fixB;
            Fixture otherFixture = footSensor == fixA ? fixB : fixA;

            if (otherFixture.getBody().getType() == BodyDef.BodyType.StaticBody ||
                otherFixture.getBody().getType() == BodyDef.BodyType.KinematicBody) {
                footContacts++;
                Gdx.app.log("Contact", "BEGIN footContacts: " + footContacts + ". Is Sonic: " + (footSensor.getBody().getUserData() instanceof Sonic));
                if (footSensor.getBody().getUserData() instanceof Sonic) {
                    Sonic sonic = (Sonic) footSensor.getBody().getUserData();
                    sonic.setOnGround(true);
                    Gdx.app.log("Contact", "Sonic ON GROUND (from beginContact).");
                }
            }
        }

        switch (cDef) {
            case Main.TRASH_BIT | Main.SONIC_BIT:
                // Si la basura golpea a Sonic
                Gdx.app.log("Contact", "Basura colisiona con Sonic!");
                Fixture trashFixture = (fixA.getFilterData().categoryBits == Main.TRASH_BIT) ? fixA : fixB;
                Trash trash = (Trash) trashFixture.getUserData();
                trash.destroy(); // Marca la basura para ser destruida
                // Podrías añadir lógica para quitar vida a Sonic aquí
                break;
            case Main.TRASH_BIT | Main.GROUND_BIT:
            case Main.TRASH_BIT | Main.BRICK_BIT: // Si la basura golpea el suelo o un ladrillo
                Gdx.app.log("Contact", "Basura colisiona con el suelo o ladrillo!");
                trashFixture = (fixA.getFilterData().categoryBits == Main.TRASH_BIT) ? fixA : fixB;
                trash = (Trash) trashFixture.getUserData();
                trash.destroy(); // Marca la basura para ser destruida
                break;
            // Añade más casos si la basura debe reaccionar a otras colisiones
        }
    }

    @Override
    public void endContact(Contact contact){
        Fixture fixA=contact.getFixtureA();
        Fixture fixB=contact.getFixtureB();

        if (fixA.getUserData() == "foot" || fixB.getUserData() == "foot") {
            Fixture footSensor = fixA.getUserData() == "foot" ? fixA : fixB;
            Fixture otherFixture = footSensor == fixA ? fixB : fixA;

            if (otherFixture.getBody().getType() == BodyDef.BodyType.StaticBody ||
                otherFixture.getBody().getType() == BodyDef.BodyType.KinematicBody) {
                footContacts--; // Decrementar el contador
                Gdx.app.log("Contact", "END footContacts: " + footContacts + ". Is Sonic: " + (footSensor.getBody().getUserData() instanceof Sonic));

                if (footSensor.getBody().getUserData() instanceof Sonic) {
                    Sonic sonic = (Sonic) footSensor.getBody().getUserData();

                    if (footContacts <= 0) {
                        sonic.setOnGround(false);
                        Gdx.app.log("End Contact", "Sonic OFF GROUND (from endContact).");

                    }
                }
            }
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
