#include "ftxui_c_api.h"
#include <stdio.h>

ftxui_app_handle_t global_app;

void on_button_click(void* userdata) {
    (void)userdata;
    printf("Button clicked!\n");
    ftxui_app_exit(global_app);
}

int main() {
    printf("Starting FTXUI C Binding Test...\n");
    
    global_app = ftxui_app_create_fullscreen();
    if (!global_app) {
        fprintf(stderr, "Failed to create FTXUI app\n");
        return 1;
    }

    ftxui_component_handle_t container = ftxui_component_container_vertical();
    ftxui_component_handle_t label1 = ftxui_element_text("Hello from C!");
    
    const char* menu_items[] = {"Item 1", "Item 2", "Item 3"};
    int selected = 0;
    ftxui_component_handle_t menu = ftxui_component_menu(menu_items, 3, &selected);

    ftxui_component_handle_t label2 = ftxui_element_text("This is a horizontal container:");

    ftxui_component_handle_t h_container = ftxui_component_container_horizontal();
    ftxui_component_handle_t btn1 = ftxui_component_button("Exit", on_button_click, NULL);
    ftxui_component_handle_t label3 = ftxui_element_text(" (<- click that)");

    ftxui_container_add(h_container, btn1);
    ftxui_container_add(h_container, label3);

    ftxui_container_add(container, label1);
    ftxui_container_add(container, ftxui_element_border(menu));

    bool checked = true;
    ftxui_component_handle_t checkbox = ftxui_component_checkbox("Check me", &checked);
    ftxui_container_add(container, checkbox);

    char input_buf[100] = "Initial text";
    ftxui_component_handle_t input = ftxui_component_input(input_buf, "Placeholder...");
    ftxui_container_add(container, input);

    const char* toggle_items[] = {"Toggle 1", "Toggle 2", "Toggle 3"};
    int toggle_selected = 1;
    ftxui_component_handle_t toggle = ftxui_component_toggle(toggle_items, 3, &toggle_selected);
    ftxui_container_add(container, toggle);

    int slider_val = 50;
    ftxui_component_handle_t slider = ftxui_component_slider("Slider", &slider_val, 0, 100, 1);
    ftxui_container_add(container, slider);

    const char* radio_items[] = {"Radio 1", "Radio 2", "Radio 3"};
    int radio_selected = 0;
    ftxui_component_handle_t radio = ftxui_component_radiobox(radio_items, 3, &radio_selected);
    ftxui_container_add(container, radio);

    ftxui_container_add(container, label2);
    ftxui_container_add(container, ftxui_element_border(h_container));

    printf("App created and components added. (Skipping loop in automated test)\n");

    ftxui_app_destroy(global_app);
    
    // Note: ideally we'd destroy container too, which should destroy children if they are wrapped.
    // In our current implementation, we might need a more robust ownership model if we were to avoid leaks.
    ftxui_component_destroy(container);
    // h_container, labels, etc are technically leaked here if not managed.
    // For this simple test, we just check compilation and basic flow.

    printf("Test finished successfully.\n");
    return 0;
}
