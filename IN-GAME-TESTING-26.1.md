# CBC 26.1 In-Game Test Checklist

Work through this in the `v26.1-testing` Prism instance. The production smoke tests (see `TODO-26.1.md`, section 0)
already prove that CBC boots on server and client, that every mixin applies, that every block can be placed and saved,
and that fuzed shells detonate on the server. They never render a world on a client, so everything that is client-side
below is untested.

Each check has an ID (e.g. **3.4**) so results can be reported as "3.4 fails". The *Code* note names what the check
exercises, so a failure points at the right place.

When something fails, collect:
1. `<instance>/.minecraft/logs/latest.log` (render problems often only show up here)
2. `<instance>/.minecraft/crash-reports/` (newest file) if it crashed
3. A screenshot (F2) for anything visual

Also search the log for `createbigcannons`, `ritchiesprojectilelib`, `Mixin`, `Exception` and `Missing`, even when
nothing looks wrong.

---

## 0. Setup

- Jars in `mods/`: `createbigcannons-5.11.7+mc.26.1.2-fabric.jar` and `ritchiesprojectilelib-2.1.2+mc.26.1.2-fabric.jar`,
  next to Create Fly 6.0.9-4 and the Aeronautics 4-in-1 jar.
- Rebuild CBC: `cd createbigcannons && ./gradlew build` (with `JAVA_HOME` set to the Prism Java 25 runtime).
- Use a **new creative world**, cheats on. Old CBC worlds are covered separately in 9.

## 1. Boot, menus, items

- [ ] **1.1** The game reaches the title screen. The mod list shows Create Big Cannons and Ritchie's Projectile Lib.
- [ ] **1.2** The CBC creative tabs exist, and every item has an icon (no purple-black "missing" cubes).
  *Code:* `assets/createbigcannons/items/*.json` (generated), `ModGroup`.
- [ ] **1.3** Shells (HE, AP, shrapnel, smoke, fluid, drop mortar) show a fuze on the icon once a fuze is added
  (craft one or use a deployer). The tip shows a head fuze and the base shows a base fuze.
  *Code:* `range_dispatch` on `createbigcannons:fuze_state`, `CBCItemModelProperties`.
- [ ] **1.4** The big cartridge icon changes when it is filled. The partially formed big and autocannon cartridges
  change shape as their sequenced assembly progresses.
  *Code:* `big_cartridge_filled` / `formed` properties, `CBCClientCommon.onClientSetup`.
- [ ] **1.5** Item tooltips: hold Shift on a shell, a cannon block and a fuze, and the CBC tooltip text appears.
  *Code:* `CBCItemTooltips`.
- [ ] **1.6** JEI: CBC items are listed. CBC recipe categories (melting, casting, built-up heating, munition assembly)
  are **expected to be missing**, because the JEI compat is not ported. Create's own categories show CBC recipes
  (mixing, compacting, sequenced assembly, cutting, mechanical crafting).
- [ ] **1.7** Ponder: hover a cannon mount, a cannon cast and the cannon builder and hold W. The scenes play and the
  blocks render. (Separate issue: the Create Fly `EntityBlockLayer` ponder crash reported earlier.)
  *Code:* `ponder/`, `CBCPonderScenes`.
- [ ] **1.8** The config files `config/createbigcannons-*.toml` are created and the values can be edited in game
  (Mod Menu).

## 2. Block rendering

Place one of each and look at it from all sides.

- [ ] **2.1** Cannon barrels / chambers / ends (log, wrought iron, cast iron, bronze, steel, nethersteel): models and
  textures are correct, and adjacent same-material blocks connect (connected textures).
  *Code:* `CBCConnectedTextures`, `CBCCTTypes`, `CBCCTSpriteShiftEntry`, `BuiltUpCannonCTBehavior`.
- [ ] **2.2** Sliding breech and screw breech: the breechblock / screw renders and moves when powered by rotation.
  *Code:* `SlidingBreechBlockEntityRenderer`, `ScrewBreechBlockEntityRenderer`.
- [ ] **2.3** Quickfiring breech, autocannon breech and recoil spring render their moving parts.
- [ ] **2.4** Cannon mount, yaw controller and mount extension: the shafts rotate with input rotation, and the mount
  head renders. *Code:* `CannonMountBlockEntityRenderer`, `CBCSafeBlockEntityRenderer`.
