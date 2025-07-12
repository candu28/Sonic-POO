package com.sonic.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;

public class Robotnik extends Enemy{
    public enum State{STANDING,WALKING,RANDOM,HITTING};
    public State currentState;
    public State previousState;
    public Body b2Body;
    public boolean onGround;
    private TextureRegion robotnikStand;

    private Animation<TextureRegion> robotnikWaiting;
    private Animation<TextureRegion> robotnikWalk;
    private Animation<TextureRegion> robotnikRandom;
    private Animation<TextureRegion> robotnikHitting;
    private float stateTimer;
    private boolean isHitActive = false;
    private boolean runningRight;

    public Robotnik(PlayScreen screen,float x, float y){
        //TextureAtlas del PlayScreen
        super(screen,x,y);
        robotnikStand=screen.getAtlas().findRegion("robotnik_waiting", 1);
        setRegion(robotnikStand);
        setBounds(x,y,robotnikStand.getRegionWidth()/ Main.PPM,robotnikStand.getRegionHeight()/Main.PPM);
        this.world=screen.getWorld();
        currentState=State.STANDING;
        previousState= State.STANDING;
        stateTimer=0;
        runningRight=true;
        onGround=true;

        //Cuerpo de Robotnik
        defineEnemy();

        Array<TextureRegion> frames=new Array<>();

        //Iteracion para "robotmik_waiting"
        for(int i=1;i<=7;i++){
            TextureRegion frame=screen.getAtlas().findRegion("robotnik_waiting",i);
            frames.add(frame);
        }
        robotnikWaiting=new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        frames.clear();

        //setBounds(0,0,robotnikWaiting.getKeyFrame(0).getRegionWidth()/Main.PPM,robotnikWaiting.getKeyFrame(0).getRegionHeight()/Main.PPM);
        //setRegion(robotnikWaiting.getKeyFrame(0));

        //Iteracion para "robotkik_walking"
        for(int i=1;i<=6;i++){
            TextureRegion frame=screen.getAtlas().findRegion("robotnik_walking", i);
            frames.add(frame);
        }
        robotnikWalk=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        //setBounds(0,0,robotnikWalk.getKeyFrame(0).getRegionWidth()/Main.PPM,robotnikWalk.getKeyFrame(0).getRegionHeight()/Main.PPM);
        //setRegion(robotnikWalk.getKeyFrame(0));

        //Iteracion para "robotnik_random"
        for(int i=1;i<=8;i++){
            TextureRegion frame=screen.getAtlas().findRegion("robotnik_random", i);
            frames.add(frame);
        }
        robotnikRandom=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        //setBounds(0,0,robotnikRandom.getKeyFrame(0).getRegionWidth()/Main.PPM,robotnikRandom.getKeyFrame(0).getRegionHeight()/Main.PPM);
        //setRegion(robotnikRandom.getKeyFrame(0));

        //Iteracion para "robotnik_hit"
        for(int i=1;i<=2;i++){
            TextureRegion frame=screen.getAtlas().findRegion("robotnik_hit", i);
            frames.add(frame);
        }
        robotnikHitting=new Animation<>(0.1f,frames,Animation.PlayMode.NORMAL);
        frames.clear();

        //      b2Body.setUserData(this);
    }

    public void jump() {
        // Solo permite saltar si está en el suelo
        // Necesitas una forma de saber si está en el suelo.
        // Si tienes un sensor de pies o una lógica de colisión para 'onGround', úsala.
        // Por ahora, asumiré que tienes una variable `onGround` que se actualiza correctamente.
        if (onGround) { // Si Robotnik está en el suelo
            // Aplica un impulso vertical al cuerpo de Box2D
            // Los valores (0, 4f) son un ejemplo; ajústalos para controlar la altura del salto.
            // 4f es la fuerza vertical en metros/segundo.
            b2Body.applyLinearImpulse(new Vector2(0, 4f), b2Body.getWorldCenter(), true);
            onGround = false; // Ya no está en el suelo
            Gdx.app.log("Robotnik", "Robotnik is jumping!");
        }
    }

    public void activateHit() {
        // Solo si no está ya golpeando o en un estado que no debería interrumpirse
        if (currentState != State.HITTING) {
            currentState = State.HITTING;
            isHitActive=true;
            stateTimer = 0; // Reinicia el temporizador de estado para la nueva animación
            Gdx.app.log("Robotnik", "Robotnik is hitting!");
            // Aquí podrías añadir lógica adicional como reproducir un sonido de golpe,
            // crear un área de daño temporal, etc.
        }
    }

