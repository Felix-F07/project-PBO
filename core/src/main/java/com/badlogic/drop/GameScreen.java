package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Iterator;

public class GameScreen implements Screen {
    final Main game;

    // --- KAMERA & LAYAR ---
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float WORLD_WIDTH = 10f;
    private final float WORLD_HEIGHT = 16f;

    // --- ASET GAMBAR ---
    private Texture roadTexture;
    private Texture grassTexture;
    private Texture playerTexture;

    // Array untuk menyimpan banyak jenis gambar musuh
    private Array<Texture> enemyTextures;

    // --- BACKGROUND SCROLLING ---
    private float backgroundOffset = 0;
    private final float SCROLL_SPEED = 10.0f;

    // --- PENGATURAN AREA JALAN ---
    private final float GRASS_WIDTH = 1f;
    private final float ROAD_WIDTH = WORLD_WIDTH - (GRASS_WIDTH * 2);
    private final float ROAD_START_X = GRASS_WIDTH;
    private final float ROAD_END_X = ROAD_START_X + ROAD_WIDTH;

    // --- LOGIKA PEMAIN ---
    private float playerWidth = 1.2f;
    private float playerHeight = 2.0f;
    private float playerY = 2f;
    private float currentPlayerX;
    private final float STEER_SPEED = 6.0f;

    private Rectangle playerRect;

    // --- LOGIKA MUSUH ---
    private Array<EnemyData> enemies;
    private long lastEnemySpawnTime;
    private float enemySpeed = 10.0f; // Kecepatan dasar musuh

    // --- UI & SKOR ---
    private Stage uiStage;
    private Label scoreLabel;
    private int score;

    // --- DEBUG ---
    private ShapeRenderer debugRenderer;
    private boolean debugMode = false;

    // Class kecil data musuh
    private class EnemyData {
        Rectangle rect;
        int textureIndex;
        float stateTime;
        float speedX; // [BARU] Kecepatan gerak kiri/kanan
    }

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // 1. LOAD ASET
        roadTexture = new Texture(Gdx.files.internal("road_texture.png"));
        grassTexture = new Texture(Gdx.files.internal("grass_texture.png"));
        playerTexture = new Texture(Gdx.files.internal("MC.png"));

        // 2. LOAD VARIASI MUSUH
        enemyTextures = new Array<>();
        // Index 0: Cream (Zig-Zag)
        enemyTextures.add(new Texture(Gdx.files.internal("truck_cream.png")));
        // Index 1: Green (Normal)
        enemyTextures.add(new Texture(Gdx.files.internal("truck_green.png")));
        // Index 2: Blue (Normal)
        enemyTextures.add(new Texture(Gdx.files.internal("truck_blue.png")));
        // Index 3: Red (Ngebut)
        enemyTextures.add(new Texture(Gdx.files.internal("truck_red.png")));

        if (Gdx.files.internal("sedan_gray.png").exists())
            enemyTextures.add(new Texture(Gdx.files.internal("sedan_gray.png")));
        if (Gdx.files.internal("sedan_blue.png").exists())
            enemyTextures.add(new Texture(Gdx.files.internal("sedan_blue.png")));
        if (Gdx.files.internal("compact_blue.png").exists())
            enemyTextures.add(new Texture(Gdx.files.internal("compact_blue.png")));

        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        debugRenderer = new ShapeRenderer();

        // Posisi Awal Pemain
        currentPlayerX = (WORLD_WIDTH / 2) - (playerWidth / 2);
        playerRect = new Rectangle();

        enemies = new Array<>();
        spawnEnemy();

        // ... kode di atas ...

        // SETUP UI SKOR
        uiStage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        score = 0;
        scoreLabel = new Label("Score: 0", skin);

        // --- BAGIAN YANG DIUBAH ---

        // 1. Perbesar Ukuran Font (Coba ganti jadi 3.5f atau 4.0f)
        scoreLabel.setFontScale(3.5f);

