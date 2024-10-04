#include <iostream>
#include <cmath>
using namespace std;


double newFunction(double x){
    for(int i = 1; i <= x; i++){
        cout << "\nThe number is " << i;
    }
    return 0;
}

int main() {
    double a = newFunction(5);
    return 0;
}
