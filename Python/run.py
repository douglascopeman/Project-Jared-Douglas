from Body import Body
import numpy as np
import Plotter
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time
import sys

N = 1000 #rename to N
dt = 0.01


simulation = Simulation.Simulation(N, 
                                   dt,
                                   orbit_examples.elliptical,
                                   #Integrator = Integrators.yoshida
                                   variable_dt_constant = 0.005
                                   )

simulation.run()

# plotter = Plotter.Plotter("Python\\Outputs", 
#                           runFast=True, 
#                           plot_centre_of_mass=False, 
#                           plot_energy=False, 
#                           plot_energy_error=False, 
#                           plot_angular_momentum_error = False, 
#                           plot_linear_momentum_error=False, 
#                           plot_3D=False,
#                           animate_orbits=False)

# plotter.plot()







if __name__ == "__main__":
    if len(sys.argv) != 3: 
        print("Usage: python run.py N dt")
        sys.exit(1)
        
    N = int(sys.argv[1])
    dt = float(sys.argv[2])

    simulation = Simulation.Simulation(N, 
                                   dt, 
                                   orbit_examples.circular)

    start = time.time()
    simulation.run()
    end = time.time()

    print("\tTime:" + str(end - start))
