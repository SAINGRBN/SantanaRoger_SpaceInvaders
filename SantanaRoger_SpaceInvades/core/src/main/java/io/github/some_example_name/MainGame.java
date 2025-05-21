package io.github.some_example_name;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new StartScreen(this)); // Empezar desde la pantalla de inicio
    }
}
