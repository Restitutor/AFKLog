package me.arcator.afklog

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin

class DatabaseConnector(private val plugin: JavaPlugin) {
    private val config = HikariConfig()
    private val hikari: HikariDataSource

    init {
        assert(plugin.config.getBoolean("subtract"))
        config.maximumPoolSize = 1
        config.jdbcUrl = plugin.config.getString("jdbcUrl")
        config.username = plugin.config.getString("username")
        config.password = plugin.config.getString("password")
        // https://stackoverflow.com/a/63998678
        Class.forName("org.mariadb.jdbc.Driver")
        hikari = HikariDataSource(config)
    }

    fun onAfkChange(uuid: String, secondsAfk: Int) {
        DatabaseRunnable(plugin.logger, hikari, uuid, secondsAfk).runTaskAsynchronously(plugin)
    }

    fun close() {
        hikari.close()
    }
}