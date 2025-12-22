package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public class Bullet extends GameObject {
    private float speed = 15.0f; // Kecepatan peluru terbang ke atas

    public Bullet(Texture texture, float x, float y) {
        // Ukuran peluru: Lebar 0.3, Tinggi 0.6
        super(texture, x, y, 0.3f, 0.6f);
    }

    @Override
    public void update(float delta) {
        // Peluru bergerak naik (Y bertambah)
        setY(getY() + speed * delta);
    }

    public boolean isOffScreen() {
        // Cek apakah peluru sudah lewat layar atas (16f adalah tinggi dunia)
        return getY() > 16f;
    }
}
