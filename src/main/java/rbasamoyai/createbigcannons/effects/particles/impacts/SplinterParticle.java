package rbasamoyai.createbigcannons.effects.particles.impacts;

import net.minecraft.util.RandomSource;

import org.joml.Quaternionf;

import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.multiloader.IndexPlatform;

public class SplinterParticle extends CBCBlockParticle {
	protected TextureAtlasSprite sprite1;
	protected float yaw;
	protected float pitch;

	SplinterParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, state);
		this.roll = this.random.nextFloat() * Mth.PI;
		this.yaw = this.random.nextFloat() * - 0.5f * Mth.HALF_PI;
		this.pitch = 0;//level.random.nextFloat() * - 0.5f * Mth.HALF_PI;
		this.gravity = 1f;
		this.friction = 0.99f;
		this.quadSize = 0.3f + this.random.nextFloat() * 0.1f;
		this.setSize(0.1f, 0.1f);
		this.setLifetime(30 + this.random.nextInt(8));
		BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
		int i = tintSource == null ? 0xFFFFFF : tintSource.colorAsTerrainParticle(state, level, BlockPos.containing(x, y, z));
		this.rCol = (float)(i >> 16 & 0xFF) / 255.0F;
		this.gCol = (float)(i >> 8 & 0xFF) / 255.0F;
		this.bCol = (float)(i & 0xFF) / 255.0F;
	}

	protected void setSecondarySprite(TextureAtlasSprite sprite) {
		this.sprite1 = sprite;
	}

	/**
	 * PORT-NOTE(mc26.1): particles are now extracted as plain quads ({@link QuadParticleRenderState}), so the custom
	 * splinter shader that cut the block texture with a particle-shaped mask ({@code sprite1}) is gone. The shard keeps its
	 * velocity-aligned orientation but renders as a block-textured quad.
	 */
	@Override
	public void extract(QuadParticleRenderState state, Camera renderInfo, float partialTicks) {
        Quaternionf quaternion = new Quaternionf();
        quaternion.mul(Axis.ZP.rotation(this.roll));
        quaternion.mul(Axis.YP.rotation(this.yaw));
        quaternion.mul(Axis.XP.rotation(this.pitch));
        Vec3 vec31 = new Vec3(this.xd, this.yd, this.zd).normalize();
        Vec3 vec32 = new Vec3(1, 0, 0);
        double dot = vec32.dot(vec31);
        if (Math.abs(dot + 1) < 1e-4d) { // anti-parallel
            quaternion.mul(Axis.YP.rotation(Mth.PI));
        } else {
            Vec3 cross = vec32.cross(vec31);
            quaternion.mul(new Quaternionf((float) cross.x, (float) cross.y, (float) cross.z, 1f + (float) dot));
        }
        quaternion.normalize();
        this.extractRotatedQuad(state, renderInfo, quaternion, partialTicks);
    }


    @Override
	public void tick() {
		if (this.age >= this.lifetime - 10) {
			this.alpha -= 0.1f;
		}
		super.tick();
	}

	public static class Provider implements ParticleProvider<SplinterParticleData> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) { this.sprites = sprites; }

		@Override
		public Particle createParticle(SplinterParticleData type, ClientLevel level, double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			BlockState blockstate = type.state();
			if (blockstate.isAir() || blockstate.is(Blocks.MOVING_PISTON))
				return null;
			if (CBCConfigs.client().useShaderCompatibleGraphics.get()) {
				level.addParticle(new CBCBlockParticleData(blockstate), x, y, z, xSpeed, ySpeed, zSpeed);
				return null;
			}
			SplinterParticle particle = new SplinterParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, blockstate);
			particle.setSecondarySprite(this.sprites.get(level.getRandom()));
			return particle;
		}
	}

}
