package potatowolfie.earth_and_water.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {

    @Shadow
    private List<String> splashTexts;

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At("TAIL"))
    private void addEANDWSplashes(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        this.splashTexts.add(Text.translatable("splash.earth-and-water.sticks_stone").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.herobrine").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.september").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.w_e_f_a").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.drowning").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.splash").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.hardlyknower").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.removed_the_brine").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.bore_you").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.sweet_carobrine").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.sun_tzu").getString());
        this.splashTexts.add(Text.translatable("splash.earth-and-water.bore_d").getString());
    }
}