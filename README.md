# Genius
[![Latest Release](https://img.shields.io/github/release/fletchly/genius.svg?color=6cd113)](https://github.com/fletchly/genius/releases/latest)
[![License](https://img.shields.io/github/license/fletchly/genius.svg)](https://github.com/fletchly/genius/blob/main/LICENSE)

Google Gemini integration for Paper/Spigot/Bukkit Minecraft Servers

## Downloads
- [Paper/Spigot/Bukkit]()

## Features
- Interact with Google's Gemini 2.0 Flash model directly from Minecraft chat
- Configurable name, response context, and response length.

## Commands

| Command            | Permissions | Usage              |
| ------------------ | ----------- | ------------------ |
| `/genius <prompt>` | genius.use  | Talk to genius     |
| `/g <prompt>`      | genius.use  | Alias of `/genius` |

## Setup
**Important:** You will need your own Gemini API key to use this plugin. You can get one from Google for free [here](https://aistudio.google.com/app/apikey).

Once you have secured your API key, paste it into the `gemini-config.api-key` property in the plugin's config.yml and you are good to go.

## Configuration
| Property   | Default | Description                                              |
| ---------- | ------- | -------------------------------------------------------- |
| `bot-name` | Genius  | Name to appear in chat with when responding to questions |
| `gemini-config.api-key` |  | Credentials for the Google Gemini API |
| `gemini-config.max-tokens` | 200 | Maximum tokens allowed for response. More tokens means a longer response |
| `gemini-config.system-instructions` | You are a helpful assistant for players in Minecraft. Your responses are concise. Do not use markdown syntax in your responses | Context for Gemini's responses |

---
*Gemini is a registered trademark of Google and/or its affiliates.*
