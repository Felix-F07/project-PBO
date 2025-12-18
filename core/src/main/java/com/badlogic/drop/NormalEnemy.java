package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public class NormalEnemy extends Enemy {
    public NormalEnemy(Texture texture, float x, float y) {
        super(texture, x, y, 10.0f); // Speed Normal = 10
    }
}
