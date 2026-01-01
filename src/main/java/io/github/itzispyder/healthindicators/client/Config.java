package io.github.itzispyder.healthindicators.client;

import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.improperui.ImproperUIAPI;
import io.github.itzispyder.improperui.config.ConfigReader;

public class Config implements Global {

    public static final ConfigReader CONFIG = ImproperUIAPI.getConfigReader(modId, "config.properties");

    public static float offsetY;
    public static float scale;
    public static boolean noNPC;
    public static boolean showOwn;
    public static boolean enabled;

    public static void update() {
        enabled = CONFIG.readBool("enabled", true);
        offsetY = (float) CONFIG.readDouble("offset-y", 0.0F);
        scale = (float) CONFIG.readDouble("scale", 1.0);
        noNPC = CONFIG.readBool("no-npc", true);
        showOwn = CONFIG.readBool("show-own", true);
    }
}