cmake_minimum_required(VERSION 3.10.2)

project("wdl")
include_directories(WDL/WDL)
add_definitions(-DWDL_ALLOW_UNSIGNED_DEFAULT_CHAR -DWDL_SIMPLEPITCHSHIFT_IMPLEMENT)
add_definitions(-DWDL_RESAMPLE_TYPE=float)
add_library(
        wdl SHARED
        WDL/WDL/fft.c
        WDL/WDL/resample.cpp
        jni/fft.cpp
        jni/resample.cpp
        jni/simple_pitchshift.cpp
        jni/sinewavegen.cpp
)

target_link_libraries(wdl log)