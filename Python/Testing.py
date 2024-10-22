from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time

N = 5000 #rename to N
dt = 0.005

simulation = Simulation.Simulation(N,
                                   dt,
                                   orbit_examples.elliptical_difficult,
                                   Integrator = Integrators.three_step_leapfrog,
                                   is_focus_on_body = False,
                                   is_variable_dt = True,
                                 #   stop_conditions = {
                                 #       "energy_error_bound": 0.001,
                                 #        "distance_bound": 20,
                                 #        "variable_dt_bound": 1*10**(-6)
                                 #   }
                                   )

start = time.time()
simulation.run()
print(time.time() - start)


plotter = Plotter.Plotter("Python\\Outputs", 
                          run_fast=False, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=True, 
                          plot_angular_momentum_error = True, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False)

plotter.plot()