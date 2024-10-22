from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time

N = 10000 #rename to N
dt = 0.2

simulation = Simulation.Simulation(N,
                                   dt,
                                   orbit_examples.elliptical,
                                   Integrator = Integrators.yoshida,
                                   is_focus_on_body = False,
                                   is_variable_dt = False,
                                 #   stop_conditions = {
                                 #       "energy_error_bound": 0.001,
                                 #        "distance_bound": 20,
                                 #        "variable_dt_bound": 1*10**(-6)
                                 #   }
                                   )

start = time.time()
for i in range(0, 100):
  simulation.run_fast()
print(time.time() - start)


plotter = Plotter.Plotter("Python\\Outputs", 
                          run_fast=True, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=False, 
                          plot_angular_momentum_error = False, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False)

plotter.plot()