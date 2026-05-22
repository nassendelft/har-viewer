@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("UNCHECKED_CAST")

package nl.ncaj

import ftxui_c.*
import kotlinx.cinterop.*

internal typealias ComponentHandle = ftxui_component_handle_t
internal typealias ElementHandle = ftxui_element_handle_t

class Color internal constructor(internal val handle: ftxui_color_handle_t?) {
    companion object {
        val Black = Color(ftxui_color_palette16(FTXUI_PALETTE16_BLACK))
        val Red = Color(ftxui_color_palette16(FTXUI_PALETTE16_RED))
        val Green = Color(ftxui_color_palette16(FTXUI_PALETTE16_GREEN))
        val Yellow = Color(ftxui_color_palette16(FTXUI_PALETTE16_YELLOW))
        val Blue = Color(ftxui_color_palette16(FTXUI_PALETTE16_BLUE))
        val Magenta = Color(ftxui_color_palette16(FTXUI_PALETTE16_MAGENTA))
        val Cyan = Color(ftxui_color_palette16(FTXUI_PALETTE16_CYAN))
        val White = Color(ftxui_color_palette16(FTXUI_PALETTE16_WHITE))
        val Default = Color(ftxui_color_default())
        val GrayLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_GRAY_LIGHT))
        val GrayDark = Color(ftxui_color_palette16(FTXUI_PALETTE16_GRAY_DARK))

        fun rgb(r: UByte, g: UByte, b: UByte) = Color(ftxui_color_rgb(r, g, b))
        fun rgba(r: UByte, g: UByte, b: UByte, a: UByte) = Color(ftxui_color_rgba(r, g, b, a))
        fun hsv(h: UByte, s: UByte, v: UByte) = Color(ftxui_color_hsv(h, s, v))
        fun hsva(h: UByte, s: UByte, v: UByte, a: UByte) = Color(ftxui_color_hsva(h, s, v, a))
        fun palette1(index: ftxui_palette1_t) = Color(ftxui_color_palette1(index))
        fun palette16(index: ftxui_palette16_t) = Color(ftxui_color_palette16(index))
        fun palette256(index: ftxui_palette256_t) = Color(ftxui_color_palette256(index))

        fun interpolate(ratio: Float, colorA: Color, colorB: Color) =
            Color(ftxui_color_interpolate(ratio, colorA.handle, colorB.handle))
    }

    fun destroy() = ftxui_color_destroy(handle)
}

enum class BorderStyle(internal val value: ftxui_border_style_t) {
    Light(ftxui_border_style_t.FTXUI_BORDER_STYLE_LIGHT),
    Dashed(ftxui_border_style_t.FTXUI_BORDER_STYLE_DASHED),
    Heavy(ftxui_border_style_t.FTXUI_BORDER_STYLE_HEAVY),
    Double(ftxui_border_style_t.FTXUI_BORDER_STYLE_DOUBLE),
    Rounded(ftxui_border_style_t.FTXUI_BORDER_STYLE_ROUNDED),
    Empty(ftxui_border_style_t.FTXUI_BORDER_STYLE_EMPTY);
}

enum class WidthOrHeight(internal val value: ftxui_width_or_height_t) {
    Width(ftxui_width_or_height_t.FTXUI_WIDTH_OR_HEIGHT_WIDTH),
    Height(ftxui_width_or_height_t.FTXUI_WIDTH_OR_HEIGHT_HEIGHT);
}

enum class Constraint(internal val value: ftxui_constraint_t) {
    LessThan(ftxui_constraint_t.FTXUI_CONSTRAINT_LESS_THAN),
    GreaterThan(ftxui_constraint_t.FTXUI_CONSTRAINT_GREATER_THAN),
    Equal(ftxui_constraint_t.FTXUI_CONSTRAINT_EQUAL),
}

data class EntryState(
    val label: String,
    val state: Boolean,
    val active: Boolean,
    val focused: Boolean,
    val index: Int
)

open class Component internal constructor(internal val handle: ComponentHandle)

class ContainerComponent internal constructor(handle: ComponentHandle): Component(handle){
    fun add(component: Component) = ftxui_container_add(this.handle, component.handle)
}

class Element internal constructor(internal val handle: ElementHandle)

class FtxUIApp internal constructor(internal val handle: ftxui_app_handle_t) {
    fun loop(root: Component) = ftxui_app_loop(handle, root.handle)

    fun exit() = ftxui_app_exit(handle)

