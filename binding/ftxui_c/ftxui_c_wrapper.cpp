#include "ftxui_c_api.h"
#include <ftxui/component/app.hpp>
#include <ftxui/component/component.hpp>
#include <ftxui/component/animation.hpp> // Include for animation easing functions
#include <ftxui/component/event.hpp>
#include <ftxui/component/component_options.hpp>
#include <ftxui/screen/color.hpp> // Include for ftxui::Color
#include <ftxui/screen/terminal.hpp> // Include for ftxui::Terminal::Size
#include <ftxui/dom/elements.hpp> // Include for ftxui::size
#include <ftxui/dom/direction.hpp>
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
ftxui_color_handle_t ftxui_color_palette256_raw(int index) {
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

ftxui_app_handle_t ftxui_app_create_terminal_output() {
    try {
        return static_cast<ftxui_app_handle_t>(new ftxui::App(ftxui::App::TerminalOutput()));
    } catch (...) {
        return nullptr;
    }
}

int ftxui_terminal_width() {
    return ftxui::Terminal::Size().dimx;
}

int ftxui_terminal_height() {
    return ftxui::Terminal::Size().dimy;
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

ftxui_element_handle_t ftxui_element_vscroll_indicator(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::vscroll_indicator;
    });
}

ftxui_element_handle_t ftxui_element_frame(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::frame;
    });
}

ftxui_element_handle_t ftxui_element_set_size(ftxui_element_handle_t element, ftxui_width_or_height_t width_or_height_enum, ftxui_constraint_t constraint_type, int value) {
    auto ftxui_constraint_modifier = [&](ftxui::Element el) {
        ftxui::WidthOrHeight width_or_height;
        ftxui::Constraint constraint;
        switch (width_or_height_enum) {
            case FTXUI_WIDTH_OR_HEIGHT_WIDTH: width_or_height = ftxui::WidthOrHeight::WIDTH; break;
            case FTXUI_WIDTH_OR_HEIGHT_HEIGHT: width_or_height = ftxui::WidthOrHeight::HEIGHT; break;
        }
        switch (constraint_type) {
            case FTXUI_CONSTRAINT_LESS_THAN: constraint = ftxui::Constraint::LESS_THAN; break;
            case FTXUI_CONSTRAINT_GREATER_THAN: constraint = ftxui::Constraint::GREATER_THAN; break;
            case FTXUI_CONSTRAINT_EQUAL: constraint = ftxui::Constraint::EQUAL; break;
        }
        return el | ftxui::size(width_or_height, constraint, value);
    };
    return apply_element_modifier(element, ftxui_constraint_modifier);
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


ftxui_element_handle_t ftxui_element_dim(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::dim;
    });
}

ftxui_element_handle_t ftxui_element_blink(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::blink;
    });
}

ftxui_element_handle_t ftxui_element_strikethrough(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::strikethrough;
    });
}

ftxui_element_handle_t ftxui_element_nothing(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::nothing(std::move(el));
    });
}

// -- END util elements

// -- START additional elements

ftxui_element_handle_t ftxui_element_vtext(const char* text) {
    return create_element_wrapper(ftxui::vtext(text));
}

ftxui_element_handle_t ftxui_element_spinner(int charset_index, int image_index) {
    return create_element_wrapper(ftxui::spinner(charset_index, image_index));
}

ftxui_element_handle_t ftxui_element_paragraph(const char* text) {
    return create_element_wrapper(ftxui::paragraph(text));
}

ftxui_element_handle_t ftxui_element_paragraph_align_left(const char* text) {
    return create_element_wrapper(ftxui::paragraphAlignLeft(text));
}

ftxui_element_handle_t ftxui_element_paragraph_align_right(const char* text) {
    return create_element_wrapper(ftxui::paragraphAlignRight(text));
}

ftxui_element_handle_t ftxui_element_paragraph_align_center(const char* text) {
    return create_element_wrapper(ftxui::paragraphAlignCenter(text));
}

ftxui_element_handle_t ftxui_element_paragraph_align_justify(const char* text) {
    return create_element_wrapper(ftxui::paragraphAlignJustify(text));
}

ftxui_element_handle_t ftxui_element_empty() {
    return create_element_wrapper(ftxui::emptyElement());
}

ftxui_element_handle_t ftxui_element_gauge_left(double value) {
    return create_element_wrapper(ftxui::gaugeLeft(static_cast<float>(value)));
}

ftxui_element_handle_t ftxui_element_gauge_right(double value) {
    return create_element_wrapper(ftxui::gaugeRight(static_cast<float>(value)));
}

ftxui_element_handle_t ftxui_element_gauge_up(double value) {
    return create_element_wrapper(ftxui::gaugeUp(static_cast<float>(value)));
}

ftxui_element_handle_t ftxui_element_gauge_down(double value) {
    return create_element_wrapper(ftxui::gaugeDown(static_cast<float>(value)));
}

ftxui_element_handle_t ftxui_element_gauge_direction(double value, ftxui_direction_t direction) {
    ftxui::Direction dir;
    switch (direction) {
        case FTXUI_DIRECTION_UP: dir = ftxui::Direction::Up; break;
        case FTXUI_DIRECTION_DOWN: dir = ftxui::Direction::Down; break;
        case FTXUI_DIRECTION_LEFT: dir = ftxui::Direction::Left; break;
        case FTXUI_DIRECTION_RIGHT: dir = ftxui::Direction::Right; break;
        default: dir = ftxui::Direction::Right; break;
    }
    return create_element_wrapper(ftxui::gaugeDirection(static_cast<float>(value), dir));
}

