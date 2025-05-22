package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private final MainGame game;
    private Texture backgroundTexture;
    private Sound dropSound;
    private Music music;
    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private Vector2 touchPos;
    private Player player;
    private Array<Enemy> enemies;
    private Array<FastDiagonalEnemy> fastEnemiesToSpawn;
    private BitmapFont font;
    private int score = 0;

    private final com.badlogic.gdx.audio.Sound deathSound;
    private final com.badlogic.gdx.audio.Sound clickSound;

    private float fastEnemySpawnTimer = 0f;
    private boolean fastEnemiesSpawned = false;

    private Stage hudStage;        // Para botones táctiles como pausa
    private Stage pauseStage;      // Para el menú de pausa
    private Skin skin;
    private boolean paused = false;


    public GameScreen(MainGame game) {
        this.game = game;
        this.spriteBatch = game.batch;

        deathSound = Gdx.audio.newSound(Gdx.files.internal("muerte.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("ok.wav"));
    }

    @Override
    public void show() {
        backgroundTexture = new Texture("background.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));

        game.playMusic("music.wav");

        viewport = new FitViewport(800, 480);

        touchPos = new Vector2();

        player = new Player(400, 50); // posición en píxeles

        enemies = new Array<>();
        fastEnemiesToSpawn = new Array<>();

        // Enemigos normales se añaden inmediatamente
        for (int i = 0; i < 5; i++) {
            enemies.add(new Enemy(100 + i * 120, 400, viewport.getWorldWidth()));
            fastEnemiesToSpawn.add(new FastDiagonalEnemy(100 + i * 120, 400, viewport.getWorldWidth()));
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Chonky Bunny.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 64;
        param.color = Color.WHITE;
        font = generator.generateFont(param);
        generator.dispose();

        // Cargar skin básica
        skin = new Skin();
        BitmapFont hudFont = new BitmapFont(); // Fuente simple para móviles
        skin.add("default-font", hudFont);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = hudFont;
        btnStyle.fontColor = Color.WHITE;
        skin.add("default", btnStyle);

// HUD Stage para botón de pausa
        hudStage = new Stage(viewport, spriteBatch);
        Gdx.input.setInputProcessor(hudStage);

// Botón de pausa (puede ser texto o ícono si tienes uno)
        TextButton pauseBtn = new TextButton("||", skin);
        pauseBtn.setSize(60, 60);
        pauseBtn.setPosition(viewport.getWorldWidth() - pauseBtn.getWidth() - 20, viewport.getWorldHeight() - pauseBtn.getHeight() - 20);

        hudStage.addActor(pauseBtn);

        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(game.getSfxVolume());
                paused = true;
                Gdx.input.setInputProcessor(pauseStage);
            }
        });

// Stage del menú de pausa
        pauseStage = new Stage(viewport, spriteBatch);

// Botón reanudar
        TextButton resumeBtn = new TextButton("Reanudar", skin);
        resumeBtn.setSize(300, 80);
        resumeBtn.setPosition((viewport.getWorldWidth() - resumeBtn.getWidth()) / 2, 260);

// Botón volver al menú
        TextButton exitBtn = new TextButton("Volver al menú", skin);
        exitBtn.setSize(300, 80);
        exitBtn.setPosition((viewport.getWorldWidth() - exitBtn.getWidth()) / 2, 160);

        pauseStage.addActor(resumeBtn);
        pauseStage.addActor(exitBtn);

// Listeners del menú
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                clickSound.play(game.getSfxVolume());
                Gdx.input.setInputProcessor(hudStage);
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(game.getSfxVolume());
                game.setScreen(new StartScreen(game));
            }
        });


        //Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (!paused) {
            input(delta);
            logic(delta);
        }

        draw();

        if (paused) {
            pauseStage.act(delta);
            pauseStage.draw();
        } else {
            hudStage.act(delta);
            hudStage.draw();
        }
    }


    private void input(float delta) {
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            float screenMiddle = viewport.getWorldWidth() / 2f;

            if (touchPos.y < 150) {
                if (touchPos.x < screenMiddle) {
                    player.moveLeft(delta);
                } else {
                    player.moveRight(delta);
                }
            }

            if (touchPos.y >= 150) {
                player.shoot(game);
            }
        } else {
            player.setIdle();
        }
    }

    private void logic(float delta) {
        player.update(delta);

        // Controlar spawn diferido de fastEnemies
        if (!fastEnemiesSpawned) {
            fastEnemySpawnTimer += delta;
            if (fastEnemySpawnTimer >= 2f) {
                enemies.addAll(fastEnemiesToSpawn);
                fastEnemiesToSpawn.clear();
                fastEnemiesSpawned = true;
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.shoot();

            for (int i = enemy.getBullets().size - 1; i >= 0; i--) {
                if (enemy.getBullets().get(i).getBoundingRectangle().overlaps(player.getBounds())) {
                    enemy.getBullets().removeIndex(i);

                    deathSound.play(game.getSfxVolume());

                    player.removeLive(score);
                    break;
                }
            }
        }

        for (int i = player.getBullets().size - 1; i >= 0; i--) {
            for (int j = enemies.size - 1; j >= 0; j--) {
                if (player.getBullets().get(i).getBoundingRectangle().overlaps(enemies.get(j).getBounds())) {
                    player.getBullets().removeIndex(i);
                    enemies.removeIndex(j);
                    score += 100;

                    if(score == 1000){
                        game.stopMusic();
                        game.setScreen(new VictoryScreen(game, score + (player.getLives() * 200)));
                    }

                    break;
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

        String scoreText = "Score: " + score;
        String livesText = "Lives: " + player.getLives();

        float marginLeft = 10f;
        float marginTop = 20f;
        float lineSpacing = 60f;

        // La coordenada Y para dibujar texto es la línea base, por eso restamos marginTop desde el viewport height
        font.draw(spriteBatch, scoreText, marginLeft, viewport.getWorldHeight() - marginTop);
        font.draw(spriteBatch, livesText, marginLeft, viewport.getWorldHeight() - marginTop - lineSpacing);

        spriteBatch.end();
    }


    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        backgroundTexture.dispose();
        dropSound.dispose();
        music.dispose();
        font.dispose();
        pauseStage.dispose();
        hudStage.dispose();
        skin.dispose();

    }
}
