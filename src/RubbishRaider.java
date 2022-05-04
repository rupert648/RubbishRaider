import Camera.Camera;
import Level.Level;
import Characters.*;
import Constants.*;
import objects.EscapeArea;
import objects.Goal;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class RubbishRaider extends PApplet {
    // image holder
    PImage PLAYER_IMAGE;

    // the current level
    public Level currentLevel = new Level(this);
    // the camera
    public Camera camera = new Camera(this, GameConstants.CAMERA_SPEED);

    GameState gm = GameState.GENERATING;

    // characters
    Player player = new Player(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, 0, 0, this, currentLevel, 3.0f, 1.0f);
    ArrayList<Enemy> enemies = new ArrayList<>();

//    Enemy enemy = new Enemy(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, this, currentLevel, 0.8f, 1f, player);

    // objects
    // TODO: make generated in position
    Goal goal = new Goal(GameConstants.MY_WIDTH / 2 + 100, GameConstants.MY_HEIGHT / 2 + 100);
    EscapeArea escapeArea = new EscapeArea(GameConstants.MY_WIDTH / 5, GameConstants.MY_HEIGHT / 5);

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

        // set enemy pathfinders
        for (Enemy enemy: enemies) {
            enemy.setPathFinder(currentLevel);
        }
    }

    public void initEnemies() {
        enemies = new ArrayList<>();
        // TODO: make adjust per level
        for (int i = 0; i < GameConstants.START_NUMB_ENEMIES; i++) {
            Enemy enemy = new Enemy(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, this, currentLevel, 0.8f, 1f, player);
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

    private void playGame() {
        background(128);

        camera.integrate(player);
        camera.render(currentLevel);

        renderUpdatePlayer();
        renderUpdateEnemies();
        renderUpdateGoal();
        renderUpdateEscapeArea();
        camera.drawHud(goal);
    }

    private void renderUpdatePlayer() {
        player.integrate(camera);
        camera.drawPlayer(player, PLAYER_IMAGE);
    }

    private void renderUpdateEnemies() {
        for (Enemy enemy: enemies) {
            renderUpdateEnemy(enemy);
        }
    }

    private void renderUpdateGoal() {
        if (goal.pickedUp) return;

        camera.drawGoal(goal);

        if (goal.checkInRange(player)) {
            goal.pickUp(player);
        }
    }

    private void renderUpdateEnemy(Enemy enemy) {
        camera.drawEnemy(enemy);

        enemy.integrate(camera, player);
    }

    private void renderUpdateEscapeArea() {
        camera.drawEscapeArea(escapeArea);

        if (!player.hasGoal) return;

        if (escapeArea.playerInArea(player)) {
            gm = GameState.WON;
        }

    }

    private void lost() {
    }

    private void generating() {
    }

    public void newLevel() {
        gm = GameState.GENERATING;

        currentLevel.generateLevel(enemies, player, goal);

        gm = GameState.PLAYING;
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
    }
}
