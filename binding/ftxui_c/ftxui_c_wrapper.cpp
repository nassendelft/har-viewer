#include "ftxui_c_api.h"
#include <ftxui/component/app.hpp>
#include <ftxui/component/component.hpp>
#include <ftxui/component/animation.hpp> // Include for animation easing functions
#include <ftxui/screen/color.hpp> // Include for ftxui::Color
#include <memory>
#include <vector>
#include <string.h> // For strdup

// Wrapper for Component to handle lifetime and children for containers
struct FTXUIComponentWrapper {
    ftxui::Component component;
};

// Wrapper for Element to handle lifetime
struct FTXUIElementWrapper {
    ftxui::Element element;
};

// --- Color Implementations ---

ftxui_color_handle_t ftxui_color_default() {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color());
}

ftxui_color_handle_t ftxui_color_rgb(uint8_t r, uint8_t g, uint8_t b) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(r, g, b));
}

ftxui_color_handle_t ftxui_color_rgba(uint8_t r, uint8_t g, uint8_t b, uint8_t a) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(r, g, b, a));
}

ftxui_color_handle_t ftxui_color_hsv(uint8_t h, uint8_t s, uint8_t v) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(ftxui::Color::HSV(h, s, v)));
}

ftxui_color_handle_t ftxui_color_hsva(uint8_t h, uint8_t s, uint8_t v, uint8_t a) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(ftxui::Color::HSVA(h, s, v, a)));
}

ftxui_color_handle_t ftxui_color_palette1(ftxui_palette1_t index) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(static_cast<ftxui::Color::Palette1>(index)));
}

ftxui_color_handle_t ftxui_color_palette16(ftxui_palette16_t index) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(static_cast<ftxui::Color::Palette16>(index)));
}

ftxui_color_handle_t ftxui_color_palette256(ftxui_palette256_t index) {
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(static_cast<ftxui::Color::Palette256>(index)));
}

ftxui_color_handle_t ftxui_color_interpolate(float t, ftxui_color_handle_t a, ftxui_color_handle_t b) {
    auto* color_a = static_cast<ftxui::Color*>(a);
    auto* color_b = static_cast<ftxui::Color*>(b);
    if (!color_a || !color_b) return nullptr;
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(ftxui::Color::Interpolate(t, *color_a, *color_b)));
}

ftxui_color_handle_t ftxui_color_blend(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs) {
    auto* color_lhs = static_cast<ftxui::Color*>(lhs);
    auto* color_rhs = static_cast<ftxui::Color*>(rhs);
    if (!color_lhs || !color_rhs) return nullptr;
    return static_cast<ftxui_color_handle_t>(new ftxui::Color(ftxui::Color::Blend(*color_lhs, *color_rhs)));
}

bool ftxui_color_is_opaque(ftxui_color_handle_t color) {
    auto* ftxui_color = static_cast<ftxui::Color*>(color);
    if (!ftxui_color) return false;
    return ftxui_color->IsOpaque();
}

bool ftxui_color_equals(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs) {
    auto* color_lhs = static_cast<ftxui::Color*>(lhs);
    auto* color_rhs = static_cast<ftxui::Color*>(rhs);
    if (!color_lhs || !color_rhs) return false; // Or handle as error
    return *color_lhs == *color_rhs;
}

bool ftxui_color_not_equals(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs) {
    auto* color_lhs = static_cast<ftxui::Color*>(lhs);
    auto* color_rhs = static_cast<ftxui::Color*>(rhs);
    if (!color_lhs || !color_rhs) return false; // Or handle as error
    return *color_lhs != *color_rhs;
}

char* ftxui_color_print(ftxui_color_handle_t color, bool is_background_color) {
    auto* ftxui_color = static_cast<ftxui::Color*>(color);
    if (!ftxui_color) return strdup(""); // Return empty string for null color
    std::string s = ftxui_color->Print(is_background_color);
    return strdup(s.c_str());
}

void ftxui_color_destroy(ftxui_color_handle_t color) {
    delete static_cast<ftxui::Color*>(color);
}

// --- App Implementations ---
ftxui_app_handle_t ftxui_app_create_fullscreen() {
    try {
        return static_cast<ftxui_app_handle_t>(new ftxui::App(ftxui::App::Fullscreen()));
    } catch (...) {
        return nullptr;
    }
}

