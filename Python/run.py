from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time

N = 5000 #rename to N
dt = 0.01

simulation = Simulation.Simulation(N, dt, orbit_examples.elliptical)

# start = time.time()
simulation.run()
# end = time.time()

# print("Elapsed Time:" + str(end - start))

plotter = Plotter.Plotter("Python\Outputs", 
                          runFast=False, 
                          plot_centre_of_mass=True, 
                          plot_energy=False, 
                          plot_energy_error=True, 
                          animate_orbits=False, 
                          plot_angular_momentum_error = True, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False)
plotter.plot()

# plotter = Plotter.Plotter("Julia\Outputs", runFast=True, plot_centre_of_mass=False, plot_energy=False, plot_energy_error=False, animate_orbits=False, plot_angular_momentum_error=False, plot_3D=False)
# plotter.plot()

# plotter = Plotter.Plotter("JavaSimulation\\Outputs", runFast=False, plot_centre_of_mass=False, plot_energy=False, plot_energy_error=False, animate_orbits=False, plot_angular_momentum_error=False, plot_3D=False)
# plotter.plot()
