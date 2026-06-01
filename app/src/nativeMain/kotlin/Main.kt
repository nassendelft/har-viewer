import har.parseHar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.*
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val path = args.firstOrNull() ?: run { println("Usage: har-viewer <file.har>"); return }
    val har = try {
        parseHar(path)
    } catch (e: IllegalStateException) {
        println("Error: ${e.message}"); return
    }
    val entries = har.log.entries
    if (entries.isEmpty()) {
        println("No entries found in HAR file."); return
    }

    var appRef: FtxUIApp? = null
    val bgScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val appState = AppState(entries)
    val requestTab = RequestTab(appState)
    val responseHeadersTab = ResponseHeadersTab(appState)
    val bodyTab = BodyTab(appState, bgScope) { appRef?.requestAnimationFrame() }
    val timingsTab = TimingsTab(appState)
    val requestListPanel = RequestListPanel(appState)

    val requestPanel = requestListPanel.build()
    val detailsPanelCtrl = DetailsPanel(appState, requestTab, responseHeadersTab, bodyTab, timingsTab)
    val detailsPanel = detailsPanelCtrl.build()

    val panelRouter = tab(appState.focusedPanel)
    panelRouter.add(requestPanel)
    panelRouter.add(detailsPanel)

    val mainContainer = renderer(panelRouter) {
        hbox(
            requestPanel.render().size(WidthOrHeight.Width, Constraint.Equal, appState.leftSize.value),
            detailsPanel.render().flex(),
        )
    }.catchEvent { event ->
        val searchActive = requestListPanel.isSearchActive
        val onDetails = appState.focusedPanel.value == 1
        when {
            event.isKey("q") && !searchActive -> { appRef?.exit(); true }
            event.isKey("r") && !searchActive -> { appState.focusedPanel.value = 0; true }
            event.isKey("1") && !searchActive -> { appState.tabSelected.value = 0; appState.focusedPanel.value = 1; true }
            event.isKey("2") && !searchActive -> { appState.tabSelected.value = 1; appState.focusedPanel.value = 1; true }
            event.isKey("3") && !searchActive -> { appState.tabSelected.value = 2; appState.focusedPanel.value = 1; true }
            event.isKey("4") && !searchActive -> { appState.tabSelected.value = 3; appState.focusedPanel.value = 1; true }
            onDetails -> detailsPanelCtrl.handleEvent(event)
            else -> false
        }
    }

    val app = FtxUIApp.fullscreen().also { appRef = it }
    app.loop(mainContainer)

    bgScope.cancel()
    app.destroy()
    bodyTab.free()
    requestTab.free()
    responseHeadersTab.free()
    timingsTab.free()
    requestListPanel.free()
    appState.close()
}
