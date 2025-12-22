package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final Main game;
    private Stage stage;
    private Skin skin;

    // HAPUS optionsTexture dari sini
    private Texture playTexture, quitTexture;

    public MainMenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 1. PERSIAPAN GAMBAR TOMBOL

        // Memuat gambar (HAPUS bagian Options Button.png)
        playTexture = new Texture(Gdx.files.internal("Play Button.png"));
        quitTexture = new Texture(Gdx.files.internal("Quit Button.png"));

        // Mengubah Texture menjadi Drawable
        TextureRegionDrawable playDrawable = new TextureRegionDrawable(new TextureRegion(playTexture));
        TextureRegionDrawable quitDrawable = new TextureRegionDrawable(new TextureRegion(quitTexture));

        // Membuat ImageButton
        ImageButton playButton = new ImageButton(playDrawable);
        ImageButton quitButton = new ImageButton(quitDrawable);


        // 2. FUNGSI TOMBOL UTAMA

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        // (HAPUS Listener optionsButton)

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // 3. MENYUSUN TAMPILAN (TABLE)

        Table table = new Table();
        table.setFillParent(true);

        // Masukkan tombol ke tabel (HAPUS bagian optionsButton)
        table.add(playButton).pad(20); // Tambah padding biar jaraknya enak
        table.row();
        table.add(quitButton).pad(20);

        // Masukkan Tabel ke Stage
        stage.addActor(table);
        // (HAPUS penambahan optionsWindow ke stage)
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Warna background sedikit diubah biar segar (opsional)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        // HAPUS optionsTexture.dispose() agar tidak error
        playTexture.dispose();
        quitTexture.dispose();
    }
}
