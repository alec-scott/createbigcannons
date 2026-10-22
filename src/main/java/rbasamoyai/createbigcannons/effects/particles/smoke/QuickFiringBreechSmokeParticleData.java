package rbasamoyai.createbigcannons.effects.particles.smoke;

import com.mojang.serialization.MapCodec;
import rbasamoyai.createbigcannons.port.create.foundation.particle.ICustomParticleData;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import rbasamoyai.createbigcannons.index.CBCParticleTypes;

public class QuickFiringBreechSmokeParticleData implements ParticleOptions, ICustomParticleData<QuickFiringBreechSmokeParticleData> {

    private static final QuickFiringBreechSmokeParticleData INSTANCE = new QuickFiringBreechSmokeParticleData();

    private QuickFiringBreechSmokeParticleData() {}

    public static QuickFiringBreechSmokeParticleData instance() { return INSTANCE; }

	private static final MapCodec<QuickFiringBreechSmokeParticleData> CODEC = MapCodec.unit(QuickFiringBreechSmokeParticleData::instance);

    private static final StreamCodec<RegistryFriendlyByteBuf, QuickFiringBreechSmokeParticleData> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override public MapCodec<QuickFiringBreechSmokeParticleData> getCodec(ParticleType<QuickFiringBreechSmokeParticleData> type) { return CODEC; }

    @Override public StreamCodec<? super RegistryFriendlyByteBuf, QuickFiringBreechSmokeParticleData> getStreamCodec() { return STREAM_CODEC; }

    @Environment(EnvType.CLIENT)
	@Override
	public ParticleProvider<QuickFiringBreechSmokeParticleData> getFactory() {
		return new QuickFiringBreechSmokeParticle.Provider();
	}

	@Override public ParticleType<?> getType() { return CBCParticleTypes.QUICK_FIRING_BREECH_SMOKE.get(); }

}
