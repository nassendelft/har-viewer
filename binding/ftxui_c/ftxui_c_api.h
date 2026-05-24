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
typedef void* ftxui_color_handle_t; // New opaque handle for Color class

/**
 * @brief A callback function for rendering a component.
 * 
 * @return ftxui_element_handle_t The element to render.
 */
typedef ftxui_element_handle_t (*ftxui_render_callback_t)(void* userdata);

// --- Colors ---

// C Enums for FTXUI Color Palettes
typedef enum {
  FTXUI_PALETTE1_DEFAULT = 0, // Transparent
} ftxui_palette1_t;

typedef enum {
  FTXUI_PALETTE16_BLACK = 0,
  FTXUI_PALETTE16_RED = 1,
  FTXUI_PALETTE16_GREEN = 2,
  FTXUI_PALETTE16_YELLOW = 3,
  FTXUI_PALETTE16_BLUE = 4,
  FTXUI_PALETTE16_MAGENTA = 5,
  FTXUI_PALETTE16_CYAN = 6,
  FTXUI_PALETTE16_GRAY_LIGHT = 7,
  FTXUI_PALETTE16_GRAY_DARK = 8,
  FTXUI_PALETTE16_RED_LIGHT = 9,
  FTXUI_PALETTE16_GREEN_LIGHT = 10,
  FTXUI_PALETTE16_YELLOW_LIGHT = 11,
  FTXUI_PALETTE16_BLUE_LIGHT = 12,
  FTXUI_PALETTE16_MAGENTA_LIGHT = 13,
  FTXUI_PALETTE16_CYAN_LIGHT = 14,
  FTXUI_PALETTE16_WHITE = 15,
} ftxui_palette16_t;

