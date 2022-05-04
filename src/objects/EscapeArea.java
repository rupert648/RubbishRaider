package objects;

import Characters.Player;
import Constants.GameConstants;
import processing.core.PVector;

public class EscapeArea {

    public PVector position;

    public EscapeArea(int x, int y) {
        position = new PVector(x, y);
    }

    public boolean playerInArea(Player player) {
        PVector playerPos = player.position;
        if (playerPos.x >= position.x &&         // right of the left edge AND
                playerPos.x <= position.x + GameConstants.ESCAPE_AREA_SIZE &&    // left of the right edge AND
                playerPos.y >= position.y &&         // below the top AND
                playerPos.y <= position.y + GameConstants.ESCAPE_AREA_SIZE) {    // above the bottom
            return true;
        }
        return false;
    }
}
