package ako.model.req

data class ModelPageReq(
    /** Frontend page ids are 1-based. */
    val page: Int = 1,
    val size: Int = 20,
    val params: Map<String, String> = emptyMap(),
    val sort: Map<String, String> = emptyMap(),
)
