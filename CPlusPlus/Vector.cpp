#include <iostream>
#include <array>
#include <cmath>
#include "Vector.h"

Vector::Vector(double x, double y, double z) : elements{x, y, z} {}

//Override the [] subscript operator for the Vector class
double& Vector::operator[](int index) {
    return elements[index];
}


const double& Vector::operator[](int index) const {
    return elements[index];
}

//Override the + operator for the Vector class
Vector Vector::operator+(const Vector& other) const {
    return Vector(elements[0] + other[0], elements[1] + other[1], elements[2] + other[2]);
}

//Override the - operator for the Vector class
Vector Vector::operator-(const Vector& other) const {
    return Vector(elements[0] - other[0], elements[1] - other[1], elements[2] - other[2]);
}

double Vector::norm() const {
    double norm = sqrt(elements[0] * elements[0] + elements[1] * elements[1] + elements[2] * elements[2]);
    return sqrt(elements[0] * elements[0] + elements[1] * elements[1] + elements[2] * elements[2]);
}

Vector Vector::scalarMultiply(double scalar) const {
    return Vector(elements[0] * scalar, elements[1] * scalar, elements[2] * scalar);
}

Vector Vector::power(double p) const {
    return Vector(pow(elements[0], p), pow(elements[1], p), pow(elements[2], p));
}

Vector Vector::dividingScalar(double scalar) const {
    return Vector(scalar / elements[0], scalar / elements[1], scalar / elements[2]);
}

void Vector::print() const {
    for (const auto& elem : elements) {
        std::cout << elem << " ";
    }
    std::cout << std::endl;
}


