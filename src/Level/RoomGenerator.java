package Level;

import processing.core.PVector;

import java.util.Arrays;
import java.util.Random;

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
