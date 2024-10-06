#include <iostream>
#include <array>

class Vector {
private:
    std::array<double, 3> elements;

public:
    Vector() : elements{0.0, 0.0, 0.0} {}

    Vector(double x, double y, double z) : elements{x, y, z} {}

    //Override the [] subscript operator for the Vector class
    double& operator[](int index) {
        return elements[index];
    }

    const double& operator[](int index) const {
        return elements[index];
    }

    //Override the + operator for the Vector class
    Vector operator+(const Vector& other) const {
        return Vector(elements[0] + other[0], elements[1] + other[1], elements[2] + other[2]);
    }

    //Override the - operator for the Vector class
    Vector operator-(const Vector& other) const {
        return Vector(elements[0] - other[0], elements[1] - other[1], elements[2] - other[2]);
    }

    double norm() {
        return sqrt(elements[0] * elements[0] + elements[1] * elements[1] + elements[2] * elements[2]);
    }

    Vector scalarMultiply(double scalar) const {
        return Vector(elements[0] * scalar, elements[1] * scalar, elements[2] * scalar);
    }

    Vector vectorMultiply(const Vector& other) const {
        return Vector(elements[0] * other[0], elements[1] * other[1], elements[2] * other[2]);
    }

    Vector power(double p) const {
        return Vector(pow(elements[0], p), pow(elements[1], p), pow(elements[2], p));
    }

    Vector divideByScalar(double scalar) const {
        return Vector(elements[0] / scalar, elements[1] / scalar, elements[2] / scalar);
    }

    Vector dividingScalar(double scalar) const {
        return Vector(scalar / elements[0], scalar / elements[1], scalar / elements[2]);
    }

    void print() const {
        for (const auto& elem : elements) {
            std::cout << elem << " ";
        }
        std::cout << std::endl;
    }
};

