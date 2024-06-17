package io.github.itzispyder.healthindicators.utils;

import com.mojang.authlib.GameProfile;
import io.github.itzispyder.healthindicators.Global;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class PlayerUtils implements Global {

    public static boolean invalid() {
        return mc == null || mc.player == null || mc.world == null || mc.interactionManager == null || mc.options == null;
    }

    public static boolean valid() {
        return !invalid();
    }

    public static boolean playerValid(PlayerEntity player) {
        if (invalid() || player == null) {
            return false;
        }

        ClientPlayerEntity p = mc.player;
        GameProfile profile = player.getGameProfile();
        PlayerListEntry entry = p.networkHandler.getPlayerListEntry(profile.getId());

        return entry != null;
    }

    public static ClientPlayerEntity player() {
        return mc.player;
    }

    public static float getEntityNameLabelHeight(Entity entity, float tickDelta) {
        float yaw = entity.getYaw(tickDelta);
        Vec3d vec = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, yaw);
        return (float)(vec == null ? 0.5 : vec.y + 0.5);
    }
}