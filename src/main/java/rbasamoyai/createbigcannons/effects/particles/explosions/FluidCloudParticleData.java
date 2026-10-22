package rbasamoyai.createbigcannons.effects.particles.explosions;

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

public class FluidCloudParticleData implements ParticleOptions, ICustomParticleData<FluidCloudParticleData> {

    private static final FluidCloudParticleData INSTANCE = new FluidCloudParticleData();

    private FluidCloudParticleData() {}

    public static FluidCloudParticleData instance() { return INSTANCE; }

	private static final MapCodec<FluidCloudParticleData> CODEC = MapCodec.unit(FluidCloudParticleData::instance);
    private static final StreamCodec<? super RegistryFriendlyByteBuf, FluidCloudParticleData> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override public MapCodec<FluidCloudParticleData> getCodec(ParticleType<FluidCloudParticleData> type) { return CODEC; }
	@Override public StreamCodec<? super RegistryFriendlyByteBuf, FluidCloudParticleData> getStreamCodec() { return STREAM_CODEC; }

    @Environment(EnvType.CLIENT)
	@Override
	public ParticleProvider<FluidCloudParticleData> getFactory() {
		return new FluidCloudParticle.Provider();
	}

	@Override public ParticleType<?> getType() { return CBCParticleTypes.FLUID_CLOUD.get(); }

}
