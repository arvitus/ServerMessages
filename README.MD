<div align="center">

# Server Messages

[![Static Badge](https://img.shields.io/badge/modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/servermessages)
[![Static Badge](https://img.shields.io/badge/GitHub-181717?logo=github)](https://github.com/arvitus/ServerMessages)
[![Static Badge](https://img.shields.io/badge/Discord-5662f6?logo=discord&logoColor=white)](https://discord.gg/xzdbetZVrn)

A Minecraft mod to customize any server message, with support for text formatting and placeholders.

</div>

---

## What does it do?

This mod lets you modify any<sup id="a1">[[1]](#f1)</sup> server message to your liking. You can replace the
message itself, add or change the [formatting](https://placeholders.pb4.eu/user/quicktext/),
and even use [placeholders](https://placeholders.pb4.eu/). The format is fully compatible with
the [translated text format](https://minecraft.wiki/w/Raw_JSON_text_format#Translated_Text) used by language files.

Common use cases include changing the color of messages like "You have been banned", adding information to the "You're
not whitelisted" message or customizing the kick notification. You could even use the mod to display some stats when a
player joins or leaves the server or make death messages appear in rainbow colors. The possibilities are endless!

> [!NOTE]  
> For anything chat related, I recommend using [Styled Chat](https://modrinth.com/mod/styled-chat) instead.
> If you do, all related messages in this mod will be disabled to prevent conflicts.
> You can then change them using Styled Chat.

## How do I use it?

To modify a message, you simply add the corresponding
[translation key](https://github.com/InventivetalentDev/minecraft-assets/blob/26.1/assets/minecraft/lang/en_us.json)
to the config and change its value to your liking. The text format is based on
the [translated text format](https://minecraft.wiki/w/Raw_JSON_text_format#Translated_Text)
and also supports the [QuickText Format](https://placeholders.pb4.eu/user/quicktext) for text formatting
and global [placeholders](https://placeholders.pb4.eu/user/default-placeholders/).
You can reload the config with `/reload`.

The format for placeholders is `${placeholder}` and for most messages you can only use placeholders that do NOT depend
on a player/entity! So `${server:tps}` and `${world:name}` (overworld) are perfectly fine, but `${player:name}` will
not always work.  
*For more information, see [Placeholder Contexts](#placeholder-contexts).*

The config format is compatible with the Minecraft language file format. So you could simply replace the config with a
language file (strongly discouraged) and it would work just fine. Note, however, that this would NOT overwrite the
language setting of the players! For reference, you can find a list of all language files currently available
[here](https://github.com/InventivetalentDev/minecraft-assets/tree/26.1/assets/minecraft/lang).

If you use translation keys manually
([QuickText](https://placeholders.pb4.eu/user/quicktext) / [STF](https://placeholders.pb4.eu/user/text-format)
or [Text components](https://minecraft.wiki/w/Text_component_format)), for example in chat messages or other mods,
they will also be affected by this mod and thus use the same formatting as specified in the config.
If you do not want this, you can either append `r` or `raw` to the
[QuickText](https://placeholders.pb4.eu/user/quicktext/#translations)
/ [STF](https://placeholders.pb4.eu/user/text-format/#translations)
translation tags (`<lang ...>` -> `<rlang ...>`), or add `#raw` as the **last** argument to the translation arguments.  
*For example, `<rawlang ...>` is the same as `<lang ... #raw>` and will prevent the message from being modified.
For [Text components](https://minecraft.wiki/w/Text_component_format#Translated_Text), it would look like this:
`{"translate": "...", "with": [..., "#raw"]}`.*

> [!NOTE]  
> Any other mod that modifies server messages (e.g. [BanHammer](https://modrinth.com/mod/banhammer)), will most
> certainly take precedence over changes made by this mod.

<h3 id="example-config"></h3>

### Example Config

```json5
{
  // only change formatting
  "multiplayer.disconnect.not_whitelisted": "<red><bold><lang multiplayer.disconnect.not_whitelisted></bold></red>",
  // replace message; use placeholder
  "multiplayer.disconnect.server_shutdown": "<green>We'll be right back!</green>\n<gray><underline>Uptime: </>${server:uptime}</gray>",
  // add text; use nested translation argument
  "multiplayer.player.joined": "<yellow><lang multiplayer.player.joined '%s'>. Good to see you!</yellow>",
  // replace message; use translation argument
  "multiplayer.player.left": "<yellow>See you soon, <bold>%s</bold>!</yellow>",
  // custom translation key; add reason to kick message
  "multiplayer.disconnect.kicked.reason": "<lang multiplayer.disconnect.kicked>\n<yellow>%s</yellow>"
}
```

> [!WARNING]  
> Don't copy this as is! The config is using the [JSON](https://en.wikipedia.org/wiki/JSON) format and JSON does **NOT**
> support comments. The comments in the example are just for clarification. **Do not copy the comments!**

### Supported Translation Keys

In theory, every translation key is supported when it is used explicitly (e.g. in
a [translation tag](https://placeholders.pb4.eu/user/quicktext/#translations)).
But natively, the server only uses a few translations.
All translation keys, that are parsed on the server, are supported. That includes most of the messages you can see when
playing on a server.

For a more detailed list of supported keys,
see [here](https://github.com/arvitus/ServerMessages/blob/main/src/main/resources/assets/servermessages/translationKeySupport.json).
> [!NOTE]  
> The information in this list may not be accurate!  
> Though it can be helpful to get a quick overview, it's only based on my findings and a lot of assumptions.

<h3 id="placeholder-contexts"></h3>

### Placeholder Contexts

Contexts are additional information that allows different placeholders to work. For example, some mods like AfkPlus
require the `player` context for their placeholders to work. This may not always be available in every message,
so please check the additional contexts of your desired message in the list below.

All messages support the basic contexts, meaning
the [Server](https://placeholders.pb4.eu/user/default-placeholders/#server)
and the [Overworld](https://placeholders.pb4.eu/user/default-placeholders/#world),
but NOT the [Player](https://placeholders.pb4.eu/user/default-placeholders/#player).

| Translation Key(s)                  |                                                                                                                             Context                                                                                                                             |   Version Added    |
|-------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------:|
| `chat.type.advancement.*`           |                                                                                                                            `player`                                                                                                                             |       `1.0`        |
| `commands.ban.success`              |                                                                                                                            `player`                                                                                                                             |       `1.1`        |
| `commands.banip.success`            |                                                                                                                            `player`                                                                                                                             |       `1.1`        |
| `commands.deop.success`             |                                                                                                                            `player`                                                                                                                             |       `1.1`        |
| `commands.kick.success`             |                                                                                                                            `player`                                                                                                                             |       `1.0`        |
| `commands.op.success`               |                                                                                                                            `player`                                                                                                                             |       `1.1`        |
| `commands.pardon.success`           |                                                                                                                            `player`                                                                                                                             |       `1.1`        |
| `death.*`                           |                                                                                                                            `player`                                                                                                                             |       `1.0`        |
| `multiplayer.disconnect.*`          | `player` if the player was already on the server before the disconnect, otherwise `profile`.<br/><br/>**Exceptions (non-exhaustive list)**<br/>`.transfers_disabled`, `.outdated_client`, `.incompatible`<br/>*These only have the default context (`server`).* | `1.1` (some `1.0`) |
| `multiplayer.player.joined`         |                                                                                                                            `player`                                                                                                                             |       `1.0`        |
| `multiplayer.player.joined.renamed` |                                                                                                                            `player`                                                                                                                             |       `1.0`        |
| `multiplayer.player.left`           |                                                                                                                            `player`                                                                                                                             |       `1.0`        |

> If you want a specific context added, please [open an issue](https://github.com/arvitus/ServerMessages/issues/new) or
> [tell me on Discord](https://discord.gg/xzdbetZVrn).

### Additional Translation Keys

This mod also adds some additional translation keys for use in the config.
They will let you customize some messages that you normally would not be able to change.

> [!NOTE]
> These translation keys are **not** provided by Minecraft itself and **only** work when specified in the mod config.

| Translation Key                        | Context  | Notes                                                                                                                                                                                                                                                                          | Default | Version Added |
|----------------------------------------|:--------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|:-------------:|
| `multiplayer.disconnect.kicked.reason` | `player` | Sent to the kicked player(s), if there was a reason specified.<br/><br/>*The reason is the only argument (`%s`).*<br/>*See [`multiplayer.disconnect.banned.reason`](https://github.com/InventivetalentDev/minecraft-assets/blob/26.1/assets/minecraft/lang/en_us.json#L5596).* | `%s`    |     `1.1`     |

## Why do I need it?

Almost every text message, a vanilla server sends to its players, uses a translation key to display the corresponding
message. So instead of sending actual text (e.g. `<Player> joined the game`), it sends something
like `translate[multiplayer.player.joined]`. The client then converts this into actual text, using the player's selected
language. This way the game ensures that each player will see the message in their preferred language. While this is
pretty helpful, sometimes it may not be the behavior you want, because you cannot change the formatting of those
messages without forcing your players to use a custom resource pack. This is why this mod exists.

Usually those messages are displayed as plain old white text, which is pretty boring. So to spice things up, you need to
be able to change those messages server wide. For example, you can change the "not on whitelist" message to give your
new players some more information. You could also change the color of the "you have been banned" message to make it
stand out. There are a lot of those server messages that you may want to customize, so give it a try!

## How does it work?

This mod replaces the translation key with the text you specified in the config, **before** the text is
sent to the player. This way the player receives your custom message instead of the default one. This does not
necessarily affect the translation of the messages. So you can add formatting to messages without losing the
auto translation feature (see [example config](#example-config)).

To prevent infinite recursion, all translation keys used in your custom messages will have their default values and
formatting, **not** the values you provided.

---
<b id="f1">[[ 1 ]](#a1):</b> *There are some special cases where it might not work as expected due to how Minecraft
handles certain messages.
If you encounter one of those, please [open an issue](https://github.com/arvitus/ServerMessages/issues/new),
and I'll see what can be done to make them work too.  
**Player chat messages (also `/me`, etc.), unfortunately do NOT work.
Use [Styled Chat](https://modrinth.com/mod/styled-chat) for these instead.***