#ifndef VECTOR_H
#define VECTOR_H

#include <array>
#include <cmath>
#include <iostream>

class Vector {
private:
    std::array<float, 3> elements;

public:
    // Vector(float x, float y, float z);
    Vector(float x, float y, float z) : elements{x, y, z} {}

    // float& operator[](int index);
    // const float& operator[](int index) const;
    inline float& operator[](int index) {
        return elements[index];
    }

    inline const float& operator[](int index) const {
        return elements[index];
    }

    // Vector operator+(const Vector& other) const;
    // Vector operator-(const Vector& other) const;
    inline Vector operator+(const Vector& other) const {
        return Vector(elements[0] + other[0], elements[1] + other[1], elements[2] + other[2]);
    }

    inline Vector operator-(const Vector& other) const {
        return Vector(elements[0] - other[0], elements[1] - other[1], elements[2] - other[2]);
    }

    // float norm() const;
    inline float norm() const {
        return sqrt(elements[0] * elements[0] + elements[1] * elements[1] + elements[2] * elements[2]);
    }

    // Vector scalarMultiply(float scalar) const;

    // Vector power(float p) const;

    // Vector dividingScalar(float scalar) const;
    
    inline Vector scalarMultiply(float scalar) const {
        return Vector(elements[0] * scalar, elements[1] * scalar, elements[2] * scalar);
    }

    inline Vector power(float p) const {
        return Vector(pow(elements[0], p), pow(elements[1], p), pow(elements[2], p));
    }

    inline Vector dividingScalar(float scalar) const {
        return Vector(scalar / elements[0], scalar / elements[1], scalar / elements[2]);
    }

    // void print() const;
    inline void print() const {
        for (const auto& elem : elements) {
            std::cout << elem << " ";
        }
        std::cout << std::endl;
    }

};

#endif