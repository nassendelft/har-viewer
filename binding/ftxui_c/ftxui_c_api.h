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

/**
 * @brief Initializes the FTXUI interactive application (ScreenInteractive).
 * 
 * @return ftxui_app_handle_t A handle to the initialized app, or NULL on failure.
 */
ftxui_app_handle_t ftxui_app_create_fullscreen();

/**
 * @brief Runs the main loop for the FTXUI app.
 * 
 * @param app The app handle.
 * @param component The root component to display.
 */
void ftxui_app_loop(ftxui_app_handle_t app, ftxui_component_handle_t component);

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

// --- Component Creation ---

/**
 * @brief Creates a simple text label component.
 * 
 * @param text The text to display.
 * @return ftxui_component_handle_t The component handle.
 */
ftxui_component_handle_t ftxui_component_text(const char* text);

/**
 * @brief Creates a button component.
 * 
 * @param label The label of the button.
 * @param on_click The callback function when the button is clicked.
 * @return ftxui_component_handle_t The button component handle.
 */
ftxui_component_handle_t ftxui_component_button(const char* label, void (*on_click)());

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
 * @brief Creates a menu/list component.
 * 
 * @param entries An array of strings for the menu entries.
 * @param count The number of entries.
 * @param selected A pointer to an integer that will store the selected index.
 * @return ftxui_component_handle_t The menu component handle.
 */
ftxui_component_handle_t ftxui_component_menu(const char** entries, int count, int* selected);

/**
 * @brief Creates a gauge component.
 *
 * @param value The value of the gauge (0.0 to 1.0).
 * @return ftxui_component_handle_t The gauge component handle.
 */
ftxui_component_handle_t ftxui_component_gauge(double value);

/**
 * @brief Adds a child component to a container.
 * 
 * @param container The container component.
 * @param child The child component to add.
 */
void ftxui_container_add(ftxui_component_handle_t container, ftxui_component_handle_t child);

// --- Decorators ---

/**
 * @brief Wraps a component with a border.
 * 
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a border.
 */
ftxui_component_handle_t ftxui_component_border(ftxui_component_handle_t component);

/**
 * @brief Makes a component flexible, allowing it to expand or shrink.
 *
 * @param component The component to make flexible.
 * @return ftxui_component_handle_t A new component with flex properties.
 */
ftxui_component_handle_t ftxui_component_flex(ftxui_component_handle_t component);

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
 * @brief Sets the foreground color of a component.
 *
 * @param component The component to color.
 * @param color The color to apply.
 * @return ftxui_component_handle_t A new component with the specified color.
 */
ftxui_component_handle_t ftxui_component_color(ftxui_component_handle_t component, ftxui_color_t color);

#ifdef __cplusplus
}
#endif

#endif // FTXUI_C_API_H