package net.juniorwmg.paytheprice.mixin._early.fml;

import com.google.common.collect.ImmutableList;
import net.juniorwmg.paytheprice.config.ConfigManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(FMLCommonHandler.class)
public class FMLCommonHandlerMixin {

    @Shadow(remap = false)
    private List<String> brandings;

    @Shadow(remap = false)
    private List<String> brandingsNoMC;

    @Inject(method = "computeBranding", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 4, remap = false), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void addCoremods(CallbackInfo ci, ImmutableList.Builder<String> builder) {
        if (ConfigManager.showCoremodCountInBrandingText && !ConfigManager.doNotShowModdedBrandingAtAll) {
            int coremodCount = CoreModManager.getReparseableCoremods().size();
            builder.add("Active coremods: " + coremodCount);
        }
    }

    @Redirect(method = "computeBranding", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;"), remap = false)
    private ImmutableList<String> replaceBranding(ImmutableList.Builder<String> builder) {
        if (ConfigManager.doNotShowModdedBrandingAtAll) {
            return ImmutableList.of("Minecraft 1.12.2");
        }
        return builder.build();
    }
}