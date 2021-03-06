# Chess Game by Paweł Marszał 

My application, that allows two players to play chess

## How to run it

In order to run this application you need to have installed Gradle.

Then you run the game in terminal using this command:

```
gradle run
``` 

---

## Logical Data Model

Main elements of logical data model are:

<ul>
    Pieces
</ul>
<ul>
    Moves
</ul>
<ul>
    Players
</ul>
<ul>
    Game object
</ul>

Game object binds other elements together and contains an array representing board.

### Some rules

My application implements all rules of chess, including:

* promotion of pawn on the last row

* double-length start of pawn

* capturing en-passant

* castling

Application validates all possible moves that can be taken at each moment in time,
so after picking a piece player sees only those that are possible without breaking the rules.

---

## Design patterns

I have used 2 design patterns in my project:

* MVC (Model-View-Controller) - to create GUI
  
* Command - to implement moves; it is necessary, because validating moves requires
simulating them and then rewinding them back
  
---

## GUI examples

### Welcome screen

![Login Screen](docs/welcome-screen.png)

### Initial layout of the game

![Login Screen](docs/initial-game-screen.png)


### Making move

![Login Screen](docs/making-move-screen.png)

### Promotion

#### Promotion dialog

![Login Screen](docs/promotion-dialog.png)

#### Game after promotion

![Login Screen](docs/after-promotion.png)

### Victory notification screen

![Login Screen](docs/blacks-victory-screen.png)

### Draw notification screen

![Login Screen](docs/draw-end-screen.png)
