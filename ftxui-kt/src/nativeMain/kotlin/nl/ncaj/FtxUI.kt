@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("UNCHECKED_CAST")

package nl.ncaj

import ftxui_c.*
import kotlinx.cinterop.*
import kotlin.reflect.KMutableProperty0

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
        val RedLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_RED_LIGHT))
        val GreenLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_GREEN_LIGHT))
        val YellowLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_YELLOW_LIGHT))
        val BlueLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_BLUE_LIGHT))
        val MagentaLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_MAGENTA_LIGHT))
        val CyanLight = Color(ftxui_color_palette16(FTXUI_PALETTE16_CYAN_LIGHT))

        fun rgb(r: UByte, g: UByte, b: UByte) = Color(ftxui_color_rgb(r, g, b))
        fun rgba(r: UByte, g: UByte, b: UByte, a: UByte) = Color(ftxui_color_rgba(r, g, b, a))
        fun hsv(h: UByte, s: UByte, v: UByte) = Color(ftxui_color_hsv(h, s, v))
        fun hsva(h: UByte, s: UByte, v: UByte, a: UByte) = Color(ftxui_color_hsva(h, s, v, a))
        fun palette1(index: ftxui_palette1_t) = Color(ftxui_color_palette1(index))
        fun palette16(index: ftxui_palette16_t) = Color(ftxui_color_palette16(index))
        fun palette256(index: ftxui_palette256_t) = Color(ftxui_color_palette256(index))
        fun palette256(index: Int) = Color(ftxui_color_palette256_raw(index))

        fun interpolate(ratio: Float, colorA: Color, colorB: Color) =
            Color(ftxui_color_interpolate(ratio, colorA.handle, colorB.handle))

        fun blend(lhs: Color, rhs: Color) =
            Color(ftxui_color_blend(lhs.handle, rhs.handle))
    }

    fun isOpaque() = ftxui_color_is_opaque(handle)

    override fun equals(other: Any?): Boolean {
        if (other !is Color) return false
        return ftxui_color_equals(handle, other.handle)
    }

    override fun hashCode() = handle.hashCode()

    fun print(isBackground: Boolean): String =
        ftxui_color_print(handle, isBackground)?.toKString() ?: ""

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

enum class Direction(internal val value: ftxui_direction_t) {
    Up(ftxui_direction_t.FTXUI_DIRECTION_UP),
    Down(ftxui_direction_t.FTXUI_DIRECTION_DOWN),
    Left(ftxui_direction_t.FTXUI_DIRECTION_LEFT),
    Right(ftxui_direction_t.FTXUI_DIRECTION_RIGHT),
}

data class EntryState(
    val label: String,
    val state: Boolean,
    val active: Boolean,
    val focused: Boolean,
    val index: Int
)

// cleanups run in registration order when destroy() is called.
open class Component internal constructor(internal val handle: ComponentHandle) {
    internal val cleanups = mutableListOf<() -> Unit>()

    fun destroy() {
        cleanups.forEach { it() }
        cleanups.clear()
        ftxui_component_destroy(handle)
    }
}

// add() transfers ownership: the container's destroy() will also destroy the child handle.
// Do not call destroy() on a child after adding it to a container.
class ContainerComponent internal constructor(handle: ComponentHandle) : Component(handle) {
    fun add(component: Component) {
        ftxui_container_add(handle, component.handle)
        cleanups.addAll(component.cleanups)
        cleanups.add { ftxui_component_destroy(component.handle) }
        component.cleanups.clear()
    }
}

class Element internal constructor(internal val handle: ElementHandle)

fun Element.destroy() = ftxui_element_destroy(handle)

data class Dimensions(val dimx: Int, val dimy: Int)

object Terminal {
    fun size(): Dimensions = Dimensions(ftxui_terminal_width(), ftxui_terminal_height())
}

class FtxUIApp internal constructor(internal val handle: ftxui_app_handle_t) {
    internal val cleanups = mutableListOf<() -> Unit>()

    fun loop(root: Component) = ftxui_app_loop(handle, root.handle)

    fun exit() = ftxui_app_exit(handle)

    fun destroy() {
        cleanups.forEach { it() }
        cleanups.clear()
        ftxui_app_destroy(handle)
    }

    fun exitClosure(): () -> Unit = { exit() }

    fun selectionChange(callback: () -> Unit) {
        val stableRef = StableRef.create(callback)
        val c = staticCFunction { refPtr: COpaquePointer? ->
            refPtr!!.asStableRef<() -> Unit>().get()()
        }
        ftxui_app_selection_change(handle, c, stableRef.asCPointer())
        cleanups.add { stableRef.dispose() }
    }

    fun getSelection(): String {
        val ptr = ftxui_app_get_selection(handle) ?: return ""
        val result = ptr.toKString()
        platform.posix.free(ptr)
        return result
    }

    companion object {
        fun fullscreen() = FtxUIApp(ftxui_app_create_fullscreen()!!)
        fun fitComponent() = FtxUIApp(ftxui_app_create_fit_component()!!)
        fun terminalOutput() = FtxUIApp(ftxui_app_create_terminal_output()!!)
    }
}

// Transfers cleanups from this component into a new wrapper component, and schedules
// destruction of this component's handle when the wrapper is destroyed.
// After calling this, only use and destroy the returned component.
private fun Component.wrapOwning(newHandle: ComponentHandle): Component {
    val new = Component(newHandle)
    new.cleanups.addAll(cleanups)
    new.cleanups.add { ftxui_component_destroy(handle) }
    cleanups.clear()
    return new
}

// Same as wrapOwning but takes ownership of two source components.
private fun wrapOwning(a: Component, b: Component, newHandle: ComponentHandle): Component {
    val new = a.wrapOwning(newHandle)
    new.cleanups.addAll(b.cleanups)
    new.cleanups.add { ftxui_component_destroy(b.handle) }
    b.cleanups.clear()
    return new
}

// -- Element decorators

fun Element.border() = Element(ftxui_element_border(this.handle)!!)
fun Element.borderLight() = Element(ftxui_element_border_light(this.handle)!!)
fun Element.borderDashed() = Element(ftxui_element_border_dashed(this.handle)!!)
fun Element.borderHeavy() = Element(ftxui_element_border_heavy(this.handle)!!)
fun Element.borderDouble() = Element(ftxui_element_border_double(this.handle)!!)
fun Element.borderRounded() = Element(ftxui_element_border_rounded(this.handle)!!)
fun Element.borderEmpty() = Element(ftxui_element_border_empty(this.handle)!!)

