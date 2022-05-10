import Camera.Camera;
import Level.Level;
import Characters.*;
import Constants.*;
import Throwable.Rock;
import objects.EscapeArea;
import objects.Goal;
import objects.Vent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONObject;

import java.util.ArrayList;

public class RubbishRaider extends PApplet {
    public int highScore = 0;

    // the current level
    public Level currentLevel = new Level(this);
    public int level = 0;
    // the camera
    public Camera camera = new Camera(this, GameConstants.CAMERA_SPEED);

    // image holder
    PImage PLAYER_IMAGE1;
    PImage PLAYER_IMAGE2;
    PImage ENEMY_LEFT;
    PImage ENEMY_RIGHT;
    PImage BED;
    PImage YOU_LOST;
    PImage GOAL;
    PImage TRANSPARENT;
    PImage ESCAPE_AREA;
    PImage COMPASS;
    PImage COMPASS_ARROW;
    PImage RACCOON_FACE;
    PImage EXCLAMATION;
    PImage VENT;
    PImage VENT2;

    // hud
    PImage HUD;
    PImage HUD_TICKED;
    PImage PAUSE1;
    PImage PAUSE2;

    // tiles
    PImage BATHROOM_TILE;
    PImage KITCHEN_TILE;
    PImage BEDROOM_TILE;
    PImage LIVING_ROOM_TILE;
    PImage WALL_TILE;
    PImage MENU1;
    PImage MENU2;
    PImage DEFAULT_TILE;

    // font
    PFont font;

    boolean menuFlash = false;
    boolean showingMap = false;
    boolean pauseOption = true;

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
    ArrayList<Vent> vents = new ArrayList<>();

    public static void main(String[] args) {
        RubbishRaider main = new RubbishRaider();
        PApplet.runSketch(new String[]{"RubbishRaider"}, main);
    }

