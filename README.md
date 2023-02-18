# Chess Game by Paweł Marszał 

The project implements server and client applications for local and remote multiplayer chess.
* Local mode uses one board shared by two people using one machine and does not require internet connection.
* Remote mode allows clients to play over network via server that ensures chess rules
and state integrity. 

## Motivation

The goal I have for this project is to learn and practice various concepts
that may be useful for Java programmer.
That includes:
* System design - I've had to decide which communication model to choose, how to organize
computation on server and client side, define interfaces between packages and modules etc.
* Multi-module Maven project
* Java modules
* JavaFX app structure and GUI design
* Building and running Docker images
* Packaging installable self-contained Java applications using `jpackage`
* Websockets - I've defined simple communication protocol over TCP sockets to allow bidirectional communication
* Network routing to self-host server using my personal public IP

## Technologies used

* Java 17
* JavaFX 17
* Maven
* Websockets
* Docker
* JPackage

## Build process

Only prerequisite is Java 17 installed.

Then to build the project use `./mvwn verify` on Linux or `.\mvwn.cmd verify` on Windows.
Maven wrapper will take care of downloading Maven and the rest of dependencies.

Afterwards, to create server's Docker image use script `./pmdocker` (on Linux environment).

Client app installer is created automatically and located in `chess-client/target/jpackage` directory.
Because `jpackage` tool doesn't support cross-platform builds Windows and/or Linux clients
need to be build on target platforms.

If environment variable `SERVER_ADDRESS` is specified during build process, it will be default
route that clients will try to connect to. Example of this variable looks like:
`SERVER_ADDRESS='127.0.0.1 11111'`.

Alternatively user can provide server's IP and port as command line arguments to client, like:
`.\windows-chess-client.exe 127.0.0.1 11111`.

## How to run it

Server can be run either directly (requires installation of Java) or as a docker container.

Client is standalone application. After you build the installer or download it from other source,
you can install it on your system and use like any other program.

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
