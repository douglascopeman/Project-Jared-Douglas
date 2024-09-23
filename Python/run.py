from Body import Body
import numpy as np
import Plotter
import Simulation

earth = Body(np.array([-5,5,0], dtype=float), np.array([-0.5,0,0], dtype=float), 10)
moon =  Body(np.array([5,0,0], dtype=float), np.array([0.5,0,0], dtype=float), 10)
bodies = [earth, moon]
T = 10000
dt = 0.005

simulation = Simulation.Simulation(T, dt, bodies)
simulation.run()
plotter = Plotter.Plotter("Outputs")
plotter.plot()
