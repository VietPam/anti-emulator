cmake_minimum_required(VERSION 3.4.1)

add_library(
        anti
        SHARED
        jni/anti.c
)

find_library(
        log-lib
        log
)

target_link_libraries(
        anti
        ${log-lib}
)