package net.team33.building.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.allOf;
import static java.util.EnumSet.copyOf;

/**
 * @param <K>
 */
public class EnumMapped<K extends Enum<K> & Key> extends Mapped.Immutable<K> {

    private final Map<K, Object> backing;

    /**
     * Initiates a new instance backed by an immutable copy of a given {@code mapper}.
     *
     * @throws NullPointerException if the {@code mapper} is {@code null}.
     */
    protected EnumMapped(final Mapper<K, ?> mapper) {
        this(mapper.backing);
    }

    /**
     * Initiates a new instance backed by an immutable copy of a given {@link Map}.
     *
     * @throws IllegalArgumentException if {@code origin} is empty and not an instance of {@link EnumMap}.
     * @throws NullPointerException     if {@code origin} is {@code null}.
     */
    protected EnumMapped(final Map<K, ?> origin) {
        backing = unmodifiableMap(new EnumMap<>(origin));
    }

    @Override
    public final Map<K, Object> asMap() {
        // Already is immutable ...
        // noinspection ReturnOfCollectionOrArrayField
        return backing;
    }

    /**
     * Provides basic implementations of a mutable counterpart to a {@link EnumMapped}
     * intended to be derived as a Builder for a derivation of {@link EnumMapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     * @param <B> The final (relevant) derivation of this class
     */
    @SuppressWarnings("PublicInnerClass")
    public abstract static class Mapper<K extends Enum<K> & Key, B extends Mapper<K, B>>
            extends Mutable<K, B> {

        private final Set<K> keySet;
        private final EnumMap<K, Object> backing;

        /**
         * Initiates a new instance by a given {@code keyClass} that will contain any possible key but {@code null},
         * associated with their {@linkplain Key#getInitial() default values}.
         *
         * @param keyClass The {@linkplain Class class representation} of the intended keys, not {@code null}.
         * @throws NullPointerException if {@code keyClass} is {@code null}.
         */
        protected Mapper(final Class<K> keyClass) {
            this(keyClass, allOf(keyClass));
        }

        /**
         * Initiates a new instance by a given {@code keySet} that will contain any possible key but {@code null},
         * associated with their {@linkplain Key#getInitial() default values}.
         *
         * @param keySet The {@linkplain Class class representation} of the intended keySet, not {@code null}.
         * @throws NullPointerException     if {@code keySet} is or contains {@code null}.
         * @throws IllegalArgumentException if {@code keySet} is empty and not an instance of {@link EnumSet}.
         */
        protected Mapper(final Collection<K> keySet) {
            this(keySet.iterator().next().getDeclaringClass(), keySet);
        }

        /**
         * @throws NullPointerException     if {@code keyClass} or {@code keys} is or contains {@code null}.
         * @throws IllegalArgumentException if {@code keys} is empty and not an instance of {@link EnumSet}.
         */
        private Mapper(final Class<K> keyClass, final Collection<K> keys) {
            keySet = unmodifiableSet(copyOf(keys));
            backing = copy(Collections.<K, Object>emptyMap(), keySet, true, true, new EnumMap<>(keyClass));
        }

        @Override
        protected final Set<K> keySet() {
            // Already is immutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return keySet;
        }

        @SuppressWarnings("CollectionDeclaredAsConcreteClass")
        @Override
        public final EnumMap<K, Object> asMap() {
            // Intended to be modifiable ...
            // noinspection ReturnOfCollectionOrArrayField
            return backing;
        }
    }
}