fun Element.flex() = Element(ftxui_element_flex(this.handle)!!)
fun Element.color(color: Color) = Element(ftxui_element_color(this.handle, color.handle)!!)
fun Element.bgcolor(color: Color) = Element(ftxui_element_bgcolor(this.handle, color.handle)!!)
fun Element.bold() = Element(ftxui_element_bold(this.handle)!!)
fun Element.inverted() = Element(ftxui_element_inverted(this.handle)!!)
fun Element.underlined() = Element(ftxui_element_underlined(this.handle)!!)
fun Element.dim() = Element(ftxui_element_dim(this.handle)!!)
fun Element.blink() = Element(ftxui_element_blink(this.handle)!!)
fun Element.strikethrough() = Element(ftxui_element_strikethrough(this.handle)!!)
fun Element.window(title: Element) = Element(ftxui_element_window(title.handle, this.handle)!!)
fun Element.vscrollIndicator() = Element(ftxui_element_vscroll_indicator(this.handle)!!)
fun Element.frame() = Element(ftxui_element_frame(this.handle)!!)
fun Element.size(widthOrHeight: WidthOrHeight, constraint: Constraint, value: Int) =
    Element(ftxui_element_set_size(this.handle, widthOrHeight.value, constraint.value, value)!!)
fun Element.hcenter() = Element(ftxui_element_hcenter(this.handle)!!)
fun Element.vcenter() = Element(ftxui_element_vcenter(this.handle)!!)
fun Element.center() = Element(ftxui_element_center(this.handle)!!)
fun Element.alignRight() = Element(ftxui_element_align_right(this.handle)!!)
fun Element.nothing() = Element(ftxui_element_nothing(this.handle)!!)

fun Element.italic() = Element(ftxui_element_italic(this.handle)!!)
fun Element.underlinedDouble() = Element(ftxui_element_underlined_double(this.handle)!!)
fun Element.automerge() = Element(ftxui_element_automerge(this.handle)!!)
fun Element.hyperlink(link: String) = Element(ftxui_element_hyperlink(link, this.handle)!!)
fun Element.hscrollIndicator() = Element(ftxui_element_hscroll_indicator(this.handle)!!)
fun Element.clearUnder() = Element(ftxui_element_clear_under(this.handle)!!)
fun Element.borderStyled(style: BorderStyle) = Element(ftxui_element_border_styled(this.handle, style.value)!!)
fun Element.borderStyled(style: BorderStyle, color: Color) = Element(ftxui_element_border_styled_color(this.handle, style.value, color.handle)!!)
fun Element.borderStyled(color: Color) = Element(ftxui_element_border_colored(this.handle, color.handle)!!)
fun Element.selectionStyleReset() = Element(ftxui_element_selection_style_reset(this.handle)!!)
fun Element.selectionColor(color: Color) = Element(ftxui_element_selection_color(this.handle, color.handle)!!)
fun Element.selectionBgColor(color: Color) = Element(ftxui_element_selection_background_color(this.handle, color.handle)!!)
fun Element.selectionFgColor(color: Color) = Element(ftxui_element_selection_foreground_color(this.handle, color.handle)!!)
fun Element.selectionStyle(
    callback: ftxui_cell_style_callback_t,
    userdata: COpaquePointer? = null,
) = Element(ftxui_element_selection_style(this.handle, callback, userdata)!!)
fun Element.focusPosition(x: Int, y: Int) = Element(ftxui_element_focus_position(this.handle, x, y)!!)
fun Element.focusPositionRelative(x: Float, y: Float) = Element(ftxui_element_focus_position_relative(this.handle, x, y)!!)

fun Element.flexGrow() = Element(ftxui_element_flex_grow(this.handle)!!)
fun Element.flexShrink() = Element(ftxui_element_flex_shrink(this.handle)!!)
fun Element.xflex() = Element(ftxui_element_xflex(this.handle)!!)
fun Element.xflexGrow() = Element(ftxui_element_xflex_grow(this.handle)!!)
fun Element.xflexShrink() = Element(ftxui_element_xflex_shrink(this.handle)!!)
fun Element.yflex() = Element(ftxui_element_yflex(this.handle)!!)
fun Element.yflexGrow() = Element(ftxui_element_yflex_grow(this.handle)!!)
fun Element.yflexShrink() = Element(ftxui_element_yflex_shrink(this.handle)!!)
fun Element.notflex() = Element(ftxui_element_notflex(this.handle)!!)
fun Element.xframe() = Element(ftxui_element_xframe(this.handle)!!)
fun Element.yframe() = Element(ftxui_element_yframe(this.handle)!!)
fun Element.focus() = Element(ftxui_element_focus(this.handle)!!)
fun Element.focusCursorBlock() = Element(ftxui_element_focus_cursor_block(this.handle)!!)
fun Element.focusCursorBlockBlinking() = Element(ftxui_element_focus_cursor_block_blinking(this.handle)!!)
fun Element.focusCursorBar() = Element(ftxui_element_focus_cursor_bar(this.handle)!!)
fun Element.focusCursorBarBlinking() = Element(ftxui_element_focus_cursor_bar_blinking(this.handle)!!)
fun Element.focusCursorUnderline() = Element(ftxui_element_focus_cursor_underline(this.handle)!!)
fun Element.focusCursorUnderlineBlinking() = Element(ftxui_element_focus_cursor_underline_blinking(this.handle)!!)

// -- Elements

fun text(text: String) = Element(ftxui_element_text(text)!!)

fun gauge(value: Float) = Element(ftxui_element_gauge(value.toDouble())!!)

fun separator() = Element(ftxui_element_separator()!!)
fun separatorLight() = Element(ftxui_element_separator_light()!!)
fun separatorDashed() = Element(ftxui_element_separator_dashed()!!)
fun separatorHeavy() = Element(ftxui_element_separator_heavy()!!)
fun separatorDouble() = Element(ftxui_element_separator_double()!!)
fun separatorEmpty() = Element(ftxui_element_separator_empty()!!)
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

fun vtext(text: String) = Element(ftxui_element_vtext(text)!!)
fun spinner(charsetIndex: Int, imageIndex: Int) = Element(ftxui_element_spinner(charsetIndex, imageIndex)!!)
fun paragraph(text: String) = Element(ftxui_element_paragraph(text)!!)
fun paragraphAlignLeft(text: String) = Element(ftxui_element_paragraph_align_left(text)!!)
fun paragraphAlignRight(text: String) = Element(ftxui_element_paragraph_align_right(text)!!)
fun paragraphAlignCenter(text: String) = Element(ftxui_element_paragraph_align_center(text)!!)
fun paragraphAlignJustify(text: String) = Element(ftxui_element_paragraph_align_justify(text)!!)
fun emptyElement() = Element(ftxui_element_empty()!!)
fun filler() = Element(ftxui_element_filler()!!)
fun gaugeLeft(value: Float) = Element(ftxui_element_gauge_left(value.toDouble())!!)
fun gaugeRight(value: Float) = Element(ftxui_element_gauge_right(value.toDouble())!!)
fun gaugeUp(value: Float) = Element(ftxui_element_gauge_up(value.toDouble())!!)
fun gaugeDown(value: Float) = Element(ftxui_element_gauge_down(value.toDouble())!!)
fun gaugeDirection(value: Float, direction: Direction) = Element(ftxui_element_gauge_direction(value.toDouble(), direction.value)!!)
fun dbox(vararg elements: Element): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    return Element(ftxui_element_dbox(array, elements.size)!!)
}
fun hflow(vararg elements: Element): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    return Element(ftxui_element_hflow(array, elements.size)!!)
}
fun vflow(vararg elements: Element): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { index, element -> array[index] = element.handle }
    return Element(ftxui_element_vflow(array, elements.size)!!)
}

