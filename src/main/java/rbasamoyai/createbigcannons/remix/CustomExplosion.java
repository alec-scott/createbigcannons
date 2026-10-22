package rbasamoyai.createbigcannons.remix;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.network.ClientboundCBCExplodePacket;

/**
 * PORT-NOTE(mc26.1): {@link Explosion} is now a server-only interface implemented by {@link ServerExplosion}, whose
 * {@code explode()} computes, damages and destroys in one call; clients no longer build explosion objects. CBC's
 * explosions therefore extend {@link ServerExplosion} on the server, and the client-side sound and particles that used to
 * run from {@code finalizeExplosion(true)} live in {@link Effects} subclasses, built from the explode packet.
 * {@code ServerExplosionMixin} supplies the terrain-damage, block-edit and entity-radius hooks.
 */
public interface CustomExplosion {

	default void editBlock(Level level, BlockPos pos, BlockState blockState, FluidState fluidState, float power) {
	}

    void setCanDamageTerrain(boolean canDamageTerrain);

    boolean canDamageTerrain();

    float getEntityRadius();

	void sendExplosionToClient(ServerPlayer player);

	abstract class Impl extends ServerExplosion implements CustomExplosion {
		protected final ServerLevel level;
		protected final double x;
		protected final double y;
		protected final double z;
		protected final float blockSize;
        protected final float entitySize;
        protected boolean canDamageTerrain = true;

		public Impl(Level level, @Nullable Entity source, @Nullable DamageSource damageSource,
					@Nullable ExplosionDamageCalculator calculator, double toBlowX, double toBlowY, double toBlowZ, float blockRadius,
					float entityRadius, boolean fire, BlockInteraction interaction) {
			super((ServerLevel) level, source, damageSource, calculator, new Vec3(toBlowX, toBlowY, toBlowZ), blockRadius, fire, interaction);
			this.level = (ServerLevel) level;
			this.x = toBlowX;
			this.y = toBlowY;
			this.z = toBlowZ;
			this.blockSize = blockRadius;
            this.entitySize = entityRadius;
		}

        @Override
        public void setCanDamageTerrain(boolean canDamageTerrain) {
            this.canDamageTerrain = canDamageTerrain;
        }

        @Override
        public boolean canDamageTerrain() {
            return this.canDamageTerrain;
        }

        @Override public float getEntityRadius() { return this.entitySize; }

        public boolean interactsWithBlocks() { return this.getBlockInteraction() != BlockInteraction.KEEP; }
	}

	/** Client-side half of an explosion: what {@code finalizeExplosion(true)} used to play on the client. */
	abstract class Effects {
		protected final Level level;
		protected final double x;
		protected final double y;
		protected final double z;
		protected final float blockSize;
		protected final float entitySize;

		protected Effects(Level level, ClientboundCBCExplodePacket packet) {
			this.level = level;
			this.x = packet.x();
			this.y = packet.y();
			this.z = packet.z();
			this.blockSize = packet.blockPower();
			this.entitySize = packet.entityPower();
		}

		public void play() {
			this.playLocalSound(this.level, this.x, this.y, this.z);
			this.spawnParticles();
		}

		protected void playLocalSound(Level level, double x, double y, double z) {
		}

		protected void spawnParticles() {
		}
	}

    class CustomDamageCalculator extends ExplosionDamageCalculator {
        @Override
        public float getEntityDamageAmount(Explosion explosion, Entity entity, float exposure) {
            float f = explosion instanceof CustomExplosion customExplosion ? customExplosion.getEntityRadius() : explosion.radius();
            f *= 2;
            Vec3 vec3 = explosion.center();
            double d0 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f;
            double d1 = (1.0 - d0) * (double) exposure;
            return (float)((d1 * d1 + d1) / 2.0 * 7.0 * (double)f + 1.0);
        }
    }

}
