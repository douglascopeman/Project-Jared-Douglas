#include <iostream>

class Body{
public:

    //Default Constructor
    Body() {}

    double calculateAcceleration() {
        return 0;
    }

private:
    float mass = 1;
    double position[3];
    double velocity[3];
    double acceleration[3];
};