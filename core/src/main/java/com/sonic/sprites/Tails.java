package com.sonic.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.sonic.Main;
import com.sonic.screens.PlayScreen;

public class Tails extends Sprite {
    public enum State{STANDING,WALKING,RUNNING,FLYING,FALLING,DRON};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;
    private TextureRegion tailsStand;
    private Animation<TextureRegion> tailsWaitingAnimation;
    private Animation<TextureRegion> tailsWalk;
    private Animation<TextureRegion> tailsRun;
    private Animation<TextureRegion> tailsFly;
    private Animation<TextureRegion> tailsDron;
    private float stateTimer;
    private boolean runningRight;
    public boolean isDronModeActive=false;

    public boolean onGround;


    public Tails(PlayScreen screen){
        //TextureAtlas del PlayScreen
        super(screen.getAtlas().findRegion("tails_waiting",1));
        tailsStand=screen.getAtlas().findRegion("tails_waiting", 1);
        this.world=screen.getWorld();
        currentState=State.STANDING;
        previousState=State.STANDING;
        stateTimer=0;
        runningRight=true;

        setBounds(0,0,tailsStand.getRegionWidth()/ Main.PPM,tailsStand.getRegionHeight()/Main.PPM);

        //Cuerpo de Tails
        defineTails();

        Array<TextureRegion> frames=new Array<>();

        //Iteracion para 'tails_waiting'
        for(int i=1;i<=10;i++){
            TextureRegion frame=screen.getAtlas().findRegion("tails_waiting",i);
            frames.add(frame);
        }
        tailsWaitingAnimation=new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,tailsWaitingAnimation.getKeyFrame(0).getRegionWidth()/Main.PPM,tailsWaitingAnimation.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(tailsWaitingAnimation.getKeyFrame(0));

        //Iteracion para 'tails_walk'
        for(int i=1;i<=6;i++){
            TextureRegion frame=screen.getAtlas().findRegion("tails_walk", i);
            frames.add(frame);
        }
        tailsWalk=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,tailsWalk.getKeyFrame(0).getRegionWidth()/Main.PPM,tailsWalk.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(tailsWalk.getKeyFrame(0));

        //Iteracion para 'tails_run'
        for(int i=1;i<=13;i++){
            TextureRegion frame=screen.getAtlas().findRegion("tails_run", i);
            frames.add(frame);
        }
        tailsRun=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,tailsRun.getKeyFrame(0).getRegionWidth()/Main.PPM,tailsRun.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(tailsRun.getKeyFrame(0));

        //Iteracion para 'tails_flying'
        for(int i=1;i<=14;i++){
            TextureRegion frame=screen.getAtlas().findRegion("tails_fly", i);
            frames.add(frame);
        }
        tailsFly=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,tailsFly.getKeyFrame(0).getRegionWidth()/Main.PPM,tailsFly.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(tailsFly.getKeyFrame(0));

        //Iteracion para 'tails_dron'
        for(int i=1;i<=15;i++){
            TextureRegion frame=screen.getAtlas().findRegion("tails_dron", i);
            frames.add(frame);
        }
        tailsDron=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);

//        Array<TextureRegion> lastFourFrames = new Array<>();
//        int totalFramesTornado = frames.size;
//

