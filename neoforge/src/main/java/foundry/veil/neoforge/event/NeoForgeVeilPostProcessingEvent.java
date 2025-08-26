package foundry.veil.neoforge.event;

import foundry.veil.api.client.render.post.PostPipeline;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

/**
 * Fired when post processing stages happen.
 *
 * @author Ocelot
 */
public abstract class NeoForgeVeilPostProcessingEvent extends Event {

    private final ResourceLocation name;
    private final PostPipeline pipeline;
    private final PostPipeline.Context context;

    private NeoForgeVeilPostProcessingEvent(ResourceLocation name, PostPipeline pipeline, PostPipeline.Context context) {
        this.name = name;
        this.pipeline = pipeline;
        this.context = context;
    }

    /**
     * @return The name of the pipeline
     */
    public ResourceLocation getName() {
        return this.name;
    }

    /**
     * @return The pipeline being processed
     */
    public PostPipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * @return The context for processing
     */
    public PostPipeline.Context getContext() {
        return this.context;
    }

    /**
     * Fired before post processing happens.
     */
    public static class Pre extends NeoForgeVeilPostProcessingEvent {
        public Pre(ResourceLocation name, PostPipeline pipeline, PostPipeline.Context context) {
            super(name, pipeline, context);
        }
    }

    /**
     * Fired after post processing happens.
     */
    public static class Post extends NeoForgeVeilPostProcessingEvent {
        public Post(ResourceLocation name, PostPipeline pipeline, PostPipeline.Context context) {
            super(name, pipeline, context);
        }
    }
}