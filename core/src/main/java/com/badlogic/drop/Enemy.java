package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends GameObject {
    private float speed;

    public Enemy(Texture texture, float x, float y, float speed) {
        super(texture, x, y, 1.2f, 2.0f);
        this.speed = speed;
    }

    @Override
    public void update(float delta) {
        setY(getY() - speed * delta);
    }

    public boolean isOffScreen() {
        return getY() + getHeight() < 0;
    }
}
