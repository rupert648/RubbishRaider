package Camera;

import Constants.GameConstants;
import Level.Level;
import Characters.Enemy;
import Characters.Player;
import objects.Bed;
import objects.EscapeArea;
import objects.Goal;
import Level.TileType;
import Throwable.Rock;
import objects.Vent;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

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

    boolean playerImage = true;
    public void drawPlayer(Player player, PImage playerImage1, PImage playerImage2) {
        applet.pushMatrix();
        applet.imageMode(CENTER);
        applet.translate(player.position.x - position.x, player.position.y - position.y);
        applet.rotate(player.orientation); // rotate 45 degrees

        // switch frames whilst moving
        if (player.moving() && applet.frameCount % 15 == 0) playerImage = !playerImage;
        applet.image(playerImage ? playerImage1 : playerImage2, 0, 0);
        applet.popMatrix();

        player.drawStep(this);
    }

    public void drawGoal(Goal goal, PImage image) {
        applet.imageMode(CENTER);
        applet.image(image, goal.position.x-position.x, goal.position.y - position.y);
        applet.imageMode(CORNER);
    }

    public void drawEscapeArea(EscapeArea ea, PImage image) {

        applet.imageMode(CORNER);
        applet.image(image, ea.position.x - position.x, ea.position.y - position.y);
        applet.imageMode(CENTER);
    }

    public void drawMap(Level level, Player player, PImage racoonFace, EscapeArea es, PImage ESImage, ArrayList<Enemy> enemies) {

        // code to center map
        int mapWidth = V_GRANULES * V_MAP_GRANULE_SIZE;
        int xDisplacement = (MY_WIDTH - mapWidth) / 2;
        int mapHeight = H_GRANULES * H_MAP_GRANULE_SIZE;
        int yDisplacement = (MY_HEIGHT - mapHeight) / 2;

        // draw text
        outlinedText("MAP", MY_WIDTH / 2, yDisplacement - 20);

        for (int row = 0; row < V_GRANULES; row++) {
            for (int col = 0; col < H_GRANULES; col++) {

                TileType tile = level.getMap()[row][col];

                float xPos = xDisplacement + col * V_MAP_GRANULE_SIZE;
                float yPos = yDisplacement + row * H_MAP_GRANULE_SIZE;

                applet.noStroke();

                if (tile == TileType.WALL || tile == TileType.PERIMITER) {
                    applet.fill(0);
                    applet.rect(xPos, yPos, V_MAP_GRANULE_SIZE, H_MAP_GRANULE_SIZE);
                } else {
                    applet.fill(200);
                    applet.rect(xPos, yPos, V_MAP_GRANULE_SIZE, H_MAP_GRANULE_SIZE);
                }
            }
        }

        // draw escape area
        float xPos = xDisplacement + (es.position.x / V_GRANULE_SIZE) * V_MAP_GRANULE_SIZE;
        float yPos = yDisplacement + (es.position.y / H_GRANULE_SIZE) * H_MAP_GRANULE_SIZE;
        applet.image(ESImage, xPos, yPos, 20, 20);

        // draw mini player onto the map
        xPos = xDisplacement + (player.position.x / V_GRANULE_SIZE) * V_MAP_GRANULE_SIZE;
        yPos = yDisplacement + (player.position.y / H_GRANULE_SIZE) * H_MAP_GRANULE_SIZE;
        applet.imageMode(CENTER);
        applet.image(racoonFace, xPos, yPos);

        // draw mini enemies
        applet.fill(255, 0, 0);
        applet.strokeWeight(1);
        for (Enemy enemy: enemies) {
            xPos = xDisplacement + (enemy.position.x / V_GRANULE_SIZE) * V_MAP_GRANULE_SIZE;
            yPos = yDisplacement + (enemy.position.y / H_GRANULE_SIZE) * H_MAP_GRANULE_SIZE;
            applet.circle(xPos, yPos, 5);
        }
        applet.fill(0);
        applet.strokeWeight(5);
    }

    public void drawVent(Vent vent, PImage VENT, PImage VENT2) {
        applet.imageMode(CORNER);
        applet.image(
                vent.containsPlayer ? VENT2 : VENT,
                vent.position.x - position.x,
                vent.position.y - position.y
        );
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

    public void drawCrosshair(Player player, int mouseX, int mouseY) {
        // draw dotted line between 2
        applet.strokeWeight(2);
        dashline(
                player.position.x - position.x,
                player.position.y - position.y,
                mouseX,
                mouseY,
                10.0f,
                10.0f
        );
    }

    void dashline(float x0, float y0, float x1, float y1, float[ ] spacing)
    {
        float distance = dist(x0, y0, x1, y1);
        float [ ] xSpacing = new float[spacing.length];
        float [ ] ySpacing = new float[spacing.length];
        float drawn = 0.0f;  // amount of distance drawn

        if (distance > 0)
        {
            int i;
            boolean drawLine = true; // alternate between dashes and gaps

    /*
      Figure out x and y distances for each of the spacing values
      I decided to trade memory for time; I'd rather allocate
      a few dozen bytes than have to do a calculation every time
      I draw.
    */
            for (i = 0; i < spacing.length; i++)
            {
                xSpacing[i] = lerp(0, (x1 - x0), spacing[i] / distance);
                ySpacing[i] = lerp(0, (y1 - y0), spacing[i] / distance);
            }

            i = 0;
            while (drawn < distance)
            {
                if (drawLine)
                {
                    applet.line(x0, y0, x0 + xSpacing[i], y0 + ySpacing[i]);
                }
                x0 += xSpacing[i];
                y0 += ySpacing[i];
                // Add distance "drawn" by this line or gap
                drawn = drawn + mag(xSpacing[i], ySpacing[i]);
                i = (i + 1) % spacing.length;  // cycle through array
                drawLine = !drawLine;  // switch between dash and gap
            }
        }
    }

    void dashline(float x0, float y0, float x1, float y1, float dash, float gap)
    {
        float [ ] spacing = { dash, gap };
        dashline(x0, y0, x1, y1, spacing);
    }

    public void drawHud(
            Goal goal,
            PImage GOAL,
            PImage HUD,
            PImage HUD_TICKED,
            int ammoAmount,
            int level,
            int sprintDuration,
            PImage COMPASS,
            PImage COMPASS_ARROW,
            EscapeArea es
    ) {

        applet.imageMode(CORNER);
        if (!goal.pickedUp)
            applet.image(HUD, GameConstants.HUD_X, GameConstants. HUD_Y);
        else
            applet.image(HUD_TICKED, GameConstants.HUD_X, GameConstants. HUD_Y);

        applet.image(GOAL, HUD_X + 10, HUD_Y + 30);

        outlinedText(Integer.toString(ammoAmount), HUD_X + 135, HUD_Y + 125);
        outlinedText(Integer.toString(level), HUD_X + 135, HUD_Y + 180);

        drawSprintBar(sprintDuration);
        drawGoalCompass(goal, COMPASS, COMPASS_ARROW, es);
    }

    public void outlinedText(String text, int x, int y) {
        applet.fill(0);
        applet.textSize(50);
        applet.textAlign(CENTER);
        // below gives text an outline
        for(int x2 = -1; x2 < 4; x2++){
            applet.text(text, (float) x + x2, y);
            applet.text(text, (float) x, y + x2);
        }
        applet.fill(255);
        applet.text(text, (float) x, y);
        applet.fill(0);
    }

    public void drawSprintBar(int sprintDuration) {
        // proportion of bar filled
        float proportion = (float) sprintDuration / (float) MAX_SPRINT_DURATION;

        // inner bar
        applet.fill(0, 255, 0);
        applet.noStroke();
        applet.shapeMode(CORNER);
        float width = proportion * SPRINT_BAR_WIDTH;
        applet.rect((MY_WIDTH / 2) - SPRINT_BAR_WIDTH / 2, (MY_HEIGHT - 30) - SPRINT_BAR_HEIGHT / 2, width, SPRINT_BAR_HEIGHT);


        // outline bar
        applet.noFill();
        applet.stroke(0);
        applet.strokeWeight(5);
        applet.shapeMode(CENTER);
        applet.rect((MY_WIDTH / 2) - SPRINT_BAR_WIDTH / 2, (MY_HEIGHT - 30) - SPRINT_BAR_HEIGHT / 2, SPRINT_BAR_WIDTH, SPRINT_BAR_HEIGHT);

    }

    public void drawGoalCompass(Goal goal, PImage COMPASS, PImage COMPASS_ARROW, EscapeArea es) {
        applet.imageMode(CENTER);
        applet.image(COMPASS, COMPASS_X, COMPASS_Y);

        PVector direction;
        if (goal.pickedUp) {
            direction = new PVector(es.position.x - position.x - COMPASS_X, es.position.y - position.y - COMPASS_Y);
        } else {
            direction = new PVector(goal.position.x - position.x - COMPASS_X, goal.position.y - position.y - COMPASS_Y);
        }

        applet.pushMatrix();
        applet.imageMode(CENTER);
        applet.translate(COMPASS_X, COMPASS_Y);
        applet.rotate(direction.heading() + PI/2); // rotate 45 degrees
        applet.image(COMPASS_ARROW, 0, 0);
        applet.popMatrix();

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
