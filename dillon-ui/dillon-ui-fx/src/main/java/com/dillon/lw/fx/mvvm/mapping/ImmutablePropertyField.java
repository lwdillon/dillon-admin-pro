package com.dillon.lw.fx.mvvm.mapping;

import javafx.beans.property.Property;

public interface ImmutablePropertyField<T, M, R extends Property<T>> extends PropertyField<T, M, R> {

	M commitImmutable(M wrappedObject);

}
