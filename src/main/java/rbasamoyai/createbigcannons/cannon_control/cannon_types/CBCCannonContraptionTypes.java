package rbasamoyai.createbigcannons.cannon_control.cannon_types;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import rbasamoyai.createbigcannons.CreateBigCannons;

public enum CBCCannonContraptionTypes implements ICannonContraptionType {
	BIG_CANNON,
	DROP_MORTAR,
	AUTOCANNON,
	HANDLE_AUTOCANNON;

	private static final Map<Identifier, CBCCannonContraptionTypes> BY_ID =
		Arrays.stream(values()).collect(Collectors.toMap(CBCCannonContraptionTypes::getId, Function.identity()));

	private final Identifier id = CreateBigCannons.resource(this.name().toLowerCase(Locale.ROOT));

	CBCCannonContraptionTypes() {
		CannonContraptionTypeRegistry.register(this.id, this);
	}

	@Override public Identifier getId() { return this.id; }

	@Nullable public static CBCCannonContraptionTypes byId(Identifier loc) { return BY_ID.get(loc); }

	public static void register() {
	}

}
