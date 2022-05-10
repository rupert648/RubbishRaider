package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import Level.TileType;
import objects.Vent;
import processing.core.PApplet;
import Throwable.Rock;

import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.atan2;

public class Player extends Character {

    public boolean hasGoal = false;
    public boolean sneaking = false;
    public boolean hiding = false;

    // digging vars
    public boolean digging = false;
    public int diggingCol;
    public int diggingRow;
    public int diggingTimer = 0;

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

    public void integrate(ArrayList<Enemy> enemies) {
        if (digging) {
            performDig(enemies);
            return;
        };

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

    public void performDig(ArrayList<Enemy> enemies) {
        diggingTimer++;

        if (diggingTimer > GameConstants.DIG_TIME) {
            digging = false;
            diggingTimer = 0;
            // change block
            level.getMap()[diggingRow][diggingCol] = TileType.GENERIC;
        }

        // must reset AStar search for new map
        // set enemy pathfinders
        for (Enemy enemy : enemies) {
            enemy.setPathFinder(level);
        }
    }

    public boolean moving() {
        return velocity.x > 0.5f || velocity.y > 0.5f
                || velocity.x < -0.5f || velocity.y < -0.5f;
    }

    public void drawStep(Camera camera) {
        if (!moving()) return;

        float stepRadius = sprinting ? 1.3f * GameConstants.STEP_SOUND_RADIUS :
                sneaking ? 0.5f * GameConstants.STEP_SOUND_RADIUS : GameConstants.STEP_SOUND_RADIUS;

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

    public void dig(ArrayList<Enemy> enemies) {
        TileType[][] map = level.getMap();

        // find nearest WALL tile
        int playerRow = (int) (position.y / GameConstants.V_GRANULE_SIZE);
        int playerCol = (int) (position.x / GameConstants.H_GRANULE_SIZE);

        int nearestRow;
        int nearestCol;

        // search out in each direction
        TileType north = map[playerRow - 1][playerCol];
        TileType east = map[playerRow][playerCol + 1];
        TileType south = map[playerRow + 1][playerCol];
        TileType west = map[playerRow][playerCol - 1];

        if (north == TileType.WALL) {
            nearestRow = playerRow - 1;
            nearestCol = playerCol;
        } else if (east == TileType.WALL) {
            nearestRow = playerRow;
            nearestCol = playerCol + 1;
        } else if (south == TileType.WALL) {
            nearestRow = playerRow + 1;
            nearestCol = playerCol;
        } else if (west == TileType.WALL) {
            nearestRow = playerRow;
            nearestCol = playerCol - 1;
        } else return;

        digBlock(nearestCol, nearestRow, enemies);
    }

    public void digBlock(int col, int row, ArrayList<Enemy> enemies) {
        digging = true;
        diggingCol = col;
        diggingRow = row;

        // alert any enemies in a wide radius
        for (Enemy enemy: enemies) {
            float dist = position.dist(enemy.position);

            if (dist < GameConstants.DIG_SOUND_RADIUS) {
                enemy.lastHeardPosition = position.copy();
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

