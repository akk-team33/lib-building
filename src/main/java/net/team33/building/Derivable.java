package net.team33.building;

/**
 * Abstracts structures that are (mentioned to be) immutable by itself, but may be derived and (presumably) modified
 * through a specific {@link Builder}.
 */
public interface Derivable<D extends Derivable<D, B>, B extends Builder<D>> {

    /**
     * Supplies a specific {@link Builder} that 'inherits' the properties of the this instance and may be modified
     * as needed to finally {@linkplain Builder#build() build} a new instance.
     */
    B builder();
}