    public void settings() {
        size(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
    }

    public void setup() {
        loadGameData();
        loadImages();
        cursor(CROSS);
    }

    public void initLevel() {
        currentLevel = new Level(this);
    }

    public void initPlayer() {
        player = new Player(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, 0, 0, this, currentLevel, GameConstants.PLAYER_SPEED, 1.0f);
    }

    public void initGoal() {
        goal = new Goal(GameConstants.MY_WIDTH / 2 + 100, GameConstants.MY_HEIGHT / 2 + 100);
    }

    public void initEnemies() {
        enemies = new ArrayList<>();

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
        textSize(50);
        fill(255);
        text("LEVEL  " + level + " COMPLETE", (float) GameConstants.MY_WIDTH / 2, (float) GameConstants.MY_HEIGHT / 2);
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

        int x = GameConstants.MY_WIDTH / 2;
        int y = 150 + GameConstants.MY_HEIGHT / 2;

        // draw highScore;
        fill(0);
        textSize(50);
        textAlign(CENTER);
        // below gives text an outline
        for(int x2 = -1; x2 < 4; x2++){
            text("Highscore: " + highScore, (float) x + x2, y);
            text("Highscore: " + highScore, (float) x, y + x2);
        }
        fill(255);
        text("Highscore: " + highScore, (float) x, y);
        fill(0);
    }

    private void paused() {
        // display background
        playGame();

        imageMode(CENTER);
        image(pauseOption ? PAUSE1 : PAUSE2, GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2);
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

        // fading in image
        tint(255, tintVal);
        imageMode(CORNER);
        image(TRANSPARENT, 0, 0);
        noTint();

        // text
        fill(0);
        textSize(50);
        textAlign(CENTER);
        // below gives text an outline
        for(int x = -1; x < 2; x++){
            text("LEVEL  " + level, (float) (GameConstants.MY_WIDTH / 2) + x, 100f);
            text("LEVEL  " + level, (float) GameConstants.MY_WIDTH / 2, 100f + x);
        }
        fill(255);
        text("LEVEL  " + level, (float) GameConstants.MY_WIDTH / 2, 100f);
        fill(0);

        if (startLevelFrame >= GameConstants.START_LEVEL_FADE_IN_TIME) {
            startLevelFrame = 0;
            gm = GameState.PLAYING;
        }

    }

    private void playGame() {
        background(0, 170, 0);

        camera.integrate(player);
        camera.render(currentLevel, KITCHEN_TILE, BATHROOM_TILE, BEDROOM_TILE, LIVING_ROOM_TILE, WALL_TILE, DEFAULT_TILE);

        renderUpdateVents();
        renderUpdateGoal();
        renderUpdateEscapeArea();
        if (gm != GameState.LOST && gm != GameState.LEVEL_WON)
            renderUpdatePlayer();
        renderUpdateEnemies();
        renderUpdateThrowables();
        updateSprint();

        camera.drawCrosshair(player, mouseX, mouseY);
        camera.drawHud(goal, GOAL, HUD, HUD_TICKED, ammoAmount, level, player.sprintDuration, COMPASS, COMPASS_ARROW, escapeArea);
        if (showingMap) camera.drawMap(currentLevel, player, RACCOON_FACE, escapeArea, ESCAPE_AREA, enemies);
    }

    private void renderUpdatePlayer() {

        if (player.hiding) return;

        if (gm != GameState.PAUSED)
            player.integrate(enemies);
        camera.drawPlayer(player, PLAYER_IMAGE1, PLAYER_IMAGE2);
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

        if (gm == GameState.PAUSED) return;
        if (!enemy.integrate(camera, player, gm == GameState.LEVEL_WON, EXCLAMATION)) return;

        // means player has been caught
        gm = GameState.LOST;
        // check if new highscore
        if (level > highScore) {
            writeNewHighScore(level);
            highScore = level;
        }
    }

    private void renderUpdateEscapeArea() {
        camera.drawEscapeArea(escapeArea, ESCAPE_AREA);

        if (!player.hasGoal) return;

        if (escapeArea.playerInArea(player)) {
            gm = GameState.LEVEL_WON;
        }
    }

    private void renderUpdateVents() {
        for (Vent vent : vents) {
            camera.drawVent(vent, VENT, VENT2);
        }
    }

    private void renderUpdateThrowables() {
        for (Rock rock: ammo) {
            renderThrowable(rock);
        }
    }

    private void renderThrowable(Rock rock) {
        if (rock.launched) {
            if (gm != GameState.PAUSED)
                rock.integrate(enemies);
            camera.drawRock(rock);
        }
    }

    public void updateSprint() {
        if (player.sprinting) {
            // decrement sprint
            if (player.sprintDuration <= 0) player.sprintDuration = 0;
            else player.sprintDuration--;

        } else {
            // every 2 frames add duration
            if (frameCount % 2 == 0)
                if (player.sprintDuration >= GameConstants.MAX_SPRINT_DURATION) player.sprintDuration = GameConstants.MAX_SPRINT_DURATION;
                else player.sprintDuration++;
        }
    }

    public void newLevel() {
        // reset vars
        startLevelFrame = 0;
        youLostFrame = 0;
        wonLevelFrame = 0;

        initLevel();
        initPlayer();
        initGoal();
        initEnemies();
        initAmmo();

        gm = GameState.GENERATING;

        level++;

        currentLevel.generateLevel(enemies, player, goal);

        vents = currentLevel.vents;

        gm = GameState.STARTING_LEVEL;

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

        if (gm == GameState.PAUSED) {
            handlePauseClick();
            return;
        }

        if (key == CODED) {
            // camera
            if (keyCode == UP) camera.movingUp();
            if (keyCode == RIGHT) camera.movingRight();
            if (keyCode == LEFT) camera.movingLeft();
            if (keyCode == DOWN) camera.movingDown();
            if (keyCode == CONTROL) player.dig(enemies);
            if (keyCode == SHIFT) player.sneaking = !player.sneaking;
        }

        if (key == 'w') player.movingUp();
        if (key == 'a') player.movingLeft();
        if (key == 's') player.movingDown();
        if (key == 'd') player.movingRight();
        if (key == 'c') camera.followingPlayer = !camera.followingPlayer;
        if (key == ' ') player.sprint();
        if (key == 'm') showingMap = true;
        if (key == 'p') {
            if (gm == GameState.PAUSED)
                gm = GameState.PLAYING;
            else gm = GameState.PAUSED;
        }
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
        if (key == 'e') player.interact(vents);
        if (key == ' ') player.stopSprinting();
        if (key == 'm') showingMap = false;
    }

    public void mousePressed() {

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

    public void handlePauseClick() {
        if (key == CODED) {
            if (keyCode == UP) {
                pauseOption = !pauseOption;
            } else if (keyCode == DOWN) {
                pauseOption = !pauseOption;
            }

            return;
        }

        if (key == '\n') {
            if (pauseOption) {
                // continue playing game
                gm = GameState.PLAYING;
            } else {
                gm = GameState.MENU;
            }
        }
    }

    public void loadImages() {
        PLAYER_IMAGE1 = loadImage("./assets/raccoonTop1.png");
        PLAYER_IMAGE1.resize(88, 30);
        PLAYER_IMAGE2 = loadImage("./assets/raccoonTop2.png");
        PLAYER_IMAGE2.resize(88, 30);

        ENEMY_LEFT = loadImage("./assets/enemyLeft.png");
        ENEMY_RIGHT = loadImage("./assets/enemyRight.png");
        ENEMY_LEFT.resize(80, 80);
        ENEMY_RIGHT.resize(80, 80);
        EXCLAMATION = loadImage("./assets/exclamation.png");
        EXCLAMATION.resize(10, 40);
        BED = loadImage("./assets/bed.png");
        YOU_LOST = loadImage("./assets/youLost.png");
        YOU_LOST.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        GOAL = loadImage("./assets/goal.png");
        GOAL.resize(60, 60);
        TRANSPARENT = loadImage("./assets/transparent.png");
        TRANSPARENT.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        ESCAPE_AREA = loadImage("./assets/escapeArea.png");
        ESCAPE_AREA.resize((int) GameConstants.ESCAPE_AREA_SIZE, (int) GameConstants.ESCAPE_AREA_SIZE);
        RACCOON_FACE = loadImage("./assets/raccoonFace.png");
        RACCOON_FACE.resize(GameConstants.RACCOON_FACE_WIDTH, GameConstants.RACCOON_FACE_HEIGHT);
        VENT = loadImage("./assets/vent.png");
        VENT.resize(GameConstants.VENT_WIDTH, GameConstants.VENT_HEIGHT);
        VENT2 = loadImage("./assets/vent2.png");
        VENT2.resize(GameConstants.VENT_WIDTH, GameConstants.VENT_HEIGHT);


        // tile
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

        PAUSE1 = loadImage("./assets/PAUSEMENU1.png");
        PAUSE2 = loadImage("./assets/PAUSEMENU2.png");
        PAUSE1.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
        PAUSE2.resize(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);

        HUD = loadImage("./assets/hud1.png");
        HUD_TICKED = loadImage("./assets/hud2.png");
        HUD.resize(200, 200);
        HUD_TICKED.resize(200, 200);

        COMPASS = loadImage("./assets/compass.png");
        COMPASS_ARROW = loadImage("./assets/compassArrow.png");
        COMPASS.resize(200, 200);
        COMPASS_ARROW.resize(200, 200);

        font = createFont("./assets/font.ttf", 128);
        textFont(font);
    }

    public void loadGameData() {
        JSONObject json;
        try {
            // try and load in highscore
            json = loadJSONObject("gameData.json");
            highScore = json.getInt("highScore");
        } catch (Exception e) {
            System.out.println("error loading in data values");
            // create new file
            writeNewHighScore(1);
            highScore = 1;
        }

        System.out.println(highScore);
    }

    public void writeNewHighScore(int newHighScore) {
        JSONObject json = new JSONObject();
        json.setInt("highScore", newHighScore);

        saveJSONObject(json, "./src/data/gameData.json");
    }

}
