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

public class FlakCloudParticleData implements ParticleOptions, ICustomParticleData<FlakCloudParticleData> {

    private static final FlakCloudParticleData INSTANCE = new FlakCloudParticleData();

    private FlakCloudParticleData() {}

    public static FlakCloudParticleData instance() { return INSTANCE; }

	private static final MapCodec<FlakCloudParticleData> CODEC = MapCodec.unit(FlakCloudParticleData::instance);

    private static final StreamCodec<RegistryFriendlyByteBuf, FlakCloudParticleData> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override public MapCodec<FlakCloudParticleData> getCodec(ParticleType<FlakCloudParticleData> type) { return CODEC; }

    @Override public StreamCodec<? super RegistryFriendlyByteBuf, FlakCloudParticleData> getStreamCodec() { return STREAM_CODEC; }

    @Environment(EnvType.CLIENT)
	@Override
	public ParticleProvider<FlakCloudParticleData> getFactory() {
		return new FlakCloudParticle.Provider();
	}

	@Override public ParticleType<?> getType() { return CBCParticleTypes.FLAK_CLOUD.get(); }

}
