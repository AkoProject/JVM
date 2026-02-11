package ako.model.resp

data class PageResp<T>(
    val page: Int,
    val size: Int,
    val total: Int,
    val list: List<T>
) {

    fun <R> map(transform: (T) -> R): PageResp<R> = PageResp(page, size, total, list.map(transform))
}