package me.arcator.afklog

import com.zaxxer.hikari.HikariDataSource
import org.bukkit.scheduler.BukkitRunnable
import java.sql.SQLException
import java.util.logging.Logger

class DatabaseRunnable(
    private val logger: Logger, private val hikari: HikariDataSource,
    private val uuid: String, private val secondsAFK: Int
) : BukkitRunnable() {
    override fun run() {
        val sql = "UPDATE `lb-players` SET onlinetime = onlinetime - ? WHERE UUID = ?;"
        try {
            hikari.connection
                .use { conn ->
                    val stmt = conn.prepareStatement(sql)
                    // onlinetime is unsigned int and secondsAFK is signed
                    stmt.setInt(1, secondsAFK)
                    stmt.setString(2, uuid)
                    stmt.executeUpdate()
                }
            logger.info("Subtracted time.")
        } catch (e: SQLException) {
            logger.warning(e.stackTraceToString())
        }
    }
}