# Design changes to consider

* Remove local mode (two players clicking on the same window)
  - Useful game modes would be single-player against engine and multiplayer via network
  - Code could be greatly simplified and improved

# Tasks to implement:

* Client threads synchronization
  - extract game state from RemoteGameProxy into a single entity
  - use built-in synchronization mechanism to avoid race conditions between Reader and JavaFX threads
* Clean up unused code
  - primarily things like getId() etc., artifacts of design/experiments phase
* More flexible GUI
  - allow resizing windows
  - bind aspect ratios of elements (for example boards needs to be 1:1 square)
  - bind size of dynamically added elements (like board cells) to their containers/parents
  - specify as little fixed sizes as possible
* Resolve in-code TODOs
  - game outcome messages - what should be sent from server to client and what should client display
  - reuse client connections?
  - disable/enable interactions with board screen or its parts while sub-windows are active (promotions, draw etc.)
  - correct/finish escalating and/or handling exceptions, including closing sockets, interrupting threads etc.
* Code style:
  - regroup classes
  - simplify class structure, if possible (things like ImageProvider interface?)
  - rename packages/modules
  - keep functions as private as possible
  - rename classes and functions
  - enforce uniform style rules (use linter)

# Possible modernizations

* Use asynchronous I/O on server side instead to reduce number of threads
* Use external communication protocol, like gRPC, for communication to ensure safety,
error-resistance and performance.
