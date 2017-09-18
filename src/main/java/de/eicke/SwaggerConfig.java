package de.eicke;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.AuthorizationScope;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
 
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
    	return new Docket(DocumentationType.SWAGGER_2)
    	        .select()
    	          .apis(RequestHandlerSelectors.any())
    	          .paths(PathSelectors.any())
    	          .build()
    	        .pathMapping("/")
    	        .directModelSubstitute(LocalDate.class,
    	            String.class)
    	        .useDefaultResponseMessages(false)
    	        .enableUrlTemplating(true)
    	        .tags(new springfox.documentation.service.Tag("Travel Manager", "All apis relating to travels")) 
    	        ;
    	  }

    	 
    	  private ApiKey apiKey() {
    	    return new ApiKey("mykey", "api_key", "header");
    	  }



    	  @Bean
    	  UiConfiguration uiConfig() {
    	    return new UiConfiguration(
    	        "validatorUrl",// url
    	        "none",       // docExpansion          => none | list
    	        "alpha",      // apiSorter             => alpha
    	        "schema",     // defaultModelRendering => schema
    	        UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
    	        false,        // enableJsonEditor      => true | false
    	        true,         // showRequestHeaders    => true | false
    	        60000L);      // requestTimeout => in milliseconds, defaults to null (uses jquery xh timeout)
    	  }    
}