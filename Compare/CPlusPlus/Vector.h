#ifndef VECTOR_H
#define VECTOR_H

#include <array>
#include <cmath>
#include <iostream>
#include <immintrin.h> // AVX intrinsics

class Vector {
private:
    std::array<double, 3> elements;

public:
    // Vector(double x, double y, double z);
    Vector(double x, double y, double z) : elements{x, y, z} {}

    // double& operator[](int index);
    // const double& operator[](int index) const;
    inline double& operator[](int index) {
        return elements[index];
    }

    inline const double& operator[](int index) const {
        return elements[index];
    }

    // Vector operator+(const Vector& other) const;
    // Vector operator-(const Vector& other) const;

    // inline Vector operator+(const Vector& other) const {
    //     return Vector(elements[0] + other[0], elements[1] + other[1], elements[2] + other[2]);
    // }

    // inline Vector operator-(const Vector& other) const {
    //     return Vector(elements[0] - other[0], elements[1] - other[1], elements[2] - other[2]);
    // }

    // What follows is a SIMD implementation of the operator+ and operator- functions as described above
    inline Vector operator+(const Vector& other) const {
        __m256d vec = _mm256_set_pd(0.0, elements[2], elements[1], elements[0]);
        __m256d other_vec = _mm256_set_pd(0.0, other[2], other[1], other[0]);
        __m256d result = _mm256_add_pd(vec, other_vec);
        double res[4];
        _mm256_storeu_pd(res, result);
        return Vector(res[0], res[1], res[2]);
    }

    inline Vector operator-(const Vector& other) const {
        __m128 a = _mm_set_ps(0.0f, elements[2], elements[1], elements[0]);
        __m128 b = _mm_set_ps(0.0f, other[2], other[1], other[0]);
        __m128 result = _mm_sub_ps(a, b);
        float res[4];
        _mm_store_ps(res, result);
        return Vector(res[0], res[1], res[2]);
    }

    // double norm() const;
    inline double norm() const {
        __m256d vec = _mm256_set_pd(0.0, elements[2], elements[1], elements[0]);
        __m256d mul = _mm256_mul_pd(vec, vec);
        __m256d sum = _mm256_hadd_pd(mul, mul);
        double res[4];
        _mm256_storeu_pd(res, sum);
        return std::sqrt(res[0] + res[2]);
    }

    inline double normCubed() const {
        __m256d vec = _mm256_set_pd(0.0, elements[2], elements[1], elements[0]);
        __m256d mul = _mm256_mul_pd(vec, vec);
        __m256d sum = _mm256_hadd_pd(mul, mul);
        double res[4];
        _mm256_storeu_pd(res, sum);
        double normSquared = res[0] + res[2];
        return normSquared * std::sqrt(normSquared);
    }

    // Vector scalarMultiply(double scalar) const;

    // Vector power(double p) const;

    // Vector dividingScalar(double scalar) const;
    

    // inline Vector scalarMultiply(double scalar) const {
    //     return Vector(elements[0] * scalar, elements[1] * scalar, elements[2] * scalar);
    // }
    // What follows is a SIMD implementation of the scalarMultiply function as described above

    inline Vector scalarMultiply(double scalar) const {
        __m256d vec = _mm256_set_pd(0.0, elements[2], elements[1], elements[0]);
        __m256d scalar_vec = _mm256_set1_pd(scalar);
        __m256d result = _mm256_mul_pd(vec, scalar_vec);
        double res[4];
        _mm256_storeu_pd(res, result);
        return Vector(res[0], res[1], res[2]);
    }

    // inline Vector power(double p) const {
    //     return Vector(std::pow(elements[0], p), std::pow(elements[1], p), std::pow(elements[2], p));
    // }

    // inline Vector dividingScalar(double scalar) const {
    //     return Vector(scalar / elements[0], scalar / elements[1], scalar / elements[2]);
    // }

    // void print() const;
    inline void print() const {
        for (const auto& elem : elements) {
            std::cout << elem << " ";
        }
        std::cout << std::endl;
    }

};

#endif