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

public class Sonic extends Sprite {
    public enum State{STANDING,WALKING,RUNNING,ROLLING,FALLING,STORMING};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;
    private TextureRegion sonicStand;
    private Animation<TextureRegion> sonicWalk;
    private Animation<TextureRegion> sonicRoll;
    private Animation<TextureRegion> sonicStorm;
    private Animation<TextureRegion> sonicStormLoopEnd;
    private float stateTimer;
    private boolean isTornadoActive = false;
    private boolean runningRight;

    private Animation<TextureRegion> sonicWaitingAnimation;

    public boolean onGround;


    public Sonic(PlayScreen screen){
        //TextureAtlas del PlayScreen
        super(screen.getAtlas().findRegion("sonic_waiting",1));
        sonicStand=screen.getAtlas().findRegion("sonic_waiting", 1);
        this.world=screen.getWorld();
        currentState=State.STANDING;
        previousState=State.STANDING;
        stateTimer=0;
        runningRight=true;

        setBounds(0,0,sonicStand.getRegionWidth()/Main.PPM,sonicStand.getRegionHeight()/Main.PPM);

        //Cuerpo de Sonic
        defineSonic();

        Array<TextureRegion> frames=new Array<>();

        //Iteracion para 'sonic_waiting'
        for(int i=1;i<=8;i++){
            TextureRegion frame=screen.getAtlas().findRegion("sonic_waiting",i);
            frames.add(frame);
        }
        sonicWaitingAnimation=new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,sonicWaitingAnimation.getKeyFrame(0).getRegionWidth()/Main.PPM,sonicWaitingAnimation.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(sonicWaitingAnimation.getKeyFrame(0));

        //Iteracion para 'sonic_walk'
        for(int i=1;i<=6;i++){
            TextureRegion frame=screen.getAtlas().findRegion("sonic_walk", i);
            frames.add(frame);
        }
        sonicWalk=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,sonicWalk.getKeyFrame(0).getRegionWidth()/Main.PPM,sonicWalk.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(sonicWalk.getKeyFrame(0));

        //Iteracion para 'sonic_rolling'
        for(int i=1;i<=15;i++){
            TextureRegion frame=screen.getAtlas().findRegion("sonic_rolling", i);
            frames.add(frame);
        }
        sonicRoll=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);
        frames.clear();

        setBounds(0,0,sonicRoll.getKeyFrame(0).getRegionWidth()/Main.PPM,sonicRoll.getKeyFrame(0).getRegionHeight()/Main.PPM);
        setRegion(sonicRoll.getKeyFrame(0));

        //Iteracion para 'sonic_tornado'
        for(int i=1;i<=16;i++){
            TextureRegion frame=screen.getAtlas().findRegion("sonic_tornado", i);
            frames.add(frame);
        }
        sonicStorm=new Animation<>(0.1f,frames,Animation.PlayMode.LOOP);

        Array<TextureRegion> lastFourFrames = new Array<>();
        int totalFramesTornado = frames.size;


        if (totalFramesTornado >= 4) {
            lastFourFrames.add(frames.get(totalFramesTornado - 4));
            lastFourFrames.add(frames.get(totalFramesTornado - 3));
            lastFourFrames.add(frames.get(totalFramesTornado - 2));
            lastFourFrames.add(frames.get(totalFramesTornado - 1));
        } else {
            lastFourFrames.addAll(frames);
        }

        this.sonicStormLoopEnd = new Animation<>(0.1f, lastFourFrames, Animation.PlayMode.LOOP);

