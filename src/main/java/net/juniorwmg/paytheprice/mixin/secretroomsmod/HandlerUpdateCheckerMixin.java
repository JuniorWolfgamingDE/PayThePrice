package net.juniorwmg.paytheprice.mixin.secretroomsmod;

import com.wynprice.secretroomsmod.handler.HandlerUpdateChecker;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandlerUpdateChecker.class, remap = false)
public class HandlerUpdateCheckerMixin {

    @Inject(method = "onPlayerJoin", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disableUpdateCheck(EntityJoinWorldEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}