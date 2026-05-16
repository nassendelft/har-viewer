@file:OptIn(ExperimentalForeignApi::class)

package nl.ncaj

import ftxui_c.*
import kotlinx.cinterop.ExperimentalForeignApi

@DslMarker
annotation class FtxUIDsl

// Define the component handle type alias for clarity
typealias Component = ftxui_component_handle_t

typealias Modifier = Component.() -> Component

// Utility to manage component lifecycle
fun destroyComponent(component: Component) {
    ftxui_component_destroy(component)
}

@FtxUIDsl
class FtxUIBuilder internal constructor() {

    fun text(value: String, modifier: Modifier = { this }): Component =
        ftxui_component_text(value)!!.let(modifier)

    fun gauge(value: Double, modifier: Modifier = { this }): Component =
        ftxui_component_gauge(value)!!.let(modifier)

    fun hbox(block: HBoxBuilder.() -> Unit): Component =
        HBoxBuilder().apply(block).build()

    fun vbox(block: VBoxBuilder.() -> Unit): Component =
        VBoxBuilder().apply(block).build()

    internal fun build(component: Component) {
        val app = ftxui_app_create_fullscreen()
        ftxui_app_loop(app, component)
        ftxui_app_destroy(app)
    }
}

fun ftxui(block: FtxUIBuilder.() -> Component) {
    val builder = FtxUIBuilder()
    val component = builder.block()
    builder.build(component)
}

// --- Decorators (Chainable) ---

fun Component.border(): Component =
    ftxui_component_border(this)!!

fun Component.flex(): Component =
    ftxui_component_flex(this)!!

fun Component.color(color: ftxui_color_t): Component =
    ftxui_component_color(this, color)!!

// --- Base class for container builders ---

@FtxUIDsl
abstract class BaseContainerBuilder internal constructor() {
    protected val components = mutableListOf<Component>()

    internal fun add(component: Component) {
        components.add(component)
    }

    internal abstract fun build(): Component

    fun text(value: String, modifier: Modifier = { this }): Component {
        val component = ftxui_component_text(value)!!.let(modifier)
        this.add(component)
        return component
    }

    fun gauge(value: Double, modifier: Modifier = { this }): Component {
        val component = ftxui_component_gauge(value)!!.let(modifier)
        this.add(component)
        return component
    }

    fun hbox(block: HBoxBuilder.() -> Unit): Component {
        val component = HBoxBuilder().apply(block).build()
        this.add(component)
        return component
    }

    fun vbox(block: VBoxBuilder.() -> Unit): Component {
        val component = VBoxBuilder().apply(block).build()
        this.add(component)
        return component
    }
}

// --- Context builders for VBox and HBox --

@FtxUIDsl
class VBoxBuilder internal constructor() : BaseContainerBuilder() {
    override fun build(): Component {
        val container = ftxui_component_container_vertical()!!
        for (component in components) {
            ftxui_container_add(container, component)
        }
        return container
    }
}

@FtxUIDsl
class HBoxBuilder internal constructor() : BaseContainerBuilder() {
    override fun build(): Component {
        val container = ftxui_component_container_horizontal()!!
        for (component in components) {
            ftxui_container_add(container, component)
        }
        return container
    }
}
