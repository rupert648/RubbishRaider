package Level;

import Characters.Enemy;
import Characters.Movable;
import Characters.Player;
import objects.Goal;
import objects.Vent;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

import Constants.*;

import static processing.core.PConstants.CORNER;

public class Level {
    final int NUMB_OF_ATTEMPTS = 100;
    final int ROOM_SIZE = 15;
    // object arrays following init
    public ArrayList<Vent> vents = new ArrayList<>();
    // room colours
    PApplet app;
    private TileType[][] map;

    public Level(PApplet app) {
        this.app = app;

        map = new TileType[GameConstants.V_GRANULES][GameConstants.H_GRANULES];
    }

    public void generateLevel(ArrayList<Enemy> enemies, Player player, Goal goal) {
        MapGenerationNode start = new MapGenerationNode(new PVector(0, 0), new PVector(GameConstants.H_GRANULES - 1, GameConstants.V_GRANULES - 1));

        generationRecur(start, 0);

        // create rooms
        RoomGenerator rg = new RoomGenerator(map);
        rg.fillRooms(start);
        rg.placeEnemies(start, enemies);
        rg.placePlayer(player);
        rg.placeGoal(goal);

        // get updated values
        map = rg.getMap();
        this.vents = rg.vents;

        // ensure edges are walls
        for (int i = 0; i < GameConstants.H_GRANULES; i++) {
            map[0][i] = TileType.PERIMITER;
            map[GameConstants.V_GRANULES - 1][i] = TileType.PERIMITER;
        }
        for (int i = 0; i < GameConstants.V_GRANULES; i++) {
            map[i][0] = TileType.PERIMITER;
            map[i][GameConstants.H_GRANULES - 1] = TileType.PERIMITER;
        }
    }

    void generationRecur(MapGenerationNode current, int depth) {
        if (current.getSize() <= 500) return;

        Random random = new Random();
        boolean horizontal = random.nextInt(2) == 0;

        // else split again
        if (horizontal) {
            horizontalGeneration(current, depth, random);
        } else {
            verticalGeneration(current, depth, random);
        }
    }

    private void verticalGeneration(MapGenerationNode current, int depth, Random random) {
        // pick random spot to split roughly in middle
        Integer colToSplit = getColToSplit(current, random);
        // failed to find valid split, so don't recurse anymore
        if (colToSplit == null) return;

        // draw col in
        for (int i = (int) current.topLeft.y; i < current.bottomRight.y; i++) {
            map[i][colToSplit] = TileType.WALL;
        }

        // make doorway
        buildHoleVertical(current, random, colToSplit);

        // split in two
        // left is top
        MapGenerationNode left = new MapGenerationNode(current.topLeft.copy(), new PVector(colToSplit, current.bottomRight.y));
        MapGenerationNode right = new MapGenerationNode(new PVector(colToSplit + 1, current.topLeft.y), current.bottomRight.copy());
        current.setLeftChild(left);
        current.setRightChild(right);

        // recur
        generationRecur(left, depth + 1);
        generationRecur(right, depth + 1);
    }

    private Integer getColToSplit(MapGenerationNode current, Random random) {
        boolean isValid = false;
        int colToSplit = 0;

        int count = 0;

        while (!isValid && count < NUMB_OF_ATTEMPTS) {
            count++;

            // grab a random column
            colToSplit = (int) current.topLeft.x + random.nextInt((int) (current.bottomRight.x - current.topLeft.x));

            if (colToSplit == 0) colToSplit++;
            // check that no row next to it filled in
            // (helps prevent stuck play areas)
            isValid = true;
            for (int i = (int) current.topLeft.y; i < current.bottomRight.y; i++) {

                // find how far to the closest wall on left
                int distLeft = getDistLeft(current, colToSplit, i);
                /// find how far to the closest wall on right
                int distRight = getDistRight(current, colToSplit, i);

                // these variables control the minimum height of the room
                if (distRight < ROOM_SIZE) {
                    isValid = false;
                    break;
                }
                if (distLeft < ROOM_SIZE) {
                    isValid = false;
                    break;
                }
            }
        }

        if (count >= NUMB_OF_ATTEMPTS) {
            return null;
        }
        return colToSplit;
    }

    private int getDistRight(MapGenerationNode current, int colToSplit, int i) {
        int distRight = 0;
        while (colToSplit - distRight >= 0
                && map[i][colToSplit - distRight] != TileType.WALL
                // check below
                && colToSplit - distRight > current.topLeft.x
        ) {
            distRight++;
        }
        return distRight;
    }

    private int getDistLeft(MapGenerationNode current, int colToSplit, int i) {
        // get distance to wall on left & right
        int distLeft = 0;
        while (colToSplit + distLeft < map[0].length
                && map[i][colToSplit + distLeft] != TileType.WALL
                // check below
                && colToSplit + distLeft < current.bottomRight.x
        ) {
            distLeft++;
        }
        return distLeft;
    }