- [ ] **2.5** Munition blocks (shells, powder charge, big cartridge) placed in the world face the right way. A fuzed
  shell shows its fuze. *Code:* `FuzedBlockEntityRenderer`.
- [ ] **2.6** Cannon cast: fill it with molten metal by pipe. The fluid level rises visibly and the translucent preview
  of the result shows. *Code:* `CannonCastBlockEntityRenderer`, `FluidRenderHelper`.
- [ ] **2.7** Particles: breaking any CBC block shows break particles (CBC models had no `particle` texture before;
  they now use a texture the model already uses).

## 3. Big cannon

Build a cannon mount, a cast iron cannon (chamber + barrels) and a sliding breech. Load it with a cannon loader,
powder charges and a solid shot. Assemble it with a redstone pulse on the mount.

- [ ] **3.1** The cannon assembles into a contraption. It renders, and the goggles show the cannon stats.
  *Code:* `PitchOrientedContraptionEntityRenderer` / `PitchOrientedContraptionVisual`, `ContraptionMixin`.
- [ ] **3.2** Rotate the yaw and pitch through the mount. The cannon turns smoothly with no jitter or wrong axis.
- [ ] **3.3** Fire it. You hear the sound, see the muzzle flash and smoke, and feel the screen shake when near. The
  projectile flies, renders, and leaves a tracer if it has one. *Code:* `BigCannonProjectileRenderer`, RPL screen shake.
- [ ] **3.4** The solid shot hits stone and penetrates or bounces. Block damage cracks show on hit blocks and fade.
  *Code:* `LevelRendererMixin` (custom block damage).
- [ ] **3.5** HE shell with an impact fuze: explodes on impact and destroys blocks. Smoke shell: leaves a smoke cloud.
  Shrapnel and grapeshot: bursts render (small fragments) and damage mobs. Fluid shell (water / lava): splashes fluid.
  *Code:* `ShrapnelBurstRenderer`, `GrapeshotBurstRenderer`, `SmokeEmitterEntity`, `DefaultFluidCompat`.
- [ ] **3.6** AP shot / AP shell penetrate several blocks.
- [ ] **3.7** Overload: too much powder for the material makes the cannon burst or squib (failure handling works).
- [ ] **3.8** Disassemble the cannon and reassemble it. All blocks and their contents come back.
- [ ] **3.9** Ride the mount (right-click with the cannon assembled). The player pose looks right, and the pitch / fire
  keys work (default keys in Controls under the CBC category). *Code:* `HumanoidPoseMixin`, `LivingEntityRendererMixin`, `CBCClientCommon` key mappings.
- [ ] **3.10** A fixed cannon mount: the pitch / yaw value boxes can be scrolled with a wrench.
  *Code:* `FixedCannonMountValueBehaviour`, `ValueSettingsHandlerMixin`.
- [ ] **3.11** A display link reading a cannon mount shows its pitch / yaw on a display board.
  *Code:* `CannonMountDisplaySourceRender`.

## 4. Autocannon and drop mortar

- [ ] **4.1** Autocannon (breech + recoil spring + barrels) on a mount: assemble, fill an ammo container with
  autocannon rounds, attach it, and fire. The recoil spring animates, rounds fly and render, and spent casings drop.
  *Code:* `AutocannonProjectileRenderer`, `AutocannonBreechBlockEntityRenderer`.
- [ ] **4.2** Ammo container GUI: open it. The slots, the tracer slot icon and the item count work, and the name from
  an anvil-renamed container is kept when broken and placed again.
  *Code:* `AutocannonAmmoContainerScreen`, `AutocannonAmmoContainerMenu`, `MenuBuilder`.
- [ ] **4.3** Flak rounds with a timed / proximity fuze explode in the air.
- [ ] **4.4** Machine gun rounds fire from a machine gun barrel setup.
- [ ] **4.5** Drop mortar: drop a fuzed drop mortar shell into a wrought iron drop mortar end. It launches and explodes
  on landing. *Code:* `DropMortarShellRenderer`.
- [ ] **4.6** Cannon carriage: place a cannon on a carriage, push / ride it, and fire from it.
  *Code:* `CannonCarriageRenderer`, `CannonCarriageEntity`.

