cmake_minimum_required(VERSION 3.12)
project(ftxui_c_binding)

set(CMAKE_CXX_STANDARD 17)

# Add FTXUI as a subdirectory
add_subdirectory(../../vendor/ftxui ftxui_build)

# Create the shared library for the C binding
add_library(ftxui_c_binding SHARED ftxui_c_wrapper.cpp)

# Link with FTXUI
target_link_libraries(ftxui_c_binding PRIVATE ftxui::ftxui)

# Include directories
target_include_directories(ftxui_c_binding PUBLIC .)

# Set output name
set_target_properties(ftxui_c_binding PROPERTIES OUTPUT_NAME "ftxui_c_binding")

# Add a test executable
add_executable(test_c build/binding/ftxui_c/test_c.c)
target_link_libraries(test_c PRIVATE ftxui_c_binding)
target_include_directories(test_c PRIVATE .)
