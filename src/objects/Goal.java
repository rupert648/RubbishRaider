package objects;

import Characters.Player;
import Constants.GameConstants;
import processing.core.PVector;

public class Goal {

    public PVector position;
    public boolean pickedUp;

    public Goal(int x, int y) {
        position = new PVector(x,y);
    }

    public void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
    }

    public void pickUp(Player player) {
        pickedUp = true;

        player.hasGoal = true;
    }

    public boolean checkInRange(Player player) {
        return player.position.dist(position) < GameConstants.OBJECT_PICKUP_RADIUS;
    }

}
