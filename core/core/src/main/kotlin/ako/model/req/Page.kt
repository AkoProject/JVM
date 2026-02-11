package ako.model.req

import ako.model.resp.PageResp

data class Page(
    val id: Int = 0,
    val size: Int = 10
) {
    fun <T> resp(total: Int, list: List<T>) = PageResp(id, size, total, list)
}