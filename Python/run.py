from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples

T = 1000
dt = 0.01

simulation = Simulation.Simulation(T, dt, orbit_examples.linear_start_choreography)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_energy=True)
plotter.plot()
