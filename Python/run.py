from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples

T = 650
dt = 0.01

simulation = Simulation.Simulation(T, dt, orbit_examples.figure_eight_moving)
simulation.run()
plotter = Plotter.Plotter("Outputs", plot_centre_of_mass=True, plot_energy=True, 
                          animate_orbits=True, animate_save=False, animate_fps=650)
plotter.plot()
