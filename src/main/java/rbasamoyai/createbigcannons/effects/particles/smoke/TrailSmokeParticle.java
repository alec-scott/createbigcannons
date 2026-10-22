package rbasamoyai.createbigcannons.effects.particles.smoke;

import net.minecraft.util.RandomSource;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;

public class TrailSmokeParticle extends SingleQuadParticle {

	TrailSmokeParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
		this.gravity = 3.0E-6F;
		this.friction = 1f;
		this.xd = xd;
		this.yd = yd + this.random.nextDouble() / 500d;
		this.zd = zd;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
			this.xd *= this.friction;
			this.yd *= this.friction;
			this.zd *= this.friction;
			this.xd += this.random.nextFloat() / 5000f * (this.random.nextBoolean() ? 1 : -1);
			this.zd += this.random.nextFloat() / 5000f * (this.random.nextBoolean() ? 1 : -1);
			this.yd -= this.gravity;
			this.move(this.xd, this.yd, this.zd);
			if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
				this.alpha -= 0.015F;
			}
		} else {
			this.remove();
		}
	}

	@Override public Layer getLayer() { return Layer.TRANSLUCENT; }

	public static class Provider implements ParticleProvider<TrailSmokeParticleData> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(TrailSmokeParticleData data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			TrailSmokeParticle particle = new TrailSmokeParticle(level, x, y, z, dx, dy, dz, this.sprites.get(random));
			particle.scale(3.0f);
			particle.setSize(0.25f, 0.25f);
			particle.setAlpha(0.35f);
			particle.setLifetime(data.lifetime());
			return particle;
		}
	}

}
