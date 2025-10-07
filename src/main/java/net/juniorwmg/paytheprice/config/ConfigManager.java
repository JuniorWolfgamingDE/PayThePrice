package net.juniorwmg.paytheprice.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigManager {
    public static boolean enableGadgetsNGoodiesMixin;
    public static boolean enableGrimPackMixin;
    public static boolean enableSignPicMixin;
    public static boolean enableComesAliveMixin;
    public static boolean enableSecretRoomsMixin;

    public static boolean showCoremodCountInBrandingText;
    public static boolean doNotShowModdedBrandingAtAll;
    public static Configuration config;

    public static void PayThePriceConfig() {
        config = new Configuration(new File("config/dpnptp.cfg"));
        config.load();

        config.setCategoryComment("Main", "Main configuration options for DPNPTP.\nAny mixins offered by this mod are automatically disabled if the corresponding mod is not present.\nKeep everything on true for the best experience.");
        config.setCategoryComment("Other", "Other configuration options not directly related to fixing mods.");

        enableGadgetsNGoodiesMixin = config.getBoolean("Enable Gadgets n' Goodies mixins", "Main", true, "Setting this to false will disable the fix for the crash occurring when equipping a Jetpack. No reason to disable this, still an option.");
        enableGrimPackMixin = config.getBoolean("Enable Grim Pack mixin", "Main", true, "Setting this to false will revert the blast resistance of the Grim Pack Gravestone from the mixin's 2000 back to 99.9.");
        enableSignPicMixin = config.getBoolean("Enable Sign Picture mixin", "Main", true, "Setting this to false will re-enable the Sign Picture update checker. Update checker causes small hiccup when entering the main menu after launch.");
        enableComesAliveMixin = config.getBoolean("Enable Minecraft Comes Alive mixin", "Main", true, "Setting this to false will re-enable the MCA update and supporter list checker. Re-enabling causes launch to take ages due to failing HTTP requests.");
        enableSecretRoomsMixin = config.getBoolean("Enable Secret Rooms Mod mixin", "Main", true, "Setting this to false will re-enable the Secret Rooms Mod update checker. Can cause crashes.");

        showCoremodCountInBrandingText = config.getBoolean("Show coremod count in main menu branding", "Other", false, "Setting this to true will show the number of *active* coremods in the branding text of the main menu. Ignored if 'doNotShowModdedBrandingAtAll' is true.");
        doNotShowModdedBrandingAtAll = config.getBoolean("Do not show any modified branding at all", "Other", false, "Setting this to true will remove all modded branding text from the main menu. This includes Forge version, mod count, information added by other mods like this one and f.e. VintageFix etc.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}