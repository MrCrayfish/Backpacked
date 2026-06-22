# Changelog

## 3.1.2
- **Root cause of the launch hang reported by user (not `comforts` as previously suspected):** `BitmapProviderDefinitionMixin`'s `@ModifyArgs` on `BitmapProvider$Glyph`'s constructor failed to find its target — MC 26.2 wraps the `NativeImage` constructor param in a new `BitmapProvider$ImageDataHolder` type. Fixed the `@At` target descriptor; the "advance" param this mixin adjusts is still index 6, so the modification logic itself didn't need to change. This mixin's failure was crashing `BitmapProvider$Definition`'s static init, which cascades into the `FontManager`/`GlyphProviderDefinition` circular class-init failure seen in earlier logs.
- `ServerPlayerGameModeMixin`: fixed two more `@At` target descriptors that still referenced the old `net.minecraft.advancements.criterion` package for `ItemUsedOnLocationTrigger`/`DefaultBlockInteractionTrigger` (moved to `advancements.triggers` in 3.1.0's port, but these were string-literal targets so the earlier import-based search missed them).
- Audited every other hardcoded mixin `@At`/`@Inject` target descriptor in the mod against MC 26.2 bytecode (cross-referencing actual constant-pool owner references, not just declared-method lists, since inherited methods keep the subclass as the symbolic owner) — no further mismatches found.

## 3.1.1
- Fixed `GuiMixin` crash on launch: `@Shadow private int overlayMessageTime` and the `setOverlayMessage` injection target `Gui.class`, but MC 26.2 moved both the field and method to the new `Hud` class. Retargeted the mixin to `Hud.class`.
- Bumped Framework dependency to 0.14.1 (fixes a launch crash in Framework's own `GameRendererMixin` — see Framework's changelog)

## 3.1.0
- Ported to Minecraft 26.2 (from 26.1.2)
- Updated Framework dependency to 0.14.0+26.2
- Migrated rendering code to the new deferred `SubmitNodeCollector` pipeline (`MultiBufferSource` removal)
- Updated `net.minecraft.advancements.criterion` predicate/trigger imports to their new `advancements.predicates` / `advancements.triggers` packages
- Replaced `EntityType` constants with the new `EntityTypes` holder class
- Replaced removed `BlockPos.getCenter()` / `getBottomCenter()` with `Vec3.atCenterOf()` / `Vec3.atBottomCenterOf()`
- Replaced removed `Minecraft.screen` / `setScreen()` / `getToastManager()` with their `Minecraft.gui` equivalents
- Replaced removed `IntrinsicHolderTagsProvider` with `TagsProvider`
- Catalogue integration disabled for local builds (not published anywhere reachable without GitHub Packages credentials)
