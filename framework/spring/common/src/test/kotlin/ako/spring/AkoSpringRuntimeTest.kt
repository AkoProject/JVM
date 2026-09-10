package ako.spring

import ako.model.base.AkoModel
import ako.model.resp.PageResp
import ako.protocol.db.DbModel
import ako.spring.db.AkoSpringDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.springframework.context.support.StaticApplicationContext

class AkoSpringRuntimeTest {

    @Test
    fun `runtime builds model contexts from the database SPI`() {
        val context = StaticApplicationContext()
        val database = RecordingDatabase()
        val runtime = AkoSpringRuntime(context, listOf(database))

        val modelContext = runtime.modelMap["TestModel"]
        assertNotNull(modelContext)
        assertEquals(TestModel::class.java, modelContext.type)

        val data = TestModel(name = "Alice")
        runtime.save(data)
        assertEquals(listOf(data), database.saved)

        modelContext.delete("9")
        assertEquals(9L, database.deletedId)

        context.close()
    }

    private class RecordingDatabase : AkoSpringDatabase {
        override val modelTypes = listOf(TestModel::class.java)
        val saved = ArrayList<TestModel>()
        var deletedId: Any? = null

        override fun <T : AkoModel> whereList(
            model: DbModel<out T>,
            params: Map<String, Any>?,
            sort: Map<String, String>?,
            page: Int?,
            size: Int?,
        ): List<T> = emptyList()

        override fun <T : AkoModel> wherePage(
            model: DbModel<out T>,
            params: Map<String, Any>?,
            sort: Map<String, String>?,
            page: Int,
            size: Int,
        ): PageResp<T> = PageResp(page, size, 0, emptyList())

        override fun <T : AkoModel> save(data: T): T {
            @Suppress("UNCHECKED_CAST")
            saved += data as TestModel
            return data
        }

        override fun delete(model: DbModel<out AkoModel>, id: Any?) {
            deletedId = id
        }
    }

    private class TestModel(
        var name: String = "",
    ) : AkoModel {
        var id: Long? = null
    }
}
