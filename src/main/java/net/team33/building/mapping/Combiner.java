package net.team33.building.mapping;

import net.team33.building.Builder;
import net.team33.building.Extractable;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ReturnOfThis")
public class Combiner<
        K extends Enum<K> & Key,
        B extends Mapped.Mutable<K, B> & Builder<R>,
        R extends Mapped<K> & Extractable<R, B>,
        C extends Combiner<K, B, R, C>>
        implements Iterable<R> {

    private final EnumMap<K, Set<?>> backing;
    private final R template;

    public Combiner(final Class<K> keyClass, final R template) {
        this.backing = new EnumMap<>(keyClass);
        this.template = template;
    }

    public final C set(final K key, final Builder<? extends Set<?>> builder) {
        backing.put(key, builder.build());
        // <this> must be an instance of <C> ...
        // noinspection unchecked
        return (C) this;
    }

    @Override
    public final Iterator<R> iterator() {
        return new Converter();
    }

    @SuppressWarnings("NonStaticInnerClassInSecureContext")
    private class Converter implements Iterator<R> {

        private final Iterator<Map<K, Object>> inner = new CombIterator<>(backing);

        @Override
        public final boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public final R next() {
            return template.extract().set(inner.next()).build();
        }

        @Override
        public final void remove() {
            inner.remove(); // --> UnsupportedOperationException (OK)
        }
    }
}
