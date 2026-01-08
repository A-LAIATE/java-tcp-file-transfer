
# Java TCP File Transfer - Client & Server 

A simple TCP client–server application written in Java. The server handles multiple clients concurrently using a fixed thread pool and supports two client commands:

- `list` — list available `.txt` files stored on the server
- `put <file>` — upload a text file from the client to the server

The server also writes a log entry for each request.

---

## Key features

- TCP sockets (client ↔ server)
- Fixed-size thread pool (concurrent clients)
- Simple text-based application protocol
- File upload (`put`) with duplicate-file protection
- File listing (`list`)
- Server-side logging to `log.txt`

---

## Project structure

Recommended repository layout:

```
java-tcp-file-transfer/
client/
Client.java
lipsum2.txt           (optional sample file)
server/
Server.java
ClientHandler.java
LogUtility.java
serverFiles/          (server storage directory)
lipsum1.txt           (optional sample file)
README.md
.gitignore

````

### What each part does
- `server/Server.java`  
  Starts the server, opens the listening socket, accepts connections, and dispatches each client to the thread pool.
- `server/ClientHandler.java`  
  Handles a single client request (reads command, executes `list` or `put`, returns response).
- `server/LogUtility.java`  
  Appends request information to `server/log.txt`.
- `server/serverFiles/`  
  The directory where the server stores files uploaded by clients.
- `client/Client.java`  
  Command-line client that connects to the server, sends a command, and prints the server response.

---

## Requirements

- Java JDK (recommended: OpenJDK 11+)
  - Download: https://openjdk.org/

---

## Compile

Open a terminal at the repository root.

### Compile the server

```bash
cd server
javac *.java
````


### Compile the client

```bash
cd ../client
javac *.java
```

---

## Run

### 1) Start the server (Terminal 1)

From the repo root:

```bash
cd server
java Server
```

The server listens on `localhost` using the port configured in `Server.java`.

### 2) Use the client (Terminal 2)

#### List files on the server

From the repo root:

```bash
cd client
java Client list
```

Expected result:

* The client prints the list of `.txt` files stored under `server/serverFiles/`
* Output ends when the server sends an end marker (see protocol below)

#### Upload a file to the server

From the repo root:

```bash
cd client
java Client put lipsum2.txt
```

Expected result:

* If the file does not already exist on the server, it is saved under `server/serverFiles/`
* If the file already exists, the server rejects the upload

---

## Protocol (how the client & server communicate)

This project uses a lightweight line-based protocol:

* **Client → Server**

  * `list`
  * or `put <filename>` followed by the file content line-by-line

* **End-of-data marker**

  * `EOF` is used to indicate the end of a multi-line response or file transfer.

This keeps the implementation simple and predictable for automated testing and debugging.

---

## Output files (generated at runtime)

* Uploaded files are stored in:

  * `server/serverFiles/`
* Logs are written to:

  * `server/log.txt`

---

## Configuration

If you change the server port in `server/Server.java`, ensure the client uses the same port in `client/Client.java`.

---

## Troubleshooting

* **`Connection refused`**: start the server first, then run the client.
* **Port already in use**: stop the process using that port, or change the port in both client and server.
* **File not found on upload**: ensure the file path you pass to `put` exists (e.g., `lipsum2.txt` is in the `client/` folder if you run the client from there).
