package me.arcator.afklog

import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class AFKLog : JavaPlugin() {
    private var listener: AFKListener? = null
    override fun onEnable() {
        config.addDefault("username", "dbuser")
        config.addDefault("password", "dbpass")
        config.addDefault("jdbcUrl", "jdbc:mariadb://localhost:3306/databaseName")
        config.addDefault("subtract", false)

        config.options().copyDefaults(true)
        saveConfig()

        if (config.getBoolean("subtract")) {
            logger.info("Subtract mode enabled. Will connect to MariaDB.")
        } else {
            logger.info("Subtract mode false. Skipping MariaDB.")
        }

        listener = AFKListener(this)
        server.pluginManager.registerEvents(listener!!, this)
    }

    override fun onDisable() {
        listener?.disable()
    }
}