        b2Body.setUserData(this);
    }

    public void setOnGround(boolean onGround) {
        if (this.onGround != onGround) {
            Gdx.app.log("Tails", "onGround changed to: " + onGround);
        }
        this.onGround=onGround;
    }


    public void update(float dt){
        State newState = getState(); // Obtener el nuevo estado basado en el movimiento y el modo dron
        if(newState != currentState){ // Si el estado cambió
            stateTimer = 0; // Reinicia el timer para la nueva animación
            previousState = currentState; // Guarda el estado anterior
            currentState = newState; // Actualiza el estado actual
        } else {
            stateTimer += dt; // Continúa el timer para la misma animación
        }
        previousState = currentState;

        float tailsMainBodyRadius = 0;
        for (Fixture f : b2Body.getFixtureList()) {
            if (f.getShape().getType() == Shape.Type.Circle) {
                tailsMainBodyRadius = f.getShape().getRadius();
                break;
            }
        }

        float finalSpriteY = b2Body.getPosition().y - ((getHeight() / 2) - tailsMainBodyRadius);
        finalSpriteY -= (6.0f / Main.PPM); // Mueve el sprite 1 pixel hacia abajo (ajusta este 1.0f si es mucho o poco)

        setPosition(b2Body.getPosition().x - getWidth() / 2, finalSpriteY);

        setRegion(getFrame(dt));
    }


    public TextureRegion getFrame(float dt){
        currentState=getState();

        TextureRegion region;
        switch(currentState){
            case FLYING:
                region=tailsFly.getKeyFrame(stateTimer,true);
                break;
            case WALKING:
                region=tailsWalk.getKeyFrame(stateTimer,true);
                break;
            case DRON:
                region = tailsDron.getKeyFrame(stateTimer, true);
                break;
            case RUNNING:
                region = tailsRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                //region=tailsStand;
                region=tailsWaitingAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        if((b2Body.getLinearVelocity().x<0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight=false;
        }else if((b2Body.getLinearVelocity().x>0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight=true;
        }
//        stateTimer=currentState==previousState?stateTimer+dt:0;
//
//        previousState=currentState;
        return region;
    }
    public boolean isOnGround() {
        return onGround;
    }

    public void activateDronMode() {
        if (!isDronModeActive) {
            Gdx.app.log("Tails", "Dron Mode Activated!");
            isDronModeActive = true;
            // Opcional: Ajustar la gravedad o la velocidad lineal para un efecto de flotación
            // Por ejemplo, si Tails flota en modo dron:
            // b2Body.setGravityScale(0); // Elimina la gravedad
            // b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0); // Detiene el movimiento vertical
        }
    }

    public void deactivateDronMode() {
        if (isDronModeActive) {
            Gdx.app.log("Tails", "Dron Mode Deactivated!");
            isDronModeActive = false;
            // Opcional: Restaurar la gravedad si fue modificada
            // b2Body.setGravityScale(1);
        }
    }


    public State getState(){
        float velocityY = b2Body.getLinearVelocity().y;
        float velocityX = b2Body.getLinearVelocity().x;

        // Si el modo DRON está activo, tiene la mayor prioridad
        if (isDronModeActive) { //
            b2Body.setGravityScale(0); // Elimina la gravedad si está en modo dron
            //b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);
            return State.DRON; //
        }else {
            b2Body.setGravityScale(1); // Restaura la gravedad cuando no está en modo dron
        }

        // Lógica para VUELO o CAÍDA (solo si no está en modo DRON)
        if (!isOnGround()) {
            if (velocityY > 0.05f) { // Un pequeño umbral para vuelo ascendente
                return State.FLYING; //
            } else if (velocityY < -0.05f) { // Un pequeño umbral para caída
                return State.FALLING; //
            }

            return State.FALLING;
        }

        // Estados de TIERRA (si está en el suelo y no en modo DRON)
        if (Math.abs(velocityX) > 0.5f) {
            return State.RUNNING;
        } else if (Math.abs(velocityX) > 0.05f) {
            return State.WALKING;
        }

        // Por defecto, si está en el suelo y no se mueve horizontalmente, está QUIETO.
        return State.STANDING;
    }

    public void defineTails() {
        BodyDef bDef = new BodyDef();
        float startYInPixels = 144.0f + (tailsStand.getRegionHeight() / 2.0f);
        bDef.position.set(32 / Main.PPM, startYInPixels / Main.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        b2Body.setUserData(this);

        //Cuerpo del Personaje
        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / Main.PPM);

        fDef.shape = shape;
        fDef.filter.categoryBits = Main.SONIC_BIT; // Este es Sonic
        fDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.COIN_BIT | Main.OBJECT_BIT;
        b2Body.createFixture(fDef);

        //Sensor para los pies
        FixtureDef footDef = new FixtureDef();

        PolygonShape footSensorShape = new PolygonShape();
        float footWidth = 10 / Main.PPM; // Ancho del pie, ajusta si es necesario
        float footHeight = 2 / Main.PPM; // Altura del pie (muy delgado)
        footSensorShape.setAsBox(footWidth / 2, footHeight / 2, new Vector2(0, -17 / Main.PPM), 0); // (width/2, height/2, offset, angle)

        footDef.shape = footSensorShape;
        footDef.isSensor = true;
        footDef.filter.categoryBits = Main.SONIC_BIT; // Sigue siendo parte de Sonic
        footDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT;
        b2Body.createFixture(footDef).setUserData("foot");

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));

        FixtureDef headDef = new FixtureDef(); // Crear una nueva FixtureDef para claridad
        headDef.shape = head;
        headDef.isSensor = true;
        headDef.filter.categoryBits = Main.SONIC_BIT; // Sigue siendo parte de Sonic
        headDef.filter.maskBits = Main.BRICK_BIT;

        b2Body.createFixture(headDef).setUserData("head");
    }

}
