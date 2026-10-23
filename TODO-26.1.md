# Create Big Cannons → 26.1.2 Fabric port: status and TODO

Paused on 2026-10-23 to go back to Aeronautics development. This file is the hand-off for picking the CBC port up
again. The overall design notes (mapping tables, decisions, the dedicated-server rule) are in `PORTING-26.1.md`.
This file covers **where the work stopped and what is left**, in as much detail as was known when it paused.

## 1. Where things stand

| Item | State |
|---|---|
| Repo / branch | `C:\Users\akrsc\Documents\git\createbigcannons`, branch `26.1-fabric` (fork: `alec-scott/createbigcannons`) |
| Last commit | See `git log`. Everything is committed locally; nothing has been pushed. |
| RPL dependency | `C:\Users\akrsc\Documents\git\RitchiesProjectileLib`, branch `26.1-fabric` (local only, there is no fork yet; fork it before pushing). Jar: `build/libs/ritchiesprojectilelib-2.1.2+mc.26.1.2-fabric.jar` |
| Create Fly | Used as a jar (`create_fly_jar` in `build.gradle`). Gaps in the Create Fly API are bridged in CBC's `port/` package and with CBC mixins. Moving them into the Create Fly fork was deferred until Aeronautics is tested, because it would change the Aeronautics build. |
| `compileJava` | **Compiles** (0 errors; it started at about 3,100). Jar: `build/libs/createbigcannons-5.11.7+mc.26.1.2-fabric.jar` (RPL ships as its own jar, not nested). |
| Runtime testing | **Server and client smoke tests pass; the full modpack client smoke test passes; server-side gameplay is verified in a world.** Client in-world rendering is **not** verified yet; see section 0. |

## 0. Runtime status (updated 2026-10-23, after compile)

Verified with the Aeronautics production smoke harness (`:aeronautics:runProductionServerSmoke` / `runProductionClientSmoke` with `-Psmoke_extra_mods=<CBC jar>,<RPL jar>[,<modpack jars>]`). The harness runs a mixin audit, so every CBC mixin target resolves against Create Fly / MC 26.1.2.

| Test | Result |
|---|---|
| Dedicated server boot + mixin audit | Pass. No errors; recipes, loot tables and tags all load. |
| Client boot to title + mixin audit | Pass. Only `particle` texture-reference warnings remain (cosmetic, models below). |
| Client boot with all 73 `v26.1-testing` pack mods | Pass for CBC. The harness reports errors from other mods only (World Alive invalid texture path, malilib/tweakeroo first-run config). |
| Server world gameplay (`-Psmoke_commands_file`, fresh world) | Pass. Every CBC block placed (BE creation/ticking), two world saves, every projectile type summoned and ticked. Impact-fuzed HE shell, shrapnel shell and drop-mortar shell detonate and crater stone. Unfuzed shells embed (`InGround`) and count down, as upstream does. |
| Client joined into a world | **Not done.** A non-smoke client launched through the harness (`--quickPlaySingleplayer`) shows a blank white window and never finishes loading, even without the pack, so this is a launch-environment problem, not CBC. Test in the Prism instance instead. |

