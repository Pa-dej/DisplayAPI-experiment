package me.padej.displayAPI.render.shapes

import me.padej.displayAPI.DisplayAPI
import me.padej.displayAPI.render.HighlightStyle
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display.Brightness
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

object Highlight {
    private const val MIN_OFFSET = 1e-2f
    private const val COLOR_CHANGE_SPEED = 5 // Скорость изменения цвета
    private var currentStartColor: Color? = null
    private var currentEndColor: Color? = null
    private var currentColor: Color? = null

    @JvmField
    var blockPosDisplays: MutableMap<String, MutableList<TextDisplay>> = HashMap()

    private var t = 0.0f
    private var increasing = true // Направление интерполяции

    // Метод для установки цветов в зависимости от стиля
    private fun setHighlightStyle(style: HighlightStyle) {
        when (style) {
            HighlightStyle.SILVER -> {
                currentStartColor = Color.fromARGB(100, 127, 112, 138)
                currentEndColor = Color.fromARGB(100, 199, 220, 208)
            }

            HighlightStyle.GOLD -> {
                currentStartColor = Color.fromARGB(100, 232, 59, 59)
                currentEndColor = Color.fromARGB(100, 251, 107, 29)
            }

            HighlightStyle.BRONZE -> {
                currentStartColor = Color.fromARGB(100, 205, 104, 61)
                currentEndColor = Color.fromARGB(100, 230, 144, 78)
            }

            HighlightStyle.OLIVE -> {
                currentStartColor = Color.fromARGB(100, 162, 169, 71)
                currentEndColor = Color.fromARGB(100, 213, 224, 75)
            }

            HighlightStyle.EMERALD -> {
                currentStartColor = Color.fromARGB(100, 30, 188, 115)
                currentEndColor = Color.fromARGB(100, 145, 219, 105)
            }

            HighlightStyle.AQUA -> {
                currentStartColor = Color.fromARGB(100, 14, 175, 155)
                currentEndColor = Color.fromARGB(100, 48, 225, 185)
            }

            HighlightStyle.BLUE -> {
                currentStartColor = Color.fromARGB(100, 77, 101, 180)
                currentEndColor = Color.fromARGB(100, 77, 155, 230)
            }

            HighlightStyle.PURPLE -> {
                currentStartColor = Color.fromARGB(100, 144, 94, 169)
                currentEndColor = Color.fromARGB(100, 168, 132, 243)
            }

            HighlightStyle.RUBY -> {
                currentStartColor = Color.fromARGB(120, 206, 33, 36)
                currentEndColor = Color.fromARGB(120, 122, 15, 17)
            }

            HighlightStyle.PINK -> {
                currentStartColor = Color.fromARGB(100, 240, 79, 120)
                currentEndColor = Color.fromARGB(100, 246, 129, 129)
            }

            else -> {
                currentStartColor = Color.fromARGB(100, 130, 25, 40)
                currentEndColor = Color.fromARGB(100, 190, 20, 40)
            }
        }
    }

    // Метод для создания дисплеев только с той стороны, где сосед isAir
    @JvmStatic
    fun createSides(location: Location, style: HighlightStyle) {
        setHighlightStyle(style) // Устанавливаем цвета в зависимости от стиля
        addIfNotNull(southSide(location), location)
        addIfNotNull(eastSide(location), location)
        addIfNotNull(northSide(location), location)
        addIfNotNull(westSide(location), location)
        addIfNotNull(downSide(location), location)
        addIfNotNull(upSide(location), location)
    }

    private fun addIfNotNull(display: TextDisplay?, location: Location) {
        if (display != null) {
            val blockPosKey = location.blockX.toString() + "," + location.blockY + "," + location.blockZ
            blockPosDisplays.computeIfAbsent(blockPosKey) { k: String? -> ArrayList() }
                .add(display)
        }
    }

    // Метод для удаления дисплеев по BlockPos
    @JvmStatic
    fun removeSelectionOnBlockPos(x: Int, y: Int, z: Int) {
        val blockPosKey = "$x,$y,$z"
        val displaysToRemove: List<TextDisplay>? = blockPosDisplays.remove(blockPosKey)
        if (displaysToRemove != null) {
            for (display in displaysToRemove) {
                display.remove()
            }
        }
    }

    // Метод для удаления всех дисплеев
    @JvmStatic
    fun removeAllSelections() {
        for (displaysList in blockPosDisplays.values) {
            for (display in displaysList) {
                display.remove()
            }
        }
        blockPosDisplays.clear()
    }

    @JvmStatic
    fun startColorUpdateTask() {
        object : BukkitRunnable() {
            override fun run() {
                updateGradientColor()
                updateAllDisplaysColor()
            }
        }.runTaskTimer(DisplayAPI.getInstance(), 0L, 1L)
    }

