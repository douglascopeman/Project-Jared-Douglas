from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import Pertubation
import time

N = 10000 #rename to N
dt = 0.1

# simulation = Simulation.Simulation(N,
#                                    dt,
#                                    orbit_examples.figure_eight,
#                                    Integrator = Integrators.yoshida,
#                                    is_focus_on_body = False,
#                                    is_variable_dt = False,
#                                    stop_conditions = {
#                                        "energy_error_bound": 0.001,
#                                         "distance_bound": 20,
#                                         "variable_dt_bound": 1*10**(-6)
#                                    }
#                                    )

# simulation.run()

# start = time.time()
# for i in range(0, 100):
#   simulation.run_fast()
# print(time.time() - start)

pertubation = Pertubation.Pertubation(
  N, dt, orbit_examples.figure_eight, 5, 0.01,
                                     stop_conditions = {
                                       "energy_error_bound": 0.001,
                                        "distance_bound": 20,
                                        "variable_dt_bound": 1*10**(-6)
                                   }
)
stop_matrix = pertubation.run()

print(stop_matrix)


# plotter = Plotter.Plotter("Python\\Outputs", 
#                           run_fast=False, 
#                           plot_centre_of_mass=True, 
#                           plot_energy=False, 
#                           plot_energy_error=True, 
#                           plot_angular_momentum_error = True, 
#                           plot_linear_momentum_error=False, 
#                           plot_3D=False)

# plotter.plot()