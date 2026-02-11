package testako.controller

import rain.controller.annotation.Path
import smartweb.annotation.CookieValue
import smartweb.annotation.GetAction
import smartweb.annotation.SessionValue
import smartweb.annotation.WebController

@Path("auth")
@WebController
class AuthController : AkoController() {


    @GetAction("isAuth")
    fun isAuth(@SessionValue isLogin: String? = null, @CookieValue ako: String? = null): Boolean {
        return true
    }

}