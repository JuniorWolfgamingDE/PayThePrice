package net.juniorwmg.paytheprice.mixin.grimpack;

import com.grim3212.mc.pack.util.grave.BlockGrave;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = BlockGrave.class, remap = false)
public class BlockGraveMixin {

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 99.9F))
    private float modifyResistance(float original) {
        return 2000.0F;
    }
}