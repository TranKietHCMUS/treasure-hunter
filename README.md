# Mobile Final Project - Treasure Hunter
## Introduction
Treasure Hunter is the final project for the Mobile Application Development course. It is a treasure-hunting game app designed to provide an engaging and interactive experience. 

In this game, players will explore a map featuring their current location and the locations of hidden treasures. When a player gets close to a treasure, they can activate the AR (Augmented Reality) mode to locate the treasure in their surroundings. Upon discovering a treasure, the player can tap on it to open the chest.

The game includes nine treasures randomly placed within a predefined radius. Each treasure chest contains a piece of a larger image that forms part of a secret keyword. The ultimate goal is to collect all nine pieces to reveal the keyword, but players can also try to guess the keyword at any time for a chance to win.

## Features
1. **Interactive Map:**
   - Displays the player's current location.
   - Highlights the approximate locations of treasures within the game radius.

2. **Treasure Hunting:**
   - Navigate to treasure locations using the map.
   - Unlock treasures by entering AR mode when close to a marked location.
   - Use AR to visually search for and interact with treasure chests.

3. **Keyword Puzzle:**
   - Each opened treasure reveals 1/9th of an image.
   - The completed image contains a secret keyword.
   - Players can attempt to guess the keyword at any stage for a chance to win.

4. **Randomized Gameplay:**
   - Treasure locations are randomly generated within a specific area, making each game unique.

5. **Augmented Reality (AR):**
   - Immersive AR experience to discover and interact with treasures.

6. **Winning Conditions:**
   - Collect all nine pieces to fully reveal the keyword.
   - Alternatively, guess the keyword based on incomplete pieces.

## Game Flow
1. Launch the app and view the map with your location and treasure markers.
2. Move toward a treasure marker on the map.
3. When you are close to a treasure, activate AR mode.
4. Locate the treasure in AR and tap on it to open the chest.
5. Collect pieces of the image and work toward revealing the keyword.
6. Guess the keyword at any time or continue until all treasures are found.
7. Win by correctly guessing the keyword or by collecting all nine pieces.

## Technology Stack
- **Frontend:** Kotlin
- **Backend:** Firebase
- **AR Framework:** ARCore (Android)
- **Mapping:** Google Maps API
- **Database:** Firebase Firestore
- **Authentication:** Firebase Authentication

## Future Enhancements
- Leaderboard to compare scores with other players.
- Multiplayer mode for competitive treasure hunting.
- Daily or weekly challenges with unique rewards.

There are our team's infomations:
- 22120038: [Chi-Cong Nguyen](https://github.com/nccongg)
- 22120082: [Quoc-Duy Tran](https://github.com/QDuy0082)
- 22120176: [Anh-Kiet Tran-Nhu](https://github.com/TranKietHCMUS)
- 22120282: [Gia-Phuc Song-Dong](https://github.com/fusodoya)
  
## Video demo

<p align="center"> 
  <a href="https://youtu.be/XS3Y0HN8Uic" target="_blank">
  <img src="https://github.com/user-attachments/assets/ebca3c50-4a8d-4dc1-9b3e-a7ef2f7d9aa7" alt="Image description" width="360">
</a>


## Setting

1. Install Android Studio.
2. Clone and open project.
3. Synchronize gradle.
4. Connect to your device, build and run app.

For the software to work, make sure your device supports ARcore and GPS/VPS.
