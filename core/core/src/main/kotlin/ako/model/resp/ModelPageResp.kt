package ako.model.resp

data class ModelPageResp(
    val total: Int,
    val entities: List<*>,
    val information: Map<String, Any?>,
)