package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;


public class StartScreen implements Screen {

    private final MainGame game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final Stage stage;
    private final Skin skin;

    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout;

    private final Texture background;

    private final TextButton startButton;
    private final TextButton exitButton;
    private final com.badlogic.gdx.audio.Sound clickSound;


    public StartScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);

        stage = new Stage(viewport);

        // Crear fuentes con FreeType
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Chonky Bunny.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 48;
        titleFont = generator.generateFont(titleParam);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = 24;
        buttonFont = generator.generateFont(buttonParam);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator.dispose();

        // Cargar fondo (cambia la ruta o pon uno que tengas)
        background = new Texture("sdm.png");

        layout = new GlyphLayout();

        // Crear skin manual con NinePatch
        skin = new Skin();
        skin.add("default-font", buttonFont);

        Texture buttonTex = new Texture(Gdx.files.internal("button1.png"));
        NinePatch ninePatch = new NinePatch(buttonTex, 24, 24, 24, 24);
        skin.add("button", ninePatch);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new NinePatchDrawable(ninePatch);
        buttonStyle.down = new NinePatchDrawable(new NinePatch(buttonTex, 24, 24, 24, 24));
        skin.add("default", buttonStyle);

        // Crear botones
        startButton = new TextButton("START", skin);
        startButton.setSize(300, 60);
        startButton.setPosition((viewport.getWorldWidth() - startButton.getWidth()) / 2, 180);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton = new TextButton("EXIT", skin);
        exitButton.setSize(300, 60);
        exitButton.setPosition((viewport.getWorldWidth() - exitButton.getWidth()) / 2, 100);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(game.getSfxVolume());
                Gdx.app.exit();
            }
        });



// Cargar texturas para los sliders
        Texture sliderBg = new Texture("slider_bg.png");     // imagen alargada, por ejemplo 200x10
        Texture sliderKnob = new Texture("slider_knob.png"); // imagen pequeña, por ejemplo 20x40

        NinePatch sliderPatch = new NinePatch(sliderBg, 4, 4, 4, 4);
        skin.add("slider-bg", sliderPatch);
        skin.add("slider-knob", sliderKnob);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.background = new NinePatchDrawable(sliderPatch);
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnob));
        skin.add("default-horizontal", sliderStyle);

// Slider de volumen de música
        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(game.getMusicVolume()); // Asume que tienes este método
        musicSlider.setSize(300, 40);
        musicSlider.setPosition((viewport.getWorldWidth() - musicSlider.getWidth()) / 2, 280);
        musicSlider.addListener(event -> {
            game.setMusicVolume(musicSlider.getValue());
            return false;
        });



// Slider de volumen de SFX
        Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxSlider.setValue(game.getSfxVolume()); // Asume que tienes este método
        sfxSlider.setSize(300, 40);
        sfxSlider.setPosition((viewport.getWorldWidth() - sfxSlider.getWidth()) / 2, 230);
        sfxSlider.addListener(event -> {
            game.setSfxVolume(sfxSlider.getValue());
            return false;
        });

        stage.addActor(musicSlider);
        stage.addActor(sfxSlider);

        float padding = 20f;
        float sliderSfxY = 230;
        float startButtonY = sliderSfxY - padding - startButton.getHeight();
        startButton.setPosition((viewport.getWorldWidth() - startButton.getWidth()) / 2, startButtonY);

        float exitButtonY = startButtonY - padding - exitButton.getHeight();
        exitButton.setPosition((viewport.getWorldWidth() - exitButton.getWidth()) / 2, exitButtonY);

        stage.addActor(startButton);
        stage.addActor(exitButton);


        game.playMusic("menu.ogg");
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

        // Dibuja fondo
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Dibuja texto título centrado arriba
        String title = "Mansion Invaders";
        layout.setText(titleFont, title);
        float x = (viewport.getWorldWidth() - layout.width) / 2;
        float y = viewport.getWorldHeight() - 60;
        titleFont.draw(batch, layout, x, y);

        // Texto para sliders
        String musicLabel = "Volumen Música";
        layout.setText(buttonFont, musicLabel);
        float musicX = (viewport.getWorldWidth() - layout.width) / 2;
        float musicY = 280 + 40 + 10;  // slider pos Y + slider altura + margen

        buttonFont.draw(batch, layout, musicX, musicY);

        String sfxLabel = "Volumen Efectos";
        layout.setText(buttonFont, sfxLabel);
        float sfxX = (viewport.getWorldWidth() - layout.width) / 2;
        float sfxY = 230 + 40 + 10;  // slider pos Y + slider altura + margen

        buttonFont.draw(batch, layout, sfxX, sfxY);

        batch.end();

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        background.dispose();
    }
}
