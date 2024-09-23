from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples

T = 10000
dt = 0.01

simulation = Simulation.Simulation(T, dt, orbit_examples.figure_eight)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_centre_of_mass=True, plot_energy=True)
plotter.plot()
