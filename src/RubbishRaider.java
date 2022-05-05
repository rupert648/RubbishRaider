import Camera.Camera;
import Level.Level;
import Characters.*;
import Constants.*;
import objects.Bed;
import objects.EscapeArea;
import objects.Goal;
import processing.core.PApplet;
import processing.core.PImage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RubbishRaider extends PApplet {
    // the current level
    public Level currentLevel = new Level(this);
    public int level = 1;
    // the camera
    public Camera camera = new Camera(this, GameConstants.CAMERA_SPEED);

    // image holder
    PImage PLAYER_IMAGE;
    PImage ENEMY_LEFT;
    PImage ENEMY_RIGHT;
    PImage BED;
    PImage YOU_LOST;
    PImage GOAL;
    PImage TRANSPARENT;
    PImage ESCAPE_AREA;

    // tiles
    PImage BATHROOM_TILE;
    PImage KITCHEN_TILE;
    PImage BEDROOM_TILE;
    PImage LIVING_ROOM_TILE;

    // gamestate options
    GameState gm = GameState.GENERATING;
    int startLevelFrame = 0;
    int youLostFrame = 0;

    // characters
    Player player = new Player(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, 0, 0, this, currentLevel, 3.0f, 1.0f);
    ArrayList<Enemy> enemies = new ArrayList<>();

    // objects
    Goal goal = new Goal(GameConstants.MY_WIDTH / 2 + 100, GameConstants.MY_HEIGHT / 2 + 100);
    EscapeArea escapeArea = new EscapeArea(GameConstants.MY_WIDTH / 5, GameConstants.MY_HEIGHT / 5);
    ArrayList<Bed> beds = new ArrayList<Bed>();

    public static void main(String[] args) {
        RubbishRaider main = new RubbishRaider();
        PApplet.runSketch(new String[]{"RubbishRaider"}, main);
    }

    public void settings() {
        size(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
    }

    // initialise screen and particle array
    public void setup() {
        loadImages();

        initEnemies();
        newLevel();
        player.setPathFinder(currentLevel);

        // get created object arrays
        beds = currentLevel.beds;

        // set enemy pathfinders
        for (Enemy enemy : enemies) {
            enemy.setPathFinder(currentLevel);
        }
    }

    public void initEnemies() {
        enemies = new ArrayList<>();
        // TODO: make adjust per level
        for (int i = 0; i < GameConstants.START_NUMB_ENEMIES; i++) {
            Enemy enemy = new Enemy(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, this, currentLevel, GameConstants.ENEMY_MAX_SPEED, 1f, player);
            enemies.add(enemy);
        }
    }

    // update particles, render.
    public void draw() {
        switch (gm) {
            case MENU -> menu();
            case PAUSED -> paused();
            case PLAYING -> playGame();
            case LOST -> lost();
            case GENERATING -> generating();
            case WON -> won();
            case STARTING_LEVEL -> startingLevelScreen();
        }
    }

    private void won() {
        background(0);
        text("YOU WON", GameConstants.MY_WIDTH / 3, GameConstants.MY_HEIGHT / 3);
    }

    private void menu() {
        background(0);
    }

    private void paused() {
    }

    private void lost() {
        playGame();

        youLostFrame++;
        int tintVal;
        if (youLostFrame >= GameConstants.LOST_FADE_IN_TIME) {
            tintVal = 255;
        } else {
            tintVal = (int) (((float) youLostFrame / (float) GameConstants.LOST_FADE_IN_TIME) * 255f);
        }

        tint(255, tintVal);
        image(YOU_LOST, 0, 0);
        noTint();
    }

    private void generating() {
        image(TRANSPARENT, 0 ,0);
        text("LOADING...", GameConstants.MY_WIDTH / 2 - 20, GameConstants.MY_HEIGHT / 2 - 20);
    }

    private void startingLevelScreen() {
        playGame();

        startLevelFrame++;
        int tintVal;
        if (youLostFrame >= GameConstants.LOST_FADE_IN_TIME) {
            tintVal = 0;
        } else {
            tintVal = 255 - (int) (((float) startLevelFrame / (float) GameConstants.LOST_FADE_IN_TIME) * 255f);
        }

        tint(255, tintVal);
        image(TRANSPARENT, 0, 0);
        fill(255, 0, 0);
        stroke(0);
        textSize(50);
        text("LEVEL  " + level, GameConstants.MY_WIDTH / 2 - 40, GameConstants.MY_HEIGHT / 2);
        fill(0);
        noStroke();
        noTint();

        if (startLevelFrame >= GameConstants.START_LEVEL_FADE_IN_TIME) {
            startLevelFrame = 0;
            gm = GameState.PLAYING;
        }

    }

    private void playGame() {
        background(0, 170, 0);

        camera.integrate(player);
        camera.render(currentLevel, KITCHEN_TILE, BATHROOM_TILE, BEDROOM_TILE, LIVING_ROOM_TILE);

        renderUpdateGoal();
        renderUpdateEscapeArea();
        if (gm != GameState.LOST)
            renderUpdatePlayer();
        renderUpdateEnemies();
        renderUpdateObjects();
        camera.drawHud(goal);
    }

    private void renderUpdatePlayer() {
        player.integrate(camera);
        camera.drawPlayer(player, PLAYER_IMAGE);
    }

    private void renderUpdateEnemies() {
        for (Enemy enemy : enemies) {
            renderUpdateEnemy(enemy);
        }
    }

    private void renderUpdateGoal() {
        if (goal.pickedUp) return;

        camera.drawGoal(goal, GOAL);

        if (goal.checkInRange(player)) {
            goal.pickUp(player);
        }
    }

    private void renderUpdateEnemy(Enemy enemy) {
        camera.drawEnemy(enemy, ENEMY_LEFT, ENEMY_RIGHT);

        if (!enemy.integrate(camera, player)) return;

        // means player has been caught
        gm = GameState.LOST;
    }

    private void renderUpdateEscapeArea() {
        camera.drawEscapeArea(escapeArea, ESCAPE_AREA);

        if (!player.hasGoal) return;

        if (escapeArea.playerInArea(player)) {
            gm = GameState.WON;
        }

    }

    private void renderUpdateObjects() {
        renderUpdateBeds();
    }

    private void renderUpdateBeds() {
        for (Bed bed : beds) {
            camera.drawBed(bed, BED);
        }
    }

    public void newLevel() {
        gm = GameState.GENERATING;

        currentLevel.generateLevel(enemies, player, goal);

        gm = GameState.STARTING_LEVEL;
    }

    public void keyPressed() {
        if (key == CODED && keyCode == SHIFT) {
            player.sneaking = true;
            return;
        }

        switch (key) {
            case 'a' -> camera.movingLeft();
            case 'd' -> camera.movingRight();
            case 'w' -> camera.movingUp();
            case 's' -> camera.movingDown();
            case 'c' -> player.stop(camera);
            case ' ' -> camera.center(player);
        }
    }

    public void keyReleased() {
        if (key == CODED && keyCode == SHIFT) {
            player.sneaking = false;
            return;
        }

        if (key == 'a') {
            camera.stopMovingLeft();
        }
        if (key == 'd') {
            camera.stopMovingRight();
        }
        if (key == 'w') {
            camera.stopMovingUp();
        }
        if (key == 's') {
            camera.stopMovingDown();
        }
    }

    public void mouseClicked() {
        // set target pos
        player.setTargetPos(mouseX, mouseY, camera);
    }

    public void loadImages() {
        PLAYER_IMAGE = loadImage("./assets/raccoonTop.png");
        PLAYER_IMAGE.resize(100, 100);

        ENEMY_LEFT = loadImage("./assets/enemyLeft.png");
        ENEMY_RIGHT = loadImage("./assets/enemyRight.png");
        BED = loadImage("./assets/bed.png");
        YOU_LOST = loadImage("./assets/youLost.png");
        YOU_LOST.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        GOAL = loadImage("./assets/goal.png");
        GOAL.resize(60, 60);
        TRANSPARENT = loadImage("./assets/transparent.png");
        TRANSPARENT.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        ESCAPE_AREA = loadImage("./assets/escapeArea.png");
        ESCAPE_AREA.resize((int) GameConstants.ESCAPE_AREA_SIZE, (int) GameConstants.ESCAPE_AREA_SIZE);

        // tiles
        BATHROOM_TILE = loadImage("./assets/bathroomTile.png");
        KITCHEN_TILE = loadImage("./assets/kitchenTile.png");
        BEDROOM_TILE = loadImage("./assets/bedroomTile.png");
        LIVING_ROOM_TILE = loadImage("./assets/livingRoomTile.png");
        BATHROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        KITCHEN_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        BEDROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        LIVING_ROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);

    }
}
