import Camera.Camera;
import Level.Level;
import Characters.*;
import Constants.*;
import Throwable.Rock;
import objects.EscapeArea;
import objects.Goal;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class RubbishRaider extends PApplet {
    // the current level
    public Level currentLevel = new Level(this);
    public int level = 0;
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

    // hud
    PImage HUD;
    PImage HUD_TICKED;

    // tiles
    PImage BATHROOM_TILE;
    PImage KITCHEN_TILE;
    PImage BEDROOM_TILE;
    PImage LIVING_ROOM_TILE;
    PImage WALL_TILE;
    PImage MENU1;
    PImage MENU2;
    PImage DEFAULT_TILE;

    boolean menuFlash = false;

    // gamestate options
    GameState gm = GameState.MENU;
    int startLevelFrame = 0;
    int youLostFrame = 0;
    int wonLevelFrame = 0;

    // characters
    Player player;
    ArrayList<Enemy> enemies = new ArrayList<>();

    // objects
    Goal goal;
    EscapeArea escapeArea = new EscapeArea(GameConstants.MY_WIDTH / 5, GameConstants.MY_HEIGHT / 5);
    ArrayList<Rock> ammo;
    int ammoAmount;
//    ArrayList<Bed> beds = new ArrayList<>();

    public static void main(String[] args) {
        RubbishRaider main = new RubbishRaider();
        PApplet.runSketch(new String[]{"RubbishRaider"}, main);
    }

    public void settings() {
        size(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
    }

    public void setup() {
        loadImages();
    }

    public void initLevel() {
        currentLevel = new Level(this);
    }

    public void initPlayer() {
        player = new Player(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, 0, 0, this, currentLevel, 3.0f, 1.0f);
    }

    public void initGoal() {
        goal = new Goal(GameConstants.MY_WIDTH / 2 + 100, GameConstants.MY_HEIGHT / 2 + 100);
    }

    public void initEnemies() {
        enemies = new ArrayList<>();
        // TODO: make adjust per level

        // new enemy every 2 levels
        int numbEnemies = GameConstants.START_NUMB_ENEMIES + level / 2;

        for (int i = 0; i < numbEnemies; i++) {
            Enemy enemy = new Enemy(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, this, currentLevel, GameConstants.ENEMY_MAX_SPEED, 1f, player);
            enemies.add(enemy);
        }
    }

    public void initAmmo() {
        ammo = new ArrayList<>();
        ammoAmount = GameConstants.AMMO_PER_LEVEL;
        for (int i = 0; i < ammoAmount; i++) {
            Rock rock = new Rock(this, player, camera);
            ammo.add(rock);
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
            case LEVEL_WON -> won();
            case STARTING_LEVEL -> startingLevelScreen();
        }
    }

    private void won() {
        playGame();

        wonLevelFrame++;
        int tintVal;
        if (wonLevelFrame >= GameConstants.WON_FADE_IN_TIME) {
            tintVal = 255;

            if (wonLevelFrame >= GameConstants.LOST_FADE_IN_TIME + 100) {
                // load the new level
                wonLevelFrame = 0;
                newLevel();
            }
        } else {
            tintVal = (int) (((float) wonLevelFrame / (float) GameConstants.WON_FADE_IN_TIME) * 255f);
        }

        tint(255, tintVal);
        imageMode(CORNER);
        image(TRANSPARENT, 0, 0);
        stroke(255, 0, 0);
        textSize(50);
        text("LEVEL  " + level + " COMPLETE", (float) GameConstants.MY_WIDTH / 2 - 100, (float) GameConstants.MY_HEIGHT / 2);
        fill(0);
        noStroke();
        noTint();
    }

    private void menu() {
        level = 0;
        imageMode(CORNER);

        if (frameCount % 30 == 0) menuFlash = !menuFlash;

        PImage image = menuFlash ? MENU1 : MENU2;

        image(image, 0, 0);
    }

    private void paused() {
    }

    private void lost() {
        playGame();

        youLostFrame++;
        int tintVal;
        if (youLostFrame >= GameConstants.LOST_FADE_IN_TIME) {
            tintVal = 255;

            if (youLostFrame >= GameConstants.LOST_FADE_IN_TIME + 100) {
                gm = GameState.MENU;
            }
        } else {
            tintVal = (int) (((float) youLostFrame / (float) GameConstants.LOST_FADE_IN_TIME) * 255f);
        }

        tint(255, tintVal);
        imageMode(CORNER);
        image(YOU_LOST, 0, 0);
        fill(255, 0, 0);
        noTint();
    }

    private void generating() {
        image(TRANSPARENT, 0 ,0);
        text("LOADING...", (float) GameConstants.MY_WIDTH / 2 - 20, (float) GameConstants.MY_HEIGHT / 2 - 20);
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
        imageMode(CORNER);
        image(TRANSPARENT, 0, 0);
        fill(255, 0, 0);
        stroke(0);
        textSize(50);
        text("LEVEL  " + level, (float) GameConstants.MY_WIDTH / 2 - 40, (float) GameConstants.MY_HEIGHT / 2);
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
        camera.render(currentLevel, KITCHEN_TILE, BATHROOM_TILE, BEDROOM_TILE, LIVING_ROOM_TILE, WALL_TILE, DEFAULT_TILE);

        renderUpdateGoal();
        renderUpdateEscapeArea();
        if (gm != GameState.LOST && gm != GameState.LEVEL_WON)
            renderUpdatePlayer();
        renderUpdateEnemies();
        renderUpdateObjects();
        renderUpdateThrowables();
        camera.drawHud(goal, GOAL, HUD, HUD_TICKED);
    }

    private void renderUpdatePlayer() {
        player.integrate();
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

        if (!enemy.integrate(camera, player, gm == GameState.LEVEL_WON)) return;

        // means player has been caught
        gm = GameState.LOST;
    }

    private void renderUpdateEscapeArea() {
        camera.drawEscapeArea(escapeArea, ESCAPE_AREA);

        if (!player.hasGoal) return;

        if (escapeArea.playerInArea(player)) {
            gm = GameState.LEVEL_WON;
        }
    }

    private void renderUpdateObjects() {
        renderUpdateBeds();
    }

    private void renderUpdateBeds() {
//        for (Bed bed : beds) {
//            camera.drawBed(bed, BED);
//        }
    }

    private void renderUpdateThrowables() {
        for (Rock rock: ammo) {
            renderThrowable(rock);
        }
    }

    private void renderThrowable(Rock rock) {
        if (rock.launched) {
            rock.integrate(enemies);
            camera.drawRock(rock);
        }
    }

    public void newLevel() {
        initLevel();
        initPlayer();
        initGoal();
        initEnemies();
        initAmmo();

        gm = GameState.GENERATING;

        level++;

        currentLevel.generateLevel(enemies, player, goal);

        gm = GameState.STARTING_LEVEL;

        player.setPathFinder(currentLevel);

        // get created object arrays
//        beds = currentLevel.beds;

        // set enemy pathfinders
        for (Enemy enemy : enemies) {
            enemy.setPathFinder(currentLevel);
        }
    }

    public void keyPressed() {
        if (gm == GameState.MENU) {
            handleMenuClick();
            return;
        }

        if (key == CODED) {
            // camera
            if (keyCode == UP) camera.movingUp();
            if (keyCode == RIGHT) camera.movingRight();
            if (keyCode == LEFT) camera.movingLeft();
            if (keyCode == DOWN) camera.movingDown();

            if (keyCode == SHIFT) player.sneaking = !player.sneaking;
        }

        if (key == 'w') player.movingUp();
        if (key == 'a') player.movingLeft();
        if (key == 's') player.movingDown();
        if (key == 'd') player.movingRight();
        if (key == 'c') camera.followingPlayer = !camera.followingPlayer;
//        if (key == ' ') player.interact(beds);
    }

    public void keyReleased() {
        if (gm == GameState.MENU) return;

        if (key == CODED) {
            if (keyCode == UP) camera.stopMovingUp();
            if (keyCode == RIGHT) camera.stopMovingRight();
            if (keyCode == LEFT) camera.stopMovingLeft();
            if (keyCode == DOWN) camera.stopMovingDown();
        }

        if (key == 'w') player.stopMovingUp();
        if (key == 'a') player.stopMovingLeft();
        if (key == 's') player.stopMovingDown();
        if (key == 'd') player.stopMovingRight();
    }

    public void mousePressed() {
        // TODO: draw arc from raccoon to mouse location


    }

    public void mouseReleased() {
        // throw rock

        // check ammo
        if (ammoAmount <= 0) return;

        player.throwRock(mouseX, mouseY, ammo.get(ammo.size() - ammoAmount--));
    }

    public void handleMenuClick() {
        if (key == '\n') {
            newLevel();
        }
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
        WALL_TILE = loadImage("./assets/wallTile.png");
        DEFAULT_TILE = loadImage("./assets/defaultTile.png");
        BATHROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        KITCHEN_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        BEDROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        LIVING_ROOM_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        WALL_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);
        DEFAULT_TILE.resize(GameConstants.V_GRANULE_SIZE, GameConstants.H_GRANULE_SIZE);

        MENU1 = loadImage("./assets/menu1.png");
        MENU2 = loadImage("./assets/menu2.png");
        MENU1.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        MENU2.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);

        HUD = loadImage("./assets/hud1.png");
        HUD_TICKED = loadImage("./assets/hud2.png");
        HUD.resize(200, 200);
        HUD_TICKED.resize(200, 200);
    }

}
