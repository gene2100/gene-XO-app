# Gene Tic-Tac-Toe

## Prerequisite

* Docker Desktop
* Node 22.9.0+
* Java 17.0.9+
* Maven 3.9.9+

## Developing or Testing Steps

1. Initialize PostgreSQL database in docker

    In `root` folder
    ```bash
    # run PostgreSQL docker container
    $ docker-compose up -d
    ```

2. Run Java Spring Boot backend service server

    In `\backend` folder
    ```bash
    # clean and install dependencies
    $ mvn clean install

    # start backend service
    $ mvn spring-boot:run
    ```

3. Run SvelteKit web frontend 

    In `\frontend` folder
    ```bash
    # install node_modules
    npm install

    # start frontend web
    npm run dev
    ```

4. Open to [localhost:5173](http://localhost:5173) for play Tic-Tac-Toe