Fixes found by the runtime tests (all committed):
- Mixin targets: Create Fly compiles `state.is(AllBlocks.X)` to `BlockState.is(Ljava/lang/Object;)Z` (a generic helper), not `is(Block)`. `LevelRenderer.tick` now takes a `Camera`.
- `BlockRecipeIngredient`: ItemStacks are built lazily, because item components aren't bound during mod init.
- Unbored screw breeches registered their item twice (an explicit `.item()` plus the one in the `screwBreechUnbored` transformer).
- `EntityTypeConfigurator` now accepts the `FabricEntityTypeBuilder` that the port Registrate hands out.
- Recipe JSON converted to 26.1 / Create Fly. Scripts: `convert_recipes.pl` and `fixup_recipes.pl` in the session scratchpad; the details are in the commit message. Missing `processing_time` is written as 0, matching Create 6's default. The one-step sequenced assembly `filling_autocannon_cartridge` became a plain `create:deploying` recipe, because Create Fly rejects sequences with fewer than 2 steps. `c:plates/steel` and `c:ingots/tin` recipes are gated with `fabric:tags_populated`.
- Block loot tables of item-less blocks (built-up cannon, cannon casts) contained a `minecraft:air` entry; those pools were dropped.
- Item definitions `assets/createbigcannons/items/*.json` (194) were generated from the item models. Predicate `overrides` became `range_dispatch` over `createbigcannons:{formed,big_cartridge_filled,fuze_state}` (`fabric/client/CBCItemModelProperties`). The gas mask uses a `display_context` select (worn = `block/gas_mask`), which replaces the deleted `GasMaskModel`.

**Note:** the generated resources were edited directly, because datagen isn't ported. Re-running upstream datagen would revert these edits.

**Next:** put the CBC and RPL jars in the Prism `v26.1-testing` instance and play through section 5.4. Then fix the `particle` texture references (cannon casts, autocannon seats, fuze, sliding breechblock / screw lock items, rotating mount) and write the CBC in-game test doc.

Error count history: 3133 → 2819 → 2653 → 2357 → 2239 → 1957 → 1621 → 1472 → 1420 → 1259 → 1028 → 968 → 863 → 770 →
665 → 599 → 582 → 551 → 459 → 439.

### Build / helper commands

```bash
cd /c/Users/akrsc/Documents/git/createbigcannons
export JAVA_HOME=/c/Users/akrsc/AppData/Roaming/PrismLauncher/java/java-runtime-epsilon
./gradlew compileJava --console=plain > /tmp/cbc-build.log 2>&1
grep -E "^\S.*\.java:[0-9]+: error:" /tmp/cbc-build.log | sort -u > /tmp/cbc-errs.txt; wc -l < /tmp/cbc-errs.txt
```

Helper scripts are in the Claude scratchpad. They are session-temporary, so copy any you want to keep:
`C:\Users\akrsc\AppData\Local\Temp\claude\c--Users-akrsc-Documents-git\a22250c0-b9eb-4805-a074-27db7661370e\scratchpad\scripts\`

- `errs_by_file.pl <errs.txt> [limit]`: error count per file.
- `errlines.pl <errs.txt> <regex>`: prints file:line plus the offending source line.
- `fix_symbol_lines.pl <build.log> <symbol-regex> '<perl s///>'`: applies a substitution on every line javac reports a given missing symbol for.
- `fix_optional.pl`, `fix_addparticle.pl`, `bridge_tooltips.pl`, `fix_contraption_nbt.pl`, `fix_fire_explode.pl`, `split_explosion.pl`, `sprite_ctor.pl`: one-off converters, already applied.
- Reference sources: MC 26.1.2 is decompiled in `scratchpad/mc-src`, and upstream Create 1.21.1 is in `scratchpad/dl-create-src/src`. Create Fly is `C:\Users\akrsc\Documents\git\Create-Fly\src\{main,client}\java`. Upstream CBC's 1.20.1 Fabric module is reachable as `git show upstream/create-v6-1.20.1:fabric/...` and is very useful for Fabric equivalents.

## 2. What I was doing at the moment it paused

**Menus with extra opening data (in progress, nothing edited yet).**

- `port/registrate/util/entry/MenuEntry.open(player, name, provider, Consumer<Object> extraData)` is a **stub**: there is a `// FIXME`, and it never writes the extra data. `MenuBuilder` builds an `ExtendedMenuType<T, Object>` that needs `extraDataCodec(...)`, and none is set, so registering an extended menu would throw `IllegalStateException`.
- CBC menus that need data: `CBCMenuTypes.SET_TIMED_FUZE`, `SET_PROXIMITY_FUZE`, `SET_DELAYED_IMPACT_FUZE`, `SET_DELAYED_INERTIA_FUZE` and `AUTOCANNON_AMMO_CONTAINER`. Each client factory is `XContainer.getClientMenu(MenuType<X>, int id, Inventory inv, RegistryFriendlyByteBuf buf)`. Each server opener writes with `buf.writeBoolean / writeVarInt / writeBlockPos / ItemStack.STREAM_CODEC.encode`; see `AutocannonAmmoContainerBlock.useWithoutItem`, `AutocannonAmmoContainerItem`, and the `DelayedImpact/DelayedInertia/Proximity/TimedFuzeItem` classes.
- **Planned fix:**
  1. Opening data type = `byte[]` with `ByteBufCodecs.BYTE_ARRAY`.
  2. `MenuEntry.open(..., Consumer<RegistryFriendlyByteBuf> extraData)` writes into a `RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess())`, copies the bytes, and returns them from `ExtendedMenuProvider.getScreenOpeningData`.
  3. `MenuBuilder` uses `ExtendedMenuType<T, byte[]>((id, inv, bytes) -> factory.create(type, id, inv, new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(bytes), inv.player.registryAccess())), ByteBufCodecs.BYTE_ARRAY)`.
  4. Change the `ForgeMenuFactory` signature from `Object data` to `RegistryFriendlyByteBuf`.
  5. Change the `buf -> {...}` lambdas at the open sites to the new consumer type.
