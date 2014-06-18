package net.team33.building;

/**
 * Abstracts structures that are (mentioned to be) immutable by itself, but may extract a specific {@link Builder}
 * that 'inherits' the properties and may finally build a copy of the original structure that may vary in some aspects
 * though (a 'modified copy').
 */
public interface Extractable<E extends Extractable<E, B>, B extends Builder<E>> {

    /**
     * Supplies a specific {@link Builder} that 'inherits' the properties of the this instance and may be modified
     * as needed to finally {@linkplain Builder#build() build} a new instance.
     */
    B extract();
}
