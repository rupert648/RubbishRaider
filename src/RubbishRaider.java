import Camera.Camera;
import Level.Level;
import Characters.*;
import Constants.*;
import processing.core.PApplet;

public class RubbishRaider extends PApplet {
    // the current level
    public Level currentLevel = new Level(this);
    // the camera
    public Camera camera = new Camera(this, GameConstants.CAMERA_SPEED);

    GameState gm = GameState.GENERATING;

    // characters
    Player player = new Player(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, 0, 0, this, currentLevel, 3.0f, 1.0f);
    Enemy e = new Enemy(GameConstants.MY_WIDTH / 2, GameConstants.MY_HEIGHT / 2, 0, this, currentLevel, 0.8f, .1f);

    public static void main(String[] args) {
        RubbishRaider main = new RubbishRaider();
        PApplet.runSketch(new String[]{"RubbishRaider"}, main);
    }

    public void settings() {
        size(GameConstants.MY_WIDTH, GameConstants.MY_HEIGHT);
    }

    // initialise screen and particle array
    public void setup() {
        newLevel();
        player.setPathFinder();
    }

    // update particles, render.
    public void draw() {
        switch (gm) {
            case MENU -> menu();
            case PAUSED -> paused();
            case PLAYING -> playGame();
            case LOST -> lost();
            case GENERATING -> generating();
        }
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
    }

    private void renderUpdatePlayer() {
        player.integrate(camera);
        camera.drawPlayer(player, e);
    }

    private void renderUpdateEnemies() {
        renderUpdateEnemy(e);
    }

    private void renderUpdateEnemy(Enemy enemy) {
        camera.drawEnemy(enemy);

        enemy.integrate();
    }

    private void lost() {
    }

    private void generating() {
    }

    public void newLevel() {
        gm = GameState.GENERATING;

        currentLevel.generateLevel();

        gm = GameState.PLAYING;
    }

    public void keyPressed() {
        switch (key) {
            case 'a' -> camera.movingLeft();
            case 'd' -> camera.movingRight();
            case 'w' -> camera.movingUp();
            case 's' -> camera.movingDown();
            case ' ' -> camera.center(player);
        }
    }

    public void keyReleased() {
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
}
