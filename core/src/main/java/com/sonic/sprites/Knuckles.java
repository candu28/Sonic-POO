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

public class Knuckles extends Sprite {
    public enum State{STANDING,WALKING,RUNNING,ROLLING,FALLING,SUPERHIT};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;
    private TextureRegion knucklesStand;
    private Animation<TextureRegion> knucklesWaitingAnimation;
    private Animation<TextureRegion> knucklesWalk;
    private Animation<TextureRegion> knucklesRun;
    private Animation<TextureRegion> knucklesRolling;
    private Animation<TextureRegion> knucklesSuperHit;
    private float stateTimer;
    private boolean runningRight;
    public boolean isSuperHitModeActive=false;

    public boolean onGround;


    public Knuckles(PlayScreen screen){
        //TextureAtlas del PlayScreen
        super(screen.getAtlas().findRegion("knuckles_waiting",1));
        knucklesStand=screen.getAtlas().findRegion("knuckles_waiting", 1);
        this.world=screen.getWorld();
        currentState= State.STANDING;
        previousState= State.STANDING;
        stateTimer=0;
        runningRight=true;

        setBounds(0,0,knucklesStand.getRegionWidth()/ Main.PPM,knucklesStand.getRegionHeight()/Main.PPM);

        //Cuerpo de Knuckles
        defineKnuckles();

        Array<TextureRegion> frames=new Array<>();

        //Iteracion para 'knuckles_waiting'
        for(int i=1;i<=7;i++){
            TextureRegion frame=screen.getAtlas().findRegion("knuckles_waiting",i);
            frames.add(frame);
        }
        knucklesWaitingAnimation=new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,knucklesWaitingAnimation.getKeyFrame(0).getRegionWidth()/Main.PPM,knucklesWaitingAnimation.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(knucklesWaitingAnimation.getKeyFrame(0));

        //Iteracion para 'knuckles_walk'
        for(int i=1;i<=6;i++){
            TextureRegion frame=screen.getAtlas().findRegion("knuckles_walking", i);
            frames.add(frame);
        }
        knucklesWalk=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,knucklesWalk.getKeyFrame(0).getRegionWidth()/Main.PPM,knucklesWalk.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(knucklesWalk.getKeyFrame(0));

        //Iteracion para 'knuckles_run'
        for(int i=1;i<4;i++){
            TextureRegion frame=screen.getAtlas().findRegion("knuckles_run", i);
            frames.add(frame);
        }
        knucklesRun=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,knucklesRun.getKeyFrame(0).getRegionWidth()/Main.PPM,knucklesRun.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(knucklesRun.getKeyFrame(0));

        //Iteracion para 'knuckles_rolling'
        for(int i=1;i<=9;i++){
            TextureRegion frame=screen.getAtlas().findRegion("knuckles_rolling", i);
            frames.add(frame);
        }
        knucklesRolling=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,knucklesRolling.getKeyFrame(0).getRegionWidth()/Main.PPM,knucklesRolling.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(knucklesRolling.getKeyFrame(0));

        //Iteracion para 'knuckles_superhit'
        for(int i=1;i<=8;i++){
            TextureRegion frame=screen.getAtlas().findRegion("knuckles_superhit", i);
            frames.add(frame);
        }
        knucklesSuperHit=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);

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
            case ROLLING:
                region=knucklesRolling.getKeyFrame(stateTimer,true);
                break;
            case WALKING:
                region=knucklesWalk.getKeyFrame(stateTimer,true);
                break;
            case SUPERHIT:
                region = knucklesSuperHit.getKeyFrame(stateTimer, true);
                break;
            case RUNNING:
                region = knucklesRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                //region=tailsStand;
                region=knucklesWaitingAnimation.getKeyFrame(stateTimer, true);
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

    public void activateSuperHitMode() {
        if (!isSuperHitModeActive) {
            isSuperHitModeActive = true;
        }
    }

    public void deactivateSuperHitMode() {
        if (isSuperHitModeActive) {
            isSuperHitModeActive = false;
            // Opcional: Restaurar la gravedad si fue modificada
            // b2Body.setGravityScale(1);
        }
    }


    public State getState(){
        if (isSuperHitModeActive) {
            return State.SUPERHIT;
        }
        if (b2Body.getLinearVelocity().y > 0 && !onGround) {
            return State.ROLLING;
        } else if (b2Body.getLinearVelocity().y < 0 && !onGround) {
            return State.FALLING;
        } else if (b2Body.getLinearVelocity().x != 0 && onGround) {
            if (Math.abs(b2Body.getLinearVelocity().x) > 2.5f) {
                return State.RUNNING;
            }
            return State.WALKING;
        } else {
            return State.STANDING;
        }
    }

    public void defineKnuckles() {
        BodyDef bDef = new BodyDef();
        float startYInPixels = 144.0f + (knucklesStand.getRegionHeight() / 2.0f);
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
