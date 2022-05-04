package Level;

import Characters.Enemy;
import Characters.Player;
import Constants.GameConstants;
import objects.Goal;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class RoomGenerator {

    // todo: need to maintain this, could tidy up data structures to better represent furniture tiles
    TileType[] furnitureTiles = {
            TileType.BED_DUVET,
            TileType.BED_PILLOW,
            TileType.CABINET
    };

    private TileType[][] map;

    public RoomGenerator(TileType[][] map) {
        this.map = map;
    }

    public TileType[][] getMap() {
        return map;
    }

    public void setMap(TileType[][] map) {
        this.map = map;
    }

    public void fillRooms(MapGenerationNode root) {
        int depth = 0;
        treeRecurse(root, depth);
    }

    public void placeEnemies(MapGenerationNode root, ArrayList<Enemy> enemies) {
        // get list of rooms
        ArrayList<MapGenerationNode> leaves = new ArrayList<>();

        Stack<MapGenerationNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            MapGenerationNode node = stack.pop();
            if (node.leftChild != null)
                stack.push(node.leftChild);
            if (node.rightChild != null)
                stack.push(node.rightChild);
            if (node.rightChild == null && node.leftChild == null)
                leaves.add(node);
        }

        // place enemy in random room in middle
        for (Enemy enemy: enemies) {
            Random random = new Random();

            int roomNo = random.nextInt(leaves.size());

            // middle of room
            MapGenerationNode room = leaves.get(roomNo);
            float col = room.topLeft.x + ((room.bottomRight.x - room.topLeft.x) / 2);
            float row = room.topLeft.y + ((room.bottomRight.y - room.topLeft.y) / 2);

            // convert columns into map positions
            float xPos = col * GameConstants.H_GRANULE_SIZE;
            float yPos = row * GameConstants.V_GRANULE_SIZE;

            enemy.position.x = xPos;
            enemy.position.y = yPos;
        }
    }

    void placePlayer(Player player) {
        // for now, place in the top left
        int col = 1;
        int row = 1;

        // convert columns into map positions
        float xPos = col * GameConstants.H_GRANULE_SIZE;
        float yPos = row * GameConstants.V_GRANULE_SIZE;

        player.position.x = xPos;
        player.position.y = yPos;
    }

    void placeGoal(Goal goal) {
        int col = map[0].length - 2;
        int row = map.length - 2;

        // convert columns into map positions
        float xPos = col * GameConstants.H_GRANULE_SIZE;
        float yPos = row * GameConstants.V_GRANULE_SIZE;

        goal.position.x = xPos;
        goal.position.y = yPos;
    }

    private void treeRecurse(MapGenerationNode current, int depth) {

        if (current.leftChild == null && current.rightChild == null) {
            // leaf node
            fillRoom(current, depth);
            return;
        }

        // search children
        if (current.leftChild != null) treeRecurse(current.leftChild, depth + 1);
        if (current.rightChild != null) treeRecurse(current.rightChild, depth + 1);

    }

    private void fillRoom(MapGenerationNode current, int depth) {
        TileType roomType = getRoomType(depth);

        // fill tiles within
        PVector topLeft = current.topLeft;
        PVector bottomRight = current.bottomRight;

        for (int i = (int) (topLeft.x ); i < bottomRight.x ; i++) {
            for (int j = (int) (topLeft.y); j < bottomRight.y ; j++) {
                map[j][i] = roomType;
            }
        }

        addFurniture(current, roomType);
    }

    private void addFurniture(MapGenerationNode current, TileType roomType) {
        if (roomType == TileType.BEDROOM) {

            Furniture bed = Furniture.bed();
            Furniture cupboard = Furniture.cupboard();
            // try and place in the top left of room
            Random rn = new Random();

            // TODO: ensure that furniture doesn't overlap

            // place bed
            int orientation = rn.nextInt(4);
            placeFurniture(current, bed, orientation);

            // place cupboard
            orientation = rn.nextInt(4);
            placeFurniture(current, cupboard, orientation);

        }
    }

    private static TileType getRoomType(int depth) {
        // fiddle with this to change size of rooms/what room is picked

        switch (depth) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: {
                Random rn = new Random();
                int val = rn.nextInt(4);
                switch (val) {
                    case 0: return TileType.LIVING_ROOM;
                    case 1: return TileType.KITCHEN;
                    case 2: return TileType.BEDROOM;
                }
            }
            default: {
                Random rn = new Random();
                int val = rn.nextInt(4);
                switch (val) {
                    case 0: return TileType.BEDROOM;
                    case 1: return TileType.BATHROOM;
                    case 2: return TileType.GENERIC;
                }
            }
        }

        return TileType.GENERIC;
    }

    public void placeFurniture(MapGenerationNode current, Furniture furniture, int orientation) {
        switch (orientation) {
            case 0 -> placeFurnitureNorth(current, furniture);
            case 1 -> placeFurnitureSouth(current, furniture);
            case 2 -> placeFurnitureEast(current, furniture);
            case 3 -> placeFurnitureWest(current, furniture);
        }
    }

    private void placeFurnitureWest(MapGenerationNode current, Furniture f) {
        Random rn = new Random();

        TileType[][] tileArray = f.tileArray;

        // random xPos
        int startX = (int) current.topLeft.x;
        int startY = -1;

        // iterate until find startY which doesn't overlap other furniture
        boolean isValid = false;
        while (!isValid) {
            // random Y
            startY = (int) current.topLeft.y +  rn.nextInt((int) (current.bottomRight.y - current.topLeft.y - tileArray.length));

            // check not next to door
            if (startX - 1 >= 0 && map[startY][startX-1] == TileType.EMPTY) {
                continue;
            }

            isValid = true;

            // iterate furniture array
            outerLoop:
            for (int i = 0; i < tileArray.length; i++) {
                for (int j = 0; j < tileArray[0].length; j++ ) {
                    if (isFurnitureTile(map[startY + j][startX + i])) {
                        isValid = false;
                        break outerLoop;
                    }
                }
            }
        }

        // place tiles
        for (int i = 0; i < tileArray.length; i++) {
            for (int j = 0; j < tileArray[0].length; j++ ) {
                TileType furnitureTile = tileArray[i][j];
                // map[row][column]
                map[startY + j][startX + i] = furnitureTile;
            }
        }
    }

    private void placeFurnitureEast(MapGenerationNode current, Furniture f) {
        Random rn = new Random();

        TileType[][] tileArray = f.tileArray;

        // random xPos
        int startX = (int) current.bottomRight.x - tileArray[0].length;
        int startY = -1;

        // iterate until find startY which doesn't overlap other furniture
        boolean isValid = false;
        while (!isValid) {
            // random Y
            startY = (int) current.topLeft.y +  rn.nextInt((int) (current.bottomRight.y - current.topLeft.y - tileArray.length));

            // check not next to door
            if (startX + 1 < map[0].length && map[startY][startX+1] == TileType.EMPTY) {
                continue;
            }

            isValid = true;

            // iterate furniture array
            outerLoop:
            for (int i = 0; i < tileArray.length; i++) {
                for (int j = 0; j < tileArray[0].length; j++ ) {
                    if (isFurnitureTile(map[startY + j][startX + (tileArray[0].length - 1 - i)])) {
                        isValid = false;
                        break outerLoop;
                    }
                }
            }
        }

        // iterate furniture array
        for (int i = 0; i < tileArray.length; i++) {
            for (int j = 0; j < tileArray[0].length; j++ ) {
                TileType furnitureTile = tileArray[i][j];
                // map[row][column]
                map[startY + j][startX + (tileArray[0].length - 1 - i)] = furnitureTile;
            }
        }
    }

    private void placeFurnitureSouth(MapGenerationNode current, Furniture f) {
        Random rn = new Random();

        TileType[][] tileArray = f.tileArray;

        // random xPos

        int startY = (int) current.bottomRight.y - tileArray.length;
        int startX = -1;

        // iterate until find startY which doesn't overlap other furniture
        boolean isValid = false;
        while (!isValid) {
            // random Y
            startX = (int) current.topLeft.x +  rn.nextInt((int) (current.bottomRight.x - current.topLeft.x - tileArray[0].length));

            // check not next to door
            // TODO: check this for every tile?
            if (startY + 1 < map.length && map[startY+1][startX] == TileType.EMPTY) {
                continue;
            }

            isValid = true;

            // iterate furniture array
            outerLoop:
            for (int i = tileArray.length - 1; i >= 0; i--) {
                for (int j = 0; j < tileArray[0].length; j++ ) {
                    if (isFurnitureTile(map[startY + (tileArray.length - 1 - i)][startX + j])) {
                        isValid = false;
                        break outerLoop;
                    }
                }
            }
        }

        // iterate furniture array
        for (int i = tileArray.length - 1; i >= 0; i--) {
            for (int j = 0; j < tileArray[0].length; j++ ) {
                TileType furnitureTile = tileArray[i][j];
                // map[row][column]
                map[startY + (tileArray.length - 1 - i)][startX + j] = furnitureTile;
            }
        }
    }

    private void placeFurnitureNorth(MapGenerationNode current, Furniture f) {
        Random rn = new Random();
        TileType[][] tileArray = f.tileArray;

        // random xPos
        int startY = (int) current.topLeft.y;
        int startX = -1;

        // iterate until find startY which doesn't overlap other furniture
        boolean isValid = false;
        while (!isValid) {
            // random Y
            startX = (int) current.topLeft.x +  rn.nextInt((int) (current.bottomRight.x - current.topLeft.x - tileArray[0].length));

            // check not next to door
            if (startY - 1 >= 0 && map[startY-1][startX] == TileType.EMPTY) {
                continue;
            }

            isValid = true;

            // iterate furniture array
            outerLoop:
            for (int i = 0; i < tileArray.length; i++) {
                for (int j = 0; j < tileArray[0].length; j++ ) {
                    if (isFurnitureTile(map[startY + i][startX + j])) {
                        isValid = false;
                        break outerLoop;
                    }
                }
            }
        }

        // iterate furniture array
        for (int i = 0; i < tileArray.length; i++) {
            for (int j = 0; j < tileArray[0].length; j++ ) {
                TileType furnitureTile = tileArray[i][j];
                // map[row][column]
                map[startY + i][startX + j] = furnitureTile;
            }
        }
    }

    private boolean isFurnitureTile(TileType t) {
        return Arrays.asList(furnitureTiles).contains(t);
    }

}
