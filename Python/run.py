from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators

N = 100 #rename to N
dt = 0.001

simulation = Simulation.Simulation(N, dt, orbit_examples.circular)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_centre_of_mass=True, plot_energy_error=True, animate_orbits=False, plot_3D=False)
plotter.plot()
