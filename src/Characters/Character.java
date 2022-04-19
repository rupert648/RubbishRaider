package Characters;

import Camera.Camera;
import Constants.GameConstants;
import Level.Level;
import processing.core.PApplet;
import processing.core.PVector;
import Level.TileType;

import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;

public abstract class Character extends Movable {

    private static final float SIGHT_RADIUS = 100;
    // char constants
    final float MAX_ROTATION = PI / 4;
    final float SLOW_RADIUS = 20f;
    final float TARGET_RADIUS = 3f;
    final float DRAG = 0.95f;
    public PApplet applet;
    // I'm allowing public access to keep things snappy
    // Static Data
    public float orientation;
    // Kinematic Data
    public float rotation;

    public Level level;

    public float maxSpeed;
    public float maxAcceleration;
    // destroyed
    public boolean destroyed;
    private PVector targetVelocity;


    public Character(PApplet applet, Level level, float maxSpeed, float maxAcceleration, PVector targetVelocity) {
        this.level = level;
        this.applet = applet;
        this.maxSpeed = maxSpeed;
        this.maxAcceleration = maxAcceleration;
        this.targetVelocity = targetVelocity;
    }

    public void colour() {
        applet.fill(255, 0, 0);
    }

    public void wander() {
        // randomly update orientation a little
        orientation += applet.random(0, PI / 64) - applet.random(0, PI / 64);
        // Keep in bounds
        if (orientation > PI) orientation -= 2 * PI;
        else if (orientation < -PI) orientation += 2 * PI;

        // derive velocity from orientation
        velocity.x = cos(orientation);
        velocity.y = sin(orientation);
        velocity.mult(maxSpeed);
        position.add(velocity);

        if ((velocity.x < 0 && level.collidesXLeft(this)) ||
                (velocity.x > 0 && level.collidesXRight(this)))
            velocity.x = -velocity.x;
        if ((velocity.y < 0 && level.collidesYUp(this)) ||
                (velocity.y > 0 && level.collidesYDown(this)))
            velocity.y = -velocity.y;
        //get orientation directly from velocity
        orientation = atan2(velocity.y, velocity.x);
    }

    public void pursueCharacter(Character c) {
        PVector direction = new PVector();
        direction.x = c.position.x - position.x;
        direction.y = c.position.y - position.y;

        seekFlee(c, direction);
    }

    public void fleeCharacter(Character c) {
        PVector direction = new PVector();
        direction.x = position.x - c.position.x;
        direction.y = position.y - c.position.y;

        seekFlee(c, direction);
    }

    private void seekFlee(Character c, PVector direction) {
        // predict characters next position
        float distance = direction.mag();
        float speed = velocity.mag();
        float prediction = distance / speed;

        // code to predict where they will be
        PVector pursueTarget = c.velocity.copy();
        pursueTarget.mult(prediction);
        pursueTarget.add(c.position);

        // found in gameplay this was very odd
//        PVector dirToSeek = new PVector(pursueTarget.x - position.x, pursueTarget.y - position.y);

        steeringSeekPoint(direction, 0);
    }

    public void steeringSeekPoint(PVector linear, float angular) {

        // if fleeing/seeking don't want them to bounce off the wall, want the 'cornered effect'
        // done by just setting velocity to 0
        if (velocity.x < 0 && level.collidesXLeft(this) || velocity.x > 0 && level.collidesXRight(this)) {
            velocity.x = 0;
        }
        if (velocity.y < 0 && level.collidesYUp(this) || velocity.y > 0 && level.collidesYDown(this)) {
            velocity.y = 0;
        }

        // add velocity afterwards to prevent jittering through walls
        position.add(velocity);

        orientation += rotation;
        if (orientation > PI) orientation -= 2 * PI;
        else if (orientation < -PI) orientation += 2 * PI;

        float distance = linear.mag();
        // If arrived, no acceleration.
        if (distance > TARGET_RADIUS) {
            float targetSpeed = maxSpeed;
            if (distance <= SLOW_RADIUS)
                targetSpeed = maxSpeed * distance / SLOW_RADIUS;

            targetVelocity = linear.copy();
            targetVelocity.normalize();
            targetVelocity.mult(targetSpeed);

            PVector accel = targetVelocity.copy();
            accel.sub(velocity);

            if (accel.mag() > maxAcceleration) {
                accel.normalize();
                accel.mult(maxAcceleration);
            }

            velocity.add(accel);
            if (velocity.mag() > maxSpeed) {
                velocity.normalize();
                velocity.mult(maxSpeed);
            }
        }

        // some drag
        velocity.mult(DRAG);

        rotation += angular;
        if (rotation > MAX_ROTATION) rotation = MAX_ROTATION;
        else if (rotation < -MAX_ROTATION) rotation = -MAX_ROTATION;
    }

