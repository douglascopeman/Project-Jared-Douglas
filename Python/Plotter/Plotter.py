import matplotlib.pyplot as plt
import numpy as np
import Body

class Plotter():
    def __init__(self, filename, **plot_kwargs):
        self.G = 6.67408e-11
        
        self.filename = filename
        defaultKwargs = {}
        self.plot_kwargs = defaultKwargs | plot_kwargs
        
        self.total_energies = self.calculate_total_energy()
        self.dt = 1 #to be read in at some point
        self.nIter = 10 #as above
        
    def read_data(self):
        self.bodies = []
        with open(self.filename, 'r') as f:
            self.timesteps = []
            self.bodies.append(Body())
            
    def calculate_potential_energies(self):
        twice_U = 0.0
        for body in self.bodies:
            for other_body in self.bodies:
                if body is not other_body:
                    for position in body.positions:
                        direction = np.linalg.norm(body.position - other_body.position)
                        twice_U -= self.G * body.mass * \
                            other_body.mass / direction
                            
        return twice_U / 2
        
    def calculate_total_energy(self):
        '''Calculates the total energy of the system at each timestep and returns the result as a numpy array'''
        #TODO: check implimentation works with arrays and tweak if necessary as with the above
        T = sum(body.kinetic_energies() for body in self.bodies)
        U = self.calculate_potential_energies()
        
        return T + U
    
    def plot_energy(self):
        x = np.linspace(0, len(self.total_energies * self.dt, self.nIter))
        plt.plot(x, self.total_energies)
        plt.xlabel("Time")
        plt.ylabel("Total Energy (J)")
        plt.title("Total Energy of the System over Time")
        plt.show()