    public void desactivateHit() {
        if(isHitActive){
            //System.out.println("Tornado desactivado!");
            isHitActive=false;
            //b2Body.setGravityScale(1);
        }
    }


    public void update(float dt){
        stateTimer += dt;
        Gdx.app.log("Robotnik", "Velocidad X: " + b2Body.getLinearVelocity().x + ", Velocidad Y: " + b2Body.getLinearVelocity().y);
        Gdx.app.log("Robotnik", "Estado actual (antes de getFrame): " + getState());
        //float sonicMainBodyRadius = 0;
//        for (Fixture f : b2Body.getFixtureList()) {
//            if (f.getShape().getType() == Shape.Type.Circle) {
//                sonicMainBodyRadius = f.getShape().getRadius();
//                break;
//            }
//        }

        //float spriteOffsetY = (getHeight() / 2) - sonicMainBodyRadius;
        //float adjustedY = b2Body.getPosition().y - (sonicMainBodyRadius);
//        float finalSpriteY = b2Body.getPosition().y - ((getHeight() / 2) - sonicMainBodyRadius);
//        finalSpriteY -= (6.0f / Main.PPM); // Mueve el sprite 1 pixel hacia abajo (ajusta este 1.0f si es mucho o poco)

        //setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - spriteOffsetY);
        //setPosition(b2Body.getPosition().x - getWidth() / 2, finalSpriteY);
        float offsetY = 5.0f; // Ajusta este valor en píxeles.
        // Si Robotnik se ve *demasiado hundido*, reduce este valor o hazlo negativo.
        // Si Robotnik flota, aumenta este valor.

        setPosition(b2Body.getPosition().x - getWidth() / 2,
            b2Body.getPosition().y - getHeight() / 2 + (offsetY / Main.PPM)); // <-- MODIFICADO

        setRegion(getFrame(dt));
        //setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2 - (6.0f / Main.PPM));

        //setRegion(getFrame(dt));
    }



