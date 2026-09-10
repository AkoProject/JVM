package ako.spring.boot2

import ako.model.base.AkoModel
import ako.model.resp.PageResp
import ako.protocol.db.DbModel
import ako.spring.AkoSpringRuntime
import ako.spring.db.AkoSpringDatabase
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest(classes = [AkoSpringBoot2TestApplication::class])
class AkoSpringBoot2AutoConfigurationTest {

    @Autowired
    lateinit var runtime: AkoSpringRuntime

    @Test
    fun `boot 2 loads the common runtime through spring factories`() {
        assertNotNull(runtime.modelMap["Boot2Model"])
    }
}

@SpringBootApplication(proxyBeanMethods = false)
open class AkoSpringBoot2TestApplication

@Component
open class Boot2TestDatabase : AkoSpringDatabase {
    override val modelTypes = listOf(Boot2Model::class.java)

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

    override fun <T : AkoModel> save(data: T): T = data

    override fun delete(model: DbModel<out AkoModel>, id: Any?) = Unit
}

class Boot2Model : AkoModel {
    var id: Long? = null
}
