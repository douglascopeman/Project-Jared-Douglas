from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators

N = 5000 #rename to N
dt = 0.01

simulation = Simulation.Simulation(N, dt, orbit_examples.elliptical)
simulation.run()
# plotter = Plotter.Plotter("Python\Outputs", runFast=True, plot_centre_of_mass=False, plot_energy=False, plot_energy_error=False, animate_orbits=False, plot_angular_momentum_error=False, plot_3D=False)
# plotter.plot()
