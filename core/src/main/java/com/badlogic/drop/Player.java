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


    private float rotation = 0;

    public Player(Texture texture) {
        super(texture, 4.4f, 2.0f, 1.2f, 2.0f);
    }

    @Override
    public void update(float delta) {
        boolean isTurning = false;


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            setX(getX() - steerSpeed * delta);


            rotation = 15.0f;
            isTurning = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setX(getX() + steerSpeed * delta);


            rotation = -15.0f;
            isTurning = true;
        }

        if (!isTurning) {

            rotation = MathUtils.lerp(rotation, 0, 10f * delta);
        }


        float clampedX = MathUtils.clamp(getX(), minX, maxX - getBounds().width);
        setX(clampedX);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(
            getTexture(),
            getX(), getY(),
            getWidth() / 2, getHeight() / 2,
            getWidth(), getHeight(),
            1, 1,
            rotation,
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, false
        );
    }
}
