package potatowolfie.earth_and_water.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.util.ModTags;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class ConduitBlockEntityMixin {

    @Shadow
    @Final
    private static Block[] ACTIVATING_BLOCKS;

    @Inject(
            method = "updateActivatingBlocks",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void addCustomFrameBlocks(World world, BlockPos pos, List<BlockPos> activatingBlocks, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        activatingBlocks.clear();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos waterPos = pos.add(i, j, k);
                    if (!world.isWater(waterPos)) {
                        return;
                    }
                }
            }
        }

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    int l = Math.abs(i);
                    int m = Math.abs(j);
                    int n = Math.abs(k);

                    if ((l > 1 || m > 1 || n > 1) &&
                            (i == 0 && (m == 2 || n == 2) ||
                                    j == 0 && (l == 2 || n == 2) ||
                                    k == 0 && (l == 2 || m == 2))) {

                        BlockPos framePos = pos.add(i, j, k);
                        BlockState blockState = world.getBlockState(framePos);

                        for (Block block : ACTIVATING_BLOCKS) {
                            if (blockState.isOf(block)) {
                                activatingBlocks.add(framePos);
                                break;
                            }
                        }

                        if (blockState.isIn(ModTags.Blocks.E_W_CONDUIT_FRAME_BLOCKS)) {
                            activatingBlocks.add(framePos);
                        }
                    }
                }
            }
        }

        boolean hasEnoughBlocks = activatingBlocks.size() >= 16;
        cir.setReturnValue(hasEnoughBlocks);
    }
}