package net.juniorwmg.paytheprice.mixin.mca;

import mca.core.MCA;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.apache.logging.log4j.Logger;
import mca.core.Localizer;
import mca.core.Config;

@Mixin(value = MCA.class, remap = false)
public class MCAMixin {

    @Shadow
    private static MCA instance;

    @Shadow
    private static Logger logger;

    @Shadow
    private static Localizer localizer;

    @Shadow
    private static Config config;

    @Shadow
    private static long startupTimestamp;

    @Shadow
    public static String latestVersion;

    @Shadow
    public static boolean updateAvailable;

    @Shadow
    public String[] supporters;

    @Inject(method = "preInit", at = @At("HEAD"), cancellable = true)
    public void replacePreInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        startupTimestamp = new java.util.Date().getTime();
        instance = (MCA)(Object)this;
        logger = event.getModLog();
        MCA.proxy.registerEntityRenderers();
        localizer = new mca.core.Localizer();
        config = new mca.core.Config(event);

        MCA.creativeTab = new net.minecraft.creativetab.CreativeTabs("MCA") {
            public net.minecraft.item.ItemStack createIcon() {
                return new net.minecraft.item.ItemStack(mca.core.minecraft.ItemsMCA.ENGAGEMENT_RING);
            }
        };

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new mca.core.forge.EventHooks());
        net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler((MCA)(Object)this, new mca.core.forge.GuiHandler());
        mca.core.forge.NetMCA.registerMessages();

        latestVersion = "";
        updateAvailable = false;
        supporters = "Furzball,wuffleoreo,Nicole,Nia,Alex,AdmiralWilson,Ty,onquicklylu,Perf3ctDude,Cezary,Kalika,UnidentifiedDuck,Theresa G.,Andrew C.,Joya F.,Mike E.".split(",");

        logger.info("Loaded " + supporters.length + " supporters.");

        ci.cancel();
    }
}