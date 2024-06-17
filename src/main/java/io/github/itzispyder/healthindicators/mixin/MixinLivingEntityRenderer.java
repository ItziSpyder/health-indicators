package io.github.itzispyder.healthindicators.mixin;

import io.github.itzispyder.healthindicators.client.Config;
import io.github.itzispyder.healthindicators.utils.PlayerUtils;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    public <T extends LivingEntity> void hasLabel(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (Config.showOwn && entity == PlayerUtils.player())
            cir.setReturnValue(true);
    }
}
