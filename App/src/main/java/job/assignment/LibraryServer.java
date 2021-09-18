package job.assignment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.properties.PropertyFileAuthentication;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import job.assignment.model.Book;

import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryServer extends AbstractVerticle {

    private Map<Integer, Book> books = new LinkedHashMap<>();
    private MySQLPool client;

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        client.close();
    }

    @Override
    public void start(Promise<Void> fut) {

        // Create a router object.
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        // auth service which uses a properties file for user/role info
        PropertyFileAuthentication authn = PropertyFileAuthentication.create(vertx, "vertx-users.properties");

        // Any requests to URI starting '/private/' require login
        router.route("/private/*").handler(RedirectAuthHandler.create(authn, "/loginpage.html"));

        // Bind "/" to message.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Welcome to the library</h1>");
        });

        // Any requests to URI starting '/private/' require login
        router.route("/private/*").handler(RedirectAuthHandler.create(authn, "/loginpage.html"));

        // Serve the static private pages from directory 'private'
        router.route("/private/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("private"));

        // Handles the actual login
        router.route("/loginhandler").handler(FormLoginHandler.create(authn));

        router.route("/private/lend/:book_id/:user_id").handler(this::lendBook);

        router.route("/login/:username/:password").handler(this::lendBook);
        router.get("/private/book/return/:id").handler(this::returnBook);
        router.get("/private/user/add/:name/:role").handler(this::addUser);
        router.get("/private/user/update/:id").handler(this::updateBook);
        router.get("/private/user/get/:id").handler(this::getbook);
        router.get("/private/book/add/:isbn/:title/:author").handler(this::addBook);
        router.get("/private/book/get/:id").handler(this::getUser);
        router.get("/private/book/update/:id").handler(this::updateUser);
        router.delete("/private/delete/user/:id").handler(this::deleteUser);

        // Implement logout
        router.route("/logout").handler(context -> {
            context.clearUser();
            // Redirect back to the index page
            context.response().putHeader("location", "/").setStatusCode(302).end();
        });

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8081.
                        config().getInteger("http.port", 8081),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("localhost")
                .setDatabase("librarydb")
                .setUser("root")
                .setPassword("root");

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        // Create the client pool
        client = MySQLPool.pool(connectOptions, poolOptions);

    }

    private void updateUser(RoutingContext routingContext) {

    }

    private void getbook(RoutingContext routingContext) {

    }

    private void lendBook(RoutingContext routingContext) {

    }

    private void returnBook(RoutingContext routingContext) {

    }

    private void addUser(RoutingContext routingContext) {

    }

    private void getUser(RoutingContext routingContext) {

    }

    private void updateBook(RoutingContext routingContext) {

    }

    private void deleteUser(RoutingContext routingContext) {

    }

    private void addBook(RoutingContext routingContext) {
}
}
