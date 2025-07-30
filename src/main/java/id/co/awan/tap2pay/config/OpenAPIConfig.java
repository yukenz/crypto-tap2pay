package id.co.awan.tap2pay.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger OPEN API Definition
 *
 * @author yukenz
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hackathon BlockDev Id",
                description = "Simulasi Hackathon",
                version = "v1.0",
                contact = @Contact(
                        name = "Yuyun Purniawan",
                        email = "yuyun.purniawan@gmail.com"
                ),
                license = @License(name = "MIT"),
                summary = "In Development",
                termsOfService = "Nothing"
        )
)
public class OpenAPIConfig {
}