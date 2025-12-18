package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public class ZigzagEnemy extends Enemy {
    private float horizontalSpeed = 3.0f; // Kecepatan gerak kiri-kanan
    private boolean movingRight = true;

    public ZigzagEnemy(Texture texture, float x, float y) {
        super(texture, x, y, 7.0f); // Kecepatan turun sedang (7.0f)
    }

    @Override
    public void update(float delta) {
        super.update(delta); // Panggil logika turun dari induk (Enemy)

        // --- LOGIKA GERAK UJUNG KE UJUNG (ZIGZAG) ---
        if (movingRight) {
            setX(getX() + horizontalSpeed * delta);
            if (getX() > 9.0f - getWidth()) { // Mentok Kanan
                movingRight = false;
            }
        } else {
            setX(getX() - horizontalSpeed * delta);
            if (getX() < 1.0f) { // Mentok Kiri
                movingRight = true;
            }
        }
    }
}
