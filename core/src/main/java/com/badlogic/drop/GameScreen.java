package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList; // [Syarat 5 & 12] Pakai Java Collections
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    final Main game;

    private OrthographicCamera camera;
    private Viewport viewport;

    // ASET TEXTURE (Diload sekali di sini, dipinjamkan ke object)
    private Texture roadTexture, grassTexture;
    private Texture playerTx, normalTx1, normalTx2, normalTx3, fastTx;

    private Player player; // [Syarat 2] Object Player

    // [Syarat 3 & 10] Polimorfisme: List menampung Induk (Enemy), tapi isinya bisa Normal/Fast
    private List<Enemy> enemies;

    private long lastSpawnTime;
    private float backgroundOffset = 0;

    // UI
    private Stage uiStage;
    private Label scoreLabel;
    private int score;

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(10f, 16f, camera);
        camera.position.set(5f, 8f, 0);

        // 1. Load Aset
        roadTexture = new Texture(Gdx.files.internal("road_texture.png"));
        grassTexture = new Texture(Gdx.files.internal("grass_texture.png"));
        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        playerTx = new Texture(Gdx.files.internal("MC.png"));
        normalTx1 = new Texture(Gdx.files.internal("truck_cream.png"));
        normalTx2 = new Texture(Gdx.files.internal("truck_green.png"));
        normalTx3 = new Texture(Gdx.files.internal("truck_blue.png"));
        fastTx = new Texture(Gdx.files.internal("truck_red.png")); // Si Merah

        // 2. Inisialisasi Object
        player = new Player(playerTx);
        enemies = new ArrayList<>(); // Koleksi Objek

        // 3. Setup UI
        uiStage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 50);
        uiStage.addActor(scoreLabel);

        spawnEnemy();
    }

    private void spawnEnemy() {
        float[] lanes = {1.35f, 3.35f, 5.35f, 7.35f};
        float randomX = lanes[MathUtils.random(0, lanes.length - 1)];
        float spawnY = 16f;

        // [Syarat 3] Random Logic: 20% Peluang muncul FastEnemy, 80% NormalEnemy
        Enemy newEnemy;
        if (MathUtils.randomBoolean(0.2f)) {
            // Munculkan Musuh Cepat (Merah)
            newEnemy = new FastEnemy(fastTx, randomX, spawnY);
        } else {
            // Munculkan Musuh Normal (Acak gambar)
            Texture randomTx;
            int r = MathUtils.random(0, 2);
            if(r==0) randomTx = normalTx1;
            else if(r==1) randomTx = normalTx2;
            else randomTx = normalTx3;

            newEnemy = new NormalEnemy(randomTx, randomX, spawnY);
        }

        enemies.add(newEnemy);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // --- UPDATE LOGIC ---
        backgroundOffset += delta * 10.0f;

        player.update(delta); // Enkapsulasi logika player

        // Spawn musuh
        if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000) {
            spawnEnemy();
        }

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();

            enemy.update(delta); // [PENTING] Polimorfisme: FastEnemy akan gerak cepat, Normal gerak biasa

            if (enemy.isOffScreen()) {
                iter.remove();
                score++;
                scoreLabel.setText("Score: " + score);
            }

            if (enemy.getBounds().overlaps(player.getBounds())) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }

        uiStage.act();

        // --- DRAWING ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Draw Background
        float vStart = -backgroundOffset / 10f;
        float vEnd = vStart + (16f / 10f);
        game.batch.draw(grassTexture, 0, 0, 1f, 16f, 0, vEnd, 1, vStart);
        game.batch.draw(roadTexture, 1f, 0, 8f, 16f, 0, vEnd, 1, vStart);
        game.batch.draw(grassTexture, 9f, 0, 1f, 16f, 0, vEnd, 1, vStart);

        // Draw Objects
        for (Enemy enemy : enemies) {
            enemy.draw(game.batch);
        }
        player.draw(game.batch);

        game.batch.end();

        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        roadTexture.dispose(); grassTexture.dispose();
        playerTx.dispose();
        normalTx1.dispose(); normalTx2.dispose(); normalTx3.dispose(); fastTx.dispose();
        uiStage.dispose();
    }
}
