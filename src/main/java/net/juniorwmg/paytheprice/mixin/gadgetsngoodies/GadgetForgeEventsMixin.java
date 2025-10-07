package net.juniorwmg.paytheprice.mixin.gadgetsngoodies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import themattyboy.gadgetsngoodies.event.GadgetForgeEvents;

import java.io.PrintStream;

@Mixin(value = GadgetForgeEvents.class, remap = false)
public class GadgetForgeEventsMixin {

    @Redirect(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
                    remap = false
            ),
            remap = false
    )
    private void removeDebugPrint(PrintStream instance, String x) {
        // Do nothing (^.^)
    }
}