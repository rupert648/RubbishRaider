package objects;

import Characters.Player;
import Constants.GameConstants;
import processing.core.PVector;

public class Vent {
    public PVector position = new PVector(0,0);
    public boolean containsPlayer = false;

    public boolean playerInArea(Player player) {

        PVector playerPos = player.position;
        // above the bottom
        return playerPos.x >= position.x &&         // right of the left edge AND
                playerPos.x <= position.x + GameConstants.VENT_WIDTH &&    // left of the right edge AND
                playerPos.y >= position.y &&         // below the top AND
                playerPos.y <= position.y + GameConstants.VENT_HEIGHT;
    }
}
