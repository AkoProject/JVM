package test.ako.controller

import ako.controller.Menu
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/ako/menu")
class MenuController {

    @GetMapping("list/{channel}")
    fun list(@PathVariable channel: String) = Menu.list(channel)

}