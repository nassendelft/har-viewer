@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("UNCHECKED_CAST")

package nl.ncaj

import ftxui_c.*
import kotlinx.cinterop.*

@DslMarker
annotation class FtxUIDsl

internal typealias ComponentHandle = ftxui_component_handle_t
internal typealias ElementHandle = ftxui_element_handle_t

class Component internal constructor(internal val handle: ComponentHandle)
class Element internal constructor(internal val handle: ElementHandle)

typealias Decorator = Element.() -> Element

@FtxUIDsl
class FtxUIBuilder internal constructor() {
    private var app: ftxui_app_handle_t? = null

    fun exit() {
        app?.let { ftxui_app_exit(it) }
    }

    internal fun build(root: Component) {
        val app = ftxui_app_create_fullscreen()!!
        this.app = app
        ftxui_app_loop(app, root.handle)
        ftxui_app_destroy(app)
    }
}

fun ftxui(content: FtxUIBuilder.() -> Component) {
    val builder = FtxUIBuilder()
    val component = builder.content()
    builder.build(component)
}

fun Element.border() =
    Element(ftxui_element_border(this.handle)!!)

fun Element.flex() =
    Element(ftxui_element_flex(this.handle)!!)

fun Element.color(color: ftxui_color_t) =
    Element(ftxui_element_color(this.handle, color)!!)

fun Element.bold() =
    Element(ftxui_element_bold(this.handle)!!)

fun Element.inverted() =
    Element(ftxui_element_inverted(this.handle)!!)

fun Element.underlined() =
    Element(ftxui_element_underlined(this.handle)!!)

fun Element.window(title: Element) =
    Element(ftxui_element_window(title.handle, this.handle)!!)

fun Component.destroy() =
    ftxui_component_destroy(handle)

fun text(text: String) = Element(ftxui_element_text(text)!!)

fun vbox(content: ElementContainerBuilder.() -> Unit): Element = memScoped {
    val elements = ElementContainerBuilder().apply(content).build()
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    Element(ftxui_element_vbox(array, elements.size)!!)
}

fun hbox(content: ElementContainerBuilder.() -> Unit): Element = memScoped {
    val elements = ElementContainerBuilder().apply(content).build()
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    Element(ftxui_element_hbox(array, elements.size)!!)
}

fun button(
    label: String,
    onClick: () -> Unit,
): Component {
    val stableRef = StableRef.create(onClick)
    val callback = staticCFunction { refPtr: COpaquePointer? ->
        refPtr?.asStableRef<() -> Unit>()?.get()?.invoke()
        Unit
    }
    return Component(ftxui_component_button(label, callback, stableRef.asCPointer())!!)
}

fun horizontal(block: ContainerBuilder.() -> Unit): Component {
    val container = ftxui_component_container_horizontal()!!
    val children = ContainerBuilder().apply(block).build()
    for (component in children) ftxui_container_add(container, component.handle)
    return Component(container)
}

fun vertical(block: ContainerBuilder.() -> Unit): Component {
    val container = ftxui_component_container_vertical()!!
    val children = ContainerBuilder().apply(block).build()
    for (component in children) ftxui_container_add(container, component.handle)
    return Component(container)
}

fun renderer(
    child: Component? = null,
    callback: () -> Element
): Component {
    val stableRef = StableRef.create(callback)
    val renderCallback = staticCFunction { refPtr: COpaquePointer? ->
        val block = refPtr!!.asStableRef<() -> Element>().get()
        block().handle
    } as ftxui_render_callback_t
    return Component(ftxui_component_renderer(child?.handle, renderCallback, stableRef.asCPointer())!!)
}

fun Component.render() =
    Element(ftxui_component_render(this.handle)!!)

@FtxUIDsl
class ElementContainerBuilder internal constructor() {
    private val elements = mutableListOf<Element>()

    fun text(value: String, decorator: Decorator = { this }) {
        elements.add(decorator(nl.ncaj.text(value)))
    }

    fun hbox(decorator: Decorator = { this }, content: ElementContainerBuilder.() -> Unit) {
        elements.add(decorator(hbox(content)))
    }

    fun vbox(decorator: Decorator = { this }, content: ElementContainerBuilder.() -> Unit) {
        elements.add(decorator(vbox(content)))
    }

    fun render(child: Component) = child.render().also { elements.add(it) }

    internal fun build() = elements
}

@FtxUIDsl
class ContainerBuilder internal constructor() {
    private val components = mutableListOf<Component>()

    fun button(
        label: String,
        onClick: () -> Unit,
    ) = nl.ncaj.button(label, onClick).also { components.add(it) }

    fun horizontal(content: ContainerBuilder.() -> Unit) =
        nl.ncaj.horizontal(content).also { components.add(it) }

    fun vertical(content: ContainerBuilder.() -> Unit) =
        nl.ncaj.vertical(content).also { components.add(it) }

    fun build() = components
}