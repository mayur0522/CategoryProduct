package com.Rest.CategoryProduct.Controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {
    private final MeterRegistry meterRegistry;

    public HomeController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Counted(value = "get_home_requests_total", description = "Counts the number of times /home/currentUserName is called")
    @Timed(value = "get_home_requests_duration", description = "Times how long /home/currentUserName takes to respond")
    @GetMapping("/currentUserName")
    public String getLoggedInUser(Principal principal){
//        meterRegistry.counter("get_home_requests_total", "endpoint", "/home/currentUserName", "method", "GET").increment();
        log.info("Current Username {}",principal.getName());
        return principal.getName();
    }


/*<<<<<<< Updated upstream
=======

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody AppUser user){
        String status = userService.createUser(user);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
>>>>>>> Stashed changes*/
}