![](/doc/banner.png)

[![Build](https://github.com/fletchly/genius/actions/workflows/build.yml/badge.svg)](https://github.com/fletchly/genius/actions/workflows/build.yml)

# Genius
*Large language model (LLM) integration for minecraft servers*

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
The default configuration is pretty-much plug-and-play if you are running Ollama on your server. If you are running ollama in the cloud, you will need to obtain an API key. Visit the [wiki](https://github.com/fletchly/genius/wiki/Configuration) for an in-depth explanation.
