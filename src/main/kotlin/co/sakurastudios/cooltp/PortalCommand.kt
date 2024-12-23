package co.sakurastudios.cooltp

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class PortalCommand : CommandExecutor {

    private val activePortals = mutableListOf<Pair<Location, Location>>() // Store portal and destination pairs

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return false

        val startLocation = sender.location
        var destinationLocation = sender.location.add(100.0, 0.0, 20.0)

        // Add the portal to the active list
        activePortals.add(Pair(startLocation, destinationLocation))



        //Create Destination From Args
        if (args.isNotEmpty()) {
            val world = startLocation.world
            val x = args[0].toDoubleOrNull() ?: 0.0
            val y = args[1].toDoubleOrNull() ?: 0.0
            val z = args[2].toDoubleOrNull() ?: 0.0

            destinationLocation = Location(world, x, y, z)

            Bukkit.getLogger().info("Destination Set To: ${destinationLocation.x} ${destinationLocation.y} ${destinationLocation.z}")
        }


        // Spawn the portal
        spawnPortal(startLocation, destinationLocation)
        Bukkit.getLogger().info("Destination Of Portal Is Been Set To: ${destinationLocation.x} ${destinationLocation.y} ${destinationLocation.z}")

        return true
    }

    fun spawnPortal(location: Location, destination: Location) {
        // Continuously spawn particles around the portal
        object : BukkitRunnable() {
            var angle = 0.0 // To track the angle for the spiral

            override fun run() {
                for (portal in activePortals) {
                    val portalLocation = portal.first
                    for (height in 0..20 step 1) {
                        val y = height / 10.0
                        val x = Math.cos(angle + y) * 0.5
                        val z = Math.sin(angle + y) * 0.5
                        val particleLoc = portalLocation.clone().add(x, y, z)

                        // Calculate RGB color based on the angle
                        val red = Math.abs(Math.sin(angle)).toFloat()
                        val green = Math.abs(Math.sin(angle + 2)).toFloat()
                        val blue = Math.abs(Math.sin(angle + 4)).toFloat()

                        // Create the particle dust options
                        val dustOptions = Particle.DustOptions(Color.fromRGB((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt()), 1f)

                        // Spawn the particle
                        portalLocation.world?.spawnParticle(Particle.DUST, particleLoc, 1, dustOptions)
                    }
                }

                angle += Math.PI / 16 // Increase angle to create the spiral effect
            }
        }.runTaskTimer(CoolAnimatedTeleport.instance, 0, 2) // Schedule task with a small delay for smooth animation

        // Continuously check for players entering any portal
        object : BukkitRunnable() {
            override fun run() {
                for (portal in activePortals) {
                    val portalLocation = portal.first
                    val destinationLocation = portal.second

                    portalLocation.world?.players?.forEach { player ->
                        if (player.location.distance(portalLocation) <= 1.5) { // If the player is within 1.5 blocks of the portal
                            Camera.MoveCameraTask(player, destinationLocation).runTaskTimer(CoolAnimatedTeleport.instance, 0, 1) // Teleport the player
                            Bukkit.getLogger().info("Player Teleported To: ${destinationLocation.x} ${destinationLocation.y} ${destinationLocation.z}")
                            player.sendMessage("You have been teleported!")
                        }
                    }
                }
            }
        }.runTaskTimer(CoolAnimatedTeleport.instance, 0, 5) // Check every 5 ticks
    }
}
