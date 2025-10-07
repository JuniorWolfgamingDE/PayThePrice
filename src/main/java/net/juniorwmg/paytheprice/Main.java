package net.juniorwmg.paytheprice;

import com.google.common.collect.ImmutableList;
import net.juniorwmg.paytheprice.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:mixinbooter@[10.0,);after:gadgetsngoodies@[1.2.2,);after:signpic@[2.8.1,);after:grimpack@[6.0.0.6,);after:mca@[6.0.0,);")
public class Main implements ILateMixinLoader {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_TAG);
    private static boolean configLoaded = false;

    @Override
    public List<String> getMixinConfigs() {
        if (!configLoaded) {
            ConfigManager.PayThePriceConfig();
            configLoaded = true;
        }

        List<String> mixinConfigs = new ArrayList<>();
        List<String> skippedConfigs = new ArrayList<>();

        mixinConfigs.add("mixins." + Tags.MOD_ID + ".json");

        ConditionalMixin[] conditionalMixins = {
                new ConditionalMixin("gadgetsngoodies", ConfigManager.enableGadgetsNGoodiesMixin),
                new ConditionalMixin("grimpack", ConfigManager.enableGrimPackMixin),
                new ConditionalMixin("signpic", ConfigManager.enableSignPicMixin),
                new ConditionalMixin("mca", ConfigManager.enableComesAliveMixin)
        };

        for (ConditionalMixin mixin : conditionalMixins) {
            String configName = "mixins." + Tags.MOD_ID + "." + mixin.modId + ".json";

            if (Loader.isModLoaded(mixin.modId) && mixin.enabled) {
                mixinConfigs.add(configName);
            } else {
                String reason = !Loader.isModLoaded(mixin.modId) ? "mod not loaded" : "disabled in config";
                skippedConfigs.add(configName + " (" + reason + ")");
            }
        }

        logMixinConfigs(mixinConfigs, skippedConfigs);
        return ImmutableList.copyOf(mixinConfigs);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Reached pre-init.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Reached init.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Reached post-init.");
    }

    private void logMixinConfigs(List<String> loaded, List<String> skipped) {
        LOGGER.info("Loading mixin configurations:");
        loaded.forEach(config -> LOGGER.info("  - {}", config));

        if (!skipped.isEmpty()) {
            LOGGER.info("Skipped mixin configurations:");
            skipped.forEach(config -> LOGGER.info("  - {}", config));
        }
    }

    private static class ConditionalMixin {
        final String modId;
        final boolean enabled;

        ConditionalMixin(String modId, boolean enabled) {
            this.modId = modId;
            this.enabled = enabled;
        }
    }
}