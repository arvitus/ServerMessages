<div align="center">

# Server Messages

[![Static Badge](https://img.shields.io/badge/modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/servermessages)
[![Static Badge](https://img.shields.io/badge/GitHub-181717?logo=github)](https://github.com/arvitus/ServerMessages)
[![Static Badge](https://img.shields.io/badge/Discord-5662f6?logo=discord&logoColor=white)](https://discord.gg/xzdbetZVrn)

A Minecraft mod to customize any server message, with support for text formatting and placeholders.

</div>

## What does it do?

This mod enables you to modify any¹ server message to your liking. You can replace the message itself, add or change
the [formatting](https://placeholders.pb4.eu/user/quicktext/) and even use [placeholders](https://placeholders.pb4.eu/).
The format is fully compatible with
the [translated text format](https://minecraft.wiki/w/Raw_JSON_text_format#Translated_Text) used by language files.

> Originally, I just needed the option to change the "not on whitelist" message and the "server closed" message. This
> mod is what emerged from it.

## How do I use it?

To modify a message, you simply add the corresponding
[translation key](https://github.com/InventivetalentDev/minecraft-assets/blob/1.21/assets/minecraft/lang/en_us.json)
to the config and change its value to your liking.
You can reload the config by using `/reload`. The text format is based on
the [translated text format](https://minecraft.wiki/w/Raw_JSON_text_format#Translated_Text), and also supports
the [QuickText Format](https://placeholders.pb4.eu/user/quicktext) for text formatting
and global [placeholders](https://placeholders.pb4.eu/user/default-placeholders/).

The format for placeholders is `${placeholder}` and for most messages you can only use placeholders that do NOT depend
on a player/entity! So `${server:tps}` and `${world:name}` (overworld) are perfectly fine, but `${player:name}` will
not always work.  
*For more information, see [Placeholder Contexts](#placeholder-contexts).*

The config format is compatible with the minecraft language file format. So you could simply replace the config with a
language file (strongly discouraged) and it would work fine. Note, however, that this would NOT overwrite the language
setting of the players! For reference, you can find a list of all language files currently
available [here](https://github.com/InventivetalentDev/minecraft-assets/tree/1.21/assets/minecraft/lang).

> [!NOTE]  
> Any other mod that modifies server messages (e.g. [BanHammer](https://modrinth.com/mod/banhammer)), will most
> certainly take precedence over changes made by this mod.**

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
  "multiplayer.player.left": "<yellow>See you soon, <bold>%s</bold>!</yellow>"
}
```

> [!WARNING]  
> Don't copy this as is! The config is using the [json](https://en.wikipedia.org/wiki/JSON) format and json does NOT
> support comments. The comments in the example are just for clarification. **Do not copy the comments!**

### Supported Translation Keys

In theory, every translation key is supported when it is used explicitly (e.g. in
a [translation tag](https://placeholders.pb4.eu/user/quicktext/#translations)).
But natively, the server only uses a few translations.
All translation keys, that are parsed on the server, are supported. That includes most of the messages you can see when
playing on a server.

For a more detailed list of supported keys, see [here](src/main/resources/assets/translationKeySupport.json).
> [!NOTE]  
> The information in this list may not be accurate!  
> Though it can be helpful to get a quick overview, it's only based on my findings and a lot of assumptions.

### Placeholder Contexts

*Contexts are additional information that allows different placeholders to work. For example, some mods like AfkPlus
require the `player` context for their placeholders to work. This may not always be available in every message,
so please check the additional contexts of your desired message in the list below.*

All messages support the basic contexts, meaning
the [Server](https://placeholders.pb4.eu/user/default-placeholders/#server) and
the [Overworld](https://placeholders.pb4.eu/user/default-placeholders/#world), but NOT
the [Player](https://placeholders.pb4.eu/user/default-placeholders/#player).
Some messages support additional contexts. See the list below.

| Translation Key(s)                       | Extra Context |
|------------------------------------------|:-------------:|
| `chat.type.advancement.*`                |   `player`    |
| `commands.kick.success`                  |   `player`    |
| `multiplayer.disconnect.banned.*`        |   `profile`   |
| `multiplayer.disconnect.banned_ip.*`     |   `profile`   |
| `multiplayer.disconnect.not_whitelisted` |   `profile`   |
| `multiplayer.disconnect.kicked`          |   `player`    |
| `multiplayer.disconnect.server_shutdown` |   `player`    |
| `multiplayer.player.joined`              |   `player`    |
| `multiplayer.player.joined.renamed`      |   `player`    |
| `multiplayer.player.left`                |   `player`    |

> I will be adding more contexts over time, but since I have to do this manually for each message, it can take a while.
> If you want a specific context added, please [open an issue](https://github.com/arvitus/ServerMessages/issues/new) and
> I'll prioritize it.

## Why do I need it?

Almost every text message, a vanilla server sends to its players, uses a translation key to display the corresponding
message. So instead of sending actual text (e.g. `<Player> joined the game`), it sends something
like `translate[multiplayer.player.joined]`. The client then converts this into actual text, using the player's selected
language. This way the game ensures that each player will see the message in their preferred language. While this is
pretty helpful, sometimes it may not be the behaviour you want, because you cannot change the formatting of those
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

Any translation keys used in your custom messages will use the default values/formatting, NOT the values you
provided. This is necessary to prevent infinite recursion.

---
**[ ¹ ]:** *There are some special cases where it might not work as expected due to how minecraft handles certain
messages. If you encounter one of those, please [open an issue](https://github.com/arvitus/ServerMessages/issues/new),
and I'll see what can be done to make them work too.*