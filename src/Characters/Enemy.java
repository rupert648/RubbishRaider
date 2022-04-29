package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import PathFinding.AStarSearch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Enemy extends Character {

    // A*
    boolean findingPath = false;
    boolean pathFound = false;
    int currentAstarPathIndex = 0;
    AStarSearch pathFinder;
    ArrayList<AStarNode> thePath = null;

    Player player;

    boolean trackingPlayer;

    PVector lastHeardPosition;

    public Enemy(int x, int y, float or, PApplet applet, Level level, float maxSpeed, float maxAcceleration, Player player) {
        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        this.player = player;
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(1f, 1f);
    }

    public void setPathFinder() {
        pathFinder = new AStarSearch(level, applet);
    }

    public void integrate(Camera camera) {

        PVector temp = new PVector(position.x - camera.position.x, position.y - camera.position.y);

        if (trackingPlayer) {
            // track player!
            pursueCharacter(player);
            return;
        }

        if (lastHeardPosition != null) {
            if (position.dist(lastHeardPosition) < GameConstants.H_GRANULE_SIZE / 4.0f) {
                System.out.println("reached last heard position");
                lastHeardPosition = null;
                return;
            }

            aStar(temp, camera);
            return;
        }

        wander();
    }

    private void aStar(PVector temp, Camera camera) {
        // calculate position in grid square
        int enemyCol = (int) position.x / GameConstants.H_GRANULE_SIZE;
        int enemyRow = (int) position.y / GameConstants.V_GRANULE_SIZE;

        // calculate targetPos grid square
        // need to re-add camera to make sure targetPosition is relative to grid location
        int targetCol = (int) lastHeardPosition.x / GameConstants.H_GRANULE_SIZE;
        int targetRow = (int) lastHeardPosition.y / GameConstants.V_GRANULE_SIZE;

        if (!pathFound) {
            findPath(targetRow, targetCol, enemyRow, enemyCol);
            currentAstarPathIndex = 0;
        } else if (thePath != null && currentAstarPathIndex < thePath.size()) {

            // break out if we've reached the end
            if (currentAstarPathIndex == thePath.size()-1) {
                lastHeardPosition = null;
                return;
            }

            // draw path
            for (AStarNode node : thePath) {
                // get positions
                int x = node.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE / 2;
                int y = node.getRow() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE / 2;

                applet.circle(x - camera.position.x, y - camera.position.y, 2);
            }

            AStarNode nextSquare = thePath.get(currentAstarPathIndex);
            PVector nextSquareCoords = new PVector(0,0);
            nextSquareCoords.x = nextSquare.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE/2;
            nextSquareCoords.y = nextSquare.getRow() * GameConstants.V_GRANULE_SIZE + GameConstants.V_GRANULE_SIZE/2;

            // calculate distance between position and next square point
            float dist = nextSquareCoords.dist(position);
            if (dist < GameConstants.H_GRANULE_SIZE / 2) {
                // close to center, update value
                currentAstarPathIndex++;
            }

            // use next coords to calc direction
            nextSquareCoords.sub(camera.position);

            PVector p = new PVector(nextSquareCoords.x - temp.x, nextSquareCoords.y - temp.y);
            kinematicSeekPoint(p);
        }
    }

    public void findPath(int monsterRow, int monsterCol, int playerRow, int playerCol) {
        pathFound = false;
        findingPath = true;
        ArrayList<AStarNode> result = pathFinder.search(monsterRow, monsterCol, playerRow, playerCol);
        // failure is represented as a null return
        if (result != null) {
            thePath = result;
            pathFound = true;
        }

        findingPath = false;
    }

    public void trackPlayer() {
        pathFound = false;
        trackingPlayer = true;
    }

    public void goToLocation(PVector pos) {
        pathFound = false;
        this.lastHeardPosition = pos;
    }

    public void stopTracking() {
        trackingPlayer = false;
    }
}