// -- Components

// ownedColors holds Color objects created internally (e.g. interpolated) that must be
// freed after the button component is constructed (C++ copies them at that point).
class ButtonOption private constructor(internal val handle: CValue<ftxui_button_option_t>) {
    var transform: ((EntryState) -> Element)? = null
    internal val ownedColors = mutableListOf<Color>()

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

        fun animated(color: Color): ButtonOption {
            val bg = Color.interpolate(0.85f, color, Color.Black)
            val fg = Color.interpolate(0.10f, color, Color.White)
            val bgActive = Color.interpolate(0.10f, color, Color.Black)
            val fgActive = Color.interpolate(0.85f, color, Color.White)
            return ButtonOption(ftxui_button_option_animated(bg.handle, fg.handle, bgActive.handle, fgActive.handle))
                .also { it.ownedColors.addAll(listOf(bg, fg, bgActive, fgActive)) }
        }

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
    val clickStableRef = StableRef.create(onClick)
    val callback = staticCFunction { refPtr: COpaquePointer? ->
        refPtr?.asStableRef<() -> Unit>()?.get()?.invoke()
        Unit
    }

    var transformStableRef: StableRef<(EntryState) -> Element>? = null
    val transform = options.transform
    if (transform != null) {
        val tsr = StableRef.create(transform)
        transformStableRef = tsr
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
            this.transform_userdata = tsr.asCPointer()
        }
    }

    val handle = ftxui_component_button_with_options(label, callback, clickStableRef.asCPointer(), options.handle)
    // C++ has copied the color values out of the handles; safe to free them now.
    options.ownedColors.forEach { it.destroy() }

    return Component(handle!!).also { c ->
        c.cleanups.add { clickStableRef.dispose() }
        transformStableRef?.let { ref -> c.cleanups.add { ref.dispose() } }
    }
}

// horizontal/vertical delegate to ContainerComponent.add(), which takes ownership of each child.
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

fun renderer(
    child: Component? = null,
    callback: () -> Element
): Component {
    val stableRef = StableRef.create(callback)
    val renderCallback = staticCFunction { refPtr: COpaquePointer? ->
        val block = refPtr!!.asStableRef<() -> Element>().get()
        block().handle
    } as ftxui_render_callback_t
    val handle = ftxui_component_renderer(child?.handle, renderCallback, stableRef.asCPointer())!!
    return (child?.wrapOwning(handle) ?: Component(handle)).also {
        it.cleanups.add { stableRef.dispose() }
    }
}

fun focusableRenderer(callback: (focused: Boolean) -> Element): Component {
    val stableRef = StableRef.create(callback)
    val renderCallback = staticCFunction { focused: Boolean, refPtr: COpaquePointer? ->
        refPtr!!.asStableRef<(Boolean) -> Element>().get()(focused).handle
    } as ftxui_focused_render_callback_t
    return Component(ftxui_component_renderer_focusable(renderCallback, stableRef.asCPointer())!!).also {
        it.cleanups.add { stableRef.dispose() }
    }
}

fun Component.render() = Element(ftxui_component_render(this.handle)!!)

fun Component.decorateRender(transform: (Element) -> Element): Component {
    val stableRef = StableRef.create(transform)
    val callback = staticCFunction { innerHandle: ftxui_element_handle_t?, refPtr: COpaquePointer? ->
        refPtr!!.asStableRef<(Element) -> Element>().get()(Element(innerHandle!!)).handle
    } as ftxui_inner_render_callback_t
    return wrapOwning(ftxui_component_renderer_with_inner(handle, callback, stableRef.asCPointer())!!).also {
        it.cleanups.add { stableRef.dispose() }
    }
}

data class FtxUIEvent(
    val input: String,
    val debugString: String,
    val isCharacter: Boolean,
    val character: String,
    val isMouse: Boolean,
    val mouseX: Int = 0,
    val mouseY: Int = 0,
) {
    fun isKey(key: String): Boolean = input == key
}

object Key {
    // Arrow keys
    const val ArrowLeft      = "\u001B[D"
    const val ArrowRight     = "\u001B[C"
    const val ArrowUp        = "\u001B[A"
    const val ArrowDown      = "\u001B[B"
    const val ArrowLeftCtrl  = "\u001B[1;5D"
    const val ArrowRightCtrl = "\u001B[1;5C"
    const val ArrowUpCtrl    = "\u001B[1;5A"
    const val ArrowDownCtrl  = "\u001B[1;5B"

    // Common keys
    const val Backspace  = "\u007F"
    const val Delete     = "\u001B[3~"
    const val Escape     = "\u001B"
    const val Return     = "\n"
    const val Tab        = "\t"
    const val TabReverse = "\u001B[Z"

    // Navigation keys
    const val Insert   = "\u001B[2~"
    const val Home     = "\u001B[H"
    const val End      = "\u001B[F"
    const val PageUp   = "\u001B[5~"
    const val PageDown = "\u001B[6~"

    // Function keys
    const val F1  = "\u001BOP"
    const val F2  = "\u001BOQ"
    const val F3  = "\u001BOR"
    const val F4  = "\u001BOS"
    const val F5  = "\u001B[15~"
    const val F6  = "\u001B[17~"
    const val F7  = "\u001B[18~"
    const val F8  = "\u001B[19~"
    const val F9  = "\u001B[20~"
    const val F10 = "\u001B[21~"
    const val F11 = "\u001B[23~"
    const val F12 = "\u001B[24~"

    // Ctrl keys (\u0001..\u001A)
    const val CtrlA = "\u0001"
    const val CtrlB = "\u0002"
    const val CtrlC = "\u0003"
    const val CtrlD = "\u0004"
    const val CtrlE = "\u0005"
    const val CtrlF = "\u0006"
    const val CtrlG = "\u0007"
    const val CtrlH = "\u0008"
    const val CtrlI = "\t"
    const val CtrlJ = "\n"
    const val CtrlK = "\u000B"
    const val CtrlL = "\u000C"
    const val CtrlM = "\r"
    const val CtrlN = "\u000E"
    const val CtrlO = "\u000F"
    const val CtrlP = "\u0010"
    const val CtrlQ = "\u0011"
    const val CtrlR = "\u0012"
    const val CtrlS = "\u0013"
    const val CtrlT = "\u0014"
    const val CtrlU = "\u0015"
    const val CtrlV = "\u0016"
    const val CtrlW = "\u0017"
    const val CtrlX = "\u0018"
    const val CtrlY = "\u0019"
    const val CtrlZ = "\u001A"

