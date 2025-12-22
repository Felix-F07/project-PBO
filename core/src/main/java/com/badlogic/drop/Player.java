package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Player extends GameObject {
    private float steerSpeed = 6.0f;
    private float minX = 1.0f;
    private float maxX = 9.0f;

    // [BARU] Variabel untuk menyimpan sudut kemiringan mobil
    private float rotation = 0;

    public Player(Texture texture) {
        super(texture, 4.4f, 2.0f, 1.2f, 2.0f);
    }

    @Override
    public void update(float delta) {
        boolean isTurning = false;

        // Logika Input & Gerak
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            setX(getX() - steerSpeed * delta);
            // [BARU] Miring ke Kiri (Positif)
            rotation = 15.0f;
            isTurning = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setX(getX() + steerSpeed * delta);
            // [BARU] Miring ke Kanan (Negatif)
            rotation = -15.0f;
            isTurning = true;
        }

        // Logika "Smoothing" (Biar baliknya halus, tidak patah-patah)
        if (!isTurning) {
            // Mengembalikan rotasi ke 0 secara perlahan (Lerp)
            rotation = MathUtils.lerp(rotation, 0, 10f * delta);
        }

        // Batasi posisi agar tidak keluar jalan
        float clampedX = MathUtils.clamp(getX(), minX, maxX - getBounds().width);
        setX(clampedX);
    }

    //  Kita Override method draw bawaan GameObject
    // Supaya kita bisa gambar pakai parameter ROTASI
    @Override
    public void draw(SpriteBatch batch) {
        // Fungsi draw yang panjang ini memungkinkan kita mengatur titik putar (origin) dan rotasi
        batch.draw(
            getTexture(),       // Gambar
            getX(), getY(),     // Posisi X, Y
            getWidth() / 2, getHeight() / 2, // Titik Putar (Origin) di tengah gambar
            getWidth(), getHeight(), // Lebar & Tinggi
            1, 1,               // Skala (tidak diperbesar/diperkecil)
            rotation,           // SUDUT PUTAR (Ini kuncinya!)
            0, 0,               // Source X, Y (potongan gambar)
            getTexture().getWidth(), getTexture().getHeight(), // Ukuran asli texture
            false, false        // Flip (tidak dibalik)
        );
    }
}
