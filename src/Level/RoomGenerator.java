package Level;

import processing.core.PVector;

import java.util.Random;

public class RoomGenerator {

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
            // only trying beds for now
            TileType[][] furnitureArray = Furniture.bed();
            // try and place in the top left of room
            // TODO: random pos against wall
            Random rn = new Random();

            int orientation = rn.nextInt(4);

            placeBed(current, furnitureArray, orientation);
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

    public void placeBed(MapGenerationNode current, TileType[][] furnitureArray, int orientation) {
        switch (orientation) {
            case 0 -> placeFurnitureNorth(current, furnitureArray);
            case 1 -> placeFurnitureSouth(current, furnitureArray);
            case 2 -> placeFurnitureEast(current, furnitureArray);
            case 3 -> placeFurnitureWest(current, furnitureArray);
        }
    }

    private void placeFurnitureWest(MapGenerationNode current, TileType[][] furnitureArray) {
        Random rn = new Random();
        // random xPos
        int startX = (int) current.topLeft.x;
        int startY = (int) current.topLeft.y +  rn.nextInt((int) (current.bottomRight.y - current.topLeft.y - furnitureArray.length));

        // iterate furniture array
        for (int i = 0; i < furnitureArray.length; i++) {
            for (int j = 0; j < furnitureArray[0].length; j++ ) {
                TileType furnitureTile = furnitureArray[i][j];
                // map[row][column]
                map[startY + j][startX + i] = furnitureTile;
            }
        }
    }

    private void placeFurnitureEast(MapGenerationNode current, TileType[][] furnitureArray) {
        Random rn = new Random();
        // random xPos
        int startX = (int) current.bottomRight.x - furnitureArray[0].length;
        int startY = (int) current.topLeft.y +  rn.nextInt((int) (current.bottomRight.y - current.topLeft.y - furnitureArray.length));

        // iterate furniture array
        for (int i = 0; i < furnitureArray.length; i++) {
            for (int j = 0; j < furnitureArray[0].length; j++ ) {
                TileType furnitureTile = furnitureArray[i][j];
                // map[row][column]
                map[startY + j][startX + (furnitureArray[0].length - 1 - i)] = furnitureTile;
            }
        }
    }

    private void placeFurnitureSouth(MapGenerationNode current, TileType[][] furnitureArray) {
        Random rn = new Random();
        // random xPos
        int startX = (int) current.topLeft.x +  rn.nextInt((int) (current.bottomRight.x - current.topLeft.x - furnitureArray[0].length));
        int startY = (int) current.bottomRight.y - furnitureArray.length;

        // iterate furniture array
        for (int i = furnitureArray.length - 1; i >= 0; i--) {
            for (int j = 0; j < furnitureArray[0].length; j++ ) {
                TileType furnitureTile = furnitureArray[i][j];
                // map[row][column]
                map[startY + (furnitureArray.length - 1 - i)][startX + j] = furnitureTile;
            }
        }
    }

    private void placeFurnitureNorth(MapGenerationNode current, TileType[][] furnitureArray) {
        Random rn = new Random();
        // random xPos
        int startX = (int) current.topLeft.x +  rn.nextInt((int) (current.bottomRight.x - current.topLeft.x - furnitureArray[0].length));
//        int startY = rn.nextBoolean() ? (int) current.topLeft.y : (int) current.bottomRight.y - furnitureArray.length;
        int startY = (int) current.topLeft.y;

        // iterate furniture array
        for (int i = 0; i < furnitureArray.length; i++) {
            for (int j = 0; j < furnitureArray[0].length; j++ ) {
                TileType furnitureTile = furnitureArray[i][j];
                // map[row][column]
                map[startY + i][startX + j] = furnitureTile;
            }
        }
    }

}
