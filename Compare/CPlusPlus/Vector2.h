#ifndef VECTOR_H
#define VECTOR_H

#include <array>
#include <cmath>
#include <iostream>
#include <immintrin.h>

class Vector {
private:
    alignas(32) std::array<double, 4> elements;

public:
    // Use constexpr for compile-time evaluation
    constexpr Vector(double x, double y, double z) noexcept : elements{x, y, z, 0.0} {}
    constexpr Vector() noexcept : elements{0.0, 0.0, 0.0, 0.0} {}

    // Make small functions constexpr and force inline
    [[nodiscard]] constexpr double& operator[](int index) noexcept {
        return elements[index];
    }

    [[nodiscard]] constexpr const double& operator[](int index) const noexcept {
        return elements[index];
    }

    // Optimize vector operations with improved AVX usage
    [[nodiscard]] Vector operator+(const Vector& other) const noexcept {
        Vector result;
        #ifdef __AVX__
            _mm256_store_pd(result.elements.data(),
                           _mm256_add_pd(_mm256_load_pd(elements.data()),
                                       _mm256_load_pd(other.elements.data())));
        #else
            std::cout << "Falling back to non-AVX addition" << std::endl;
            // Fallback for non-AVX systems
            for(int i = 0; i < 4; ++i) {
                result.elements[i] = elements[i] + other.elements[i];
            }
        #endif
        return result;
    }

    [[nodiscard]] Vector operator-(const Vector& other) const noexcept {
        Vector result;
        #ifdef __AVX__
            _mm256_store_pd(result.elements.data(),
                           _mm256_sub_pd(_mm256_load_pd(elements.data()),
                                       _mm256_load_pd(other.elements.data())));
        #else
            std::cout << "Falling back to non-AVX addition" << std::endl;
            for(int i = 0; i < 4; ++i) {
                result.elements[i] = elements[i] - other.elements[i];
            }
        #endif
        return result;
    }

    [[nodiscard]] Vector operator*(double scalar) const noexcept {
        Vector result;
        #ifdef __AVX__
            _mm256_store_pd(result.elements.data(),
                           _mm256_mul_pd(_mm256_load_pd(elements.data()),
                                       _mm256_set1_pd(scalar)));
        #else
            std::cout << "Falling back to non-AVX addition" << std::endl;
            for(int i = 0; i < 4; ++i) {
                result.elements[i] = elements[i] * scalar;
            }
        #endif
        return result;
    }

    [[nodiscard]] friend Vector operator*(double scalar, const Vector& vec) noexcept {
        return vec * scalar;
    }

    // Optimized norm calculation using FMA when available
    [[nodiscard]] double norm() const noexcept {
        #ifdef __AVX__
            #ifdef __FMA__
                __m256d vec = _mm256_load_pd(elements.data());
                __m256d result = _mm256_fmadd_pd(vec, vec, _mm256_setzero_pd());
                __m256d sum = _mm256_hadd_pd(result, result);
                double temp[4];
                _mm256_store_pd(temp, sum);
                return std::sqrt(temp[0] + temp[2]);
            #else
                std::cout << "Falling back to non-FMA addition" << std::endl;
                __m256d vec = _mm256_load_pd(elements.data());
                __m256d squares = _mm256_mul_pd(vec, vec);
                __m256d sum = _mm256_hadd_pd(squares, squares);
                double temp[4];
                _mm256_store_pd(temp, sum);
                return std::sqrt(temp[0] + temp[2]);
            #endif
        #else
            std::cout << "Falling back to non-AVX addition" << std::endl;
            return std::sqrt(elements[0] * elements[0] + 
                           elements[1] * elements[1] + 
                           elements[2] * elements[2]);
        #endif
    }

    [[nodiscard]] double normCubed() const noexcept {
        const double n = norm();
        return n * n * n;
    }

    void print() const {
        std::cout << "( ";
        for (const auto& elem : elements) {
            std::cout << elem << " ";
        }
        std::cout << ")" << std::endl;
    }
};

#endif



// #ifndef VECTOR_H
// #define VECTOR_H

// #include <array>
// #include <cmath>
// #include <iostream>
// #include <immintrin.h> // AVX intrinsics

// class Vector {
// private:
//     alignas(32) std::array<double, 4> elements;

// public:
//     Vector(double x, double y, double z) : elements{x, y, z, 0.0} {}
//     Vector() : elements{0.0, 0.0, 0.0, 0.0} {}

//     inline double& operator[](int index) {
//         return elements[index];
//     }

//     inline const double& operator[](int index) const {
//         return elements[index];
//     }

//     //We impliment vector addition, subtraction, and multiplication using AVX

//     inline Vector operator+(const Vector& other) const {
//         __m256d vec = _mm256_load_pd(elements.data());
//         __m256d other_vec = _mm256_load_pd(other.elements.data());
//         __m256d result = _mm256_add_pd(vec, other_vec);
//         Vector res;
//         _mm256_store_pd(res.elements.data(), result);
//         return res;
//     }

//     inline Vector operator-(const Vector& other) const{
//         __m256d vec = _mm256_load_pd(elements.data());
//         __m256d other_vec = _mm256_load_pd(other.elements.data());
//         __m256d result = _mm256_sub_pd(vec, other_vec);
//         Vector res;
//         _mm256_store_pd(res.elements.data(), result);
//         return res;
//     }

//     inline Vector operator*(const double& scalar) const {
//         __m256d vec = _mm256_load_pd(elements.data());
//         __m256d scalar_vec = _mm256_set1_pd(scalar);
//         __m256d result = _mm256_mul_pd(vec, scalar_vec);
//         Vector res;
//         _mm256_storeu_pd(res.elements.data(), result);
//         return res;
//     }

//     friend inline Vector operator*(const double& scalar, const Vector& vec) {
//         return vec * scalar;
//     }

//     inline double norm() const {
//         __m256d vec = _mm256_set_pd(0.0, elements[2], elements[1], elements[0]);
//         __m256d mul = _mm256_mul_pd(vec, vec);
//         __m256d hadd = _mm256_hadd_pd(mul, mul);
//         double res[4];
//         _mm256_storeu_pd(res, hadd);
//         return std::sqrt(res[0] + res[2]);
//     }

//     inline double normCubed() const {
//         __m256d vec = _mm256_load_pd(elements.data());
//         __m256d squares = _mm256_mul_pd(vec, vec);

//         __m256d hadd = _mm256_hadd_pd(squares, squares);
//         double res[4];
//         _mm256_store_pd(res, hadd);
//         const double result = res[0] + res[2];
//         return result * std::sqrt(result);
//     }

//     inline void print() const {
//         std::cout << "( ";
//         for (const auto& elem : elements) {
//             std::cout << elem << " ";
//         }
//         std::cout << ")" << std::endl;
//     }
// };

// #endif
