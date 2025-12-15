package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    private Texture playerTexture;

    // --- BACKGROUND SCROLLING ---
    private float backgroundOffset = 0;
    private final float SCROLL_SPEED = 10.0f; // Kecepatan jalan ke bawah

    // --- PENGATURAN AREA JALAN ---
    private final float GRASS_WIDTH = 1f;
    private final float ROAD_WIDTH = WORLD_WIDTH - (GRASS_WIDTH * 2);
    private final float ROAD_START_X = GRASS_WIDTH;
    private final float ROAD_END_X = ROAD_START_X + ROAD_WIDTH;

    // --- LOGIKA PEMAIN ---
    private float playerWidth = 1.2f;
    private float playerHeight = 2.0f;
    private float playerY = 2f;

    // Posisi X pemain sekarang (Mulai tepat di tengah layar)
    private float currentPlayerX;

    // (PENTING) Kecepatan geser kiri-kanan saat tombol ditahan
    private final float STEER_SPEED = 6.0f;

    // --- DEBUG ---
    private ShapeRenderer debugRenderer;
    private boolean debugMode = false;

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        roadTexture = new Texture(Gdx.files.internal("road_texture.png"));
        grassTexture = new Texture(Gdx.files.internal("grass_texture.png"));
        playerTexture = new Texture(Gdx.files.internal("MC.png"));

        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        debugRenderer = new ShapeRenderer();

        // Set posisi awal mobil di tengah-tengah jalan raya
        currentPlayerX = (WORLD_WIDTH / 2) - (playerWidth / 2);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        // 1. Gerakkan Jalan
        backgroundOffset += delta * SCROLL_SPEED;
        if (backgroundOffset > 100) backgroundOffset %= 100;

        // 2. Cek Input (Gerakan Halus)
        handleInput(delta);

        // 3. Batasi agar mobil tidak keluar dari aspal (Nabrak Rumput)
        // MathUtils.clamp memastikan nilai X tidak kurang dari batas kiri dan tidak lebih dari batas kanan
        currentPlayerX = MathUtils.clamp(currentPlayerX, ROAD_START_X, ROAD_END_X - playerWidth);
    }

    private void handleInput(float delta) {
        // PERUBAHAN PENTING:
        // Gunakan 'isKeyPressed' (bukan isKeyJustPressed).
        // Ini akan mendeteksi selama tombol DITAHAN.

        // Gerak ke KIRI
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentPlayerX -= STEER_SPEED * delta; // Kurangi posisi X (geser kiri)
        }

        // Gerak ke KANAN
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentPlayerX += STEER_SPEED * delta; // Tambah posisi X (geser kanan)
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Gambar Background
        drawScrollingTexture(grassTexture, 0, 0, GRASS_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(roadTexture, ROAD_START_X, 0, ROAD_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(grassTexture, ROAD_END_X, 0, GRASS_WIDTH, WORLD_HEIGHT);

        // Gambar Mobil Pemain
        game.batch.draw(playerTexture, currentPlayerX, playerY, playerWidth, playerHeight);

        game.batch.end();

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
        // Gambar batas jalan saja
        debugRenderer.line(ROAD_START_X, 0, ROAD_START_X, WORLD_HEIGHT);
        debugRenderer.line(ROAD_END_X, 0, ROAD_END_X, WORLD_HEIGHT);
        debugRenderer.end();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }
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
        debugRenderer.dispose();
    }
}
