package me.arcator.afklog

import net.ess3.api.IUser
import net.ess3.api.events.AfkStatusChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.lang.management.ManagementFactory

class AFKListener(private var plugin: JavaPlugin) : Listener {
    private var connector: DatabaseConnector? = null

    init {
        if (plugin.config.getBoolean("subtract")) {
            connector = DatabaseConnector(plugin)
        }
    }

    @EventHandler
    fun onAfkChange(event: AfkStatusChangeEvent) {
        val person: IUser = event.affected
        if (!person.isAfk) return

        // Some edge cases like reloads or switching servers cause the AFK time to be invalid
        // Compare against start of server
        if (person.afkSince < ManagementFactory.getRuntimeMXBean().startTime) return

        val millisecondsAFK = (System.currentTimeMillis() - person.afkSince)
        val secondsAFK = millisecondsAFK / 1000
        plugin.logger.info("${person.name} is no longer AFK after $secondsAFK seconds.")
        // An edge case exists where onlinetime is already 0 (player not joined before)
        // We let this fail
        connector?.onAfkChange(person.uuid.toString(), secondsAFK.toInt())
    }

    fun disable() {
        connector?.close()
    }
}
