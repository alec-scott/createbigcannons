package rbasamoyai.createbigcannons.base;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import rbasamoyai.createbigcannons.port.registrate.util.nullness.NonNullSupplier;

public class LazySupplier<T> implements NonNullSupplier<T> {

	private Supplier<T> sup;
	private T value = null;

	public LazySupplier(@NotNull Supplier<T> sup) {
		this.sup = sup;
	}

	@Override
	public T get() {
		Supplier<T> sup = this.sup;
		if (sup != null) {
			this.value = sup.get();
			this.sup = null;
		}
		return this.value;
	}

}