    public TextureRegion getFrame(float dt){
        State newState = getState();
        Gdx.app.log("Robotnik", "getState() devolvió: " + newState); // Log para ver qué devuelve getState

        if (newState != currentState) {
            stateTimer = 0;
            Gdx.app.log("Robotnik", "CAMBIO DE ESTADO detectado: " + currentState + " -> " + newState + " (Reiniciando timer)");
        } else {
            Gdx.app.log("Robotnik", "No hay cambio de estado. Estado: " + currentState + ", Timer: " + stateTimer);
        }
        // Si el estado ha cambiado respecto al último frame, reinicia el temporizador
        // para que la nueva animación empiece desde el principio.
//        if (newState != currentState) {
//            stateTimer = 0;
//            Gdx.app.log("Robotnik", "Cambio de estado: " + currentState + " -> " + newState + " (Reiniciando timer)");
//        }
        currentState=getState();

        TextureRegion region;
        switch(currentState){
            case RANDOM:
                region=robotnikRandom.getKeyFrame(stateTimer);
                break;
            case WALKING:
                region=robotnikWalk.getKeyFrame(stateTimer,true);
                break;
            case HITTING:
                region=robotnikHitting.getKeyFrame(stateTimer,false);
                break;
            case STANDING:
            default:
                region=robotnikStand;
                break;
        }
        if((b2Body.getLinearVelocity().x<0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight=false;
        }else if((b2Body.getLinearVelocity().x>0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight=true;
        }
        //stateTimer=currentState==previousState?stateTimer+dt:0;

        //previousState=currentState;
        return region;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public State getState(){
        //float velocityX = b2Body.getLinearVelocity().x;
//        if (isTornadoActive) {
//            return State.STORMING;
//        }
//        if (b2Body.getLinearVelocity().y > 0 && !onGround) {
//            return Sonic.State.ROLLING;
//        } else if (b2Body.getLinearVelocity().y < 0 && !onGround) {
//            return Sonic.State.FALLING;
//        } else if (b2Body.getLinearVelocity().x != 0 && onGround) {
//            if (Math.abs(b2Body.getLinearVelocity().x) > 2.5f) {
//                return Sonic.State.RUNNING;
//            }
//            return Sonic.State.WALKING;
//        } else {
//            return Sonic.State.STANDING;
//        }
//        if(isHitActive){
//            return State.HITTING;
//        }
//        if (b2Body.getLinearVelocity().y > 0.1f && !onGround) {
//            // Puedes devolver STANDING o WALKING, o si creas un State.JUMPING, úsalo aquí.
//            // Por ahora, simplemente no cambia a WALKING/STANDING si está en el aire.
//            return currentState; // Mantiene el estado anterior (ej. STANDING o WALKING)
//            // o puedes forzar a STANDING si quieres que se "congele" en el aire.
//        } else if (b2Body.getLinearVelocity().y < -0.1f && !onGround) {
//            // Si está cayendo
//            return currentState; // Igual, mantiene el estado anterior
//        } else if (Math.abs(b2Body.getLinearVelocity().x) > 0.1f && onGround) {
//            return State.WALKING;
//        } else {
//            return State.STANDING;
//        }

//        if(Math.abs(velocityX)>0.1f){
//            return State.WALKING;
//        }
//        return State.STANDING;
//        if (isHitActive && !robotnikHitting.isAnimationFinished(stateTimer)) {
//            return State.HITTING;
//        }
//
//        // Prioridad 2: Lógica de movimiento (después de HITTING)
//        float velocityX = b2Body.getLinearVelocity().x;
//        float velocityY = b2Body.getLinearVelocity().y;
//
//        // Si hay movimiento horizontal significativo
//        if (Math.abs(velocityX) > 0.1f) { // Umbral pequeño para evitar "temblores"
//            return State.WALKING;
//        }
//
//        // Si no hay movimiento horizontal, pero hay movimiento vertical (saltando/cayendo)
//        // Aunque no tengas animación de salto, puedes usar esto para la lógica de onGround
//        if (Math.abs(velocityY) > 0.1f && !onGround) {
//            // Podrías devolver el estado anterior o STANDING, ya que no hay animación de salto.
//            // O si quieres un estado "en el aire", podrías crearlo.
//            return State.STANDING; // O un estado específico para "en el aire" si lo añades
//        }
//
//        // Si no se mueve horizontalmente y está en el suelo
//        // Puedes añadir lógica para RANDOM aquí si quieres que lo haga cuando esté quieto
//        // Por ejemplo: if (Math.random() < 0.001f) return State.RANDOM; // Pequeña probabilidad de ir a random
//        return State.STANDING;
        if (isHitActive && !robotnikHitting.isAnimationFinished(stateTimer)) {
            // Gdx.app.log("Robotnik_DEBUG", "Retornando HITTING: isHitActive=" + isHitActive + ", AnimFinished=" + robotnikHitting.isAnimationFinished(stateTimer));
            return State.HITTING;
        }
        // Si isHitActive es false (solté T) O la animación ya terminó, entonces pasamos a evaluar otros estados.

        // Prioridad 2: WALKING
        float velocityX = b2Body.getLinearVelocity().x;
        // Gdx.app.log("Robotnik_DEBUG", "Velocidad X: " + velocityX);
        if (Math.abs(velocityX) > 0.1f) {
            // Gdx.app.log("Robotnik_DEBUG", "Retornando WALKING.");
            return State.WALKING;
        }

        // Prioridad 3: STANDING (o saltando/cayendo si tienes lógica)
        float velocityY = b2Body.getLinearVelocity().y;
        if (Math.abs(velocityY) > 0.1f && !onGround) {
            return State.STANDING; // O un estado de salto/caída si lo implementas
        }

        // Por defecto, si nada más aplica
        // Gdx.app.log("Robotnik_DEBUG", "Retornando STANDING.");
        return State.STANDING;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        //bDef.position.set(200 / Main.PPM, 200 / Main.PPM);
        bDef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);activateHit();
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        b2Body.setUserData(this);


        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();

        shape.setRadius(25 / Main.PPM);

        fDef.shape = shape;
        fDef.filter.categoryBits = Main.ENEMY_BIT;

        // Con qué puede colisionar Robotnik:
        fDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT | Main.SONIC_BIT | Main.OBJECT_BIT;
        b2Body.createFixture(fDef).setUserData("robotnik_body");

         PolygonShape sensorAtaque = new PolygonShape();
         sensorAtaque.setAsBox(30 / Main.PPM, 20 / Main.PPM, new Vector2(0, 0), 0);
         FixtureDef sensorDef = new FixtureDef();
         sensorDef.shape = sensorAtaque;
         sensorDef.isSensor = true;
         sensorDef.filter.categoryBits = Main.ENEMY_BIT;
         sensorDef.filter.maskBits = Main.SONIC_BIT; // Solo detecta a Sonic
         b2Body.createFixture(sensorDef).setUserData("robotnik_attack_sensor");

        shape.dispose(); // Libera la memoria de la forma cuando ya no se necesita
        sensorAtaque.dispose();
    }
}
