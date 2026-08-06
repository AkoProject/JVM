package testako.controller

import ako.rain.controller.IMenuController
import rain.controller.annotation.Path
import smartweb.annotation.WebController

@Path("menu")
@WebController
class MenuController: AkoController(), IMenuController{


}