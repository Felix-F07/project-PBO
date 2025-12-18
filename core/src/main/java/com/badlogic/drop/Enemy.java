package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends GameObject {
    protected float speed;

    public Enemy(Texture texture, float x, float y, float speed) {
        super(texture, x, y, 1.2f, 2.0f); // Ukuran musuh selalu 1.2 x 2.0
        this.speed = speed;
    }

    @Override
    public void update(float delta) {
        // Logika dasar musuh: gerak ke bawah
        bounds.y -= speed * delta;
    }

    public boolean isOffScreen() {
        return bounds.y + bounds.height < 0;
    }
}
