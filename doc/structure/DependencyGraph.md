# Dependency Graph

Elastic has a dependency graph to take care of how resources depends on each other, and what can be safely deleted.

## Structure of dependencies
```
Top
    Revision
        NetList
            Node
                Link
                    Revision
                        …
                Audio
                Midi
                Image
                Video
    Revision
        …same as above
```
Here nodes points on an audio clip, which will never be deleted as long as the Node is pointing at it (and that something else is pointing at it.

Note: Multiple nodes can point at the same audio file

When saving the project (Creating a checkpoint which creates a revision), the revision tree gets duplicated.

We will support of deleting and merging old history, which will delete older revision, and will then clear out any resources that was previously used, but no more.