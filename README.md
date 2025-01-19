# Senior Honours Project --- Stability of Figure-8 Orbit, Unstable Closed N-Body Orbits, and Choreographies

## Overview
This project focuses on solving the N-Body Problem, and then determining the stability of orbits by considering various perturbations to the system's initial conditions.

The N-body problem involves predicting the individual motions of a group of celestial objects interacting with each other only via gravitational forces. To this end, we compare and contrast multiple numerical methods (of varying accuracy and order) for integration of the equations of motion, as well as implementing various optimizations and stopping conditions that terminate a simulation early if a given parameter strays too far from what would be possible in reality. 

Further to the actual N-body simulations themselves, we also perturb the simulations in various ways (by changing initial: position, velocity, energy, angular momentum) to determine the stability of the orbit in question.

The main focus of the project is considering the stability of the figure-8 orbit of 3 equal mass bodies, though we have have left all calculations general in aid of extending this goal if time permits. 

A side goal of the project was to speed up the simulation as compared to previous projects by considering languages other than Python (which was used for all prior projects under this topic). As such, we compared Python, Julia and Java (with an attempt made in C++ that suffered from implementor error and ended up being exceedingly slow -- were it not for lack of time, this likely could have been amended). Ultimately, while much of the initial exploratory code was completed in Python for speed and ease of understanding sake, we decided on Java for the significant increase in speed which allowed us to compute the highest resolution perturbations plots we could find.

The (still being completed) writeup can be found at: #TODO: link here

## Features
- Simulation of N-Body systems using different numerical integration methods.
- Benchmarking of different programming languages (Python, Julia, Java) for performance comparison.
- Visualization of orbits and trajectories of celestial bodies.
- Simulation of different perturbations of N-Body systems to deduce stability
- Visualization of stability of an orbit
- Visualization of the perturbations to orbits

## Installation
To run the simulations and perturbations, you need to have the following software installed:
- Python 3.x
- Java 11 or higher

## Usage

The JavaCompileAndRun.ps1 script takes arguments:
- Orbit: String representation of the orbit (declared in `OrbitExamples.java`)
- Time: Integer value of the total time the simulation should reach
- Timestep: Double value for the relative jump in time for each integration iteration (broadly: accuracy of the simulation)

Additionally, the following flags can be included: 
- `--integrator` followed by the name of the integrator to be used in camelCase (e.g. `--integrator "symplecticEuler"`)
- `--useVariableTimestep`: treat the passed Timestep value as a variable timestep scaling constant.
- `--checkStopConditions`: stop simulation early if certain physical conditions are not met in the simulation (drift too far from reality)
- `--calculateCentreOfMass`: calculate and log the centre of mass of all of the bodies
- `--calculateEnergies`: calculate and log the potential and kinetic energies of all bodies
- `--calculateAngularMomentum`: calculate and log the angular momentum of all bodies
- `--calculateLinearMomentum`: calculate and log the linear momentum of all bodies
- `--findOrbitLength`: determine the length of a single orbit (used to label plots by orbit)
- `--calculateShapeSpace`: use a variable transform to determine the stability of a given orbit or perturbation
- `--skipSaveToCSV`: skips saving data (used when running perturbations to prevent every simulation from being saved and overwritten constantly)
- `--perturbPositions`: run a perturbation with the given parameters of positions
- `--perturbVelocities`: run a perturbation with the given parameters of velocities
- `--perturbEnergies`: run a perturbation with the given parameters of energies
- `--halfGridSize`: Required for perturbations. the number of perturbations to run in each direction.
- `--delta`: Required for perturbations. The size of the perturbation step
- `--shiftEnergy`: How much to change the energy of the system for the perturbation plot
- `--energyDelta`: similar to `--delta` but for energy layers
- `--halfGridSizeEnergy`: similar to `--halfGridSize` but for energy layers

Note that `--checkStopConditions` and `--skipSaveToCSV` are assumed for all perturbations.


The Plotting code is entirely done in Python and some (rather rough) examples of the plotting is given in `Testing.py`




<!-- # Project-Jared-Douglas
N-Body Problem Project

**Jobs To Do**
- Run various benchmarking tests on the functions, try and find areas to optimise
- Play around with 3+ bodies in the system, try and get a figure 8 orbit
- 

**Code Refactoring to be done**
- Change simulation from a T by 6 by n Matrix to a T by n matrix of orbit objects containing time evolution of position and velocity
- change run_fast to be a flag instead of a new function


**Code changes to make for week 6**
- check to see if language speed is linear in timesteps (graph speeds over num timesteps)
- compare cpu times for different integration algorithms
- start moving onto 3 body cases
- add means of perturbing initial conditions (maintiaining total energy and angular momentum as constants - also CoM's pos & vel)
- keep in plane. vary 2 pos * 2 vel * 3 bodies variables
- use symmetries to keep constants constant
- 6 constants (1 for ang momentum, 1 for energy, 2 for CoM pos, 2 for CoM vel)
- solving this would be purely analytical, no numerics needed

**Benchmarking**
For $N=10000$, $dt=0.2$ using yoshida, elliptical looped simulation 100 times.
1st Run
Python = 94934ms
Julia  = 10900ms
Java   =  2513ms

2nd Run
Python = 86025 ms
Julia  = 12056 ms
Java   =  2547 ms -->
