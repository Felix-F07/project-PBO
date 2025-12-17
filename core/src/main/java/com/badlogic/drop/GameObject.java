package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// [Syarat 4 & 9] Abstract Class: Tidak bisa dibuat langsung, harus diturunkan
public abstract class GameObject {
    // [Syarat 1] Enkapsulasi: Protected agar bisa diakses anak, tapi tidak oleh publik sembarangan
    protected Rectangle bounds;
    protected Texture texture;

    public GameObject(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
    }

    // Method abstract yang WAJIB di-override oleh anak-anaknya (Polimorfisme)
    public abstract void update(float delta);

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // [Syarat 1] Getter untuk akses aman
    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        // Texture jangan didispose di sini jika dipakai ramai-ramai (shared),
        // tapi untuk struktur PBO sederhana, ini metode standar.
    }
}
