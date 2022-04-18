import Level.Level;
import characters.Enemy;
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

    public void integrate() {
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

    void movingLeft() {
        movingLeft = true;
    }

    void stopMovingLeft() {
        movingLeft = false;
    }

    void movingRight() {
        movingRight = true;
    }

    void stopMovingRight() {
        movingRight = false;
    }

    void movingUp() {
        movingUp = true;
    }

    void stopMovingUp() {
        movingUp = false;
    }

    void movingDown() {
        movingDown = true;
    }

    void stopMovingDown() {
        movingDown = false;
    }
}
