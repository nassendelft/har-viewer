#include "ftxui_c_api.h"
#include <ftxui/component/app.hpp>
#include <ftxui/component/component.hpp>
#include <memory>
#include <vector>

// Wrapper for Component to handle lifetime and children for containers
struct FTXUIComponentWrapper {
    ftxui::Component component;
};

// Wrapper for Element to handle lifetime
struct FTXUIElementWrapper {
    ftxui::Element element;
};

ftxui_app_handle_t ftxui_app_create_fullscreen() {
    try {
        return static_cast<ftxui_app_handle_t>(new ftxui::App(ftxui::App::Fullscreen()));
    } catch (...) {
        return nullptr;
    }
}

void ftxui_app_loop(ftxui_app_handle_t app, ftxui_component_handle_t component) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    auto* wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (ftxui_app && wrapper) {
        ftxui_app->Loop(wrapper->component);
    }
}

void ftxui_app_exit(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    if (ftxui_app) {
        ftxui_app->Exit();
    }
}

void ftxui_app_destroy(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    delete ftxui_app;
}

void ftxui_component_destroy(ftxui_component_handle_t component) {
    auto* wrapper = static_cast<FTXUIComponentWrapper*>(component);
    delete wrapper;
}

ftxui_component_handle_t ftxui_component_renderer(ftxui_component_handle_t component, ftxui_render_callback_t callback, void* userdata) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
    auto* wrapper = new FTXUIComponentWrapper();

    auto render_lambda = [callback, userdata] {
        ftxui_element_handle_t element_handle = callback(userdata);
        auto* element_wrapper = static_cast<FTXUIElementWrapper*>(element_handle);
        ftxui::Element el = std::move(element_wrapper->element);
        delete element_wrapper;
        return el;
    };

    if (inner_wrapper) {
        wrapper->component = ftxui::Renderer(inner_wrapper->component, render_lambda);
    } else {
        wrapper->component = ftxui::Renderer(render_lambda);
    }

    return static_cast<ftxui_component_handle_t>(wrapper);
}

void ftxui_element_destroy(ftxui_element_handle_t element) {
    auto* wrapper = static_cast<FTXUIElementWrapper*>(element);
    delete wrapper;
}

