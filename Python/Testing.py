from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import Perturbation
import PerturbationPlot
import time

N = 5000 #rename to N
dt = 0.1

# simulation = Simulation.Simulation(N,
#                                    dt,
#                                    orbit_examples.figure_eight,
#                                    Integrator = Integrators.yoshida,
#                                    is_focus_on_body = False,
#                                    is_variable_dt = False,
#                                   #  stop_conditions = {
#                                   #      "energy_error_bound": 0.001,
#                                   #       "distance_bound": 20,
#                                   #       "variable_dt_bound": 1*10**(-6)
#                                   #  }
#                                    )

# simulation.run()

# start = time.time()
# for i in range(0, 100):
#   simulation.run_fast()
# print(time.time() - start)

perturbation = Perturbation.Perturbation(
  N, dt, orbit_examples.figure_eight, 4, 0.01,
                                     stop_conditions = {
                                       "energy_error_bound": 0.001,
                                        "distance_bound": 20,
                                        "variable_dt_bound": 1*10**(-6)
                                   }
)
perturbation.run_specfic_pertubation(0.8,-0.8)




plotter = Plotter.Plotter("Python\\Outputs", 
                          run_fast=True, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=False, 
                          plot_angular_momentum_error = False, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False,
                          x_label="Time",
                          save_plots=False
                          )

plotter.plot()

# perturbation_plot = PerturbationPlot.PerturbationPlot("javasimulation\\Outputs")
# perturbation_plot.plot("timeMatrix")
# #perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")