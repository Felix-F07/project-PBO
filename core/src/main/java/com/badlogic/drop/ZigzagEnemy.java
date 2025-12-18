package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;

public class ZigzagEnemy extends Enemy {
    private float horizontalSpeed = 3.0f;
    private boolean movingRight = true;

    public ZigzagEnemy(Texture texture, float x, float y) {
        super(texture, x, y, 7.0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (movingRight) {
            setX(getX() + horizontalSpeed * delta);
            if (getX() > 9.0f - getWidth()) {
                movingRight = false;
            }
        } else {
            setX(getX() - horizontalSpeed * delta);
            if (getX() < 1.0f) {
                movingRight = true;
            }
        }
    }
}
