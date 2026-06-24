# Changelog

## [3.0.4 → 3.1.4] Port to Minecraft 26.2

Ported from Minecraft 26.1.2 to 26.2 (Fabric + NeoForge). Confirmed working: game launches and the backpack recipe unlocks and shows on the crafting table.

- Updated Framework dependency to 0.14.1+26.2 (published locally to mavenLocal; not available via GitHub Packages without GPR credentials).
- Migrated rendering code to the new deferred `SubmitNodeCollector` pipeline (`MultiBufferSource` was removed entirely): `GuiBackpackRenderer`, `FirstPersonEffectsRenderer`, `ShelfRenderer`, `BackpackDockRenderer`, and the Fabric/NeoForge render-event hooks (NeoForge's old `RenderLevelStageEvent.AfterOpaqueBlocks` moved to `SubmitCustomGeometryEvent`).
- Updated `net.minecraft.advancements.criterion` predicate/trigger references (imports *and* hardcoded mixin `@At` target descriptors) to their new `advancements.predicates` / `advancements.predicates.entity` / `advancements.triggers` packages.
- Replaced `EntityType` constants with the new `EntityTypes` holder class; added explicit `<Player>`/`<TraderLlama>` type witnesses where generic inference needed help.
- Replaced removed `BlockPos.getCenter()` / `getBottomCenter()` with `Vec3.atCenterOf()` / `Vec3.atBottomCenterOf()`.
- Replaced removed `Minecraft.screen` / `setScreen()` / `getToastManager()` with their `Minecraft.gui` equivalents (`Gui#screen()`, `Gui#setScreen()`, `Gui#hud#getChat()`, `Hud#toastManager()`).
- Replaced removed `IntrinsicHolderTagsProvider` with `TagsProvider`. Fabric's data generator mixes into vanilla `TagsProvider` and requires the instance to actually be a `FabricTagsProvider`, so added a Fabric-specific `FabricBlockTagGen extends FabricTagsProvider.BlockTagsProvider`; NeoForge keeps the shared `CommonBlockTagGen`.
- `GuiMixin`: `overlayMessageTime` field and `setOverlayMessage` moved from `Gui` to the new `Hud` class — retargeted the mixin to `Hud.class`.
- `BitmapProviderDefinitionMixin`: the `Glyph` constructor now wraps `NativeImage` in a new package-private `ImageDataHolder` type. Updated the `@At` target descriptor, and switched from `@ModifyArgs` to `@ModifyArg` (singular) so the generated handler only touches the one `int` "advance" param we modify, instead of needing to box every constructor parameter (which threw `IllegalAccessError` against the inaccessible `ImageDataHolder` type).
- Other smaller API renames: `GameRenderer#getMainCamera()` → `mainCamera()`, `GameRenderer#getLighting()` → `lighting()`, `LevelRenderer.getLightCoords` → `LightCoordsUtil`, `ParticleRenderType` constructor now takes a shorthand string too, Fabric Loom's `includeInternal` configuration removed.
- Catalogue integration disabled for local builds (not published anywhere reachable without GitHub Packages credentials).
- Ran `:fabric:runDatagen` / `:neoforge:runData` to (re)generate recipes, recipe-unlock advancements, loot tables, and tags — this output is gitignored, so it must be regenerated on any fresh clone or the jar silently ships without that content.
