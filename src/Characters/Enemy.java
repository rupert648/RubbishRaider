package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static Constants.GameConstants.CONE_ANGLE;
import static processing.core.PApplet.*;
import static processing.core.PApplet.radians;


public class Enemy extends AStarCharacter {

    Player player;

    boolean trackingPlayer;
    boolean trackingObject;

    PVector lastHeardPosition;

    public Enemy(int x, int y, float or, PApplet applet, Level level, float maxSpeed, float maxAcceleration, Player player) {
        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        this.player = player;
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(1f, 1f);
    }

    public boolean integrate(Camera camera, Player player, boolean levelFinished, PImage EXCLAMATION) {
        trackingPlayer = false;

        // if player is hiding, can't find him!
        if (!levelFinished && !player.hiding) {
            if (playerInVision(camera, player)) {
                trackingPlayer = true;
                lastHeardPosition = null;
            } else {
                int stepRadius = (int) (player.sprinting ? 1.3f * GameConstants.STEP_SOUND_RADIUS :
                        player.sneaking ? 0.5f * GameConstants.STEP_SOUND_RADIUS : GameConstants.STEP_SOUND_RADIUS);

                checkIfCanHearPlayer(stepRadius, player, camera);
            }
        }

        PVector temp = new PVector(position.x - camera.position.x, position.y - camera.position.y);

        if (trackingPlayer) {
            // track player!
            pursueCharacter(player, 2f);

            // set orientation
            orientation = velocity.heading();

            // draw exclamation
            applet.image(EXCLAMATION, position.x - camera.position.x + 10, position.y - camera.position.y - 10);

            // check for collision only if tracking player, prevents calling every frame
            return checkIfCaughtPlayer(player);
        }

        if (lastHeardPosition != null) {
            if (position.dist(lastHeardPosition) < GameConstants.H_GRANULE_SIZE / 4.0f) {
                lastHeardPosition = null;
                if (trackingObject) trackingObject = false;
                return false;
            }

            if (aStar(temp, camera, lastHeardPosition, 1.5f)) {
                lastHeardPosition = null;
                if (trackingObject) trackingObject = false;
            }

            // draw exclamation
            applet.image(EXCLAMATION, position.x - camera.position.x, position.y - camera.position.y - 50);

            return false;
        }

        wander();
        return false;
    }

    public boolean checkIfCaughtPlayer(Player player) {
        float dist = player.position.dist(position);

        return dist < GameConstants.CATCH_RADIUS;
    }

    public boolean playerInVision(Camera camera, Player player) {
        // check within range of cone before wasting time doing calcs
        if (player.position.dist(position) > GameConstants.VISION_SIZE) return false;

        // check have line of site.
        PVector playerTemp = new PVector(player.position.x - camera.position.x, player.position.y - camera.position.y);
        PVector enemyTemp = new PVector(position.x - camera.position.x, position.y - camera.position.y);
        if (efficientDDA(playerTemp, enemyTemp, camera)) {
            // cone check
            // http://www.jeffreythompson.org/collision-detection/tri-point.php

            // calculate points of triangle
            // one is simply pos of enemy
            float x1, y1, x2, y2;
            x1 = enemyTemp.x + GameConstants.VISION_SIZE * cos(orientation + radians(CONE_ANGLE) / 2);
            y1 = enemyTemp.y + GameConstants.VISION_SIZE * sin(orientation + radians(CONE_ANGLE) / 2);
            x2 = enemyTemp.x + GameConstants.VISION_SIZE * cos(orientation - radians(CONE_ANGLE) / 2);
            y2 = enemyTemp.y + GameConstants.VISION_SIZE * sin(orientation - radians(CONE_ANGLE) / 2);

            boolean result = pointTriangleCollision(
                    x1, y1,
                    x2, y2,
                    enemyTemp.x, enemyTemp.y,
                    playerTemp.x, playerTemp.y
            );

            // check if player is within vision triangle
            return result;
        }

        return false;
    }

    public void checkIfCanHearPlayer(int dist, Player player, Camera camera) {
        if (trackingPlayer) return;
        if (!player.moving()) return;

        PVector temp = new PVector(player.position.x - camera.position.x, player.position.y - camera.position.y);
        PVector enemyTemp = new PVector(position.x - camera.position.x, position.y - camera.position.y);

        if (position.dist(player.position) < dist) {
            if (efficientDDA(enemyTemp, temp, camera)) {
                // has line of sight of target
                trackPlayer();
            } else {
                stopTracking();
                // remember where last heard
                goToLocation(player.position);
            }
        } else {
            stopTracking();
        }
    }

    public void trackPlayer() {
        pathFound = false;
        trackingPlayer = true;
    }

    public void goToLocation(PVector pos) {
        pathFound = false;
        this.lastHeardPosition = pos;
    }

    public void trackObject(PVector pos) {
        // don't track object if already tracking player
        if (!trackingPlayer)
            pathFound = false;
            trackingObject = true;
            this.lastHeardPosition = pos;
    }

    public void stopTracking() {
        trackingPlayer = false;
    }
}
