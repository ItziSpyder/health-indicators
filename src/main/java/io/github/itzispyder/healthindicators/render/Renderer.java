package io.github.itzispyder.healthindicators.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.utils.HeartType;
import io.github.itzispyder.healthindicators.utils.PlayerHealthData;
import io.github.itzispyder.healthindicators.utils.PlayerUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Renderer implements Global {

    private static final Map<UUID, PlayerHealthData> dataMap = new HashMap<>();
    private static final Random random = new Random();

    public static void render(MatrixStack matrices, PlayerEntity player, Quaternionf rotation) {
        if (Config.noNPC && !PlayerUtils.playerValid(player))
            return;

        float scale = 0.025F * Config.scale;
        float height = PlayerUtils.getEntityNameLabelHeight(player, 1.0F);
        float width = 80;
        float x = -width / 2;
        float y = -20 - Config.offsetY;

        matrices.push();
        matrices.translate(0, height, 0);
        matrices.multiply(rotation);
        matrices.scale(scale, -scale, scale);

        renderStatusBars(matrices, player, x, y);

        matrices.pop();
    }

    private static void renderStatusBars(MatrixStack matrices, PlayerEntity player, float x, float y) {
        int ticks = mc.inGameHud.getTicks();
        UUID id = player.getGameProfile().id();
        PlayerHealthData data = dataMap.computeIfAbsent(id, uuid -> new PlayerHealthData());

        int lastHealth = MathHelper.ceil(player.getHealth());
        boolean blinking = data.heartJumpEndTick > (long)ticks && (data.heartJumpEndTick - (long)ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();

        if (lastHealth < data.lastHealthValue && player.timeUntilRegen > 0) {
            data.lastHealthCheckTime = l;
            data.heartJumpEndTick = ticks + 20;
        }
        else if (lastHealth > data.lastHealthValue && player.timeUntilRegen > 0) {
            data.lastHealthCheckTime = l;
            data.heartJumpEndTick = ticks + 10;
        }

        if (l - data.lastHealthCheckTime > 1000L) {
            data.renderHealthValue = lastHealth;
            data.lastHealthCheckTime = l;
        }

        data.lastHealthValue = lastHealth;
        int health = data.renderHealthValue;
        float maxHealth = Math.max((float)player.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(health, lastHealth));
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int lines = Math.max(10 - (MathHelper.ceil((maxHealth + (float)absorption) / 2.0F / 10.0F) - 2), 3);
        int regeneratingHeartIndex = -1;

        if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
            regeneratingHeartIndex = ticks % MathHelper.ceil(maxHealth + 5.0F);
        }

        renderHealthBar(matrices, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    private static void renderHealthBar(MatrixStack matrices, PlayerEntity player, float x, float y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        HeartType heartType = HeartType.fromPlayerState(player);
        boolean hardcore = player.getEntityWorld().getLevelProperties().isHardcore();
        int maxHearts = MathHelper.ceil((double)maxHealth / 2.0);
        int absorptionHearts = MathHelper.ceil((double)absorption / 2.0);
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

    private static void drawHeart(MatrixStack matrices, HeartType type, float x, float y, boolean hardcore, boolean blinking, boolean half) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        Matrix4f mat = matrices.peek().getPositionMatrix();
        float s = 9;

        buf.vertex(mat, x, y, 0).color(0xFFFFFFFF).texture(0, 0);
        buf.vertex(mat, x, y + s, 0).color(0xFFFFFFFF).texture(0, 1);
        buf.vertex(mat, x + s, y + s, 0).color(0xFFFFFFFF).texture(1, 1);
        buf.vertex(mat, x + s, y, 0).color(0xFFFFFFFF).texture(1, 0);

        RenderLayers.TEX_QUADS.apply(type.getTexture(hardcore, half, blinking)).draw(buf.end());
    }

    public static AbstractClientPlayerEntity findMatchingPlayer(PlayerEntityRenderState state) {
        if (state == null || mc.world == null)
            return null;

        for (AbstractClientPlayerEntity player: mc.world.getPlayers())
            if (player.getId() == state.id)
                return player;
        return null;
    }
}
