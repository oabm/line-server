# LineServer

The server that serves lines.

## What is This?

This is a solution to the problem presented [here](https://salsify.github.io/line-server.html), know colloquially as "Salsify's Line Server".

It is a simple REST API that returns lines from a local file, addressed by index.

## Running the Server

This project require `sbt` to run. Installation instructions for `sbt` can be found [here](https://www.scala-sbt.org/1.0/docs/Setup.html).

```bash
$ git clone git@github.com:oabm/line-server.git
$ cd line-server
$ ./build.sh
$ ./run.sh /path/to/my/favorite/file
```

## Accessing the Server

Once the server has been started (see above), it can be accessed on localhost, port 9000.

e.g.

```bash
$ curl localhost:9000/lines/42
```

## Answers to all the Questions

#### How does the system work?

The server is a very basic Scala Play Framework application. There is a single controller (creatively named `LineController`), which delegates the responsibility of actually fetching the proper line to a `FileReader`, of which there are several varieties:

###### `CheckpointedFileReader`

This is the default implementation, due to its ability to handle files of arbitrary size.

When the server starts up, the `CheckpointedFileReader` reads through the specified file once, taking notes as it goes. These notes take the form of "checkpoints" specifying at what byte certain lines begin.

When a line needs to be fetched, the `CheckpointedFileReader` looks up the checkpoint located before the desired line. It then opens the file, navigates to the byte specified by the checkpoint, and reads lines until it reaches the desired line.

###### `GreedyFileReader`

When the server starts up , the `GreedyFileReader` reads the entire file into memory, storing it as an array. When a line is requested, it is immediately returned without any fuss.

This implementation is extremely speedy, and can be utilized by passing the `--greedy` flag to the `run.sh` script. Unfortunately it will explode for large files, so use with care!

###### `TestFileReader` and `PloddingFileReader`

These are test implementations, and should be ignored.


#### How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?

Increasing the size of the served file will increase startup time proportionally (as the entire file is read once initially). It will also increase the time to fetch lines farthest from checkpoints. The time to fetch lines immediately following checkpoints should not be greatly affected, even as file size increases by orders of magnitude.

As a side note, for files that fit in memory, performance can be _greatly_ increased by using the `--greedy` flag.


#### How will your system perform with 100 users? 10000 users? 1000000 users?

10,000 concurrent requests (assuming that is what is meant by "users") would  certainly result in timeouts. As would 1,000,000. However, if these users were only querying the server infrequently, there is no intrinsic limit to the number of distinct users it could support.

#### What documentation, websites, papers, etc did you consult in doing this assignment?

I didn't do any research on how this problem (serving a too-big-for-memory file) is solved is in the wild. I just came up with and idea and started hacking away.

That being said, there was plenty of Googling and lots of:
- https://www.playframework.com/documentation/2.6.x
- https://stackoverflow.com/

for Play/Scala best practices and syntax.


#### What third-party libraries or other tools does the system use? How did you choose each library or framework you used?

This project uses the following:
- [Play Framework](https://www.playframework.com/documentation/2.6.x)
- [sbt](https://www.scala-sbt.org/)

I have had some experience with Play in the past, and when I saw Scala was a preferred language I decided to go with it. As for `sbt`, it seems to go hand-in-hand with Scala development (especially when using IntelliJ).


#### How long did you spend on this exercise? If you had unlimited more time to spend on this, how would you spend it and how would you prioritize each item?

I spent in the ballpark of 4 hours on the project.

A list of possible improvements:
- Be smart when choosing the `FileReader`. If the entire file will fit in memory, be greedy!
- Add some caching. Because the lines are never going to change, a simple LRU cache could be used.
- Update the `CheckpointedFileReader` to be able to search backwards from checkpoints, not just forwards.
- Update the `CheckpointedFileReader` to find relevant checkpoints via a binary search tree.
- Use a shared pool of file handles, instead of opening/closing one for each request.
- Add some unit/integration tests!

It is difficult to prioritize these without knowing the objective of this project. Depending on the expected use-case for this application, different features will have different benefits. That being said, throwing in an LRU cache would be a fast addition, likely to be helpful in any use-case.


#### If you were to critique your code, what would you have to say about it?

Some of my Scala code is basically Java, written with Scala syntax. I'm sure I'm missing out on Scala-isms and best practices.

