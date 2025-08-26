package foundry.veil.neoforge;

import com.google.common.collect.ImmutableList;
import foundry.veil.Veil;
import foundry.veil.VeilClient;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.neoforge.event.NeoForgeVeilRegisterBlockLayerEvent;
import foundry.veil.neoforge.event.NeoForgeVeilRegisterFixedBuffersEvent;
import foundry.veil.neoforge.event.NeoForgeVeilRendererEvent;
import foundry.veil.impl.VeilBuiltinPacks;
import foundry.veil.impl.VeilReloadListeners;
import foundry.veil.impl.client.render.VeilUITooltipRenderer;
import foundry.veil.impl.client.render.shader.VeilVanillaShaders;
import foundry.veil.mixin.accessor.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.resource.PathPackResources;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class VeilNeoForgeClient {

    public static void init() {
        VeilClient.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(VeilNeoForgeClient::registerKeys);
        modEventBus.addListener(VeilNeoForgeClient::registerGuiOverlays);
        modEventBus.addListener(VeilNeoForgeClient::registerListeners);
        modEventBus.addListener(VeilNeoForgeClient::registerShaders);
        modEventBus.addListener(VeilNeoForgeClient::addPackFinders);

        ImmutableList.Builder<RenderType> blockLayers = ImmutableList.builder();
        ModLoader.get().postEvent(new NeoForgeVeilRegisterBlockLayerEvent(renderType -> {
            if (Veil.platform().isDevelopmentEnvironment() && renderType.bufferSize() > RenderType.SMALL_BUFFER_SIZE) {
                Veil.LOGGER.warn("Block render layer '{}' uses a large buffer size: {}. If this is intended you can ignore this message", ((RenderStateShardAccessor) renderType).getName(), renderType.bufferSize());
            }
            blockLayers.add(renderType);
        }));
        NeoForgeRenderTypeStageHandler.setBlockLayers(blockLayers);
    }

    private static void registerListeners(RegisterClientReloadListenersEvent event) {
        VeilClient.initRenderer();
        VeilReloadListeners.registerListeners((type, id, listener) -> event.registerReloadListener(listener));
        ModLoader loader = ModLoader.get();
        loader.postEvent(new NeoForgeVeilRendererEvent(VeilRenderSystem.renderer()));
        loader.postEvent(new NeoForgeVeilRegisterFixedBuffersEvent(NeoForgeRenderTypeStageHandler::register));
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(VeilClient.EDITOR_KEY);
    }

    private static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, "uitooltip", VeilUITooltipRenderer::renderOverlay);
    }

    private static void registerShaders(RegisterShadersEvent event) {
        try {
            VeilVanillaShaders.registerShaders((id, vertexFormat, loadCallback) -> event.registerShader(new ShaderInstance(event.getResourceProvider(), id, vertexFormat), loadCallback));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO allow pack enabled by default
    private static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            VeilBuiltinPacks.registerPacks((id, defaultEnabled) -> registerBuiltinPack(event, id));
        }
    }

    private static void registerBuiltinPack(AddPackFindersEvent event, ResourceLocation id) {
        Path resourcePath = ModList.get().getModFileById(Veil.MODID).getFile().findResource("resourcepacks/" + id.getPath());
        Pack pack = Pack.readMetaAndCreate(id.toString(), Component.literal(id.getNamespace() + "/" + id.getPath()), false, packId -> new PathPackResources(packId, true, resourcePath), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
        if (pack == null) {
            Veil.LOGGER.error("Failed to find builtin pack: {}", id);
            return;
        }
        event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
    }
}