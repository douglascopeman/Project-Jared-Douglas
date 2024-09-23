from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples

T = 1000
dt = 0.051

simulation = Simulation.Simulation(T, dt, orbit_examples.figure_eight_moving)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_centre_of_mass=True, plot_energy=False, animate_orbits=True)
plotter.plot()
