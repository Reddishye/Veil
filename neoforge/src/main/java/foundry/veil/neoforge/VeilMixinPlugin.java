package foundry.veil.neoforge;

import foundry.veil.mixin.plugin.VeilMixinConfigPlugin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class VeilMixinPlugin implements IMixinConfigPlugin {

    private final VeilMixinConfigPlugin plugin = new VeilMixinConfigPlugin();

    @Override
    public void onLoad(String mixinPackage) {
        this.plugin.onLoad(mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return this.plugin.getRefMapperConfig();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return this.plugin.shouldApplyMixin(targetClassName, mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        this.plugin.acceptTargets(myTargets, otherTargets);
    }

    @Override
    public List<String> getMixins() {
        return this.plugin.getMixins();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        this.plugin.preApply(targetClassName, targetClass, mixinClassName, mixinInfo);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        this.plugin.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
    }
}