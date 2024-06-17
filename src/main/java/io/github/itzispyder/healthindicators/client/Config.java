package io.github.itzispyder.healthindicators.client;

import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.script.ScriptParser;

public class Config implements Global {

    public static float offsetY;
    public static float scale;
    public static boolean noNPC;
    public static boolean showOwn;
    public static boolean enabled;

    public static void update() {
        enabled = readBool("enabled", true);
        offsetY = (float) readDouble("offset-y", 0.0);
        scale = (float) readDouble("scale", 1.0);
        noNPC = readBool("no-npc", true);
        showOwn = readBool("show-own", true);
    }

    public static ConfigKey getKey(String property) {
        return new ConfigKey(modId, "config.properties", property);
    }

    public static Properties.Value read(String property) {
        return ScriptParser.getCache(modId).getProperty(getKey(property));
    }

    public static void write(String property, Object value) {
        ScriptParser.getCache(modId).setProperty(getKey(property), value, true);
    }

    public static boolean readBool(String property, boolean def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toBool() : def;
    }

    public static int readInt(String property, int def) {
        return (int)readDouble(property, def);
    }

    public static double readDouble(String property, double def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toDouble() : def;
    }

    public static String readStr(String property, String def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toString() : def;
    }
}