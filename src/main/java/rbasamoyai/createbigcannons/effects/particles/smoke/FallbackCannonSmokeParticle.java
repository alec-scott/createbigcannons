package rbasamoyai.createbigcannons.effects.particles.smoke;

import net.minecraft.util.RandomSource;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

public class FallbackCannonSmokeParticle extends CannonSmokeParticle {

	FallbackCannonSmokeParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
		super(level, x, y, z, dx, dy, dz, sprites, 0);
	}

	@Override public Layer getLayer() { return Layer.TRANSLUCENT; }

	public static class Provider implements ParticleProvider<FallbackCannonSmokeParticleData> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(FallbackCannonSmokeParticleData data, ClientLevel level, double x, double y, double z,
									   double dx, double dy, double dz, RandomSource random) {
			FallbackCannonSmokeParticle particle = new FallbackCannonSmokeParticle(level, x, y, z, dx, dy, dz, this.sprites);
			particle.quadSize = data.size();
			particle.friction = data.friction();
			particle.setLifetime(data.lifetime());
			return particle;
		}
	}

}
