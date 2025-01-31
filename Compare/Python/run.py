from Body import Body
import numpy as np
import Simulation
from orbit_examples import orbit_examples
import Integrators
import time
import sys

# Run now used for benchmarking
# Testing.py is for playing around with orbits etc...
if len(sys.argv) != 3: 
    sys.exit(1)
else:  
    N = int(sys.argv[1])
    dt = float(sys.argv[2])
    
    simulation = Simulation.Simulation(N, dt, orbit_examples.circular)

    start = time.time()
    simulation.run_fast()
    end = time.time()

    elapsed_time_ms = (end - start) * 1000
    print("\tTime: {:.3f} ms".format(elapsed_time_ms))
