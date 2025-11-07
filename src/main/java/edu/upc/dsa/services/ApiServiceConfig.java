package edu.upc.dsa;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;

public class ApiServiceConfig extends ResourceConfig {
    public ApiServiceConfig() {
        // Paquete donde están los @Path
        packages("edu.upc.dsa.services");

        // Swagger (recursos que exponen /swagger.json)
        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        // BeanConfig (swagger-core 1.x)
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Biblioteca Rural API");
        beanConfig.setDescription("Servicio REST del Mínimo 1");
        beanConfig.setVersion("1.0");
        // Base path del API (ajústalo si en Main montas otro)
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/api"); // <- importante
        beanConfig.setResourcePackage("edu.upc.dsa.services");
        beanConfig.setScan(true);
    }
}
