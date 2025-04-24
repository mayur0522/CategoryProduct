package com.Rest.CategoryProduct.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/currentUserName")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }


//<<<<<<< Updated upstream
//=======
//
//    @PostMapping("/register")
//    public ResponseEntity<String> createUser(@RequestBody AppUser user){
//        String status = userService.createUser(user);
//        return new ResponseEntity<>(status, HttpStatus.OK);
//    }
//>>>>>>> Stashed changes
}