    // Alt keys (ESC + letter)
    const val AltA = "\u001Ba"
    const val AltB = "\u001Bb"
    const val AltC = "\u001Bc"
    const val AltD = "\u001Bd"
    const val AltE = "\u001Be"
    const val AltF = "\u001Bf"
    const val AltG = "\u001Bg"
    const val AltH = "\u001Bh"
    const val AltI = "\u001Bi"
    const val AltJ = "\u001Bj"
    const val AltK = "\u001Bk"
    const val AltL = "\u001Bl"
    const val AltM = "\u001Bm"
    const val AltN = "\u001Bn"
    const val AltO = "\u001Bo"
    const val AltP = "\u001Bp"
    const val AltQ = "\u001Bq"
    const val AltR = "\u001Br"
    const val AltS = "\u001Bs"
    const val AltT = "\u001Bt"
    const val AltU = "\u001Bu"
    const val AltV = "\u001Bv"
    const val AltW = "\u001Bw"
    const val AltX = "\u001Bx"
    const val AltY = "\u001By"
    const val AltZ = "\u001Bz"

    // CtrlAlt keys (ESC + Ctrl key)
    const val CtrlAltA = "\u001B\u0001"
    const val CtrlAltB = "\u001B\u0002"
    const val CtrlAltC = "\u001B\u0003"
    const val CtrlAltD = "\u001B\u0004"
    const val CtrlAltE = "\u001B\u0005"
    const val CtrlAltF = "\u001B\u0006"
    const val CtrlAltG = "\u001B\u0007"
    const val CtrlAltH = "\u001B\u0008"
    const val CtrlAltI = "\u001B\t"
    const val CtrlAltJ = "\u001B\n"
    const val CtrlAltK = "\u001B\u000B"
    const val CtrlAltL = "\u001B\u000C"
    const val CtrlAltM = "\u001B\r"
    const val CtrlAltN = "\u001B\u000E"
    const val CtrlAltO = "\u001B\u000F"
    const val CtrlAltP = "\u001B\u0010"
    const val CtrlAltQ = "\u001B\u0011"
    const val CtrlAltR = "\u001B\u0012"
    const val CtrlAltS = "\u001B\u0013"
    const val CtrlAltT = "\u001B\u0014"
    const val CtrlAltU = "\u001B\u0015"
    const val CtrlAltV = "\u001B\u0016"
    const val CtrlAltW = "\u001B\u0017"
    const val CtrlAltX = "\u001B\u0018"
    const val CtrlAltY = "\u001B\u0019"
    const val CtrlAltZ = "\u001B\u001A"
}

fun Component.catchEvent(handler: (FtxUIEvent) -> Boolean): Component {
    val stableRef = StableRef.create(handler)
    val callback = staticCFunction { eventHandle: ftxui_event_handle_t?, refPtr: COpaquePointer? ->
        val block = refPtr!!.asStableRef<(FtxUIEvent) -> Boolean>().get()
        val isMouse = ftxui_event_is_mouse(eventHandle)
        val e = FtxUIEvent(
            input = ftxui_event_input(eventHandle)?.toKString() ?: "",
            debugString = ftxui_event_debug_string(eventHandle)?.toKString() ?: "",
            isCharacter = ftxui_event_is_character(eventHandle),
            character = ftxui_event_character(eventHandle)?.toKString() ?: "",
            isMouse = isMouse,
            mouseX = if (isMouse) ftxui_event_mouse_x(eventHandle) else 0,
            mouseY = if (isMouse) ftxui_event_mouse_y(eventHandle) else 0,
        )
        block(e)
    }
    return wrapOwning(ftxui_component_catch_event(handle, callback, stableRef.asCPointer())!!).also {
        it.cleanups.add { stableRef.dispose() }
    }
}

// Wraps `inner` with a Renderer that bidirectionally syncs a Kotlin property with a
// native buffer on every frame.
//
// Sync logic per frame:
//   - If the Kotlin property changed since last frame → push to native (Kotlin wins).
//   - Otherwise, if the native value changed (FTXUI event) → pull to Kotlin.
//
// This means Kotlin programmatic changes take precedence over FTXUI event-driven changes
// when both happen in the same frame, which is the expected behaviour.
private fun <T> syncWrapper(
    inner: Component,
    prop: KMutableProperty0<T>,
    getNative: () -> T,
    setNative: (T) -> Unit,
): Component {
    var lastKotlin = prop.get()
    return renderer(child = inner) {
        val current = prop.get()
        if (current != lastKotlin) {
            setNative(current)
            lastKotlin = current
        } else {
            val fromNative = getNative()
            if (fromNative != lastKotlin) {
                prop.set(fromNative)
                lastKotlin = fromNative
            }
        }
        inner.render()
    }
}

// Allocates a native Int buffer, builds an inner component with it, wraps the result in
// a sync renderer, and registers cleanup of the buffer with the returned component.
private fun intStateSynced(
    initial: Int,
    prop: KMutableProperty0<Int>,
    createInner: (CPointer<IntVar>) -> Component,
): Component {
    val native = nativeHeap.alloc<IntVar>().also { it.value = initial }
    val inner = createInner(native.ptr)
    return syncWrapper(inner, prop, { native.value }, { native.value = it })
        .also { it.cleanups.add { nativeHeap.free(native) } }
}

// Same as intStateSynced but for Boolean state.
private fun boolStateSynced(
    initial: Boolean,
    prop: KMutableProperty0<Boolean>,
    createInner: (CPointer<BooleanVar>) -> Component,
): Component {
    val native = nativeHeap.alloc<BooleanVar>().also { it.value = initial }
    val inner = createInner(native.ptr)
    return syncWrapper(inner, prop, { native.value }, { native.value = it })
        .also { it.cleanups.add { nativeHeap.free(native) } }
}

// -- Component decorators
// These wrap the component in a Renderer and transfer ownership: the source component's
// handle will be destroyed when the returned component is destroyed.
// Note: wrapping discards focus/event forwarding — use on non-interactive components only.

fun Component.border() = wrapOwning(ftxui_component_border(handle)!!)
fun Component.borderLight() = wrapOwning(ftxui_component_border_light(handle)!!)
fun Component.borderDashed() = wrapOwning(ftxui_component_border_dashed(handle)!!)
fun Component.borderHeavy() = wrapOwning(ftxui_component_border_heavy(handle)!!)
fun Component.borderDouble() = wrapOwning(ftxui_component_border_double(handle)!!)
fun Component.borderRounded() = wrapOwning(ftxui_component_border_rounded(handle)!!)
fun Component.borderEmpty() = wrapOwning(ftxui_component_border_empty(handle)!!)

fun Component.flex() = wrapOwning(ftxui_component_flex(handle)!!)
fun Component.frame() = wrapOwning(ftxui_component_frame(handle)!!)
fun Component.vscrollIndicator() = wrapOwning(ftxui_component_vscroll_indicator(handle)!!)
fun Component.size(widthOrHeight: WidthOrHeight, constraint: Constraint, value: Int) =
    wrapOwning(ftxui_component_set_size(handle, widthOrHeight.value, constraint.value, value)!!)

