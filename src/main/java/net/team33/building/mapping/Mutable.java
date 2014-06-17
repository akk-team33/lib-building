package net.team33.building.mapping;

import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * Provides basic implementations of a mutable counterpart to a {@link net.team33.building.mapping.Mapped}
 * intended to be derived as a Builder for a derivation of {@link net.team33.building.mapping.Mapped}.
 *
 * @param <K> The specific type of the keys representing the properties.
 * @param <B> The final (relevant) derivation of this class
 */
@SuppressWarnings("PublicInnerClass")
public abstract class Mutable<K extends Key, B extends Mutable<K, B>> extends Mapped<K> {

    /**
     * {@inheritDoc}
     * <p/>
     * In contrast to the common specification, a Mutable implementation must supply a MUTABLE map!
     */
    @SuppressWarnings("AbstractMethodOverridesAbstractMethod") // differing specification (java doc)
    @Override
    public abstract Map<K, Object> asMap();

    /**
     * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
     * intended key set}. Otherwise throws an IllegalArgumentException.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key}
     *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  class} associated with the specified {@code key}.
     * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()
     *                                  intended key set}.
     */
    public final B set(final K key, final Object value) {
        return set(key, value, false);
    }

    /**
     * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
     * intended key set}. Otherwise either ignores the value or throws an IllegalArgumentException.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key} indicates
     *                                  to be not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  value class} associated with the specified {@code key}.
     * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()}
     *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
     */
    public final B set(final K key, final Object value, final boolean ignoreOverhead) {
        if (keySet().contains(key)) {
            asMap().put(key, valid(key, value));
        } else if (!ignoreOverhead) {
            throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
        }
        return finallyThis();
    }

    /**
     * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
     * intended key set}. Otherwise throws an IllegalArgumentException.
     * <p/>
     * Values associated with keys not covered by the origin map will remain as is.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
     *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  class} associated with the corresponding {@code key}.
     * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
     *                                  intended key set}.
     */
    public final B set(final Map<? extends K, ?> origin) {
        return set(origin, false);
    }

    /**
     * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
     * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
     * <p/>
     * Values associated with keys not covered by the origin map will remain as is.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
     *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  class} associated with the corresponding {@code key}.
     * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
     *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
     */
    public final B set(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
        copy(origin, keySet(), false, ignoreOverhead, asMap());
        return finallyThis();
    }

    /**
     * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
     * intended key set}. Otherwise throws an IllegalArgumentException.
     * <p/>
     * Values associated with keys not covered by the origin map will be reset to their
     * {@linkplain net.team33.building.mapping.Key#getInitial() defaults}.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
     *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  class} associated with the corresponding {@code key}.
     * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
     *                                  intended key set}.
     */
    public final B reset(final Map<? extends K, ?> origin) {
        return reset(origin, false);
    }

    /**
     * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
     * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
     * <p/>
     * Values associated with keys not covered by the origin map will be reset to their
     * {@linkplain net.team33.building.mapping.Key#getInitial() defaults}.
     *
     * @return {@code this} in its final representation.
     * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
     *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
     * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
     *                                  class} associated with the corresponding {@code key}.
     * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
     *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
     */
    public final B reset(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
        copy(origin, keySet(), true, ignoreOverhead, asMap());
        return finallyThis();
    }

    /**
     * Supplies the intended key set.
     * <p/>
     * Must be a separate immutable set - not simply the {@link java.util.Map#keySet()} for the {@linkplain #asMap()
     * underlying map} - otherwise expect strange side effects!
     */
    protected abstract Set<K> keySet();

    /**
     * Supplies {@code this} in its final representation.
     */
    protected abstract B finallyThis();
}
