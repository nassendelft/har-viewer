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
} ftxui_color_t;

/**
 * @brief Initializes the FTXUI interactive application (ScreenInteractive).
 * 
 * @return ftxui_app_handle_t A handle to the initialized app, or NULL on failure.
 */
ftxui_app_handle_t ftxui_app_create_fullscreen();

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