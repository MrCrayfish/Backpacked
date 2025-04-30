---
sidebar_position: 6
---

# Translations

Minecraft has a directory in the assets where translations are provided. This will allow the name of a backpack to be displayed in the language players set in their game. There must be at least a US English translation (denoted by `en_us`) as this is the default and fallback langauge. Adding support for more languages is completely optional, you can find a list of the locale codes on the [Minecraft Wiki](https://minecraft.wiki/w/Language).

1. In your `assets` directory, create the directory structure: `assets/<namespace>/lang/`. If you downloaded the **Base Addon Pack** from the [Assets](./assets.md) page, the directory should have already be created, just make sure to change the `example` namespace as mentioned in the [Getting Started](/docs/custom-backpack/getting-started.mdx#steps) section.
2. Create a file named `en_us.json` in the `lang` folder, then open the file with a text editor and simply add `{}` to the contents.
3. Save the file.
   
:::info Example
If the **namespace** for your addon pack is `more_backpacks`, the directory would be `assets/more_backpacks/lang/`. Any translation files would have the path of `assets/more_backpacks/lang/<locale_code>.json`.
:::

## Defining Backpack Translations

Every backpack has it's translation key created based on the **namespace** and the **name** used to register the backpack. If the **namespace** of the addon is `more_backpacks` and the **name** of the backpack is `panda_bag`, the translation key would be generated as: `backpack.more_backpacks.panda_bag`

### Steps

1. Open the `en_us.json` file from the `lang` folder in a text editor.
2. Enter your backpack translation keys as described above, and provide a value to them as shown in the code block below.
```json title="en_us.json"
{
    "backpack.namespace.your_backpack_name": "Backpack Name",
    "backpack.namespace.another_backpack_name": "Another Backpack Name"
}
```
3. Save the file, and verify the translation are working by starting up the game with the addon pack loaded. The translation values should be visible in the backpack customisation menu, and it should no longer show the translation keys.

❌ Untranslated backpack:

![Test](/img/bad_translation.png) 

✅ Translated backpack:

![Test](/img/good_translation.png) 

## Next Step

It's time to [add a challenge](/docs/custom-backpack/setting-the-challenge.md) to unlock the backpack and give the player a fun goal!