typedef enum {
  FTXUI_PALETTE256_AQUAMARINE1 = 122,
  FTXUI_PALETTE256_AQUAMARINE1BIS = 86,
  FTXUI_PALETTE256_AQUAMARINE3 = 79,
  FTXUI_PALETTE256_BLUE1 = 21,
  FTXUI_PALETTE256_BLUE3 = 19,
  FTXUI_PALETTE256_BLUE3BIS = 20,
  FTXUI_PALETTE256_BLUEVIOLET = 57,
  FTXUI_PALETTE256_CADETBLUE = 72,
  FTXUI_PALETTE256_CADETBLUEBIS = 73,
  FTXUI_PALETTE256_CHARTREUSE1 = 118,
  FTXUI_PALETTE256_CHARTREUSE2 = 112,
  FTXUI_PALETTE256_CHARTREUSE2BIS = 82,
  FTXUI_PALETTE256_CHARTREUSE3 = 70,
  FTXUI_PALETTE256_CHARTREUSE3BIS = 76,
  FTXUI_PALETTE256_CHARTREUSE4 = 64,
  FTXUI_PALETTE256_CORNFLOWERBLUE = 69,
  FTXUI_PALETTE256_CORNSILK1 = 230,
  FTXUI_PALETTE256_CYAN1 = 51,
  FTXUI_PALETTE256_CYAN2 = 50,
  FTXUI_PALETTE256_CYAN3 = 43,
  FTXUI_PALETTE256_DARKBLUE = 18,
  FTXUI_PALETTE256_DARKCYAN = 36,
  FTXUI_PALETTE256_DARKGOLDENROD = 136,
  FTXUI_PALETTE256_DARKGREEN = 22,
  FTXUI_PALETTE256_DARKKHAKI = 143,
  FTXUI_PALETTE256_DARKMAGENTA = 90,
  FTXUI_PALETTE256_DARKMAGENTABIS = 91,
  FTXUI_PALETTE256_DARKOLIVEGREEN1 = 191,
  FTXUI_PALETTE256_DARKOLIVEGREEN1BIS = 192,
  FTXUI_PALETTE256_DARKOLIVEGREEN2 = 155,
  FTXUI_PALETTE256_DARKOLIVEGREEN3 = 107,
  FTXUI_PALETTE256_DARKOLIVEGREEN3BIS = 113,
  FTXUI_PALETTE256_DARKOLIVEGREEN3TER = 149,
  FTXUI_PALETTE256_DARKORANGE = 208,
  FTXUI_PALETTE256_DARKORANGE3 = 130,
  FTXUI_PALETTE256_DARKORANGE3BIS = 166,
  FTXUI_PALETTE256_DARKRED = 52,
  FTXUI_PALETTE256_DARKREDBIS = 88,
  FTXUI_PALETTE256_DARKSEAGREEN = 108,
  FTXUI_PALETTE256_DARKSEAGREEN1 = 158,
  FTXUI_PALETTE256_DARKSEAGREEN1BIS = 193,
  FTXUI_PALETTE256_DARKSEAGREEN2 = 151,
  FTXUI_PALETTE256_DARKSEAGREEN2BIS = 157,
  FTXUI_PALETTE256_DARKSEAGREEN3 = 115,
  FTXUI_PALETTE256_DARKSEAGREEN3BIS = 150,
  FTXUI_PALETTE256_DARKSEAGREEN4 = 65,
  FTXUI_PALETTE256_DARKSEAGREEN4BIS = 71,
  FTXUI_PALETTE256_DARKSLATEGRAY1 = 123,
  FTXUI_PALETTE256_DARKSLATEGRAY2 = 87,
  FTXUI_PALETTE256_DARKSLATEGRAY3 = 116,
  FTXUI_PALETTE256_DARKTURQUOISE = 44,
  FTXUI_PALETTE256_DARKVIOLET = 128,
  FTXUI_PALETTE256_DARKVIOLETBIS = 92,
  FTXUI_PALETTE256_DEEPPINK1 = 198,
  FTXUI_PALETTE256_DEEPPINK1BIS = 199,
  FTXUI_PALETTE256_DEEPPINK2 = 197,
  FTXUI_PALETTE256_DEEPPINK3 = 161,
  FTXUI_PALETTE256_DEEPPINK3BIS = 162,
  FTXUI_PALETTE256_DEEPPINK4 = 125,
  FTXUI_PALETTE256_DEEPPINK4BIS = 89,
  FTXUI_PALETTE256_DEEPPINK4TER = 53,
  FTXUI_PALETTE256_DEEPSKYBLUE1 = 39,
  FTXUI_PALETTE256_DEEPSKYBLUE2 = 38,
  FTXUI_PALETTE256_DEEPSKYBLUE3 = 31,
  FTXUI_PALETTE256_DEEPSKYBLUE3BIS = 32,
  FTXUI_PALETTE256_DEEPSKYBLUE4 = 23,
  FTXUI_PALETTE256_DEEPSKYBLUE4BIS = 24,
  FTXUI_PALETTE256_DEEPSKYBLUE4TER = 25,
  FTXUI_PALETTE256_DODGERBLUE1 = 33,
  FTXUI_PALETTE256_DODGERBLUE2 = 27,
  FTXUI_PALETTE256_DODGERBLUE3 = 26,
  FTXUI_PALETTE256_GOLD1 = 220,
  FTXUI_PALETTE256_GOLD3 = 142,
  FTXUI_PALETTE256_GOLD3BIS = 178,
  FTXUI_PALETTE256_GREEN1 = 46,
  FTXUI_PALETTE256_GREEN3 = 34,
  FTXUI_PALETTE256_GREEN3BIS = 40,
  FTXUI_PALETTE256_GREEN4 = 28,
  FTXUI_PALETTE256_GREENYELLOW = 154,
  FTXUI_PALETTE256_GREY0 = 16,
  FTXUI_PALETTE256_GREY100 = 231,
  FTXUI_PALETTE256_GREY11 = 234,
  FTXUI_PALETTE256_GREY15 = 235,
  FTXUI_PALETTE256_GREY19 = 236,
  FTXUI_PALETTE256_GREY23 = 237,
  FTXUI_PALETTE256_GREY27 = 238,
  FTXUI_PALETTE256_GREY3 = 232,
  FTXUI_PALETTE256_GREY30 = 239,
  FTXUI_PALETTE256_GREY35 = 240,
  FTXUI_PALETTE256_GREY37 = 59,
  FTXUI_PALETTE256_GREY39 = 241,
  FTXUI_PALETTE256_GREY42 = 242,
  FTXUI_PALETTE256_GREY46 = 243,
  FTXUI_PALETTE256_GREY50 = 244,
  FTXUI_PALETTE256_GREY53 = 102,
  FTXUI_PALETTE256_GREY54 = 245,
  FTXUI_PALETTE256_GREY58 = 246,
  FTXUI_PALETTE256_GREY62 = 247,
  FTXUI_PALETTE256_GREY63 = 139,
  FTXUI_PALETTE256_GREY66 = 248,
  FTXUI_PALETTE256_GREY69 = 145,
  FTXUI_PALETTE256_GREY7 = 233,
  FTXUI_PALETTE256_GREY70 = 249,
  FTXUI_PALETTE256_GREY74 = 250,
  FTXUI_PALETTE256_GREY78 = 251,
  FTXUI_PALETTE256_GREY82 = 252,
  FTXUI_PALETTE256_GREY84 = 188,
  FTXUI_PALETTE256_GREY85 = 253,
  FTXUI_PALETTE256_GREY89 = 254,
  FTXUI_PALETTE256_GREY93 = 255,
  FTXUI_PALETTE256_HONEYDEW2 = 194,
  FTXUI_PALETTE256_HOTPINK = 205,
  FTXUI_PALETTE256_HOTPINK2 = 169,
  FTXUI_PALETTE256_HOTPINK3 = 132,
  FTXUI_PALETTE256_HOTPINK3BIS = 168,
  FTXUI_PALETTE256_HOTPINKBIS = 206,
  FTXUI_PALETTE256_INDIANRED = 131,
  FTXUI_PALETTE256_INDIANRED1 = 203,
  FTXUI_PALETTE256_INDIANRED1BIS = 204,
  FTXUI_PALETTE256_INDIANREDBIS = 167,
  FTXUI_PALETTE256_KHAKI1 = 228,
  FTXUI_PALETTE256_KHAKI3 = 185,
  FTXUI_PALETTE256_LIGHTCORAL = 210,
  FTXUI_PALETTE256_LIGHTCYAN1BIS = 195,
  FTXUI_PALETTE256_LIGHTCYAN3 = 152,
  FTXUI_PALETTE256_LIGHTGOLDENROD1 = 227,
  FTXUI_PALETTE256_LIGHTGOLDENROD2 = 186,
  FTXUI_PALETTE256_LIGHTGOLDENROD2BIS = 221,
  FTXUI_PALETTE256_LIGHTGOLDENROD2TER = 222,
  FTXUI_PALETTE256_LIGHTGOLDENROD3 = 179,
  FTXUI_PALETTE256_LIGHTGREEN = 119,
  FTXUI_PALETTE256_LIGHTGREENBIS = 120,
  FTXUI_PALETTE256_LIGHTPINK1 = 217,
  FTXUI_PALETTE256_LIGHTPINK3 = 174,
  FTXUI_PALETTE256_LIGHTPINK4 = 95,
  FTXUI_PALETTE256_LIGHTSALMON1 = 216,
  FTXUI_PALETTE256_LIGHTSALMON3 = 137,
  FTXUI_PALETTE256_LIGHTSALMON3BIS = 173,
  FTXUI_PALETTE256_LIGHTSEAGREEN = 37,
  FTXUI_PALETTE256_LIGHTSKYBLUE1 = 153,
  FTXUI_PALETTE256_LIGHTSKYBLUE3 = 109,
  FTXUI_PALETTE256_LIGHTSKYBLUE3BIS = 110,
  FTXUI_PALETTE256_LIGHTSLATEBLUE = 105,
  FTXUI_PALETTE256_LIGHTSLATEGREY = 103,
  FTXUI_PALETTE256_LIGHTSTEELBLUE = 147,
  FTXUI_PALETTE256_LIGHTSTEELBLUE1 = 189,
  FTXUI_PALETTE256_LIGHTSTEELBLUE3 = 146,
  FTXUI_PALETTE256_LIGHTYELLOW3 = 187,
  FTXUI_PALETTE256_MAGENTA1 = 201,
  FTXUI_PALETTE256_MAGENTA2 = 165,
  FTXUI_PALETTE256_MAGENTA2BIS = 200,
  FTXUI_PALETTE256_MAGENTA3 = 127,
  FTXUI_PALETTE256_MAGENTA3BIS = 163,
  FTXUI_PALETTE256_MAGENTA3TER = 164,
  FTXUI_PALETTE256_MEDIUMORCHID = 134,
  FTXUI_PALETTE256_MEDIUMORCHID1 = 171,
  FTXUI_PALETTE256_MEDIUMORCHID1BIS = 207,
  FTXUI_PALETTE256_MEDIUMORCHID3 = 133,
  FTXUI_PALETTE256_MEDIUMPURPLE = 104,
  FTXUI_PALETTE256_MEDIUMPURPLE1 = 141,
  FTXUI_PALETTE256_MEDIUMPURPLE2 = 135,
  FTXUI_PALETTE256_MEDIUMPURPLE2BIS = 140,
  FTXUI_PALETTE256_MEDIUMPURPLE3 = 97,
  FTXUI_PALETTE256_MEDIUMPURPLE3BIS = 98,
  FTXUI_PALETTE256_MEDIUMPURPLE4 = 60,
  FTXUI_PALETTE256_MEDIUMSPRINGGREEN = 49,
  FTXUI_PALETTE256_MEDIUMTURQUOISE = 80,
  FTXUI_PALETTE256_MEDIUMVIOLETRED = 126,
  FTXUI_PALETTE256_MISTYROSE1 = 224,
  FTXUI_PALETTE256_MISTYROSE3 = 181,
  FTXUI_PALETTE256_NAVAJOWHITE1 = 223,
  FTXUI_PALETTE256_NAVAJOWHITE3 = 144,
  FTXUI_PALETTE256_NAVYBLUE = 17,
  FTXUI_PALETTE256_ORANGE1 = 214,
  FTXUI_PALETTE256_ORANGE3 = 172,
  FTXUI_PALETTE256_ORANGE4 = 58,
  FTXUI_PALETTE256_ORANGE4BIS = 94,
  FTXUI_PALETTE256_ORANGERED1 = 202,
  FTXUI_PALETTE256_ORCHID = 170,
  FTXUI_PALETTE256_ORCHID1 = 213,
  FTXUI_PALETTE256_ORCHID2 = 212,
  FTXUI_PALETTE256_PALEGREEN1 = 121,
  FTXUI_PALETTE256_PALEGREEN1BIS = 156,
  FTXUI_PALETTE256_PALEGREEN3 = 114,
  FTXUI_PALETTE256_PALEGREEN3BIS = 77,
  FTXUI_PALETTE256_PALETURQUOISE1 = 159,
  FTXUI_PALETTE256_PALETURQUOISE4 = 66,
  FTXUI_PALETTE256_PALEVIOLETRED1 = 211,
  FTXUI_PALETTE256_PINK1 = 218,
  FTXUI_PALETTE256_PINK3 = 175,
  FTXUI_PALETTE256_PLUM1 = 219,
  FTXUI_PALETTE256_PLUM2 = 183,
  FTXUI_PALETTE256_PLUM3 = 176,
  FTXUI_PALETTE256_PLUM4 = 96,
  FTXUI_PALETTE256_PURPLE = 129,
  FTXUI_PALETTE256_PURPLE3 = 56,
  FTXUI_PALETTE256_PURPLE4 = 54,
  FTXUI_PALETTE256_PURPLE4BIS = 55,
  FTXUI_PALETTE256_PURPLEBIS = 93,
  FTXUI_PALETTE256_RED1 = 196,
  FTXUI_PALETTE256_RED3 = 124,
  FTXUI_PALETTE256_RED3BIS = 160,
  FTXUI_PALETTE256_ROSYBROWN = 138,
  FTXUI_PALETTE256_ROYALBLUE1 = 63,
  FTXUI_PALETTE256_SALMON1 = 209,
  FTXUI_PALETTE256_SANDYBROWN = 215,
  FTXUI_PALETTE256_SEAGREEN1 = 84,
  FTXUI_PALETTE256_SEAGREEN1BIS = 85,
  FTXUI_PALETTE256_SEAGREEN2 = 83,
  FTXUI_PALETTE256_SEAGREEN3 = 78,
  FTXUI_PALETTE256_SKYBLUE1 = 117,
  FTXUI_PALETTE256_SKYBLUE2 = 111,
  FTXUI_PALETTE256_SKYBLUE3 = 74,
  FTXUI_PALETTE256_SLATEBLUE1 = 99,
  FTXUI_PALETTE256_SLATEBLUE3 = 61,
  FTXUI_PALETTE256_SLATEBLUE3BIS = 62,
  FTXUI_PALETTE256_SPRINGGREEN1 = 48,
  FTXUI_PALETTE256_SPRINGGREEN2 = 42,
  FTXUI_PALETTE256_SPRINGGREEN2BIS = 47,
  FTXUI_PALETTE256_SPRINGGREEN3 = 35,
  FTXUI_PALETTE256_SPRINGGREEN3BIS = 41,
  FTXUI_PALETTE256_SPRINGGREEN4 = 29,
  FTXUI_PALETTE256_STEELBLUE = 67,
  FTXUI_PALETTE256_STEELBLUE1 = 75,
  FTXUI_PALETTE256_STEELBLUE1BIS = 81,
  FTXUI_PALETTE256_STEELBLUE3 = 68,
  FTXUI_PALETTE256_TAN = 180,
  FTXUI_PALETTE256_THISTLE1 = 225,
  FTXUI_PALETTE256_THISTLE3 = 182,
  FTXUI_PALETTE256_TURQUOISE2 = 45,
  FTXUI_PALETTE256_TURQUOISE4 = 30,
  FTXUI_PALETTE256_VIOLET = 177,
  FTXUI_PALETTE256_WHEAT1 = 229,
  FTXUI_PALETTE256_WHEAT4 = 101,
  FTXUI_PALETTE256_YELLOW1 = 226,
  FTXUI_PALETTE256_YELLOW2 = 190,
  FTXUI_PALETTE256_YELLOW3 = 148,
  FTXUI_PALETTE256_YELLOW3BIS = 184,
  FTXUI_PALETTE256_YELLOW4 = 100,
  FTXUI_PALETTE256_YELLOW4BIS = 106,
} ftxui_palette256_t;

