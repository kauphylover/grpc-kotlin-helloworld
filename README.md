## Intro
This is a simple [gRPC][grpc] "hello-world" repo that uses Kotlin to run the gRPC server and client. I went through the [grpc-kotlin][grpc-kotlin] repo but decided to not use it as my starting point since a lot of the boilerplate code was taken care of. Since my goal was to get a handle on how stuff worked ground-up, I decided to do it all from scratch.

### Usage
1. Import the project into IntellijIDEA
2. ```mvn clean install``` through the IDE
3. Run AmallelaServer.kt to start the gRPC server up
4. Run AmallelaClient.kt to send requests to the server

[grpc]:        https://grpc.io/
[grpc-kotlin]: https://github.com/grpc/grpc-kotlin