package Constants;

public abstract class GameConstants {
    // game size
    public static int MY_WIDTH = 1024;
    public static int MY_HEIGHT = 768;

    public static int V_GRANULES = 90;
    public static int H_GRANULES = 100;

    // and how big are they?
    public static int V_GRANULE_SIZE = 25;
    public static int H_GRANULE_SIZE = 25;

    // PLAYER CONSTANTS
    // player size
    public static int PLAYER_SIZE_X = 10;
    public static int PLAYER_SIZE_Y = 10;
    // steps
    public static int STEP_SOUND_RADIUS = 300;
    public static int STEP_RADIUS_INCR = 5;

    // camera
    public static float CAMERA_SPEED = 10.0f;

    // enemy constants
    public static int START_NUMB_ENEMIES = 3;
    public static float VISION_SIZE = 200.0f;
    public static int CONE_ANGLE = 50;
    public static float ENEMY_MAX_SPEED = 2f;
    public static int CATCH_RADIUS = 20;

    // objects
    public static float OBJECT_PICKUP_RADIUS = 20f;
    public static float ESCAPE_AREA_SIZE = 150f;
    public static int BED_WIDTH = 200;
    public static int BED_HEIGHT = 220;
    public static int BED_INTERACT_AREA = 100;

    // random
    public static int LOST_FADE_IN_TIME = 150;
    public static int START_LEVEL_FADE_IN_TIME = 150;
    public static int WON_FADE_IN_TIME = 150;

    // TODO: digging (makes a lot of noise)

    // HUD
    public static int HUD_X = 10;
    public static int HUD_Y = 30;

    // ammo
    public static int AMMO_PER_LEVEL = 10;
    public static float ROCK_SPEED = 5;
}
