package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private Texture playerTexture; // (BARU) Texture untuk mobil pemain

    // --- BACKGROUND SCROLLING ---
    private float backgroundOffset = 0;
    private final float SCROLL_SPEED = 5.0f; // Kecepatan jalan

    // --- PENGATURAN 5 JALUR ---
    private final float GRASS_WIDTH = 1f;
    private final float ROAD_WIDTH = WORLD_WIDTH - (GRASS_WIDTH * 2); // 8 meter
    private final float LANE_WIDTH = ROAD_WIDTH / 5f; // 1.6 meter per jalur
    private final float ROAD_START_X = GRASS_WIDTH;
    private final float ROAD_END_X = ROAD_START_X + ROAD_WIDTH;

    // --- LOGIKA PEMAIN (BARU) ---
    private int playerLane = 3; // Mulai di jalur tengah (Jalur 3 dari 5)
    private float playerY = 2f; // Posisi Y (tinggi) mobil tetap di bawah
    private float playerWidth = 1.2f; // Lebar mobil (dalam meter)
    private float playerHeight = 2.0f; // Tinggi mobil (dalam meter)

    // --- DEBUG ---
    private ShapeRenderer debugRenderer;
    private boolean debugMode = false; // Set ke true jika ingin lihat garis jalur

    public GameScreen(final Main game) {
        this.game = game;

        // 1. Setup Kamera
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // 2. Load Gambar
        roadTexture = new Texture(Gdx.files.internal("road_texture.png"));
        grassTexture = new Texture(Gdx.files.internal("grass_texture.png"));
        playerTexture = new Texture(Gdx.files.internal("MC.png")); // (BARU) Load mobil

        // Setting agar jalan bisa looping
        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        debugRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        // 1. Gerakkan Jalan (Scrolling Background)
        backgroundOffset += delta * SCROLL_SPEED;
        if (backgroundOffset > 100) backgroundOffset %= 100;

        // 2. Cek Input Keyboard (BARU)
        handleInput();
    }

    private void handleInput() {
        // Pindah ke KIRI (Tombol Panah Kiri atau A)
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (playerLane > 1) { // Hanya bisa pindah kalau belum di jalur paling kiri
                playerLane--;
            }
        }

        // Pindah ke KANAN (Tombol Panah Kanan atau D)
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (playerLane < 5) { // Hanya bisa pindah kalau belum di jalur paling kanan
                playerLane++;
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // 1. Gambar Background (Jalan & Rumput)
        drawScrollingTexture(grassTexture, 0, 0, GRASS_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(roadTexture, ROAD_START_X, 0, ROAD_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(grassTexture, ROAD_END_X, 0, GRASS_WIDTH, WORLD_HEIGHT);

        // 2. Gambar Mobil Pemain (BARU)
        // Hitung posisi X mobil berdasarkan Jalur saat ini (1-5)
        // Rumus: Awal Jalan + (Nomor Jalur - 1 * Lebar Jalur) + (Setengah Lebar Jalur) - (Setengah Lebar Mobil)
        float laneCenterX = ROAD_START_X + ((playerLane - 1) * LANE_WIDTH) + (LANE_WIDTH / 2);
        float playerX = laneCenterX - (playerWidth / 2);

        game.batch.draw(playerTexture, playerX, playerY, playerWidth, playerHeight);

        game.batch.end();

        // Debug Garis (Opsional)
        if (debugMode) drawDebugLines();
    }

    /**
     * Fungsi gambar background scrolling (Sudah diperbaiki arahnya)
     */
    private void drawScrollingTexture(Texture texture, float x, float y, float width, float height) {
        float vStart = -backgroundOffset / 10f; // Minus agar terlihat maju
        float vEnd = vStart + (height / 10f);
        game.batch.draw(texture, x, y, width, height, 0, vEnd, 1, vStart);
    }

    private void drawDebugLines() {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(1, 0, 0, 1);
        for (int i = 1; i <= 5; i++) {
            float laneX = ROAD_START_X + (i * LANE_WIDTH);
            debugRenderer.line(laneX, 0, laneX, WORLD_HEIGHT);
        }
        debugRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        playerTexture.dispose(); // (BARU) Jangan lupa buang sampah texture
        debugRenderer.dispose();
    }
}
