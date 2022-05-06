package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import processing.core.PApplet;
import Level.TileType;

import processing.core.PVector;

import static processing.core.PApplet.atan2;


public class Player extends AStarCharacter {

    public boolean hasGoal = false;
    public boolean sneaking = false;

    // moving directions
    boolean movingLeft;
    boolean movingRight;
    boolean movingUp;
    boolean movingDown;

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
        float multiplier = sneaking ? 0.5f : 1.0f;

        // only update orientation if moved
        if (movingLeft) {
            velocity.x = -1 * maxSpeed * multiplier;
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingRight) {
            velocity.x = maxSpeed * multiplier;
            orientation = atan2(velocity.y, velocity.x);
        } else velocity.x = 0;
        if (movingUp) {
            velocity.y = -1 * maxSpeed * multiplier;
            orientation = atan2(velocity.y, velocity.x);
        } else if (movingDown) {
            velocity.y = maxSpeed * multiplier;
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
}
