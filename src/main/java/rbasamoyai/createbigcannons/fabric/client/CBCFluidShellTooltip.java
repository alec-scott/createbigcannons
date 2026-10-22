package rbasamoyai.createbigcannons.fabric.client;

import java.util.List;

import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.AbstractFluidShellBlockEntity;

/** PORT-NOTE(fabric): moved from IndexPlatform, which is loaded on servers; this uses Create Fly client classes. */
public class CBCFluidShellTooltip {

	public static void addFluidShellComponents(Fluid fluid, DataComponentPatch fluidData, long amount, List<Component> tooltip) {
		int capacity = AbstractFluidShellBlockEntity.getFluidShellCapacity();
		LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
		if (fluid != Fluids.EMPTY && amount > 0) {
			CreateLang.translate("gui.goggles.fluid_container").addTo(tooltip);
			CreateLang.text(" ")
				.add(CreateLang.fluidName(new FluidStack(fluid, 1, fluidData)).style(ChatFormatting.GRAY))
				.addTo(tooltip);

			CreateLang.text(" ")
				.add(CreateLang.builder()
				.add(CreateLang.number(amount).add(mb).style(ChatFormatting.GOLD))
				.text(ChatFormatting.GRAY, " / ")
				.add(CreateLang.number(capacity).add(mb).style(ChatFormatting.DARK_GRAY)))
				.addTo(tooltip);
		} else {
			CreateLang.translate("gui.goggles.fluid_container.capacity")
				.add(CreateLang.number(capacity).add(mb).style(ChatFormatting.GOLD))
				.style(ChatFormatting.GRAY)
				.addTo(tooltip);
		}
	}

}
