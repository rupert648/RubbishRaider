package PathFinding;

import Constants.GameConstants;
import Level.Level;
import Level.TileType;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections ;

public class AStarSearch {
    PApplet applet;
    // Graph represented as an array. This will make querying adjacent nodes easy.
    private AStarNode[][] graph ;
    // The open list, from which we will pluck the next node at each iteration
    private ArrayList<AStarNode>open ;

    // The constructor takes in the game map and builds the graph
    public AStarSearch(Level level, PApplet applet) {
        this.applet = applet;
        TileType[][] map = level.getMap();

        graph = new AStarNode[map.length][] ;
        for (int row = 0; row < map.length; row++) {
            graph[row] = new AStarNode[map[row].length] ;
            for (int col = 0; col < map[row].length; col++) {
                // if not wall, valid path
                if (map[row][col] != TileType.WALL)
                    graph[row][col] = new AStarNode(row, col) ;
                else
                    graph[row][col] = null ;
            }
        }
    }

    // resets ready for a new search. Avoids lots of object reconstruction
    private void reset() {
        for (int row = 0; row < graph.length; row++) {
            for (int col = 0; col < graph[row].length; col++) {
                if (graph[row][col] != null)
                    graph[row][col].reset() ;
            }
        }
    }

    // Process a node adjacent to the current node
    private void process(AStarNode curr, AStarNode node, int currRow, int currCol, PVector goalPos, int newCost) {
        // if node is null then there was a wall in the visited direction
        if (node == null) return ;
        // if node is closed nothing to do
        if (node.isClosed()) return ;
        // We've been here before
        if (node.isVisited()) {
            if (node.getCost() > newCost) {
                node.setCost(newCost) ;
                node.setPrevNode(curr) ;
            }
        }
        // This node was unvisited
        else {
            node.setCost(newCost) ;
            node.makeEstimate(goalPos) ;
            node.setPrevNode(curr) ;
            node.setVisited() ;
            open.add(node) ;
        }
    }

    // returns true if goal node is the first thing on the open list
    // Otherwise processes adjacent nodes
    private boolean searchIteration(int goalRow, int goalCol, PVector goalPos) {
        Collections.sort(open) ;
        AStarNode currentNode = open.remove(0) ;
        // if this is the goal node we are done.
        if (currentNode.hasCoords(goalRow, goalCol))
            return true ;
        // Iterate over reachable nodes.
        int currRow = currentNode.getRow() ;
        int currCol = currentNode.getCol() ;

        // use distance between the nodes as cost
        int newCostV = currentNode.getCost() + GameConstants.V_GRANULE_SIZE ;
        int newCostH = currentNode.getCost() + GameConstants.H_GRANULE_SIZE ;
        // look N
        process(currentNode, graph[currRow-1][currCol], currRow, currCol, goalPos, newCostH) ;
        // look NW
        // can only check this if the north and west squares both aren't blocked
        if (graph[currRow-1][currCol] != null && graph[currRow][currCol-1] != null)
            process(currentNode, graph[currRow-1][currCol-1], currRow, currCol, goalPos, newCostH);
        // look NE
        // can only check this if the north and east squares both aren't blocked
        if (graph[currRow-1][currCol] != null && graph[currRow][currCol+1] != null)
            process(currentNode, graph[currRow-1][currCol-1], currRow, currCol, goalPos, newCostH);
        // look S
        process(currentNode, graph[currRow+1][currCol], currRow, currCol, goalPos, newCostH) ;
        // look SW
        // can only check this if the south and west squares both aren't blocked
        if (graph[currRow+1][currCol] != null && graph[currRow][currCol-1] != null)
            process(currentNode, graph[currRow-1][currCol-1], currRow, currCol, goalPos, newCostH);
        // look SE
        // can only check this if the south and east squares both aren't blocked
        if (graph[currRow+1][currCol] != null && graph[currRow][currCol+1] != null)
            process(currentNode, graph[currRow-1][currCol-1], currRow, currCol, goalPos, newCostH);
        // look E
        process(currentNode, graph[currRow][currCol+1], currRow, currCol, goalPos, newCostV) ;
        // look W
        process(currentNode, graph[currRow][currCol-1], currRow, currCol, goalPos, newCostV) ;
        // This node now done and can be closed
        currentNode.close() ;
        return false ;
    }

    // Extract path by tracing the prevNode fields of AStarNode
    private ArrayList<AStarNode> extractPath(int sourceRow, int sourceCol, int goalRow, int goalCol) {
        ArrayList<AStarNode>path = new ArrayList<AStarNode>() ;
        AStarNode currNode = graph[goalRow][goalCol] ;
        do {
            path.add(currNode) ;
            currNode = currNode.getPrevNode() ;
        } while (currNode != null) ;
        return path ;
    }

    // Start the A* search for a path between the specified points
    public ArrayList<AStarNode> search(int sourceRow, int sourceCol, int goalRow, int goalCol) {
        reset() ;
        // initialise the open list
        open = new ArrayList<AStarNode>();
        open.add(graph[sourceRow][sourceCol]);
        graph[sourceRow][sourceCol].setCost(0);

        // calculate goal position
        // saves us having to calculate it every iteration
        int xPos = goalCol * GameConstants.H_GRANULE_SIZE+GameConstants.H_GRANULE_SIZE/2 ;
        int yPos = goalRow * GameConstants.V_GRANULE_SIZE+ GameConstants.V_GRANULE_SIZE/2 ;
        PVector goalPos = new PVector(xPos, yPos);

        // Continue until the open list is empty (which may indicate failure), or the goal is the first thing on open
        while(!open.isEmpty()) {
            if (searchIteration(goalRow, goalCol, goalPos)) {
                return extractPath(sourceRow, sourceCol, goalRow, goalCol);
            }
        }
        return null ;
    }
}
