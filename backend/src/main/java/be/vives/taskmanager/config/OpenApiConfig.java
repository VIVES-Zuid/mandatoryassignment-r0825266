package be.vives.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TaskManager API",
                version = "1.0.0",
                description = """
                        RESTful API for managing projects (with project owners/users) and tasks.

                        **Authentication**
                        - JWT Bearer token authentication

                        **Authorization**
                        - USER: can create, read, update projects and tasks
                        - ADMIN: same as USER + can delete projects and tasks + can make a user admin

                        **Some business rules you should know**
                        - Projects with active tasks cannot be deleted
                        - Tasks with status DONE cannot be modified( except if the only field being changed is status) or deleted
                        """,
                contact = @Contact(
                        name = "Andang Kloran",
                        email = "andang.kloranawah@student.vives.be",
                        url = "https://www.vives.be"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development"),
                @Server(url = "https://https://api.kloran-taskmanager.org", description = "Production")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = """
                JWT Bearer authentication.

                Obtain a token via:
                - POST /auth/register
                - POST /auth/login

                Use the token in the Authorize dialog.
                """
)
public class OpenApiConfig {
}
