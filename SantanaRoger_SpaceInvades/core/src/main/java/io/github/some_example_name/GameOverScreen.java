package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;

    public GameOverScreen(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // Asegúrate de tener uiskin.atlas y uiskin.json

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton retryButton = new TextButton("Reintentar", skin);
        TextButton exitButton = new TextButton("Salir", skin);

        retryButton.addListener(event -> {
            if (retryButton.isPressed()) {
                game.setScreen(new GameScreen(game)); // Crea una nueva partida
                return true;
            }
            return false;
        });

        exitButton.addListener(event -> {
            if (exitButton.isPressed()) {
                Gdx.app.exit(); // Cierra la app
                return true;
            }
            return false;
        });

        table.add("¡Has perdido!").padBottom(20).row();
        table.add(retryButton).width(200).padBottom(10).row();
        table.add(exitButton).width(200);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
