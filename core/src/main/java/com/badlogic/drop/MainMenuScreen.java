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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final Main game;
    private Stage stage;
    private Skin skin;

    private Texture playTexture, optionsTexture, quitTexture;

    public MainMenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        playTexture = new Texture(Gdx.files.internal("Play Button.png"));
        optionsTexture = new Texture(Gdx.files.internal("Options Button.png"));
        quitTexture = new Texture(Gdx.files.internal("Quit Button.png"));

        TextureRegionDrawable playDrawable = new TextureRegionDrawable(new TextureRegion(playTexture));
        TextureRegionDrawable optionsDrawable = new TextureRegionDrawable(new TextureRegion(optionsTexture));
        TextureRegionDrawable quitDrawable = new TextureRegionDrawable(new TextureRegion(quitTexture));

        ImageButton playButton = new ImageButton(playDrawable);
        ImageButton optionsButton = new ImageButton(optionsDrawable);
        ImageButton quitButton = new ImageButton(quitDrawable);

        final Window optionsWindow = new Window("Game Options", skin);


        optionsWindow.add(new Label("Pengaturan Suara (Coming Soon)", skin)).pad(20);
        optionsWindow.row();
        TextButton closeOptionsButton = new TextButton("Close", skin);
        optionsWindow.add(closeOptionsButton).pad(10);


        optionsWindow.pack();
        optionsWindow.setPosition(
            (Gdx.graphics.getWidth() - optionsWindow.getWidth()) / 2,
            (Gdx.graphics.getHeight() - optionsWindow.getHeight()) / 2
        );


        optionsWindow.setVisible(false);


        closeOptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionsWindow.setVisible(false);
            }
        });



        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                optionsWindow.setVisible(true);

                optionsWindow.toFront();
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);


        table.add(playButton).pad(10);
        table.row();
        table.add(optionsButton).pad(10);
        table.row();
        table.add(quitButton).pad(10);

        stage.addActor(table);
        stage.addActor(optionsWindow);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
        playTexture.dispose();
        optionsTexture.dispose();
        quitTexture.dispose();
    }
}
