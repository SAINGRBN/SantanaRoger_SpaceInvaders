package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.MainGame;

public class Player {
    private float x, y;
    private float speed = 200f; // píxeles por segundo
    private float stateTime;
    private float shootCooldown = 0.5f;
    private float shootTimer = 0f;

    private Texture bulletTexture;
    private Array<Sprite> bullets;

    public int lives = 3;

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

    private final com.badlogic.gdx.audio.Sound shootSound;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.bullets = new Array<>();
        this.bulletTexture = new Texture("bullet_0.png");

        // Cargar animaciones (igual que antes)
        idleAnimation = loadAnimation("sakuyaidle_", 8, 0.15f, Animation.PlayMode.LOOP);
        startLeftAnimation = loadAnimation("sakuyastartleft_", 4, 0.1f, Animation.PlayMode.NORMAL);
        moveLeftAnimation = loadAnimation("sakuyaleft_", 4, 0.1f, Animation.PlayMode.LOOP);
        startRightAnimation = loadAnimation("sakuyastartright_", 4, 0.1f, Animation.PlayMode.NORMAL);
        moveRightAnimation = loadAnimation("sakuyaright_", 4, 0.1f, Animation.PlayMode.LOOP);

        currentAnimation = idleAnimation;

        shootSound = Gdx.audio.newSound(Gdx.files.internal("disparo.wav"));
    }

    private Animation<TextureRegion> loadAnimation(String prefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(prefix + i + ".png");
            frames.add(new TextureRegion(tex));
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(playMode);
        return anim;
    }

    public void update(float delta) {
        State previousState = currentState;
        shootTimer += delta;

        if (currentState == previousState) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            bullet.translateY(400f * delta);
            if (bullet.getY() > 480) bullets.removeIndex(i); // fuera de pantalla
        }
    }

    public void draw(Batch batch) {
        TextureRegion currentFrame;

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

        batch.draw(currentFrame, x, y, 64, 64); // Tamaño en píxeles

        for (Sprite bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public void shoot(MainGame game) {
        if (shootTimer >= shootCooldown) {

            shootSound.play(game.getSfxVolume());

            Sprite bullet = new Sprite(bulletTexture);
            bullet.setSize(10, 20);
            bullet.setPosition(x + 27, y + 64); // centro horizontal
            bullets.add(bullet);
            shootTimer = 0f;

        }
    }

    public void moveRight(float delta) {
        x += speed * delta;
        if (x > 800 - 64) x = 800 - 64;

        if (currentState != State.START_RIGHT && currentState != State.MOVE_RIGHT) {
            currentState = State.START_RIGHT;
            stateTime = 0f;
        } else if (currentState == State.START_RIGHT && startRightAnimation.isAnimationFinished(stateTime)) {
            currentState = State.MOVE_RIGHT;
        }
    }

    public void moveLeft(float delta) {
        x -= speed * delta;
        if (x < 0) x = 0;

        if (currentState != State.START_LEFT && currentState != State.MOVE_LEFT) {
            currentState = State.START_LEFT;
            stateTime = 0f;
        } else if (currentState == State.START_LEFT && startLeftAnimation.isAnimationFinished(stateTime)) {
            currentState = State.MOVE_LEFT;
        }
    }

    public void setIdle() {
        if (currentState != State.IDLE) {
            currentState = State.IDLE;
            stateTime = 0f;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 64, 64);
    }

    public Array<Sprite> getBullets() {
        return bullets;
    }

    public void removeLive(int score) {
        lives--;
        if (lives <= 0) {
            MainGame game = (MainGame) Gdx.app.getApplicationListener();
            game.stopMusic();
            game.setScreen(new GameOverScreen(game, score));
        }
    }

    public int getLives() { return lives; }
}
