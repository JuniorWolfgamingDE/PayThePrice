package net.juniorwmg.paytheprice.mixin.signpic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = com.kamesuta.mc.signpic.information.Informations.class, remap = false)
public class InformationsMixin {

    @Inject(method = "onlineCheck", at = @At("HEAD"), cancellable = true, remap = false)
    private void disableOnlineCheck(CallbackInfo ci) {
        ci.cancel();
    }
}