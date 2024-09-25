from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators

N = 1000 #rename to N
dt = 0.01

simulation = Simulation.Simulation(N, dt, orbit_examples.figure_eight)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_centre_of_mass=True, plot_energy=False, plot_energy_error=False, animate_orbits=True, plot_3D=False)
plotter.plot()
