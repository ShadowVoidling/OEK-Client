package com.oekclient;

/** Each utility is an independently switchable module. */
public enum Module {
    ORE_RADAR("Ore Radar", "Show nearby client-known ores", true),
    COORDINATES("Coordinates", "Current player block position", true),
    COMPASS("Compass", "Cardinal facing direction", true),
    FPS("FPS Counter", "Performance counter", true),
    DURABILITY("Tool Alerts", "Warn when held tools nearly break", true),
    WAYPOINT("Waypoint", "Display saved waypoint and distance", true),
    ZOOM("Hold Zoom", "Hold Z to temporarily lower FOV", true),
    BRIGHTNESS("Brightness Boost", "Raise gamma to the normal max", false);

    public final String title;
    public final String description;
    public final boolean initialState;

    Module(String title, String description, boolean initialState) {
        this.title = title;
        this.description = description;
        this.initialState = initialState;
    }
}
