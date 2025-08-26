package foundry.veil.neoforge.event;

import foundry.veil.api.client.render.rendertype.layer.VeilRenderTypeStage;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when fixed buffers should be registered for rendering.
 *
 * @author Ocelot
 */
public class NeoForgeVeilRegisterFixedBuffersEvent extends Event implements IModBusEvent {

    public interface Registration {
        /**
         * Registers a render type for a specific stage.
         *
         * @param stage      The stage to register for, or null for all stages
         * @param renderType The render type to register
         */
        void register(@Nullable RenderLevelStageEvent.Stage stage, RenderType renderType);
    }

    private final Registration registration;

    public NeoForgeVeilRegisterFixedBuffersEvent(Registration registration) {
        this.registration = registration;
    }

    /**
     * Registers a render type for a specific stage.
     *
     * @param stage      The stage to register for, or null for all stages
     * @param renderType The render type to register
     */
    public void register(@Nullable RenderLevelStageEvent.Stage stage, RenderType renderType) {
        this.registration.register(stage, renderType);
    }
}