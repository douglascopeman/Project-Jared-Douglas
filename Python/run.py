from Body import Body
import numpy as np
import Plotter
import Simulation

earth = Body(np.array([-5,5,0], dtype=float), np.array([-0.5,0,0], dtype=float), 10)
moon =  Body(np.array([5,-5,0], dtype=float), np.array([0.5,0,0], dtype=float), 10)
bodies = [earth, moon]
T = 1000    # Number of "frames" in simulation
dt = 1    # Timestep between each frame

simulation = Simulation.Simulation(T, dt, bodies)
simulation.run()
plotter = Plotter.Plotter("Outputs")