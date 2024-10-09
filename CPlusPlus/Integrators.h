#ifndef INTERGRATORS_H
#define INTERGRATORS_H

#include <vector>
#include "Vector.h"
#include "Body.h"

class Integrators {
    public:
    std::vector<Body> symplecticEuler(std::vector<Body> bodies, double dt);

};

#endif