ftxui_element_handle_t ftxui_element_text(const char* text) {
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::text(text);
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_gauge(double value) {
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::gauge(value);
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_separator() {
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::separator();
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_vbox(ftxui_element_handle_t* elements, int count) {
    ftxui::Elements children;
    for (int i = 0; i < count; ++i) {
        auto* wrapper = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (wrapper) {
            children.push_back(std::move(wrapper->element));
            delete wrapper;
        }
    }
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::vbox(std::move(children));
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_hbox(ftxui_element_handle_t* elements, int count) {
    ftxui::Elements children;
    for (int i = 0; i < count; ++i) {
        auto* wrapper = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (wrapper) {
            children.push_back(std::move(wrapper->element));
            delete wrapper;
        }
    }
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::hbox(std::move(children));
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_window(ftxui_element_handle_t title, ftxui_element_handle_t component) {
    auto* element_wrapper = static_cast<FTXUIElementWrapper*>(component);
    if (!element_wrapper) return nullptr;
    auto* title_wrapper = static_cast<FTXUIElementWrapper*>(title);
    if (!title_wrapper) return nullptr;

    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = ftxui::window(std::move(title_wrapper->element), std::move(element_wrapper->element));
    return static_cast<ftxui_element_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_element_color(ftxui_element_handle_t element, ftxui_color_t color) {
    auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
    if (!inner_wrapper) return nullptr;

    ftxui::Color ftxui_color;
    switch (color) {
        case FTXUI_COLOR_BLACK: ftxui_color = ftxui::Color::Black; break;
        case FTXUI_COLOR_RED: ftxui_color = ftxui::Color::Red; break;
        case FTXUI_COLOR_GREEN: ftxui_color = ftxui::Color::Green; break;
        case FTXUI_COLOR_YELLOW: ftxui_color = ftxui::Color::Yellow; break;
        case FTXUI_COLOR_BLUE: ftxui_color = ftxui::Color::Blue; break;
        case FTXUI_COLOR_MAGENTA: ftxui_color = ftxui::Color::Magenta; break;
        case FTXUI_COLOR_CYAN: ftxui_color = ftxui::Color::Cyan; break;
        case FTXUI_COLOR_WHITE: ftxui_color = ftxui::Color::White; break;
        case FTXUI_COLOR_DEFAULT:
        default: ftxui_color = ftxui::Color::Default; break;
    }

    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = inner_wrapper->element | ftxui::color(ftxui_color);
    return static_cast<ftxui_element_handle_t>(wrapper);
}

// -- START decorators

ftxui_element_handle_t ftxui_element_border(ftxui_element_handle_t element) {
     auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
     if (!inner_wrapper) return nullptr;

     auto* wrapper = new FTXUIElementWrapper();
     wrapper->element = inner_wrapper->element | ftxui::border;
     return static_cast<ftxui_element_handle_t>(wrapper);
 }

 ftxui_element_handle_t ftxui_element_flex(ftxui_element_handle_t element) {
     auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
     if (!inner_wrapper) return nullptr;

     auto* wrapper = new FTXUIElementWrapper();
     wrapper->element = inner_wrapper->element | ftxui::flex;
     return static_cast<ftxui_element_handle_t>(wrapper);
 }

 ftxui_element_handle_t ftxui_element_bold(ftxui_element_handle_t element) {
     auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
     if (!inner_wrapper) return nullptr;

     auto* wrapper = new FTXUIElementWrapper();
     wrapper->element = inner_wrapper->element | ftxui::bold;
     return static_cast<ftxui_element_handle_t>(wrapper);
 }

 ftxui_element_handle_t ftxui_element_inverted(ftxui_element_handle_t element) {
     auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
     if (!inner_wrapper) return nullptr;

     auto* wrapper = new FTXUIElementWrapper();
     wrapper->element = inner_wrapper->element | ftxui::inverted;
     return static_cast<ftxui_element_handle_t>(wrapper);
 }

 ftxui_element_handle_t ftxui_element_underlined(ftxui_element_handle_t element) {
     auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element);
     if (!inner_wrapper) return nullptr;

     auto* wrapper = new FTXUIElementWrapper();
     wrapper->element = inner_wrapper->element | ftxui::underlined;
     return static_cast<ftxui_element_handle_t>(wrapper);
 }

// -- END decorators

ftxui_component_handle_t ftxui_component_button(const char* label, void (*on_click)(void*), void* userdata) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Button(label, [on_click, userdata] {
        if (on_click) on_click(userdata);
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_checkbox(const char* label, bool* checked) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Checkbox(label, checked);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_input(char* content, const char* placeholder) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Input(content, placeholder);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_toggle(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> toggle_entries;
    for (int i = 0; i < count; ++i) {
        toggle_entries.push_back(entries[i]);
    }
    wrapper->component = ftxui::Toggle(std::move(toggle_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_slider(const char* label, int* value, int min, int max, int increment) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Slider(label, value, min, max, increment);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_radiobox(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> radio_entries;
    for (int i = 0; i < count; ++i) {
        radio_entries.push_back(entries[i]);
    }
    wrapper->component = ftxui::Radiobox(std::move(radio_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_vertical() {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Container::Vertical({});
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_horizontal() {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Container::Horizontal({});
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_tab(int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Container::Tab({}, selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_stacked() {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Container::Stacked({});
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_menu(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> menu_entries;
    for (int i = 0; i < count; ++i) {
        menu_entries.push_back(entries[i]);
    }
    wrapper->component = ftxui::Menu(std::move(menu_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_menu_entry(const char* label) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::MenuEntry(label);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_dropdown(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> dropdown_entries;
    for (int i = 0; i < count; ++i) {
        dropdown_entries.push_back(entries[i]);
    }
    wrapper->component = ftxui::Dropdown(std::move(dropdown_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_resizable_split_left(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size) {
    auto* main_wrapper = static_cast<FTXUIComponentWrapper*>(main);
    auto* back_wrapper = static_cast<FTXUIComponentWrapper*>(back);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::ResizableSplitLeft(main_wrapper->component, back_wrapper->component, main_size);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_resizable_split_right(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size) {
    auto* main_wrapper = static_cast<FTXUIComponentWrapper*>(main);
    auto* back_wrapper = static_cast<FTXUIComponentWrapper*>(back);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::ResizableSplitRight(main_wrapper->component, back_wrapper->component, main_size);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_resizable_split_top(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size) {
    auto* main_wrapper = static_cast<FTXUIComponentWrapper*>(main);
    auto* back_wrapper = static_cast<FTXUIComponentWrapper*>(back);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::ResizableSplitTop(main_wrapper->component, back_wrapper->component, main_size);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_resizable_split_bottom(ftxui_component_handle_t main, ftxui_component_handle_t back, int* main_size) {
    auto* main_wrapper = static_cast<FTXUIComponentWrapper*>(main);
    auto* back_wrapper = static_cast<FTXUIComponentWrapper*>(back);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::ResizableSplitBottom(main_wrapper->component, back_wrapper->component, main_size);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_collapsible(const char* label, ftxui_component_handle_t child, bool* show) {
    auto* child_wrapper = static_cast<FTXUIComponentWrapper*>(child);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Collapsible(label, child_wrapper->component, show);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_maybe(ftxui_component_handle_t child, const bool* show) {
    auto* child_wrapper = static_cast<FTXUIComponentWrapper*>(child);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Maybe(child_wrapper->component, show);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_modal(ftxui_component_handle_t main, ftxui_component_handle_t modal, const bool* show_modal) {
    auto* main_wrapper = static_cast<FTXUIComponentWrapper*>(main);
    auto* modal_wrapper = static_cast<FTXUIComponentWrapper*>(modal);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Modal(main_wrapper->component, modal_wrapper->component, show_modal);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

void ftxui_container_add(ftxui_component_handle_t container, ftxui_component_handle_t child) {
    auto* cont_wrapper = static_cast<FTXUIComponentWrapper*>(container);
    auto* child_wrapper = static_cast<FTXUIComponentWrapper*>(child);
    if (cont_wrapper && child_wrapper) {
        cont_wrapper->component->Add(child_wrapper->component);
    }
}

ftxui_component_handle_t ftxui_component_hoverable(ftxui_component_handle_t component, bool* hover) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (!inner_wrapper) return nullptr;

    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Hoverable(inner_wrapper->component, hover);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_element_handle_t ftxui_component_render(ftxui_component_handle_t component) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (!inner_wrapper) return nullptr;

    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = inner_wrapper->component->Render();
    return static_cast<ftxui_element_handle_t>(wrapper);
}

// -- END components