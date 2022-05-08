package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import objects.Bed;
import objects.Vent;
import processing.core.PApplet;
import Throwable.Rock;

import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.atan2;

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

        if (movingLeft) {
            if (movingUp) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = -1 * speed2;
                velocity.y = -1 * speed2;

            } else if (movingDown) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = -1 * speed2;
                velocity.y = speed2;
            } else {
                velocity.x = -1 * maxSpeed * multiplier;
                velocity.y = 0;
            }
            orientation = atan2(velocity.y, velocity.x);

        } else if (movingRight) {
            if (movingUp) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x =  speed2;
                velocity.y = -1 * speed2;

            } else if (movingDown) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = speed2;
                velocity.y = speed2;
            } else {
                velocity.x = maxSpeed * multiplier;
                velocity.y = 0;
            }
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingUp) {
            if (movingLeft) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = -1 * speed2;
                velocity.y = -1 * speed2;

            } else if (movingRight) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = speed2;
                velocity.y = -1 * speed2;
            } else {
                velocity.x = 0;
                velocity.y = -1 * maxSpeed * multiplier;
            }
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingDown) {
            if (movingLeft) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = -1 * speed2;
                velocity.y =  speed2;

            } else if (movingRight) {
                float speed = maxSpeed * multiplier;
                float speed2 = (float) Math.sqrt((speed * speed) / 2);

                velocity.x = speed2;
                velocity.y = speed2;
            } else {
                velocity.x = 0;
                velocity.y = maxSpeed * multiplier;
            }
            orientation = atan2(velocity.y, velocity.x);
        } else {
            velocity.x = 0;
            velocity.y = 0;
        }

        // Deal with collisions by stopping dead. Bit crude.
        // Horizontal collisions
        if ((velocity.x < 0 && level.collidesXLeft(this)) ||
                (velocity.x > 0 && level.collidesXRight(this))) {

            // find closest empty square and move them to it
            velocity.x = 0;
            moveToClosestSquareHorizontal();
        } else if ((velocity.y < 0 && level.collidesYUp(this)) ||
                (velocity.y > 0 && level.collidesYDown(this))) {
            velocity.y = 0;
            moveToClosestSquareVertical();
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

    public void interact(ArrayList<Vent> vents) {
        if (hiding) {
            hiding = false;

            // reset all vents status
            for (Vent vent: vents) {
                vent.containsPlayer = false;
            }

            return;
        }

        // check if in range of any of the beds
        for (Vent vent: vents) {

            if (vent.playerInArea(this)) {
                hiding = true;
                vent.containsPlayer = true;
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