- The same files also need these fixes:
  - `ItemStack.STREAM_CODEC.encode(buf, new ItemStack(this))` is fine once `buf` is a `RegistryFriendlyByteBuf`.
  - `this.getDescription()` becomes `this.getName(stack)` (or `Component.translatable(getDescriptionId())`).
  - `.use(...).getResult()` becomes `.use(...)`, because `use` returns `InteractionResult` now.
  - `AutocannonAmmoContainerMenu`: `ClickType` moved or was renamed (check `net.minecraft.world.inventory.ContainerInput`/`ClickType` in mc-src). `playerInv.selected` is private; use `getSelectedSlot()`. `Slot.getNoItemIcon()` now returns an `Identifier` sprite (no `Pair` with `BLOCK_ATLAS`).
  - `AutocannonAmmoContainerBlockEntity` (10 errors):
    - `Component.Serializer.toJson` / `parseCustomNameSafe`: store the name with `ComponentSerialization.CODEC` through `ValueOutput.storeNullable("CustomName", ...)`.
    - `applyImplicitComponents(DataComponentInput)` becomes `applyImplicitComponents(DataComponentGetter)`.
    - `startOpen/stopOpen(Player)` now take a `ContainerUser`.
    - `canInteractWithBlock` moved (use `Container.stillValidBlockEntity(this, player)`).
    - `saveToItem(stack, registries)` and `CBCUtils.saveComponentsToStructureTag`: `saveAdditional` takes a `ValueOutput` now.

## 3. Remaining compile errors, grouped (in rough order of priority)

### 3a. Common (server) code: fix these first, because a dedicated server must load them

1. **Menus** (section 2).
2. **Data-component BE hooks**: `applyImplicitComponents(DataComponentInput)` → `applyImplicitComponents(DataComponentGetter)` and `collectImplicitComponents(DataComponentMap.Builder)` (check the exact 26.1 names in `BlockEntity`).
   - Files: `FluidShellBlockEntity`, `BigCannonProjectileBlockEntity`, `FuzedBlockEntity`, `BigCartridgeBlockEntity`, `AutocannonAmmoContainerBlockEntity`.
   - `writeSafeComponentsForItemRequirement` is a Create method; check Create Fly's name.
