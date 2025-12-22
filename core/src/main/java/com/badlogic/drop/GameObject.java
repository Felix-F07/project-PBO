package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    // [Syarat 1 & 7] Enkapsulasi: Wajib private
    private Rectangle bounds;
    private Texture texture;

    public GameObject(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
    }


    public abstract void update(float delta);

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // Getter untuk akses dari luar (karena variabel private)
    public Rectangle getBounds() {
        return bounds;
    }

    // [BARU] Kita butuh ini supaya Player bisa ambil gambarnya untuk di-rotasi
    public Texture getTexture() {
        return texture;
    }
    public float getX() {
        return bounds.x;
    }
    public float getY() {
        return bounds.y;
    }
    public float getWidth() {
        return bounds.width;
    }
    public float getHeight() {
        return bounds.height;
    }
    public void setX(float x) {
        bounds.x = x;
    }
    public void setY(float y) {
        bounds.y = y;
    }
}
