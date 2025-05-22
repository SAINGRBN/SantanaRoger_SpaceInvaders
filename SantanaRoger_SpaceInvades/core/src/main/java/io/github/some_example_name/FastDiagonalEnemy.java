    package io.github.some_example_name;

    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.graphics.g2d.Sprite;
    import com.badlogic.gdx.math.Vector2;

    public class FastDiagonalEnemy extends Enemy {

        public FastDiagonalEnemy(float startX, float startY, float worldWidth) {
            super(startX, startY, worldWidth);
            // Aumentar la velocidad lateral
            this.speedX = 200f;  // el doble que el original (100f)
            // Puedes ajustar stepDown si quieres
        }

        @Override
        public void update(float delta) {
            super.update(delta);

            // Actualizar movimiento de las balas en diagonal:
            for (int i = getBullets().size - 1; i >= 0; i--) {
                Sprite bullet = getBullets().get(i);
                // Mover bala hacia abajo y a la derecha
                bullet.translateX(100f * delta);   // velocidad diagonal en X
                bullet.translateY(-200f * delta);  // velocidad diagonal en Y (hacia abajo)

                // Eliminar si la bala sale de pantalla (x > worldWidth o y < 0)
                if (bullet.getY() < 0 || bullet.getX() > worldWidth) {
                    getBullets().removeIndex(i);
                }
            }
        }

        @Override
        public void shoot() {
            if (shootTimer >= shootCooldown) {
                Sprite bullet = new Sprite(bulletTexture);
                bullet.setSize(10f, 25f);
                bullet.setPosition(getX() + WIDTH / 2 - 5f, getY());
                getBullets().add(bullet);
                shootTimer = 0f;
            }
        }
        @Override
        public void draw(Batch batch) {
            batch.setColor(Color.RED);            // Pinta todo de rojo (el enemigo y sus balas)
            super.draw(batch);                    // Llama a la lógica de dibujo de Enemy
            batch.setColor(Color.WHITE);          // Resetea el color para que no afecte a otros dibujos
        }

    }
