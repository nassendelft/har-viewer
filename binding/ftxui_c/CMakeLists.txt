cmake_minimum_required(VERSION 3.12)
project(ftxui_c_binding)

set(CMAKE_CXX_STANDARD 17)

# Add FTXUI as a subdirectory
add_subdirectory(../../vendor/ftxui ftxui_build)

# Create the shared library for the C binding
add_library(ftxui_c_binding_shared SHARED ftxui_c_wrapper.cpp)
target_link_libraries(ftxui_c_binding_shared PRIVATE ftxui::ftxui)
target_include_directories(ftxui_c_binding_shared PUBLIC .)
set_target_properties(ftxui_c_binding_shared PROPERTIES OUTPUT_NAME "ftxui_c_binding")

# Create the static library for the C binding
add_library(ftxui_c_binding_static STATIC ftxui_c_wrapper.cpp)
target_link_libraries(ftxui_c_binding_static PRIVATE ftxui::ftxui)
target_include_directories(ftxui_c_binding_static PUBLIC .)
set_target_properties(ftxui_c_binding_static PROPERTIES OUTPUT_NAME "ftxui_c_binding")

# Add a test executable
add_executable(test_c build/binding/ftxui_c/test_c.c)
target_link_libraries(test_c PRIVATE ftxui_c_binding_shared)
target_include_directories(test_c PRIVATE .)