fun Component.bold() = wrapOwning(ftxui_component_bold(handle)!!)
fun Component.inverted() = wrapOwning(ftxui_component_inverted(handle)!!)
fun Component.underlined() = wrapOwning(ftxui_component_underlined(handle)!!)
fun Component.dim() = wrapOwning(ftxui_component_dim(handle)!!)
fun Component.blink() = wrapOwning(ftxui_component_blink(handle)!!)
fun Component.strikethrough() = wrapOwning(ftxui_component_strikethrough(handle)!!)

fun Component.color(color: Color) = wrapOwning(ftxui_component_color(handle, color.handle)!!)
fun Component.bgcolor(color: Color) = wrapOwning(ftxui_component_bgcolor(handle, color.handle)!!)

fun Component.hcenter() = wrapOwning(ftxui_component_hcenter(handle)!!)
fun Component.vcenter() = wrapOwning(ftxui_component_vcenter(handle)!!)
fun Component.center() = wrapOwning(ftxui_component_center(handle)!!)
fun Component.alignRight() = wrapOwning(ftxui_component_align_right(handle)!!)

fun Component.nothing() = wrapOwning(ftxui_component_nothing(handle)!!)
fun Component.hoverable(hover: BoolState) = wrapOwning(ftxui_component_hoverable(handle, hover.ptr)!!)

// -- State holders
// Native-heap-backed mutable values for interactive components.
// Call free() when the associated component is destroyed.

class BoolState(initial: Boolean = false) {
    private val native = nativeHeap.alloc<BooleanVar>().also { it.value = initial }
    var value: Boolean
        get() = native.value
        set(v) { native.value = v }
    internal val ptr get() = native.ptr
    fun free() = nativeHeap.free(native)
}

class IntState(initial: Int = 0) {
    private val native = nativeHeap.alloc<IntVar>().also { it.value = initial }
    var value: Int
        get() = native.value
        set(v) { native.value = v }
    internal val ptr get() = native.ptr
    fun free() = nativeHeap.free(native)
}

class StringState(initial: String = "") {
    private val handle = ftxui_string_create(initial)!!
    var value: String
        get() = ftxui_string_get(handle)?.toKString() ?: ""
        set(v) { ftxui_string_set(handle, v) }
    internal val ptr get() = handle
    fun free() = ftxui_string_destroy(handle)
}

class FloatState(initial: Float = 0f) {
    private val native = nativeHeap.alloc<FloatVar>().also { it.value = initial }
    var value: Float
        get() = native.value
        set(v) { native.value = v }
    internal val ptr get() = native.ptr
    fun free() = nativeHeap.free(native)
}

// -- Additional components

fun input(content: StringState, placeholder: String = ""): Component =
    Component(ftxui_component_input(content.ptr, placeholder)!!)

fun inputPassword(content: StringState, placeholder: String = ""): Component =
    Component(ftxui_component_input_password(content.ptr, placeholder)!!)

fun checkbox(label: String, checked: BoolState): Component =
    Component(ftxui_component_checkbox(label, checked.ptr)!!)

fun toggle(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_toggle(ptrs, entries.size, selected.ptr)!!)
}

fun slider(label: String, value: IntState, min: Int, max: Int, increment: Int = 1): Component =
    Component(ftxui_component_slider(label, value.ptr, min, max, increment)!!)

fun slider(value: IntState, min: Int, max: Int, increment: Int = 1, direction: Direction): Component =
    Component(ftxui_component_slider_int_direction(value.ptr, min, max, increment, direction.value)!!)

fun slider(label: String, value: FloatState, min: Float, max: Float, increment: Float): Component =
    Component(ftxui_component_slider_float(label, value.ptr, min, max, increment)!!)

fun slider(value: FloatState, min: Float, max: Float, increment: Float, direction: Direction,
           colorActive: Color? = null, colorInactive: Color? = null): Component =
    Component(ftxui_component_slider_float_direction(value.ptr, min, max, increment, direction.value, colorActive?.handle, colorInactive?.handle)!!)

fun radiobox(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_radiobox(ptrs, entries.size, selected.ptr)!!)
}

fun menu(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_menu(ptrs, entries.size, selected.ptr)!!)
}

fun menuEntry(label: String): Component =
    Component(ftxui_component_menu_entry(label)!!)

fun menuEntry(label: String, animatedColors: CValue<ftxui_animated_colors_option_t>): Component =
    Component(ftxui_component_menu_entry_animated(label, animatedColors)!!)

fun animatedMenuEntryColors(
    bgActive: Color, bgInactive: Color = Color.Black,
    fgActive: Color = Color.White, fgInactive: Color = bgActive
): CValue<ftxui_animated_colors_option_t> = cValue<ftxui_animated_colors_option_t> {
    background.enabled = true
    background.active = bgActive.handle
    background.inactive = bgInactive.handle
    background.duration_ms = 250
    background.easing_function_type = ftxui_easing_function_type_t.FTXUI_EASING_QUINTIC_IN_OUT
    foreground.enabled = true
    foreground.active = fgActive.handle
    foreground.inactive = fgInactive.handle
    foreground.duration_ms = 250
    foreground.easing_function_type = ftxui_easing_function_type_t.FTXUI_EASING_QUINTIC_IN_OUT
}

fun menuHorizontal(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_menu_horizontal(ptrs, entries.size, selected.ptr)!!)
}

private val separatorFuncBridge = staticCFunction<COpaquePointer?, ftxui_element_handle_t?> { userdata ->
    userdata!!.asStableRef<() -> Element>().get()().handle
}

fun resizableSplit(
    main: Component,
    back: Component,
    mainSize: IntState,
    direction: Direction,
    minSize: IntState? = null,
    maxSize: IntState? = null,
    separator: (() -> Element)? = null
): Component {
    val ref = separator?.let { StableRef.create(it) }
    val option = cValue<ftxui_resizable_split_option_t> {
        this.main = main.handle
        this.back = back.handle
        this.direction = direction.value
        this.main_size = mainSize.ptr
        this.min_size = minSize?.ptr
        this.max_size = maxSize?.ptr
        if (ref != null) {
            this.separator_func = separatorFuncBridge
            this.separator_userdata = ref.asCPointer()
        }
    }
    val handle = ftxui_component_resizable_split_opt(option)!!
    return wrapOwning(main, back, handle)
}

fun gridbox(rows: List<List<Element>>): Element = memScoped {
    val flat = rows.flatten()
    val cells = allocArray<ftxui_element_handle_tVar>(flat.size)
    flat.forEachIndexed { i, el -> cells[i] = el.handle }
    val rowLengths = allocArray<IntVar>(rows.size)
    rows.forEachIndexed { i, row -> rowLengths[i] = row.size }
    Element(ftxui_element_gridbox(cells, flat.size, rowLengths, rows.size)!!)
}

