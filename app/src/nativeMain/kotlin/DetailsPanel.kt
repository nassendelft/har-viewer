import nl.ncaj.*

internal class DetailsPanel(
    private val appState: AppState,
    private val requestTab: RequestTab,
    private val responseHeadersTab: ResponseHeadersTab,
    private val bodyTab: BodyTab,
    private val timingsTab: TimingsTab,
) {
    private var lastKey = ""
    private var lastTabSelected = -1

    fun build(): Component {
        val tabContainer = tab(appState.tabSelected)
        tabContainer.add(requestTab.build())
        tabContainer.add(responseHeadersTab.build())
        tabContainer.add(bodyTab.build())
        tabContainer.add(timingsTab.build())

        val tabLabels = listOf("Request", "Resp Headers", "Body", "Diagnostics")
        return renderer(tabContainer) {
            val effectiveTab = appState.tabSelected.value
            if (lastTabSelected == 2 && effectiveTab != 2) bodyTab.deactivate()
            lastTabSelected = effectiveTab
            vbox(tabContainer.render().flex())
                .flex()
                .window(
                    renderTabBar(
                        appState.tabSelected.value,
                        appState.focusedPanel.value,
                        tabLabels
                    )
                )
                .let { if (appState.focusedPanel.value != 1) it.color(Color.GrayDark) else it }
        }
    }

    fun handleEvent(event: FtxUIEvent): Boolean {
        val contentHeight = Terminal.size().dimy - 4
        val prevKey = lastKey
        if (!event.isMouse) lastKey = event.input
        return when (appState.tabSelected.value) {
            0 -> requestTab.handleScrollEvent(event, prevKey, contentHeight)
            1 -> responseHeadersTab.handleScrollEvent(event, prevKey, contentHeight)
            2 -> bodyTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            3 -> timingsTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            else -> false
        }
    }
}
