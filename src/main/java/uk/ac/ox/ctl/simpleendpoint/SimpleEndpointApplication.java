package uk.ac.ox.ctl.simpleendpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SimpleEndpointApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleEndpointApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*").allowedOrigins("*");
            }
        };
    }

    @RestController
    public static class SimpleController {

        private final Map<String, Item> items = new HashMap<>();

        @GetMapping("/get")
        public ResponseEntity<Item> get(HttpServletRequest request) {
            String remoteAddr = request.getRemoteAddr();
            Item item = items.get(remoteAddr);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(item);
        }

        @PostMapping(value = "/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        public Item updateForm(HttpServletRequest request, @Valid Item item) {
            return this.updateJson(request, item);
        }

        @PostMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
        public Item updateJson(HttpServletRequest request, @RequestBody @Valid Item item) {
            String remoteAddr = request.getRemoteAddr();
            items.put(remoteAddr, item);
            return item;
        }

        @PostMapping("/clear")
        public ResponseEntity<Void> clear(HttpServletRequest request) {
            if (items.remove(request.getRemoteAddr()) != null) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

    }

    public static class Item {
        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
