## The implementation details
I have decided to use Glassfish Tyrus implementation for Java WebSocket API, as in fact, it is suitable for a web application that requires a large volume of small messages, e.g. ticker broadcasting.

Hereby is the description of key parts:

* The `Client` class annotated with `@ClientEndpoint` contains lifecycle methods such as `onMessage`, `onClose`, etc. implemented appropriately to react the lifecycle events such as closing the connection or receiving messages.
On startup, a client endpoint instance will be created and subscription message will be sent to the websocket feed, to subscribe for the specified channel and get real-time price updates for the specified products.

* To ensure simple configuration, I have decided to have the configuration data about channel and instruments in `application.properties` file in resources.
It enables to configure the application by just updating the channel name and products in the file which keeps the application usage as simple as it can be.

* REST API is used to show the recently received data from the channel on `GET` request.

* Lombok is used to avoid boilerplate code by using builder pattern, on class annotations, etc.

## Evaluation

* The message handling mechanism expects that all received messages should have a field `type`

* Sends message synchronously using `RemoteEndpoint.Basic`, this may throw `IllegalStateException` if many threads attempt to send.

* `HashMap` is used for storing data per each instrument, which is not a thread safe data structure and should be changed to `ConcurrentHashMap` if more than one thread will try to update it in the future. 

* Unit tests are simple and check only some simple behavioural aspects of the application 

## How to run

* `./run.sh`
    * Builds and packages app into jar
    * Creates docker image based on `Dockerfile`
    * Creates container with name `ticker-client`
    
* To see the recently received data: `curl localhost:8080/`
* To see dynamic updates: `watch curl localhost:8080/`

## P.S.

For the containerization part, I have done a small research about how quickly manage the docker containers, and I have found `ctop` which does that perfectly.
[Install ctop here](https://github.com/bcicen/ctop)