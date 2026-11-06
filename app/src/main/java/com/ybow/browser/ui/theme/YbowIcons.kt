package com.ybow.browser.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The interface icons, drawn from the same SVG paths as the design so the strokes
 * and joins match the mock exactly. Icons are tinted at use, like any Compose icon.
 */
object YbowIcons {

    private fun icon(name: String, build: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply(build).build()

    private fun ImageVector.Builder.stroke(d: String, width: Float = 2f) = addPath(
        pathData = PathParser().parsePathString(d).toNodes(),
        fill = null,
        stroke = SolidColor(Color.White),
        strokeLineWidth = width,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )

    private fun ImageVector.Builder.fill(d: String) = addPath(
        pathData = PathParser().parsePathString(d).toNodes(),
        fill = SolidColor(Color.White),
    )

    val Search: ImageVector = icon("search") {
        stroke("M4.5 11a6.5 6.5 0 1 0 13 0a6.5 6.5 0 1 0 -13 0")
        stroke("M16 16 20.5 20.5")
    }

    val Back: ImageVector = icon("back") {
        stroke("M19 12H5")
        stroke("M11 6l-6 6 6 6")
    }

    val Home: ImageVector = icon("home") {
        stroke("M4 11 12 4l8 7v8a1 1 0 0 1-1 1h-4v-6H9v6H5a1 1 0 0 1-1-1z")
    }

    val Dots: ImageVector = icon("dots") {
        fill("M3.3 12a1.7 1.7 0 1 0 3.4 0a1.7 1.7 0 1 0 -3.4 0")
        fill("M10.3 12a1.7 1.7 0 1 0 3.4 0a1.7 1.7 0 1 0 -3.4 0")
        fill("M17.3 12a1.7 1.7 0 1 0 3.4 0a1.7 1.7 0 1 0 -3.4 0")
    }

    val Plus: ImageVector = icon("plus") {
        stroke("M12 5v14")
        stroke("M5 12h14")
    }

    val Bookmark: ImageVector = icon("bookmark") {
        stroke("M7 4h10v16l-5-3.5L7 20z")
    }

    val EyeOff: ImageVector = icon("eyeoff") {
        stroke("M3 3l18 18")
        stroke("M10.6 6.2A9.6 9.6 0 0 1 12 6c5 0 8.5 4 9.5 6-.5 1-1.6 2.5-3.2 3.7")
        stroke("M6.4 7.7C4.4 9 3 11 2.5 12c1 2 4.5 6 9.5 6 1.5 0 2.8-.4 4-.9")
        stroke("M9.9 10A3 3 0 0 0 14 14.1")
    }

    val Arrow: ImageVector = icon("arrow") {
        stroke("M5 12h14")
        stroke("M13 6l6 6-6 6")
    }

    val Close: ImageVector = icon("close") {
        stroke("M6 6l12 12")
        stroke("M18 6 6 18")
    }

    val Reload: ImageVector = icon("reload") {
        stroke("M20 12a8 8 0 1 1-2.5-5.8")
        stroke("M20 4v5h-5")
    }

    val Lock: ImageVector = icon("lock") {
        stroke("M7 11h10a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2z")
        stroke("M8 11V8a4 4 0 0 1 8 0v3")
    }

    val Trash: ImageVector = icon("trash") {
        stroke("M5 7h14")
        stroke("M10 7V4h4v3")
        stroke("M7 7l1 13h8l1-13")
    }

    val Pencil: ImageVector = icon("pencil") {
        stroke("M4 20l1-4L16 5l3 3L8 19z")
    }

    val External: ImageVector = icon("external") {
        stroke("M14 4h6v6")
        stroke("M20 4l-9 9")
        stroke("M18 14v5H5V6h5")
    }

    val Clock: ImageVector = icon("clock") {
        stroke("M4 12a8 8 0 1 0 16 0a8 8 0 1 0 -16 0")
        stroke("M12 7v5l3 2")
    }

    val Download: ImageVector = icon("download") {
        stroke("M12 4v11")
        stroke("M8 11l4 4 4-4")
        stroke("M5 20h14")
    }

    val Sliders: ImageVector = icon("sliders") {
        stroke("M4 7h9")
        stroke("M17 7h3")
        stroke("M4 17h3")
        stroke("M11 17h9")
        stroke("M13 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0")
        stroke("M7 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0")
    }

    val Share: ImageVector = icon("share") {
        stroke("M4 12v7a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-7")
        stroke("M12 3v12")
        stroke("M8 7l4-4 4 4")
    }

    val Copy: ImageVector = icon("copy") {
        stroke("M8 8h11a1 1 0 0 1 1 1v11a1 1 0 0 1-1 1H8a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z")
        stroke("M5 16H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h11a1 1 0 0 1 1 1v1")
    }
}
