#include <iostream>
#include <vector>
using namespace std;

class Body{
public:

    //Default Constructor
    Body() {}

    double calculateAcceleration(vector<Body> bodies) {
        for(Body otherBody : bodies){
            if(otherBody != *this){
                vector<double> direction = *this->position - otherBody.position
            }
        }
    }

private:
    float mass = 1;
    double position[3];
    double velocity[3];
    double acceleration[3];
    float G = 1
};