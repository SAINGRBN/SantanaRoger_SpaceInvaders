package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    private float x, y;
    private float speed = 4f;
    private float stateTime;
    private float shootCooldown = 0.5f;
    private float shootTimer = 0f;

    private Texture bulletTexture;
    private Array<Sprite> bullets;

    private enum State {
        IDLE, START_LEFT, MOVE_LEFT, START_RIGHT, MOVE_RIGHT
    }

    private State currentState = State.IDLE;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> startLeftAnimation;
    private Animation<TextureRegion> moveLeftAnimation;
    private Animation<TextureRegion> startRightAnimation;
    private Animation<TextureRegion> moveRightAnimation;


    private Animation<TextureRegion> currentAnimation;


    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.bullets = new Array<>();
        this.bulletTexture = new Texture("bullet_0.png");

        // Idle: sakuyaidle_0.png to sakuyaidle_7.png
        TextureRegion[] idleFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            idleFrames[i] = new TextureRegion(new Texture("sakuyaidle_" + i + ".png"));
        }
        idleAnimation = new Animation<>(0.15f, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Start left: sakuyastartleft_0.png to sakuyastartleft_3.png
        TextureRegion[] startLeftFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            startLeftFrames[i] = new TextureRegion(new Texture("sakuyastartleft_" + i + ".png"));
        }
        startLeftAnimation = new Animation<>(0.1f, startLeftFrames);
        startLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Move left: sakuyaleft_0.png to sakuyaleft_3.png
        TextureRegion[] moveLeftFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            moveLeftFrames[i] = new TextureRegion(new Texture("sakuyaleft_" + i + ".png"));
        }
        moveLeftAnimation = new Animation<>(0.1f, moveLeftFrames);
        moveLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Start right: sakuyastartright_0.png to sakuyastartright_3.png
        TextureRegion[] startRightFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            startRightFrames[i] = new TextureRegion(new Texture("sakuyastartright_" + i + ".png"));
        }
        startRightAnimation = new Animation<>(0.1f, startRightFrames);
        startRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Move right: sakuyaright_0.png to sakuyaright_3.png
        TextureRegion[] moveRightFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            moveRightFrames[i] = new TextureRegion(new Texture("sakuyaright_" + i + ".png"));
        }
        moveRightAnimation = new Animation<>(0.1f, moveRightFrames);
        moveRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Inicializa la animación actual
        currentAnimation = idleAnimation;
    }


    private Animation<TextureRegion> loadAnimation(String prefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);

        for (int i = 0; i < frameCount; i++) {
            Texture texture = new Texture(prefix + i + ".png");
            frames.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }
    public void update(float delta) {
        // AÑADIR: guarda el estado anterior
        State previousState = currentState;

        shootTimer += delta;
/*
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            if (currentState != State.MOVE_LEFT && currentState != State.START_LEFT) {
                currentState = State.START_LEFT;
            } else if (currentState == State.START_LEFT && startLeftAnimation.isAnimationFinished(stateTime)) {
                currentState = State.MOVE_LEFT;
            }
            x -= speed * delta;
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            if (currentState != State.MOVE_RIGHT && currentState != State.START_RIGHT) {
                currentState = State.START_RIGHT;
            } else if (currentState == State.START_RIGHT && startRightAnimation.isAnimationFinished(stateTime)) {
                currentState = State.MOVE_RIGHT;
            }
            x += speed * delta;
        } else {
            //currentState = State.IDLE;
        }*/

        // AÑADIR: actualiza el tiempo solo si el estado no cambió
        if (currentState == previousState) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            bullet.translateY(4f * delta);
            if (bullet.getY() > 10) bullets.removeIndex(i);
        }
    }


    public void draw(Batch batch) {
        TextureRegion currentFrame;

        System.out.println(currentState.toString());

        switch (currentState) {
            case START_LEFT:
                currentFrame = startLeftAnimation.getKeyFrame(stateTime);
                break;
            case MOVE_LEFT:
                currentFrame = moveLeftAnimation.getKeyFrame(stateTime);
                break;
            case START_RIGHT:
                currentFrame = startRightAnimation.getKeyFrame(stateTime);
                break;
            case MOVE_RIGHT:
                currentFrame = moveRightAnimation.getKeyFrame(stateTime);
                break;
            case IDLE:
            default:
                currentFrame = idleAnimation.getKeyFrame(stateTime);
                break;
        }

        batch.draw(currentFrame, x, y, 1f, 1f);

        for (Sprite bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public void shoot() {
        if (shootTimer >= shootCooldown) {
            Sprite bullet = new Sprite(bulletTexture);
            bullet.setSize(0.2f, 0.5f);
            bullet.setPosition(x + 0.4f, y + 1);
            bullets.add(bullet);
            shootTimer = 0f;
        }
    }


    public void receiveHit() {
        // Aquí puedes reducir vida, mostrar animación de daño, etc.
        System.out.println("Player hit!");
    }

    public Array<Sprite> getBullets() {
        return bullets;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 1f, 1f);
    }
    public void moveRight(float delta) {
        x += speed * delta;
        if (currentState != State.START_RIGHT && currentState != State.MOVE_RIGHT) {
            currentState = State.START_RIGHT;
            stateTime = 0f;

            System.out.println("PlayerAAAAAAAAAAAA hit!");
        } else if (currentState == State.START_RIGHT && startRightAnimation.isAnimationFinished(stateTime)) {
            currentState = State.MOVE_RIGHT;
            stateTime = 0f;

            System.out.println("Player hit!");
        }
    }

    public void moveLeft(float delta) {
        x -= speed * delta;
        if (currentState != State.START_LEFT && currentState != State.MOVE_LEFT) {
            currentState = State.START_LEFT;
            stateTime = 0f;
        } else if (currentState == State.START_LEFT && startLeftAnimation.isAnimationFinished(stateTime)) {
            currentState = State.MOVE_LEFT;
            stateTime = 0f;
        }
    }

    public void setIdle() {
        if (currentState != State.IDLE) {
            currentState = State.IDLE;
            stateTime = 0f;
        }
    }


    public float getX() { return x; }
    public float getY() { return y; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
}
