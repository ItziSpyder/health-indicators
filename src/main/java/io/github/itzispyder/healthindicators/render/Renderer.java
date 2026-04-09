package io.github.itzispyder.healthindicators.render;

import com.mojang.blaze3d.vertex.*;
import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.utils.HeartType;
import io.github.itzispyder.healthindicators.utils.PlayerHealthData;
import io.github.itzispyder.healthindicators.utils.PlayerUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Renderer implements Global {

    private static final Map<UUID, PlayerHealthData> dataMap = new HashMap<>();
    private static final Random random = new Random();

    public static void render(PoseStack matrices, Player player, Quaternionf rotation) {
        if (Config.noNPC && !PlayerUtils.playerValid(player))
            return;

        float scale = 0.025F * Config.scale;
        float height = PlayerUtils.getEntityNameLabelHeight(player, 1.0F);
        float width = 80;
        float x = -width / 2;
        float y = -20 - Config.offsetY;

        matrices.pushPose();
        matrices.translate(0, height, 0);
        matrices.mulPose(rotation);
        matrices.scale(scale, -scale, scale);

        renderStatusBars(matrices, player, x, y);

        matrices.popPose();
    }

    private static void renderStatusBars(PoseStack matrices, Player player, float x, float y) {
        int ticks = mc.gui.getGuiTicks();
        UUID id = player.getGameProfile().id();
        PlayerHealthData data = dataMap.computeIfAbsent(id, uuid -> new PlayerHealthData());

        int lastHealth = Mth.ceil(player.getHealth());
        boolean blinking = data.heartJumpEndTick > (long)ticks && (data.heartJumpEndTick - (long)ticks) / 3L % 2L == 1L;
        long l = Util.getMillis();

        if (lastHealth < data.lastHealthValue && player.invulnerableTime > 0) {
            data.lastHealthCheckTime = l;
            data.heartJumpEndTick = ticks + 20;
        }
        else if (lastHealth > data.lastHealthValue && player.invulnerableTime > 0) {
            data.lastHealthCheckTime = l;
            data.heartJumpEndTick = ticks + 10;
        }

        if (l - data.lastHealthCheckTime > 1000L) {
            data.renderHealthValue = lastHealth;
            data.lastHealthCheckTime = l;
        }

        data.lastHealthValue = lastHealth;
        int health = data.renderHealthValue;
        float maxHealth = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(health, lastHealth));
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int lines = Math.max(10 - (Mth.ceil((maxHealth + (float)absorption) / 2.0F / 10.0F) - 2), 3);
        int regeneratingHeartIndex = -1;

        if (player.hasEffect(MobEffects.REGENERATION)) {
            regeneratingHeartIndex = ticks % Mth.ceil(maxHealth + 5.0F);
        }

        renderHealthBar(matrices, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    private static void renderHealthBar(PoseStack matrices, Player player, float x, float y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        HeartType heartType = HeartType.fromPlayerState(player);
        boolean hardcore = player.level().getLevelData().isHardcore();
        int maxHearts = Mth.ceil((double)maxHealth / 2.0);
        int absorptionHearts = Mth.ceil((double)absorption / 2.0);
        int maxHpPoints = maxHearts * 2;

        for (int i = maxHearts + absorptionHearts - 1; i >= 0; --i) {
            float renderX = x + (i % 10) * 8;
            float renderY = y - (int)(i / 10) * lines;
            int asHealthPoints = i * 2;

            if (lastHealth + absorption <= 4)
                renderY += random.nextInt(2);
            if (i < maxHearts && i == regeneratingHeartIndex)
                renderY -= 2;

            drawHeart(matrices, HeartType.CONTAINER, renderX, renderY, hardcore, blinking, false);

            if (i >= maxHearts) {
                int diff = asHealthPoints - maxHpPoints;
                if (diff < absorption) {
                    boolean half = diff + 1 == absorption;
                    drawHeart(matrices, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, renderX, renderY, hardcore, false, half);
                }
            }

            if (blinking && asHealthPoints < health)
                drawHeart(matrices, heartType, renderX, renderY, hardcore, true, asHealthPoints + 1 == health);

            if (asHealthPoints < lastHealth)
                drawHeart(matrices, heartType, renderX, renderY, hardcore, false, asHealthPoints + 1 == lastHealth);
        }
    }

    private static void drawHeart(PoseStack matrices, HeartType type, float x, float y, boolean hardcore, boolean blinking, boolean half) {
        Tesselator tes = Tesselator.getInstance();
        BufferBuilder buf = tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f mat = matrices.last().pose();
        float s = 9;

        buf.addVertex(mat, x, y, 0).setColor(0xFFFFFFFF).setUv(0, 0);
        buf.addVertex(mat, x, y + s, 0).setColor(0xFFFFFFFF).setUv(0, 1);
        buf.addVertex(mat, x + s, y + s, 0).setColor(0xFFFFFFFF).setUv(1, 1);
        buf.addVertex(mat, x + s, y, 0).setColor(0xFFFFFFFF).setUv(1, 0);

        RenderLayers.TEX_QUADS.apply(type.getTexture(hardcore, half, blinking)).draw(buf.buildOrThrow());
    }

    public static AbstractClientPlayer findMatchingPlayer(AvatarRenderState state) {
        if (state == null || mc.level == null)
            return null;

        for (AbstractClientPlayer player: mc.level.players())
            if (player.getId() == state.id)
                return player;
        return null;
    }
}
