@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("UNCHECKED_CAST")

package nl.ncaj

import ftxui_c.*
import kotlinx.cinterop.*

internal typealias ComponentHandle = ftxui_component_handle_t
internal typealias ElementHandle = ftxui_element_handle_t

class Component internal constructor(internal val handle: ComponentHandle)
class Element internal constructor(internal val handle: ElementHandle)

typealias Decorator = Element.() -> Element

class FtxUIApp internal constructor(internal val handle: ftxui_app_handle_t) {
    fun loop(root: Component) {
        ftxui_app_loop(handle, root.handle)
    }

    fun exit() {
        ftxui_app_exit(handle)
    }

    fun destroy() {
        ftxui_app_destroy(handle)
    }

    companion object {
        fun fullscreen() = FtxUIApp(ftxui_app_create_fullscreen()!!)
        fun fitComponent() = FtxUIApp(ftxui_app_create_fit_component()!!)
    }
}

// -- Decorators

fun Element.border() =
    Element(ftxui_element_border(this.handle)!!)

fun Element.borderLight() =
    Element(ftxui_element_border_light(this.handle)!!)

fun Element.borderDashed() =
    Element(ftxui_element_border_dashed(this.handle)!!)

fun Element.borderHeavy() =
    Element(ftxui_element_border_heavy(this.handle)!!)

fun Element.borderDouble() =
    Element(ftxui_element_border_double(this.handle)!!)

fun Element.borderRounded() =
    Element(ftxui_element_border_rounded(this.handle)!!)

fun Element.borderEmpty() =
    Element(ftxui_element_border_empty(this.handle)!!)

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

// -- Elements

fun text(
    text: String,
    decorator: Decorator = {this}
) = Element(ftxui_element_text(text)!!).let(decorator)

fun separator() = Element(ftxui_element_separator()!!)
fun separatorLight() = Element(ftxui_element_separator_light()!!)
fun separatorDashed() = Element(ftxui_element_separator_dashed()!!)
fun separatorHeavy() = Element(ftxui_element_separator_heavy()!!)
fun separatorDouble() = Element(ftxui_element_separator_double()!!)
fun separatorEmpty() = Element(ftxui_element_separator_empty()!!)
fun separatorStyled(style: ftxui_border_style_t) = Element(ftxui_element_separator_styled(style)!!)
fun separatorCharacter(character: String) = Element(ftxui_element_separator_character(character)!!)
fun separatorHSelector(
    left: Float,
    right: Float,
    unselectedColor: ftxui_color_t = ftxui_color_t.FTXUI_COLOR_DEFAULT,
    selectedColor: ftxui_color_t = ftxui_color_t.FTXUI_COLOR_DEFAULT
) = Element(ftxui_element_separator_hselector(left, right, unselectedColor, selectedColor)!!)

fun separatorVSelector(
    up: Float,
    down: Float,
    unselectedColor: ftxui_color_t = ftxui_color_t.FTXUI_COLOR_DEFAULT,
    selectedColor: ftxui_color_t = ftxui_color_t.FTXUI_COLOR_DEFAULT
) = Element(ftxui_element_separator_vselector(up, down, unselectedColor, selectedColor)!!)

fun vbox(
    vararg elements: Element,
     decorator: Decorator = {this})
: Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    val element = Element(ftxui_element_vbox(array, elements.size)!!)
    decorator(element)
}

fun hbox(
    vararg elements: Element,
    decorator: Decorator = {this}
): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    val element = Element(ftxui_element_hbox(array, elements.size)!!)
    decorator(element)
}

// -- Util Elements

fun Element.hcenter() =
    Element(ftxui_element_hcenter(this.handle)!!)

fun Element.vcenter() =
    Element(ftxui_element_vcenter(this.handle)!!)

fun Element.center() =
    Element(ftxui_element_center(this.handle)!!)

fun Element.alignRight() =
    Element(ftxui_element_align_right(this.handle)!!)

fun Element.nothing() =
    Element(ftxui_element_nothing(this.handle)!!)

// -- Components

data class EntryState(
    val label: String,
    val state: Boolean,
    val active: Boolean,
    val focused: Boolean,
    val index: Int
)

class ButtonOption private constructor(
    internal val style: Style = Style.Simple,
    var transform: ((EntryState) -> Element)? = null,
) {
    internal enum class Style {
        Simple,
        Border,
        Animated
    }

    companion object {
        fun simple() = ButtonOption(Style.Simple)
        fun border() = ButtonOption(Style.Border)
        fun animated() = ButtonOption(Style.Animated)
    }
}

fun button(
    label: String,
    options: ButtonOption? = null,
    onClick: () -> Unit,
): Component {
    val stableRef = StableRef.create(onClick)
    val callback = staticCFunction { refPtr: COpaquePointer? ->
        refPtr?.asStableRef<() -> Unit>()?.get()?.invoke()
        Unit
    }

    val nativeOptions = when (options?.style) {
        ButtonOption.Style.Simple -> ftxui_button_option_simple()
        ButtonOption.Style.Border -> ftxui_button_option_border()
        ButtonOption.Style.Animated -> ftxui_button_option_animated()
        else -> null
    }?.let { baseOptions ->
        val transformBlock = options?.transform
        if (transformBlock != null) {
            val transformStableRef = StableRef.create(transformBlock)
            baseOptions.copy {
                this.transform = staticCFunction { state: CValue<ftxui_entry_state_t>, userdata: COpaquePointer? ->
                    val block = userdata!!.asStableRef<(EntryState) -> Element>().get()
                    val entryState = state.useContents {
                        EntryState(
                            label = this@useContents.label!!.toKString(),
                            state = this@useContents.state,
                            active = active,
                            focused = focused,
                            index = index
                        )
                    }
                    block(entryState).handle as ElementHandle?
                }
                this.transform_userdata = transformStableRef.asCPointer()
            }
        } else baseOptions
    }

    val handle = if (nativeOptions != null) {
        ftxui_component_button_with_options(label, callback, stableRef.asCPointer(), nativeOptions)
    } else {
        ftxui_component_button(label, callback, stableRef.asCPointer())
    }
    return Component(handle!!)
}

fun horizontal(vararg components: Component): Component {
    val container = ftxui_component_container_horizontal()!!
    for (component in components) ftxui_container_add(container, component.handle)
    return Component(container)
}

fun vertical(vararg components: Component): Component {
    val container = ftxui_component_container_vertical()!!
    for (component in components) ftxui_container_add(container, component.handle)
    return Component(container)
}

@Suppress("UNCHECKED_CAST")
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

fun Component.destroy() =
    ftxui_component_destroy(handle)