        // 2. Turunkan Sedikit Posisinya (Ganti -50 jadi -100 biar ga kepotong)
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 100);

        // --------------------------

        uiStage.addActor(scoreLabel);
    } // Akhir dari Constructor

    private void spawnEnemy() {
        EnemyData enemy = new EnemyData();
        enemy.rect = new Rectangle();
        enemy.rect.width = 1.2f;
        enemy.rect.height = 2.0f;
        enemy.stateTime = 0;

        // Pilih tipe musuh secara acak
        enemy.textureIndex = MathUtils.random(0, enemyTextures.size - 1);

        // --- LOGIKA BARU UNTUK SEDAN BLUE (Index 5) ---
        // Asumsi urutan texture: 0-3 Truk, 4 Sedan Gray, 5 Sedan Blue
        if (enemy.textureIndex == 5) {

            // Tentukan posisi Y (Tetap dari atas layar)
            enemy.rect.y = WORLD_HEIGHT + 2f; // Muncul agak jauh di atas

            // Acak: Muncul dari KIRI atau KANAN?
            boolean startFromLeft = MathUtils.randomBoolean();

            if (startFromLeft) {
                // Mulai dari LUAR KIRI layar, jalan ke KANAN
                enemy.rect.x = -2.0f;
                enemy.speedX = 5.0f; // Kecepatan positif (ke kanan)
            } else {
                // Mulai dari LUAR KANAN layar, jalan ke KIRI
                enemy.rect.x = WORLD_WIDTH + 1.0f;
                enemy.speedX = -5.0f; // Kecepatan negatif (ke kiri)
            }

        } else {
            // --- LOGIKA MOBIL BIASA (Tetap di Jalur) ---
            float[] lanes = {1.35f, 3.35f, 5.35f, 7.35f};
            int randomLaneIndex = MathUtils.random(0, lanes.length - 1);

            enemy.rect.x = lanes[randomLaneIndex];
            enemy.rect.y = WORLD_HEIGHT;
            enemy.speedX = 0; // Mobil biasa tidak geser kiri-kanan
        }

        enemies.add(enemy);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        // ==========================================
        // 1. GERAKKAN BACKGROUND JALAN
        // ==========================================
        backgroundOffset += delta * SCROLL_SPEED;
        if (backgroundOffset > 100) backgroundOffset %= 100;

        // ==========================================
        // 2. UPDATE POSISI PEMAIN
        // ==========================================
        handleInput(delta);
        currentPlayerX = MathUtils.clamp(currentPlayerX, ROAD_START_X, ROAD_END_X - playerWidth);
        playerRect.set(currentPlayerX, playerY, playerWidth, playerHeight);

        // ==========================================
        // 3. SPAWN MUSUH (MAKIN LAMA MAKIN CEPAT!)
        // ==========================================

        // Rumus: Waktu Dasar (1.2 detik) - (Skor * 0.02 detik)
        // Jadi setiap dapat 1 poin, musuh muncul lebih cepat 0.02 detik.
        long spawnInterval = 1200000000L - (score * 20000000L);

        // Batasi (Clamp): Jangan biarkan interval lebih cepat dari 0.5 detik (500000000 nanosecond)
        // Agar game tetap bisa dimainkan dan tidak nge-bug tumpuk-tumpuk.
        if (spawnInterval < 500000000L) {
            spawnInterval = 500000000L;
        }

        if (TimeUtils.nanoTime() - lastEnemySpawnTime > spawnInterval) {
            spawnEnemy();
        }

        // ==========================================
        // 4. LOGIKA PERGERAKAN SEMUA MUSUH
        // ==========================================
        Iterator<EnemyData> iter = enemies.iterator();
        while (iter.hasNext()) {
            EnemyData enemy = iter.next();

            if (enemy.textureIndex == 5) {
                // SEDAN BLUE (Menyeberang)
                enemy.rect.y -= (enemySpeed * 0.7f) * delta;
                enemy.rect.x += enemy.speedX * delta;

            } else if (enemy.textureIndex == 3) {
                // RED TRUCK (Ngebut)
                enemy.rect.y -= (enemySpeed * 1.5f) * delta;

            } else if (enemy.textureIndex == 0) {
                // CREAM TRUCK (Zig-Zag)
                enemy.rect.y -= (enemySpeed * 0.8f) * delta;
                enemy.stateTime += delta;
                float zigZagAmount = MathUtils.sin(enemy.stateTime * 5.0f) * 3.0f * delta;
                enemy.rect.x += zigZagAmount;
                enemy.rect.x = MathUtils.clamp(enemy.rect.x, ROAD_START_X, ROAD_END_X - enemy.rect.width);

            } else {
                // NORMAL
                enemy.rect.y -= enemySpeed * delta;
            }

            // --- SKOR & HAPUS MUSUH ---
            if (enemy.rect.y + enemy.rect.height < 0) {
                iter.remove();
                score++;
                scoreLabel.setText("Score: " + score);
            }

            // --- GAME OVER ---
            // ... (kode sebelumnya) ...

            // --- GAME OVER ---
            else if (enemy.rect.overlaps(playerRect)) {
                // GANTI BAGIAN INI:
                // Panggil GameOverScreen dan kirim skor saat ini (score)
                game.setScreen(new GameOverScreen(game, score));
                dispose();
            }
        }

        uiStage.act();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentPlayerX -= STEER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentPlayerX += STEER_SPEED * delta;
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        drawScrollingTexture(grassTexture, 0, 0, GRASS_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(roadTexture, ROAD_START_X, 0, ROAD_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(grassTexture, ROAD_END_X, 0, GRASS_WIDTH, WORLD_HEIGHT);

        for (EnemyData enemy : enemies) {
            Texture currentTexture = enemyTextures.get(enemy.textureIndex);
            game.batch.draw(currentTexture, enemy.rect.x, enemy.rect.y, enemy.rect.width, enemy.rect.height);
        }

        game.batch.draw(playerTexture, currentPlayerX, playerY, playerWidth, playerHeight);

        game.batch.end();

        uiStage.draw();

        if (debugMode) drawDebugLines();
    }

    private void drawScrollingTexture(Texture texture, float x, float y, float width, float height) {
        float vStart = -backgroundOffset / 10f;
        float vEnd = vStart + (height / 10f);
        game.batch.draw(texture, x, y, width, height, 0, vEnd, 1, vStart);
    }

    private void drawDebugLines() {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(1, 0, 0, 1);
        debugRenderer.line(ROAD_START_X, 0, ROAD_START_X, WORLD_HEIGHT);
        debugRenderer.line(ROAD_END_X, 0, ROAD_END_X, WORLD_HEIGHT);
        debugRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(null); }
    @Override
    public void hide() { }
    @Override
    public void pause() { }
    @Override
    public void resume() { }

    @Override
    public void dispose() {
        roadTexture.dispose();
        grassTexture.dispose();
        playerTexture.dispose();
        for(Texture tex : enemyTextures) tex.dispose();
        debugRenderer.dispose();
        uiStage.dispose();
    }
}
