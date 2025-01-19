import Simulation
from orbit_examples import orbit_examples
import Integrators
import time

N = 500 
dt = 0.01

simulation = Simulation.Simulation(N,
                                   dt,
                                   orbit_examples.figure_eight,
                                   Integrator = Integrators.yoshida,
                                   is_focus_on_body = False,
                                   is_variable_dt = False,
                                  #  stop_conditions = {
                                  #      "energy_error_bound": 0.001,
                                  #       "distance_bound": 20,
                                  #       "variable_dt_bound": 1*10**(-6)
                                  #  }
                                   )

simulation.run()
print("Done!")

# start = time.time()
# for i in range(0, 100):
#   simulation.run_fast()
# print(time.time() - start)