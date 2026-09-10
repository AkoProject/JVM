package ako.spring.data

import ako.model.base.CompleteModel
import ako.controller.Menu
import ako.controller.Model
import ako.model.req.ModelPageReq
import ako.spring.AkoSpringRuntime
import ako.spring.`fun`.findAccess
import ako.spring.`fun`.transaction
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.MediaType
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AkoSpringDataTestApplication::class],
    properties = [
        "spring.datasource.url=jdbc:h2:mem:ako_spring_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
    ],
)
class AkoSpringDataDatabaseTest {

    @Autowired
    lateinit var runtime: AkoSpringRuntime

    @Autowired
    lateinit var repository: TestEntityRepository

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `spring data repositories are exposed through the ako model context`() {
        repository.deleteAll()
        val saved = repository.save(TestEntity().apply { name = "Alice" })
        val second = repository.save(TestEntity().apply { name = "Bob" })
        val modelContext = runtime.modelMap["TestEntity"]
        assertNotNull(modelContext)

        val page = modelContext.wherePage(
            params = mapOf("name_like" to "A%"),
            sort = mapOf("id" to "ASC"),
            pid = 1,
            pSize = 20,
        )
        assertEquals(1, page.total)
        assertEquals(saved.id, (page.list.single() as TestEntity).id)

        val firstPage = modelContext.wherePage(emptyMap(), null, 1, 1)
        val secondPage = modelContext.wherePage(emptyMap(), null, 2, 1)
        assertEquals("Alice", (firstPage.list.single() as TestEntity).name)
        assertEquals(second.id, (secondPage.list.single() as TestEntity).id)

        modelContext.delete(saved.id.toString())
        assertFalse(modelContext.whereList().isEmpty())
        assertEquals(second.id, (modelContext.whereList().single() as TestEntity).id)
    }

    @Test
    fun `ako http endpoints keep the rain request contract after startup`() {
        repository.deleteAll()
        val saved = repository.save(TestEntity().apply { name = "Alice" })

        assertEquals(saved.id, TestEntity.findById(saved.id!!).get().id)

        val menu = request("GET", "/api/ako/menu/list/default")
        assertEquals(200, menu.statusCode())
        assertTrue(menu.body().contains("\"id\":\"TestEntity\""))

        val pageBody = """
            {"page":1,"size":20,"params":{"name_like":"A%"},"sort":{"id":"ASC"}}
        """.trimIndent()
        val page = request("POST", "/api/ako/model/page/TestEntity", pageBody)
        assertEquals(200, page.statusCode())
        assertTrue(page.body().contains("\"total\":1"))
        assertTrue(page.body().contains("\"name\":\"Alice\""))

        val save = request("POST", "/api/ako/model/save/TestEntity", "{\"name\":\"Bob\"}")
        assertEquals(200, save.statusCode())

        val delete = request("POST", "/api/ako/model/delete/TestEntity", "[\"${saved.id}\"]")
        assertEquals(200, delete.statusCode())

        val remaining = request("POST", "/api/ako/model/page/TestEntity", "{\"page\":1,\"size\":20}")
        assertEquals(200, remaining.statusCode())
        assertTrue(remaining.body().contains("\"total\":1"))
        assertTrue(remaining.body().contains("\"name\":\"Bob\""))
    }

    private fun request(method: String, path: String, body: String? = null): HttpResponse<String> {
        val builder = HttpRequest.newBuilder(URI("http://127.0.0.1:$port$path"))
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        if (method == "GET") builder.GET()
        else builder.method(method, HttpRequest.BodyPublishers.ofString(body ?: ""))
        return HttpClient.newHttpClient().send(
            builder.build(),
            HttpResponse.BodyHandlers.ofString(),
        )
    }
}

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = [TestEntityRepository::class])
open class AkoSpringDataTestApplication

interface TestEntityRepository : AkoJpaRepository<TestEntity, Int>

@Entity
@Table(name = "ako_spring_test_entity")
open class TestEntity : CompleteModel() {
    @Column(nullable = false)
    open var name: String = ""

    companion object : TestEntityRepository by findAccess()
}

@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("/api/ako/menu")
open class TestAkoMenuController {
    @org.springframework.web.bind.annotation.GetMapping("/list/{channel}")
    fun list(@org.springframework.web.bind.annotation.PathVariable channel: String): Map<String, Any> =
        Menu.list(channel)
}

@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("/api/ako/model")
open class TestAkoModelController {
    @org.springframework.web.bind.annotation.PostMapping("/page/{model}")
    fun page(
        @org.springframework.web.bind.annotation.PathVariable model: String,
        @org.springframework.web.bind.annotation.RequestBody data: ModelPageReq,
    ) = transaction { Model.page(model, data) }

    @org.springframework.web.bind.annotation.PostMapping("/save/{model}")
    fun save(
        @org.springframework.web.bind.annotation.PathVariable model: String,
        @org.springframework.web.bind.annotation.RequestBody data: TestEntity,
    ) = transaction { Model.save(model, data) }

    @org.springframework.web.bind.annotation.PostMapping("/delete/{model}")
    fun delete(
        @org.springframework.web.bind.annotation.PathVariable model: String,
        @org.springframework.web.bind.annotation.RequestBody ids: List<String>,
    ) = transaction { Model.delete(model, ids) }
}
