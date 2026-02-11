package ako.controller

import smartweb.annotation.GetAction

interface IMenuController {

    @GetAction("list/{channel}")
    fun list(channel: String/*, user: AkoUser*/): Map<String, Any> = Menu.list(channel)

}