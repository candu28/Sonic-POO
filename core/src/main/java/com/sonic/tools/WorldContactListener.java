package com.sonic.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;
import com.sonic.sprites.InteractiveTileObject;
import com.sonic.sprites.Robotnik;
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

        //int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
//
//        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
//            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
//            Fixture object = head == fixA ? fixB : fixA;
//
//            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
//                ((InteractiveTileObject) object.getUserData()).onHeadHit();
//            }
//        }
//
//        if (fixA.getUserData() == "foot" || fixB.getUserData() == "foot") {
//            Fixture footSensor = fixA.getUserData() == "foot" ? fixA : fixB;
//            Fixture otherFixture = footSensor == fixA ? fixB : fixA;
//
//            if (otherFixture.getBody().getType() == BodyDef.BodyType.StaticBody ||
//                otherFixture.getBody().getType() == BodyDef.BodyType.KinematicBody) {
//                footContacts++;
//                Gdx.app.log("Contact", "BEGIN footContacts: " + footContacts + ". Is Sonic: " + (footSensor.getBody().getUserData() instanceof Sonic));
//                if (footSensor.getBody().getUserData() instanceof Sonic) {
//                    Sonic sonic = (Sonic) footSensor.getBody().getUserData();
//                    sonic.setOnGround(true);
//                    Gdx.app.log("Contact", "Sonic ON GROUND (from beginContact).");
//                }
//            }
        Object userDataA = fixA.getBody().getUserData();
        Object userDataB = fixB.getBody().getUserData();

        // Combinación de bits de las categorías de los cuerpos que colisionan
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // --- Detección de contacto de Robotnik con el suelo ---
        // Opción 1: Si Robotnik tiene un "sensor de pies" y colisiona con el suelo
        if ( (fixA.getUserData() != null && fixA.getUserData().equals("robotnik_foot") && fixB.getFilterData().categoryBits == Main.GROUND_BIT) ||
            (fixB.getUserData() != null && fixB.getUserData().equals("robotnik_foot") && fixA.getFilterData().categoryBits == Main.GROUND_BIT) ) {
            // Asumiendo que el UserData del cuerpo de Robotnik es el propio objeto Robotnik
            Robotnik robotnik = (Robotnik) (fixA.getUserData().equals("robotnik_foot") ? userDataA : userDataB); // Ajustar si el userData es el Body
            if (robotnik == null && userDataA instanceof Robotnik) robotnik = (Robotnik) userDataA;
            if (robotnik == null && userDataB instanceof Robotnik) robotnik = (Robotnik) userDataB;

            if (robotnik != null) {
                robotnik.setOnGround(true);
                Gdx.app.log("ContactListener", "Robotnik ha tocado el suelo (sensor de pie).");
            }
        }
        // Opción 2: Si el cuerpo principal de Robotnik colisiona directamente con el suelo
        else if ((cDef == (Main.ENEMY_BIT | Main.GROUND_BIT)) || (cDef == (Main.ENEMY_BIT | Main.BRICK_BIT)) ) {
            Robotnik robotnik = null;
            if (userDataA instanceof Robotnik) robotnik = (Robotnik) userDataA;
            else if (userDataB instanceof Robotnik) robotnik = (Robotnik) userDataB;

            if (robotnik != null) {
                robotnik.setOnGround(true);
                Gdx.app.log("ContactListener", "Robotnik ha tocado el suelo (cuerpo principal).");
            }
        }

        // --- Otros contactos (ej. Sonic vs. Robotnik) ---
        switch (cDef) {
            case Main.ENEMY_BIT | Main.SONIC_BIT:
                Gdx.app.log("ContactListener", "Sonic colisiona con Robotnik.");
                // Lógica de daño a Sonic o Robotnik aquí
                break;
            // Otros casos de colisión
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

//        if (fixA.getUserData() == "foot" || fixB.getUserData() == "foot") {
//            Fixture footSensor = fixA.getUserData() == "foot" ? fixA : fixB;
//            Fixture otherFixture = footSensor == fixA ? fixB : fixA;
//
//            if (otherFixture.getBody().getType() == BodyDef.BodyType.StaticBody ||
//                otherFixture.getBody().getType() == BodyDef.BodyType.KinematicBody) {
//                footContacts--; // Decrementar el contador
//                Gdx.app.log("Contact", "END footContacts: " + footContacts + ". Is Sonic: " + (footSensor.getBody().getUserData() instanceof Sonic));
//
//                if (footSensor.getBody().getUserData() instanceof Sonic) {
//                    Sonic sonic = (Sonic) footSensor.getBody().getUserData();
//
//                    if (footContacts <= 0) {
//                        sonic.setOnGround(false);
//                        Gdx.app.log("End Contact", "Sonic OFF GROUND (from endContact).");
//
//                    }
//                }
//            }
//        }
        Object userDataA = fixA.getBody().getUserData();
        Object userDataB = fixB.getBody().getUserData();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // --- Detección de fin de contacto de Robotnik con el suelo ---
        // Opción 1: Si Robotnik tiene un "sensor de pies" y deja el suelo
        if ( (fixA.getUserData() != null && fixA.getUserData().equals("robotnik_foot") && fixB.getFilterData().categoryBits == Main.GROUND_BIT) ||
            (fixB.getUserData() != null && fixB.getUserData().equals("robotnik_foot") && fixA.getFilterData().categoryBits == Main.GROUND_BIT) ) {
            Robotnik robotnik = (Robotnik) (fixA.getUserData().equals("robotnik_foot") ? userDataA : userDataB);
            if (robotnik == null && userDataA instanceof Robotnik) robotnik = (Robotnik) userDataA;
            if (robotnik == null && userDataB instanceof Robotnik) robotnik = (Robotnik) userDataB;

            if (robotnik != null) {
                robotnik.setOnGround(false);
                Gdx.app.log("ContactListener", "Robotnik ha dejado el suelo (sensor de pie).");
            }
        }
        // Opción 2: Si el cuerpo principal de Robotnik deja el suelo
        else if ((cDef == (Main.ENEMY_BIT | Main.GROUND_BIT)) || (cDef == (Main.ENEMY_BIT | Main.BRICK_BIT)) ) {
            Robotnik robotnik = null;
            if (userDataA instanceof Robotnik) robotnik = (Robotnik) userDataA;
            else if (userDataB instanceof Robotnik) robotnik = (Robotnik) userDataB;

            if (robotnik != null) {
                robotnik.setOnGround(false);
                Gdx.app.log("ContactListener", "Robotnik ha dejado el suelo (cuerpo principal).");
            }
        }

        // --- Otros contactos (ej. Sonic vs. Robotnik) ---
        // No hay lógica para endContact

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
