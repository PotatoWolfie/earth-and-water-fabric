package potatowolfie.earth_and_water.mixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractCauldronBlock.class)
public interface AbstractCauldronBlockMixin {
    @Invoker("canBeFilledByDripstone")
    boolean invokeCanBeFilledByDripstone(Fluid fluid);
}