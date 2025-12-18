package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Player extends GameObject {
    private float steerSpeed = 6.0f;

    public Player(Texture texture) {
        // Panggil konstruktor induk (GameObject)
        // Posisi awal di tengah (5.0 - 0.6) = 4.4, Y = 2.0
        super(texture, 4.4f, 2.0f, 1.2f, 2.0f);
    }

    @Override
    public void update(float delta) {
        // Logika input khusus Player
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bounds.x -= steerSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bounds.x += steerSpeed * delta;
        }

        // Batasi agar tidak keluar jalan (1.0 sampai 9.0)
        bounds.x = MathUtils.clamp(bounds.x, 1.0f, 9.0f - bounds.width);
    }
}
