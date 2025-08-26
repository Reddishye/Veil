package foundry.veil.neoforge;

import foundry.veil.Veil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(Veil.MODID)
public class VeilNeoForge {

    public VeilNeoForge(ModContainer container) {
        Veil.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            VeilNeoForgeClient.init();
        }
    }
}