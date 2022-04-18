package Camera;

import Level.Level;
import Characters.Enemy;
import Characters.Player;
import processing.core.PApplet;
import processing.core.PVector;

import static Constants.GameConstants.PLAYER_SIZE_X;
import static Constants.GameConstants.PLAYER_SIZE_Y;
import static processing.core.PApplet.*;

public class Camera {

    public PVector position;
    public PVector velocity;
    public float maxSpeed;
    public PApplet applet;

    // moving directions
    boolean movingLeft;
    boolean movingRight;
    boolean movingUp;
    boolean movingDown;

    public Camera(PApplet applet, float maxSpeed) {
        this.applet = applet;
        this.maxSpeed = maxSpeed;

        this.velocity = new PVector(0, 0);
        this.position = new PVector(0,0);
    }

    public void integrate(Player player) {
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

        // update targetPos
        player.targetPos.sub(velocity);
    }

    public void render(Level current) {
        current.render(position);
    }

    public void drawEnemy(Enemy enemy) {
        float xm = enemy.position.x, ym = enemy.position.y ;

        enemy.colour();

        applet.ellipse(xm - position.x, ym - position.y,  PLAYER_SIZE_X, PLAYER_SIZE_Y) ;
        // Show orientation
        int newxm = (int)(xm + PLAYER_SIZE_X/3 * cos(enemy.orientation)) ;
        int newym = (int)(ym + PLAYER_SIZE_Y/3 * sin(enemy.orientation)) ;
        applet.fill(0);
        applet.ellipse(newxm - position.x, newym - position.y, PLAYER_SIZE_X/3,PLAYER_SIZE_Y/3) ;
    }

    public void drawPlayer(Player player) {
        applet.fill(0, 0, 200);
        applet.circle(player.position.x - position.x, player.position.y - position.y, 20);
        applet.fill(0);
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
