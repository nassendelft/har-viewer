import nl.ncaj.*

internal class DetailsPanel(
    private val appState: AppState,
    private val requestTab: RequestTab,
    private val responseHeadersTab: ResponseHeadersTab,
    private val bodyTab: BodyTab,
    private val timingsTab: TimingsTab,
    private val imageTab: ImageTab,
) {
    private var lastKey = ""
    private var lastTabSelected = -1

    fun build(): Component {
        val tabContainer = tab(appState.tabSelected)
        tabContainer.add(requestTab.build())
        tabContainer.add(responseHeadersTab.build())
        tabContainer.add(bodyTab.build())
        tabContainer.add(timingsTab.build())
        tabContainer.add(imageTab.build())

        return renderer(tabContainer) {
            val entry = appState.entries[appState.selectedEntry.value]
            val hasImage = isImageContent(entry.response.content, entry.request.url)
            val tabLabels = buildList {
                add("Request"); add("Resp Headers"); add("Body"); add("Diagnostics")
                if (hasImage) add("Image")
            }
            val currentTab = appState.tabSelected.value
            if (!hasImage && currentTab == 4) appState.tabSelected.value = 0
            if (lastTabSelected == 4 && currentTab != 4) imageTab.deactivate()
            lastTabSelected = appState.tabSelected.value
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
        val onImage = appState.tabSelected.value == 4
        val contentHeight = Terminal.size().dimy - 6
        val prevKey = lastKey
        if (!event.isMouse) lastKey = event.input
        return when {
            onBody -> bodyTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            onReqHeaders -> requestTab.handleScrollEvent(event, prevKey, contentHeight)
            onRespHeaders -> responseHeadersTab.handleScrollEvent(event, prevKey, contentHeight)
            onTimings -> timingsTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            onImage -> imageTab.handleScrollEvent(event, prevKey, contentHeight - 2)
            else -> false
        }
    }
}