3. **FluidShellBlock / FluidShellBlockItem** still use NeoForge `FluidTank`: switch them to `utils/CBCFluidTank` (has `readFromNBT(Provider, CompoundTag)`). Use `stack.getComponentChanges()` in place of `getComponents()`. The fluid amount in `EndFluidStack` is mB, so convert with `FluidUnits.toMb`.
4. **Item tooltips calling other items**: `fuze.getItem().appendHoverText(fuze, ctx, subTooltip, flag)` in `FuzedProjectileBlockItem`, `AutocannonCartridgeItem` and `FlakAutocannonRoundItem`. These must call the CBC 4-arg List overload (cast to `FuzeItem`/`AutocannonRoundItem`), or the 5-arg `(stack, ctx, TooltipDisplay.DEFAULT, subTooltip::add, flag)`. Background: `bridge_tooltips.pl` turned every CBC `appendHoverText(ItemStack, TooltipContext, List<Component>, TooltipFlag)` into an overload, with a bridging 26.1 override at the top of each class chain. `FuzeItem` has a base List method.
5. **`TrafficConeBlockItem`**: `Equipable` is gone. Use the `DataComponents.EQUIPPABLE` component in `CBCBlocks`/item properties (as already done for `GAS_MASK` in `CBCItems`).
6. **`BigCartridgeBlock` / `PowderChargeBlock`**:
   - `dimensionType().ultraWarm()` → environment attribute (`level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)` or similar; check mc-src).
   - `stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand))` → `hand.asEquipmentSlot()` / `stack.hurtAndBreak(1, player, hand)`.
   - `projectile.mayInteract(level, pos)` → `mayInteract(ServerLevel, BlockPos)`.
   - `data.nbt().getInt("Power")` → `getIntOr`.
7. **`GasCloudEntity`**: `applyInstantenousEffect(...)` gained a `ServerLevel` parameter. `tag.get("potion_contents")` on a `ValueInput`: use `tag.read("potion_contents", PotionContents.CODEC)`.
8. **`SmokeEmitterEntity`, `PrimedPropellant`, `ShrapnelBurst`, `MountedBigCannonContraption`, `AbstractAutocannonProjectile`, `DefaultFluidCompat`, `FuzedProjectileBlock`, `PoleMoverBlockEntity`, `CannonCarriageEntity:365`, `PitchOrientedContraptionEntity:57`, `CannonCastBlock`, `FinishedCannonCastBlock`, `ContraptionRemix` (6), `CBCUtils` (6), `MountedAutocannonContraption`**: these are one-liners of the same kinds already fixed elsewhere:
   - `makeBoundingBox()` → `makeBoundingBox(Vec3)`.
   - `newDoubleList` → `tag.store(key, Vec3.CODEC, v)`.
   - `updateInWaterStateAndDoFluidPushing` was renamed.
   - `entity.hurt` → `hurtServer(ServerLevel, ...)`.
   - `level.random` is protected, so use `level.getRandom()`.
   - `getNearbyPlayers(TargetingConditions, null, AABB)` gained a `ServerLevel`.
   - `EntityType.create(level, reason)` returns `@Nullable T`; the generic is wrong for `PITCH_ORIENTED_CONTRAPTION` (cast).
   - Run `errlines.pl /tmp/cbc-errs.txt .` on each.
9. **`CreateBigCannons.java:41`**: `CreateRegistrate.create(MOD_ID)` returns the base `Registrate`. Use `new CreateRegistrate(MOD_ID)` (port class constructor), or add a static `create` to the port `CreateRegistrate`.
10. **`AbstractCannonProjectile:82`**: `EntityDataSerializers.VECTOR3` is now `Vector3fc`. Change the `EntityDataAccessor<Vector3f>` to `Vector3fc` and adapt `getOrientation`/`setOrientation`.
11. **Compat (Sable / Simulated / framedblocks)**:
    - `compat/sable/*` (SableCompat, SableForceGroupsCompat, ShellSubLevelImpactCallback) still use the NeoForge event bus (`IEventBus`). Port them to Sable's Fabric API, or Fabric events, as Aeronautics does.
    - `compat/simulated/SimulatedCompat` (2 errors).
    - `mixin/compat/framedblocks/FramedAdjustableDoubleBlockEntityAccessor`: FramedBlocks has no 26.1 Fabric build. Remove the mixin from `createbigcannons.mixins.json` and exclude it in `build.gradle`, as was done for the `compat/framedblocks` package.
