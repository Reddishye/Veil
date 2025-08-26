package foundry.veil.neoforge.platform;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.event.*;
import foundry.veil.neoforge.event.*;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import java.util.Map;

@ApiStatus.Internal
public class NeoForgeVeilEventPlatform implements VeilEventPlatform {

    private static final BiMap<VeilRenderLevelStageEvent.Stage, RenderLevelStageEvent.Stage> STAGE_MAPPING = HashBiMap.create(Map.ofEntries(
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_SKY, RenderLevelStageEvent.Stage.AFTER_SKY),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS, RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS, RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS, RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_ENTITIES, RenderLevelStageEvent.Stage.AFTER_ENTITIES),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES, RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS, RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_PARTICLES, RenderLevelStageEvent.Stage.AFTER_PARTICLES),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_WEATHER, RenderLevelStageEvent.Stage.AFTER_WEATHER),
            Map.entry(VeilRenderLevelStageEvent.Stage.AFTER_LEVEL, RenderLevelStageEvent.Stage.AFTER_LEVEL)
    ));

    @Override
    public void onVeilRenderTypeStageRender(VeilRenderLevelStageEvent event) {
        NeoForge.EVENT_BUS.<RenderLevelStageEvent>addListener(forgeEvent -> {
            VeilRenderLevelStageEvent.Stage veilStage = STAGE_MAPPING.inverse().get(forgeEvent.getStage());
            if (veilStage != null) {
                event.onRenderStage(veilStage, forgeEvent.getLevelRenderer(), forgeEvent.getPoseStack(), forgeEvent.getProjectionMatrix(), forgeEvent.getRenderTick(), forgeEvent.getPartialTick(), forgeEvent.getCamera(), forgeEvent.getFrustum());
            }
        });
    }

    @Override
    public void onFreeNativeResources(FreeNativeResourcesEvent event) {
        NeoForge.EVENT_BUS.<NeoForgeFreeNativeResourcesEvent>addListener(forgeEvent -> event.onFree());
    }

    @Override
    public void onVeilRendererAvailable(VeilRendererEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().<NeoForgeVeilRendererEvent>addListener(forgeEvent -> event.onVeilRendererAvailable(forgeEvent.getRenderer()));
    }

    @Override
    public void preVeilPostProcessing(VeilPostProcessingEvent.Pre event) {
        NeoForge.EVENT_BUS.<NeoForgeVeilPostProcessingEvent.Pre>addListener(forgeEvent -> event.preVeilPostProcessing(forgeEvent.getName(), forgeEvent.getPipeline(), forgeEvent.getContext()));
    }

    @Override
    public void postVeilPostProcessing(VeilPostProcessingEvent.Post event) {
        NeoForge.EVENT_BUS.<NeoForgeVeilPostProcessingEvent.Post>addListener(forgeEvent -> event.postVeilPostProcessing(forgeEvent.getName(), forgeEvent.getPipeline(), forgeEvent.getContext()));
    }

    @Override
    public void onVeilRegisterFixedBuffers(VeilRegisterFixedBuffersEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().<NeoForgeVeilRegisterFixedBuffersEvent>addListener(forgeEvent -> event.onRegisterFixedBuffers((stage, renderType) -> {
            if (stage == null) {
                forgeEvent.register(null, renderType);
                return;
            }

            RenderLevelStageEvent.Stage forgeStage = STAGE_MAPPING.get(stage);
            if (forgeStage != null) {
                forgeEvent.register(forgeStage, renderType);
            }
        }));
    }
}