package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleComposition() {
    var leftCount = 0
    var rightCount = 0

    val leftButtons = horizontal(
        button("Decrease", onClick = { leftCount-- }),
        button("Increase", onClick = { leftCount++ }),
    )

    val rightButtons = horizontal(
        button("Decrease", onClick = { rightCount-- }),
        button("Increase", onClick = { rightCount++ }),
    )

    val leftPane = renderer(leftButtons) {
        vbox(
            text("This is the left control"),
            separator(),
            text("Left button count: $leftCount"),
            leftButtons.render(),
        ).border()
    }

    val rightPane = renderer(rightButtons) {
        vbox(
            text("This is the right control"),
            separator(),
            text("Right button count: $rightCount"),
            rightButtons.render(),
        ).border()
    }

    val composition = horizontal(leftPane, rightPane)

    val screen = FtxUIApp.fitComponent()
    screen.loop(composition)
}