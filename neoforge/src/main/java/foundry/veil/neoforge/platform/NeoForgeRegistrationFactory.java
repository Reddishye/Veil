package foundry.veil.neoforge.platform;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public class NeoForgeRegistrationFactory implements RegistrationProvider.Factory {

    @Override
    public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        RegistryBuilder<T> registryFactory;
        if (RegistryManager.ACTIVE.getRegistry(resourceKey) == null && !BuiltInRegistries.REGISTRY.containsKey(resourceKey.location())) {
            registryFactory = RegistryBuilder.<T>of().disableSaving().disableSync().setName(resourceKey.location());
        } else {
            registryFactory = null;
        }
        return new Provider<>(modId, resourceKey, registryFactory, FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static class Provider<T> implements RegistrationProvider<T> {

        private final String modId;
        private final ResourceKey<? extends Registry<T>> resourceKey;
        private final RegistryWrapper<T> wrapper;

        private final Map<NeoForgeRegistryObject<T>, Supplier<? extends T>> entries = new HashMap<>();
        private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(this.entries.keySet());

        private Provider(String modId, ResourceKey<? extends Registry<T>> resourceKey, @Nullable RegistryBuilder<T> registryFactory, IEventBus bus) {
            this.modId = modId;
            this.resourceKey = resourceKey;
            this.wrapper = new RegistryWrapper<>(resourceKey);

            if (registryFactory != null) {
                bus.register(registryFactory);
            }

            bus.register(this);
        }

        @SubscribeEvent
        public void register(RegisterEvent event) {
            if (event.getRegistryKey().equals(this.resourceKey)) {
                for (Map.Entry<NeoForgeRegistryObject<T>, Supplier<? extends T>> entry : this.entries.entrySet()) {
                    NeoForgeRegistryObject<T> object = entry.getKey();
                    event.register(this.resourceKey, object.getId(), () -> entry.getValue().get());

                    IForgeRegistry<T> forgeRegistry = event.getForgeRegistry();
                    if (forgeRegistry != null) {
                        object.updateReference(forgeRegistry);
                        continue;
                    }

                    Registry<T> vanillaRegistry = event.getVanillaRegistry();
                    if (vanillaRegistry != null) {
                        object.updateReference(vanillaRegistry);
                    }
                }
            }
        }

        @Override
        public String getModId() {
            return this.modId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> RegistryObject<I> register(ResourceLocation id, Supplier<? extends I> supplier) {
            NeoForgeRegistryObject<I> ro = (NeoForgeRegistryObject<I>) new NeoForgeRegistryObject<>(ResourceKey.create(this.resourceKey, id));
            this.entries.put((NeoForgeRegistryObject<T>) ro, supplier);
            return ro;
        }

        @Override
        public Set<RegistryObject<T>> getEntries() {
            return this.entriesView;
        }

        @Override
        public Registry<T> asVanillaRegistry() {
            return this.wrapper;
        }
    }

    private static class NeoForgeRegistryObject<I> implements RegistryObject<I> {

        private final ResourceKey<I> key;
        private Supplier<I> value;

        private NeoForgeRegistryObject(ResourceKey<I> key) {
            this.key = key;
        }

        @Override
        public ResourceKey<I> getResourceKey() {
            return this.key;
        }

        @Override
        public ResourceLocation getId() {
            return this.key.location();
        }

        @Override
        public I get() {
            if (this.value == null) {
                throw new IllegalStateException("Registry object not present: " + this.key);
            }
            return this.value.get();
        }

        @Override
        public Holder<I> asHolder() {
            throw new UnsupportedOperationException("Holders not supported");
        }

        @Override
        public boolean isPresent() {
            return this.value != null;
        }

        @Override
        public void updateReference(Registry<?> registry) {
            this.value = () -> registry.get(this.key);
        }

        public void updateReference(IForgeRegistry<?> registry) {
            this.value = () -> (I) registry.getValue(this.key.location());
        }
    }

    // This wrapper is similar to the forge version - provides a minimal Registry implementation
    private record RegistryWrapper<T>(ResourceKey<? extends Registry<T>> resourceKey) implements Registry<T> {

        private IForgeRegistry<T> getRegistry() {
            return RegistryManager.ACTIVE.getRegistry(this.resourceKey);
        }

        @Override
        public @Nullable T get(@Nullable ResourceLocation name) {
            if (name == null) {
                return null;
            }
            IForgeRegistry<T> registry = this.getRegistry();
            return registry != null ? registry.getValue(name) : null;
        }

        @Override
        public @Nullable T get(@Nullable ResourceKey<T> name) {
            return name != null ? this.get(name.location()) : null;
        }

        @Override
        public ResourceLocation getKey(T value) {
            IForgeRegistry<T> registry = this.getRegistry();
            return registry != null ? registry.getKey(value) : null;
        }

        @Override
        public Optional<ResourceKey<T>> getResourceKey(T entry) {
            ResourceLocation location = this.getKey(entry);
            return location != null ? Optional.of(ResourceKey.create(this.resourceKey, location)) : Optional.empty();
        }

        @Override
        public int getId(@Nullable T value) {
            throw new UnsupportedOperationException("IDs are not supported");
        }

        @Override
        public @Nullable T byId(int id) {
            throw new UnsupportedOperationException("IDs are not supported");
        }

        @Override
        public int size() {
            IForgeRegistry<T> registry = this.getRegistry();
            return registry != null ? registry.getKeys().size() : 0;
        }

        @Override
        public Set<ResourceLocation> keySet() {
            IForgeRegistry<T> registry = this.getRegistry();
            return registry != null ? registry.getKeys() : Set.of();
        }

        @Override
        public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
            IForgeRegistry<T> registry = this.getRegistry();
            if (registry == null) {
                return Set.of();
            }
            return registry.getEntries().stream()
                    .collect(Collectors.toMap(
                            entry -> ResourceKey.create(this.resourceKey, entry.getKey()),
                            Map.Entry::getValue))
                    .entrySet();
        }

        @Override
        public Set<ResourceKey<T>> registryKeySet() {
            return this.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        }

        @Override
        public Optional<Holder.Reference<T>> getHolder(int id) {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Holder<T> wrapAsHolder(T value) {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Iterator<T> iterator() {
            IForgeRegistry<T> registry = this.getRegistry();
            return registry != null ? registry.iterator() : Collections.emptyIterator();
        }

        @Override
        public boolean containsKey(ResourceLocation name) {
            return this.get(name) != null;
        }

        @Override
        public boolean containsKey(ResourceKey<T> key) {
            return this.containsKey(key.location());
        }

        @Override
        public Registry<T> freeze() {
            return this;
        }

        @Override
        public Holder.Reference<T> createIntrusiveHolder(T value) {
            throw new UnsupportedOperationException("Intrusive holders are not supported");
        }

        @Override
        public Optional<HolderSet.Named<T>> getTag(TagKey<T> name) {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public void bindTags(Map<TagKey<T>, List<Holder<T>>> newTags) {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public HolderOwner<T> holderOwner() {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public HolderLookup.RegistryLookup<T> asLookup() {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public void resetTags() {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public @NotNull Holder<T> wrapAsHolder(@NotNull T value) {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Optional<Holder.Reference<T>> getRandom(RandomSource rand) {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Stream<Holder.Reference<T>> holders() {
            throw new UnsupportedOperationException("Holders are not supported");
        }

        @Override
        public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public HolderSet.Named<T> getOrCreateTag(TagKey<T> name) {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public Stream<TagKey<T>> getTagNames() {
            throw new UnsupportedOperationException("Tags are not supported");
        }

        @Override
        public Lifecycle lifecycle(T value) {
            return Lifecycle.stable();
        }

        @Override
        public Optional<T> getOptional(@Nullable ResourceLocation name) {
            return Optional.ofNullable(this.get(name));
        }

        @Override
        public Optional<T> getOptional(@Nullable ResourceKey<T> name) {
            return Optional.ofNullable(this.get(name));
        }
    }
}