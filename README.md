# Genius
[![Latest Release](https://img.shields.io/github/release/fletchly/genius.svg?color=6cd113)](https://github.com/fletchly/genius/releases/latest)
[![License](https://img.shields.io/github/license/fletchly/genius.svg?color=2664ff)](https://github.com/fletchly/genius/blob/main/LICENSE)

Language model integration for Paper/Spigot/Bukkit Minecraft Servers

## Downloads
- [Paper/Spigot/Bukkit](https://modrinth.com/plugin/genius)

## Features
- Interact with a language model directly from Minecraft chat
- Configurable model, name, response context, and response length.

## Commands

| Command            | Permissions | Usage              |
|--------------------|-------------|--------------------|
| `/genius <prompt>` | genius.use  | Talk to genius     |
| `/g <prompt>`      | genius.use  | Alias of `/genius` |

## Setup
**Important:** You will need to specify a model type, base url, and API key to use this plugin. For a list of supported models, click [here](/supported-models.md)

Once you have determined your API details, paste them into the `api-config` property in the plugin's config.yml, and you are good to go!

## Configuration
| Property                         | Default                                                                                                                        | Description                                                              |
|----------------------------------|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| `bot-name`                       | Genius                                                                                                                         | Name to appear in chat with when responding to questions                 |
| `api-config.model-type`          |                                                                                                                                | Language model to use. See [here](/supported-models.md) for more info    |
| `api-config.api-key`             |                                                                                                                                | Credentials for the language model API                                   |
| `api-config.base-url`            |                                                                                                                                | Base URL for the language model API endpoint                             |
| `api-config.max-tokens`          | 400                                                                                                                            | Maximum tokens allowed for response. More tokens means a longer response |
| `api-config.system-instructions` | You are a helpful assistant for players in Minecraft. Your responses are concise. Do not use markdown syntax in your responses | Context for Genius's responses                                           |
