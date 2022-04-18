import Level.Level;
import characters.*;
import Constants.*;
import characters.Character;
import processing.core.PApplet;

import static Constants.GameConstants.PLAYER_SIZE_X;
import static Constants.GameConstants.PLAYER_SIZE_Y;

public class RubbishRaider extends PApplet {
    // the current level
    public Level currentLevel = new Level(this);
    // the camera
    public Camera camera = new Camera(this);

    GameState gm = GameState.GENERATING;

    // characters
    Enemy e = new Enemy(0,0,0, this, currentLevel, 0.8f, .1f);

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

        camera.render(currentLevel);

        renderUpdateEnemies();
    }

    private void renderUpdateEnemies() {
        renderUpdateEnemy(e);
    }

    private void renderUpdateEnemy(Enemy enemy) {
        camera.drawEnemy(enemy);

        enemy.integrate() ;
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
        if (key == 'a') {
            camera.position.x -= 5;
        }
        if (key == 'd') {
            camera.position.x += 5;
        }
        if (key == 'w') {
            camera.position.y -= 5;
        }
        if (key == 's') {
            camera.position.y += 5;
        }
    }
}
