#ifndef FTXUI_C_API_H
#define FTXUI_C_API_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handles
typedef void* ftxui_app_handle_t;
typedef void* ftxui_component_handle_t;
typedef void* ftxui_element_handle_t;

/**
 * @brief A callback function for rendering a component.
 * 
 * @return ftxui_element_handle_t The element to render.
 */
typedef ftxui_element_handle_t (*ftxui_render_callback_t)(void* userdata);

// --- Colors ---

typedef enum {
    FTXUI_COLOR_BLACK,
    FTXUI_COLOR_RED,
    FTXUI_COLOR_GREEN,
    FTXUI_COLOR_YELLOW,
    FTXUI_COLOR_BLUE,
    FTXUI_COLOR_MAGENTA,
    FTXUI_COLOR_CYAN,
    FTXUI_COLOR_WHITE,
    FTXUI_COLOR_DEFAULT,
    FTXUI_COLOR_GRAY_LIGHT,
    FTXUI_COLOR_GRAY_DARK,
} ftxui_color_t;

typedef struct {
    bool enabled;
    ftxui_color_t inactive;
    ftxui_color_t active;
} ftxui_animated_color_option_t;

typedef struct {
    ftxui_animated_color_option_t background;
    ftxui_animated_color_option_t foreground;
} ftxui_animated_colors_option_t;

typedef struct {
    const char* label;
    bool state;
    bool active;
    bool focused;
    int index;
} ftxui_entry_state_t;

typedef ftxui_element_handle_t (*ftxui_button_transform_t)(ftxui_entry_state_t state, void* userdata);

typedef struct {
    ftxui_animated_colors_option_t animated_colors;
    ftxui_button_transform_t transform;
    void* transform_userdata;
} ftxui_button_option_t;

typedef enum {
    FTXUI_BORDER_STYLE_LIGHT,
    FTXUI_BORDER_STYLE_DASHED,
    FTXUI_BORDER_STYLE_HEAVY,
    FTXUI_BORDER_STYLE_DOUBLE,
    FTXUI_BORDER_STYLE_ROUNDED,
    FTXUI_BORDER_STYLE_EMPTY,
} ftxui_border_style_t;

/**
 * @brief Initializes the FTXUI interactive application (ScreenInteractive).
 * 
 * @return ftxui_app_handle_t A handle to the initialized app, or NULL on failure.
 */
ftxui_app_handle_t ftxui_app_create_fullscreen();

/**
 * @brief Initializes the FTXUI interactive application (ScreenInteractive) to fit the component.
 *
 * @return ftxui_app_handle_t A handle to the initialized app, or NULL on failure.
 */
ftxui_app_handle_t ftxui_app_create_fit_component();

/**
 * @brief Creates a simple text element.
 * 
 * @param text The text to display.
 * @return ftxui_element_handle_t The element handle.
 */
ftxui_element_handle_t ftxui_element_text(const char* text);

/**
 * @brief Runs the main loop for the FTXUI app.
 * 
 * @param app The app handle.
 * @param component The root component to display.
 */
void ftxui_app_loop(ftxui_app_handle_t app, ftxui_component_handle_t component);

/**
 * @brief Creates a component that calls a function periodically.
 * 
 * @param app The app handle.
 * @param on_poll The callback function to call.
 * @return ftxui_component_handle_t The component handle.
 */
ftxui_component_handle_t ftxui_component_poll(ftxui_app_handle_t app, void (*on_poll)(void*), void* userdata);

/**
 * @brief Requests the app to exit.
 * 
 * @param app The app handle.
 */
void ftxui_app_exit(ftxui_app_handle_t app);

/**
 * @brief Cleans up and destroys the FTXUI app.
 * 
 * @param app The app handle to destroy.
 */
void ftxui_app_destroy(ftxui_app_handle_t app);

/**
 * @brief Cleans up and destroys a component.
 * 
 * @param component The component handle to destroy.
 */
void ftxui_component_destroy(ftxui_component_handle_t component);

/**
 * @brief Creates a component with a custom render function.
 * 
 * @param component The inner component to wrap (optional).
 * @param callback The callback function to call for rendering.
 * @return ftxui_component_handle_t The component handle.
 */
ftxui_component_handle_t ftxui_component_renderer(ftxui_component_handle_t component, ftxui_render_callback_t callback, void* userdata);

/**
 * @brief Destroys an element.
 * 
 * @param element The element handle to destroy.
 */
void ftxui_element_destroy(ftxui_element_handle_t element);

// --- Element Creation (for use in ftxui_render_callback_t) ---

