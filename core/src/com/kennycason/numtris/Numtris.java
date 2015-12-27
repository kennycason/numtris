package com.kennycason.numtris;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.*;

public class Numtris extends ApplicationAdapter {
    private static final Sprites SPRITES = new Sprites();
    private static final Random RANDOM = new Random();

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int DIM = 32;
    public static final int HUD_WIDTH = 160;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    public Piece[][] pieces = new Piece[WIDTH][HEIGHT];
    private boolean[][] flags = new boolean[WIDTH][HEIGHT];
    private Queue<Piece> next = new ArrayDeque<>();
    private Piece current;
    private long checkForCombo;
    private long score;
    private int combos = 0;


    private final long dropSpeed = 1000 / 4;
    private final long moveSpeed = 150;
    private final long comboSpeed = dropSpeed;

    @Override
    public void create () {
        SPRITES.load();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH * DIM + HUD_WIDTH, HEIGHT * DIM);
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        next.clear();
        next.add(getNext());
        next.add(getNext());
        next.add(getNext());
        next.add(getNext());
        next.add(getNext());
    }


    @Override
    public void render () {
        if (current == null) {
            current = getNext();
        }

        if (checkForCombo > 0) {
            if (TimeUtils.timeSinceMillis(checkForCombo) > comboSpeed) {
                findChains();
                checkForCombo = 0;
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawHud(batch);
        drawPieces(batch);

        batch.end();

        handleInput();
        dropPiece();
    }

    private void handleInput() {
        if (current == null) { return; }

        if (TimeUtils.timeSinceMillis(current.lastMoved) > moveSpeed) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                if (current.x > 0 && pieces[current.x - 1][current.y] == null) {
                    current.x--;
                    current.lastMoved = TimeUtils.millis();
                }
            }
            if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
                if (current.x < WIDTH -1 && pieces[current.x + 1][current.y] == null) {
                    current.x++;
                    current.lastMoved = TimeUtils.millis();
                }
            }
        }

        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            current.lastDropped -= 250;
        }
    }

    private void dropPiece() {
        if (current == null) { return; }

        if (current.y == 0 || pieces[current.x][current.y - 1] != null) {
            pieces[current.x][current.y] = current;
            current = null;
            findChains();
        }
        else if (TimeUtils.timeSinceMillis(current.lastDropped) >= dropSpeed) {
            current.y--;
            current.lastDropped = TimeUtils.millis();
        }
    }

    private void findChains() {
        clearFlags();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (pieces[x][y] == null) {
                    flags[x][y] = true;
                    continue;
                }
                final List<int[]> matches = new ArrayList<>();
                matches.add(new int[] {x, y});
                flags[x][y] = true;
                search(x, y, matches);
                if (matches.size() >= 4) {
                    score += (matches.size() * 20) + (combos * 250);
                    combos++;
                    clearAllMatches(matches);
                    dropAllFloatingPieces();
                    checkForCombo = TimeUtils.millis();
                } else {
                    combos = 0;
                }
            }
        }
    }

    private void dropAllFloatingPieces() {
        for (int x = 0; x < WIDTH; x++) {
            dropColumn(x);
        }
    }

    private void dropColumn(final int x) {
        int top = 0;
        for (int y = 0; y < HEIGHT; y++) {
            if (pieces[x][y] == null) {
                top = y;
                break;
            }
        }
        for (int y = top; y < HEIGHT; y++) {
            if (pieces[x][y] != null) {
                pieces[x][top] = pieces[x][y];
                pieces[x][y].x = x;
                pieces[x][y].y = top;
                pieces[x][y] = null;
                top++;
            }
        }
    }

    private void clearAllMatches(final List<int[]> matches) {
        for (int[] piece : matches) {
            pieces[piece[0]][piece[1]] = null;
        }
    }

    private void search(final int x, final int y, final List<int[]> matches) {
        // left
        if (x > 0
                && !flags[x - 1][y]
                && pieces[x - 1][y] != null
                && pieces[x - 1][y].value == pieces[x][y].value) {
            matches.add(new int[] {x - 1, y});
            flags[x - 1][y] = true;
            search(x - 1, y, matches);
        }
        // right
        if (x < WIDTH - 1
                && !flags[x + 1][y]
                && pieces[x + 1][y] != null
                && pieces[x + 1][y].value == pieces[x][y].value) {
            matches.add(new int[] {x + 1, y});
            flags[x + 1][y] = true;
            search(x + 1, y, matches);
        }
        // up
        if (y < HEIGHT - 1
                && !flags[x][y + 1]
                && pieces[x][y + 1] != null
                && pieces[x][y + 1].value == pieces[x][y].value) {
            matches.add(new int[] {x, y + 1});
            flags[x][y + 1] = true;
            search(x, y + 1, matches);
        }
        // down
        if (y > 0
                && !flags[x][y - 1]
                && pieces[x][y - 1] != null
                && pieces[x][y - 1].value == pieces[x][y].value) {
            matches.add(new int[] {x, y - 1});
            flags[x][y - 1] = true;
            search(x, y - 1, matches);
        }
    }

    private void clearFlags() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                flags[x][y] = false;
            }
        }
    }

    private Piece getNext() {
        next.add(buildNext());
        return next.poll();
    }

    private Piece buildNext() {
        final int next = RANDOM.nextInt(10);
        switch (next) {
            case 0: return new Piece(0, SPRITES.zero);
            case 1: return new Piece(1, SPRITES.one);
            case 2: return new Piece(2, SPRITES.two);
            case 3: return new Piece(3, SPRITES.three);
            case 4: return new Piece(4, SPRITES.four);
            case 5: return new Piece(5, SPRITES.five);
            case 6: return new Piece(6, SPRITES.six);
            case 7: return new Piece(7, SPRITES.seven);
            case 8: return new Piece(8, SPRITES.eight);
            case 9: return new Piece(9, SPRITES.nine);
        }
        throw new IllegalStateException("Unknown next piece: " + next);
    }

    private void drawPieces(final SpriteBatch batch) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (pieces[x][y] != null) {
                    batch.draw(pieces[x][y].texture, pieces[x][y].x * DIM, pieces[x][y].y * DIM);
                }
            }
        }
        if (current != null) {
            batch.draw(current.texture, current.x * DIM, current.y * DIM);
        }
    }

    private void drawHud(final SpriteBatch batch) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                batch.draw(SPRITES.empty, x * DIM, y * DIM);
            }
        }

        font.draw(batch, String.valueOf(score),
                WIDTH * DIM + 10,
                (HEIGHT * DIM) - 10);

        int i = 2;
        for (Piece piece : next) {
            batch.draw(piece.texture,
                    WIDTH * DIM + 10,
                    (HEIGHT * DIM) - (i * (DIM + 10)));
            i++;
        }
    }

}
