package ako.controller

import ako.AkoService
import ako.model.req.ModelPageReq
import ako.model.resp.ModelPageResp
import ako.rain.`fun`.transaction
import com.alibaba.fastjson2.JSON
import smartweb.annotation.PostAction
import smartweb.annotation.RequestBody
import javax.inject.Named

@JvmDefaultWithoutCompatibility
interface IModelController {

    @PostAction("page/{model}")
    fun page(@RequestBody data: ModelPageReq/*, user: AkoUser*/, @Named("model") model: String): ModelPageResp = transaction {
        Model.page(model, data)
    }

    @PostAction("delete/{model}")
    fun delete(@Named("model") model: String, @RequestBody ids: List<String>/*, user: AkoUser*/) = transaction {
        Model.delete(model, ids)
    }

    @PostAction("save/{model}")
    fun save(@Named("model") model: String, @RequestBody data: String/*, user: AkoUser*/) {
        transaction {
            val mc = AkoService.modelOf(model)
            Model.save(model, JSON.parseObject(data, mc.type))
        }
    }

}