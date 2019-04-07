# Keyboard only UI management

An idea to make it possible to control Elastic completely by just using the keyboard. 
## Shortcuts

### Global - works everywhere
Space - Play/pause
Esc - Go back in whatever Window/state we are in
CTRL+Z - Undo
CTRL+SHIFT+Z - Redo

## NodeView
- A = Open "Add node"-window
    <input text> - Search for the node's name, including presets
- S = Search for an existing node
    - (input text) = Search for an existing node by name. Dynamically scroll to it
    - Enter = Close window and select node
    - Esc = Scroll back to original position in NodeView
- Arrow keys = Move between nodes
    - Enter = Select node and enter focus
        - Esc = Exit node focus
        - Arrow keys = Move between ports and UI components in the node
            - Enter = Follow 