import processing.core.PVector;

public class Camera {

    public PVector position;

    public Camera() {
        position = new PVector(0,0);
    }

    public void render(Level current) {
        current.render(position);
    }
}
