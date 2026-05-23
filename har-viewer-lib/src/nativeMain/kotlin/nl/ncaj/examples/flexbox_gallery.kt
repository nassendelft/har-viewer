package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleFlexboxGallery() {
    val app = FtxUIApp.fullscreen()

    val directionSelected = IntState(0)
    val wrapSelected = IntState(0)
    val justifySelected = IntState(0)
    val alignItemsSelected = IntState(0)
    val alignContentSelected = IntState(0)

    val directions = listOf("Row", "RowInversed", "Column", "ColumnInversed")
    val wraps = listOf("NoWrap", "Wrap", "WrapInversed")
    val justifyContent = listOf("FlexStart", "FlexEnd", "Center", "Stretch", "SpaceBetween", "SpaceAround", "SpaceEvenly")
    val alignItems = listOf("FlexStart", "FlexEnd", "Center", "Stretch")
    val alignContent = listOf("FlexStart", "FlexEnd", "Center", "Stretch", "SpaceBetween", "SpaceAround", "SpaceEvenly")

    val rbDirection = radiobox(directions, directionSelected)
    val rbWrap = radiobox(wraps, wrapSelected)
    val rbJustify = radiobox(justifyContent, justifySelected)
    val rbAlignItems = radiobox(alignItems, alignItemsSelected)
    val rbAlignContent = radiobox(alignContent, alignContentSelected)

    fun makeBox(dimX: Int, dimY: Int, index: Int): Element {
        val title = "${dimX}x${dimY}"
        val color = Color.hsv((index * 25).toUByte(), 255u, 255u)
        val el = vbox(
            text(title).hcenter().bold(),
            text(index.toString()).hcenter().dim(),
        ).size(WidthOrHeight.Width, Constraint.Equal, dimX)
            .size(WidthOrHeight.Height, Constraint.Equal, dimY)
            .bgcolor(color)
            .color(Color.Black)
            .border()
        color.destroy()
        return el
    }

    val controlsLayout = vertical(
        horizontal(rbDirection, rbWrap),
        horizontal(rbJustify, rbAlignItems, rbAlignContent),
    )

    val contentRenderer = renderer {
        val config = FlexboxConfig(
            direction = FlexboxDirection.entries[directionSelected.value],
            wrap = FlexboxWrap.entries[wrapSelected.value],
            justifyContent = FlexboxJustify.entries[justifySelected.value],
            alignItems = FlexboxAlignItems.entries[alignItemsSelected.value],
            alignContent = FlexboxAlignContent.entries[alignContentSelected.value],
        )
        flexbox(
            makeBox(8, 4, 0), makeBox(9, 6, 1), makeBox(11, 6, 2),
            makeBox(10, 4, 3), makeBox(13, 7, 4), makeBox(12, 4, 5),
            makeBox(12, 5, 6), makeBox(10, 4, 7), makeBox(12, 4, 8),
            makeBox(10, 5, 9),
            config = config,
        ).flex().border()
    }

    val layout = vertical(controlsLayout, contentRenderer)

    val component = renderer(layout) {
        vbox(
            hbox(
                rbDirection.render().window(text("Direction")),
                rbWrap.render().window(text("Wrap")),
            ),
            hbox(
                rbJustify.render().window(text("JustifyContent")),
                rbAlignItems.render().window(text("AlignItems")),
                rbAlignContent.render().window(text("AlignContent")),
            ),
            contentRenderer.render().flex(),
        )
    }

    app.loop(component)
    app.destroy()
    component.destroy()
    directionSelected.free()
    wrapSelected.free()
    justifySelected.free()
    alignItemsSelected.free()
    alignContentSelected.free()
}