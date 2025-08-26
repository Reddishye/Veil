package foundry.veil.neoforge.mixin.client;

import foundry.veil.neoforge.event.NeoForgeFreeNativeResourcesEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "close", at = @At("HEAD"))
    public void veil$close(CallbackInfo ci) {
        ModLoader.get().postEvent(new NeoForgeFreeNativeResourcesEvent());
    }
}