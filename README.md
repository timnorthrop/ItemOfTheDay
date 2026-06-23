# ItemOfTheDay

A lightweight Minecraft Paper server plugin that allows players to exchange a different item each day for a reward. Server operators have the ability to set or shuffle the Item of the Day (IOTD), set the reward for the current Item of the Day, and set the default reward, which is reverted back to on date change or when the IOTD is shuffled.

### Requirements

Server must have Paper installed. Currently supported versions of Paper include stable builds built for versions `1.21.11` through `26.2` of Minecraft. Use with alpha/beta builds of Paper at your own risk and discretion.

### Commands

- `/iotd info`: available to all players - displays info on the current Item of the Day and corresponding reward.
- `/iotd exchange`: available to all players - if the player has a single instance of the IOTD in their main hand and has not already exchanged today, the IOTD in their hand will be replaced with the current reward.
- `/iotd shuffle`: operator required - randomizes the IOTD and resets the reward back to the specified default reward.
- `/iotd set <item>`: operator required - sets the current IOTD to the specified item WITHOUT changing the reward.
- `/iotd set-reward <reward> <amount>`: operator required - sets the current reward to the specified item and amount.
- `/iotd set-default-reward <default-reward> <amount>`: operator required - sets the default reward (reverted to when IOTD is shuffled) to the specified item and amount.
- `/iotd reset-claimed`: operator required - clears the config's list of players (UUIDs) who have already exchanged for the IOTD.

### Config

The plugin creates a config file at `plugins/ItemOfTheDay/config.yml`. All key-value pairs found in the config file can be edited through commands except for the list of `blacklisted-items`, so the only time server owners should have to edit this config file is to add or remove items from the blacklist.

By default, `blacklisted-items` contains all items which are unobtainable in survival mode. Edit the list and restart the server for blacklist changes to take effect.

Support for adding/removing items to and from the blacklist on the fly with commands will be added in a future update.

### Features

- Uses the `ItemType` registry to select items from and validate against, so any custom items added by mods or plugins will be included in IOTD by default.
- Supports command arguments of type `ItemStack` for the reward commands, allowing server operators to create unique custom rewards using the same syntax as the Vanilla `/give` command.
- `DateWatcher`: a lightweight class that keeps track of the current date by checking against a saved `LocalDate` every 1 minute (1200 ticks). Note that this is based on server ticks, so if ticks do not occur when players are offline on your server, the IOTD will be shuffled the first time someone joins on a new date (within a minute).

### Contributing

Contribute at [github.com/timnorthrop/ItemOfTheDay](https://github.com/timnorthrop/ItemOfTheDay).