12. **`port/registrate/providers/RegistrateRecipeProvider`** (3): datagen-only, so exclude it in `build.gradle` or fix the signatures.
13. **`datagen/assets/CBCBuilderTransformers`** (21): this was deliberately *kept* (`KEPT_DATAGEN` in `build.gradle`) because `CBCBlocks`/`CBCItems` reference its transformers. Either stub the model/loot-table generators (datagen is not run) or strip those `.blockstate/.model/.loot` calls from the index classes and drop the file from `KEPT_DATAGEN`.

### 3b. Client code (about 60% of the remaining errors)

These must stay in client-only classes. Never reference `com.zurrtum.create.client.*` or `net.minecraft.client.*` from common classes; Create Fly ships a server jar without its client packages.

1. **Entity renderers → 26.1 render-state/submit model**:
   - `BigCannonProjectileRenderer`, `AutocannonProjectileRenderer`, `DropMortarShellRenderer`, `ShrapnelBurstRenderer`, `GrapeshotBurstRenderer`, `PrimedPropellantRenderer`, `CannonCarriageRenderer`, `GasMaskModel`.
   - Pattern: `EntityRenderer<T, S extends EntityRenderState>` with `createRenderState()`, `extractRenderState(entity, state, partialTick)` and `submit(state, PoseStack, SubmitNodeCollector, CameraRenderState)`. RPL's `ProjectileBurstRenderer<T, S>` is already ported this way and is the model to copy.
   - `fabric/client/CBCEntityRenderers` (19 errors) resolves once the renderers have the right generics.
2. **Block entity renderers → 26.1**:
   - `CannonMountBlockEntityRenderer`, `QuickfiringBreech…`, `SlidingBreech…`, `ScrewBreech…`, `AutocannonBreechRenderer`, `AutocannonRecoilSpringRenderer`, `AbstractCannonCastBlockEntityRenderer`, `CannonCastBlockEntityRenderer`, `FinishedCannonCast…`, `LayeredCannonBlockEntityRenderer`, `FuzedBlockEntityRenderer`.
   - `SafeBlockEntityRenderer`/`SmartBlockEntityRenderer` come from Create Fly client (`com.zurrtum.create.client.foundation.blockEntity.renderer.*`). Copy how Create Fly's own renderers (e.g. `ValveHandleRenderer`, `MechanicalPressRenderer`) build render states and submit `SuperByteBuffer`s.
   - Cast fluid rendering used `NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox`. Create Fly has a fluid renderer helper in `client/catnip/render`.
   - `BakedModel`/`BakedQuad`/`BlockRenderDispatcher` are gone; use `BlockStateModel` / `QuadCollection`.
   - `fabric/client/CBCBlockEntityRenderers` (11 errors) resolves after that.
3. **Custom render types / shaders**: `index/CBCRenderTypes` (8), `CBCRenderingParts` (12), `CBCVertexFormatElements` (3) and `remix/LightingRemix` (2) use `ShaderInstance`/`CompositeState`. These need 26.1 `RenderPipeline` plus `RenderType.create(...)`.
   - What uses them: cannon smoke gradient shader, splinter particle shader, cannon-cast/ghost rendering.
   - For now, cannon smoke always uses `FallbackCannonSmokeParticle`, and splinters render as plain block-textured quads (see the PORT-NOTEs). Keep that for the first runnable build, and stub or remove these classes if they block compiling.
