package io.github.some_example_name;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public class MainGame extends Game {
    public SpriteBatch batch;
    private Music music;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new StartScreen(this));
    }
    public void playMusic(String filePath) {
        if (music != null) {
            music.stop();
            music.dispose();
        }
        music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
        }
    }
    private float musicVolume = 0.5f;
    private float sfxVolume = 0.5f;

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (music != null) {
            music.setVolume(volume);
        }
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float volume) {
        sfxVolume = volume;
        // Aplica manualmente al reproducir SFX
    }
}
