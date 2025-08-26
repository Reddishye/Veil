package foundry.veil.neoforge.event;

import foundry.veil.api.client.render.VeilRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * Fired when the veil renderer becomes available on the client.
 *
 * @author Ocelot
 */
public class NeoForgeVeilRendererEvent extends Event implements IModBusEvent {

    private final VeilRenderer renderer;

    public NeoForgeVeilRendererEvent(VeilRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * @return The veil renderer instance
     */
    public VeilRenderer getRenderer() {
        return this.renderer;
    }
}