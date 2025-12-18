package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Player extends GameObject {
    private float steerSpeed = 6.0f;
    private float minX = 1.0f; // Batas kiri jalan
    private float maxX = 9.0f; // Batas kanan jalan

    public Player(Texture texture) {
        super(texture, 4.4f, 2.0f, 1.2f, 2.0f);
    }

    @Override
    public void update(float delta) {
        // Logika Input dipindah ke sini (Separation of Concerns)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            setX(getX() - steerSpeed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setX(getX() + steerSpeed * delta);
        }

        // Batasi posisi agar tidak keluar jalan
        // Kita akses getBounds() karena bounds bersifat private
        float clampedX = MathUtils.clamp(getX(), minX, maxX - getBounds().width);
        setX(clampedX);
    }
}
