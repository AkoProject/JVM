package test.ako.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/ako/auth")
class AuthController {

    @GetMapping("isAuth")
    fun isAuth() = true

    @PostMapping("login")
    fun login() {
    }

}