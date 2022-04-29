package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import PathFinding.AStarNode;
import PathFinding.AStarSearch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Enemy extends AStarCharacter {

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

            aStar(temp, camera, lastHeardPosition);
            return;
        }

        wander();
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
