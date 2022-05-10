package Throwable;

import Camera.Camera;
import Characters.Enemy;
import Characters.Player;
import Constants.GameConstants;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Rock {
    public PVector position;
    public PVector startPos;
    public PVector landPos;
    Player player;

    public boolean launched = false;
    public boolean landed = false;

    // sound
    public boolean madeSound;
    public int currentSoundRadius;

    PApplet applet;
    Camera camera;

    public Rock(PApplet applet, Player player, Camera camera) {
        this.camera = camera;
        this.applet = applet;
        this.player = player;
    }

    public void integrate(ArrayList<Enemy> enemies) {
        if (landed) {
            // make sound
            if (!madeSound) {

                makeSound(enemies);

            }
        };

        if (position.dist(landPos) < 10) {
            landed = true;
            return;
        }

        // get direction vector
        PVector dir = landPos.copy();
        dir.sub(position);
        dir.normalize();
        dir.mult(GameConstants.ROCK_SPEED);

        position.add(dir);

    }

    public void makeSound(ArrayList<Enemy> enemies) {
        if (currentSoundRadius >= GameConstants.STEP_SOUND_RADIUS) {
            madeSound = true;
            return;
        }

        // else draw sound radius and check for any enemies within it
        currentSoundRadius += GameConstants.STEP_RADIUS_INCR;
        applet.noFill();
        applet.stroke(255, 0, 0);

        applet.circle(position.x - camera.position.x, position.y - camera.position.y, currentSoundRadius);

        applet.stroke(0);
        applet.fill(0);

        checkEnemies(enemies);
    }

    public void checkEnemies(ArrayList<Enemy> enemies) {
        for (Enemy enemy: enemies) {

            float dist = enemy.position.dist(position);

            if (dist <= currentSoundRadius) {
                // within sound radius;
                enemy.trackObject(position);
            }

        }
    }

    public void launch(PVector startPos, float mouseX, float mouseY) {
        launched = true;
        this.startPos = startPos;
        this.position = startPos.copy();
        // need to adjust to get position on the map
        landPos = new PVector(mouseX + camera.position.x, mouseY + camera.position.y);
    }
}
