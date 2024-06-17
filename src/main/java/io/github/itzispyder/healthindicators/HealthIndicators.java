package io.github.itzispyder.healthindicators;

import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.client.MenuCallbacks;
import io.github.itzispyder.improperui.ImproperUIAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static io.github.itzispyder.healthindicators.Global.modId;
import static io.github.itzispyder.healthindicators.Global.screens;

public class HealthIndicators implements ModInitializer {

    public static final KeyBinding BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "binds.autoclicker.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            "binds.autoclicker"
    ));

    @Override
    public void onInitialize() {
        ImproperUIAPI.init(modId, HealthIndicators.class, screens);
        Config.update();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Config.update();
            while (BIND.wasPressed()) {
                ImproperUIAPI.parseAndRunFile(modId, "screen.ui", new MenuCallbacks());
            }
        });
    }
}
