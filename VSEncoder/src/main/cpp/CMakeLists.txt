cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.
project("x264")
message("-----------------------------")
message(${CMAKE_BUILD_TYPE})
message(${ANDROID_PLATFORM})
message(${CMAKE_SOURCE_DIR})
message(${ANDROID_ABI})
message("-----------------------------")

file(GLOB cpp_source *.cpp core/*.cpp)
#引入头文件
include_directories(${CMAKE_SOURCE_DIR}/x264/include)
#引入x264 静态库
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/x264/${ANDROID_ABI}")

add_library(
             x264-lib
             SHARED
             ${cpp_source} )

find_library(log-lib log)

target_link_libraries(
                        x264-lib
                       ${log-lib}
                        x264)