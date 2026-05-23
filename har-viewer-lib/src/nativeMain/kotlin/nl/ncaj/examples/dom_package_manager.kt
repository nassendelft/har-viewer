package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomPackageManager() {
    data class Task(
        val name: String,
        var numberOfThreads: Int,
        var downloaded: Int,
        val size: Int,
    )

    val remainingTasks = mutableListOf(
        Task("contact server       ", 10, 0, 6 * 25),
        Task("download index.html  ", 10, 0, 9 * 25),
        Task("download script.js   ", 1, 0, 3 * 25),
        Task("download style.js    ", 1, 0, 4 * 25),
        Task("download image.png   ", 1, 0, 5 * 25),
        Task("download big_1.png   ", 1, 0, 30 * 25),
        Task("download icon_1.png  ", 1, 0, 7 * 25),
        Task("download icon_2.png  ", 1, 0, 8 * 25),
        Task("download big_2.png   ", 1, 0, 30 * 25),
        Task("download small_1.png ", 1, 0, 10 * 25),
        Task("download small_2.png ", 1, 0, 11 * 25),
        Task("download small_3.png ", 1, 0, 12 * 25),
    )

    val displayedTasks = mutableListOf<Task>()
    var remainingThreads = 12
    var nbQueued = remainingTasks.size
    var nbActive = 0
    var nbDone = 0

    fun toText(n: Int) = text(n.toString()).size(WidthOrHeight.Width, Constraint.Equal, 3)

    fun renderTask(task: Task): Element {
        val style: (Element) -> Element = if (task.downloaded == task.size) Element::dim else Element::bold
        return hbox(
            style(text(task.name)),
            separator(),
            toText(task.downloaded),
            text("/"),
            toText(task.size),
            separator(),
            gauge(task.downloaded.toFloat() / task.size),
        )
    }

    fun renderSummary(): Element {
        val summary = vbox(
            hbox(text("- done:   "), toText(nbDone).bold()).color(Color.Green),
            hbox(text("- active: "), toText(nbActive).bold()).color(Color.RedLight),
            hbox(text("- queue:  "), toText(nbQueued).bold()).color(Color.Red),
        )
        return summary.window(text(" Summary "))
    }

    fun updateModel() {
        for (task in displayedTasks) {
            if (task.downloaded != task.size) {
                task.downloaded++
            } else if (task.numberOfThreads > 0) {
                remainingThreads += task.numberOfThreads
                task.numberOfThreads = 0
                nbActive--
                nbDone++
            }
        }
        if (remainingTasks.isNotEmpty() && remainingTasks.first().numberOfThreads <= remainingThreads) {
            val next = remainingTasks.removeFirst()
            remainingThreads -= next.numberOfThreads
            displayedTasks.add(next)
            nbQueued--
            nbActive++
        }
    }

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        updateModel()
        val taskEntries = displayedTasks.map { renderTask(it) }
        val doc = vbox(
            vbox(*taskEntries.toTypedArray()).window(text(" Task ")),
            hbox(renderSummary(), filler()),
        )
        if (nbActive + nbQueued == 0) app.exit()
        else app.requestAnimationFrame()
        doc
    }

    app.loop(component)
    app.destroy()
    component.destroy()
}