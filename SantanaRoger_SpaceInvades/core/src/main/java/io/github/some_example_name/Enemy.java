package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    protected Animation<TextureRegion> idleAnimation;
    protected float stateTime = 0f;
    protected float x, y;
    protected float worldWidth;
    protected float speedX = 100f;       // Velocidad en unidades de mundo por segundo (pixeles lógicos)
    protected boolean movingRight = true;
    protected float stepDown = 30f;      // Bajar 30 unidades en Y cuando cambia de lado

    protected Texture bulletTexture;
    protected Array<Sprite> bullets;
    protected float shootCooldown = 2f;
    protected float shootTimer = 0f;

    protected static final float WIDTH = 50f;   // Tamaño ancho del enemigo en unidades de mundo (pixeles)
    protected static final float HEIGHT = 50f;  // Tamaño alto

    public Enemy(float startX, float startY, float worldWidth) {
        this.x = startX;
        this.y = startY;
        this.worldWidth = worldWidth;

        bullets = new Array<>();
        bulletTexture = new Texture("bullet_1.png");

        // Cargar animación idle
        TextureRegion[] idleFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            idleFrames[i] = new TextureRegion(new Texture("idle_" + i + ".png"));
        }

        idleAnimation = new Animation<TextureRegion>(0.2f, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;
        shootTimer += delta;

        // Movimiento lateral
        float move = speedX * delta * (movingRight ? 1 : -1);
        x += move;

        if ((movingRight && x > worldWidth - WIDTH) || (!movingRight && x < 0)) {
            movingRight = !movingRight;
            y -= stepDown;
        }

        // Actualizar balas
        for (int i = bullets.size - 1; i >= 0; i--) {
            Sprite bullet = bullets.get(i);
            bullet.translateY(-200f * delta);  // Velocidad bala ajustada a unidades mundo
            if (bullet.getY() < 0) bullets.removeIndex(i);
        }
    }

    public void draw(Batch batch) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, x, y, WIDTH, HEIGHT);  // Usar tamaño en unidades mundo

        for (Sprite bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public void shoot() {
        if (shootTimer >= shootCooldown) {
            Sprite bullet = new Sprite(bulletTexture);
            bullet.setSize(10f, 25f);   // Tamaño bala en unidades mundo
            bullet.setPosition(x + WIDTH / 2 - 5f, y);  // Centrar bala en enemigo
            bullets.add(bullet);
            shootTimer = 0f;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public Array<Sprite> getBullets() {
        return bullets;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
