package Camera;

import Constants.GameConstants;
import Level.Level;
import Characters.Enemy;
import Characters.Player;
import objects.Bed;
import objects.EscapeArea;
import objects.Goal;
import Throwable.Rock;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static Constants.GameConstants.*;
import static processing.core.PApplet.*;

public class Camera {

    public PVector position;
    public PVector velocity;
    public float maxSpeed;
    public PApplet applet;
    boolean leftEnemyImage = false;

    public boolean followingPlayer = true;

    // moving directions
    boolean movingLeft;
    boolean movingRight;
    boolean movingUp;
    boolean movingDown;

    public Camera(PApplet applet, float maxSpeed) {
        this.applet = applet;
        this.maxSpeed = maxSpeed;

        this.velocity = new PVector(0, 0);
        this.position = new PVector(0, 0);
    }

    public void integrate(Player player) {
        if (followingPlayer) {
            center(player);
            return;
        }

        if (movingLeft) {
            velocity.x = -1 * maxSpeed;
        } else if (movingRight) {
            velocity.x = maxSpeed;
        } else velocity.x = 0;
        if (movingUp) {
            velocity.y = -1 * maxSpeed;
        } else if (movingDown) {
            velocity.y = maxSpeed;
        } else velocity.y = 0;

        position.add(velocity);
    }

    public void render(
            Level current,
            PImage KITCHEN_TILE,
            PImage BATHROOM_TILE,
            PImage BEDROOM_TILE,
            PImage LIVING_ROOM_TILE,
            PImage WALL_TILE,
            PImage DEFAULT_TILE
    ) {
        current.render(position, KITCHEN_TILE, BATHROOM_TILE, BEDROOM_TILE, LIVING_ROOM_TILE, WALL_TILE, DEFAULT_TILE);
    }

    public void drawEnemy(Enemy enemy, PImage ENEMY_LEFT, PImage ENEMY_RIGHT) {
        float xm = enemy.position.x, ym = enemy.position.y;

        drawEnemyCone(enemy);

        applet.pushMatrix();
        applet.imageMode(CENTER);
        applet.translate(xm - position.x, ym - position.y);
        applet.rotate(enemy.orientation + PI / 2); // rotate 45 degrees

        // swap image every 30 frames
        if (applet.frameCount % 30 == 0) {
            leftEnemyImage = !leftEnemyImage;
        }

        PImage img = leftEnemyImage ? ENEMY_LEFT : ENEMY_RIGHT;
        applet.image(img, 0, 0);

        applet.popMatrix();

//        applet.ellipse(xm - position.x, ym - position.y, PLAYER_SIZE_X, PLAYER_SIZE_Y);
//        // Show orientation
//        int newxm = (int) (xm + PLAYER_SIZE_X / 3 * cos(enemy.orientation));
//        int newym = (int) (ym + PLAYER_SIZE_Y / 3 * sin(enemy.orientation));
//        applet.fill(0);
//        applet.ellipse(newxm - position.x, newym - position.y, PLAYER_SIZE_X / 3, PLAYER_SIZE_Y / 3);
    }

    private void drawEnemyCone(Enemy enemy) {
        float xm = enemy.position.x - position.x, ym = enemy.position.y - position.y;

        float x1, y1, x2, y2;
        x1 = xm + GameConstants.VISION_SIZE * cos(enemy.orientation + radians(CONE_ANGLE) / 2);
        y1 = ym + GameConstants.VISION_SIZE * sin(enemy.orientation + radians(CONE_ANGLE) / 2);
        x2 = xm + GameConstants.VISION_SIZE * cos(enemy.orientation - radians(CONE_ANGLE) / 2);
        y2 = ym + GameConstants.VISION_SIZE * sin(enemy.orientation - radians(CONE_ANGLE) / 2);

        applet.stroke(200, 0, 0);
        applet.line(xm, ym, x1, y1);
        applet.line(xm, ym, x2, y2);
        applet.line(x1, y1, x2, y2);
        applet.stroke(0);
    }

    public void drawPlayer(Player player, PImage playerImage) {
        applet.pushMatrix();
        applet.imageMode(CENTER);
        applet.translate(player.position.x - position.x, player.position.y - position.y);
        applet.rotate(player.orientation); // rotate 45 degrees
        applet.image(playerImage, 0, 0);
        applet.popMatrix();

        player.drawStep(this);
    }

    public void drawGoal(Goal goal, PImage image) {
        applet.imageMode(CENTER);
        applet.image(image, goal.position.x-position.x, goal.position.y - position.y);
        applet.imageMode(CORNER);
    }

    public void drawEscapeArea(EscapeArea ea, PImage image) {

        applet.image(image, ea.position.x - position.x, ea.position.y - position.y);
    }

    public void drawBed(Bed bed, PImage bedImage) {
        applet.circle(bed.position.x - position.x, bed.position.y - position.y, 10);

        applet.pushMatrix();
        applet.imageMode(CORNER);
        applet.translate(bed.position.x - position.x, bed.position.y - position.y);
        applet.rotate(bed.orientation);

        applet.image(bedImage, 0, 0, BED_WIDTH, BED_HEIGHT);
        applet.popMatrix();
    }

    public void drawRock(Rock rock) {
        float xm = rock.position.x - position.x;
        float ym = rock.position.y - position.y;

        // size calculate based on distance from middle point between start position and targetPosition
        PVector midPoint = new PVector(0, 0);
        float midX = rock.startPos.x + ((rock.landPos.x - rock.startPos.x) / 2);
        float midY = rock.startPos.y + ((rock.landPos.y - rock.startPos.y) / 2);
        midPoint.x = midX;
        midPoint.y = midY;

        // dist from midpoint
        float dist = rock.position.dist(midPoint);
        // take this as a proportion of the greatest distance from center
        float prop = dist / rock.startPos.dist(midPoint);
        // biggest when dist = 0
        prop = 1 - prop;

        // max of 1.5x original size
        float scalar = 1.0f + prop * 2;

        if (!rock.landed) {
            applet.fill(0);
            applet.circle(xm - 5, ym + 5, 10 * scalar * 0.5f);
        }
        applet.fill(50);
        applet.circle(xm, ym, 10 * scalar);
        applet.fill(0);

    }

    public void drawHud(Goal goal, PImage GOAL, PImage HUD, PImage HUD_TICKED) {

        applet.imageMode(CORNER);
        if (!goal.pickedUp)
            applet.image(HUD, GameConstants.HUD_X, GameConstants. HUD_Y);
        else
            applet.image(HUD_TICKED, GameConstants.HUD_X, GameConstants. HUD_Y);

        applet.image(GOAL, HUD_X + 10, HUD_Y + 30);
    }

    public void center(Player player) {
        float x, y;
        x = player.position.x - MY_WIDTH / 2;
        y = player.position.y - MY_HEIGHT / 2;

        position.x = x;
        position.y = y;
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
