from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time
import sys

N = 1000 #rename to N
dt = 0.05

simulation = Simulation.Simulation(N, 
                                   dt,
                                   orbit_examples.elliptical,
                                   Integrator = Integrators.threeStepLeapFrog,
                                   variable_dt = True
                                   )

simulation.run()

plotter = Plotter.Plotter("Python\\Outputs", 
                          runFast=False, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=True, 
                          plot_angular_momentum_error = True, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False,
                          animate_orbits=False)

plotter.plot()