fun dropdown(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_dropdown(ptrs, entries.size, selected.ptr)!!)
}

fun dropdownCustom(
    entries: List<String>,
    selected: IntState? = null,
    transform: ((open: Boolean, checkbox: Element, radiobox: Element) -> Element)? = null,
    entryTransform: ((EntryState) -> Element)? = null,
): Component {
    val transformRef = transform?.let { StableRef.create(it) }
    val transformCb: ftxui_dropdown_transform_callback_t? = if (transform != null) {
        staticCFunction { open: Boolean, cbHandle: ftxui_element_handle_t?, rbHandle: ftxui_element_handle_t?, refPtr: COpaquePointer? ->
            val block = refPtr!!.asStableRef<(Boolean, Element, Element) -> Element>().get()
            block(open, Element(cbHandle!!), Element(rbHandle!!)).handle
        } as ftxui_dropdown_transform_callback_t
    } else null

    val entryTransformRef = entryTransform?.let { StableRef.create(it) }
    val entryTransformCb: ftxui_button_transform_t? = if (entryTransform != null) {
        staticCFunction { state: CValue<ftxui_entry_state_t>, refPtr: COpaquePointer? ->
            state.useContents {
                val block = refPtr!!.asStableRef<(EntryState) -> Element>().get()
                block(EntryState(
                    label = this.label?.toKString() ?: "",
                    state = this.state,
                    active = this.active,
                    focused = this.focused,
                    index = this.index
                )).handle
            }
        } as ftxui_button_transform_t
    } else null

    val handle = memScoped {
        val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
        entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
        ftxui_component_dropdown_custom(
            ptrs, entries.size, selected?.ptr,
            transformCb, transformRef?.asCPointer(),
            entryTransformCb, entryTransformRef?.asCPointer(),
        )!!
    }

    return Component(handle).also { c ->
        transformRef?.let { c.cleanups.add { it.dispose() } }
        entryTransformRef?.let { c.cleanups.add { it.dispose() } }
    }
}

fun tab(selected: IntState): ContainerComponent =
    ContainerComponent(ftxui_component_container_tab(selected.ptr)!!)

fun stacked(): ContainerComponent =
    ContainerComponent(ftxui_component_container_stacked()!!)

fun resizableSplitLeft(main: Component, back: Component, mainSize: IntState): Component =
    wrapOwning(main, back, ftxui_component_resizable_split_left(main.handle, back.handle, mainSize.ptr)!!)

fun resizableSplitRight(main: Component, back: Component, mainSize: IntState): Component =
    wrapOwning(main, back, ftxui_component_resizable_split_right(main.handle, back.handle, mainSize.ptr)!!)

fun resizableSplitTop(main: Component, back: Component, mainSize: IntState): Component =
    wrapOwning(main, back, ftxui_component_resizable_split_top(main.handle, back.handle, mainSize.ptr)!!)

fun resizableSplitBottom(main: Component, back: Component, mainSize: IntState): Component =
    wrapOwning(main, back, ftxui_component_resizable_split_bottom(main.handle, back.handle, mainSize.ptr)!!)

fun collapsible(label: String, child: Component, show: BoolState): Component =
    child.wrapOwning(ftxui_component_collapsible(label, child.handle, show.ptr)!!)

fun maybe(child: Component, show: BoolState): Component =
    child.wrapOwning(ftxui_component_maybe(child.handle, show.ptr)!!)

fun modal(main: Component, modal: Component, showModal: BoolState): Component =
    wrapOwning(main, modal, ftxui_component_modal(main.handle, modal.handle, showModal.ptr)!!)

// -- Property-ref overloads
// These accept a KMutableProperty0<T> (e.g. ::myVar) instead of an IntState/BoolState.
// The native buffer is managed internally and freed when the component is destroyed.
// No manual state management required.

fun checkbox(label: String, checked: KMutableProperty0<Boolean>): Component =
    boolStateSynced(checked.get(), checked) { ptr ->
        Component(ftxui_component_checkbox(label, ptr)!!)
    }

fun toggle(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_toggle(ptrs, entries.size, ptr)!!)
        }
    }

fun slider(label: String, value: KMutableProperty0<Int>, min: Int, max: Int, increment: Int = 1): Component =
    intStateSynced(value.get(), value) { ptr ->
        Component(ftxui_component_slider(label, ptr, min, max, increment)!!)
    }

fun radiobox(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_radiobox(ptrs, entries.size, ptr)!!)
        }
    }

fun menu(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_menu(ptrs, entries.size, ptr)!!)
        }
    }

fun dropdown(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_dropdown(ptrs, entries.size, ptr)!!)
        }
    }

fun collapsible(label: String, child: Component, show: KMutableProperty0<Boolean>): Component =
    boolStateSynced(show.get(), show) { ptr ->
        child.wrapOwning(ftxui_component_collapsible(label, child.handle, ptr)!!)
    }

fun maybe(child: Component, show: KMutableProperty0<Boolean>): Component =
    boolStateSynced(show.get(), show) { ptr ->
        child.wrapOwning(ftxui_component_maybe(child.handle, ptr)!!)
    }

fun modal(main: Component, modal: Component, showModal: KMutableProperty0<Boolean>): Component =
    boolStateSynced(showModal.get(), showModal) { ptr ->
        wrapOwning(main, modal, ftxui_component_modal(main.handle, modal.handle, ptr)!!)
    }

fun Component.maybe(show: BoolState) = maybe(this, show)
fun Component.maybe(show: KMutableProperty0<Boolean>) = maybe(this, show)
fun Component.modal(modal: Component, showModal: BoolState) = modal(this, modal, showModal)
fun Component.modal(modal: Component, showModal: KMutableProperty0<Boolean>) = modal(this, modal, showModal)

fun resizableSplitLeft(main: Component, back: Component, mainSize: KMutableProperty0<Int>): Component =
    intStateSynced(mainSize.get(), mainSize) { ptr ->
        wrapOwning(main, back, ftxui_component_resizable_split_left(main.handle, back.handle, ptr)!!)
    }

fun resizableSplitRight(main: Component, back: Component, mainSize: KMutableProperty0<Int>): Component =
    intStateSynced(mainSize.get(), mainSize) { ptr ->
        wrapOwning(main, back, ftxui_component_resizable_split_right(main.handle, back.handle, ptr)!!)
    }

fun resizableSplitTop(main: Component, back: Component, mainSize: KMutableProperty0<Int>): Component =
    intStateSynced(mainSize.get(), mainSize) { ptr ->
        wrapOwning(main, back, ftxui_component_resizable_split_top(main.handle, back.handle, ptr)!!)
    }

fun resizableSplitBottom(main: Component, back: Component, mainSize: KMutableProperty0<Int>): Component =
    intStateSynced(mainSize.get(), mainSize) { ptr ->
        wrapOwning(main, back, ftxui_component_resizable_split_bottom(main.handle, back.handle, ptr)!!)
    }