4. **Connected textures**: `index/CBCCTTypes` (14), `connected_textures/CBCCTSpriteShiftEntry` (7), `CBCCTSpriteShifter`, `CBCSpriteShifts`, `BuiltUpCannonCTBehavior`, `SlidingBreechCTBehavior`. Create Fly's `CTType` is not an interface; copy how Create Fly's `AllCTTypes`/`AllSpriteShifts` are built. The registration side (`fabric/client/CBCConnectedTextures`) is already written.
5. **GUI**:
   - `GasMaskOverlay` and `base/goggles/EntityGoggleOverlayRenderer`: port to a `HudElement` `(GuiGraphicsExtractor, DeltaTracker)`; there are no `RenderSystem` state calls in 26.1, so use `graphics.blit(RenderPipelines.GUI_TEXTURED, ...)`.
   - `CBCGuiTextures`.
   - `AutocannonAmmoContainerScreen`, `AbstractFuzeScreen` and the 4 fuze screens.
   - `CBCClientCommon` (2: `poce.getSeatPos(player)` → derive it from `contraption.getSeatOf(uuid)`).
6. **Mixins (client)**: `HumanoidPoseMixin` (4), `CannonMountDisplaySourceMixin` (2), `GoggleOverlayTooltipMixin` (2), `PitchOrientedContraptionEntityMixin` (2), `network/CBCClientHandlers` (4).
7. **Ponder**: `ponder/CannonLoadingScenes` (8), `CBCPonderScenes` (4), `CannonCraftingScenes` (2), `CBCPonderTags` (2).
   - `modifyBlockEntityNBT` lambdas still call `ItemStack.save(...)`; use `CBCValueIO.saveItem(provider, stack)`.
   - Fallback option: temporarily drop ponder registration for the first runnable build.

## 4. Things done this session (on top of what PORTING-26.1.md already lists)

Add these to PORTING-26.1.md's decisions and session log; it has not been updated for this session yet.

- **Renderers moved client-side**:
  - `fabric/client/CBCBlockEntityRenderers` and `fabric/client/CBCEntityRenderers` (with `SimpleEntityVisualizer` for the pitch-oriented contraption).
  - `fabric/client/CBCItemTooltips` (item descriptions; was `REGISTRATE.setTooltipModifierFactory` in common code).
  - `fabric/client/CBCFluidShellTooltip` (was in `IndexPlatform`, which servers load).
  - The `IndexPlatform.getCastRenderer` method was removed.
- **Explosions**: `CustomExplosion.Impl extends ServerExplosion` (server only). Client sound and particles live in nested `X.Effects` classes, built from the explode packet in `CBCClientHandlers`. `mixin/ServerExplosionMixin` replaces `ExplosionMixin` and provides:
  - terrain protection (skips `calculateExplodedPositions`);
  - the `editBlock` hook;
  - the entity radius in `hurtEntities`.
  - The packet's `toBlow` list is now always empty; clients get block changes from the server.
- **Particles**: `SingleQuadParticle`/`Layer`, providers take `RandomSource`, and pending providers come from Fabric `ParticleProviderRegistry.PendingParticleProvider`.
  - `SparkParticle` is rebuilt as a row of quads with a white-concrete sprite.
  - `SplinterParticle` is a velocity-aligned block quad (no mask shader).
  - `CannonSmokeParticle` always spawns the fallback particle.
  - `addParticle(o, force, ...)` became `addParticle(o, force, false, ...)`.
- **Tags**: `CBCTags` holds runtime keys only; the generated JSON is used as is, and datagen appenders were removed.
- **Fixed cannon mount pitch/yaw slots**:
  - Server: `FixedCannonMountScrollValueBehaviour extends ServerScrollValueBehaviour`. The pitch slot is registered as `TYPE` with net id 0, the yaw slot as `YAW_TYPE` with net id 1.
  - Client: `fabric/client/FixedCannonMountValueBehaviour`.
  - `mixin/ValueSettingsHandlerMixin` routes yaw packets on the server.
  - `mixin/client/ValueSettingsInputHandlerMixin` handles input for both slots.
  - `FixedCannonMountBoxRenderer` draws both value boxes.
  - Removed CBC's own packet (`ServerboundSetFixedCannonMountValuePacket`), screen (`FixedCannonMountValueScreen`) and `ValueSettingsClientMixin`.
