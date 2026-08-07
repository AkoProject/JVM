package ako.rain.access

import ako.model.base.SoftDeleteModel
import smartaccess.annotation.MetadataProvider
import smartaccess.annotation.ProvideAccessTemple
import smartaccess.jpa.access.QueryRewriter
import java.io.Serializable

@ProvideAccessTemple
@MetadataProvider(AkoMetadataProvider::class)
interface SoftDeleteAccess<T : SoftDeleteModel, PK : Serializable> : AkoAccess<T, PK> {

    override val executeRewriter: QueryRewriter?
        get() = QueryRewriter {
            if (!it.startsWith("delete", ignoreCase = true)) return@QueryRewriter it
            it.replace("delete form", "update", ignoreCase = true)
                .replace("where", "set deleteTime = ${System.currentTimeMillis()} where")
        }

}