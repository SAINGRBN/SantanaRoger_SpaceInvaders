package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SpaceInvadersGame implements ApplicationListener {
    Texture backgroundTexture;
    Texture playerTexture;
    Texture bulletTexture;
    Sound shootSound;
    Music backgroundMusic;
    SpriteBatch batch;
    FitViewport viewport;
    Sprite playerSprite;
    Vector2 touchPos;
    Array<Sprite> bullets;
    float bulletCooldown;
    Rectangle playerBounds;
    Rectangle bulletBounds;

    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        playerTexture = new Texture("player.png");
        bulletTexture = new Texture("bullet.png");
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(1, 1);
        playerSprite.setPosition(3.5f, 0.5f);
        touchPos = new Vector2();
        bullets = new Array<>();
        playerBounds = new Rectangle();
        bulletBounds = new Rectangle();
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        handleInput();
        updateLogic();
        drawGame();
    }

    private void handleInput() {
        float speed = 4f * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerSprite.translateX(-speed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerSprite.translateX(speed);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            playerSprite.setCenterX(touchPos.x);
        }

        bulletCooldown -= Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && bulletCooldown <= 0) {
            shootBullet();
            bulletCooldown = 0.5f;
        }
    }

    private void updateLogic() {
        float worldWidth = viewport.getWorldWidth();
        float playerWidth = playerSprite.getWidth();
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, worldWidth - playerWidth));

        float delta = Gdx.graphics.getDeltaTime();
        playerBounds.set(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());

        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            bullet.translateY(5f * delta);
            if (bullet.getY() > viewport.getWorldHeight()) {
                bullets.removeIndex(i);
            }
        }
    }

    private void drawGame() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        playerSprite.draw(batch);
        for (Sprite bullet : bullets) {
            bullet.draw(batch);
        }

        batch.end();
    }

    private void shootBullet() {
        Sprite bullet = new Sprite(bulletTexture);
        bullet.setSize(0.2f, 0.5f);
        bullet.setPosition(playerSprite.getX() + playerSprite.getWidth() / 2 - 0.1f, playerSprite.getY() + playerSprite.getHeight());
        bullets.add(bullet);
        shootSound.play();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();
        bulletTexture.dispose();
        shootSound.dispose();
        backgroundMusic.dispose();
    }
}
