package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleSwitcher() {
    data class Example(val name: String, val run: () -> Unit)

    val examples = listOf(
        Example("button") { exampleButton() },
        Example("button_animated") { exampleButtonAnimated() },
        Example("button_in_frame") { exampleButtonInFrame() },
        Example("button_style") { exampleButtonStyle() },
        Example("canvas_animated") { exampleCanvasAnimated() },
        Example("checkbox") { exampleCheckbox() },
        Example("checkbox_in_frame") { exampleCheckboxInFrame() },
        Example("collapsible") { exampleCollapsible() },
        Example("composition") { exampleComposition() },
        Example("custom_loop") { exampleCustomLoop() },
        Example("dom_border") { exampleDomBorder() },
        Example("dom_border_colored") { exampleDomBorderColored() },
        Example("dom_border_style") { exampleDomBorderStyle() },
        Example("dom_canvas") { exampleDomCanvas() },
        Example("dom_color_gallery") { exampleDomColorGallery() },
        Example("dom_color_info_palette256") { exampleDomColorInfoPalette256() },
        Example("dom_color_truecolor_hsv") { exampleDomColorTruecolorHsv() },
        Example("dom_color_truecolor_rgb") { exampleDomColorTruecolorRgb() },
        Example("dom_dbox") { exampleDomDbox() },
        Example("dom_gauge") { exampleDomGauge() },
        Example("dom_gauge_direction") { exampleDomGaugeDirection() },
        Example("dom_graph") { exampleDomGraph() },
        Example("dom_gridbox") { exampleDomGridbox() },
        Example("dom_hflow") { exampleDomHflow() },
        Example("dom_html_like") { exampleDomHtmlLike() },
        Example("dom_linear_gradient") { exampleDomLinearGradient() },
        Example("dom_package_manager") { exampleDomPackageManager() },
        Example("dom_paragraph") { exampleDomParagraph() },
        Example("dom_separator") { exampleDomSeparator() },
        Example("dom_separator_style") { exampleDomSeparatorStyle() },
        Example("dom_size") { exampleDomSize() },
        Example("dom_spinner") { exampleDomSpinner() },
        Example("dom_style_blink") { exampleDomStyleBlink() },
        Example("dom_style_bold") { exampleDomStyleBold() },
        Example("dom_style_color") { exampleDomStyleColor() },
        Example("dom_style_dim") { exampleDomStyleDim() },
        Example("dom_style_gallery") { exampleDomStyleGallery() },
        Example("dom_style_hyperlink") { exampleDomStyleHyperlink() },
        Example("dom_style_inverted") { exampleDomStyleInverted() },
        Example("dom_style_italic") { exampleDomStyleItalic() },
        Example("dom_style_strikethrough") { exampleDomStyleStrikethrough() },
        Example("dom_style_underlined") { exampleDomStyleUnderlined() },
        Example("dom_style_underlined_double") { exampleDomStyleUnderlinedDouble() },
        Example("dom_table") { exampleDomTable() },
        Example("dom_text") { exampleDomText() },
        Example("dom_vbox_hbox") { exampleDomVboxHbox() },
        Example("dom_vflow") { exampleDomVflow() },
        Example("dropdown") { exampleDropdown() },
        Example("dropdown_custom") { exampleDropdownCustom() },
        Example("flexbox_gallery") { exampleFlexboxGallery() },
        Example("focus") { exampleFocus() },
        Example("focus_cursor") { exampleFocusCursor() },
        Example("gallery") { exampleGallery() },
        Example("homescreen") { exampleHomescreen() },
        Example("input") { exampleInput() },
        Example("input_in_frame") { exampleInputInFrame() },
        Example("input_style") { exampleInputStyle() },
        Example("linear_gradient_gallery") { exampleLinearGradientGallery() },
        Example("maybe") { exampleMaybe() },
        Example("menu") { exampleMenu() },
        Example("menu2") { exampleMenu2() },
        Example("menu_entries") { exampleMenuEntries() },
        Example("menu_entries_animated") { exampleMenuEntriesAnimated() },
        Example("menu_in_frame") { exampleMenuInFrame() },
        Example("menu_in_frame_horizontal") { exampleMenuInFrameHorizontal() },
        Example("menu_multiple") { exampleMenuMultiple() },
        Example("menu_style") { exampleMenuStyle() },
        Example("menu_underline_animated_gallery") { exampleMenuUnderlineAnimatedGallery() },
        Example("modal_dialog") { exampleModalDialog() },
        Example("modal_dialog_custom") { exampleModalDialogCustom() },
        Example("nested_screen") { exampleNestedScreen() },
        Example("print_key_press") { examplePrintKeyPress() },
        Example("radiobox") { exampleRadiobox() },
        Example("radiobox_in_frame") { exampleRadioboxInFrame() },
        Example("renderer") { exampleRenderer() },
        Example("resizable_split") { exampleResizableSplit() },
        Example("resizable_split_clamp") { exampleResizableSplitClamp() },
        Example("scrollbar") { exampleScrollbar() },
        Example("selection") { exampleSelection() },
        Example("slider") { exampleSlider() },
        Example("slider_direction") { exampleSliderDirection() },
        Example("slider_rgb") { exampleSliderRgb() },
        Example("tab_horizontal") { exampleTabHorizontal() },
        Example("tab_vertical") { exampleTabVertical() },
        Example("textarea") { exampleTextarea() },
        Example("toggle") { exampleToggle() },
        Example("window") { exampleWindow() },
    )

    val names = examples.map { it.name }
    val selected = IntState(0)

    while (true) {
        var selectedToRun = -1

        val app = FtxUIApp.fullscreen()
        val menuComponent = menu(names, selected)

        val component = renderer(menuComponent) {
            vbox(
                text("FtxUI Examples  ↑↓ navigate · Enter run · q quit").bold(),
                separator(),
                menuComponent.render().frame().flex(),
            ).flex().border()
        }.catchEvent { event ->
            when {
                event.input == "\n" -> {
                    selectedToRun = selected.value
                    app.exit()
                    true
                }
                event.character == "q" -> {
                    app.exit()
                    true
                }
                else -> false
            }
        }

        app.loop(component)
        component.destroy()
        app.destroy()

        if (selectedToRun < 0) break
        examples[selectedToRun].run()
    }

    selected.free()
}