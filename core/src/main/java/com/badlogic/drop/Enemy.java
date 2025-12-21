package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends GameObject {
    private float speed;
    public int health; // [BARU] Menambahkan nyawa

    public Enemy(Texture texture, float x, float y, float speed) {
        super(texture, x, y, 1.2f, 2.0f);
        this.speed = speed;
        this.health = 1; // Default nyawa musuh adalah 1
    }

    @Override
    public void update(float delta) {
        setY(getY() - speed * delta);
    }

    public boolean isOffScreen() {
        return getY() + getHeight() < 0;
    }

    // [BARU] Fungsi untuk menerima damage
    public void takeDamage() {
        health--;
    }
}
