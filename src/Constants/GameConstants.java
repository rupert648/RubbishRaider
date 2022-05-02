package Constants;

public abstract class GameConstants {
    // game size
    public static int MY_WIDTH = 1024;
    public static int MY_HEIGHT = 768;

    public static int V_GRANULES = 90;
    public static int H_GRANULES = 100;

    // and how big are they?
    public static int V_GRANULE_SIZE = 20;
    public static int H_GRANULE_SIZE = 20;

    // PLAYER CONSTANTS
    // player size
    public static int PLAYER_SIZE_X = 10;
    public static int PLAYER_SIZE_Y = 10;
    // steps
    public static int STEP_SOUND_RADIUS = 150;
    public static int STEP_RADIUS_INCR = 5;

    // camera
    public static float CAMERA_SPEED = 10.0f;

    // enemy constants
    public static float VISION_SIZE = 200.0f;
    public static int CONE_ANGLE = 50;
}
