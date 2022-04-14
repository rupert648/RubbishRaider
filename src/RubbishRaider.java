import processing.core.PApplet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RubbishRaider extends PApplet {

    // game size
    final static int MY_WIDTH = 1024;
    final static int MY_HEIGHT = 768;
    // How many pieces (cells in the dungeon level) are we dividing the play area into, horizontally and vertically
    final static int V_GRANULES = 90;
    final static int H_GRANULES = 100;
    // map contents
    final static int EMPTY = 0;
    final static int WALL = 1;
    // and how big are they?
    final static int V_GRANULE_SIZE = MY_HEIGHT / V_GRANULES;
    final static int H_GRANULE_SIZE = MY_WIDTH / H_GRANULES;
    // the current level
    public Level currentLevel = new Level(this);
    GameState gm = GameState.GENERATING;
    Properties config = new Properties();
    String configFilePath = "./src/configuration/RubbishRaider.config";

    public static void main(String[] args) {
        RubbishRaider main = new RubbishRaider();
        PApplet.runSketch(new String[]{"RubbishRaider"}, main);
    }

    public void settings() {
        size(MY_WIDTH, MY_HEIGHT);
    }

    // initialise screen and particle array
    public void setup() {
        // load configuration
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            config.load(fis);
        } catch (FileNotFoundException e) {
            System.out.println("Configuration File Not Found");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Issue loading in configuration");
            System.exit(1);
        }

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
        float width = Float.parseFloat(config.getProperty("GAME_WIDTH"));
        float height = Float.parseFloat(config.getProperty("GAME_HEIGHT"));

        background(0);
    }

    private void paused() {
    }

    private void playGame() {
        background(128);

        currentLevel.render();
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
}
