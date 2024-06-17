package io.github.itzispyder.healthindicators.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * PORTED FROM MINECRAFT
 */
@Environment(EnvType.CLIENT)
public enum HeartType {
    CONTAINER(Identifier.ofVanilla("textures/gui/sprites/hud/heart/container.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_hardcore.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_hardcore_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_hardcore.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/container_hardcore_blinking.png")),
    NORMAL(Identifier.ofVanilla("textures/gui/sprites/hud/heart/full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/half_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/hardcore_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/hardcore_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/hardcore_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/hardcore_half_blinking.png")),
    POISONED(Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_half_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_hardcore_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_hardcore_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_hardcore_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/poisoned_hardcore_half_blinking.png")),
    WITHERED(Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_half_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_hardcore_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_hardcore_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_hardcore_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/withered_hardcore_half_blinking.png")),
    ABSORBING(Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_half_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_hardcore_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_hardcore_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_hardcore_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/absorbing_hardcore_half_blinking.png")),
    FROZEN(Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_half_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_hardcore_full.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_hardcore_full_blinking.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_hardcore_half.png"), Identifier.ofVanilla("textures/gui/sprites/hud/heart/frozen_hardcore_half_blinking.png"));

    private final Identifier fullTexture;
    private final Identifier fullBlinkingTexture;
    private final Identifier halfTexture;
    private final Identifier halfBlinkingTexture;
    private final Identifier hardcoreFullTexture;
    private final Identifier hardcoreFullBlinkingTexture;
    private final Identifier hardcoreHalfTexture;
    private final Identifier hardcoreHalfBlinkingTexture;

    HeartType(final Identifier fullTexture, final Identifier fullBlinkingTexture, final Identifier halfTexture, final Identifier halfBlinkingTexture, final Identifier hardcoreFullTexture, final Identifier hardcoreFullBlinkingTexture, final Identifier hardcoreHalfTexture, final Identifier hardcoreHalfBlinkingTexture) {
        this.fullTexture = fullTexture;
        this.fullBlinkingTexture = fullBlinkingTexture;
        this.halfTexture = halfTexture;
        this.halfBlinkingTexture = halfBlinkingTexture;
        this.hardcoreFullTexture = hardcoreFullTexture;
        this.hardcoreFullBlinkingTexture = hardcoreFullBlinkingTexture;
        this.hardcoreHalfTexture = hardcoreHalfTexture;
        this.hardcoreHalfBlinkingTexture = hardcoreHalfBlinkingTexture;
    }

    public Identifier getTexture(boolean hardcore, boolean half, boolean blinking) {
        if (!hardcore) {
            if (half)
                return blinking ? this.halfBlinkingTexture : this.halfTexture;
            else
                return blinking ? this.fullBlinkingTexture : this.fullTexture;
        }
        else if (half)
            return blinking ? this.hardcoreHalfBlinkingTexture : this.hardcoreHalfTexture;
        else
            return blinking ? this.hardcoreFullBlinkingTexture : this.hardcoreFullTexture;
    }

    public static HeartType fromPlayerState(PlayerEntity player) {
        HeartType heartType;
        if (player.hasStatusEffect(StatusEffects.POISON))
            heartType = POISONED;
        else if (player.hasStatusEffect(StatusEffects.WITHER))
            heartType = WITHERED;
        else if (player.isFrozen())
            heartType = FROZEN;
        else
            heartType = NORMAL;
        return heartType;
    }
}