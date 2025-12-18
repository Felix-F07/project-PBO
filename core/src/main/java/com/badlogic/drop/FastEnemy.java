package com.badlogic.drop;
import com.badlogic.gdx.graphics.Texture;

public class FastEnemy extends Enemy {
    public FastEnemy(Texture texture, float x, float y) {
        super(texture, x, y, 20.0f); // Kecepatan tinggi (Ngebut)
    }
}
