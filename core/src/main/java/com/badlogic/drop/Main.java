package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    // BitmapFont font;  <-- HAPUS INI (Tidak terpakai)

    @Override
    public void create() {
        batch = new SpriteBatch();
        // font = new BitmapFont(); <-- HAPUS INI
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // font.dispose(); <-- HAPUS INI
    }
}
