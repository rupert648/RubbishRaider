package characters;

import Level.Level;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Enemy extends Character {

    public Enemy(int x, int y, float or, PApplet applet, Level level, float maxSpeed, float maxAcceleration){
        super(applet, level, maxSpeed, maxAcceleration, new PVector(0,0));
        position = new PVector(x, y) ;
        orientation = or ;
        velocity = new PVector(1f, 1f) ;
    }

    public void integrate() {

        // find which is closest
//        float distFromPlayer = player.position.dist(position);
//        float distFromFamilyMember = Float.MAX_VALUE;
//        FamilyCharacter closestFam = null;
//        // find closest
//        for (FamilyCharacter fam: familyMember) {
//            float temp = fam.position.dist(position);
//            if (temp < distFromFamilyMember) {
//                distFromFamilyMember = temp;
//                closestFam = fam;
//            }
//        }
//
//        Character toChase;
//        if (distFromFamilyMember < distFromPlayer) {
//            toChase = closestFam;
//        } else {
//            toChase = player;
//        }
//
//        if (seesCharacter(toChase)) {
//            pursueCharacter(toChase);
//            // check to see if pursued successfully
//            float dist = toChase.position.dist(position);
//            if (dist < AI.V_GRANULE_SIZE/3) {
//                if (toChase instanceof FamilyCharacter) {
//                    toChase.destroy();
//                } else {
//                    ((PlayerCharacter) toChase).damage();
//                    this.destroy();
//                }
//            }
//        }
        wander();
    }
}
