package rbasamoyai.createbigcannons.effects.particles.impacts;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * PORT-NOTE(mc26.1): this drew two crossed, velocity-aligned strips with a custom entity render type. 26.1 particles can
 * only extract camera-facing quads, so the streak is now a short row of small quads along the velocity, textured with a
 * plain white block sprite and tinted by the spark colour.
 */
public class SparkParticle extends SingleQuadParticle {

	private static final int SEGMENTS = 4;

	SparkParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float r, float g, float b) {
		super(level, x, y, z, Minecraft.getInstance().getModelManager().getBlockStateModelSet()
			.getParticleMaterial(Blocks.WHITE_CONCRETE.defaultBlockState()).sprite());
		this.xd = dx;
		this.yd = dy;
		this.zd = dz;
		this.rCol = r;
		this.gCol = g;
		this.bCol = b;
		this.gravity = 0.5f;
		this.friction = 0.8f;
		this.quadSize = 1 / 16f;
	}

	@Override
	public void extract(QuadParticleRenderState state, Camera camera, float partialTicks) {
		Quaternionf rotation = new Quaternionf();
		this.getFacingCameraMode().setRotation(rotation, camera, partialTicks);
		Vec3 cam = camera.position();
		double dx = this.x - this.xo;
		double dy = this.y - this.yo;
		double dz = this.z - this.zo;
		float x1 = (float) (this.xo + partialTicks * dx - cam.x());
		float y1 = (float) (this.yo + partialTicks * dy - cam.y());
		float z1 = (float) (this.zo + partialTicks * dz - cam.z());
		Vec3 vel = new Vec3(dx, dy, dz);
		double length = Math.min(vel.length(), 1);
		Vector3f step = vel.lengthSqr() < 1e-8 ? new Vector3f() : vel.normalize().scale(-length / SEGMENTS).toVector3f();
		for (int i = 0; i < SEGMENTS; ++i) {
			this.extractRotatedQuad(state, rotation, x1 + step.x * i, y1 + step.y * i, z1 + step.z * i, partialTicks);
		}
	}

	@Override
	protected Layer getLayer() {
		return Layer.OPAQUE_TERRAIN;
	}

	@Override
	public void tick() {
		if (this.onGround) {
			this.remove();
			return;
		}
		this.rCol *= 0.99f;
		this.gCol *= 0.98f;
		this.bCol *= 0.97f;
		super.tick();
	}

	@Override
	protected int getLightCoords(float partialTick) {
		float progress = 1 - Mth.clamp((this.age + partialTick) / (float) this.lifetime, 0, 1);
		float brightness = Math.max(0.75f, progress * progress * progress * progress);

		int i = super.getLightCoords(partialTick);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(brightness * 240f);
		if (j > 240)
			j = 240;
		return j | k << 16;
	}

	public static class Provider implements ParticleProvider<SparkParticleData> {
		@Override
		public Particle createParticle(SparkParticleData type, ClientLevel level, double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			Vector3f color = type.color();
			SparkParticle particle = new SparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, color.x(), color.y(), color.z());
			int lifetime = 12 + level.getRandom().nextInt(12);
			particle.setLifetime(lifetime);
			return particle;
		}
	}

}
