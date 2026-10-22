package rbasamoyai.createbigcannons.multiloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import rbasamoyai.createbigcannons.port.create.foundation.particle.ICustomParticleData;

public class ParticlesPlatform {
	@Environment(EnvType.CLIENT)
	public static <T extends ParticleOptions> void register(ICustomParticleData<T> data, ParticleType<T> type, ParticleEngine particles) {
		data.register(type);
	}
}
