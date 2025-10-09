package net.juniorwmg.paytheprice.mixin.fw;

import com.hancinworld.fw.proxy.ClientProxy;
import com.hancinworld.fw.utility.LogHelper;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class ClientProxyMixin {

    @Inject(method = "performStartupChecks", at = @At("HEAD"), cancellable = true, remap = false)
    private void checkCleanroomMod(CallbackInfo ci) {
        if (Loader.isModLoaded("cleanroom")) {
            // Disable if CleanroomMC is used, as it has its own fullscreen windowed fix
            LogHelper.info("DPNPTP disabled Fullscreen Windowed due to CleanroomMC being used. CleanroomMC offers it's own Borderless Fullscreen option, please enable it in the forge_early.cfg should you wish to use it.");
            ci.cancel();
        }
    }
}
