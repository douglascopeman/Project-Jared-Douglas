import matplotlib.pyplot as plt
from mpl_toolkits import mplot3d
from itertools import combinations
import numpy as np
import os
import Body

class Plotter():
    def __init__(self, outputDirectory, **plot_kwargs):
        self.outputDirectory = outputDirectory
        defaultKwargs = {
                        "plot_centre_of_mass":False,
                        "plot_energy":False
                        }
        self.plot_kwargs = defaultKwargs | plot_kwargs
        
    def plot(self):
        self.read_data()
        
        #Orbit plot
        fig = plt.figure()
        ax = plt.axes(projection='3d')
        self.add_orbits(fig, ax)
        if self.plot_kwargs["plot_centre_of_mass"]:
            self.add_centre_of_mass(fig, ax)
        fig.legend()
        fig.show()
        plt.show()
             
        #Other plots
        if self.plot_kwargs["plot_energy"]:
            self.plot_energy()
        
    def read_data(self):
        outputDirectory = os.path.join(os.getcwd(), self.outputDirectory)
        
        #Start by loading in the simulation settings to determine what to plot
        with open(os.path.join(outputDirectory, "simulationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.T = int(data[0])
            self.dt = data[1]
            self.n = int(data[2])
            self.G = float(data[3])
            
        #Then set an array up to hold the correct number of bodies and time steps
        self.bodies = np.zeros((self.T, 6, self.n))
        
        #Then load in the data for each body
        for i in range(self.n):
            with open(os.path.join(outputDirectory, "output" + str(i) + ".csv"), 'r') as f:
                data = np.loadtxt(f, delimiter=",")
                self.bodies[:,:,i] = data

        # Set an array to hold centreOfMass and potentialEnergy
        self.centreOfMass = np.zeros((self.T, 3), dtype=float)
        self.potentialEnergy = np.zeros((self.T), dtype=float)
        self.kineticEnergy = np.zeros((self.T), dtype=float)

        # Load in centreOfMass data
        with open(os.path.join(outputDirectory, "centreOfMass.csv"), 'r') as f:
            self.centreOfMass[:,:] = np.loadtxt(f, delimiter=",")
        with open(os.path.join(outputDirectory, "potentialEnergy.csv"), 'r') as f:
            self.potentialEnergy[:] = np.loadtxt(f, delimiter=",")
        with open(os.path.join(outputDirectory, "kineticEnergy.csv"), 'r') as f:
            self.kineticEnergy = np.loadtxt(f, delimiter=",")
        

    def add_orbits(self, fig, ax):
        for i in range(self.n):
            ax.plot(self.bodies[:,0,i], self.bodies[:,1,i], self.bodies[:,2,i])
            ax.scatter(self.bodies[-1,0,i], self.bodies[-1,1,i], self.bodies[-1,2,i], label="Body " + str(i))
    
    def plot_energy(self):
        total_energy = self.potentialEnergy + self.kineticEnergy
        energy_max_min = max(total_energy) - min(total_energy)
        print(energy_max_min)
        plt.plot(total_energy)
        plt.xlabel("Time")
        plt.ylabel("Total Energy (J)")
        plt.title("Total Energy of the System over Time")
        plt.show()
        
    def add_centre_of_mass(self, fig, ax): 
        ax.plot(self.centreOfMass[:,0], self.centreOfMass[:,1], self.centreOfMass[:,2])
        ax.scatter(self.centreOfMass[-1, 0], self.centreOfMass[-1,1], self.centreOfMass[-1,2], label="Centre of Mass")