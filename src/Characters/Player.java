package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import objects.Bed;
import processing.core.PApplet;
import Throwable.Rock;
import Level.TileType;

import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.atan2;
import static processing.core.PConstants.PI;


public class Player extends AStarCharacter {

    public boolean hasGoal = false;
    public boolean sneaking = false;
    public boolean hiding = false;

    // moving directions
    boolean movingLeft;
    boolean movingRight;
    boolean movingUp;
    boolean movingDown;
    public boolean sprinting;

    public int sprintDuration = GameConstants.MAX_SPRINT_DURATION;

    // Step (sound radius)
    int currentStepRadius = 0;

    public Player(int x, int y, float or,
                  float xVel, float yVel, PApplet applet, Level level, float maxSpeed, float maxAcceleration) {

        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(xVel, yVel);
        rotation = 0;
    }

    public void integrate() {
        // get speed multiplier
        float multiplier = sprinting ? 1.5f :
                sneaking ? 0.5f : 1.0f;

        // check has enough sprintDuration left;
        multiplier = sprintDuration == 0 ? 1.0f : multiplier;

        // only update orientation if moved
        if (movingLeft) {
            velocity.x = -1 * maxSpeed * multiplier;
            hiding = false;
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingRight) {
            velocity.x = maxSpeed * multiplier;
            hiding = false;
            orientation = atan2(velocity.y, velocity.x);
        } else velocity.x = 0;
        if (movingUp) {
            velocity.y = -1 * maxSpeed * multiplier;
            hiding = false;
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingDown) {
            velocity.y = maxSpeed * multiplier;
            hiding = false;
            orientation = atan2(velocity.y, velocity.x);
        } else velocity.y = 0;

        // Deal with collisions by stopping dead. Bit crude.
        // Horizontal collisions
        if ((velocity.x < 0 && level.collidesXLeft(this)) ||
                (velocity.x > 0 && level.collidesXRight(this))) {

            // find closest empty square and move them to it
            moveToClosestSquareHorizontal();
            velocity.x = 0;
        } else if ((velocity.y < 0 && level.collidesYUp(this)) ||
                (velocity.y > 0 && level.collidesYDown(this))) {

            moveToClosestSquareVertical();
            velocity.y = 0;
        }

        // update orientation
        position.add(velocity);
    }



    public boolean moving() {
        return velocity.x > 0.5f || velocity.y > 0.5f
                || velocity.x < -0.5f || velocity.y < -0.5f;
    }

    // TODO: make check all enemies
    public void drawStep(Camera camera) {
        if (!moving()) return;

        float stepRadius = sneaking ? GameConstants.STEP_SOUND_RADIUS / 2 : GameConstants.STEP_SOUND_RADIUS;

        if (currentStepRadius < stepRadius) {
            applet.noFill();
            applet.stroke(255, 0, 0);
            applet.strokeWeight(3.0f);
            applet.ellipse(position.x - camera.position.x, position.y - camera.position.y, currentStepRadius, currentStepRadius);
            currentStepRadius += GameConstants.STEP_RADIUS_INCR;
            applet.stroke(0);
            applet.fill(0);
        } else {
            currentStepRadius = 0;
        }
    }

    public void interact(ArrayList<Bed> beds) {
        // check if in range of any of the beds
        for (Bed bed: beds) {
            float dist = bed.position.dist(position);

            if (dist < GameConstants.BED_INTERACT_AREA) {
                hiding = true;
                // jump to center of bed
                position.x = bed.position.x;
                position.y = bed.position.y;

                if (bed.orientation == 0) {
                    position.x += (float) GameConstants.BED_WIDTH / 2;
                    position.y += (float) GameConstants.BED_HEIGHT / 2;
                } else if (bed.orientation == PI/2) {
                    position.x -= (float) GameConstants.BED_HEIGHT / 2;
                    position.y += (float) GameConstants.BED_WIDTH / 2;
                } else if (bed.orientation == PI) {
                    position.x -= (float) GameConstants.BED_WIDTH / 2;
                    position.y -= (float) GameConstants.BED_HEIGHT / 2;
                } else if (bed.orientation == 3 * PI / 2) {
                    position.x += (float) GameConstants.BED_HEIGHT / 2;
                    position.y -= (float) GameConstants.BED_WIDTH / 2;
                }
            }
        }
    }

    public void throwRock(float mouseX, float mouseY, Rock rock) {
        rock.launch(position.copy(), mouseX, mouseY);
    }

    public void movingLeft() {
        movingLeft = true;
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void movingRight() {
        movingRight = true;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void movingUp() {
        movingUp = true;
    }

    public void stopMovingUp() {
        movingUp = false;
    }

    public void movingDown() {
        movingDown = true;
    }

    public void stopMovingDown() {
        movingDown = false;
    }

    public void sprint() {
        sneaking = false;
        sprinting = true;
    }

    public void stopSprinting() {
        sprinting = false;
    }
}

