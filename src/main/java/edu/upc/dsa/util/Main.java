package edu.upc.dsa.util;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.io.IOException;
import java.net.URI;


public class Main {


    public static final String BASE_URI = "http://localhost:8080/dsaApp/";


    public static HttpServer startServer() {

        // Configuración de recursos REST
        ResourceConfig rc = new ResourceConfig().packages("edu.upc.dsa.services");
        rc.register(org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);

        // ---- Swagger ----
        rc.register(ApiListingResource.class);
        rc.register(SwaggerSerializers.class);


        rc.property(ServerProperties.WADL_FEATURE_DISABLE, true);

        // ---- Configuración de Swagger ----
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Biblioteca Rural REST API");
        beanConfig.setDescription("Servicio REST del proyecto Minim1-DSA");
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/dsaApp");
        beanConfig.setResourcePackage("edu.upc.dsa.services");
        beanConfig.setScan(true);

        // ---- Crear servidor HTTP ----
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, false);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();


        StaticHttpHandler staticHandler = new StaticHttpHandler("src/main/resources/public");
        server.getServerConfiguration().addHttpHandler(staticHandler, "/");

        // Iniciar servidor
        NetworkListener listener = server.getListeners().iterator().next();
        System.out.println("Servidor iniciado en: " + listener.getHost() + ":" + listener.getPort());
        System.out.println("Base URI: " + BASE_URI);
        System.out.println("Swagger UI disponible en: http://localhost:8080/");
        System.out.println("Presiona ENTER para detener...");

        server.start();
        System.in.read();
        server.shutdownNow();
    }
}
