/*
package io.github.some_example_name;
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
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Sound dropSound;
    Music music;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Vector2 touchPos;
    Player player;
    Array<Enemy> enemies;

    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        touchPos = new Vector2();
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();

        player = new Player(4f, 0.5f);

        enemies = new Array<>();
        for (int i = 0; i < 5; i++) {
            enemies.add(new Enemy(1f + i * 1.2f, 4f, viewport.getWorldWidth()));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            // Movimiento táctil
            float screenMiddle = viewport.getWorldWidth() / 2f;
            if (touchPos.y < 2f) { // Zona baja = control de movimiento
                if (touchPos.x < screenMiddle) {
                    player.moveLeft(delta);
                } else {
                    player.moveRight(delta);
                }
            }

            // Disparo táctil
            if (touchPos.y >= 2f) {
                player.shoot();
            }
        } else {
           player.setIdle();
        }
    }


    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.shoot(); // Asegura que disparen

            Array<Sprite> enemyBullets = enemy.getBullets();

            for (int i = enemyBullets.size - 1; i >= 0; i--) {
                Sprite bullet = enemyBullets.get(i);
                Rectangle bulletBounds = bullet.getBoundingRectangle();

                if (bulletBounds.overlaps(player.getBounds())) {
                    enemyBullets.removeIndex(i);     // Elimina la bala
                    player.removeLive();             // Quita una vida
                    break;                           // Una bala es suficiente
                }
            }
        }

        // Detectar colisiones entre balas y enemigos
        Array<Sprite> bullets = player.getBullets();
        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            Rectangle bulletBounds = bullet.getBoundingRectangle();

            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                Rectangle enemyBounds = enemy.getBounds();

                if (bulletBounds.overlaps(enemyBounds)) {
                    // Colisión detectada: elimina bala y enemigo
                    bullets.removeIndex(i);
                    enemies.removeIndex(j);
                    break; // salir del loop interno: la bala ya no existe
                }
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        for (Enemy enemy : enemies) {
            enemy.draw(spriteBatch);
        }

        player.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
*/
