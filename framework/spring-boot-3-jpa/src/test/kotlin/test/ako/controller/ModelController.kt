package test.ako.controller

import ako.AkoService
import ako.controller.Model
import ako.spring.`fun`.toObject
import ako.`fun`.webError
import ako.model.req.ModelPageReq
import ako.spring.`fun`.transaction
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/ako/model")
class ModelController {

    @PostMapping("page/{model}")
    fun page(@RequestBody data: ModelPageReq, @PathVariable model: String) = transaction { Model.page(model, data) }

    @PostMapping("save/{model}")
    fun save(@PathVariable model: String, @RequestBody data: JsonNode) {
        val dbType = AkoService.modelMap[model]
            ?: webError(881001001, "模型 $model 不存在")
        val obj = data.toObject(dbType.model)
        Model.save(model,obj)
    }

    @PostMapping("delete/{model}")
    fun delete(@PathVariable model: String, @RequestBody ids: List<Int>) = transaction { Model.delete(model, ids) }
}