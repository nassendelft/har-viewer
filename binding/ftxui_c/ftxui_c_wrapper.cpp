#include "ftxui_c_api.h"
#include <ftxui/component/screen_interactive.hpp>
#include <ftxui/component/component.hpp>
#include <ftxui/dom/elements.hpp>
#include <memory>
#include <vector>

using namespace ftxui;

// Wrapper for Component to handle lifetime and children for containers
struct FTXUIComponentWrapper {
    Component component;
};

ftxui_app_handle_t ftxui_app_create_fullscreen() {
    try {
        auto* app = new ScreenInteractive(ScreenInteractive::Fullscreen());
        return static_cast<ftxui_app_handle_t>(app);
    } catch (...) {
        return nullptr;
    }
}

void ftxui_app_loop(ftxui_app_handle_t app, ftxui_component_handle_t component) {
    auto* ftxui_app = static_cast<ScreenInteractive*>(app);
    auto* wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (ftxui_app && wrapper) {
        ftxui_app->Loop(wrapper->component);
    }
}

void ftxui_app_exit(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ScreenInteractive*>(app);
    if (ftxui_app) {
        ftxui_app->Exit();
    }
}

void ftxui_app_destroy(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ScreenInteractive*>(app);
    delete ftxui_app;
}

void ftxui_component_destroy(ftxui_component_handle_t component) {
    auto* wrapper = static_cast<FTXUIComponentWrapper*>(component);
    delete wrapper;
}

ftxui_component_handle_t ftxui_component_text(const char* text) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Renderer([t = std::string(text)] {
        return ftxui::text(t);
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_button(const char* label, void (*on_click)()) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Button(label, on_click);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_checkbox(const char* label, bool* checked) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Checkbox(label, checked);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_input(char* content, const char* placeholder) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Input(content, placeholder);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_toggle(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> toggle_entries;
    for (int i = 0; i < count; ++i) {
        toggle_entries.push_back(entries[i]);
    }
    wrapper->component = Toggle(std::move(toggle_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_slider(const char* label, int* value, int min, int max, int increment) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Slider(label, value, min, max, increment);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_radiobox(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> radio_entries;
    for (int i = 0; i < count; ++i) {
        radio_entries.push_back(entries[i]);
    }
    wrapper->component = Radiobox(std::move(radio_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_vertical() {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Container::Vertical({});
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_container_horizontal() {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Container::Horizontal({});
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_menu(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> menu_entries;
    for (int i = 0; i < count; ++i) {
        menu_entries.push_back(entries[i]);
    }
    wrapper->component = Menu(std::move(menu_entries), selected);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_gauge(double value) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Renderer([value] {
        return ftxui::gauge(value);
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

void ftxui_container_add(ftxui_component_handle_t container, ftxui_component_handle_t child) {
    auto* cont_wrapper = static_cast<FTXUIComponentWrapper*>(container);
    auto* child_wrapper = static_cast<FTXUIComponentWrapper*>(child);
    if (cont_wrapper && child_wrapper) {
        cont_wrapper->component->Add(child_wrapper->component);
    }
}

ftxui_component_handle_t ftxui_component_border(ftxui_component_handle_t component) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (!inner_wrapper) return nullptr;
    
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Renderer(inner_wrapper->component, [inner_wrapper] {
        return inner_wrapper->component->Render() | ftxui::border;
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_flex(ftxui_component_handle_t component) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (!inner_wrapper) return nullptr;

    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Renderer(inner_wrapper->component, [inner_wrapper] {
        return inner_wrapper->component->Render() | ftxui::flex;
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_color(ftxui_component_handle_t component, ftxui_color_t color) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component);
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

    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = Renderer(inner_wrapper->component, [inner_wrapper, ftxui_color] {
        return inner_wrapper->component->Render() | ftxui::color(ftxui_color);
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}