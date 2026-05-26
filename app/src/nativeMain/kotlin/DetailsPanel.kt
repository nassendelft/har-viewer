import nl.ncaj.*

internal class DetailsPanel(
    private val appState: AppState,
    private val requestTab: RequestTab,
    private val responseHeadersTab: ResponseHeadersTab,
    private val bodyTab: BodyTab,
    private val timingsTab: TimingsTab,
) {
    private var lastKey = ""

    fun build(): Component {
        val tabContainer = tab(appState.tabSelected)
        tabContainer.add(requestTab.build())
        tabContainer.add(responseHeadersTab.build())
        tabContainer.add(bodyTab.build())
        tabContainer.add(timingsTab.build())

        val tabLabels = listOf("Request", "Resp Headers", "Body", "Diagnostics")
        return renderer(tabContainer) {
            val detailsFocused = appState.focusedPanel.value == 1
            vbox(
                renderTabBar(appState.tabSelected.value, appState.focusedPanel.value, tabLabels),
                separator(),
                tabContainer.render().flex(),
            ).flex().window(run {
                val labelColor = if (appState.focusedPanel.value == 1) Color.CyanLight else Color.GrayDark
                text(" details ").bold().color(labelColor)
            })
            .let { if (appState.focusedPanel.value != 1) it.color(Color.GrayDark) else it }
        }
    }

    fun handleEvent(event: FtxUIEvent): Boolean {
        val onBody = appState.tabSelected.value == 2
        val onReqHeaders = appState.tabSelected.value == 0
        val onRespHeaders = appState.tabSelected.value == 1
        val onTimings = appState.tabSelected.value == 3
        val contentHeight = Terminal.size().dimy - 6
        val prevKey = lastKey
        if (!event.isMouse) lastKey = event.input
        return when {
            onBody -> bodyTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            onReqHeaders -> requestTab.handleScrollEvent(event, prevKey, contentHeight)
            onRespHeaders -> responseHeadersTab.handleScrollEvent(event, prevKey, contentHeight)
            onTimings -> timingsTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            else -> false
        }
    }
}
