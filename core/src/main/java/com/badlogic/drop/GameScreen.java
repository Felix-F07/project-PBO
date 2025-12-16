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
        float stateTime; // [BARU] Timer khusus untuk animasi zig-zag
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

        // SETUP UI SKOR
        uiStage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        score = 0;
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 50);
        uiStage.addActor(scoreLabel);
    }

    private void spawnEnemy() {
        EnemyData enemy = new EnemyData();
        enemy.rect = new Rectangle();
        enemy.rect.width = 1.2f;
        enemy.rect.height = 2.0f;
        enemy.stateTime = 0; // Reset waktu skill

        // Angka Jalur RAPI
        float[] lanes = {1.35f, 3.35f, 5.35f, 7.35f};

        int randomLaneIndex = MathUtils.random(0, lanes.length - 1);
        enemy.rect.x = lanes[randomLaneIndex];

        enemy.rect.y = WORLD_HEIGHT;
        enemy.textureIndex = MathUtils.random(0, enemyTextures.size - 1);

        enemies.add(enemy);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        backgroundOffset += delta * SCROLL_SPEED;
        if (backgroundOffset > 100) backgroundOffset %= 100;

        handleInput(delta);
        currentPlayerX = MathUtils.clamp(currentPlayerX, ROAD_START_X, ROAD_END_X - playerWidth);

        playerRect.set(currentPlayerX, playerY, playerWidth, playerHeight);

        if (TimeUtils.nanoTime() - lastEnemySpawnTime > 1000000000) {
            spawnEnemy();
        }

        Iterator<EnemyData> iter = enemies.iterator();
        while (iter.hasNext()) {
            EnemyData enemy = iter.next();

            // [SKILL MUSUH DIMULAI DI SINI]

            // Cek warna truk berdasarkan Index Texture:
            // 0 = Cream, 1 = Green, 2 = Blue, 3 = Red

            if (enemy.textureIndex == 3) {
                // --- SKILL 1: RED TRUCK (NGEBUT) ---
                // Turun 1.5x lebih cepat dari normal
                enemy.rect.y -= (enemySpeed * 1.5f) * delta;

            } else if (enemy.textureIndex == 0) {
                // --- SKILL 2: CREAM TRUCK (ZIG-ZAG / MABUK) ---
                // Turun sedikit lebih pelan
                enemy.rect.y -= (enemySpeed * 0.8f) * delta;

                // Logika Matematika Sinus untuk gerakan gelombang kiri-kanan
                enemy.stateTime += delta;
                float zigZagAmount = MathUtils.sin(enemy.stateTime * 5.0f) * 3.0f * delta;
                enemy.rect.x += zigZagAmount;

                // Pastikan mobil mabuk tidak keluar aspal
                enemy.rect.x = MathUtils.clamp(enemy.rect.x, ROAD_START_X, ROAD_END_X - enemy.rect.width);

            } else {
                // --- NORMAL (Mobil Lain) ---
                enemy.rect.y -= enemySpeed * delta;
            }

            // Logika Tambah Skor
            if (enemy.rect.y + enemy.rect.height < 0) {
                iter.remove();
                score++;
                scoreLabel.setText("Score: " + score);
            }

            // Logika Game Over
            if (enemy.rect.overlaps(playerRect)) {
                game.setScreen(new MainMenuScreen(game));
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