- **Assembly exceptions on goggles**: common `base/goggles/CBCAssemblyExceptionHolder`, read by `CBCGoggleTooltip`, which implements `IDisplayAssemblyExceptions`.
- **Recipes**:
  - The 6 custom crafting recipes were ported to the 26.1 `CustomRecipe`.
  - The 5 deployer recipes now extend `CBCDeployerRecipe implements ItemApplicationRecipe` and are supplied by `mixin/DeployerBlockEntityMixin` (`getRecipe` RETURN-null fallback → `CBCCommonEvents.getDeployerRecipe`).
  - Cannon casting uses `crafting/casting/CastingFluidIngredient` (same `{"tag"}`/`{"fluid"}` JSON).
  - The foundry uses `MeltingRecipe.time()`.
- **Block hooks**:
  - `base/CBCRemovalListener` + `mixin/LevelChunkMixin` replace `onRemove`; the hook is called just before the block entity's `preRemoveSideEffects`.
  - `onExplosionHit` replaces NeoForge `onBlockExploded`.
  - `base/CaughtFireBlock`, `base/CBCDynamicFlammableBlock` and `mixin/FireBlockMixin` are ported from upstream's 1.20.1 Fabric version.
  - Signature updates: `getCloneItemStack(..., boolean)`, `updateShape` (8-arg, `ticks.scheduleTick`), `neighborChanged(..., Orientation, boolean)`, `getAnalogOutputSignal(..., Direction)`.
- **Configs**:
  - `port/catnip/config/ConfigBase` is the original catnip base on NeoForge `ModConfigSpec`, which Forge Config API Port provides on Fabric. CBC keeps its TOML configs. Create Fly's JSON config base is not used.
  - Server config is *not* synced to clients on dedicated servers (same as before through FCAP; verify).
- **Fluids**: `CBCFluids` uses the port Registrate Fabric fluid builder (`SimpleFlowableFluid`), with still/flow textures and the default renderer. The NeoForge fluid-type viscosity/density/temperature and the submerged fog colour/distance are not ported.
- **Reload listeners**: `SimpleJsonResourceReloadListener<JsonElement>(ExtraCodecs.JSON, FileToIdConverter.json(dir))`, so the data folder layout is unchanged. `IndexPlatform.makeRegistryOps` falls back to the built-in registries when there is no registry access.
- **Saves**:
  - Contraptions use `read(Level, ValueInput, boolean)` / `write(ValueOutput, boolean)`; the `ContraptionMixin` hooks and `ContraptionRemix` helpers were updated.
  - `PitchOrientedContraptionEntity` uses `readAdditional/writeAdditional(ValueInput/Output, boolean)`.
  - Cannon mount interfaces use `child()` views.
  - `CBCValueIO` gained `saveItem`, `parseItem`, `writeBlockPos`, `readBlockPos` and `loadWithComponents`.
  - **Partial block damage** saved data is now `data/createbigcannons/block_damage.dat` (a codec-wrapped compound). Damage saved in old worlds' `createbigcannons_block_damage.dat` is not migrated. This is minor and only affects in-progress block cracks.
