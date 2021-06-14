package com.sampaio.hiroshi.restclient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

enum BananaVariety {
    Red, LadyFinger, BlueJava, Manzano, Burro, Plantain
}

/**
 * How to use discovery with Feign
 * https://www.baeldung.com/spring-cloud-netflix-eureka#Feign
 */
@FeignClient("rest-server")
interface BananaVarietyClient {
    @GetMapping(path = "/getBananaVariety", produces = "application/json")
    ResponseEntity<Banana> getBanana(@RequestParam final String variety);
}

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@Slf4j
public class RestClientApplication {

    private final BananaVarietyClient client;

    public RestClientApplication(BananaVarietyClient client) {
        this.client = client;
    }

    public static void main(String[] args) {
        SpringApplication.run(RestClientApplication.class, args);
    }

    @Scheduled(fixedRate = 4000, initialDelay = 0L)
    public void scheduledCallToGetRedBanana() {
        final ResponseEntity<Banana> redBanana = client.getBanana("Red");
        log.info("Red banana: {}", redBanana);
    }

    @Scheduled(fixedRate = 4000, initialDelay = 2L)
    public void scheduledCallToGetNanicaBanana() {
        final ResponseEntity<Banana> nanicaBanana = client.getBanana("Nanica");
        log.info("Nanica banana: {}", nanicaBanana);
    }

}

@Data
class Banana {
    private BananaVariety variety;
    private String whoAmI;
}

/**
 * How to programmatically fetch service 
 * configuration from discovery server.
 * https://spring.io/guides/gs/service-registration-and-discovery/#_talking_to_the_registry * 
 */
@RestController
class ServiceInstanceRestController {

    private final DiscoveryClient discoveryClient;

    ServiceInstanceRestController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/service-instances/{applicationName}")
    public ResponseEntity<List<ServiceInstance>> getServiceInstance(@PathVariable final String applicationName) {
        return new ResponseEntity<>(discoveryClient.getInstances(applicationName), HttpStatus.OK);
    }

}