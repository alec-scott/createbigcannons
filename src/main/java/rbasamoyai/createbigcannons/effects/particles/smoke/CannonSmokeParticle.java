package rbasamoyai.createbigcannons.effects.particles.smoke;

import net.minecraft.util.RandomSource;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.effects.particles.ParticleWindHandler;
import rbasamoyai.createbigcannons.index.CBCRenderTypes;

public class CannonSmokeParticle extends BaseAshSmokeParticle {

	private final float power;
	private final Vec3 wind = ParticleWindHandler.getWindForce(0);

	CannonSmokeParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, float power) {
		super(level, x, y, z, 0.1f, 0.1f, 0.1f, dx, dy, dz, 1, sprites, 1, 8, -0.05f, true);
		this.power = power;
		this.rCol = 1;
		this.gCol = 1;
		this.bCol = 1;
	}

	@Override
	public void tick() {
		super.tick();
		float f = this.onGround ? 1 : 0.5f;
		this.move(this.wind.x * f, this.wind.y, this.wind.z * f);
		float progress = Mth.clamp((float) this.age / (float) this.lifetime, 0, 1);
		this.alpha = this.lifetime == 0 || this.age >= this.lifetime ? 0 : 1 - progress * progress;
	}

	@Override
	public void setSpriteFromAge(SpriteSet sprite) {
		float progress = Mth.clamp((float) this.age / (float) this.lifetime * 1.5f, 0, 1);
		float inv = 1 - progress;
		float spriteProgress = 1 - inv * inv * inv * inv;
		if (!this.removed)
			this.setSprite(sprite.get((int) Math.floor(spriteProgress * this.lifetime), this.lifetime));
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		float f = (this.age + scaleFactor) / (float) this.lifetime * 32.0F;
		return this.quadSize * Mth.lerp(f, 0.9f, 1.0f);
	}

	@Override public Layer getLayer() { return Layer.TRANSLUCENT; }

	@Override
	public int getLightCoords(float partialTick) {
		float progress = 1 - Mth.clamp((this.age + partialTick) / (float) this.lifetime * 1.5f, 0, 1);
		float brightness = progress * progress * progress * progress;

		int i = super.getLightCoords(partialTick);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(brightness * 240f);
		if (j > 240)
			j = 240;
		return j | k << 16;
	}

	public static class Provider implements ParticleProvider<CannonSmokeParticleData> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(CannonSmokeParticleData data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			// PORT-NOTE(mc26.1): the gradient smoke shader (CBCRenderTypes.CANNON_SMOKE_PARTICLE) is not ported yet; always use
			// the shader-compatible fallback particle, as the useShaderCompatibleGraphics option did.
			level.addParticle(new FallbackCannonSmokeParticleData(data), true, false, x, y, z, dx, dy, dz);
			return null;
		}
	}

}
