package Level;

import processing.core.PVector;

public class MapGenerationNode {
    public MapGenerationNode parent;
    public MapGenerationNode leftChild;
    public MapGenerationNode rightChild;

    public PVector topLeft;
    public PVector bottomRight;

    public MapGenerationNode(PVector topLeft, PVector bottomRight) {
        leftChild = null;
        rightChild = null;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public int getSize() {
        int horizontal = (int) (bottomRight.x - topLeft.x) + 1;
        int vertical = (int) (bottomRight.y - topLeft.y) + 1;

        return horizontal * vertical;
    }

    public void setParent(MapGenerationNode parent) {
        this.parent = parent;
    }

    public void setLeftChild(MapGenerationNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(MapGenerationNode rightChild) {
        this.rightChild = rightChild;
    }
}
