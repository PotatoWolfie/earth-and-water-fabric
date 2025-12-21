package potatowolfie.earth_and_water.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {

    @Shadow
    private List<String> splashTexts;

    @Inject(method = "apply*",
            at = @At("TAIL"))
    private void addEANDWSplashes(CallbackInfo ci) {
        splashTexts = new ArrayList<>(splashTexts);

        splashTexts.add(Text.translatable("splash.earth-and-water.sticks_stone").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.herobrine").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.september").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.w_e_f_a").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.w_e_f_a").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.drowning").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.splash").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.hardlyknower").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.removed_the_brine").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.bore_you").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.sweet_carobrine").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.sun_tzu").getString());
        splashTexts.add(Text.translatable("splash.earth-and-water.bore_d").getString());
    }
}