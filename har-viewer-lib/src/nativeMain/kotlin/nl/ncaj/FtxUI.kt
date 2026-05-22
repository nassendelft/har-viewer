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

        fun rgb(r: UByte, g: UByte, b: UByte) = Color(ftxui_color_rgb(r, g, b))
        fun rgba(r: UByte, g: UByte, b: UByte, a: UByte) = Color(ftxui_color_rgba(r, g, b, a))
        fun hsv(h: UByte, s: UByte, v: UByte) = Color(ftxui_color_hsv(h, s, v))
        fun hsva(h: UByte, s: UByte, v: UByte, a: UByte) = Color(ftxui_color_hsva(h, s, v, a))
        fun palette1(index: ftxui_palette1_t) = Color(ftxui_color_palette1(index))
        fun palette16(index: ftxui_palette16_t) = Color(ftxui_color_palette16(index))
        fun palette256(index: ftxui_palette256_t) = Color(ftxui_color_palette256(index))

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

class FtxUIApp internal constructor(internal val handle: ftxui_app_handle_t) {
    fun loop(root: Component) = ftxui_app_loop(handle, root.handle)

    fun exit() = ftxui_app_exit(handle)

    fun destroy() = ftxui_app_destroy(handle)

    companion object {
        fun fullscreen() = FtxUIApp(ftxui_app_create_fullscreen()!!)
        fun fitComponent() = FtxUIApp(ftxui_app_create_fit_component()!!)
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

fun Component.render() = Element(ftxui_component_render(this.handle)!!)

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

// -- Additional components

fun checkbox(label: String, checked: BoolState): Component =
    Component(ftxui_component_checkbox(label, checked.ptr)!!)

fun toggle(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_toggle(ptrs, entries.size, selected.ptr)!!)
}

fun slider(label: String, value: IntState, min: Int, max: Int, increment: Int = 1): Component =
    Component(ftxui_component_slider(label, value.ptr, min, max, increment)!!)

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

fun dropdown(entries: List<String>, selected: IntState): Component = memScoped {
    val ptrs = allocArray<CPointerVar<ByteVar>>(entries.size)
    entries.forEachIndexed { i, s -> ptrs[i] = s.cstr.getPointer(this) }
    Component(ftxui_component_dropdown(ptrs, entries.size, selected.ptr)!!)
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

// -- Property-ref overloads (Option B state binding)
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
