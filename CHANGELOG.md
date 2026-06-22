# Changelog

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