## 5. Fuzes and munition assembly

- [ ] **5.1** Each fuze GUI (timed, proximity, delayed impact, wired, inertia) opens, renders its background, and the
  setting can be changed and is saved. *Code:* `AbstractFuzeScreen` and subclasses.
- [ ] **5.2** Deployer munition assembly: a deployer puts a fuze on a shell, puts a tracer on, and fills a big
  cartridge with powder. *Code:* `DeployerBlockEntityMixin`, the `*_deployer` recipe types.
- [ ] **5.3** Crafting-grid munition recipes: fuzing, fuze removal, tracer application / removal, cartridge assembly.
- [ ] **5.4** Powder charges and big cartridges: set them on fire. They catch fire and explode (the primed propellant
  renders flashing). *Code:* `FireBlockMixin`, `CaughtFireBlock`, `PrimedPropellantRenderer`.
- [ ] **5.5** Big cartridges can't be used in water / where `water_evaporates` (the Nether): check that the behaviour
  matches upstream.

## 6. Crafting the cannons

- [ ] **6.1** Foundry: basin + basin foundry lid over a blaze burner. Melting cast iron / bronze / steel ingots gives
  molten metal. *Code:* `MeltingRecipe`, `BasinFoundryBlockEntity`.
- [ ] **6.2** Molten metal fluid: renders (still and flowing), can be bucketed and piped, and a dispenser places it.
  (Submerged fog colour is not ported; expect a plain fluid look when inside it.)
- [ ] **6.3** Cannon cast: fill it fully. It turns into a finished cast and opens with a wrench to give the unbored
  cannon block. *Code:* `CannonCastBlockEntity`, `FinishedCannonCastBlockEntity`.
- [ ] **6.4** Cannon drill: bores an unbored barrel / chamber into the finished block, with scrap drops.
  *Code:* `CannonDrillBlockEntity`, `BoringScrapLoot`.
- [ ] **6.5** Cannon builder: builds a built-up steel cannon from layers, and heating it works.
  *Code:* `CannonBuilderBlockEntity`, `BuiltUpHeatingRecipe`.
- [ ] **6.6** Spot-check Create recipes: mixing bronze / steel, compacting molten metal into blocks, cutting spring
  wire, the recoil spring sequenced assembly (3 loops), mechanical crafting a cannon mount.
  *Code:* the generated `data/createbigcannons/recipe/**` (converted to the 26.1 format).

## 7. Equipment

- [ ] **7.1** Gas mask: wear it. It shows on the head (3D mask model) in third person, and the screen overlay shows
  in first person. *Code:* `items/gas_mask.json` (`display_context` select), `GasMaskOverlay`.
- [ ] **7.2** Gas cloud from a fluid shell with a harmful fluid: the gas mask protects. *Code:* `GasCloudEntity`.
- [ ] **7.3** Goggles on a cannon block / contraption show the CBC tooltip, including assembly errors.
  *Code:* `GoggleOverlayTooltipMixin`, `EntityGoggleOverlayRenderer`.
- [ ] **7.4** Traffic cone: can be worn and fired.

## 8. Aeronautics / Sable

- [ ] **8.1** Mount a cannon on a Sable sub-level (an assembled ship). It fires, the recoil pushes the ship, and the
  impacts push hit ships. *Code:* `compat/sable/SableForceGroupsCompat`, `SableCompat.onPrePhysicsTick`.
- [ ] **8.2** Shells hitting a sub-level damage its blocks. *Code:* `ShellSubLevelImpactCallback`.

## 9. Saves and multiplayer

- [ ] **9.1** Save and quit with an assembled cannon, loaded munitions, filled casts and ammo containers. Reload: all
  restored.
- [ ] **9.2** Open the world to LAN or use a dedicated server, then repeat 3.3, 4.1 and 5.1 from a second client
  (networking: projectile spawn data, menu opening, fuze settings).
- [ ] **9.3** (Optional) An old CBC world from 1.21.1: expect breakage, because data upgrade is not ported.

## Known gaps (not bugs to report)

- JEI compat for CBC recipe categories is not ported.
- Custom shaders (cannon smoke, splinter) are not ported. Smoke uses the fallback render.
- Submerged molten-metal fog is not ported.

