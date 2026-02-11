package testako.controller

import ako.controller.IMenuController
import ako.controller.Menu
import rain.controller.annotation.Path
import smartweb.annotation.GetAction
import smartweb.annotation.WebController

@Path("menu")
@WebController
class MenuController: AkoController(), IMenuController{


}