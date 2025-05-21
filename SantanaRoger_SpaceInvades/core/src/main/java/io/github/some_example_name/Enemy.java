package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> sideStartAnimation;
    private Animation<TextureRegion> sideLoopAnimation;
    private float stateTime;
    private float x, y;
    private float speedX = 1f;
    private boolean movingRight = true;
    private float worldWidth;
    private float stepDown = 0.5f;

    private Texture bulletTexture;
    private Array<Sprite> bullets;
    private float shootCooldown = 2f;
    private float shootTimer = 0f;

    private enum State { IDLE, SIDE_START, SIDE_LOOP }
    private State currentState = State.IDLE;

    public Enemy(float startX, float startY, float worldWidth) {
        this.x = startX;
        this.y = startY;
        this.worldWidth = worldWidth;
        this.bullets = new Array<>();
        this.bulletTexture = new Texture("bullet_1.png");

        TextureRegion[] idleFrames = new TextureRegion[5];
        TextureRegion[] sideStartFrames = new TextureRegion[3];
        TextureRegion[] sideLoopFrames = new TextureRegion[4];

        for (int i = 0; i < 5; i++)
            idleFrames[i] = new TextureRegion(new Texture("idle_" + i + ".png"));

        for (int i = 0; i < 3; i++)
            sideStartFrames[i] = new TextureRegion(new Texture("side_" + i + ".png"));

        for (int i = 0; i < 4; i++)
            sideLoopFrames[i] = new TextureRegion(new Texture("trueside_" + i + ".png"));

        idleAnimation = new Animation<TextureRegion>(0.2f, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        sideStartAnimation = new Animation<TextureRegion>(0.15f, sideStartFrames);

        sideLoopAnimation = new Animation<TextureRegion>(0.1f, sideLoopFrames);
        sideLoopAnimation.setPlayMode(Animation.PlayMode.LOOP);

    }

    public void update(float delta) {
        stateTime += delta;
        shootTimer += delta;

        float move = speedX * delta * (movingRight ? 1 : -1);
        x += move;

        if ((movingRight && x > worldWidth - 1) || (!movingRight && x < 0)) {
            movingRight = !movingRight;
            y -= stepDown;
            stateTime = 0;
            currentState = State.SIDE_START;
        }

        if (currentState == State.SIDE_START && sideStartAnimation.isAnimationFinished(stateTime)) {
            currentState = State.SIDE_LOOP;
            stateTime = 0;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            bullet.translateY(-2f * delta);
            if (bullet.getY() < 0) bullets.removeIndex(i);
        }
    }

    public void draw(Batch batch) {

        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);
            switch (currentState) {
                case IDLE:
                    currentFrame = idleAnimation.getKeyFrame(stateTime);
                    break;
                case SIDE_START:
                    currentFrame = idleAnimation.getKeyFrame(stateTime);
                    break;
                case SIDE_LOOP:
                    currentFrame = idleAnimation.getKeyFrame(stateTime);
                    break;
        };

        batch.draw(currentFrame, x, y, 1f, 1f);

        for (Sprite bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public void shoot() {
        if (shootTimer >= shootCooldown) {
            Sprite bullet = new Sprite(bulletTexture);
            bullet.setSize(0.2f, 0.5f);
            bullet.setPosition(x + 0.4f, y);
            bullets.add(bullet);
            shootTimer = 0f;

            System.out.println("Estoy Disparando");

        }
    }


    public Array<Sprite> getBullets() {
        return bullets;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 1f, 1f);
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