fun poll(app: FtxUIApp, onPoll: () -> Unit): Component {
    val stableRef = StableRef.create(onPoll)
    val callback = staticCFunction { refPtr: COpaquePointer? ->
        refPtr?.asStableRef<() -> Unit>()?.get()?.invoke()
        Unit
    }
    return Component(ftxui_component_poll(app.handle, callback, stableRef.asCPointer())!!).also {
        it.cleanups.add { stableRef.dispose() }
    }
}

fun FtxUIApp.requestAnimationFrame() = ftxui_app_request_animation_frame(handle)

// -- Animated menus

fun menuHorizontalAnimated(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_menu_horizontal_animated(ptrs, entries.size, selected.ptr)!!)
}

fun menuToggle(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_menu_toggle(ptrs, entries.size, selected.ptr)!!)
}

fun menuHorizontalAnimated(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_menu_horizontal_animated(ptrs, entries.size, ptr)!!)
        }
    }

fun menuToggle(entries: List<String>, selected: KMutableProperty0<Int>): Component =
    intStateSynced(selected.get(), selected) { ptr ->
        memScoped {
            val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
            entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
            Component(ftxui_component_menu_toggle(ptrs, entries.size, ptr)!!)
        }
    }

// -- Canvas

class Canvas internal constructor(private val handle: ftxui_canvas_handle_t) {
    fun destroy() = ftxui_canvas_destroy(handle)

    fun drawText(x: Int, y: Int, text: String) = ftxui_canvas_draw_text(handle, x, y, text)
    fun drawText(x: Int, y: Int, text: String, color: Color) =
        ftxui_canvas_draw_text_color(handle, x, y, text, color.handle)

    fun drawPointLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Color? = null) =
        ftxui_canvas_draw_point_line(handle, x1, y1, x2, y2, color?.handle)
    fun drawBlockLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Color? = null) =
        ftxui_canvas_draw_block_line(handle, x1, y1, x2, y2, color?.handle)

    fun drawPointCircle(x: Int, y: Int, radius: Int) = ftxui_canvas_draw_point_circle(handle, x, y, radius)
    fun drawPointCircleFilled(x: Int, y: Int, radius: Int) = ftxui_canvas_draw_point_circle_filled(handle, x, y, radius)
    fun drawBlockCircle(x: Int, y: Int, radius: Int) = ftxui_canvas_draw_block_circle(handle, x, y, radius)
    fun drawBlockCircleFilled(x: Int, y: Int, radius: Int) = ftxui_canvas_draw_block_circle_filled(handle, x, y, radius)

    fun drawPointEllipse(x: Int, y: Int, rx: Int, ry: Int) = ftxui_canvas_draw_point_ellipse(handle, x, y, rx, ry)
    fun drawPointEllipseFilled(x: Int, y: Int, rx: Int, ry: Int) = ftxui_canvas_draw_point_ellipse_filled(handle, x, y, rx, ry)
    fun drawBlockEllipse(x: Int, y: Int, rx: Int, ry: Int) = ftxui_canvas_draw_block_ellipse(handle, x, y, rx, ry)
    fun drawBlockEllipseFilled(x: Int, y: Int, rx: Int, ry: Int) = ftxui_canvas_draw_block_ellipse_filled(handle, x, y, rx, ry)

    fun toElement(): Element = Element(ftxui_element_canvas_ref(handle)!!)

    companion object {
        operator fun invoke(width: Int, height: Int) = Canvas(ftxui_canvas_create(width, height)!!)
    }
}

// -- graph element
// GraphFn wraps a graph callback and keeps the StableRef alive.
// Create once and keep alive as long as graph() elements using it are rendered.
class GraphFn(fn: (width: Int, height: Int, output: IntArray) -> Unit) {
    private val stableRef = StableRef.create(fn)
    internal val cCallback: ftxui_graph_callback_t = staticCFunction { w: Int, h: Int, out: CPointer<IntVar>?, refPtr: COpaquePointer? ->
        val block = refPtr!!.asStableRef<(Int, Int, IntArray) -> Unit>().get()
        val arr = IntArray(w)
        block(w, h, arr)
        for (i in 0 until w) out!![i] = arr[i]
    }
    internal val userData get() = stableRef.asCPointer()
    fun destroy() = stableRef.dispose()
}

fun graph(fn: GraphFn): Element = Element(ftxui_element_graph(fn.cCallback, fn.userData)!!)

// -- LinearGradient

class LinearGradient internal constructor(internal val handle: ftxui_linear_gradient_handle_t) {
    fun destroy() = ftxui_linear_gradient_destroy(handle)

    fun angle(degrees: Float): LinearGradient { ftxui_linear_gradient_angle(handle, degrees); return this }
    fun stop(color: Color): LinearGradient { ftxui_linear_gradient_stop(handle, color.handle); return this }
    fun stop(color: Color, position: Float): LinearGradient { ftxui_linear_gradient_stop_at(handle, color.handle, position); return this }

    companion object {
        operator fun invoke() = LinearGradient(ftxui_linear_gradient_create()!!)
    }
}

fun Element.bgcolorLinearGradient(gradient: LinearGradient): Element =
    Element(ftxui_element_bgcolor_linear_gradient(handle, gradient.handle)!!)

fun Element.colorLinearGradient(gradient: LinearGradient): Element =
    Element(ftxui_element_color_linear_gradient(handle, gradient.handle)!!)

// -- flexbox element

enum class FlexboxDirection(internal val value: ftxui_flexbox_direction_t) {
    Row(ftxui_flexbox_direction_t.FTXUI_FLEXBOX_DIRECTION_ROW),
    RowInversed(ftxui_flexbox_direction_t.FTXUI_FLEXBOX_DIRECTION_ROW_INVERSED),
    Column(ftxui_flexbox_direction_t.FTXUI_FLEXBOX_DIRECTION_COLUMN),
    ColumnInversed(ftxui_flexbox_direction_t.FTXUI_FLEXBOX_DIRECTION_COLUMN_INVERSED),
}

enum class FlexboxWrap(internal val value: ftxui_flexbox_wrap_t) {
    NoWrap(ftxui_flexbox_wrap_t.FTXUI_FLEXBOX_WRAP_NO_WRAP),
    Wrap(ftxui_flexbox_wrap_t.FTXUI_FLEXBOX_WRAP_WRAP),
    WrapInversed(ftxui_flexbox_wrap_t.FTXUI_FLEXBOX_WRAP_WRAP_INVERSED),
}

enum class FlexboxJustify(internal val value: ftxui_flexbox_justify_t) {
    FlexStart(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_FLEX_START),
    FlexEnd(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_FLEX_END),
    Center(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_CENTER),
    Stretch(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_STRETCH),
    SpaceBetween(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_SPACE_BETWEEN),
    SpaceAround(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_SPACE_AROUND),
    SpaceEvenly(ftxui_flexbox_justify_t.FTXUI_FLEXBOX_JUSTIFY_SPACE_EVENLY),
}

