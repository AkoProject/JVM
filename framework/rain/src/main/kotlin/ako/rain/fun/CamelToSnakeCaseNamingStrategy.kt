package ako.rain.`fun`

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

class CamelToSnakeCaseNamingStrategy : PhysicalNamingStrategy {

    override fun toPhysicalCatalogName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(name)
    }

    override fun toPhysicalSchemaName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(name)
    }

    override fun toPhysicalTableName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(name)
    }

    override fun toPhysicalSequenceName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(name)
    }

    override fun toPhysicalColumnName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(name)
    }

    private fun convertToSnakeCase(name: Identifier?): Identifier? {
        if (name == null) return null
        if (name.isQuoted) return name
        val newName = name.text
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
        return Identifier.toIdentifier(newName)
    }
}