package characters;

import Level.Level;
import processing.core.PApplet;
import processing.core.PVector;

public class Player extends Character {
    Player(int x, int y, float or,
           float xVel, float yVel, PApplet applet, Level level, float maxSpeed, float maxAcceleration) {

        super(applet, level, maxSpeed, maxAcceleration, new PVector(0, 0));
        position = new PVector(x, y);
        orientation = or;
        velocity = new PVector(xVel, yVel);
        rotation = 0;
    }
}
