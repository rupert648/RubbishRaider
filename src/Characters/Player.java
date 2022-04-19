package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import PathFinding.AStarSearch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Player extends Character {
    public PVector targetPos;
    
    // A*
    boolean findingPath = false;
    boolean pathFound = true;
    int currentAstarPathIndex = 0;
    AStarSearch pathFinder;
    ArrayList<AStarNode> thePath = null ;

    public Player(int x, int y, float or,
                  float xVel, float yVel, PApplet applet, Level level, float maxSpeed, float maxAcceleration) {

        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(xVel, yVel);
        rotation = 0;
        targetPos = new PVector(x, y);
    }

    public void setPathFinder() {
        pathFinder = new AStarSearch(level, applet);
    }

    public void integrate(Camera camera) {
        applet.fill(255, 255, 0);
        applet.circle(targetPos.x, targetPos.y, 10);
        applet.fill(0);

        // if has line of site of target
        PVector temp = new PVector(position.x - camera.position.x, position.y - camera.position.y);

        if (efficientDDA(temp, targetPos, camera)) {
            PVector p = new PVector(targetPos.x - temp.x, targetPos.y - temp.y);
            kinematicSeekPoint(p);
            return;
        }
        
        // calculate position in grid square
        int playerCol = (int) position.x / GameConstants.H_GRANULE_SIZE;
        int playerRow = (int) position.y / GameConstants.V_GRANULE_SIZE;
        
        // calculate targetPos grid square
        // need to re-add camera to make sure targetPosition is relative to grid location
        int targetCol = (int) (targetPos.x + camera.position.x) / GameConstants.H_GRANULE_SIZE;
        int targetRow = (int) (targetPos.y + camera.position.y) / GameConstants.V_GRANULE_SIZE;
        
        // TODO: checks to ensure is inbounds;
        
        if (!pathFound) {
            findPath(targetRow, targetCol, playerRow, playerCol);
            currentAstarPathIndex = 0;
        } else if (thePath != null && currentAstarPathIndex < thePath.size()) {

            // draw path
            for (AStarNode node: thePath) {
                // get positions
                int x = node.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE/2;
                int y = node.getRow() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE/2;

                applet.circle(x - camera.position.x, y - camera.position.y, 2);
            }

            // iterate from this and find last path that can be directly seen.
            for (int i = currentAstarPathIndex; i < thePath.size(); i++) {
                AStarNode current = thePath.get(i);
                PVector currentVector = new PVector(0, 0);
                currentVector.x = (current.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE/2);
                currentVector.y = (current.getRow() * GameConstants.V_GRANULE_SIZE + GameConstants.V_GRANULE_SIZE/2);

                // include camera
                currentVector.sub(camera.position);

                if (!efficientDDA(temp, currentVector, camera)) {
                    currentAstarPathIndex = i - 1;
                    break;
                }
            }

            // seek first visible path
            AStarNode nextSquare = thePath.get(currentAstarPathIndex);
            PVector nextSquareCoords = new PVector(0,0);
            nextSquareCoords.x = nextSquare.getCol() * GameConstants.H_GRANULE_SIZE + GameConstants.H_GRANULE_SIZE/2;
            nextSquareCoords.y = nextSquare.getRow() * GameConstants.V_GRANULE_SIZE + GameConstants.V_GRANULE_SIZE/2;
            nextSquareCoords.sub(camera.position);

            PVector p = new PVector(nextSquareCoords.x - temp.x, nextSquareCoords.y - temp.y);
            kinematicSeekPoint(p);
        }
    }

    public void setTargetPos(float x, float y) {
        // calculate targetPos relative to map
        targetPos.x = x;
        targetPos.y = y;
        
        // set pathFound to false to redo A*;
        pathFound = false;
    }

    public void findPath(int monsterRow, int monsterCol, int playerRow, int playerCol) {
        pathFound = false ;
        findingPath = true ;
        ArrayList<AStarNode> result = pathFinder.search(monsterRow, monsterCol, playerRow, playerCol) ;
        // failure is represented as a null return
        if (result != null) {
            thePath = result ;
            pathFound = true ;
        }

        findingPath = false ;
    }
}
