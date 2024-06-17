package io.github.itzispyder.healthindicators;

import net.minecraft.client.MinecraftClient;

public interface Global {

    MinecraftClient mc = MinecraftClient.getInstance();

    String modId = "healthindicators";
    String[] screens = {
        "assets/healthindicators/improperui/screen.ui"
    };
}