        b2Body.setUserData(this);
    }

    public void setOnGround(boolean onGround) {
        if (this.onGround != onGround) {
            Gdx.app.log("Sonic", "onGround changed to: " + onGround);
        }
        this.onGround=onGround;
    }

    public void activateTornado() {
        if (!isTornadoActive) {
            System.out.println("Tornado activado!");
            isTornadoActive = true;
            currentState = State.STORMING;

            b2Body.setGravityScale(0);
            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);
        }
    }

    public void update(float dt){
        stateTimer += dt;
        float sonicMainBodyRadius = 0;
        for (Fixture f : b2Body.getFixtureList()) {
            if (f.getShape().getType() == Shape.Type.Circle) {
                sonicMainBodyRadius = f.getShape().getRadius();
                break;
            }
        }

        //float spriteOffsetY = (getHeight() / 2) - sonicMainBodyRadius;
        //float adjustedY = b2Body.getPosition().y - (sonicMainBodyRadius);
        float finalSpriteY = b2Body.getPosition().y - ((getHeight() / 2) - sonicMainBodyRadius);
        finalSpriteY -= (6.0f / Main.PPM); // Mueve el sprite 1 pixel hacia abajo (ajusta este 1.0f si es mucho o poco)

        //setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - spriteOffsetY);
        setPosition(b2Body.getPosition().x - getWidth() / 2, finalSpriteY);

        setRegion(getFrame(dt));
    }

    public void deactivateTornado() {
        if(isTornadoActive){
            System.out.println("Tornado desactivado!");
            isTornadoActive=false;
            b2Body.setGravityScale(1);
        }
    }


    public TextureRegion getFrame(float dt){
        currentState=getState();

        TextureRegion region;
        switch(currentState){
            case ROLLING:
                region=sonicRoll.getKeyFrame(stateTimer);
                break;
            case WALKING:
                region=sonicWalk.getKeyFrame(stateTimer,true);
                break;
            case STORMING:
                if (sonicStorm.isAnimationFinished(stateTimer)) {
                    float timeInLoop = stateTimer - sonicStorm.getAnimationDuration();
                    region = sonicStormLoopEnd.getKeyFrame(timeInLoop, true);
                } else {

                    region = sonicStorm.getKeyFrame(stateTimer);
                }

                break;
            case RUNNING:
            case FALLING:
            case STANDING:
            default:
                region=sonicStand;
                break;
        }
        if((b2Body.getLinearVelocity().x<0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight=false;
        }else if((b2Body.getLinearVelocity().x>0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight=true;
        }
        stateTimer=currentState==previousState?stateTimer+dt:0;

        previousState=currentState;
        return region;
    }

    public State getState(){
        if (isTornadoActive) {
            return State.STORMING;
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

    public void defineSonic(){
        BodyDef bDef=new BodyDef();
        float startYInPixels = 144.0f + (sonicStand.getRegionHeight() / 2.0f);
        bDef.position.set(32 / Main.PPM, startYInPixels / Main.PPM);
        bDef.type=BodyDef.BodyType.DynamicBody;
        b2Body= world.createBody(bDef);

        b2Body.setUserData(this);

        //Cuerpo del Personaje
        FixtureDef fDef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(12/Main.PPM);

        fDef.shape=shape;
        fDef.filter.categoryBits = Main.SONIC_BIT; // Este es Sonic
        fDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.COIN_BIT | Main.OBJECT_BIT;
        b2Body.createFixture(fDef);

        //Sensor para los pies
        FixtureDef footDef=new FixtureDef();

        PolygonShape footSensorShape = new PolygonShape();
        float footWidth = 10 / Main.PPM; // Ancho del pie, ajusta si es necesario
        float footHeight = 2 / Main.PPM; // Altura del pie (muy delgado)
        footSensorShape.setAsBox(footWidth / 2, footHeight / 2, new Vector2(0, -17 / Main.PPM), 0); // (width/2, height/2, offset, angle)

        footDef.shape = footSensorShape;
        footDef.isSensor = true;
        footDef.filter.categoryBits = Main.SONIC_BIT; // Sigue siendo parte de Sonic
        footDef.filter.maskBits = Main.GROUND_BIT | Main.BRICK_BIT;
        b2Body.createFixture(footDef).setUserData("foot");

        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/Main.PPM,6/Main.PPM),new Vector2(2/Main.PPM,6/Main.PPM));

        FixtureDef headDef = new FixtureDef(); // Crear una nueva FixtureDef para claridad
        headDef.shape = head;
        headDef.isSensor = true;
        headDef.filter.categoryBits = Main.SONIC_BIT; // Sigue siendo parte de Sonic
        headDef.filter.maskBits = Main.BRICK_BIT;

        b2Body.createFixture(headDef).setUserData("head");
    }

}
