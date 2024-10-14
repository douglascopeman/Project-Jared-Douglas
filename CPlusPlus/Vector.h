#ifndef VECTOR_H
#define VECTOR_H

#include <array>

class Vector {
private:
    std::array<double, 3> elements;

public:
    Vector(double x, double y, double z);

    double& operator[](int index);
    const double& operator[](int index) const;

    Vector operator+(const Vector& other) const;
    Vector operator-(const Vector& other) const;

    double norm() const;

    Vector scalarMultiply(double scalar) const;

    Vector power(double p) const;

    Vector dividingScalar(double scalar) const;

    void print() const;
};

#endif