enum class FlexboxAlignItems(internal val value: ftxui_flexbox_align_items_t) {
    FlexStart(ftxui_flexbox_align_items_t.FTXUI_FLEXBOX_ALIGN_ITEMS_FLEX_START),
    FlexEnd(ftxui_flexbox_align_items_t.FTXUI_FLEXBOX_ALIGN_ITEMS_FLEX_END),
    Center(ftxui_flexbox_align_items_t.FTXUI_FLEXBOX_ALIGN_ITEMS_CENTER),
    Stretch(ftxui_flexbox_align_items_t.FTXUI_FLEXBOX_ALIGN_ITEMS_STRETCH),
}

enum class FlexboxAlignContent(internal val value: ftxui_flexbox_align_content_t) {
    FlexStart(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_FLEX_START),
    FlexEnd(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_FLEX_END),
    Center(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_CENTER),
    Stretch(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_STRETCH),
    SpaceBetween(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_BETWEEN),
    SpaceAround(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_AROUND),
    SpaceEvenly(ftxui_flexbox_align_content_t.FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_EVENLY),
}

data class FlexboxConfig(
    val direction: FlexboxDirection = FlexboxDirection.Row,
    val wrap: FlexboxWrap = FlexboxWrap.Wrap,
    val justifyContent: FlexboxJustify = FlexboxJustify.FlexStart,
    val alignItems: FlexboxAlignItems = FlexboxAlignItems.Stretch,
    val alignContent: FlexboxAlignContent = FlexboxAlignContent.FlexStart,
    val gapX: Int = 0,
    val gapY: Int = 0,
)

fun flexbox(vararg elements: Element, config: FlexboxConfig = FlexboxConfig()): Element = memScoped {
    val array = allocArray<ftxui_element_handle_tVar>(elements.size)
    elements.forEachIndexed { i, el -> array[i] = el.handle }
    val cConfig = cValue<ftxui_flexbox_config_t> {
        direction = config.direction.value
        wrap = config.wrap.value
        justify_content = config.justifyContent.value
        align_items = config.alignItems.value
        align_content = config.alignContent.value
        gap_x = config.gapX
        gap_y = config.gapY
    }
    Element(ftxui_element_flexbox(array, elements.size, cConfig)!!)
}

// -- Table

class Table internal constructor(private val handle: ftxui_table_handle_t) {
    fun destroy() = ftxui_table_destroy(handle)
    fun render(): Element = Element(ftxui_table_render(handle)!!)

    fun selectAll() = TableSelection(ftxui_table_select_all(handle)!!)
    fun selectRow(row: Int) = TableSelection(ftxui_table_select_row(handle, row)!!)
    fun selectRows(from: Int, to: Int) = TableSelection(ftxui_table_select_rows(handle, from, to)!!)
    fun selectColumn(col: Int) = TableSelection(ftxui_table_select_column(handle, col)!!)
    fun selectCell(col: Int, row: Int) = TableSelection(ftxui_table_select_cell(handle, col, row)!!)

    companion object {
        operator fun invoke(rows: List<List<String>>): Table = memScoped {
            val numRows = rows.size
            val numCols = rows.maxOfOrNull { it.size } ?: 0
            val flat = allocArray<CPointerVar<ByteVar>>(numRows * numCols)
            rows.forEachIndexed { r, row ->
                row.forEachIndexed { c, cell -> flat[r * numCols + c] = cell.cstr.getPointer(this) }
                for (c in row.size until numCols) flat[r * numCols + c] = "".cstr.getPointer(this)
            }
            Table(ftxui_table_create(flat, numRows, numCols)!!)
        }
    }
}

class TableSelection internal constructor(private val handle: ftxui_table_selection_handle_t) {
    fun destroy() = ftxui_table_selection_destroy(handle)

    fun border(style: BorderStyle = BorderStyle.Light) = apply { ftxui_table_selection_border(handle, style.value) }
    fun borderColor(style: BorderStyle, color: Color) = apply { ftxui_table_selection_border_color(handle, style.value, color.handle) }
    fun separatorVertical(style: BorderStyle = BorderStyle.Light) = apply { ftxui_table_selection_separator_vertical(handle, style.value) }
    fun decorateBold() = apply { ftxui_table_selection_decorate_bold(handle) }
    fun decorateCellsAlignRight() = apply { ftxui_table_selection_decorate_cells_align_right(handle) }
    fun decorateCellsColor(color: Color) = apply { ftxui_table_selection_decorate_cells_color(handle, color.handle) }
    fun decorateCellsColorAlternateRow(color: Color, modulo: Int, offset: Int) =
        apply { ftxui_table_selection_decorate_cells_color_alternate_row(handle, color.handle, modulo, offset) }
}

// -- Window component

class WindowOptions(
    val inner: Component? = null,
    val title: String? = null,
    val left: IntState? = null,
    val top: IntState? = null,
    val width: IntState? = null,
    val height: IntState? = null,
    val leftDefault: Int = 0,
    val topDefault: Int = 0,
    val widthDefault: Int = 20,
    val heightDefault: Int = 10,
)

fun windowComponent(options: WindowOptions): Component = memScoped {
    val opts = cValue<ftxui_window_options_t> {
        this.inner = options.inner?.handle
        this.title = options.title?.cstr?.getPointer(this@memScoped)
        this.left = options.left?.ptr
        this.top = options.top?.ptr
        this.width = options.width?.ptr
        this.height = options.height?.ptr
        this.left_default = options.leftDefault
        this.top_default = options.topDefault
        this.width_default = options.widthDefault
        this.height_default = options.heightDefault
    }
    Component(ftxui_component_window(opts)!!)
}

// -- Loop

class FtxUILoop internal constructor(
    private val loopHandle: ftxui_loop_handle_t,
    val app: FtxUIApp,
) {
    fun hasQuitted(): Boolean = ftxui_loop_has_quitted(loopHandle)
    fun runOnce() = ftxui_loop_run_once(loopHandle)
    fun destroy() = ftxui_loop_destroy(loopHandle)

    companion object {
        operator fun invoke(app: FtxUIApp, component: Component): FtxUILoop =
            FtxUILoop(ftxui_loop_create(app.handle, component.handle)!!, app)
    }
}

// -- ColorInfo

data class ColorInfo(val index256: Int, val name: String)

fun colorInfoSorted2D(): List<List<ColorInfo>> = memScoped {
    val rows = alloc<IntVar>()
    val cols = alloc<IntVar>()
    val data = ftxui_color_info_sorted_2d(rows.ptr, cols.ptr) ?: return emptyList()
    val numRows = rows.value
    val numCols = cols.value
    val result = (0 until numRows).map { r ->
        (0 until numCols).mapNotNull { c ->
            val entry = data[r * numCols + c]
            if (entry.index_256 == -1) null
            else ColorInfo(entry.index_256, entry.name?.toKString() ?: "")
        }
    }
    ftxui_color_info_free(data)
    result
}