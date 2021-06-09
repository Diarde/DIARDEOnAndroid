package com.studiocinqo.diardeonandroid.enginecanvas.floorplanbuilder

import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.engine3d.Vector2D
import com.studiocinqo.diardeonandroid.engine3d.Vector2F
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine
import com.studiocinqo.diardeonandroid.enginecanvas.primitives.Line
import com.studiocinqo.diardeonandroid.enginecanvas.primitives.Polygon
import com.studiocinqo.diardeonandroid.enginecanvas.primitives.Text

class Floorplanbuilder(
    private val floorplan: IModel.IData.IFloorplan,
    private val engine: CanvasEngine
) {
    val minX: Float
    val minY: Float
    val maxX: Float
    val maxY: Float

    val vertices: HashMap<String, IModel.IData.IFloorplan.IPoint2D> =
        hashMapOf<String, IModel.IData.IFloorplan.IPoint2D>().apply {
            floorplan.vertices.forEach { vertex ->
                put(vertex.id, vertex.point)
            }
        }

    val displacements = hashMapOf<String, IModel.IData.IFloorplan.IPoint2D>().apply {
        floorplan.displacements.forEach { displacement ->
            put(displacement.id, displacement.point)
        }
    }

    init {

        getMaxs().let {
            minX = it.minX
            minY = it.minY
            maxX = it.maxX
            maxY = it.maxY
        }

        makeWall()
        makeDimensions()

    }


    private fun makeDimensions() {
        floorplan.walls.forEach { wall ->

            wall.wall.let { wall ->
                makeLines(wall, 0.5f)

            }
            wall.sections.forEach { section -> makeLines(section, 1f) }
            wall.windows.forEach { window -> makeLines(window, 1f) }
            wall.doors.forEach { door -> makeLines(door, 1f) }
        }
    }

    private fun makeWall() {

        floorplan.walls.forEach { wall ->
            wall.sections.forEach { section ->
                drawSection(section.id1, section.id2)
            }
            wall.windows.forEach { window ->
                drawWindow(window.id1, window.id2)
            }
            wall.doors.forEach { door ->
                drawDoor(door.id1, door.id2)
            }
        }

    }

    private fun drawSection(id1: String, id2: String) {

        vertices.get(id1)?.let { p1 ->
            vertices.get(id2)?.let { p2 ->
                displacements.get(id1)?.let { displacement1 ->
                    displacements.get(id2)?.let { displacement2 ->
                        val xs = floatArrayOf(
                            100 * p1.x.toFloat(),
                            100 * p2.x.toFloat(),
                            100 * (p2.x + displacement2.x).toFloat(),
                            100 * (p1.x + displacement1.x).toFloat()
                        )
                        val ys = floatArrayOf(
                            100 * p1.y.toFloat(),
                            100 * p2.y.toFloat(),
                            100 * (p2.y + displacement2.y).toFloat(),
                            100 * (p1.y + displacement1.y).toFloat()
                        )
                        Polygon(xs, ys, "#FF4500")?.apply { bind(engine) }
                    }
                }
            }
        }
    }

    private fun drawWindow(id1: String, id2: String) {

        vertices.get(id1)?.let { p1 ->
            vertices.get(id2)?.let { p2 ->
                displacements.get(id1)?.let { displacement1 ->
                    displacements.get(id2)?.let { displacement2 ->
                        val xs = floatArrayOf(
                            100 * p1.x.toFloat(),
                            100 * p2.x.toFloat(),
                            100 * (p2.x + displacement2.x).toFloat(),
                            100 * (p1.x + displacement1.x).toFloat()
                        )
                        val ys = floatArrayOf(
                            100 * p1.y.toFloat(),
                            100 * p2.y.toFloat(),
                            100 * (p2.y + displacement2.y).toFloat(),
                            100 * (p1.y + displacement1.y).toFloat()
                        )
                        Polygon(xs, ys, "#555555")?.apply { bind(engine) }
                    }
                }
            }
        }
    }

    private fun drawDoor(id1: String, id2: String) {

        vertices.get(id1)?.let { p1 ->
            vertices.get(id2)?.let { p2 ->
                displacements.get(id1)?.let { displacement1 ->
                    displacements.get(id2)?.let { displacement2 ->
                        val xs = floatArrayOf(
                            100 * p1.x.toFloat(),
                            100 * p2.x.toFloat(),
                            100 * (p2.x + displacement2.x).toFloat(),
                            100 * (p1.x + displacement1.x).toFloat()
                        )
                        val ys = floatArrayOf(
                            100 * p1.y.toFloat(),
                            100 * p2.y.toFloat(),
                            100 * (p2.y + displacement2.y).toFloat(),
                            100 * (p1.y + displacement1.y).toFloat()
                        )
                        Polygon(xs, ys, "#555555")?.apply { bind(engine) }
                    }
                }
            }
        }
    }

    private fun drawLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        offset: Float,
        normal: Vector2F
    ) {

        normal.normalise().let{normal ->
            val _x1 = 100f * (x1 + normal.X * offset)
            val _y1 = 100f * (y1 + normal.Y * offset)
            val _x2 = 100f * (x2 + normal.X * offset)
            val _y2 = 100f * (y2 + normal.Y * offset)

            Line(_x1, _y1, _x2, _y2, "#FF4500")?.apply { bind(engine) }
        }

    }

    fun makeLines(wall: IModel.IData.IFloorplan.IWall.IIDPair, offset: Float) {

        fun getLength(p1: IModel.IData.IFloorplan.IPoint2D, p2: IModel.IData.IFloorplan.IPoint2D): Double{
            return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y))
        }

        vertices.get(wall.id1)?.let { p1 ->
            vertices.get(wall.id2)?.let { p2 ->
                val orientation = getOrientation(wall)
                when (orientation) {
                    Orientation.EAST -> {
                        drawLineWithWinglets(
                            (minX - offset), p1.y.toFloat(),
                            (minX - offset), p2.y.toFloat(), "#FF4500")
                        printText(String.format("%.2f", getLength(p1, p2)), (minX - offset-0.05f), (p1.y+p2.y).toFloat()/2f,
                            -90f, "#FF4500" )
                    }
                    Orientation.WEST -> {
                        drawLineWithWinglets(
                            maxX + offset,  p1.y.toFloat(),
                            maxX + offset,  p2.y.toFloat(), "#FF4500"
                        )
                        printText(String.format("%.2f", getLength(p1, p2)), (maxX + offset + 0.05f), (p1.y+p2.y).toFloat()/2f,
                            90f, "#FF4500" )
                    }
                    Orientation.NORTH ->{
                        drawLineWithWinglets(
                            p1.x.toFloat(), (minY - offset),
                            p2.x.toFloat(), (minY - offset), "#FF4500"
                        )
                        printText(String.format("%.2f", getLength(p1, p2)), (p1.x+p2.x).toFloat()/2f, (minY -offset - 0.05f),
                            0f, "#FF4500" )
                    }
                    Orientation.SOUTH ->{
                        drawLineWithWinglets(
                             p1.x.toFloat(), (maxY + offset),
                             (p2.x.toFloat()), (maxY + offset), "#FF4500"
                        )
                    printText(String.format("%.2f", getLength(p1, p2)), (p1.x+p2.x).toFloat()/2f, (maxY + offset + 0.05f),
                        -180f, "#FF4500" )}
                    null -> null
                }
            }
        }

    }

    private fun drawLineWithWinglets(x1: Float, y1: Float, x2: Float, y2: Float, color: String){
        val normal: Vector2F =  Vector2F(y2-y1, x1-x2).normalise().scale(0.08f)


        Line(100f * x1, 100f * y1, 100f * x2, 100f * y2, "#FF4500").apply {
            bind(engine) }
        Line(100f*(x1 + normal.X), 100f*(y1 + normal.Y), 100f*(x1 - normal.X), 100f*(y1 - normal.Y),
            "#FF4500"
        ).apply {
            bind(engine)
        }
        Line(100f*(x2 + normal.X), 100f*(y2 + normal.Y), 100f*(x2 - normal.X), 100f*(y2 - normal.Y),
            "#FF4500"
        ).apply {
            bind(engine)
        }
    }

    private fun printText(text: String, x: Float, y: Float, rotation: Float, color: String){

        Text(text, 100f*x, 100f*y, rotation, color).apply { bind(engine) }

    }

    private fun getMaxs(): IMax {
        return (floorplan.walls.mapNotNull { wall ->
            vertices.get(wall.wall.id1)?.let { p1 ->
                vertices.get(wall.wall.id2)?.let { p2 ->
                    Pair<IModel.IData.IFloorplan.IPoint2D, IModel.IData.IFloorplan.IPoint2D>(p1, p2)
                }
            }
        }).run {
            val maxX = maxOf { x -> Math.max(x.first.x, x.second.x) }.toFloat()
            val maxY = maxOf { x -> Math.max(x.first.y, x.second.y) }.toFloat()
            val minX = minOf { x -> Math.min(x.first.x, x.second.x) }.toFloat()
            val minY = minOf { x -> Math.min(x.first.y, x.second.y) }.toFloat()
            IMax(maxX, maxY, minX, minY)
        }
    }


    private fun getOrientation(wall: IModel.IData.IFloorplan.IWall.IIDPair): Orientation? {
        val limit = Math.cos(Math.toRadians(5.0)).toFloat()
        return vertices.get(wall.id1)?.let { p1 ->
            vertices.get(wall.id2)?.let { p2 ->
                displacements.get(wall.id1)?.let { displacement ->
                    val v1 = Vector2D((p2.y - p1.y), (p1.x - p2.x))
                    val disp = Vector2D(displacement.x, displacement.y)
                    v1.scale(v1.dot(disp)).normalise().let{v1 ->
                        for (v in Orientation.values()) {
                            if (v1.dot(v.pointer) > limit) {
                                return v
                            }
                        }
                    }
                    return null
                }
            }
        }
    }

    class IMax(val maxX: Float, val maxY: Float, val minX: Float, val minY: Float)

    enum class Orientation(val pointer: Vector2D) {
        NORTH(Vector2D(0.0, -1.0)),
        SOUTH(Vector2D(0.0, 1.0)),
        EAST(Vector2D(-1.0, 0.0)),
        WEST(Vector2D(1.0, 0.0));
    }
}