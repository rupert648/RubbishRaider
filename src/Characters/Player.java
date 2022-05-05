package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import processing.core.PApplet;
import Level.TileType;

import processing.core.PVector;


public class Player extends AStarCharacter {
    public PVector targetPos;

    public boolean hasGoal = false;
    public boolean sneaking = false;

    // Step (sound radius)
    int currentStepRadius = 0;

    public Player(int x, int y, float or,
                  float xVel, float yVel, PApplet applet, Level level, float maxSpeed, float maxAcceleration) {

        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(xVel, yVel);
        rotation = 0;
        targetPos = new PVector(x, y);
    }

    public void integrate(Camera camera) {

        applet.fill(255, 255, 0);
        applet.circle(targetPos.x, targetPos.y, 10);
        applet.fill(0);

        PVector temp = new PVector(position.x - camera.position.x, position.y - camera.position.y);

        if (temp.dist(targetPos) < GameConstants.H_GRANULE_SIZE / 4.0f) {
            velocity.x = 0;
            velocity.y = 0;
            return;
        }

        float multiplier = sneaking ? 0.5f : 1.0f;

        // if has line of site of target
        if (efficientDDA(temp, targetPos, camera)) {
            PVector p = new PVector(targetPos.x - temp.x, targetPos.y - temp.y);
            kinematicSeekPoint(p,  multiplier);
            return;
        }

        aStar(temp, camera, targetPos, multiplier);
    }

    @Override
    boolean aStar(PVector temp, Camera camera, PVector targetPos, float multiplier) {
        // calculate position in grid square
        int playerCol = (int) position.x / GameConstants.H_GRANULE_SIZE;
        int playerRow = (int) position.y / GameConstants.V_GRANULE_SIZE;

        // calculate targetPos grid square
        // need to re-add camera to make sure targetPosition is relative to grid location
        int targetCol = (int) (targetPos.x + camera.position.x) / GameConstants.H_GRANULE_SIZE;
        int targetRow = (int) (targetPos.y + camera.position.y) / GameConstants.V_GRANULE_SIZE;

        if (!pathFound) {
            findPath(targetRow, targetCol, playerRow, playerCol);
            currentAstarPathIndex = 0;
        } else if (thePath != null && currentAstarPathIndex < thePath.size()) {

            // iterate from this and find last path that can be directly seen.
            for (int i = currentAstarPathIndex; i < thePath.size(); i++) {
                AStarNode current = thePath.get(i);
                PVector currentVector = new PVector(0, 0);
                currentVector.x = (current.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE / 2);
                currentVector.y = (current.getRow() * GameConstants.V_GRANULE_SIZE + GameConstants.V_GRANULE_SIZE / 2);

                // include camera
                currentVector.sub(camera.position);

                if (!efficientDDA(temp, currentVector, camera)) {
                    currentAstarPathIndex = i - 1;
                    break;
                }
            }

            // seek first visible path
            AStarNode nextSquare = thePath.get(currentAstarPathIndex);
            PVector nextSquareCoords = new PVector(0, 0);
            nextSquareCoords.x = nextSquare.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE / 2;
            nextSquareCoords.y = nextSquare.getRow() * GameConstants.V_GRANULE_SIZE + GameConstants.V_GRANULE_SIZE / 2;
            nextSquareCoords.sub(camera.position);

            PVector p = new PVector(nextSquareCoords.x - temp.x, nextSquareCoords.y - temp.y);
            kinematicSeekPoint(p, multiplier);
        }
        return false;
    }

    public void setTargetPos(float x, float y, Camera camera) {
        int targetCol = (int) (x + camera.position.x) / GameConstants.H_GRANULE_SIZE;
        int targetRow = (int) (y + camera.position.y) / GameConstants.V_GRANULE_SIZE;
        // check valid location to click;
        if (targetRow >= level.getMap().length || targetRow < 0 || targetCol >= level.getMap()[0].length || targetCol < 0) {
            return;
        } else if (level.getMap()[targetRow][targetCol] == TileType.WALL) {
            return;
        }

        targetPos.x = x;
        targetPos.y = y;

        // set pathFound to false to redo A*;
        pathFound = false;
    }

    public void stop(Camera camera) {
        velocity.x = 0;
        velocity.y = 0;
        targetPos = position.copy();
        targetPos.sub(camera.position);
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
            applet.ellipse(position.x - camera.position.x, position.y - camera.position.y, currentStepRadius, currentStepRadius);
            currentStepRadius += GameConstants.STEP_RADIUS_INCR;
            applet.fill(0);
        } else {
            currentStepRadius = 0;
        }
    }


}
