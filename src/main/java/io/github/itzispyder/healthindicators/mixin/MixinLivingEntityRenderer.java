package io.github.itzispyder.healthindicators.mixin;

import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.utils.PlayerUtils;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("RETURN"), cancellable = true)
    public <T extends LivingEntity> void hasLabel(T entity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (entity == PlayerUtils.player())
            cir.setReturnValue(Config.showOwn);
    }
}
