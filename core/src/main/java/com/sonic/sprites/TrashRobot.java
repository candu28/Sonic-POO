package com.sonic.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;

public class TrashRobot extends Enemy {
    private float stateTime=0f;
    private Animation<TextureRegion> robotAnimation;
    private Array<TextureRegion> frames;
    //private float moveSpeed = 0.5f; // Velocidad del robot
     // Empieza moviéndose a la derecha

    private float moveSpeed = 0.5f;
    private boolean movingRight = true;
//    private boolean movingRight;
//    private float launchTimer;
//    private float launchInterval = 3.0f;
    private float lastDropTime = 0f;
    private static final float DROP_INTERVAL = 3f;

    public TrashRobot(PlayScreen screen, float x, float y) {
        super(screen,x, y);

        Array<TextureRegion> frames = new Array<>();

        //Iteracion para 'trash_robot'
        for (int i = 1; i <= 8; i++) {
            TextureRegion frame = screen.getAtlas().findRegion("trash_robot", i);
            if (frame == null) {
                // Mensaje de error si alguna frame no se encuentra. Crucial para depuración.
                Gdx.app.error("TrashRobot", "Frame 'trash_robot' with index " + i + " not found in atlas!");
            }
            frames.add(frame);
        }
        robotAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        frames.clear();
        stateTime = 0;
        //setBounds(getX(), getY(), 16 / Main.PPM, 16 / Main.PPM);
        setBounds(getX(), getY(), 50 / Main.PPM, 50 / Main.PPM);
       // b2Body.setGravityScale(0);
    }

    public void update(float dt) {
        stateTime += dt;

//        if (movingRight) {
//            b2Body.setLinearVelocity(moveSpeed, b2Body.getLinearVelocity().y);
//        } else {
//            b2Body.setLinearVelocity(-moveSpeed, b2Body.getLinearVelocity().y);
//        }
//
//        // Lógica para cambiar de dirección (ej. al llegar al borde de su rango o chocar)
//        if (b2Body.getPosition().x > screen.getPlayer().b2Body.getPosition().x + 2f && movingRight) { // Ejemplo: 2 metros a la derecha de Sonic
//            movingRight = false;
//        }
//        if (b2Body.getPosition().x < screen.getPlayer().b2Body.getPosition().x - 2f && !movingRight) { // Ejemplo: 2 metros a la izquierda de Sonic
//            movingRight = true;
//        }
//        // Si tu robot debe patrullar un área fija, usa coordenadas de mundo específicas.
//        // Ejemplo:
//        // if (b2Body.getPosition().x > 5f && movingRight) { // Si pasa de 5 metros en X
//        //     movingRight = false;
//        // } else if (b2Body.getPosition().x < 2f && !movingRight) { // Si baja de 2 metros en X
//        //     movingRight = true;
//        // }
//
//        // >> LÓGICA DE LANZAMIENTO DE BASURA <<
//        launchTimer += dt;
//        if (launchTimer >= launchInterval) {
//            launchTrash();
//            launchTimer = 0; // Reinicia el temporizador
//        }
//
//        // Posicionamiento del sprite (ajuste si es necesario, como con Sonic)
//        // Si el sprite se ve por debajo/encima, ajusta esta línea
//        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
//
//        // Voltear el sprite según la dirección
//        TextureRegion region = flyAnimation.getKeyFrame(stateTime, true);
//        if ((b2Body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX()) {
//            region.flip(true, false);
//        } else if ((b2Body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX()) {
//            region.flip(true, false);
//        }
//
//        setRegion(region);
//        setPosition(b2Body.getPosition().x-getWidth()/2,b2Body.getPosition().y-getHeight()/2);
//        setRegion(flyAnimation.getKeyFrame(stateTime, true));

        // EJEMPLO BÁSICO: SI EXCEDES CIERTAS COORDENADAS X
        float currentX = b2Body.getPosition().x;
        float leftBoundary = 5.0f; // Ajusta este valor según tu mapa
        float rightBoundary = 15.0f; // Ajusta este valor según tu mapa

        if (movingRight) {
            b2Body.setLinearVelocity(new Vector2(moveSpeed, b2Body.getLinearVelocity().y));
            if (currentX > rightBoundary) {
                movingRight = false; // Cambiar a mover a la izquierda
            }
        } else {
            b2Body.setLinearVelocity(new Vector2(-moveSpeed, b2Body.getLinearVelocity().y)); // Velocidad negativa para ir a la izquierda
            if (currentX < leftBoundary) {
                movingRight = true; // Cambiar a mover a la derecha
            }
        }

        //b2Body.setLinearVelocity(0.5f, 0); // Ejemplo de movimiento constante

        if (stateTime - lastDropTime >= DROP_INTERVAL) {
            dropTrash();
            lastDropTime = stateTime;
        }

        // Posiciona el sprite del robot en base a su cuerpo de Box2D
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        setRegion(robotAnimation.getKeyFrame(stateTime, true));
        Gdx.app.log("TrashRobot", "Posición del Robot: " + b2Body.getPosition().x + ", " + b2Body.getPosition().y);
    }

    private void dropTrash() {

        System.out.println("Robot lanza basura!"); // Mensaje de depuración

        //screen.createTrash(b2Body.getPosition().x, b2Body.getPosition().y - (20 / Main.PPM));
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        //float startYInPixels = 144.0f + (sonicStand.getRegionHeight() / 2.0f);
        //bDef.position.set(32 / Main.PPM, 32/ Main.PPM);
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.KinematicBody;
        b2Body = world.createBody(bDef);

        b2Body.setUserData(this);

        //Cuerpo del Personaje
        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(25 / Main.PPM); // Por ejemplo, 25 píxeles de radio
        //shape.setRadius((float) 16 /2 / Main.PPM);
        fDef.shape = shape;
        fDef.filter.categoryBits = Main.ENEMY_BIT;
        fDef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT | Main.SONIC_BIT;


        b2Body.createFixture(fDef).setUserData("robot_body");
        shape.dispose();
    }


    public void draw(SpriteBatch batch) {
        TextureRegion frameActual = robotAnimation.getKeyFrame(stateTime, true);

         if (b2Body.getLinearVelocity().x < 0 && !frameActual.isFlipX()) {
             frameActual.flip(true, false);
         } else if (b2Body.getLinearVelocity().x > 0 && frameActual.isFlipX()) {
             frameActual.flip(true, false);
         }
        //setRegion(frameActual); // Establece la región para que Sprite la dibuje
        super.draw(batch); // Llama al draw de Sprite, que usa setRegion y setPosition

        // ELIMINAR: El robot no dibuja la lista de basura interna. PlayScreen dibuja la basura.
        // for (Trash t : trashList) { ... }
    }
}

