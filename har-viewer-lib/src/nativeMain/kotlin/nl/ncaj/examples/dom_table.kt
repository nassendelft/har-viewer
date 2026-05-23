package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomTable() {
    val rows = listOf(
        listOf("Version", "Marketing name", "Release date", "API level", "Runtime"),
        listOf("2.3", "Gingerbread", "February 9 2011", "10", "Dalvik 1.4.0"),
        listOf("4.0", "Ice Cream Sandwich", "October 19 2011", "15", "Dalvik"),
        listOf("4.1", "Jelly Bean", "July 9 2012", "16", "Dalvik"),
        listOf("4.2", "Jelly Bean", "November 13 2012", "17", "Dalvik"),
        listOf("4.3", "Jelly Bean", "July 24 2013", "18", "Dalvik"),
        listOf("4.4", "KitKat", "October 31 2013", "19", "Dalvik and ART"),
        listOf("5.0", "Lollipop", "November 3 2014", "21", "ART"),
        listOf("5.1", "Lollipop", "March 9 2015", "22", "ART"),
        listOf("6.0", "Marshmallow", "October 5 2015", "23", "ART"),
        listOf("7.0", "Nougat", "August 22 2016", "24", "ART"),
        listOf("7.1", "Nougat", "October 4 2016", "25", "ART"),
        listOf("8.0", "Oreo", "August 21 2017", "26", "ART"),
        listOf("8.1", "Oreo", "December 5 2017", "27", "ART"),
        listOf("9", "Pie", "August 6 2018", "28", "ART"),
        listOf("10", "10", "September 3 2019", "29", "ART"),
        listOf("11", "11", "September 8 2020", "30", "ART"),
    )

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        val table = Table(rows)

        table.selectAll().border()
        table.selectColumn(0).border()
        table.selectRow(0).decorateBold()
        table.selectRow(0).separatorVertical()
        table.selectRow(0).border(BorderStyle.Double)
        table.selectColumn(2).decorateCellsAlignRight()

        val content = table.selectRows(1, -1)
        content.decorateCellsColorAlternateRow(Color.Blue, 3, 0)
        content.decorateCellsColorAlternateRow(Color.Cyan, 3, 1)
        content.decorateCellsColorAlternateRow(Color.White, 3, 2)
        content.destroy()

        table.selectCell(3, 4).borderColor(BorderStyle.Light, Color.Red)
        table.selectCell(2, 7).borderColor(BorderStyle.Light, Color.Red)

        val element = table.render()
        table.destroy()
        element
    }

    app.loop(component)
    app.destroy()
    component.destroy()
}