    private void buildHoleVertical(MapGenerationNode current, Random random, int colToSplit) {
        int val1 = (int) current.topLeft.y + random.nextInt((int) (current.bottomRight.y - current.topLeft.y));
        int x = 0;
        int y = 0;
        int removed = 0;
        while (val1 + x < current.bottomRight.y && removed < 5) {
            int c = val1 + x;
            map[c][colToSplit] = TileType.EMPTY;

            x++;
            removed++;
        }
        while (val1 - y > current.topLeft.y && removed < 5) {
            int c = val1 - y;
            map[c][colToSplit] = TileType.EMPTY;

            y++;
            removed++;
        }
    }

    private void horizontalGeneration(MapGenerationNode current, int depth, Random random) {
        Integer rowToSplit = getRowToSplit(current, random);
        // if unable to find row to split, don't recurse
        if (rowToSplit == null) return;

        // draw row in
        for (int i = (int) current.topLeft.x; i < current.bottomRight.x; i++) {
            map[rowToSplit][i] = TileType.WALL;
        }

        buildHoleHorizontal(current, random, rowToSplit);

        // split in two
        // left is top
        MapGenerationNode left = new MapGenerationNode(current.topLeft.copy(), new PVector(current.bottomRight.x, rowToSplit));
        MapGenerationNode right = new MapGenerationNode(new PVector(current.topLeft.x, rowToSplit + 1), current.bottomRight.copy());
        current.setLeftChild(left);
        current.setRightChild(right);

        // recur
        generationRecur(left, depth + 1);
        generationRecur(right, depth + 1);
    }

    private Integer getRowToSplit(MapGenerationNode current, Random random) {
        boolean isValid = false;
        int rowToSplit = 0;

        int count = 0;

        // maximum of 10 attempts
        while (!isValid && count < NUMB_OF_ATTEMPTS) {
            count++;

            rowToSplit = (int) current.topLeft.y + random.nextInt((int) (current.bottomRight.y - current.topLeft.y));
            if (rowToSplit == 0) rowToSplit++;

            // check that no row next to it filled in
            // (helps prevent stuck play areas)
            isValid = true;

            for (int i = (int) current.topLeft.x; i < current.bottomRight.x; i++) {

                // get distance to wall on left & right
                int distBelow = getDistBelow(current, rowToSplit, i);
                int distAbove = getDistAbove(current, rowToSplit, i);

                // these variables control the minimum height of the room
                if (distAbove < ROOM_SIZE) {
                    isValid = false;
                    break;
                }
                if (distBelow < ROOM_SIZE) {
                    isValid = false;
                    break;
                }
            }
        }

        if (count >= NUMB_OF_ATTEMPTS) {
            return null;
        }
        return rowToSplit;
    }

    private int getDistBelow(MapGenerationNode current, int rowToSplit, int i) {
        int distBelow = 0;
        while (rowToSplit + distBelow < map.length
                && map[rowToSplit + distBelow][i] != TileType.WALL
                && rowToSplit + distBelow < current.bottomRight.y
        ) {
            distBelow++;
        }
        return distBelow;
    }

    private int getDistAbove(MapGenerationNode current, int rowToSplit, int i) {
        int distAbove = 0;
        while (rowToSplit - distAbove >= 0
                && map[rowToSplit - distAbove][i] != TileType.WALL
                && rowToSplit - distAbove > current.topLeft.y
        ) {
            distAbove++;
        }
        return distAbove;
    }

    private void buildHoleHorizontal(MapGenerationNode current, Random random, int rowToSplit) {
        // random 10 wide hole in wall
        int val1 = (int) current.topLeft.x + random.nextInt((int) (current.bottomRight.x - current.topLeft.x));
        int x = 0;
        int y = 0;
        int removed = 0;
        while (val1 + x < current.bottomRight.x && removed < 10) {
            int c = val1 + x;
            map[rowToSplit][c] = TileType.EMPTY;

            x++;
            removed++;
        }
        while (val1 - y > current.topLeft.x && removed < 10) {
            int c = val1 - y;
            map[rowToSplit][c] = TileType.EMPTY;

            y++;
            removed++;
        }
    }

    public void render(
            PVector cameraPosition,
            PImage KITCHEN_TILE,
            PImage BATHROOM_TILE,
            PImage BEDROOM_TILE,
            PImage LIVING_ROOM_TILE,
            PImage WALL_TILE,
            PImage DEFAULT_TILE
    ) {
        app.imageMode(CORNER);
        app.fill(0);
        for (int row = 0; row < GameConstants.V_GRANULES; row++) {
            for (int col = 0; col < GameConstants.H_GRANULES; col++) {

                TileType tile = map[row][col];

                float xPos = col * GameConstants.H_GRANULE_SIZE - cameraPosition.x;
                float yPos = row * GameConstants.V_GRANULE_SIZE - cameraPosition.y;

                switch (tile) {
                    case KITCHEN -> app.image(KITCHEN_TILE, xPos, yPos);
                    case BATHROOM -> app.image(BATHROOM_TILE, xPos, yPos);
                    case BEDROOM -> app.image(BEDROOM_TILE, xPos, yPos);
                    case LIVING_ROOM -> app.image(LIVING_ROOM_TILE, xPos, yPos);
                    case WALL -> app.image(WALL_TILE, xPos, yPos);
                    case PERIMITER -> app.image(WALL_TILE, xPos, yPos);
                    default -> app.image(DEFAULT_TILE, xPos, yPos);
                }
            }
        }
        app.fill(0);
    }