ftxui_element_handle_t ftxui_element_dbox(ftxui_element_handle_t* elements, int count) {
    ftxui::Elements children;
    for (int i = 0; i < count; ++i) {
        auto* wrapper = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (wrapper) {
            children.push_back(std::move(wrapper->element));
            delete wrapper;
        }
    }
    return create_element_wrapper(ftxui::dbox(std::move(children)));
}

ftxui_element_handle_t ftxui_element_hflow(ftxui_element_handle_t* elements, int count) {
    ftxui::Elements children;
    for (int i = 0; i < count; ++i) {
        auto* wrapper = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (wrapper) {
            children.push_back(std::move(wrapper->element));
            delete wrapper;
        }
    }
    return create_element_wrapper(ftxui::hflow(std::move(children)));
}

ftxui_element_handle_t ftxui_element_vflow(ftxui_element_handle_t* elements, int count) {
    ftxui::Elements children;
    for (int i = 0; i < count; ++i) {
        auto* wrapper = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (wrapper) {
            children.push_back(std::move(wrapper->element));
            delete wrapper;
        }
    }
    return create_element_wrapper(ftxui::vflow(std::move(children)));
}

ftxui_element_handle_t ftxui_element_flex_grow(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::flex_grow(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_flex_shrink(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::flex_shrink(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_xflex(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::xflex(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_xflex_grow(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::xflex_grow(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_xflex_shrink(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::xflex_shrink(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_yflex(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::yflex(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_yflex_grow(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::yflex_grow(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_yflex_shrink(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::yflex_shrink(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_notflex(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::notflex(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_filler() {
    return create_element_wrapper(ftxui::filler());
}

ftxui_element_handle_t ftxui_element_xframe(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::xframe(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_yframe(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::yframe(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focus(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_block(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorBlock(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_block_blinking(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorBlockBlinking(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_bar(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorBar(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_bar_blinking(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorBarBlinking(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_underline(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorUnderline(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_focus_cursor_underline_blinking(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::focusCursorUnderlineBlinking(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_italic(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::italic(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_underlined_double(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::underlinedDouble(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_automerge(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::automerge(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_hyperlink(const char* link, ftxui_element_handle_t element) {
    std::string link_str(link);
    return apply_element_modifier(element, [link_str](ftxui::Element el) {
        return ftxui::hyperlink(link_str, std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_hscroll_indicator(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return std::move(el) | ftxui::hscroll_indicator;
    });
}

ftxui_element_handle_t ftxui_element_clear_under(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::clear_under(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_border_styled(ftxui_element_handle_t element, ftxui_border_style_t style) {
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
    return apply_element_modifier(element, [ftxui_style](ftxui::Element el) {
        return std::move(el) | ftxui::borderStyled(ftxui_style);
    });
}

ftxui_element_handle_t ftxui_element_border_styled_color(ftxui_element_handle_t element, ftxui_border_style_t style, ftxui_color_handle_t color) {
    auto* color_ptr = static_cast<ftxui::Color*>(color);
    if (!color_ptr) return nullptr;
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
    return apply_element_modifier(element, [ftxui_style, color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::borderStyled(ftxui_style, *color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_border_colored(ftxui_element_handle_t element, ftxui_color_handle_t color) {
    auto* color_ptr = static_cast<ftxui::Color*>(color);
    if (!color_ptr) return nullptr;
    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::borderStyled(*color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_selection_style_reset(ftxui_element_handle_t element) {
    return apply_element_modifier(element, [](ftxui::Element el) {
        return ftxui::selectionStyleReset(std::move(el));
    });
}

ftxui_element_handle_t ftxui_element_selection_color(ftxui_element_handle_t element, ftxui_color_handle_t color) {
    auto* color_ptr = static_cast<ftxui::Color*>(color);
    if (!color_ptr) return nullptr;
    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::selectionColor(*color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_selection_background_color(ftxui_element_handle_t element, ftxui_color_handle_t color) {
    auto* color_ptr = static_cast<ftxui::Color*>(color);
    if (!color_ptr) return nullptr;
    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::selectionBackgroundColor(*color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_selection_foreground_color(ftxui_element_handle_t element, ftxui_color_handle_t color) {
    auto* color_ptr = static_cast<ftxui::Color*>(color);
    if (!color_ptr) return nullptr;
    return apply_element_modifier(element, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::selectionForegroundColor(*color_ptr);
    });
}

ftxui_element_handle_t ftxui_element_focus_position(ftxui_element_handle_t element, int x, int y) {
    return apply_element_modifier(element, [x, y](ftxui::Element el) {
        return std::move(el) | ftxui::focusPosition(x, y);
    });
}

ftxui_element_handle_t ftxui_element_focus_position_relative(ftxui_element_handle_t element, float x, float y) {
    return apply_element_modifier(element, [x, y](ftxui::Element el) {
        return std::move(el) | ftxui::focusPositionRelative(x, y);
    });
}

// -- END additional elements

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
    if (option.inactive) res.inactive = *static_cast<ftxui::Color*>(option.inactive);
    if (option.active) res.active = *static_cast<ftxui::Color*>(option.active);
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
    ftxui_button_option_t res = {};
    res.transform = button_transform_simple_wrapper;
    return res;
}

static ftxui_element_handle_t button_transform_ascii_wrapper(ftxui_entry_state_t c_state, void* userdata) {
    return make_button_transform_wrapper(c_state, userdata, [](const ftxui::EntryState& state, void* /*unused*/) {
        const std::string t = state.focused ? "[" + state.label + "]" : " " + state.label + " ";
        return ftxui::text(t);
    });
}

ftxui_button_option_t ftxui_button_option_ascii() {
    ftxui_button_option_t res = {};
    res.transform = button_transform_ascii_wrapper;
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
    ftxui_button_option_t res = {};
    res.transform = button_transform_border_wrapper;
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
    ftxui_button_option_t res = {};
    res.animated_colors.foreground = create_animated_color_option(foreground, foreground_active);
    res.animated_colors.background = create_animated_color_option(background, background_active);
    res.transform = button_transform_animated_with_colors_wrapper;
    return res;
}

ftxui_component_handle_t ftxui_component_checkbox(const char* label, bool* checked) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Checkbox(label, checked);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- String handle ---
struct FTXUIStringWrapper { std::string value; };

ftxui_string_handle_t ftxui_string_create(const char* initial) {
    auto* w = new FTXUIStringWrapper();
    if (initial) w->value = initial;
    return static_cast<ftxui_string_handle_t>(w);
}
const char* ftxui_string_get(ftxui_string_handle_t str) {
    auto* w = static_cast<FTXUIStringWrapper*>(str);
    return w ? w->value.c_str() : nullptr;
}
void ftxui_string_set(ftxui_string_handle_t str, const char* value) {
    auto* w = static_cast<FTXUIStringWrapper*>(str);
    if (w && value) w->value = value;
}
void ftxui_string_destroy(ftxui_string_handle_t str) {
    delete static_cast<FTXUIStringWrapper*>(str);
}

ftxui_component_handle_t ftxui_component_input(ftxui_string_handle_t content, const char* placeholder) {
    auto* str_wrapper = static_cast<FTXUIStringWrapper*>(content);
    auto* wrapper = new FTXUIComponentWrapper();
    std::string ph = placeholder ? placeholder : "";
    wrapper->component = ftxui::Input(&str_wrapper->value, ph);
    return static_cast<ftxui_component_handle_t>(wrapper);
}
ftxui_component_handle_t ftxui_component_input_password(ftxui_string_handle_t content, const char* placeholder) {
    auto* str_wrapper = static_cast<FTXUIStringWrapper*>(content);
    auto* wrapper = new FTXUIComponentWrapper();
    std::string ph = placeholder ? placeholder : "";
    ftxui::InputOption opt;
    opt.password = true;
    wrapper->component = ftxui::Input(&str_wrapper->value, ph, opt);
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

// Generic helper to apply a modifier to a component and return a new wrapped component
template <typename Modifier>
static ftxui_component_handle_t apply_component_modifier(ftxui_component_handle_t component_handle, Modifier modifier) {
    auto* inner_wrapper = static_cast<FTXUIComponentWrapper*>(component_handle);
    if (!inner_wrapper) return nullptr;

    auto* new_wrapper = new FTXUIComponentWrapper();
    new_wrapper->component = ftxui::Renderer(inner_wrapper->component, [modifier, inner_wrapper] {
        return modifier(inner_wrapper->component->Render());
    });
    return static_cast<ftxui_component_handle_t>(new_wrapper);
}


ftxui_component_handle_t ftxui_component_dim(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::dim;
    });
}

ftxui_component_handle_t ftxui_component_blink(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::blink;
    });
}

ftxui_component_handle_t ftxui_component_strikethrough(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::strikethrough;
    });
}

ftxui_component_handle_t ftxui_component_nothing(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::nothing(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::border(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_light(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderLight(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_dashed(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderDashed(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_heavy(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderHeavy(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_double(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderDouble(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_rounded(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderRounded(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_border_empty(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::borderEmpty(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_frame(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::frame;
    });
}

ftxui_component_handle_t ftxui_component_flex(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::flex;
    });
}

ftxui_component_handle_t ftxui_component_bold(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::bold;
    });
}

ftxui_component_handle_t ftxui_component_inverted(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::inverted;
    });
}

ftxui_component_handle_t ftxui_component_underlined(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::underlined;
    });
}

ftxui_component_handle_t ftxui_component_color(ftxui_component_handle_t component, ftxui_color_handle_t color_handle) {
    auto* color_ptr = static_cast<ftxui::Color*>(color_handle);
    if (!color_ptr) return nullptr;
    return apply_component_modifier(component, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::color(*color_ptr);
    });
}

ftxui_component_handle_t ftxui_component_bgcolor(ftxui_component_handle_t component, ftxui_color_handle_t color_handle) {
    auto* color_ptr = static_cast<ftxui::Color*>(color_handle);
    if (!color_ptr) return nullptr;
    return apply_component_modifier(component, [color_ptr](ftxui::Element el) {
        return std::move(el) | ftxui::bgcolor(*color_ptr);
    });
}

ftxui_component_handle_t ftxui_component_vscroll_indicator(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return std::move(el) | ftxui::vscroll_indicator;
    });
}

ftxui_component_handle_t ftxui_component_set_size(ftxui_component_handle_t component, ftxui_width_or_height_t width_or_height_enum, ftxui_constraint_t constraint_type, int value) {
    return apply_component_modifier(component, [width_or_height_enum, constraint_type, value](ftxui::Element el) {
        ftxui::WidthOrHeight width_or_height;
        ftxui::Constraint constraint;
        switch (width_or_height_enum) {
            case FTXUI_WIDTH_OR_HEIGHT_WIDTH: width_or_height = ftxui::WidthOrHeight::WIDTH; break;
            case FTXUI_WIDTH_OR_HEIGHT_HEIGHT: width_or_height = ftxui::WidthOrHeight::HEIGHT; break;
        }
        switch (constraint_type) {
            case FTXUI_CONSTRAINT_LESS_THAN: constraint = ftxui::Constraint::LESS_THAN; break;
            case FTXUI_CONSTRAINT_GREATER_THAN: constraint = ftxui::Constraint::GREATER_THAN; break;
            case FTXUI_CONSTRAINT_EQUAL: constraint = ftxui::Constraint::EQUAL; break;
        }
        return el | ftxui::size(width_or_height, constraint, value);
    });
}

ftxui_component_handle_t ftxui_component_hcenter(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::hcenter(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_vcenter(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::vcenter(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_center(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::center(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_align_right(ftxui_component_handle_t component) {
    return apply_component_modifier(component, [](ftxui::Element el) {
        return ftxui::align_right(std::move(el));
    });
}

ftxui_component_handle_t ftxui_component_poll(ftxui_app_handle_t /*app*/, void (*on_poll)(void*), void* userdata) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Renderer([on_poll, userdata] {
        if (on_poll) on_poll(userdata);
        return ftxui::text("");
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Direction helper ---
static ftxui::Direction to_ftxui_direction(ftxui_direction_t d) {
    switch (d) {
        case FTXUI_DIRECTION_UP: return ftxui::Direction::Up;
        case FTXUI_DIRECTION_DOWN: return ftxui::Direction::Down;
        case FTXUI_DIRECTION_LEFT: return ftxui::Direction::Left;
        case FTXUI_DIRECTION_RIGHT: return ftxui::Direction::Right;
        default: return ftxui::Direction::Right;
    }
}

// --- CatchEvent ---
struct FTXUIEventWrapper {
    ftxui::Event event;
    std::string debug_str;
    std::string character_str;
    FTXUIEventWrapper(ftxui::Event e) : event(std::move(e)), debug_str(event.DebugString()),
        character_str(event.is_character() ? event.character() : "") {}
};

const char* ftxui_event_input(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return w ? w->event.input().c_str() : nullptr;
}
const char* ftxui_event_debug_string(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return w ? w->debug_str.c_str() : nullptr;
}
bool ftxui_event_is_character(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return w && w->event.is_character();
}
const char* ftxui_event_character(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return (w && w->event.is_character()) ? w->character_str.c_str() : nullptr;
}
bool ftxui_event_is_mouse(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return w && w->event.is_mouse();
}
int ftxui_event_mouse_x(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return (w && w->event.is_mouse()) ? w->event.mouse().x : 0;
}
int ftxui_event_mouse_y(ftxui_event_handle_t event) {
    auto* w = static_cast<FTXUIEventWrapper*>(event);
    return (w && w->event.is_mouse()) ? w->event.mouse().y : 0;
}
ftxui_component_handle_t ftxui_component_catch_event(ftxui_component_handle_t component, ftxui_catch_event_callback_t callback, void* userdata) {
    auto* inner = static_cast<FTXUIComponentWrapper*>(component);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::CatchEvent(inner->component, [callback, userdata](ftxui::Event event) -> bool {
        auto* ew = new FTXUIEventWrapper(std::move(event));
        bool result = callback(static_cast<ftxui_event_handle_t>(ew), userdata);
        delete ew;
        return result;
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Focusable renderer ---
ftxui_component_handle_t ftxui_component_renderer_focusable(ftxui_focused_render_callback_t callback, void* userdata) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Renderer([callback, userdata](bool focused) {
        ftxui_element_handle_t h = callback(focused, userdata);
        ftxui::Element el = std::move(static_cast<FTXUIElementWrapper*>(h)->element);
        ftxui_element_destroy(h);
        return el;
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Component render decorator ---
ftxui_component_handle_t ftxui_component_renderer_with_inner(ftxui_component_handle_t component, ftxui_inner_render_callback_t callback, void* userdata) {
    auto* inner = static_cast<FTXUIComponentWrapper*>(component);
    ftxui::Component inner_comp = inner->component;
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Renderer(inner_comp, [callback, userdata, inner_comp]() {
        ftxui::Element inner_el = inner_comp->Render();
        auto* ew = new FTXUIElementWrapper();
        ew->element = std::move(inner_el);
        ftxui_element_handle_t inner_h = static_cast<ftxui_element_handle_t>(ew);
        ftxui_element_handle_t result_h = callback(inner_h, userdata);
        ftxui::Element result = std::move(static_cast<FTXUIElementWrapper*>(result_h)->element);
        delete static_cast<FTXUIElementWrapper*>(result_h);
        return result;
    });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Directional int slider ---
ftxui_component_handle_t ftxui_component_slider_int_direction(int* value, int min, int max, int increment, ftxui_direction_t direction) {
    auto* wrapper = new FTXUIComponentWrapper();
    ftxui::SliderOption<int> opt;
    opt.value = value;
    opt.min = min;
    opt.max = max;
    opt.increment = increment;
    opt.direction = to_ftxui_direction(direction);
    wrapper->component = ftxui::Slider(opt);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Float sliders ---
ftxui_component_handle_t ftxui_component_slider_float(const char* label, float* value, float min, float max, float increment) {
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Slider(label ? label : "", value, min, max, increment);
    return static_cast<ftxui_component_handle_t>(wrapper);
}
ftxui_component_handle_t ftxui_component_slider_float_direction(float* value, float min, float max, float increment, ftxui_direction_t direction, ftxui_color_handle_t color_active, ftxui_color_handle_t color_inactive) {
    auto* wrapper = new FTXUIComponentWrapper();
    ftxui::SliderOption<float> opt;
    opt.value = value;
    opt.min = min;
    opt.max = max;
    opt.increment = increment;
    opt.direction = to_ftxui_direction(direction);
    if (color_active) opt.color_active = *static_cast<ftxui::Color*>(color_active);
    if (color_inactive) opt.color_inactive = *static_cast<ftxui::Color*>(color_inactive);
    wrapper->component = ftxui::Slider(opt);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Horizontal menu ---
ftxui_component_handle_t ftxui_component_menu_horizontal(const char** entries, int count, int* selected) {
    auto* wrapper = new FTXUIComponentWrapper();
    std::vector<std::string> v;
    for (int i = 0; i < count; ++i) v.push_back(entries[i]);
    wrapper->component = ftxui::Menu(std::move(v), selected, ftxui::MenuOption::Horizontal());
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- ResizableSplit with options ---
ftxui_component_handle_t ftxui_component_resizable_split_opt(ftxui_resizable_split_option_t option) {
    auto* main_w = static_cast<FTXUIComponentWrapper*>(option.main);
    auto* back_w = static_cast<FTXUIComponentWrapper*>(option.back);
    auto* wrapper = new FTXUIComponentWrapper();
    ftxui::ResizableSplitOption opt;
    opt.main = main_w->component;
    opt.back = back_w->component;
    opt.direction = to_ftxui_direction(option.direction);
    opt.main_size = option.main_size;
    if (option.min_size) opt.min = option.min_size;
    if (option.max_size) opt.max = option.max_size;
    if (option.separator_func) {
        auto func = option.separator_func;
        auto userdata = option.separator_userdata;
        opt.separator_func = [func, userdata]() -> ftxui::Element {
            auto* w = static_cast<FTXUIElementWrapper*>(func(userdata));
            auto elem = std::move(w->element);
            delete w;
            return elem;
        };
    }
    wrapper->component = ftxui::ResizableSplit(opt);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Gridbox element ---
ftxui_element_handle_t ftxui_element_gridbox(ftxui_element_handle_t* cells, int total_cells, int* row_lengths, int row_count) {
    std::vector<std::vector<ftxui::Element>> rows;
    int idx = 0;
    for (int r = 0; r < row_count; r++) {
        std::vector<ftxui::Element> row;
        for (int c = 0; c < row_lengths[r]; c++) {
            auto* w = static_cast<FTXUIElementWrapper*>(cells[idx++]);
            row.push_back(std::move(w->element));
            delete w;
        }
        rows.push_back(std::move(row));
    }
    return create_element_wrapper(ftxui::gridbox(std::move(rows)));
}

// --- MenuEntry with animated colors ---
ftxui_component_handle_t ftxui_component_menu_entry_animated(const char* label, ftxui_animated_colors_option_t animated_colors) {
    auto* wrapper = new FTXUIComponentWrapper();
    ftxui::MenuEntryOption opt;
    opt.animated_colors = to_ftxui_animated_colors(animated_colors);
    wrapper->component = ftxui::MenuEntry(label ? label : "", opt);
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Animated horizontal menu ---
ftxui_component_handle_t ftxui_component_menu_horizontal_animated(const char** entries, int count, int* selected) {
    std::vector<std::string> v;
    for (int i = 0; i < count; i++) v.push_back(entries[i] ? entries[i] : "");
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Menu(std::move(v), selected, ftxui::MenuOption::HorizontalAnimated());
    return static_cast<ftxui_component_handle_t>(wrapper);
}

ftxui_component_handle_t ftxui_component_menu_toggle(const char** entries, int count, int* selected) {
    std::vector<std::string> v;
    for (int i = 0; i < count; i++) v.push_back(entries[i] ? entries[i] : "");
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Menu(std::move(v), selected, ftxui::MenuOption::Toggle());
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- RequestAnimationFrame ---
void ftxui_app_request_animation_frame(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    if (ftxui_app) {
        ftxui_app->RequestAnimationFrame();
    }
}

// --- Canvas ---
#include <ftxui/dom/canvas.hpp>

ftxui_canvas_handle_t ftxui_canvas_create(int width, int height) {
    return static_cast<ftxui_canvas_handle_t>(new ftxui::Canvas(width, height));
}

void ftxui_canvas_destroy(ftxui_canvas_handle_t canvas) {
    delete static_cast<ftxui::Canvas*>(canvas);
}

void ftxui_canvas_draw_text(ftxui_canvas_handle_t canvas, int x, int y, const char* text) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c && text) c->DrawText(x, y, text);
}

void ftxui_canvas_draw_text_color(ftxui_canvas_handle_t canvas, int x, int y, const char* text, ftxui_color_handle_t color) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    auto* col = static_cast<ftxui::Color*>(color);
    if (c && text && col) c->DrawText(x, y, text, *col);
}

void ftxui_canvas_draw_point_line(ftxui_canvas_handle_t canvas, int x1, int y1, int x2, int y2, ftxui_color_handle_t color) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    auto* col = static_cast<ftxui::Color*>(color);
    if (!c) return;
    if (col) c->DrawPointLine(x1, y1, x2, y2, *col);
    else c->DrawPointLine(x1, y1, x2, y2);
}

void ftxui_canvas_draw_block_line(ftxui_canvas_handle_t canvas, int x1, int y1, int x2, int y2, ftxui_color_handle_t color) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    auto* col = static_cast<ftxui::Color*>(color);
    if (!c) return;
    if (col) c->DrawBlockLine(x1, y1, x2, y2, *col);
    else c->DrawBlockLine(x1, y1, x2, y2);
}

void ftxui_canvas_draw_point_circle(ftxui_canvas_handle_t canvas, int x, int y, int radius) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawPointCircle(x, y, radius);
}

void ftxui_canvas_draw_point_circle_filled(ftxui_canvas_handle_t canvas, int x, int y, int radius) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawPointCircleFilled(x, y, radius);
}

void ftxui_canvas_draw_block_circle(ftxui_canvas_handle_t canvas, int x, int y, int radius) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawBlockCircle(x, y, radius);
}

void ftxui_canvas_draw_block_circle_filled(ftxui_canvas_handle_t canvas, int x, int y, int radius) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawBlockCircleFilled(x, y, radius);
}

void ftxui_canvas_draw_point_ellipse(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawPointEllipse(x, y, rx, ry);
}

void ftxui_canvas_draw_point_ellipse_filled(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawPointEllipseFilled(x, y, rx, ry);
}

void ftxui_canvas_draw_block_ellipse(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawBlockEllipse(x, y, rx, ry);
}

void ftxui_canvas_draw_block_ellipse_filled(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (c) c->DrawBlockEllipseFilled(x, y, rx, ry);
}

ftxui_element_handle_t ftxui_element_canvas_ref(ftxui_canvas_handle_t canvas) {
    auto* c = static_cast<ftxui::Canvas*>(canvas);
    if (!c) return nullptr;
    // Copy the canvas so the element owns its own data (safe even if caller destroys canvas).
    ftxui::Canvas copy = *c;
    return create_element_wrapper(ftxui::canvas(std::move(copy)));
}

// --- graph element ---
ftxui_element_handle_t ftxui_element_graph(ftxui_graph_callback_t callback, void* userdata) {
    if (!callback) return nullptr;
    auto graph_fn = [callback, userdata](int width, int height) -> std::vector<int> {
        std::vector<int> output(width, 0);
        callback(width, height, output.data(), userdata);
        return output;
    };
    return create_element_wrapper(ftxui::graph(graph_fn));
}

// --- LinearGradient ---
#include <ftxui/dom/linear_gradient.hpp>

ftxui_linear_gradient_handle_t ftxui_linear_gradient_create() {
    return static_cast<ftxui_linear_gradient_handle_t>(new ftxui::LinearGradient());
}

void ftxui_linear_gradient_destroy(ftxui_linear_gradient_handle_t gradient) {
    delete static_cast<ftxui::LinearGradient*>(gradient);
}

void ftxui_linear_gradient_angle(ftxui_linear_gradient_handle_t gradient, float angle) {
    auto* g = static_cast<ftxui::LinearGradient*>(gradient);
    if (g) g->Angle(angle);
}

void ftxui_linear_gradient_stop(ftxui_linear_gradient_handle_t gradient, ftxui_color_handle_t color) {
    auto* g = static_cast<ftxui::LinearGradient*>(gradient);
    auto* col = static_cast<ftxui::Color*>(color);
    if (g && col) g->Stop(*col);
}

void ftxui_linear_gradient_stop_at(ftxui_linear_gradient_handle_t gradient, ftxui_color_handle_t color, float position) {
    auto* g = static_cast<ftxui::LinearGradient*>(gradient);
    auto* col = static_cast<ftxui::Color*>(color);
    if (g && col) g->Stop(*col, position);
}

ftxui_element_handle_t ftxui_element_bgcolor_linear_gradient(ftxui_element_handle_t element, ftxui_linear_gradient_handle_t gradient) {
    auto* ew = static_cast<FTXUIElementWrapper*>(element);
    auto* g = static_cast<ftxui::LinearGradient*>(gradient);
    if (!ew || !g) return nullptr;
    return create_element_wrapper(ew->element | ftxui::bgcolor(*g));
}

ftxui_element_handle_t ftxui_element_color_linear_gradient(ftxui_element_handle_t element, ftxui_linear_gradient_handle_t gradient) {
    auto* ew = static_cast<FTXUIElementWrapper*>(element);
    auto* g = static_cast<ftxui::LinearGradient*>(gradient);
    if (!ew || !g) return nullptr;
    return create_element_wrapper(ew->element | ftxui::color(*g));
}

// --- flexbox element ---
#include <ftxui/dom/flexbox_config.hpp>

ftxui_element_handle_t ftxui_element_flexbox(ftxui_element_handle_t* elements, int count, ftxui_flexbox_config_t config) {
    ftxui::Elements elems;
    for (int i = 0; i < count; i++) {
        auto* w = static_cast<FTXUIElementWrapper*>(elements[i]);
        if (w) {
            elems.push_back(std::move(w->element));
            delete w;
        }
    }

    ftxui::FlexboxConfig ftx_config;
    ftx_config.direction = static_cast<ftxui::FlexboxConfig::Direction>(config.direction);
    ftx_config.wrap = static_cast<ftxui::FlexboxConfig::Wrap>(config.wrap);
    ftx_config.justify_content = static_cast<ftxui::FlexboxConfig::JustifyContent>(config.justify_content);
    ftx_config.align_items = static_cast<ftxui::FlexboxConfig::AlignItems>(config.align_items);
    ftx_config.align_content = static_cast<ftxui::FlexboxConfig::AlignContent>(config.align_content);
    ftx_config.gap_x = config.gap_x;
    ftx_config.gap_y = config.gap_y;

    return create_element_wrapper(ftxui::flexbox(std::move(elems), ftx_config));
}

// --- Table ---
#include <ftxui/dom/table.hpp>

struct FTXUITableWrapper {
    ftxui::Table table;
};

struct FTXUITableSelectionWrapper {
    ftxui::TableSelection selection;
};

ftxui_table_handle_t ftxui_table_create(const char** cells, int rows, int cols) {
    std::vector<std::vector<std::string>> data;
    for (int r = 0; r < rows; r++) {
        std::vector<std::string> row;
        for (int c = 0; c < cols; c++) {
            const char* cell = cells[r * cols + c];
            row.push_back(cell ? cell : "");
        }
        data.push_back(std::move(row));
    }
    auto* wrapper = new FTXUITableWrapper();
    wrapper->table = ftxui::Table(data);
    return static_cast<ftxui_table_handle_t>(wrapper);
}

void ftxui_table_destroy(ftxui_table_handle_t table) {
    delete static_cast<FTXUITableWrapper*>(table);
}

ftxui_element_handle_t ftxui_table_render(ftxui_table_handle_t table) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return create_element_wrapper(tw->table.Render());
}

static ftxui::BorderStyle to_ftxui_border_style(ftxui_border_style_t style) {
    switch (style) {
        case FTXUI_BORDER_STYLE_DASHED: return ftxui::DASHED;
        case FTXUI_BORDER_STYLE_HEAVY:  return ftxui::HEAVY;
        case FTXUI_BORDER_STYLE_DOUBLE: return ftxui::DOUBLE;
        case FTXUI_BORDER_STYLE_ROUNDED: return ftxui::ROUNDED;
        case FTXUI_BORDER_STYLE_EMPTY:  return ftxui::EMPTY;
        default: return ftxui::LIGHT;
    }
}

ftxui_table_selection_handle_t ftxui_table_select_all(ftxui_table_handle_t table) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return static_cast<ftxui_table_selection_handle_t>(new FTXUITableSelectionWrapper{tw->table.SelectAll()});
}

ftxui_table_selection_handle_t ftxui_table_select_row(ftxui_table_handle_t table, int row) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return static_cast<ftxui_table_selection_handle_t>(new FTXUITableSelectionWrapper{tw->table.SelectRow(row)});
}

ftxui_table_selection_handle_t ftxui_table_select_rows(ftxui_table_handle_t table, int from, int to) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return static_cast<ftxui_table_selection_handle_t>(new FTXUITableSelectionWrapper{tw->table.SelectRows(from, to)});
}

ftxui_table_selection_handle_t ftxui_table_select_column(ftxui_table_handle_t table, int col) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return static_cast<ftxui_table_selection_handle_t>(new FTXUITableSelectionWrapper{tw->table.SelectColumn(col)});
}

ftxui_table_selection_handle_t ftxui_table_select_cell(ftxui_table_handle_t table, int col, int row) {
    auto* tw = static_cast<FTXUITableWrapper*>(table);
    if (!tw) return nullptr;
    return static_cast<ftxui_table_selection_handle_t>(new FTXUITableSelectionWrapper{tw->table.SelectCell(col, row)});
}

void ftxui_table_selection_destroy(ftxui_table_selection_handle_t sel) {
    delete static_cast<FTXUITableSelectionWrapper*>(sel);
}

void ftxui_table_selection_border(ftxui_table_selection_handle_t sel, ftxui_border_style_t style) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    if (sw) sw->selection.Border(to_ftxui_border_style(style));
}

void ftxui_table_selection_border_color(ftxui_table_selection_handle_t sel, ftxui_border_style_t style, ftxui_color_handle_t color) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    auto* col = static_cast<ftxui::Color*>(color);
    if (sw && col) {
        ftxui::Color c = *col;
        sw->selection.Border(to_ftxui_border_style(style), [c](ftxui::Element e) { return e | ftxui::color(c); });
    }
}

void ftxui_table_selection_separator_vertical(ftxui_table_selection_handle_t sel, ftxui_border_style_t style) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    if (sw) sw->selection.SeparatorVertical(to_ftxui_border_style(style));
}

void ftxui_table_selection_decorate_bold(ftxui_table_selection_handle_t sel) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    if (sw) sw->selection.Decorate(ftxui::bold);
}

void ftxui_table_selection_decorate_cells_align_right(ftxui_table_selection_handle_t sel) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    if (sw) sw->selection.DecorateCells(ftxui::align_right);
}

void ftxui_table_selection_decorate_cells_color(ftxui_table_selection_handle_t sel, ftxui_color_handle_t color) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    auto* col = static_cast<ftxui::Color*>(color);
    if (sw && col) {
        ftxui::Color c = *col;
        sw->selection.DecorateCells([c](ftxui::Element e) { return e | ftxui::color(c); });
    }
}

void ftxui_table_selection_decorate_cells_color_alternate_row(ftxui_table_selection_handle_t sel, ftxui_color_handle_t color, int modulo, int offset) {
    auto* sw = static_cast<FTXUITableSelectionWrapper*>(sel);
    auto* col = static_cast<ftxui::Color*>(color);
    if (sw && col) {
        ftxui::Color c = *col;
        sw->selection.DecorateCellsAlternateRow([c](ftxui::Element e) { return e | ftxui::color(c); }, modulo, offset);
    }
}

// --- Window component ---
#include <ftxui/component/component_options.hpp>

struct FTXUIWindowWrapper {
    std::string title;              // kept alive for the ConstStringRef
    ftxui::Component component;
};

ftxui_component_handle_t ftxui_component_window(ftxui_window_options_t options) {
    auto* win_wrapper = new FTXUIWindowWrapper();
    if (options.title) win_wrapper->title = options.title;

    ftxui::WindowOptions win_opts;
    if (options.inner) {
        auto* inner = static_cast<FTXUIComponentWrapper*>(options.inner);
        win_opts.inner = inner->component;
    }
    win_opts.title = win_wrapper->title;

    if (options.left) win_opts.left = options.left;
    else win_opts.left = options.left_default;
    if (options.top) win_opts.top = options.top;
    else win_opts.top = options.top_default;
    if (options.width) win_opts.width = options.width;
    else win_opts.width = options.width_default;
    if (options.height) win_opts.height = options.height;
    else win_opts.height = options.height_default;

    win_wrapper->component = ftxui::Window(win_opts);

    // Wrap in a component wrapper that owns the FTXUIWindowWrapper via shared_ptr.
    auto win_wrapper_shared = std::shared_ptr<FTXUIWindowWrapper>(win_wrapper);
    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Renderer(win_wrapper->component,
        [win_wrapper_shared] { return win_wrapper_shared->component->Render(); });
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Loop ---
#include <ftxui/component/loop.hpp>

struct FTXUILoopWrapper {
    ftxui::Loop loop;
    FTXUILoopWrapper(ftxui::App* app, ftxui::Component component)
        : loop(app, component) {}
};

ftxui_loop_handle_t ftxui_loop_create(ftxui_app_handle_t app, ftxui_component_handle_t component) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    auto* wrapper = static_cast<FTXUIComponentWrapper*>(component);
    if (!ftxui_app || !wrapper) return nullptr;
    return static_cast<ftxui_loop_handle_t>(new FTXUILoopWrapper(ftxui_app, wrapper->component));
}

bool ftxui_loop_has_quitted(ftxui_loop_handle_t loop) {
    auto* lw = static_cast<FTXUILoopWrapper*>(loop);
    return lw ? lw->loop.HasQuitted() : true;
}

void ftxui_loop_run_once(ftxui_loop_handle_t loop) {
    auto* lw = static_cast<FTXUILoopWrapper*>(loop);
    if (lw) lw->loop.RunOnce();
}

void ftxui_loop_destroy(ftxui_loop_handle_t loop) {
    delete static_cast<FTXUILoopWrapper*>(loop);
}

// --- ColorInfo ---
#include <ftxui/screen/color_info.hpp>
#include <algorithm>

ftxui_color_info_t* ftxui_color_info_sorted_2d(int* num_rows, int* max_cols) {
    auto info_columns = ftxui::ColorInfoSorted2D();
    int rows = static_cast<int>(info_columns.size());
    int cols = 0;
    for (auto& col : info_columns) {
        cols = std::max(cols, static_cast<int>(col.size()));
    }
    *num_rows = rows;
    *max_cols = cols;
    if (rows == 0 || cols == 0) return nullptr;

    auto* result = new ftxui_color_info_t[rows * cols];
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            int idx = r * cols + c;
            if (c < static_cast<int>(info_columns[r].size())) {
                result[idx].index_256 = info_columns[r][c].index_256;
                result[idx].name = info_columns[r][c].name;
            } else {
                result[idx].index_256 = -1;
                result[idx].name = "";
            }
        }
    }
    return result;
}

void ftxui_color_info_free(ftxui_color_info_t* data) {
    delete[] data;
}

// --- Dropdown with custom transform ---
#include <ftxui/component/component_options.hpp>

ftxui_component_handle_t ftxui_component_dropdown_custom(
    const char** entries, int count, int* selected,
    ftxui_dropdown_transform_callback_t transform, void* transform_userdata,
    ftxui_button_transform_t entry_transform, void* entry_transform_userdata
) {
    std::vector<std::string> v;
    for (int i = 0; i < count; i++) v.push_back(entries[i] ? entries[i] : "");

    ftxui::DropdownOption opt;
    opt.radiobox.entries = std::move(v);
    if (selected) opt.radiobox.selected = selected;

    if (transform) {
        opt.transform = [transform, transform_userdata](bool open, ftxui::Element checkbox, ftxui::Element radiobox) -> ftxui::Element {
            // Copy the shared_ptrs so FTXUI's own references remain intact.
            auto* cb = new FTXUIElementWrapper();
            cb->element = checkbox;
            auto* rb = new FTXUIElementWrapper();
            rb->element = radiobox;

            ftxui_element_handle_t result_h = transform(
                open,
                static_cast<ftxui_element_handle_t>(cb),
                static_cast<ftxui_element_handle_t>(rb),
                transform_userdata
            );
            // cb/rb are consumed by Kotlin element combinators (which delete wrappers).
            // Do NOT free them here.

            auto* rw = static_cast<FTXUIElementWrapper*>(result_h);
            ftxui::Element result = std::move(rw->element);
            delete rw;
            return result;
        };
    }

    if (entry_transform) {
        opt.radiobox.transform = [entry_transform, entry_transform_userdata](const ftxui::EntryState& state) -> ftxui::Element {
            ftxui_entry_state_t c_state = to_ftxui_c_entry_state(state);
            ftxui_element_handle_t h = entry_transform(c_state, entry_transform_userdata);
            if (!h) return ftxui::text(state.label);
            auto* w = static_cast<FTXUIElementWrapper*>(h);
            ftxui::Element el = std::move(w->element);
            delete w;
            return el;
        };
    }

    auto* wrapper = new FTXUIComponentWrapper();
    wrapper->component = ftxui::Dropdown(std::move(opt));
    return static_cast<ftxui_component_handle_t>(wrapper);
}

// --- Cell style / selectionStyle ---
#include <ftxui/screen/cell.hpp>

ftxui_element_handle_t ftxui_element_selection_style(
    ftxui_element_handle_t element,
    ftxui_cell_style_callback_t callback,
    void* userdata
) {
    if (!callback) return element;
    return apply_element_modifier(element, [callback, userdata](ftxui::Element el) {
        return el | ftxui::selectionStyle([callback, userdata](ftxui::Cell& cell) {
            ftxui_cell_t c;
            c.blink = cell.blink;
            c.bold = cell.bold;
            c.dim = cell.dim;
            c.italic = cell.italic;
            c.inverted = cell.inverted;
            c.underlined = cell.underlined;
            c.underlined_double = cell.underlined_double;
            c.strikethrough = cell.strikethrough;
            c.automerge = cell.automerge;
            callback(&c, userdata);
            cell.blink = c.blink;
            cell.bold = c.bold;
            cell.dim = c.dim;
            cell.italic = c.italic;
            cell.inverted = c.inverted;
            cell.underlined = c.underlined;
            cell.underlined_double = c.underlined_double;
            cell.strikethrough = c.strikethrough;
            cell.automerge = c.automerge;
        });
    });
}

// --- App selection ---
void ftxui_app_selection_change(ftxui_app_handle_t app, void (*callback)(void*), void* userdata) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    if (ftxui_app && callback) {
        ftxui_app->SelectionChange([callback, userdata] { callback(userdata); });
    }
}

char* ftxui_app_get_selection(ftxui_app_handle_t app) {
    auto* ftxui_app = static_cast<ftxui::App*>(app);
    if (!ftxui_app) return strdup("");
    return strdup(ftxui_app->GetSelection().c_str());
}