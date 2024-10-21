#ifndef INTERGRATORS_H
#define INTERGRATORS_H

#include <vector>
#include "Body.h"

class Integrators {
    public:
    static std::vector<Body*> symplecticEuler(std::vector<Body*> bodies, float dt);

};

#endif