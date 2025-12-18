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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Iterator;

public class GameScreen implements Screen {
    final Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float WORLD_WIDTH = 10f;
    private final float WORLD_HEIGHT = 16f;

    private Texture roadTexture;
    private Texture grassTexture;
    private Texture playerTexture;


    private Array<Texture> textureNormal;
    private Array<Texture> textureZigzag;
    private Array<Texture> textureFast;


    private Player player;
    private Array<Enemy> enemies;


    private float backgroundOffset = 0;
    private final float SCROLL_SPEED = 10.0f;
    private final float GRASS_WIDTH = 1f;
    private final float ROAD_WIDTH = WORLD_WIDTH - (GRASS_WIDTH * 2);
    private final float ROAD_START_X = GRASS_WIDTH;
    private final float ROAD_END_X = ROAD_START_X + ROAD_WIDTH;


    private long lastEnemySpawnTime;
    private int score;
    private Stage uiStage;
    private Label scoreLabel;

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);


        roadTexture = new Texture(Gdx.files.internal("road_texture.png"));
        grassTexture = new Texture(Gdx.files.internal("grass_texture.png"));
        playerTexture = new Texture(Gdx.files.internal("MC.png"));


        textureNormal = new Array<>();
        textureNormal.add(new Texture(Gdx.files.internal("truck_blue.png")));
        textureNormal.add(new Texture(Gdx.files.internal("truck_green.png")));
        textureNormal.add(new Texture(Gdx.files.internal("trailer.png")));


        textureZigzag = new Array<>();
        textureZigzag.add(new Texture(Gdx.files.internal("compact_red.png")));
        textureZigzag.add(new Texture(Gdx.files.internal("compact_blue.png")));


        textureFast = new Array<>();
        textureFast.add(new Texture(Gdx.files.internal("sedan_red.png")));
        textureFast.add(new Texture(Gdx.files.internal("sedan_blue.png")));

        roadTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        player = new Player(playerTexture);
        enemies = new Array<>();
        spawnEnemy();


        uiStage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        score = 0;
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setFontScale(3.5f);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 100);
        uiStage.addActor(scoreLabel);
    }

    private void spawnEnemy() {
        float[] lanes = {1.35f, 3.35f, 5.35f, 7.35f};
        int laneIndex = MathUtils.random(0, lanes.length - 1);
        float spawnX = lanes[laneIndex];
        float spawnY = WORLD_HEIGHT;

        Enemy newEnemy;
        int typeChance = MathUtils.random(1, 100);

        if (typeChance <= 40) {

            Texture tex = textureNormal.random();
            newEnemy = new NormalEnemy(tex, spawnX, spawnY);

        } else if (typeChance <= 70) {

            Texture tex = textureZigzag.random();
            newEnemy = new ZigzagEnemy(tex, spawnX, spawnY);

        }
        else {

            Texture tex = textureFast.random();
            newEnemy = new FastEnemy(tex, spawnX, spawnY);
        }

        enemies.add(newEnemy);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        updateGame(delta);
        drawGame();
    }

    private void updateGame(float delta) {
        backgroundOffset += delta * SCROLL_SPEED;
        if (backgroundOffset > 100) backgroundOffset %= 100;

        player.update(delta);


        long spawnInterval = 1000000000L - (score * 10000000L);
        if (spawnInterval < 400000000L) spawnInterval = 400000000L;

        if (TimeUtils.nanoTime() - lastEnemySpawnTime > spawnInterval) {
            spawnEnemy();
        }

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();

            enemy.update(delta);

            if (enemy.getBounds().overlaps(player.getBounds())) {
                game.setScreen(new GameOverScreen(game, score));
                dispose();
                return;
            }

            if (enemy.isOffScreen()) {
                iter.remove();
                score++;
                scoreLabel.setText("Score: " + score);
            }
        }

        uiStage.act();
    }

    private void drawGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        drawScrollingTexture(grassTexture, 0, 0, GRASS_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(roadTexture, ROAD_START_X, 0, ROAD_WIDTH, WORLD_HEIGHT);
        drawScrollingTexture(grassTexture, ROAD_END_X, 0, GRASS_WIDTH, WORLD_HEIGHT);

        player.draw(game.batch);

        for (Enemy enemy : enemies) {
            enemy.draw(game.batch);
        }

        game.batch.end();
        uiStage.draw();
    }

    private void drawScrollingTexture(Texture texture, float x, float y, float width, float height) {
        float vStart = -backgroundOffset / 10f;
        float vEnd = vStart + (height / 10f);
        game.batch.draw(texture, x, y, width, height, 0, vEnd, 1, vStart);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        roadTexture.dispose();
        grassTexture.dispose();
        playerTexture.dispose();
        for(Texture t : textureNormal) t.dispose();
        for(Texture t : textureZigzag) t.dispose();
        for(Texture t : textureFast) t.dispose();
        uiStage.dispose();
    }

    @Override public void show() { Gdx.input.setInputProcessor(null); }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
