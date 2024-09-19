import mainPython
from Body import Body
import numpy as np
from Plotter import Plotter

earth = Body(np.array([0,0,0], dtype=float), np.array([0,1,0], dtype=float), 1000)
moon =  Body(np.array([0,10,0], dtype=float), np.array([10,0,0], dtype=float), 1)
bodies = [earth, moon]
T = 1000    # Number of "frames" in simulation
dt = 1    # Timestep between each frame

mainPython.run(T, dt, bodies)
plotter = Plotter("Outputs")