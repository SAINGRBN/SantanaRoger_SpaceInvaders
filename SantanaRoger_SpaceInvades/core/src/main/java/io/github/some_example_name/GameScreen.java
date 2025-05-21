package io.github.some_example_name;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class GameScreen implements Screen {
    private MainGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Player player;
    private Array<Enemy> enemies;

    public GameScreen(MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 16, 9);
        batch = new SpriteBatch();

        player = new Player(7, 1);
        enemies = new Array<>();
        for (int i = 0; i < 5; i++) {
            enemies.add(new Enemy(i * 2f, 7, 16));
        }
    }

    private void logic(float delta) {
        player.update(delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.shoot();

            for (Sprite bullet : enemy.getBullets()) {
                if (bullet.getBoundingRectangle().overlaps(player.getBounds())) {
                    player.removeLive();
                    enemy.getBullets().removeValue(bullet, true);
                }
            }
        }

        Array<Sprite> bullets = player.getBullets();
        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            for (int j = enemies.size - 1; j >= 0; j--) {
                if (bullet.getBoundingRectangle().overlaps(enemies.get(j).getBounds())) {
                    bullets.removeIndex(i);
                    enemies.removeIndex(j);
                    break;
                }
            }
        }

        if (player.getLives() <= 0) {
            game.setScreen(new GameOverScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        logic(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        batch.dispose();
    }
}
