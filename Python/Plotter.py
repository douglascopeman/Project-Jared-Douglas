import matplotlib.pyplot as plt
from mpl_toolkits import mplot3d
import numpy as np
import os
import Body

class Plotter():
    def __init__(self, outputDirectory, **plot_kwargs):
        self.G = 1
        
        self.outputDirectory = outputDirectory
        defaultKwargs = {}
        self.plot_kwargs = defaultKwargs | plot_kwargs
        
        self.read_data()
        self.plot_orbits()
        
        #self.total_energies = self.calculate_total_energy()
        
    def read_data(self):
        outputDirectory = os.path.join(os.getcwd(), self.outputDirectory)
        
        #Start by loading in the simulation settings to determine what to plot
        with open(os.path.join(outputDirectory, "simulationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.T = int(data[0])
            self.dt = data[1]
            self.n = int(data[2])
            
        #Then set an array up to hold the correct number of bodies and time steps
        self.bodies = np.zeros((self.T, 6, self.n))
        
        #Then load in the data for each body
        for i in range(self.n):
            with open(os.path.join(outputDirectory, "output" + str(i) + ".csv"), 'r') as f:
                data = np.loadtxt(f, delimiter=",")
                self.bodies[:,:,i] = data
                
        print(self.bodies)
        
    def plot_orbits(self):
        fig = plt.figure()
        ax = plt.axes(projection='3d')  
        for i in range(self.n):
            ax.plot(self.bodies[:,0,i], self.bodies[:,1,i], self.bodies[:,2,i])
            ax.scatter(self.bodies[-1,0,i], self.bodies[-1,1,i], self.bodies[-1,2,i], label="Body " + str(i))
        fig.legend()
        plt.show()
            
    def calculate_potential_energies(self):
        #TODO: check this works with arrays and tweak if necessary
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
        
if __name__ == "__main__":
    plotter = Plotter("Outputs")