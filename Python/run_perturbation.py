from orbit_examples import orbit_examples
import Perturbation

N = 1000
dt = 0.1

perturbation = Perturbation.Perturbation(
  N, dt, orbit_examples.figure_eight, 10, 0.01,
                                     stop_conditions = {
                                       "energy_error_bound": 0.001,
                                        "distance_bound": 20,
                                        "variable_dt_bound": 1*10**(-5)
                                   }
)

perturbation.run()


# perturbation.run_specfic_pertubation(0.1,0.1)
