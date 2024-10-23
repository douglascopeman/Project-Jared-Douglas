# Project-Jared-Douglas
N-Body Problem Project

**Jobs To Do**
- Run various benchmarking tests on the functions, try and find areas to optimise
- Play around with 3+ bodies in the system, try and get a figure 8 orbit
- 

**Code Refactoring to be done**
- Change simulation from a T by 6 by n Matrix to a T by n matrix of orbit objects containing time evolution of position and velocity
- change run_fast to be a flag instead of a new function


**Code changes to make for week 5**
- ~~make body focus automatically chose the largest initial value (i.e. one that is not zero)~~
- ~~implement forrest ruth~~
- ~~change variable_timestep_constant to use the dt variable~~
- ~~fix all plots to actually show time instead of timesteps~~
- ~~check to see if language speed is linear in timesteps (graph speeds over num timesteps)~~
- ~~optimise c++ code?~~ (FAILED, miserably)
- compare cpu times for different integration algorithms
- ~~make close plot close all plots~~
- start moving onto 3 body cases
- include stopping conditions for orbits
    - ~~if variable timestep becomes too small (eg 10e-10 or something similar)~~
    - ~~if particles move too far away from each other (eg max distance > some number)~~
    - ~~if energy error is too large (output unbelievable anyway)~~
    - ~~if stopping condition is met, output which broke and how long it took to get there~~
    - number of timesteps to be as large as we can get away with
- ~~Housekeeping: refactor code for readability~~


**TODO: Week 6**
- add means of perturbing initial conditions (maintiaining total energy and angular momentum as constants - also CoM's pos & vel)
    - keep in plain. vary 2 pos * 2 vel * 3 bodies variables
    - use symmetries to keep constants constant
    - 6 constants (1 for ang momentum, 1 for energy, 2 for CoM pos, 2 for CoM vel)
    - solving this would be purely analytical, no numerics needed
- continue optimization of c++, use debugger to see which function is taking most compute time
