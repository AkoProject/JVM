package ako.rain.controller

import ako.controller.Menu
import smartweb.annotation.GetAction
import javax.inject.Named

@JvmDefaultWithoutCompatibility
interface IMenuController {

    @GetAction("list/{channel}")
    fun list(@Named("channel") channel: String/*, user: AkoUser*/): Map<String, Any> = Menu.list(channel)

}