    private fun updateGradientColor() {
        if (currentStartColor == null || currentEndColor == null) return
        currentColor = lerpColor(currentStartColor, currentEndColor, t)

        if (increasing) {
            t += COLOR_CHANGE_SPEED / 100.0f
            if (t >= 1.0f) {
                t = 1.0f
                increasing = false // Начинаем уменьшать
            }
        } else {
            t -= COLOR_CHANGE_SPEED / 100.0f
            if (t <= 0.0f) {
                t = 0.0f
                increasing = true // Начинаем увеличивать
            }
        }
    }

    private fun lerpColor(color1: Color?, color2: Color?, t: Float): Color {
        val a = (color1!!.alpha + t * (color2!!.alpha - color1.alpha)).toInt()
        val r = (color1.red + t * (color2.red - color1.red)).toInt()
        val g = (color1.green + t * (color2.green - color1.green)).toInt()
        val b = (color1.blue + t * (color2.blue - color1.blue)).toInt()

        return Color.fromARGB(a, r, g, b)
    }

    private fun updateAllDisplaysColor() {
        for (displaysList in blockPosDisplays.values) {
            for (display in displaysList) {
                if (display != null) {
                    display.backgroundColor = currentColor
                }
            }
        }
    }

    private fun southSide(location: Location): TextDisplay? {
        val southBlock = location.block.getRelative(BlockFace.SOUTH)
        if (southBlock.type == Material.AIR || southBlock.type == Material.SNOW_BLOCK || southBlock.type == Material.POWDER_SNOW) {
            val world = location.world
            val displayLoc = Location(
                world,
                location.blockX + 0.5,
                location.blockY.toDouble(),
                (location.blockZ + 1 + MIN_OFFSET).toDouble()
            )
            return createTextDisplay(displayLoc, 0f, 0f)
        }
        return null
    }

    private fun eastSide(location: Location): TextDisplay? {
        val eastBlock = location.block.getRelative(BlockFace.EAST)
        if (eastBlock.type == Material.AIR || eastBlock.type == Material.SNOW_BLOCK || eastBlock.type == Material.POWDER_SNOW) {
            val world = location.world
            val displayLoc = Location(
                world,
                (location.blockX + 1 + MIN_OFFSET).toDouble(),
                location.blockY.toDouble(),
                location.blockZ + 0.5
            )
            return createTextDisplay(displayLoc, -90f, 0f)
        }
        return null
    }

    private fun northSide(location: Location): TextDisplay? {
        val northBlock = location.block.getRelative(BlockFace.NORTH)
        if (northBlock.type == Material.AIR || northBlock.type == Material.SNOW_BLOCK || northBlock.type == Material.POWDER_SNOW) {
            val world = location.world
            val displayLoc = Location(
                world,
                location.blockX + 1 - 0.5,
                location.blockY.toDouble(),
                (location.blockZ - MIN_OFFSET).toDouble()
            )
            return createTextDisplay(displayLoc, -180f, 0f)
        }
        return null
    }

    private fun westSide(location: Location): TextDisplay? {
        val westBlock = location.block.getRelative(BlockFace.WEST)
        if (westBlock.type == Material.AIR) {
            val world = location.world
            val displayLoc = Location(
                world,
                (location.blockX - MIN_OFFSET).toDouble(),
                location.blockY.toDouble(),
                location.blockZ + 0.5
            )
            return createTextDisplay(displayLoc, 90f, 0f)
        }
        return null
    }

    private fun downSide(location: Location): TextDisplay? {
        val downBlock = location.block.getRelative(BlockFace.DOWN)
        if (downBlock.type == Material.AIR) {
            val world = location.world
            val displayLoc = Location(
                world,
                location.blockX + 0.5,
                (location.blockY - MIN_OFFSET).toDouble(),
                location.blockZ.toDouble()
            )
            return createTextDisplay(displayLoc, 0f, 90f)
        }
        return null
    }

    private fun upSide(location: Location): TextDisplay? {
        val upBlock = location.block.getRelative(BlockFace.UP)
        if (upBlock.type == Material.AIR) {
            val world = location.world
            val displayLoc = Location(
                world,
                location.blockX + 0.5,
                (location.blockY + 1 + MIN_OFFSET).toDouble(),
                (location.blockZ + 1).toDouble()
            )
            return createTextDisplay(displayLoc, 0f, -90f)
        }
        return null
    }

    private fun createTextDisplay(displayLoc: Location, rotationX: Float, rotationY: Float): TextDisplay {
        val world = displayLoc.world
        val textDisplay = world.spawnEntity(displayLoc, EntityType.TEXT_DISPLAY) as TextDisplay
        textDisplay.setRotation(rotationX, rotationY)
        textDisplay.interpolationDuration = 1
        textDisplay.transformation = Transformation(
            Vector3f(-0.1f, 0f, 0f),
            AxisAngle4f(),
            Vector3f(8f, 4f, 1f),
            AxisAngle4f()
        )
        textDisplay.text = " "
        textDisplay.backgroundColor = currentColor
        textDisplay.brightness = Brightness(15, 15)
        return textDisplay
    }
}
