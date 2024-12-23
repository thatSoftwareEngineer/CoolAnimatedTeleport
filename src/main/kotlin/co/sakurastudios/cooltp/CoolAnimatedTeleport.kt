package co.sakurastudios.cooltp

import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class CoolAnimatedTeleport : JavaPlugin() {

    companion object {
        lateinit var instance: CoolAnimatedTeleport
    }

    override fun onEnable() {
        instance = this
        getCommand("spawnportal")?.setExecutor(PortalCommand())
    }

    override fun onDisable() {

    }




}
