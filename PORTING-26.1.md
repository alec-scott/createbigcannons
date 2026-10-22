# 26.1.2 Fabric Port — Working Log

Branch `26.1-fabric`, from `create-v6-1.21.1` (CBC 5.11.7, NeoForge-only).
Goal: Create Big Cannons on **Fabric 26.1.2**, running on **Create Fly** (our fork at `../Create-Fly`), alongside the
Create Aeronautics port and the full testing modpack.

## Starting point

| | |
|---|---|
| Upstream | `Cannoneers-of-Create/CreateBigCannons` (remote `upstream`); fork `alec-scott/createbigcannons` (`origin`) |
| Base branch | `create-v6-1.21.1`: NeoForge 1.21.1 only (`enabled_platforms=neoforge`), 668 Java files |
| Fabric reference | upstream `create-v6-1.20.1`: multi-loader, has a `fabric/` module (100 files) with Fabric versions of CBC's platform code |
| Published Fabric builds | 1.20.1 only. Nobody has published CBC for Fabric on 1.21+ |
| Dependency | **Ritchie's Projectile Library (RPL)**, also NeoForge-only on 1.21.1 → ported first, see below |

Three ports at once:
1. **NeoForge → Fabric.** Entry points, events, networking, capabilities → Fabric Transfer API, client registration.
   CBC keeps a small `multiloader` layer (`IndexPlatform`, `NetworkPlatform`, `ParticlesPlatform`, `PonderPlatform`,
   `EnvExecute`, `EntityTypeConfigurator`); the 1.20.1 branch has Fabric implementations of each.
2. **Minecraft 1.21.1 → 26.1.2.** Same API changes as the Aeronautics port (see `../create-aeronautics-fabric/PORTING-26.1.md`):
   `Identifier`, render-state/submit rendering, `ValueInput`/`ValueOutput`, `SavedDataType`, `ChunkPos` record, etc.
3. **Create → Create Fly.** Create Fly is a Fabric rewrite of Create under `com.zurrtum.create`. Mostly a package move,
   with client-side classes under `com.zurrtum.create.client.*`, a few moved classes (e.g. `BlockEntityBehaviour` →
   `api.behaviour`), and **no Registrate or data-generation helpers**. Missing APIs may be added to our Create Fly fork.

## Ritchie's Projectile Library

Local clone `../RitchiesProjectileLib`, branch `26.1-fabric` (no fork yet: fork it to push). **Builds** (2026-10-22).
- Single-target Fabric build; upstream's Stonecutter multiversion setup and Forge/NeoForge modules removed on that branch.
- Porting Lib (no 26.1 release) replaced: networking on Fabric payloads; screen shake via a `Camera` mixin (adds roll).
- **API change for CBC:** `ProjectileBurstRenderer<T, S extends ProjectileBurstRenderer.State>` is submit-based;
  subclasses override `submitSubProjectile(state, poseStack, collector, camera)` instead of `renderSubProjectile`.
- Its `ServerEntity` mixin now filters vanilla movement packets by type (26.1 `ServerEntity.Synchronizer`).

## Decisions

- **Base on the 1.21.1 NeoForge branch**, not the 1.20.1 multi-loader one: the Minecraft API gap to 26.1 is far
  smaller, and CBC's feature set is current. Fabric-specific code is ported from the 1.20.1 `fabric/` module.
- **Registrate:** reuse the Fabric 26.1 Registrate already ported in the Aeronautics repo (Simulated module), but
  **relocated into a CBC package**, along with its `CreateRegistrate` shim and datagen stubs. Two mods shipping the
  same class names is fragile on Fabric (first one loaded wins), and CBC shouldn't depend on Aeronautics.
- **Datagen stubbed** (as in Aeronautics): the committed `src/generated/resources` are used as-is.

## Plan

1. ✅ RPL builds on Fabric 26.1.2.
2. CBC build migration: Fabric Loom (non-remap) on 26.1.2, Create Fly (local jar), RPL (nested), Fabric API,
   Forge Config API Port; `neoforge.mods.toml` → `fabric.mod.json`; access transformer → access widener.
3. Relocated Registrate + `CreateRegistrate` shim + datagen stubs.
4. Mechanical rewrites: Create → Create Fly packages; Minecraft renames.
5. NeoForge → Fabric platform code (from the 1.20.1 `fabric/` module).
6. Compile-error grind, module by module.
7. Runtime: smoke tests (with mixin audit), then the full modpack, then load into a world and fire a cannon.

## Session log

### 2026-10-22
- Cloned the fork; added `upstream`. Assessed the three-way port (above).
- Ported RPL to Fabric 26.1.2 (builds).
