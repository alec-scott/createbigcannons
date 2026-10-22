package rbasamoyai.createbigcannons.utils;

/**
 * PORT-NOTE(fabric): Fabric (and Create Fly) measure fluids in droplets, 81 per millibucket. CBC's own numbers (cast
 * sizes, leak rates, shell capacity) are millibuckets, so convert at the tank boundary.
 */
public final class FluidUnits {
	public static final int DROPLETS_PER_MB = 81;

	private FluidUnits() {
	}

	public static int fromMb(int mb) {
		return mb * DROPLETS_PER_MB;
	}

	public static int toMb(long droplets) {
		return (int) (droplets / DROPLETS_PER_MB);
	}
}
