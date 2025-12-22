package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    final Main game;
    private Stage stage;
    private Skin skin;
    private int finalScore;
    private Texture quitTexture;

    public GameOverScreen(final Main game, int score) {
        this.game = game;
        this.finalScore = score;

        // Menggunakan ScreenViewport agar UI tajam
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 1. PERSIAPAN TOMBOL QUIT
        quitTexture = new Texture(Gdx.files.internal("Quit Button.png"));
        TextureRegionDrawable quitDrawable = new TextureRegionDrawable(new TextureRegion(quitTexture));
        ImageButton quitButton = new ImageButton(quitDrawable);

        //  2. MEMBUAT LABEL TEKS BESAR
        Label titleLabel = new Label("GAME OVER", skin);
        // Perbesar skala font judul (Coba 6.0f biar mantap)
        titleLabel.setFontScale(6.0f);

        Label scoreLabel = new Label("Final Score: " + finalScore, skin);
        // Perbesar skala font skor
        scoreLabel.setFontScale(4.0f);

        // 3. MENYUSUN POSISI (TABLE)
        Table table = new Table();
        table.setFillParent(true);

        // Baris 1: Tulisan GAME OVER
        table.add(titleLabel).padBottom(50); // Tambah jarak bawah biar ga nempel skor
        table.row();

        // Baris 2: Tulisan Skor
        table.add(scoreLabel).padBottom(80); // Jarak antara skor dan tombol
        table.row();

        // Baris 3: Tombol Quit
        // UKURAN BARU: Lebih Lebar dan Besar (450 x 120)
        // Rasio ini biasanya aman untuk tombol panjang agar tidak terlihat gepeng
        table.add(quitButton).size(450, 120);

        stage.addActor(table);

        // 4. LOGIKA TOMBOL
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Warna Background Merah Gelap
        Gdx.gl.glClearColor(0.3f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        quitTexture.dispose();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