/**
 * @brief Specifies whether to apply size constraints to width or height.
 */
typedef enum {
    FTXUI_WIDTH_OR_HEIGHT_WIDTH,  ///< Apply constraints to the width.
    FTXUI_WIDTH_OR_HEIGHT_HEIGHT, ///< Apply constraints to the height.
} ftxui_width_or_height_t;

/**
 * @brief Specifies the type of size constraint to apply.
 */
typedef enum {
    FTXUI_CONSTRAINT_LESS_THAN,      ///< The size must be less than the given value.
    FTXUI_CONSTRAINT_GREATER_THAN,   ///< The size must be greater than the given value.
    FTXUI_CONSTRAINT_EQUAL,          ///< The size must be equal to the given value.
} ftxui_constraint_t;

typedef enum {
    FTXUI_DIRECTION_UP,
    FTXUI_DIRECTION_DOWN,
    FTXUI_DIRECTION_LEFT,
    FTXUI_DIRECTION_RIGHT,
} ftxui_direction_t;

/**
 * @brief Creates a default (transparent) Color object.
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_default();

/**
 * @brief Creates a Color object from RGB values.
 * @param r Red component (0-255).
 * @param g Green component (0-255).
 * @param b Blue component (0-255).
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_rgb(uint8_t r, uint8_t g, uint8_t b);

/**
 * @brief Creates a Color object from RGBA values.
 * @param r Red component (0-255).
 * @param g Green component (0-255).
 * @param b Blue component (0-255).
 * @param a Alpha component (0-255).
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_rgba(uint8_t r, uint8_t g, uint8_t b, uint8_t a);

/**
 * @brief Creates a Color object from HSV values.
 * @param h Hue component (0-255).
 * @param s Saturation component (0-255).
 * @param v Value component (0-255).
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_hsv(uint8_t h, uint8_t s, uint8_t v);

/**
 * @brief Creates a Color object from HSVA values.
 * @param h Hue component (0-255).
 * @param s Saturation component (0-255).
 * @param v Value component (0-255).
 * @param a Alpha component (0-255).
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_hsva(uint8_t h, uint8_t s, uint8_t v, uint8_t a);

/**
 * @brief Creates a Color object from a 1-bit palette index.
 * @param index The 1-bit palette index.
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_palette1(ftxui_palette1_t index);

/**
 * @brief Creates a Color object from a 16-bit palette index.
 * @param index The 16-bit palette index.
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_palette16(ftxui_palette16_t index);

/**
 * @brief Creates a Color object from a 256-bit palette index.
 * @param index The 256-bit palette index.
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_palette256(ftxui_palette256_t index);

/**
 * @brief Creates a Color object from a raw 256-color palette index (0-255).
 * @param index Raw palette index (0-255).
 * @return A handle to the new Color object.
 */
