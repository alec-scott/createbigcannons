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

- **Create Fly API gaps are filled in CBC's `port/` package for now** (Registrate, Create's sound-entry builder,
  `ICustomParticleData`, datagen helper stubs). Simulated (Aeronautics) carries its own copies of several of these in
  Create Fly's package; consolidating them into our Create Fly fork is the right long-term home, but it changes the
  Aeronautics build, so it waits until Aeronautics has been tested. Candidates for the fork: Registrate + `CreateRegistrate`,
  `AllSoundEvents.SoundEntryBuilder`, `ICustomParticleData`, a `DeployerRecipeSearchEvent` hook, public
  `AllTransfer.registerItemSide/registerFluidSide`.
- **JEI compat excluded for now**: it's built on Create's JEI helpers, which Create Fly organises differently. Port it once
  the mod runs. Copycats+, FramedBlocks and Curios compat excluded too (no 26.1 Fabric builds).

## Port mechanics (how things map)

| NeoForge 1.21.1 | Fabric 26.1.2 here |
|---|---|
| `@Mod` constructor + mod event bus | `fabric/CreateBigCannonsFabric` (runs registration in order; former common-setup work at the end) |
| `CBCClientNeoForge` | `fabric/CBCClientFabric`; missing events become mixins (mouse click/scroll, FOV) |
| `ViewportEvent.ComputeCameraAngles` | RPL's `CameraAngleHooks` (shared with RPL's screen shake) |
| `DeferredRegister` (tabs, particles, recipes) | direct `Registry.register` (`ModGroup.registerTabs()`, `IndexPlatform`) |
| `NewRegistryEvent` | `FabricRegistryBuilder` in `CBCRegistries` |
| `PayloadRegistrar` / `PacketDistributor` | Fabric payload networking (`network/CBCPacket`, `fabric/CBCNetworkFabric[Client]`) |
| `IItemHandler` capability | Fabric `Storage<ItemVariant>` via `ItemStorage.SIDED` (snapshot-based views from the 1.20.1 Fabric module) |
| NeoForge `FluidStack`/`FluidTank` | Create Fly `FluidStack` / `foundation.fluid.FluidTank`, exposed with `FluidInventoryStorage.of(...)` |
| Fluid amounts (mB) | **Fabric droplets**, 81 per mB: `IndexPlatform.getFluidConversionFactor()` = 81 |
| `RegisterGuiLayersEvent` | Fabric `HudElementRegistry` (`HudElement` uses `GuiGraphicsExtractor`) |
| `RegisterParticleProvidersEvent` | Fabric `ParticleProviderRegistry` (`port/.../ICustomParticleData`) |
| Create's `SoundEntryBuilder` | `port/create/AllSoundEvents` (Create Fly replaced it with a builder-less record) |
| `BlockEntity` NBT save/load | `ValueOutput`/`ValueInput`; nested tags via `utils/CBCValueIO` (`ExtraCodecs.NBT`, same on-disk shape) |
| Registrate `.addLayer(...)` | removed: 26.1 picks the render layer from texture transparency |

**Dedicated-server rule:** Create Fly ships a separate server jar **without** its `com.zurrtum.create.client.*` packages.
Common code must not touch those classes on paths a server runs (e.g. `CBCCommonMetal` no longer uses Catnip's `Lang`).

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
- RPL: added `CameraAngleHooks` so CBC can share the camera-roll mixin.
- CBC: build migrated; Create -> Create Fly imports mapped (208 by path, 8 moved, 32 gaps); relocated Registrate;
  Fabric entry points, platform layer, networking, registries, sounds, creative tab, item transfer (breech, ammo
  container, mounts); ValueInput/ValueOutput save data; many 26.1 renames. Compile errors 3133 -> ~1600.
- Next: fluid tanks (cast, drill, fluid shell), particles (26.1 particle API), renderers (submit model + shaders ->
  RenderPipeline), recipes, explosions (26.1 ServerExplosion), item models (1.21.4 item definitions), Sable compat.

## Trinkets (2026-11-24)

Gas mask Trinkets support restored from upstream's 1.20.1 Fabric code (`compat/trinkets/`), on Trinkets Updated 4.0.1
(Patbox, `eu.pb4.trinkets`, compileOnly, optional): gas masks go in `head/face`, which is widened to 2 slots as upstream
did (so goggles and a gas mask fit together); counts for the wearing and overlay predicates (non-cosmetic slots); the
renderer follows Create Fly's 26.1 goggle trinket renderer. Curios compat stays excluded (NeoForge only).