    fun destroy() = ftxui_app_destroy(handle)

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

fun Element.color(color: Color) =
    Element(ftxui_element_color(this.handle, color.handle)!!)

fun Element.bgcolor(color: Color) =
    Element(ftxui_element_bgcolor(this.handle, color.handle)!!)

fun Element.bold() =
    Element(ftxui_element_bold(this.handle)!!)

fun Element.inverted() =
    Element(ftxui_element_inverted(this.handle)!!)

fun Element.underlined() =
    Element(ftxui_element_underlined(this.handle)!!)

fun Element.window(title: Element) =
    Element(ftxui_element_window(title.handle, this.handle)!!)

fun Element.vscrollIndicator() =
    Element(ftxui_element_vscroll_indicator(this.handle)!!)

fun Element.frame() =
    Element(ftxui_element_frame(this.handle)!!)

fun Element.size(widthOrHeight: WidthOrHeight, constraint: Constraint, value: Int) =
    Element(ftxui_element_set_size(this.handle, widthOrHeight.value, constraint.value, value)!!)

// -- Elements

fun text(text: String) = Element(ftxui_element_text(text)!!)

fun gauge(value: Float) = Element(ftxui_element_gauge(value.toDouble())!!)

fun separator() = Element(ftxui_element_separator()!!)
fun separatorLight() =
    Element(ftxui_element_separator_light()!!)

fun separatorDashed() =
    Element(ftxui_element_separator_dashed()!!)

fun separatorHeavy() =
    Element(ftxui_element_separator_heavy()!!)

fun separatorDouble() =
    Element(ftxui_element_separator_double()!!)

fun separatorEmpty() =
    Element(ftxui_element_separator_empty()!!)

fun separatorStyled(style: BorderStyle) = Element(ftxui_element_separator_styled(style.value)!!)
fun separatorCharacter(character: String) = Element(ftxui_element_separator_character(character)!!)
fun separatorHSelector(
    left: Float,
    right: Float,
    unselectedColor: Color = Color.Default,
    selectedColor: Color = Color.Default
) = Element(ftxui_element_separator_hselector(left, right, unselectedColor.handle, selectedColor.handle)!!)

fun separatorVSelector(
    up: Float,
    down: Float,
    unselectedColor: Color = Color.Default,
    selectedColor: Color = Color.Default
) = Element(ftxui_element_separator_vselector(up, down, unselectedColor.handle, selectedColor.handle)!!)

fun vbox(vararg elements: Element): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    return Element(ftxui_element_vbox(array, elements.size)!!)
}

fun hbox(vararg elements: Element): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    return Element(ftxui_element_hbox(array, elements.size)!!)
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

class ButtonOption private constructor(internal val handle: CValue<ftxui_button_option_t>) {
    var transform: ((EntryState) -> Element)? = null

    companion object {
        fun simple() = ButtonOption(ftxui_button_option_simple())
        fun ascii() = ButtonOption(ftxui_button_option_ascii())
        fun border() = ButtonOption(ftxui_button_option_border())
        fun animated() = ButtonOption(
            ftxui_button_option_animated(
                Color.Black.handle,
                Color.GrayLight.handle,
                Color.GrayDark.handle,
                Color.White.handle
            )
        )

        fun animated(color: Color) = ButtonOption(
            ftxui_button_option_animated(
                Color.interpolate(0.85f, color, Color.Black).handle,
                Color.interpolate(0.10f, color, Color.White).handle,
                Color.interpolate(0.10f, color, Color.Black).handle,
                Color.interpolate(0.85f, color, Color.White).handle
            )
        )

        fun animated(background: Color, foreground: Color) = ButtonOption(
            ftxui_button_option_animated(
                background.handle,
                foreground.handle,
                background.handle,
                foreground.handle,
            )
        )

        fun animated(
            background: Color,
            foreground: Color,
            backgroundActive: Color,
            foregroundActive: Color
        ) = ButtonOption(
            ftxui_button_option_animated(
                background.handle,
                foreground.handle,
                backgroundActive.handle,
                foregroundActive.handle,
            )
        )
    }
}

fun button(
    label: String,
    onClick: () -> Unit,
    options: ButtonOption = ButtonOption.simple()
): Component {
    val stableRef = StableRef.create(onClick)
    val callback = staticCFunction { refPtr: COpaquePointer? ->
        refPtr?.asStableRef<() -> Unit>()?.get()?.invoke()
        Unit
    }

    val transform = options.transform
    if (transform != null) {
        val transformStableRef = StableRef.create(transform)
        options.handle.useContents {
            this.transform = staticCFunction { state: CValue<ftxui_entry_state_t>, refPtr: COpaquePointer? ->
                state.useContents {
                    val block = refPtr!!.asStableRef<(EntryState) -> Element>().get()
                    val entryState = EntryState(
                        label = this.label?.toKString() ?: "",
                        state = this.state,
                        active = this.active,
                        focused = this.focused,
                        index = this.index
                    )
                    block(entryState).handle
                }
            }
            this.transform_userdata = transformStableRef.asCPointer()
        }
    }

    val handle = ftxui_component_button_with_options(label, callback, stableRef.asCPointer(), options.handle)
    return Component(handle!!)
}

fun horizontal(vararg components: Component): ContainerComponent {
    val container = ContainerComponent(ftxui_component_container_horizontal()!!)
    for (component in components) container.add(component)
    return container
}

fun vertical(vararg components: Component): ContainerComponent {
    val container = ContainerComponent(ftxui_component_container_vertical()!!)
    for (component in components) container.add(component)
    return container
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