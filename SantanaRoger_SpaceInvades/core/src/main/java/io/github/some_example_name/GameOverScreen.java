package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {
    private final MainGame game;
    private final SpriteBatch batch;
    private final Texture background;
    private final BitmapFont font, buttonFont;
    private final GlyphLayout layout;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final Stage stage;
    private final Skin skin;
    private final TextButton retryButton, exitButton;
    private final com.badlogic.gdx.audio.Sound clickSound;


    private int score;

    public GameOverScreen(MainGame game, int score) {
        this.game = game;
        this.score = score;  // Guardamos la puntuación recibida
        this.batch = game.batch;

        // Fuente personalizada
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Chonky Bunny.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 48;
        font = generator.generateFont(titleParam);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = 24;
        buttonFont = generator.generateFont(buttonParam);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator.dispose();

        this.background = new Texture("sdm.png");
        this.layout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // Crear skin y estilo
        skin = new Skin();
        skin.add("default-font", buttonFont);
        Texture buttonTexture = new Texture(Gdx.files.internal("button1.png"));
        NinePatch ninePatch = new NinePatch(buttonTexture, 24, 24, 24, 24);
        skin.add("button", ninePatch);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = skin.newDrawable("button", Color.WHITE);
        buttonStyle.down = skin.newDrawable("button", Color.GRAY);
        skin.add("default", buttonStyle);

        retryButton = new TextButton("RETRY", skin);
        retryButton.setSize(200, 50);
        retryButton.setPosition((viewport.getWorldWidth() - retryButton.getWidth()) / 2, 140);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(game.getSfxVolume());
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton = new TextButton("EXIT", skin);
        exitButton.setSize(200, 50);
        exitButton.setPosition((viewport.getWorldWidth() - exitButton.getWidth()) / 2, 60);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(game.getSfxVolume());
                Gdx.app.exit();
            }
        });

        stage.addActor(retryButton);
        stage.addActor(exitButton);

        game.playMusic("gameover.mp3");
        clickSound = Gdx.audio.newSound(Gdx.files.internal("ok.wav"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        layout.setText(font, "GAME OVER");
        float x = (viewport.getWorldWidth() - layout.width) / 2;
        float y = viewport.getWorldHeight() - 80;
        font.draw(batch, layout, x, y);

        // Mostrar puntuación debajo del título
        String scoreText = "Puntuación final: " + score;
        layout.setText(font, scoreText);
        float scoreX = (viewport.getWorldWidth() - layout.width) / 2;
        float scoreY = y - 50;  // 50 unidades más abajo que el título
        font.draw(batch, layout, scoreX, scoreY);

        batch.end();

        stage.act(delta);
        stage.draw();
    }


    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        font.dispose();
        buttonFont.dispose();
        background.dispose();
        stage.dispose();
        skin.dispose();
    }
}