ftxui_color_handle_t ftxui_color_palette256_raw(int index);

/**
 * @brief Interpolates between two colors.
 * @param t The interpolation factor (0.0 to 1.0).
 * @param a The first color.
 * @param b The second color.
 * @return A handle to the new interpolated Color object.
 */
ftxui_color_handle_t ftxui_color_interpolate(float t, ftxui_color_handle_t a, ftxui_color_handle_t b);

/**
 * @brief Blends two colors.
 * @param lhs The left-hand side color.
 * @param rhs The right-hand side color.
 * @return A handle to the new blended Color object.
 */
ftxui_color_handle_t ftxui_color_blend(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs);

/**
 * @brief Checks if a color is opaque.
 * @param color The color to check.
 * @return True if the color is opaque, false otherwise.
 */
bool ftxui_color_is_opaque(ftxui_color_handle_t color);

/**
 * @brief Compares two colors for equality.
 * @param lhs The left-hand side color.
 * @param rhs The right-hand side color.
 * @return True if the colors are equal, false otherwise.
 */
bool ftxui_color_equals(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs);

/**
 * @brief Compares two colors for inequality.
 * @param lhs The left-hand side color.
 * @param rhs The right-hand side color.
 * @return True if the colors are not equal, false otherwise.
 */
bool ftxui_color_not_equals(ftxui_color_handle_t lhs, ftxui_color_handle_t rhs);

/**
 * @brief Returns a string representation of the color.
 * The returned string must be freed using free().
 * @param color The color object.
 * @param is_background_color True if the color is used as a background color.
 * @return A dynamically allocated string representing the color.
 */
char* ftxui_color_print(ftxui_color_handle_t color, bool is_background_color);

/**
 * @brief Destroys a Color object and frees its memory.
 * @param color The handle to the Color object to destroy.
 */
void ftxui_color_destroy(ftxui_color_handle_t color);

/**
 * @brief Easing function for animations.
 * @param progress A value between 0.0 and 1.0 representing the animation progress.
 * @return The eased progress value.
 */
typedef float (*ftxui_easing_function_t)(float progress);

typedef enum {
    FTXUI_EASING_LINEAR,
    FTXUI_EASING_QUADRATIC_IN,
    FTXUI_EASING_QUADRATIC_OUT,
    FTXUI_EASING_QUADRATIC_IN_OUT,
    FTXUI_EASING_CUBIC_IN,
    FTXUI_EASING_CUBIC_OUT,
    FTXUI_EASING_CUBIC_IN_OUT,
    FTXUI_EASING_QUARTIC_IN,
    FTXUI_EASING_QUARTIC_OUT,
    FTXUI_EASING_QUARTIC_IN_OUT,
    FTXUI_EASING_QUINTIC_IN,
    FTXUI_EASING_QUINTIC_OUT,
    FTXUI_EASING_QUINTIC_IN_OUT,
    FTXUI_EASING_SINE_IN,
    FTXUI_EASING_SINE_OUT,
    FTXUI_EASING_SINE_IN_OUT,
    FTXUI_EASING_CIRCULAR_IN,
    FTXUI_EASING_CIRCULAR_OUT,
    FTXUI_EASING_CIRCULAR_IN_OUT,
    FTXUI_EASING_EXPONENTIAL_IN,
    FTXUI_EASING_EXPONENTIAL_OUT,
    FTXUI_EASING_EXPONENTIAL_IN_OUT,
    FTXUI_EASING_ELASTIC_IN,
    FTXUI_EASING_ELASTIC_OUT,
    FTXUI_EASING_ELASTIC_IN_OUT,
    FTXUI_EASING_BACK_IN,
    FTXUI_EASING_BACK_OUT,
    FTXUI_EASING_BACK_IN_OUT,
    FTXUI_EASING_BOUNCE_IN,
    FTXUI_EASING_BOUNCE_OUT,
    FTXUI_EASING_BOUNCE_IN_OUT,
} ftxui_easing_function_type_t;

