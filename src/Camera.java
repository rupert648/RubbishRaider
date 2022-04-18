import Level.Level;
import characters.Enemy;
import processing.core.PApplet;
import processing.core.PVector;

import static Constants.GameConstants.PLAYER_SIZE_X;
import static Constants.GameConstants.PLAYER_SIZE_Y;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

public class Camera {

    public PVector position;
    public PApplet applet;

    public Camera(PApplet applet) {
        this.applet = applet;
        position = new PVector(0,0);
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
}
