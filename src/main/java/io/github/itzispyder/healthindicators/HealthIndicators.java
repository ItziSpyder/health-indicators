package io.github.itzispyder.healthindicators;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.client.MenuCallbacks;
import io.github.itzispyder.improperui.ImproperUIAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static io.github.itzispyder.healthindicators.Global.modId;
import static io.github.itzispyder.healthindicators.Global.screens;

public class HealthIndicators implements ModInitializer {

    public static final KeyMapping BIND = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "binds.healthindicators.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("healthindicators", "binds.healthindicators"))
    ));

    @Override
    public void onInitialize() {
        ImproperUIAPI.init(modId, HealthIndicators.class, screens);
        Config.update();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Config.update();
            while (BIND.consumeClick()) {
                ImproperUIAPI.parseAndRunFile(modId, "screen.ui", new MenuCallbacks());
            }
        });
    }
}
