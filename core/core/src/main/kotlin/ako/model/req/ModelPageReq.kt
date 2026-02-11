package ako.model.req

data class ModelPageReq(
    val page: Int = 0,
    val size: Int = 20,
    val params: Map<String, String> = emptyMap(),
    val sort: Map<String, String> = emptyMap(),
)