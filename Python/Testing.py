from Body import Body
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import Perturbation
import PerturbationPlot
import ThreeDimensionalPerturbationPlot
import time

N = 5000 #rename to N
dt = 0.01

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

# perturbation = Perturbation.Perturbation(
#   N, dt, orbit_examples.figure_eight, 4, 0.01,
#                                      stop_conditions = {
#                                        "energy_error_bound": 0.001,
#                                         "distance_bound": 20,
#                                         "variable_dt_bound": 1*10**(-5)
#                                    }
# )
# perturbation.run_specfic_pertubation(0.1,0.1)




# plotter = Plotter.Plotter("javasimulation\\Outputs", 
#                           run_fast=True, 
#                           plot_centre_of_mass=False, 
#                           plot_energy=False, 
#                           plot_energy_error=False, 
#                           plot_angular_momentum_error = False, 
#                           plot_linear_momentum_error=False, 
#                           plot_3D=False,
#                           x_label="Time",
#                           save_plots=False
#                           )

# plotter.plot()
# plotter.plot_simulation_shape_space("javasimulation\\Outputs\\shapeSpaceMatrix.csv")





perturbation_plot = PerturbationPlot.PerturbationPlot("javasimulation\\Outputs")


# perturbation_plot.plot_time("timeMatrix")
# perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")
# perturbation_plot.plot_stability("stabilityMatrix")

# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix", "stabilityMatrix")
perturbation_plot.plot_stop_codes_gradient_simple("timeMatrix", "stopCodeMatrix")


# pertubation_3dplot = ThreeDimensionalPerturbationPlot.ThreeDimensionalPerturbationPlot("C:\\Users\\Douglas\\OneDrive - University of Edinburgh\\Uni\\Project-Jared-Douglas\\PaperTrail\\Pertubations\\Energy Layers\\First Energy Cube")
# pertubation_3dplot.scatter_plot("D")
