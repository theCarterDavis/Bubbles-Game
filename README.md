# BUBBLES: Platformer Game with Level Editor

## Overview
BUBBLES is a 2D platformer game with a built-in level editor that allows you to create, save, and play your own custom levels. The game features a variety of special tiles with unique effects, physics-based gameplay with gravity and velocity, and a space-themed visual aesthetic.

## Features
- **Built-in Level Editor**: Create your own levels and save them for later play
- **Physics-Based Gameplay**: Experience realistic platforming with gravity, velocity, and collisions
- **Special Tiles**: Utilize a variety of interactive tiles with unique behaviors:
  - Ground tiles (basic platforms)
  - Start points
  - Goal points (level completion)
  - Breakable tiles that collapse after being stood on
  - Death tiles that reset the level
  - Velocity modifiers that alter player movement
  - Push/pull tiles that create force fields
  - Activator tiles that can toggle other tiles on/off
  - Timer tiles that activate periodically
- **Space-Themed Visuals**: Enjoy a starry background with animated shooting stars and parallax scrolling

## How to Play

### Controls
- **Movement**: Use A and D keys to move left and right
- **Jump**: Press SPACE to jump
- **Level Editor Mode**: Click the EDITOR button to return to the level editor

### Gameplay
1. Navigate through the level using platforms and special tiles
2. Avoid death tiles and falling off the screen
3. Use physics-based mechanics like push/pull zones to your advantage
4. Reach the goal to complete the level

## Level Editor

### Controls
- **Navigation**: Use W, A, S, D keys to move the viewport
- **Placing Tiles**: Select a tile type from the right panel and click in the grid to place it
- **Deleting Tiles**: Use the erase tool (red X) to remove tiles
- **Save & Test**: Click SAVE to save your current level, or PLAY to test it immediately

### Tile Types
- **Ground**: Basic solid platforms
- **Start**: Defines the player's starting position (required)
- **Goal**: End-level trigger
- **Break**: Platforms that collapse after a short time
- **Death**: Resets the level when touched
- **Velmo**: Modifies player velocity
- **Pushes**: Creates a repelling force field
- **Pull**: Creates an attracting force field
- **Actives**: Toggles specified tiles on/off
- **Timed**: Alternates between active/inactive states

## Customizing Tiles
You can customize tile behaviors by editing the `atributes.txt` file. Each tile type has specific parameters that can be adjusted:

```
tileName R G B A r g b a texture.png collideable numDecorators decoratorType [parameters]
```

For example:
```
break 0.0 1.0 1.0 1.0 0.0 0.0 1.0 1.0 break.png true 1 break 0.4
```

Where:
- `break` is the tile name
- The first four values are the fill color (RGBA)
- The next four values are the stroke color (RGBA)
- `break.png` is the texture file
- `true` indicates the tile is collideable
- `1` indicates one decorator will be applied
- `break` is the decorator type
- `0.4` is the parameter for the decorator (break time in seconds)

## Installation
1. Ensure you have Java and JavaFX installed on your system
2. Clone or download this repository
3. Make sure the `assets` folder containing all PNG files is in the same directory as the executable

## Development
The game uses the Decorator pattern to apply different behaviors to tiles. Here's how it works:
- `BaseTile` provides the core functionality for all tiles
- Decorators like `BreaksDecorator`, `PushDecorator`, etc. add specific behaviors
- The `TileComponent` interface ensures all tiles implement required methods
- The `CoordinateTileMap` efficiently manages tile positions using a chunk-based approach

## Credits
This game was developed as a programming project to demonstrate game development concepts, design patterns, and JavaFX capabilities.