    public void kinematicSeekPoint(PVector linear) {
        velocity = linear.copy();
        velocity.normalize();
        velocity.mult(maxSpeed);

        float orientationIncrement = PI / 32;

        position.add(velocity);
        // Apply an impulse to bounce off the edge of the screen
        if ((position.x < 0) || (position.x > applet.width)) velocity.x = -velocity.x;
        if ((position.y < 0) || (position.y > applet.height)) velocity.y = -velocity.y;

        //move a bit towards velocity:
        // turn vel into orientation
        float targetOrientation = atan2(velocity.y, velocity.x);

        // Will take a frame extra at the PI boundary
        if (abs(targetOrientation - orientation) <= orientationIncrement) {
            orientation = targetOrientation;
            return;
        }

        // if it's less than me, then how much if up to PI less, decrease otherwise increase
        if (targetOrientation < orientation) {
            if (orientation - targetOrientation < PI) orientation -= orientationIncrement;
            else orientation += orientationIncrement;
        } else {
            if (targetOrientation - orientation < PI) orientation += orientationIncrement;
            else orientation -= orientationIncrement;
        }
    }

    public boolean seesCharacter(Character p) {
        // calculate distance between two.
        if (p.position.dist(this.position) < SIGHT_RADIUS) {
            // within seeing range
            // perform DDA
//            return efficientDDA(position, p.position);

        }

        return false;
    }

    public boolean efficientDDA(PVector start, PVector end, Camera camera) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        applet.line(start.x, start.y, end.x, end.y);
        PVector rayDir = new PVector(dx, dy);

        // more optimal step size
        PVector unitStepSize = new PVector(
                sqrt(1 + (rayDir.y / rayDir.x) * (rayDir.y / rayDir.x)),
                sqrt(1 + (rayDir.x / rayDir.y) * (rayDir.x / rayDir.y))
        );

        PVector vMapCheck = start.copy();
        PVector vRayLength1D = new PVector(0, 0);
        PVector vStep = new PVector(0, 0);

        // Establish Starting Conditions
        if (rayDir.x < 0) {
            vStep.x = -1;
            vRayLength1D.x = (start.x - vMapCheck.x) * unitStepSize.x;
        } else {
            vStep.x = 1;
            vRayLength1D.x = ((vMapCheck.x + 1) - start.x) * unitStepSize.x;
        }

        if (rayDir.y < 0) {
            vStep.y = -1;
            vRayLength1D.y = (start.y - vMapCheck.y) * unitStepSize.y;
        } else {
            vStep.y = 1;
            vRayLength1D.y = ((vMapCheck.y + 1) - start.y) * unitStepSize.y;
        }

        // Perform "Walk" until collision or range check
        boolean bTileFound = false;
        // only search up until the player
        float fMaxDistance = rayDir.mag();
        float fDistance = 0.0f;
        while (!bTileFound && fDistance < fMaxDistance) {
            // Walk along shortest path
            if (vRayLength1D.x < vRayLength1D.y) {
                vMapCheck.x += vStep.x;
                fDistance = vRayLength1D.x;
                vRayLength1D.x += unitStepSize.x;
            } else {
                vMapCheck.y += vStep.y;
                fDistance = vRayLength1D.y;
                vRayLength1D.y += unitStepSize.y;
            }

            applet.stroke(255, 0, 0);
            applet.circle(vMapCheck.x, vMapCheck.y, 1);
            applet.stroke(0);

            int row = (int) (vMapCheck.y + camera.position.y) / GameConstants.V_GRANULE_SIZE;
            int col = (int) (vMapCheck.x + camera.position.x) / GameConstants.H_GRANULE_SIZE;
            if (level.getMap()[row][col] == TileType.WALL) {
                bTileFound = true;
            }
        }
        return !bTileFound;
    }
}