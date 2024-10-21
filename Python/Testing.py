from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators

N = 5000 #rename to N
dt = 0.05

simulation = Simulation.Simulation(N, 
                                   dt,
                                   orbit_examples.elliptical,
                                   Integrator = Integrators.yoshida,
                                   is_focus_on_body = False,
                                   stop_conditions = {
                                       "energy_error_bound": 1,
                                        "distance_bound": 10,
                                        "variable_dt_bound": 1*10**(-8)
                                   }
                                   )

simulation.run()

plotter = Plotter.Plotter("Python\\Outputs", 
                          run_fast=False, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=True, 
                          plot_angular_momentum_error = True, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False)

plotter.plot()