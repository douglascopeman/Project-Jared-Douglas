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
- check to see if language speed is linear in timesteps (graph speeds over num timesteps)
- compare cpu times for different integration algorithms
- start moving onto 3 body cases
- add means of perturbing initial conditions (maintiaining total energy and angular momentum as constants - also CoM's pos & vel)
- keep in plain. vary 2 pos * 2 vel * 3 bodies variables
- use symmetries to keep constants constant
- 6 constants (1 for ang momentum, 1 for energy, 2 for CoM pos, 2 for CoM vel)
- solving this would be purely analytical, no numerics needed

