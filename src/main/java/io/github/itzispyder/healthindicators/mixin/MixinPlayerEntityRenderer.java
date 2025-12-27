package io.github.itzispyder.healthindicators.mixin;

import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.render.Renderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer implements Global {

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("TAIL"))
    public void renderLabelIfPresent(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!Config.enabled)
            return;

        AbstractClientPlayerEntity player = Renderer.findMatchingPlayer(playerEntityRenderState);
        if (player != null)
            Renderer.render(matrices, player, cameraRenderState.orientation);
    }
}
