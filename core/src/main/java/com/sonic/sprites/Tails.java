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
    private Animation<TextureRegion> tailsWalk;
    private Animation<TextureRegion> tailsRoll;
    private Animation<TextureRegion> tailsFly;
    private Animation<TextureRegion> tailsDron;
    private float stateTimer;
    //private boolean isTornadoActive = false;
    private boolean runningRight;
    private boolean isDronModeActive=false;

    private Animation<TextureRegion> tailsWaitingAnimation;

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

        //Cuerpo de Sonic
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

        //Iteracion para 'tails_flying'
        for(int i=1;i<=20;i++){
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

//    public void activateTornado() {
//        if (!i) {
//            System.out.println("Tornado activado!");
//            isTornadoActive = true;
//            currentState = Sonic.State.STORMING;
//
//            b2Body.setGravityScale(0);
//            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);
//        }
//    }

    public void update(float dt){
        stateTimer += dt;
        float tailsMainBodyRadius = 0;
        for (Fixture f : b2Body.getFixtureList()) {
            if (f.getShape().getType() == Shape.Type.Circle) {
                tailsMainBodyRadius = f.getShape().getRadius();
                break;
            }
        }

        //float spriteOffsetY = (getHeight() / 2) - sonicMainBodyRadius;
        //float adjustedY = b2Body.getPosition().y - (sonicMainBodyRadius);
        float finalSpriteY = b2Body.getPosition().y - ((getHeight() / 2) - tailsMainBodyRadius);
        finalSpriteY -= (6.0f / Main.PPM); // Mueve el sprite 1 pixel hacia abajo (ajusta este 1.0f si es mucho o poco)

        //setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - spriteOffsetY);
        setPosition(b2Body.getPosition().x - getWidth() / 2, finalSpriteY);

        setRegion(getFrame(dt));
    }

//    public void deactivateTornado() {
//        if(isTornadoActive){
//            System.out.println("Tornado desactivado!");
//            isTornadoActive=false;
//            b2Body.setGravityScale(1);
//        }
//    }


    public TextureRegion getFrame(float dt){
        currentState=getState();

        TextureRegion region;
        switch(currentState){
            case FLYING:
                region=tailsFly.getKeyFrame(stateTimer);
                break;
            case WALKING:
                region=tailsWalk.getKeyFrame(stateTimer,true);
                break;
            case DRON:
                region = tailsDron.getKeyFrame(stateTimer, true);
                break;
            case RUNNING:
            case FALLING:
            case STANDING:
            default:
                region=tailsStand;
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
        // Obtenemos la velocidad lineal del cuerpo de Box2D de Tails
        float velocityY = b2Body.getLinearVelocity().y;
        float velocityX = b2Body.getLinearVelocity().x;

        // Lógica para determinar el estado:

        // 1. Estado de VUELO o CAÍDA:
        // Si no está en el suelo
        if (!isOnGround()) {
            if (velocityY < 0) {
                return State.FALLING; // Cayendo
            } else if (velocityY > 0) {
                // Aquí podrías diferenciar entre un salto inicial y un vuelo sostenido
                // Para vuelo sostenido, podrías verificar si un botón de "volar" está presionado
                // o si velocityY es consistentemente positiva mientras no toca el suelo.
                return State.FLYING;
            }
        }

        // 2. Estados de TIERRA (si está en el suelo):
        // Usar Math.abs para la velocidad horizontal para no preocuparnos por la dirección
        if (Math.abs(velocityX) > 1.5f) { // Umbral para correr (ajusta este valor si es necesario)
            return State.RUNNING;
        } else if (Math.abs(velocityX) > 0.1f) { // Umbral para caminar (evita que el ruido del float lo haga caminar)
            return State.WALKING;
        }

        // 3. Estado DRON (si es un modo de vuelo especial/inmóvil en el aire)
        // Esto requeriría una condición específica, por ejemplo, si Tails está en un modo "dron" activado
        // o si tiene velocidad 0 en Y y está en el aire (flotando).
        // Por ejemplo, podrías tener una bandera `isDronMode`
        // if (isDronMode) {
        //     return State.DRON;
        // }
        // Si no hay una lógica clara para DRON, podrías omitirlo o darle una condición específica.
        // Por ahora, lo pondremos como una alternativa final si no hay movimiento.

        // 4. Estado QUIETO:
        // Si no se cumple ninguna de las condiciones anteriores y está en el suelo, está quieto.
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