/**
 * @brief Creates a vbox element.
 * 
 * @param elements An array of element handles.
 * @param count The number of elements.
 * @return ftxui_element_handle_t The element handle.
 */
ftxui_element_handle_t ftxui_element_vbox(ftxui_element_handle_t* elements, int count);

/**
 * @brief Creates an hbox element.
 * 
 * @param elements An array of element handles.
 * @param count The number of elements.
 * @return ftxui_element_handle_t The element handle.
 */
ftxui_element_handle_t ftxui_element_hbox(ftxui_element_handle_t* elements, int count);

/**
 * @brief Creates a gauge element.
 *
 * @param value The value of the gauge (0.0 to 1.0).
 * @return ftxui_element_handle_t The gauge element handle.
 */
ftxui_element_handle_t ftxui_element_gauge(double value);

/**
 * @brief Creates a separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator();

/**
 * @brief Creates a light separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_light();

/**
 * @brief Creates a dashed separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_dashed();

/**
 * @brief Creates a heavy separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_heavy();

/**
 * @brief Creates a double separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_double();

/**
 * @brief Creates an empty separator element.
 *
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_empty();

/**
 * @brief Creates a styled separator element.
 *
 * @param style The border style to use.
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_styled(ftxui_border_style_t style);

/**
 * @brief Creates a separator element with a custom character.
 *
 * @param character The character to use for the separator.
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_character(const char* character);

/**
 * @brief Creates a horizontal selector separator.
 *
 * @param left The left position.
 * @param right The right position.
 * @param unselected_color The color when not selected.
 * @param selected_color The color when selected.
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_hselector(float left, float right, ftxui_color_t unselected_color, ftxui_color_t selected_color);

/**
 * @brief Creates a vertical selector separator.
 *
 * @param up The up position.
 * @param down The down position.
 * @param unselected_color The color when not selected.
 * @param selected_color The color when selected.
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_vselector(float up, float down, ftxui_color_t unselected_color, ftxui_color_t selected_color);

/**
 * @brief Wraps an element with a window.
 * 
 * @param title The title element.
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element wrapped in a window.
 */
ftxui_element_handle_t ftxui_element_window(ftxui_element_handle_t title, ftxui_element_handle_t element);


// --- Decorators ---

/**
 * @brief Wraps an element with a border.
 * 
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a border.
 */
