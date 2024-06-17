package io.github.itzispyder.healthindicators.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.utils.HeartType;
import io.github.itzispyder.healthindicators.utils.PlayerHealthData;
import io.github.itzispyder.healthindicators.utils.PlayerUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer implements Global {

    @Unique private final Map<UUID, PlayerHealthData> dataMap = new HashMap<>();
    @Unique private final Random random = new Random();

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V", at = @At("TAIL"))
    public void renderLabelIfPresent(AbstractClientPlayerEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int i, float f, CallbackInfo ci) {
        if (Config.enabled)
            render(matrices, entity);
    }

    @Unique
    public void render(MatrixStack matrices, PlayerEntity player) {
        if (Config.noNPC && !PlayerUtils.playerValid(player))
            return;

        var dispatcher = mc.getEntityRenderDispatcher();
        float scale = 0.025F * Config.scale;

        float height = PlayerUtils.getEntityNameLabelHeight(player, 1.0F);
        float width = 80;
        float x = -width / 2;
        float y = -20 - Config.offsetY;

        matrices.push();
        matrices.translate(0.0F, height, 0.0F);
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(scale, -scale, scale);

        renderStatusBars(matrices, player, x, y);

        matrices.pop();
    }

    @Unique
    private void renderStatusBars(MatrixStack matrices, PlayerEntity player, float x, float y) {
        int ticks = mc.inGameHud.getTicks();
        UUID id = player.getGameProfile().getId();
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
        float maxHealth = Math.max((float)player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(health, lastHealth));
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int lines = Math.max(10 - (MathHelper.ceil((maxHealth + (float)absorption) / 2.0F / 10.0F) - 2), 3);
        int regeneratingHeartIndex = -1;

        if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
            regeneratingHeartIndex = ticks % MathHelper.ceil(maxHealth + 5.0F);
        }

        this.renderHealthBar(matrices, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    @Unique
    private void renderHealthBar(MatrixStack matrices, PlayerEntity player, float x, float y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        HeartType heartType = HeartType.fromPlayerState(player);
        boolean hardcore = player.getWorld().getLevelProperties().isHardcore();
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

            this.drawHeart(matrices, HeartType.CONTAINER, renderX, renderY, hardcore, blinking, false);

            if (i >= maxHearts) {
                int diff = asHealthPoints - maxHpPoints;
                if (diff < absorption) {
                    boolean half = diff + 1 == absorption;
                    this.drawHeart(matrices, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, renderX, renderY, hardcore, false, half);
                }
            }

            if (blinking && asHealthPoints < health)
                this.drawHeart(matrices, heartType, renderX, renderY, hardcore, true, asHealthPoints + 1 == health);

            if (asHealthPoints < lastHealth)
                this.drawHeart(matrices, heartType, renderX, renderY, hardcore, false, asHealthPoints + 1 == lastHealth);
        }
    }

    @Unique
    private void drawHeart(MatrixStack matrices, HeartType type, float x, float y, boolean hardcore, boolean blinking, boolean half) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f mat = matrices.peek().getPositionMatrix();

        buf.vertex(mat, x, y, 0).texture(0, 0);
        buf.vertex(mat, x, y + 9, 0).texture(0, 1);
        buf.vertex(mat, x + 9, y + 9, 0).texture(1, 1);
        buf.vertex(mat, x + 9, y, 0).texture(1, 0);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, type.getTexture(hardcore, half, blinking));
        RenderSystem.enableDepthTest();

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.disableBlend();
    }
}
