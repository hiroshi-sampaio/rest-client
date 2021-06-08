package com.sampaio.hiroshi.restclient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

enum BananaVariety {
    Red, LadyFinger, BlueJava, Manzano, Burro, Plantain;
}

@FeignClient(name = "feign-client", url = "${com.sampaio.hiroshi.rest-server.url}")
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