ftxui_element_handle_t ftxui_element_border(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with a light border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a light border.
 */
ftxui_element_handle_t ftxui_element_border_light(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with a dashed border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a dashed border.
 */
ftxui_element_handle_t ftxui_element_border_dashed(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with a heavy border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a heavy border.
 */
ftxui_element_handle_t ftxui_element_border_heavy(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with a double border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a double border.
 */
ftxui_element_handle_t ftxui_element_border_double(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with a rounded border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with a rounded border.
 */
ftxui_element_handle_t ftxui_element_border_rounded(ftxui_element_handle_t element);

/**
 * @brief Wraps an element with an empty border.
 *
 * @param element The element to wrap.
 * @return ftxui_element_handle_t A new element with an empty border.
 */
ftxui_element_handle_t ftxui_element_border_empty(ftxui_element_handle_t element);

/**
 * @brief Makes an element flexible, allowing it to expand or shrink.
 *
 * @param element The element to make flexible.
 * @return ftxui_element_handle_t A new element with flex properties.
 */
ftxui_element_handle_t ftxui_element_flex(ftxui_element_handle_t element);

/**
 * @brief Makes an element bold.
 *
 * @param element The element to make bold.
 * @return ftxui_element_handle_t A new element with bold properties.
 */
ftxui_element_handle_t ftxui_element_bold(ftxui_element_handle_t element);

/**
 * @brief Makes an element inverted.
 *
 * @param element The element to make inverted.
 * @return ftxui_element_handle_t A new element with inverted properties.
 */
ftxui_element_handle_t ftxui_element_inverted(ftxui_element_handle_t element);

/**
 * @brief Makes an element underlined.
 *
 * @param element The element to make underlined.
 * @return ftxui_element_handle_t A new element with underlined properties.
 */
ftxui_element_handle_t ftxui_element_underlined(ftxui_element_handle_t element);

/**
 * @brief Sets the foreground color of an element.
 *
 * @param element The element to color.
 * @param color The color to apply.
 * @return ftxui_element_handle_t A new element with the specified color.
 */
ftxui_element_handle_t ftxui_element_color(ftxui_element_handle_t element, ftxui_color_t color);

// --- Util ---

/**
 * @brief Centers an element horizontally.
 *
 * @param element The element to center.
 * @return ftxui_element_handle_t A new element centered horizontally.
 */
ftxui_element_handle_t ftxui_element_hcenter(ftxui_element_handle_t element);

/**
 * @brief Centers an element vertically.
 *
 * @param element The element to center.
 * @return ftxui_element_handle_t A new element centered vertically.
 */
ftxui_element_handle_t ftxui_element_vcenter(ftxui_element_handle_t element);

/**
 * @brief Centers an element both horizontally and vertically.
 *
 * @param element The element to center.
 * @return ftxui_element_handle_t A new element centered.
 */
ftxui_element_handle_t ftxui_element_center(ftxui_element_handle_t element);

/**
 * @brief Aligns an element to the right.
 *
 * @param element The element to align.
 * @return ftxui_element_handle_t A new element aligned to the right.
 */
ftxui_element_handle_t ftxui_element_align_right(ftxui_element_handle_t element);

/**
 * @brief Creates an element that does nothing, effectively hiding the child.
 *
 * @param element The element to hide.
 * @return ftxui_element_handle_t An empty element.
 */
ftxui_element_handle_t ftxui_element_nothing(ftxui_element_handle_t element);

// --- Component Creation ---

/**
 * @brief Creates a button component.
 * 
 * @param label The label of the button.
 * @param on_click The callback function when the button is clicked.
 * @return ftxui_component_handle_t The button component handle.
 */
ftxui_component_handle_t ftxui_component_button(const char* label, void (*on_click)(void*), void* userdata);

/**
 * @brief Creates a button component with options.
 * 
 * @param label The label of the button.
 * @param on_click The callback function when the button is clicked.
 * @param options The options for the button.
 * @return ftxui_component_handle_t The button component handle.
 */
ftxui_component_handle_t ftxui_component_button_with_options(const char* label, void (*on_click)(void*), void* userdata, ftxui_button_option_t options);

/**
 * @brief Returns the default button options (Simple).
 * 
 * @return ftxui_button_option_t The default button options.
 */
ftxui_button_option_t ftxui_button_option_simple();

/**
 * @brief Returns the ASCII button options.
 * 
 * @return ftxui_button_option_t The ASCII button options.
 */
ftxui_button_option_t ftxui_button_option_ascii();

/**
 * @brief Returns the border button options.
 * 
 * @return ftxui_button_option_t The border button options.
 */
ftxui_button_option_t ftxui_button_option_border();

/**
 * @brief Returns the animated button options.
 * 
 * @return ftxui_button_option_t The animated button options.
 */
ftxui_button_option_t ftxui_button_option_animated();

/**
 * @brief Returns the animated button options with a specific color.
 * 
 * @param color The color to use for the animation.
 * @return ftxui_button_option_t The animated button options.
 */
ftxui_button_option_t ftxui_button_option_animated_with_color(ftxui_color_t color);

/**
 * @brief Creates a checkbox component.
 * 
 * @param label The label of the checkbox.
 * @param checked A pointer to a boolean that stores the checked state.
 * @return ftxui_component_handle_t The checkbox component handle.
 */
ftxui_component_handle_t ftxui_component_checkbox(const char* label, bool* checked);

/**
 * @brief Creates an input component.
 * 
 * @param content A pointer to a char buffer for the input content.
 * @param placeholder The placeholder text when the input is empty.
 * @return ftxui_component_handle_t The input component handle.
 */
ftxui_component_handle_t ftxui_component_input(char* content, const char* placeholder);

/**
 * @brief Creates a toggle component.
 * 
 * @param entries An array of strings for the toggle entries.
 * @param count The number of entries.
 * @param selected A pointer to an integer that stores the selected index.
 * @return ftxui_component_handle_t The toggle component handle.
 */
ftxui_component_handle_t ftxui_component_toggle(const char** entries, int count, int* selected);

/**
 * @brief Creates a slider component.
 * 
 * @param label The label of the slider.
 * @param value A pointer to an integer that stores the value.
 * @param min The minimum value.
 * @param max The maximum value.
 * @param increment The increment value.
 * @return ftxui_component_handle_t The slider component handle.
 */
ftxui_component_handle_t ftxui_component_slider(const char* label, int* value, int min, int max, int increment);

/**
 * @brief Creates a radiobox component.
 * 
 * @param entries An array of strings for the radiobox entries.
 * @param count The number of entries.
 * @param selected A pointer to an integer that stores the selected index.
 * @return ftxui_component_handle_t The radiobox component handle.
 */
ftxui_component_handle_t ftxui_component_radiobox(const char** entries, int count, int* selected);

/**
 * @brief Creates a vertical container for components.
 * 
 * @return ftxui_component_handle_t The container component.
 */
ftxui_component_handle_t ftxui_component_container_vertical();

/**
 * @brief Creates a horizontal container for components.
 * 
 * @return ftxui_component_handle_t The container component.
 */
ftxui_component_handle_t ftxui_component_container_horizontal();

/**
 * @brief Creates a tab container for components.
 *
 * @param selected A pointer to an integer that stores the selected index.
 * @return ftxui_component_handle_t The container component.
 */
ftxui_component_handle_t ftxui_component_container_tab(int* selected);

/**
 * @brief Creates a stacked container for components.
 * 
 * @return ftxui_component_handle_t The container component.
 */
ftxui_component_handle_t ftxui_component_container_stacked();

/**
 * @brief Creates a menu/list component.
 * 
 * @param entries An array of strings for the menu entries.
 * @param count The number of entries.
 * @param selected A pointer to an integer that will store the selected index.
 * @return ftxui_component_handle_t The menu component handle.
 */
ftxui_component_handle_t ftxui_component_menu(const char** entries, int count, int* selected);

/**
 * @brief Creates a menu entry component.
 * 
 * @param label The label of the menu entry.
 * @return ftxui_component_handle_t The menu entry component handle.
 */
ftxui_component_handle_t ftxui_component_menu_entry(const char* label);

/**
 * @brief Creates a dropdown component.
 * 
 * @param entries An array of strings for the dropdown entries.
 * @param count The number of entries.
 * @param selected A pointer to an integer that will store the selected index.
 * @return ftxui_component_handle_t The dropdown component handle.
 */
ftxui_component_handle_t ftxui_component_dropdown(const char** entries, int count, int* selected);


/**
 * @brief Creates a resizable split left component.
 * 
 * @param main The main component.
 * @param back The background component.
 * @param main_size A pointer to an integer that stores the size of the main component.
 * @return ftxui_component_handle_t The resizable split component handle.
 */
ftxui_component_handle_t ftxui_component_resizable_split_left(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size);

/**
 * @brief Creates a resizable split right component.
 * 
 * @param main The main component.
 * @param back The background component.
 * @param main_size A pointer to an integer that stores the size of the main component.
 * @return ftxui_component_handle_t The resizable split component handle.
 */
ftxui_component_handle_t ftxui_component_resizable_split_right(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size);

/**
 * @brief Creates a resizable split top component.
 * 
 * @param main The main component.
 * @param back The background component.
 * @param main_size A pointer to an integer that stores the size of the main component.
 * @return ftxui_component_handle_t The resizable split component handle.
 */
ftxui_component_handle_t ftxui_component_resizable_split_top(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size);

/**
 * @brief Creates a resizable split bottom component.
 * 
 * @param main The main component.
 * @param back The background component.
 * @param main_size A pointer to an integer that stores the size of the main component.
 * @return ftxui_component_handle_t The resizable split component handle.
 */
ftxui_component_handle_t ftxui_component_resizable_split_bottom(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size);

/**
 * @brief Creates a collapsible component.
 * 
 * @param label The label of the collapsible component.
 * @param child The child component.
 * @param show A pointer to a boolean that stores the visibility state.
 * @return ftxui_component_handle_t The collapsible component handle.
 */
ftxui_component_handle_t ftxui_component_collapsible(const char* label, ftxui_component_handle_t child, bool* show);

/**
 * @brief Creates a maybe component, which is shown only if a condition is met.
 * 
 * @param child The child component.
 * @param show A pointer to a boolean that stores the visibility state.
 * @return ftxui_component_handle_t The maybe component handle.
 */
ftxui_component_handle_t ftxui_component_maybe(ftxui_component_handle_t child, const bool* show);

/**
 * @brief Creates a modal component.
 * 
 * @param main The main component.
 * @param modal The modal component.
 * @param show_modal A pointer to a boolean that stores the visibility state of the modal.
 * @return ftxui_component_handle_t The modal component handle.
 */
ftxui_component_handle_t ftxui_component_modal(ftxui_component_handle_t main, ftxui_component_handle_t modal, const bool* show_modal);

/**
 * @brief Adds a child component to a container.
 * 
 * @param container The container component.
 * @param child The child component to add.
 */
void ftxui_container_add(ftxui_component_handle_t container, ftxui_component_handle_t child);

/**
 * @brief Renders an element.
 *
 * @param element The element to render.
 * @return ftxui_render_handle_t The element handle.
 */
ftxui_element_handle_t ftxui_component_render(ftxui_component_handle_t component);


#ifdef __cplusplus
}
#endif

#endif // FTXUI_C_API_H