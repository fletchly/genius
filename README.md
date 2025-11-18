![](/doc/banner.png)

[![Build](https://github.com/fletchly/genius/actions/workflows/build.yml/badge.svg)](https://github.com/fletchly/genius/actions/workflows/build.yml)

# Genius
*Large language model (LLM) integration for Minecraft servers.*

> [!CAUTION]
> This plugin is still in **early beta** stages of development and has **not** been fully tested.
>
> Even though it doesn't interact with critical server systems (like worlds, player data, or core mechanics), beta software always carries some risk.
>
> That means:
> - Expect bugs, crashes, and unexpected behavior
> - Some features may be incomplete, broken, or subject to major changes
> - Performance is not yet optimized
>
> **Use at your own risk!**
> 
> If you encounter issues, please report them on the issue tracker with as much detail as possible (logs, steps to reproduce, etc.). Your feedback helps improve the plugin!
> 
> Thank you for supporting the development of Genius. 💡



## Features
- Custom model support via Ollama API
- Configurable response parameters
- Async execution

## Installation
1. Download latest release from the [releases](https://github.com/fletchly/genius/releases/latest) page.
2. Drop the .jar file into your server’s plugins/ folder.
3. Follow the [getting started](https://github.com/fletchly/genius/wiki/Getting-Started) guide to configure the plugin.

## Commands
| Command | Aliases | Permissions  | Description           |
|---------|---------|--------------|-----------------------|
| `/ask`  | `/g`    | `genius.ask` | Ask Genius a question |

## Permissions
| Permission   | Default | Description                              |
|--------------|---------|------------------------------------------|
| `genius.ask` | `true`  | Allow players to make requests to genius |

## Configuration
The default configuration is pretty much plug-and-play if you are running Ollama on your server. If you are running ollama in the cloud, you will need to obtain an API key. Visit the [wiki](https://github.com/fletchly/genius/wiki/Configuration) for an in-depth explanation.
