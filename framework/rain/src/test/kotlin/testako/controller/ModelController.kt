package testako.controller

import ako.AkoService
import ako.controller.Model
import ako.model.req.ModelPageReq
import ako.model.resp.ModelPageResp
import ako.rain.`fun`.transaction
import com.alibaba.fastjson2.JSON
import rain.controller.annotation.Path
import smartweb.annotation.PostAction
import smartweb.annotation.RequestBody
import smartweb.annotation.WebController


@Path("model")
@WebController
class ModelController: AkoController(){

    @PostAction("page/{model}")
    fun page(@RequestBody data: ModelPageReq/*, user: AkoUser*/, model: String): ModelPageResp = transaction {
        Model.page(model, data)
    }

    @PostAction("delete/{model}")
    fun delete(model: String, @RequestBody ids: List<Int>/*, user: AkoUser*/) = transaction {
        Model.delete(model, ids)
    }

    @PostAction("save/{model}")
    fun save(model: String, @RequestBody data: String/*, user: AkoUser*/) {
        transaction {
            val mc = AkoService.modelOf(model)
            Model.save(model, JSON.parseObject(data, mc.type))
        }
    }

}