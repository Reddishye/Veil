package foundry.veil.neoforge.event;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Consumer;

/**
 * Fired when block render layers should be registered.
 *
 * @author Ocelot
 */
public class NeoForgeVeilRegisterBlockLayerEvent extends Event implements IModBusEvent {

    private final Consumer<RenderType> consumer;

    public NeoForgeVeilRegisterBlockLayerEvent(Consumer<RenderType> consumer) {
        this.consumer = consumer;
    }

    /**
     * Registers the specified render type as a block layer.
     *
     * @param renderType The render type to register
     */
    public void registerBlockLayer(RenderType renderType) {
        this.consumer.accept(renderType);
    }
}