    // W
    // W* ---
    // W
    public boolean collidesXLeft(Movable object) {
        int charX = (int) object.position.x;
        int charCol = charX / GameConstants.H_GRANULE_SIZE;
        int charY = (int) object.position.y;
        int charRow = charY / GameConstants.V_GRANULE_SIZE;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int col = charCol - 1;
            int row = charRow + rowOffset;

            // check in bounds
            if (row >= map.length || row < 0 || col >= map[row].length || col < 0) {
                return false;
            }

            if (map[row][col] == TileType.WALL || map[row][col] == TileType.PERIMITER) {
                int blockX = col * GameConstants.H_GRANULE_SIZE;
                int blockY = row * GameConstants.V_GRANULE_SIZE;
                if (blockX - charX > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (charX - (blockX + GameConstants.H_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (blockY - charY > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                if (charY - (blockY + GameConstants.V_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                return true;
            }
        }
        return false;
    }

    //   W
    //  *W ---
    //   W
    public boolean collidesXRight(Movable object) {
        int charX = (int) object.position.x;
        int charCol = charX / GameConstants.H_GRANULE_SIZE;
        int charY = (int) object.position.y;
        int charRow = charY / GameConstants.V_GRANULE_SIZE;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int col = charCol + 1;
            int row = charRow + rowOffset;

            // check in bounds
            if (row >= map.length || row < 0 || col >= map[row].length || col < 0) {
                return false;
            }

            if (map[row][col] == TileType.WALL || map[row][col] == TileType.PERIMITER) {
                int blockX = col * GameConstants.H_GRANULE_SIZE;
                int blockY = row * GameConstants.V_GRANULE_SIZE;
                if (blockX - charX > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (charX - (blockX + GameConstants.H_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (blockY - charY > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                if (charY - (blockY + GameConstants.V_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                return true;
            }
        }
        return false;
    }

    // WWW
    //  * ---
    public boolean collidesYUp(Movable object) {
        int charX = (int) object.position.x;
        int charCol = charX / GameConstants.H_GRANULE_SIZE;
        int charY = (int) object.position.y;
        int charRow = charY / GameConstants.V_GRANULE_SIZE;

        for (int colOffset = -1; colOffset <= 1; colOffset++) {
            int col = charCol + colOffset;
            int row = charRow - 1;

            // check in bounds
            if (row >= map.length || row < 0 || col >= map[row].length || col < 0) {
                return false;
            }

            if (map[row][col] == TileType.WALL || map[row][col] == TileType.PERIMITER) {
                int blockX = col * GameConstants.H_GRANULE_SIZE;
                int blockY = row * GameConstants.V_GRANULE_SIZE;
                if (blockX - charX > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (charX - (blockX + GameConstants.H_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (blockY - charY > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                if (charY - (blockY + GameConstants.V_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                return true;
            }
        }
        return false;
    }


    // Still a problem. If have: x
    //                          WW  and travelling SW st that overlap both W at once
    // then will reverse both. Naturally want the y only to be reversed here.
    // But if had:  Wx
    //              WW _would_ want both reversed and:
    // if had x
    //       W   travelling SW also want them both reversed!
    // Solution seems to be rules mapping the above patterns to decisions

    //  *
    // WWW ---
    public boolean collidesYDown(Movable object) {
        int charX = (int) object.position.x;
        int charCol = charX / GameConstants.H_GRANULE_SIZE;
        int charY = (int) object.position.y;
        int charRow = charY / GameConstants.V_GRANULE_SIZE;

        for (int colOffset = -1; colOffset <= 1; colOffset++) {
            int col = charCol + colOffset;
            int row = charRow + 1;

            // check in bounds
            if (row >= map.length || row < 0 || col >= map[row].length || col < 0) {
                return false;
            }

            if (map[row][col] == TileType.WALL || map[row][col] == TileType.PERIMITER) {
                int blockX = col * GameConstants.H_GRANULE_SIZE;
                int blockY = row * GameConstants.V_GRANULE_SIZE;
                if (blockX - charX > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (charX - (blockX + GameConstants.H_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_X / 2)
                    continue;
                if (blockY - charY > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                if (charY - (blockY + GameConstants.V_GRANULE_SIZE) > GameConstants.PLAYER_SIZE_Y / 2)
                    continue;
                return true;
            }
        }
        return false;
    }

    public TileType[][] getMap() {
        return this.map;
    }
}