typedef struct {
    bool enabled;
    ftxui_color_handle_t inactive;
    ftxui_color_handle_t active;
    int duration_ms;
    ftxui_easing_function_type_t easing_function_type;
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
 * @brief Initializes the FTXUI interactive application (ScreenInteractive) as terminal output.
 *
 * @return ftxui_app_handle_t A handle to the initialized app, or NULL on failure.
 */
ftxui_app_handle_t ftxui_app_create_terminal_output();

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
 * @brief Returns the current terminal width in columns.
 *
 * @return int The terminal width.
 */
int ftxui_terminal_width();

/**
 * @brief Returns the current terminal height in rows.
 *
 * @return int The terminal height.
 */
int ftxui_terminal_height();

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
ftxui_element_handle_t ftxui_element_separator_hselector(float left, float right, ftxui_color_handle_t unselected_color, ftxui_color_handle_t selected_color);

/**
 * @brief Creates a vertical selector separator.
 *
 * @param up The up position.
 * @param down The down position.
 * @param unselected_color The color when not selected.
 * @param selected_color The color when selected.
 * @return ftxui_element_handle_t The separator element handle.
 */
ftxui_element_handle_t ftxui_element_separator_vselector(float up, float down, ftxui_color_handle_t unselected_color, ftxui_color_handle_t selected_color);

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
ftxui_element_handle_t ftxui_element_color(ftxui_element_handle_t element, ftxui_color_handle_t color);

/**
 * @brief Sets the background color of an element.
 *
 * @param element The element to color.
 * @param color The color to apply.
 * @return ftxui_element_handle_t A new element with the specified color.
 */
ftxui_element_handle_t ftxui_element_bgcolor(ftxui_element_handle_t element, ftxui_color_handle_t color);

/**
 * @brief Adds a vertical scroll indicator to an element.
 *
 * @param element The element to add the scroll indicator to.
 * @return ftxui_element_handle_t A new element with a vertical scroll indicator.
 */
ftxui_element_handle_t ftxui_element_vscroll_indicator(ftxui_element_handle_t element);

/**
 * @brief Wraps an element in a frame, allowing it to be scrolled.
 *
 * @param element The element to wrap in a frame.
 * @return ftxui_element_handle_t A new element wrapped in a frame.
 */
ftxui_element_handle_t ftxui_element_frame(ftxui_element_handle_t element);

/**
 * @brief Applies size constraints to an element.
 *
 * @param element The element to apply constraints to.
 * @param width_or_height Specifies whether to constrain width or height.
 * @param constraint_type The type of constraint to apply.
 * @param value The value for the constraint.
 * @return ftxui_element_handle_t A new element with the specified size constraints.
 */
ftxui_element_handle_t ftxui_element_set_size(ftxui_element_handle_t element, ftxui_width_or_height_t width_or_height, ftxui_constraint_t constraint_type, int value);

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
 * @brief Makes an element dim.
 *
 * @param element The element to make dim.
 * @return ftxui_element_handle_t A new element with dim properties.
 */
ftxui_element_handle_t ftxui_element_dim(ftxui_element_handle_t element);

/**
 * @brief Makes an element blink.
 *
 * @param element The element to make blink.
 * @return ftxui_element_handle_t A new element with blink properties.
 */
ftxui_element_handle_t ftxui_element_blink(ftxui_element_handle_t element);

/**
 * @brief Makes an element strikethrough.
 *
 * @param element The element to make strikethrough.
 * @return ftxui_element_handle_t A new element with strikethrough properties.
 */
ftxui_element_handle_t ftxui_element_strikethrough(ftxui_element_handle_t element);

/**
 * @brief Creates an element that does nothing, effectively hiding the child.
 *
 * @param element The element to hide.
 * @return ftxui_element_handle_t An empty element.
 */
ftxui_element_handle_t ftxui_element_nothing(ftxui_element_handle_t element);

/**
 * @brief Returns the easing function for a given type.
 *
 * @param type The type of easing function.
 * @return ftxui_easing_function_t The easing function.
 */
ftxui_easing_function_t ftxui_easing_function_get(ftxui_easing_function_type_t type);

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
 * @brief Returns the animated button options with background and foreground colors.
 * 
 * @param background The background color.
 * @param foreground The foreground color.
 * @param background_active The background active color.
 * @param foreground_active The foreground active color.
 * @return ftxui_button_option_t The button options.
 */
ftxui_button_option_t ftxui_button_option_animated(ftxui_color_handle_t background, ftxui_color_handle_t foreground, ftxui_color_handle_t background_active, ftxui_color_handle_t foreground_active);

/**
 * @brief Creates a checkbox component.
 * 
 * @param label The label of the checkbox.
 * @param checked A pointer to a boolean that stores the checked state.
 * @return ftxui_component_handle_t The checkbox component handle.
 */
ftxui_component_handle_t ftxui_component_checkbox(const char* label, bool* checked);

// --- String handle (for Input component) ---
typedef void* ftxui_string_handle_t;
ftxui_string_handle_t ftxui_string_create(const char* initial);
const char* ftxui_string_get(ftxui_string_handle_t str);
void ftxui_string_set(ftxui_string_handle_t str, const char* value);
void ftxui_string_destroy(ftxui_string_handle_t str);

/**
 * @brief Creates an input component.
 *
 * @param content A string handle for the input content.
 * @param placeholder The placeholder text when the input is empty.
 * @return ftxui_component_handle_t The input component handle.
 */
ftxui_component_handle_t ftxui_component_input(ftxui_string_handle_t content, const char* placeholder);
ftxui_component_handle_t ftxui_component_input_password(ftxui_string_handle_t content, const char* placeholder);

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

// --- Component Decorators ---

/**
 * @brief Wraps a component with a border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a border.
 */
ftxui_component_handle_t ftxui_component_border(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with a light border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a light border.
 */
ftxui_component_handle_t ftxui_component_border_light(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with a dashed border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a dashed border.
 */
ftxui_component_handle_t ftxui_component_border_dashed(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with a heavy border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a heavy border.
 */
ftxui_component_handle_t ftxui_component_border_heavy(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with a double border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a double border.
 */
ftxui_component_handle_t ftxui_component_border_double(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with a rounded border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with a rounded border.
 */
ftxui_component_handle_t ftxui_component_border_rounded(ftxui_component_handle_t component);

/**
 * @brief Wraps a component with an empty border.
 *
 * @param component The component to wrap.
 * @return ftxui_component_handle_t A new component with an empty border.
 */
ftxui_component_handle_t ftxui_component_border_empty(ftxui_component_handle_t component);

/**
 * @brief Wraps a component in a frame, allowing it to be scrolled.
 *
 * @param component The component to wrap in a frame.
 * @return ftxui_component_handle_t A new component wrapped in a frame.
 */
ftxui_component_handle_t ftxui_component_frame(ftxui_component_handle_t component);

/**
 * @brief Makes a component flexible, allowing it to expand or shrink.
 *
 * @param component The component to make flexible.
 * @return ftxui_component_handle_t A new component with flex properties.
 */
ftxui_component_handle_t ftxui_component_flex(ftxui_component_handle_t component);

/**
 * @brief Makes a component bold.
 *
 * @param component The component to make bold.
 * @return ftxui_component_handle_t A new component with bold properties.
 */
ftxui_component_handle_t ftxui_component_bold(ftxui_component_handle_t component);

/**
 * @brief Makes a component inverted.
 *
 * @param component The component to make inverted.
 * @return ftxui_component_handle_t A new component with inverted properties.
 */
ftxui_component_handle_t ftxui_component_inverted(ftxui_component_handle_t component);

/**
 * @brief Makes a component underlined.
 *
 * @param component The component to make underlined.
 * @return ftxui_component_handle_t A new component with underlined properties.
 */
ftxui_component_handle_t ftxui_component_underlined(ftxui_component_handle_t component);

/**
 * @brief Sets the foreground color of a component.
 *
 * @param component The component to color.
 * @param color The color to apply.
 * @return ftxui_component_handle_t A new component with the specified color.
 */
ftxui_component_handle_t ftxui_component_color(ftxui_component_handle_t component, ftxui_color_handle_t color);

/**
 * @brief Sets the background color of a component.
 *
 * @param component The component to color.
 * @param color The color to apply.
 * @return ftxui_component_handle_t A new component with the specified color.
 */
ftxui_component_handle_t ftxui_component_bgcolor(ftxui_component_handle_t component, ftxui_color_handle_t color);

/**
 * @brief Adds a vertical scroll indicator to a component.
 *
 * @param component The component to add the scroll indicator to.
 * @return ftxui_component_handle_t A new component with a vertical scroll indicator.
 */
ftxui_component_handle_t ftxui_component_vscroll_indicator(ftxui_component_handle_t component);

/**
 * @brief Applies size constraints to a component.
 *
 * @param component The component to apply constraints to.
 * @param width_or_height Specifies whether to constrain width or height.
 * @param constraint_type The type of constraint to apply.
 * @param value The value for the constraint.
 * @return ftxui_component_handle_t A new component with the specified size constraints.
 */
ftxui_component_handle_t ftxui_component_set_size(ftxui_component_handle_t component, ftxui_width_or_height_t width_or_height, ftxui_constraint_t constraint_type, int value);

/**
 * @brief Centers a component horizontally.
 *
 * @param component The component to center.
 * @return ftxui_component_handle_t A new component centered horizontally.
 */
ftxui_component_handle_t ftxui_component_hcenter(ftxui_component_handle_t component);

/**
 * @brief Centers a component vertically.
 *
 * @param component The component to center.
 * @return ftxui_component_handle_t A new component centered vertically.
 */
ftxui_component_handle_t ftxui_component_vcenter(ftxui_component_handle_t component);

/**
 * @brief Centers a component both horizontally and vertically.
 *
 * @param component The component to center.
 * @return ftxui_component_handle_t A new component centered.
 */
ftxui_component_handle_t ftxui_component_center(ftxui_component_handle_t component);

/**
 * @brief Aligns a component to the right.
 *
 * @param component The component to align.
 * @return ftxui_component_handle_t A new component aligned to the right.
 */
ftxui_component_handle_t ftxui_component_align_right(ftxui_component_handle_t component);


/**
 * @brief Makes a component dim.
 *
 * @param component The component to make dim.
 * @return ftxui_component_handle_t A new component with dim properties.
 */
ftxui_component_handle_t ftxui_component_dim(ftxui_component_handle_t component);

/**
 * @brief Makes a component blink.
 *
 * @param component The component to make blink.
 * @return ftxui_component_handle_t A new component with blink properties.
 */
ftxui_component_handle_t ftxui_component_blink(ftxui_component_handle_t component);

/**
 * @brief Makes a component strikethrough.
 *
 * @param component The component to make strikethrough.
 * @return ftxui_component_handle_t A new component with strikethrough properties.
 */
ftxui_component_handle_t ftxui_component_strikethrough(ftxui_component_handle_t component);

/**
 * @brief Creates a component that does nothing, effectively hiding the child.
 *
 * @param component The component to hide.
 * @return ftxui_component_handle_t An empty component.
 */
ftxui_component_handle_t ftxui_component_nothing(ftxui_component_handle_t component);

/**
 * @brief Wraps a component to track hover state.
 *
 * @param component The component to wrap.
 * @param hover A pointer to a boolean that stores the hover state.
 * @return ftxui_component_handle_t A new component that tracks hover state.
 */
ftxui_component_handle_t ftxui_component_hoverable(ftxui_component_handle_t component, bool* hover);

// --- Additional Element Creation ---

ftxui_element_handle_t ftxui_element_vtext(const char* text);
ftxui_element_handle_t ftxui_element_spinner(int charset_index, int image_index);
ftxui_element_handle_t ftxui_element_paragraph(const char* text);
ftxui_element_handle_t ftxui_element_paragraph_align_left(const char* text);
ftxui_element_handle_t ftxui_element_paragraph_align_right(const char* text);
ftxui_element_handle_t ftxui_element_paragraph_align_center(const char* text);
ftxui_element_handle_t ftxui_element_paragraph_align_justify(const char* text);
ftxui_element_handle_t ftxui_element_empty();

ftxui_element_handle_t ftxui_element_gauge_left(double value);
ftxui_element_handle_t ftxui_element_gauge_right(double value);
ftxui_element_handle_t ftxui_element_gauge_up(double value);
ftxui_element_handle_t ftxui_element_gauge_down(double value);
ftxui_element_handle_t ftxui_element_gauge_direction(double value, ftxui_direction_t direction);

ftxui_element_handle_t ftxui_element_dbox(ftxui_element_handle_t* elements, int count);
ftxui_element_handle_t ftxui_element_hflow(ftxui_element_handle_t* elements, int count);
ftxui_element_handle_t ftxui_element_vflow(ftxui_element_handle_t* elements, int count);

ftxui_element_handle_t ftxui_element_flex_grow(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_flex_shrink(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_xflex(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_xflex_grow(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_xflex_shrink(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_yflex(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_yflex_grow(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_yflex_shrink(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_notflex(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_filler();

ftxui_element_handle_t ftxui_element_xframe(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_yframe(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_block(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_block_blinking(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_bar(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_bar_blinking(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_underline(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_focus_cursor_underline_blinking(ftxui_element_handle_t element);

ftxui_element_handle_t ftxui_element_italic(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_underlined_double(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_automerge(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_hyperlink(const char* link, ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_hscroll_indicator(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_clear_under(ftxui_element_handle_t element);

ftxui_element_handle_t ftxui_element_border_styled(ftxui_element_handle_t element, ftxui_border_style_t style);
ftxui_element_handle_t ftxui_element_border_styled_color(ftxui_element_handle_t element, ftxui_border_style_t style, ftxui_color_handle_t color);
ftxui_element_handle_t ftxui_element_border_colored(ftxui_element_handle_t element, ftxui_color_handle_t color);

ftxui_element_handle_t ftxui_element_selection_style_reset(ftxui_element_handle_t element);
ftxui_element_handle_t ftxui_element_selection_color(ftxui_element_handle_t element, ftxui_color_handle_t color);
ftxui_element_handle_t ftxui_element_selection_background_color(ftxui_element_handle_t element, ftxui_color_handle_t color);
ftxui_element_handle_t ftxui_element_selection_foreground_color(ftxui_element_handle_t element, ftxui_color_handle_t color);

ftxui_element_handle_t ftxui_element_focus_position(ftxui_element_handle_t element, int x, int y);
ftxui_element_handle_t ftxui_element_focus_position_relative(ftxui_element_handle_t element, float x, float y);

// --- CatchEvent ---
typedef void* ftxui_event_handle_t;
const char* ftxui_event_input(ftxui_event_handle_t event);
const char* ftxui_event_debug_string(ftxui_event_handle_t event);
bool ftxui_event_is_character(ftxui_event_handle_t event);
const char* ftxui_event_character(ftxui_event_handle_t event);
bool ftxui_event_is_mouse(ftxui_event_handle_t event);
int ftxui_event_mouse_x(ftxui_event_handle_t event);
int ftxui_event_mouse_y(ftxui_event_handle_t event);
typedef bool (*ftxui_catch_event_callback_t)(ftxui_event_handle_t event, void* userdata);
ftxui_component_handle_t ftxui_component_catch_event(ftxui_component_handle_t component, ftxui_catch_event_callback_t callback, void* userdata);

// --- Focusable renderer ---
typedef ftxui_element_handle_t (*ftxui_focused_render_callback_t)(bool focused, void* userdata);
ftxui_component_handle_t ftxui_component_renderer_focusable(ftxui_focused_render_callback_t callback, void* userdata);

// --- Component render decorator ---
typedef ftxui_element_handle_t (*ftxui_inner_render_callback_t)(ftxui_element_handle_t inner, void* userdata);
ftxui_component_handle_t ftxui_component_renderer_with_inner(ftxui_component_handle_t component, ftxui_inner_render_callback_t callback, void* userdata);

// --- Directional int slider (no label) ---
ftxui_component_handle_t ftxui_component_slider_int_direction(int* value, int min, int max, int increment, ftxui_direction_t direction);

// --- Float slider ---
ftxui_component_handle_t ftxui_component_slider_float(const char* label, float* value, float min, float max, float increment);
ftxui_component_handle_t ftxui_component_slider_float_direction(float* value, float min, float max, float increment, ftxui_direction_t direction, ftxui_color_handle_t color_active, ftxui_color_handle_t color_inactive);

// --- Horizontal menu ---
ftxui_component_handle_t ftxui_component_menu_horizontal(const char** entries, int count, int* selected);

// --- ResizableSplit with options ---
typedef ftxui_element_handle_t (*ftxui_separator_func_t)(void* userdata);

typedef struct {
    ftxui_component_handle_t main;
    ftxui_component_handle_t back;
    ftxui_direction_t direction;
    int* main_size;
    int* min_size;
    int* max_size;
    ftxui_separator_func_t separator_func;
    void* separator_userdata;
} ftxui_resizable_split_option_t;

ftxui_component_handle_t ftxui_component_resizable_split_opt(ftxui_resizable_split_option_t option);

// --- Gridbox element ---
// cells: flat row-major array; row_lengths[i] = number of cells in row i
ftxui_element_handle_t ftxui_element_gridbox(ftxui_element_handle_t* cells, int total_cells, int* row_lengths, int row_count);

// --- MenuEntry with animated colors ---
ftxui_component_handle_t ftxui_component_menu_entry_animated(const char* label, ftxui_animated_colors_option_t animated_colors);

// --- Animated horizontal menu ---
ftxui_component_handle_t ftxui_component_menu_horizontal_animated(const char** entries, int count, int* selected);
ftxui_component_handle_t ftxui_component_menu_toggle(const char** entries, int count, int* selected);

// --- RequestAnimationFrame ---
void ftxui_app_request_animation_frame(ftxui_app_handle_t app);

// --- Canvas ---
typedef void* ftxui_canvas_handle_t;
ftxui_canvas_handle_t ftxui_canvas_create(int width, int height);
void ftxui_canvas_destroy(ftxui_canvas_handle_t canvas);
void ftxui_canvas_draw_text(ftxui_canvas_handle_t canvas, int x, int y, const char* text);
void ftxui_canvas_draw_text_color(ftxui_canvas_handle_t canvas, int x, int y, const char* text, ftxui_color_handle_t color);
void ftxui_canvas_draw_point_line(ftxui_canvas_handle_t canvas, int x1, int y1, int x2, int y2, ftxui_color_handle_t color);
void ftxui_canvas_draw_block_line(ftxui_canvas_handle_t canvas, int x1, int y1, int x2, int y2, ftxui_color_handle_t color);
void ftxui_canvas_draw_point_circle(ftxui_canvas_handle_t canvas, int x, int y, int radius);
void ftxui_canvas_draw_point_circle_filled(ftxui_canvas_handle_t canvas, int x, int y, int radius);
void ftxui_canvas_draw_block_circle(ftxui_canvas_handle_t canvas, int x, int y, int radius);
void ftxui_canvas_draw_block_circle_filled(ftxui_canvas_handle_t canvas, int x, int y, int radius);
void ftxui_canvas_draw_point_ellipse(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry);
void ftxui_canvas_draw_point_ellipse_filled(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry);
void ftxui_canvas_draw_block_ellipse(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry);
void ftxui_canvas_draw_block_ellipse_filled(ftxui_canvas_handle_t canvas, int x, int y, int rx, int ry);
// Creates an element from the canvas. Caller keeps ownership of the canvas handle.
ftxui_element_handle_t ftxui_element_canvas_ref(ftxui_canvas_handle_t canvas);

// --- graph element ---
// The callback fills `output` (pre-allocated array of `width` ints) with graph values [0..height].
typedef void (*ftxui_graph_callback_t)(int width, int height, int* output, void* userdata);
ftxui_element_handle_t ftxui_element_graph(ftxui_graph_callback_t callback, void* userdata);

// --- LinearGradient ---
typedef void* ftxui_linear_gradient_handle_t;
ftxui_linear_gradient_handle_t ftxui_linear_gradient_create();
void ftxui_linear_gradient_destroy(ftxui_linear_gradient_handle_t gradient);
void ftxui_linear_gradient_angle(ftxui_linear_gradient_handle_t gradient, float angle);
void ftxui_linear_gradient_stop(ftxui_linear_gradient_handle_t gradient, ftxui_color_handle_t color);
void ftxui_linear_gradient_stop_at(ftxui_linear_gradient_handle_t gradient, ftxui_color_handle_t color, float position);
ftxui_element_handle_t ftxui_element_bgcolor_linear_gradient(ftxui_element_handle_t element, ftxui_linear_gradient_handle_t gradient);
ftxui_element_handle_t ftxui_element_color_linear_gradient(ftxui_element_handle_t element, ftxui_linear_gradient_handle_t gradient);

// --- flexbox element ---
typedef enum {
    FTXUI_FLEXBOX_DIRECTION_ROW,
    FTXUI_FLEXBOX_DIRECTION_ROW_INVERSED,
    FTXUI_FLEXBOX_DIRECTION_COLUMN,
    FTXUI_FLEXBOX_DIRECTION_COLUMN_INVERSED,
} ftxui_flexbox_direction_t;

typedef enum {
    FTXUI_FLEXBOX_WRAP_NO_WRAP,
    FTXUI_FLEXBOX_WRAP_WRAP,
    FTXUI_FLEXBOX_WRAP_WRAP_INVERSED,
} ftxui_flexbox_wrap_t;

typedef enum {
    FTXUI_FLEXBOX_JUSTIFY_FLEX_START,
    FTXUI_FLEXBOX_JUSTIFY_FLEX_END,
    FTXUI_FLEXBOX_JUSTIFY_CENTER,
    FTXUI_FLEXBOX_JUSTIFY_STRETCH,
    FTXUI_FLEXBOX_JUSTIFY_SPACE_BETWEEN,
    FTXUI_FLEXBOX_JUSTIFY_SPACE_AROUND,
    FTXUI_FLEXBOX_JUSTIFY_SPACE_EVENLY,
} ftxui_flexbox_justify_t;

typedef enum {
    FTXUI_FLEXBOX_ALIGN_ITEMS_FLEX_START,
    FTXUI_FLEXBOX_ALIGN_ITEMS_FLEX_END,
    FTXUI_FLEXBOX_ALIGN_ITEMS_CENTER,
    FTXUI_FLEXBOX_ALIGN_ITEMS_STRETCH,
} ftxui_flexbox_align_items_t;

typedef enum {
    FTXUI_FLEXBOX_ALIGN_CONTENT_FLEX_START,
    FTXUI_FLEXBOX_ALIGN_CONTENT_FLEX_END,
    FTXUI_FLEXBOX_ALIGN_CONTENT_CENTER,
    FTXUI_FLEXBOX_ALIGN_CONTENT_STRETCH,
    FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_BETWEEN,
    FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_AROUND,
    FTXUI_FLEXBOX_ALIGN_CONTENT_SPACE_EVENLY,
} ftxui_flexbox_align_content_t;

typedef struct {
    ftxui_flexbox_direction_t direction;
    ftxui_flexbox_wrap_t wrap;
    ftxui_flexbox_justify_t justify_content;
    ftxui_flexbox_align_items_t align_items;
    ftxui_flexbox_align_content_t align_content;
    int gap_x;
    int gap_y;
} ftxui_flexbox_config_t;

ftxui_element_handle_t ftxui_element_flexbox(ftxui_element_handle_t* elements, int count, ftxui_flexbox_config_t config);

// --- Table ---
typedef void* ftxui_table_handle_t;
typedef void* ftxui_table_selection_handle_t;

// cells is a flat row-major array of (rows * cols) strings
ftxui_table_handle_t ftxui_table_create(const char** cells, int rows, int cols);
void ftxui_table_destroy(ftxui_table_handle_t table);
ftxui_element_handle_t ftxui_table_render(ftxui_table_handle_t table);

ftxui_table_selection_handle_t ftxui_table_select_all(ftxui_table_handle_t table);
ftxui_table_selection_handle_t ftxui_table_select_row(ftxui_table_handle_t table, int row);
ftxui_table_selection_handle_t ftxui_table_select_rows(ftxui_table_handle_t table, int from, int to);
ftxui_table_selection_handle_t ftxui_table_select_column(ftxui_table_handle_t table, int col);
ftxui_table_selection_handle_t ftxui_table_select_cell(ftxui_table_handle_t table, int col, int row);
void ftxui_table_selection_destroy(ftxui_table_selection_handle_t sel);

void ftxui_table_selection_border(ftxui_table_selection_handle_t sel, ftxui_border_style_t style);
void ftxui_table_selection_border_color(ftxui_table_selection_handle_t sel, ftxui_border_style_t style, ftxui_color_handle_t color);
void ftxui_table_selection_separator_vertical(ftxui_table_selection_handle_t sel, ftxui_border_style_t style);
void ftxui_table_selection_decorate_bold(ftxui_table_selection_handle_t sel);
void ftxui_table_selection_decorate_cells_align_right(ftxui_table_selection_handle_t sel);
void ftxui_table_selection_decorate_cells_color(ftxui_table_selection_handle_t sel, ftxui_color_handle_t color);
void ftxui_table_selection_decorate_cells_color_alternate_row(ftxui_table_selection_handle_t sel, ftxui_color_handle_t color, int modulo, int offset);

// --- Window component ---
typedef struct {
    ftxui_component_handle_t inner;  // nullable
    const char* title;               // nullable
    int* left;    // nullable (use left_default if null)
    int* top;     // nullable
    int* width;   // nullable
    int* height;  // nullable
    int left_default;
    int top_default;
    int width_default;
    int height_default;
} ftxui_window_options_t;

ftxui_component_handle_t ftxui_component_window(ftxui_window_options_t options);

// --- Loop ---
typedef void* ftxui_loop_handle_t;
ftxui_loop_handle_t ftxui_loop_create(ftxui_app_handle_t app, ftxui_component_handle_t component);
bool ftxui_loop_has_quitted(ftxui_loop_handle_t loop);
void ftxui_loop_run_once(ftxui_loop_handle_t loop);
void ftxui_loop_destroy(ftxui_loop_handle_t loop);

// --- ColorInfo ---
typedef struct {
    int index_256;
    const char* name;
} ftxui_color_info_t;

// Returns a flat row-major array of (num_rows * max_cols) entries.
// Entries with index_256 == -1 are padding.
// Caller must call ftxui_color_info_free() on the returned pointer.
ftxui_color_info_t* ftxui_color_info_sorted_2d(int* num_rows, int* max_cols);
void ftxui_color_info_free(ftxui_color_info_t* data);

// --- Dropdown with custom transform ---
typedef ftxui_element_handle_t (*ftxui_dropdown_transform_callback_t)(
    bool open,
    ftxui_element_handle_t checkbox,
    ftxui_element_handle_t radiobox,
    void* userdata
);

// entry_transform may be null (uses default radiobox entry rendering).
ftxui_component_handle_t ftxui_component_dropdown_custom(
    const char** entries, int count, int* selected,
    ftxui_dropdown_transform_callback_t transform, void* transform_userdata,
    ftxui_button_transform_t entry_transform, void* entry_transform_userdata
);

// --- Cell style for selectionStyle ---
typedef struct {
    bool blink;
    bool bold;
    bool dim;
    bool italic;
    bool inverted;
    bool underlined;
    bool underlined_double;
    bool strikethrough;
    bool automerge;
} ftxui_cell_t;

typedef void (*ftxui_cell_style_callback_t)(ftxui_cell_t* cell, void* userdata);

ftxui_element_handle_t ftxui_element_selection_style(
    ftxui_element_handle_t element,
    ftxui_cell_style_callback_t callback,
    void* userdata
);

// Registers a callback invoked whenever the terminal text selection changes.
void ftxui_app_selection_change(ftxui_app_handle_t app, void (*callback)(void*), void* userdata);

// Returns the currently selected text. Caller must free() the returned string.
char* ftxui_app_get_selection(ftxui_app_handle_t app);

#ifdef __cplusplus
}
#endif

#endif // FTXUI_C_API_H