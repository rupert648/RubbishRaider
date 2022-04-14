import processing.core.PApplet;
import processing.core.PVector;

import java.util.Random;
import Level.MapGenerationNode;

public class Level {
    PApplet app;

    private int[][] map;

    public Level(PApplet app) {
        this.app = app;

        map = new int[RubbishRaider.V_GRANULES][RubbishRaider.H_GRANULES];
    }
    
    public void generateLevel() {
        MapGenerationNode start = new MapGenerationNode(new PVector(0,0), new PVector(RubbishRaider.H_GRANULES-1, RubbishRaider.V_GRANULES-1));

        generationRecur(start, 0);

        // ensure edges are walls

        for (int i = 0; i < RubbishRaider.H_GRANULES; i++) {
            map[0][i] = RubbishRaider.WALL;
            map[RubbishRaider.V_GRANULES-1][i] = RubbishRaider.WALL;
        }
        // ensure edges are walls
        for (int i = 0; i < RubbishRaider.V_GRANULES; i++) {
            map[i][0] = RubbishRaider.WALL;
            map[i][RubbishRaider.H_GRANULES - 1] = RubbishRaider.WALL;
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
        boolean isValid = false;
        int colToSplit = 0;


        int count = 0;
        int numbOfAttempts = 100;

        while (!isValid && count < numbOfAttempts) {
            count++;

            colToSplit = (int) current.topLeft.x + random.nextInt((int) (current.bottomRight.x - current.topLeft.x));
            if (colToSplit == 0) colToSplit++;
            // check that no row next to it filled in
            // (helps prevent stuck play areas)
            isValid = true;
            for (int i = (int) current.topLeft.y; i < current.bottomRight.y; i++) {

                // get distance to wall on left & right
                int distRight = 0;
                int distLeft = 0;
                while (colToSplit+distLeft < map[0].length
                        && map[i][colToSplit+distLeft] != RubbishRaider.WALL
                        // check below
                        && colToSplit+distLeft < current.bottomRight.x
                ) {
                    System.out.println("distLeft  " + distLeft  );
                    distLeft++;
                }

                while (colToSplit-distRight >= 0
                        && map[i][colToSplit-distRight] != RubbishRaider.WALL
                        // check below
                        && colToSplit-distRight > current.topLeft.x
                ) {
                    System.out.println("distRight  " + distRight );
                    distRight++;
                }

                // these variables control the minimum height of the room
                if (distRight < 15) {
                    isValid = false;
                    break;
                }
                if (distLeft < 15) {
                    isValid = false;
                    break;
                }
            }
        }

        if (count >= numbOfAttempts ) {
            return;
        }

        // draw col in
        for (int i = (int) current.topLeft.y; i < current.bottomRight.y; i++) {
            map[i][colToSplit] = RubbishRaider.WALL;
        }
        // random 10 wide hole in wall
        // random 10 wide hole in wall
        int val1 = (int) current.topLeft.y + random.nextInt((int) (current.bottomRight.y - current.topLeft.y));
        int x = 0;
        int y = 0;
        int removed = 0;
        while (val1 + x < current.bottomRight.y && removed < 10) {
            int c = val1 + x;
            map[c][colToSplit] = RubbishRaider.EMPTY;

            x++;
            removed++;
        }
        while (val1 - y > current.topLeft.y && removed < 10) {
            int c = val1 - y;
            map[c][colToSplit] = RubbishRaider.EMPTY;

            y++;
            removed++;
        }

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

    private void horizontalGeneration(MapGenerationNode current, int depth, Random random) {
        boolean isValid = false;
        int rowToSplit = 0;

        int count = 0;
        int numbOfAttempts = 100;

        // maximum of 10 attempts
        while (!isValid && count < numbOfAttempts) {
            count++;

            rowToSplit = (int) current.topLeft.y + random.nextInt((int) (current.bottomRight.y - current.topLeft.y));
            if (rowToSplit == 0) rowToSplit++;

            // check that no row next to it filled in
            // (helps prevent stuck play areas)
            isValid = true;

            for (int i = (int) current.topLeft.x; i < current.bottomRight.x; i++) {

                // get distance to wall on left & right
                int distAbove = 0;
                int distBelow = 0;
                while (rowToSplit+distBelow < map.length
                    && map[rowToSplit+distBelow][i] != RubbishRaider.WALL
                    && rowToSplit+distBelow < current.bottomRight.y
                ) {                     System.out.println("loop 1" );
                    distBelow++; }

                while (rowToSplit-distAbove >= 0
                    && map[rowToSplit-distAbove][i] != RubbishRaider.WALL
                    && rowToSplit-distAbove > current.topLeft.y
                ) {
                    System.out.println("loop 2" );
                    distAbove++;
                }

                // these variables control the minimum height of the room
                if (distAbove < 15) {
                    isValid = false;
                    break;
                }
                if (distBelow < 15) {
                    isValid = false;
                    break;
                }
            }
        }


        if (count >= numbOfAttempts ) {
            System.out.println("in here");
            return;
        }

        // draw row in
        for (int i = (int) current.topLeft.x; i < current.bottomRight.x; i++) {
            map[rowToSplit][i] = RubbishRaider.WALL;
        }
        // random 10 wide hole in wall
        int val1 = (int) current.topLeft.x + random.nextInt((int) (current.bottomRight.x - current.topLeft.x));
        int x = 0;
        int y = 0;
        int removed = 0;
        while (val1 + x < current.bottomRight.x && removed < 10) {
            int c = val1 + x;
            map[rowToSplit][c] = RubbishRaider.EMPTY;

            x++;
            removed++;
        }
        while (val1 - y > current.topLeft.x && removed < 10) {
            int c = val1 - y;
            map[rowToSplit][c] = RubbishRaider.EMPTY;

            y++;
            removed++;
        }

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
    
    public void render() {
        app.fill(0);
        for (int row = 0; row < RubbishRaider.V_GRANULES; row++) {
            for (int col = 0; col < RubbishRaider.H_GRANULES; col++) {
                if (map[row][col] == RubbishRaider.WALL) {

                    // check surrounding for air space
                    if ((row+1 < map.length && map[row+1][col] == RubbishRaider.EMPTY) ||
                            (row-1 >= 0 && map[row-1][col] == RubbishRaider.EMPTY)  ||
                            (col+1 < map[0].length && map[row][col+1] == RubbishRaider.EMPTY) ||
                            (col-1 >= 0 && map[row][col-1] == RubbishRaider.EMPTY)
                    ) {
                        app.stroke(0);
                        app.fill(0);
                    }
                    else {
                        app.stroke(255, 140, 0);
                        app.fill(255, 140, 0);
                    }
                    app.rect(col * RubbishRaider.H_GRANULE_SIZE, row * RubbishRaider.V_GRANULE_SIZE,
                            RubbishRaider.H_GRANULE_SIZE, RubbishRaider.V_GRANULE_SIZE);
                }
            }
        }
        app.fill(0);
        app.stroke(0);
    }
}
