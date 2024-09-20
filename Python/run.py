from Body import Body
import numpy as np
import Plotter
import Simulation

earth = Body(np.array([0,0,0], dtype=float), np.array([0,1,0], dtype=float), 10)
moon =  Body(np.array([0,10,0], dtype=float), np.array([10,0,0], dtype=float), 1)
bodies = [earth, moon]
T = 10    # Number of "frames" in simulation
dt = 1    # Timestep between each frame

simulation = Simulation.Simulation(T, dt, bodies)
simulation.run()
plotter = Plotter.Plotter("Outputs")