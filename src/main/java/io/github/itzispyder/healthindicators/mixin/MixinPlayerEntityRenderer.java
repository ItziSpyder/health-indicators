package io.github.itzispyder.healthindicators.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.itzispyder.healthindicators.Global;
import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.render.Renderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class MixinPlayerEntityRenderer implements Global {

    @Shadow protected abstract boolean shouldShowName(Entity par1, double par2);

    @Inject(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    public void renderLabelIfPresent(AvatarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.CameraRenderState camera, CallbackInfo ci) {
        if (!Config.enabled)
            return;

        AbstractClientPlayer player = Renderer.findMatchingPlayer(state);
        if (player != null && shouldShowName(player, state.distanceToCameraSq))
            Renderer.render(submitNodeCollector, poseStack, player, camera.orientation);
    }
}
