package Constants;

public abstract class GameConstants {
    // game size
    public static int MY_WIDTH = 1024;
    public static int MY_HEIGHT = 768;

    public static int V_GRANULES = 90;
    public static int H_GRANULES = 100;

    // and how big are they?
    public static int V_GRANULE_SIZE = 50;
    public static int H_GRANULE_SIZE = 50;

    // map
    public static int V_MAP_GRANULE_SIZE = 5;
    public static int H_MAP_GRANULE_SIZE = 5;
    public static int RACCOON_FACE_HEIGHT = 20;
    public static int RACCOON_FACE_WIDTH = 20;

    // PLAYER CONSTANTS
    // player size
    public static int PLAYER_SIZE_X = 10;
    public static int PLAYER_SIZE_Y = 10;
    // steps
    public static int STEP_SOUND_RADIUS = 300;
    public static int STEP_RADIUS_INCR = 5;
    // maximum 5 seconds of sprint
    public static int MAX_SPRINT_DURATION = 300;
    public static int DIG_TIME = 90;

    // camera
    public static float CAMERA_SPEED = 10.0f;

    // enemy constants
    public static int START_NUMB_ENEMIES = 7;
    public static float VISION_SIZE = 500.0f;
    public static int CONE_ANGLE = 50;
    public static float ENEMY_MAX_SPEED = 2f;
    public static int CATCH_RADIUS = 20;

    // objects
    public static float OBJECT_PICKUP_RADIUS = 20f;
    public static float ESCAPE_AREA_SIZE = 150f;
    public static int VENT_WIDTH = 100;
    public static int VENT_HEIGHT = 100;

    // random
    public static int LOST_FADE_IN_TIME = 150;
    public static int START_LEVEL_FADE_IN_TIME = 150;
    public static int WON_FADE_IN_TIME = 150;

    // digging
    public static int DIG_SOUND_RADIUS = 1000;

    // HUD
    public static int HUD_X = 10;
    public static int HUD_Y = 30;
    public static int SPRINT_BAR_WIDTH = 400;
    public static int SPRINT_BAR_HEIGHT = 20;
    public static int COMPASS_X = MY_WIDTH - 90;
    public static int COMPASS_Y = MY_HEIGHT - 90;

    // ammo
    public static int AMMO_PER_LEVEL = 10;
    public static float ROCK_SPEED = 5;
}
