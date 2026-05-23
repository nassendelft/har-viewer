package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun examplePrintKeyPress() {
    val events = mutableListOf<FtxUIEvent>()

    fun code(event: FtxUIEvent): String {
        val codes = StringBuilder()
        for (c in event.input) {
            codes.append(" ")
            codes.append(c.code.toUInt())
        }
        return codes.toString()
    }

    val leftColumn = renderer {
        val children = mutableListOf<Element>()
        children.add(text("Codes"))
        children.add(separator())
        val start = maxOf(0, events.size - 20)
        for (i in start until events.size) {
            children.add(text(code(events[i])))
        }
        vbox(*children.toTypedArray())
    }

    val rightColumn = renderer {
        val children = mutableListOf<Element>()
        children.add(text("Event"))
        children.add(separator())
        val start = maxOf(0, events.size - 20)
        for (i in start until events.size) {
            children.add(text(events[i].debugString))
        }
        vbox(*children.toTypedArray())
    }

    val splitSize = IntState(40)
    var component = resizableSplitLeft(leftColumn, rightColumn, splitSize)
    component = component.border()
    component = component.catchEvent { event ->
        events.add(event)
        false
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(component)
}