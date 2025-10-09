package net.juniorwmg.paytheprice.mixin.rawinput;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import world.maryt.rawinput.RawInput;

@Mixin(RawInput.class)
public class RawInputMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private void onInit(FMLInitializationEvent event, CallbackInfo ci) {
        if (Loader.isModLoaded("cleanroom")) {
            RawInput.LOGGER.info("DPNPTP disabled Raw Input due to CleanroomMC being used. CleanroomMC offers it's own raw input option which is enabled by default. Toggle it in the forge_early.cfg should you wish to disable it.");
            ci.cancel();
        }
    }
}
