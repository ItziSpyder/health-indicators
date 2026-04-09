package io.github.itzispyder.healthindicators.utils;

import com.mojang.authlib.GameProfile;
import io.github.itzispyder.healthindicators.Global;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class PlayerUtils implements Global {

    public static boolean invalid() {
        return mc == null || mc.player == null || mc.level == null || mc.gameMode == null || mc.options == null;
    }

    public static boolean valid() {
        return !invalid();
    }

    public static boolean playerValid(Player player) {
        if (invalid() || player == null) {
            return false;
        }

        LocalPlayer p = mc.player;
        GameProfile profile = player.getGameProfile();
        PlayerInfo entry = p.connection.getPlayerInfo(profile.id());

        return entry != null;
    }

    public static LocalPlayer player() {
        return mc.player;
    }

    public static float getEntityNameLabelHeight(Entity entity, float tickDelta) {
        float yaw = entity.getViewYRot(tickDelta);
        Vec3 vec = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, yaw);
        return (float)(vec == null ? 0.5 : vec.y + 0.5);
    }
}