- **Projectile spawn data**: Create Fly's `NbtSpawnPacket` carries CBC's spawn-buffer bytes, replacing NeoForge's `IEntityWithComplexSpawn`. The projectile owner id is no longer sent in the spawn packet.
- **Misc**:
  - The `ProjectileDamageEvent` NeoForge event is now a Fabric `Event` (`canDamageTerrain(level, pos)`; return false to protect).
  - Game rules go through `CBCUtils.booleanRule` (server-only rules; the client gets the default).
  - `Direction.getApproximateNearest` / `getNearest(x, y, z, null)`; `getUnitVec3i`.
  - `AllClientHandle.INSTANCE` for server speed, client contraption BEs and visual updates.
  - `SeatBlock.getColorBlock`.
  - Item cooldowns take stacks.
  - Access widener: `BlockEntityType.validBlocks`.
  - `EntityTypeConfigurator` uses `clientTrackingRange` and `alwaysUpdateVelocity`.
  - Gas mask: `EQUIPPABLE` head component plus `EquipmentDispenseItemBehavior`. Its head model (`GasMaskModel`) is not ported.
  - Partially formed cartridge "formed" property: computed from Create Fly's `SEQUENCED_ASSEMBLY_PROGRESS` × steps (6 for autocannon, 5 for big cartridge).
  - Carriage: `InterpolationHandler`, `interact(..., Vec3)`, `hurtServer`.
  - Autocannon contraption insert/extract uses Fabric transactions.

## 5. After it compiles: runtime checklist (the user's original goal)

1. **Resource fixes that compile errors don't reveal**:
   - **Recipe JSON conversion script.** The committed JSON is in NeoForge format: `neoforge:conditions`, `{"item":...}`/`{"tag":...}` ingredient objects, `neoforge:tag` fluid ingredients, and mB amounts. Create Fly needs 1.21.2+ ingredient strings (`"ns:id"` / `"#tag"`), `fluid_ingredients` `{"type":"fluid_stack","amount":<droplets = mB×81>,"fluid":...}`, and a singular `ingredient` for cutting.
   - **Item definitions** (`assets/createbigcannons/items/*.json`), which 1.21.4+ requires and CBC lacks. Generate them from the item models. Use `range_dispatch` for the 4 properties kept in `IndexPlatform.ITEM_PROPERTIES` (formed ×2, big_cartridge_filled, fuze_state). A custom `RangeSelectItemModelProperty` registration is needed.
   - Fluid tags: the generated fluid tag JSON still exists; check that `c:molten_*` exists for the casting recipes.
2. **Smoke tests** (copy the Aeronautics setup):
   - Server and client launch, with the mixin audit. With `defaultRequire: 1`, any Create Fly / MC target mismatch fails at startup.
   - New mixins to watch: `ServerExplosionMixin`, `LevelChunkMixin`, `FireBlockMixin`, `DeployerBlockEntityMixin`, `ValueSettingsHandlerMixin`, `client.ValueSettingsInputHandlerMixin`, `port.SpawnPlacementsAccessor`, `client.GoggleOverlayTooltipMixin`.
   - Check the server for client-class loading (`NoClassDefFoundError` for `com.zurrtum.create.client.*` or `net.minecraft.client.*`).
3. **Full modpack**: the `v26.1-testing` Prism instance (`C:\Users\akrsc\AppData\Roaming\PrismLauncher\instances\v26.1-testing\.minecraft\mods`), together with Aeronautics/Sable/Simulated.
4. **Load a world and test**:
   - Cannon mount assembly and firing: big cannon, autocannon, drop mortar.
   - Fixed cannon mount pitch/yaw slots.
   - Carriage.
   - Every munition type and its explosion effects.
   - Fuze GUIs.
   - Ammo container GUI.
   - Casting (fluid fill via pipe, recipe completion), drilling, built-up cannons.
   - Foundry melting.
   - Deployer munition assembly.
   - Powder charges catching fire and exploding.
   - Gas mask.
   - Goggle tooltips, including assembly errors.
   - Config file generation.
   - Old-world upgrade (a 1.21.11 world with CBC blocks), if relevant.
5. Update `PORTING-26.1.md` (decisions and session log) and write CBC in-game test instructions, like Aeronautics' `IN-GAME-TESTING-26.1.md`.
6. Later: JEI compat (`compat/jei`, `compat/common_jei`, both currently excluded); the custom shaders; the gas mask head model; submerged molten-metal fog; moving `port/` shims into the Create Fly fork; forking RPL and pushing both repos (ask first).