ftxui_app_handle_t ftxui_app_create_fit_component() {
    try {
        return static_cast<ftxui_app_handle_t>(new ftxui::App(ftxui::App::FitComponent()));
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
        ftxui::Element el = std::move(static_cast<FTXUIElementWrapper*>(element_handle)->element);
        ftxui_element_destroy(element_handle); // Use the common destroy function
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

// Helper to create and wrap an ftxui::Element into an ftxui_element_handle_t
static ftxui_element_handle_t create_element_wrapper(ftxui::Element element) {
    auto* wrapper = new FTXUIElementWrapper();
    wrapper->element = std::move(element);
    return static_cast<ftxui_element_handle_t>(wrapper);
}

// Generic helper to apply a modifier to an element and return a new wrapped element
template <typename Modifier>
static ftxui_element_handle_t apply_element_modifier(ftxui_element_handle_t element_handle, Modifier modifier) {
    auto* inner_wrapper = static_cast<FTXUIElementWrapper*>(element_handle);
    if (!inner_wrapper) return nullptr;

    ftxui::Element el = modifier(std::move(inner_wrapper->element));
    delete inner_wrapper;
    return create_element_wrapper(std::move(el));
}

ftxui_element_handle_t ftxui_element_text(const char* text) {
    return create_element_wrapper(ftxui::text(text));
}

ftxui_element_handle_t ftxui_element_gauge(double value) {
    return create_element_wrapper(ftxui::gauge(value));
}

ftxui_element_handle_t ftxui_element_separator() {
    return create_element_wrapper(ftxui::separator());
}

ftxui_element_handle_t ftxui_element_separator_light() {
    return create_element_wrapper(ftxui::separatorLight());
}

ftxui_element_handle_t ftxui_element_separator_dashed() {
    return create_element_wrapper(ftxui::separatorDashed());
}

ftxui_element_handle_t ftxui_element_separator_heavy() {
    return create_element_wrapper(ftxui::separatorHeavy());
}

ftxui_element_handle_t ftxui_element_separator_double() {
    return create_element_wrapper(ftxui::separatorDouble());
}

ftxui_element_handle_t ftxui_element_separator_empty() {
    return create_element_wrapper(ftxui::separatorEmpty());
}

ftxui_element_handle_t ftxui_element_separator_styled(ftxui_border_style_t style) {
    ftxui::BorderStyle ftxui_style;
    switch (style) {
        case FTXUI_BORDER_STYLE_LIGHT: ftxui_style = ftxui::LIGHT; break;
        case FTXUI_BORDER_STYLE_DASHED: ftxui_style = ftxui::DASHED; break;
        case FTXUI_BORDER_STYLE_HEAVY: ftxui_style = ftxui::HEAVY; break;
        case FTXUI_BORDER_STYLE_DOUBLE: ftxui_style = ftxui::DOUBLE; break;
        case FTXUI_BORDER_STYLE_ROUNDED: ftxui_style = ftxui::ROUNDED; break;
        case FTXUI_BORDER_STYLE_EMPTY: ftxui_style = ftxui::EMPTY; break;
        default: ftxui_style = ftxui::LIGHT; break;
    }
    return create_element_wrapper(ftxui::separatorStyled(ftxui_style));
}

ftxui_element_handle_t ftxui_element_separator_character(const char* character) {
    return create_element_wrapper(ftxui::separatorCharacter(character));
}

ftxui_element_handle_t ftxui_element_separator_hselector(float left, float right, ftxui_color_handle_t unselected_color_handle, ftxui_color_handle_t selected_color_handle) {
    auto* unselected_color_ptr = static_cast<ftxui::Color*>(unselected_color_handle);
    auto* selected_color_ptr = static_cast<ftxui::Color*>(selected_color_handle);
    if (!unselected_color_ptr || !selected_color_ptr) return nullptr;

    return create_element_wrapper(ftxui::separatorHSelector(left, right, *unselected_color_ptr, *selected_color_ptr));
}

ftxui_element_handle_t ftxui_element_separator_vselector(float up, float down, ftxui_color_handle_t unselected_color_handle, ftxui_color_handle_t selected_color_handle) {
    auto* unselected_color_ptr = static_cast<ftxui::Color*>(unselected_color_handle);
    auto* selected_color_ptr = static_cast<ftxui::Color*>(selected_color_handle);
    if (!unselected_color_ptr || !selected_color_ptr) return nullptr;

    return create_element_wrapper(ftxui::separatorVSelector(up, down, *unselected_color_ptr, *selected_color_ptr));
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
    return create_element_wrapper(ftxui::vbox(std::move(children)));
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
    return create_element_wrapper(ftxui::hbox(std::move(children)));
}

ftxui_element_handle_t ftxui_element_window(ftxui_element_handle_t title, ftxui_element_handle_t component) {
    auto* element_wrapper = static_cast<FTXUIElementWrapper*>(component);
    if (!element_wrapper) return nullptr;
    auto* title_wrapper = static_cast<FTXUIElementWrapper*>(title);
    if (!title_wrapper) return nullptr;

    return create_element_wrapper(ftxui::window(std::move(title_wrapper->element), std::move(element_wrapper->element)));
}

ftxui_element_handle_t ftxui_element_color(ftxui_element_handle_t element, ftxui_color_handle_t color_handle) {
    auto* color_ptr = static_cast<ftxui::Color*>(color_handle);
    if (!color_ptr) return nullptr;

    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::color(*color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_bgcolor(ftxui_element_handle_t element, ftxui_color_handle_t color_handle) {
    auto* color_ptr = static_cast<ftxui::Color*>(color_handle);
    if (!color_ptr) return nullptr;

    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::bgcolor(*color_ptr);
    });
}

// -- START decorators

ftxui_element_handle_t ftxui_element_border(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::border(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_light(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderLight(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_dashed(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderDashed(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_heavy(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderHeavy(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_double(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderDouble(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_rounded(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderRounded(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_empty(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::borderEmpty(std::move(el));
    });
}

 ftxui_element_handle_t ftxui_element_flex(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::flex;
    });
 }

 ftxui_element_handle_t ftxui_element_bold(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::bold;
    });
 }

 ftxui_element_handle_t ftxui_element_inverted(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::inverted;
    });
 }

 ftxui_element_handle_t ftxui_element_underlined(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::underlined;
    });
 }

// -- END decorators

// -- START util elements

ftxui_element_handle_t ftxui_element_hcenter(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::hcenter(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_vcenter(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::vcenter(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_center(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::center(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_align_right(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::align_right(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_nothing(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::nothing(std::move(el));
    });
}

// -- END util elements

ftxui_easing_function_t ftxui_easing_function_get(ftxui_easing_function_type_t type) {
    switch (type) {
        case FTXUI_EASING_LINEAR: return ftxui::animation::easing::Linear;
        case FTXUI_EASING_QUADRATIC_IN: return ftxui::animation::easing::QuadraticIn;
        case FTXUI_EASING_QUADRATIC_OUT: return ftxui::animation::easing::QuadraticOut;
        case FTXUI_EASING_QUADRATIC_IN_OUT: return ftxui::animation::easing::QuadraticInOut;
        case FTXUI_EASING_CUBIC_IN: return ftxui::animation::easing::CubicIn;
        case FTXUI_EASING_CUBIC_OUT: return ftxui::animation::easing::CubicOut;
        case FTXUI_EASING_CUBIC_IN_OUT: return ftxui::animation::easing::CubicInOut;
        case FTXUI_EASING_QUARTIC_IN: return ftxui::animation::easing::QuarticIn;
        case FTXUI_EASING_QUARTIC_OUT: return ftxui::animation::easing::QuarticOut;
        case FTXUI_EASING_QUARTIC_IN_OUT: return ftxui::animation::easing::QuarticInOut;
        case FTXUI_EASING_QUINTIC_IN: return ftxui::animation::easing::QuinticIn;
        case FTXUI_EASING_QUINTIC_OUT: return ftxui::animation::easing::QuinticOut;
        case FTXUI_EASING_QUINTIC_IN_OUT: return ftxui::animation::easing::QuinticInOut;
        case FTXUI_EASING_SINE_IN: return ftxui::animation::easing::SineIn;
        case FTXUI_EASING_SINE_OUT: return ftxui::animation::easing::SineOut;
        case FTXUI_EASING_SINE_IN_OUT: return ftxui::animation::easing::SineInOut;
        case FTXUI_EASING_CIRCULAR_IN: return ftxui::animation::easing::CircularIn;
        case FTXUI_EASING_CIRCULAR_OUT: return ftxui::animation::easing::CircularOut;
        case FTXUI_EASING_CIRCULAR_IN_OUT: return ftxui::animation::easing::CircularInOut;
        case FTXUI_EASING_EXPONENTIAL_IN: return ftxui::animation::easing::ExponentialIn;
        case FTXUI_EASING_EXPONENTIAL_OUT: return ftxui::animation::easing::ExponentialOut;
        case FTXUI_EASING_EXPONENTIAL_IN_OUT: return ftxui::animation::easing::ExponentialInOut;
        case FTXUI_EASING_ELASTIC_IN: return ftxui::animation::easing::ElasticIn;
        case FTXUI_EASING_ELASTIC_OUT: return ftxui::animation::easing::ElasticOut;
        case FTXUI_EASING_ELASTIC_IN_OUT: return ftxui::animation::easing::ElasticInOut;
        case FTXUI_EASING_BACK_IN: return ftxui::animation::easing::BackIn;
        case FTXUI_EASING_BACK_OUT: return ftxui::animation::easing::BackOut;
        case FTXUI_EASING_BACK_IN_OUT: return ftxui::animation::easing::BackInOut;
        case FTXUI_EASING_BOUNCE_IN: return ftxui::animation::easing::BounceIn;
        case FTXUI_EASING_BOUNCE_OUT: return ftxui::animation::easing::BounceOut;
        case FTXUI_EASING_BOUNCE_IN_OUT: return ftxui::animation::easing::BounceInOut;
        default: return ftxui::animation::easing::Linear; // Default to Linear
    }
}

static ftxui::AnimatedColorOption to_ftxui_animated_color_option(ftxui_animated_color_option_t option) {
    ftxui::AnimatedColorOption res;
    res.enabled = option.enabled;
    res.inactive = *static_cast<ftxui::Color*>(option.inactive);
    res.active = *static_cast<ftxui::Color*>(option.active);
    res.duration = std::chrono::milliseconds(option.duration_ms);
    res.function = ftxui_easing_function_get(option.easing_function_type);
    return res;
}

static ftxui::AnimatedColorsOption to_ftxui_animated_colors(ftxui_animated_colors_option_t option) {
    ftxui::AnimatedColorsOption res;
    res.background = to_ftxui_animated_color_option(option.background);
    res.foreground = to_ftxui_animated_color_option(option.foreground);
    return res;
}

// Helper to convert C-style entry state to C++ EntryState
static ftxui::EntryState to_ftxui_entry_state(ftxui_entry_state_t c_state) {
    ftxui::EntryState state;
    state.label = c_state.label;
    state.state = c_state.state;
    state.active = c_state.active;
    state.focused = c_state.focused;
    state.index = c_state.index;
    return state;
}

// Helper to convert C++ EntryState to C-style entry state
static ftxui_entry_state_t to_ftxui_c_entry_state(const ftxui::EntryState& state) {
    ftxui_entry_state_t c_state;
    c_state.label = state.label.c_str();
    c_state.state = state.state;
    c_state.active = state.active;
    c_state.focused = state.focused;
    c_state.index = state.index;
    return c_state;
}

// Helper to create a ftxui_element_handle_t from ftxui_entry_state_t
// and a lambda that generates the ftxui::Element
template <typename F>
static ftxui_element_handle_t make_button_transform_wrapper(ftxui_entry_state_t c_state, void* userdata, F func) {
    ftxui::EntryState state = to_ftxui_entry_state(c_state);
    ftxui::Element element = func(state, userdata);
    return create_element_wrapper(std::move(element));
}

static ftxui::ButtonOption to_ftxui_button_option(ftxui_button_option_t option) {
    ftxui::ButtonOption res;

    res.animated_colors = to_ftxui_animated_colors(option.animated_colors);

    if (option.transform) {
        res.transform = [option](const ftxui::EntryState& state) {
            ftxui_entry_state_t c_state = to_ftxui_c_entry_state(state);
            ftxui_element_handle_t handle = option.transform(c_state, option.transform_userdata);
            if (!handle) return ftxui::text(state.label);
            auto* wrapper = static_cast<FTXUIElementWrapper*>(handle);
            ftxui::Element el = std::move(wrapper->element);
            delete wrapper;
            return el;
        };
    }

    return res;
}

ftxui_component_handle_t ftxui_component_button(const char* label, void (*on_click)(void*), void* userdata) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Button(label, [on_click, userdata] {
        if (on_click) on_click(userdata);
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_button_with_options(const char* label, void (*on_click)(void*), void* userdata, ftxui_button_option_t options) {
    auto* wrapper = new FTXUIComponentWrapper();
    auto opt = to_ftxui_button_option(options);
    opt.on_click = [on_click, userdata] {
        if (on_click) on_click(userdata);
    };
    wrapper->component = ftxui::Button(label, opt.on_click, opt);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

static ftxui_element_handle_t button_transform_simple_wrapper(ftxui_entry_state_t c_state, void* userdata) {
    return make_button_transform_wrapper(c_state, userdata, [](const ftxui::EntryState& state, void* /*unused*/) {
        ftxui::Element element = ftxui::text(state.label) | ftxui::borderLight;
        if (state.focused) {
            element |= ftxui::inverted;
        }
        return element;
    });
}

ftxui_button_option_t ftxui_button_option_simple() {
    ftxui_button_option_t res;
    res.transform = button_transform_simple_wrapper;
    res.transform_userdata = nullptr; // No specific userdata needed for this transform
    return res;
}

static ftxui_element_handle_t button_transform_ascii_wrapper(ftxui_entry_state_t c_state, void* userdata) {
    return make_button_transform_wrapper(c_state, userdata, [](const ftxui::EntryState& state, void* /*unused*/) {
        const std::string t = state.focused ? "[" + state.label + "]" : " " + state.label + " ";
        return ftxui::text(t);
    });
}

ftxui_button_option_t ftxui_button_option_ascii() {
    ftxui_button_option_t res;
    res.transform = button_transform_ascii_wrapper;
    res.transform_userdata = nullptr; // No specific userdata needed for this transform
    return res;
}

static ftxui_element_handle_t button_transform_border_wrapper(ftxui_entry_state_t c_state, void* userdata) {
    return make_button_transform_wrapper(c_state, userdata, [](const ftxui::EntryState& state, void* /*unused*/) {
        ftxui::Element element = ftxui::text(state.label) | ftxui::border;
        if (state.active) {
          element |= ftxui::bold;
        }
        if (state.focused) {
          element |= ftxui::inverted;
        }
        return element;
    });
}

ftxui_button_option_t ftxui_button_option_border() {
    ftxui_button_option_t res;
    res.transform = button_transform_border_wrapper;
    res.transform_userdata = nullptr; // No specific userdata needed for this transform
    return res;
}

static ftxui_element_handle_t button_transform_animated_with_colors_wrapper(ftxui_entry_state_t c_state, void* userdata) {
    return make_button_transform_wrapper(c_state, userdata, [](const ftxui::EntryState& state, void* /*unused*/) {
        ftxui::Element element = ftxui::text(state.label) | ftxui::borderEmpty;
        if (state.focused) {
            element |= ftxui::bold;
        }
        return element;
    });
}

static ftxui_animated_color_option_t create_animated_color_option(ftxui_color_handle_t inactive, ftxui_color_handle_t active) {
    ftxui_animated_color_option_t option;
    option.enabled = true;
    option.inactive = inactive;
    option.active = active;
    option.duration_ms = 250;
    option.easing_function_type = FTXUI_EASING_QUINTIC_IN_OUT;
    return option;
}

ftxui_button_option_t ftxui_button_option_animated(ftxui_color_handle_t background, ftxui_color_handle_t foreground, ftxui_color_handle_t background_active, ftxui_color_handle_t foreground_active) {
    ftxui_animated_colors_option_t animated_colors;
    animated_colors.foreground = create_animated_color_option(foreground, foreground_active);
    animated_colors.background = create_animated_color_option(background, background_active);

    ftxui_button_option_t res;
    res.animated_colors = animated_colors;
    res.transform = button_transform_animated_with_colors_wrapper;
    res.transform_userdata = nullptr; // No specific userdata needed for this transform
    return res;
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

    return create_element_wrapper(inner_wrapper->component->Render());
}