package com.example.soilserverspring.firebase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util. concurrent. ExecutionException;

@RestController
@RequestMapping("/users")
public class FirebaseRestController {

    @Autowired
    FirebaseService fireBaseService;

    @GetMapping("/getUserSamplesDetails")
    public String getUserSamplesDetails(@RequestHeader() String email) throws ExecutionException, InterruptedException {
        System.out.println("Controller(/getUserSamplesDetails): получен запрос на обработку");

        return fireBaseService.getUserSamplesDetails(email);
    }

    @PostMapping("/createUser")
    public String saveUserDetails(@RequestHeader String email) {
        System.out.println("Controller(/createUser): получен запрос на обработку");
        return fireBaseService.saveUserDetails(email);
    }

    @DeleteMapping("/deleteUserSample")
    public String deleteUserSample(
            @RequestHeader String email,
            @RequestHeader String sampleDate){
        System.out.println("Controller(/deleteUserSample): получен запрос на обработку");

        return fireBaseService.deleteUserSample(email, sampleDate);
    }

}