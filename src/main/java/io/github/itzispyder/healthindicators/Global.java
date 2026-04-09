package io.github.itzispyder.healthindicators;

import net.minecraft.client.Minecraft;

public interface Global {

    Minecraft mc = Minecraft.getInstance();

    String modId = "healthindicators";
    String[] screens = {
        "assets/healthindicators/improperui/screen.ui"
    };
}
