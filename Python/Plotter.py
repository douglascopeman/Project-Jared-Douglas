import matplotlib.pyplot as plt
from mpl_toolkits import mplot3d
from itertools import combinations
import numpy as np
import os
import Body
from matplotlib.animation import FuncAnimation

class Plotter():
    def __init__(self, outputDirectory, **plot_kwargs):
        self.outputDirectory = outputDirectory
        defaultKwargs = {
                        "plot_centre_of_mass":False,
                        "plot_energy":False,
                        "animate_orbits":False,
                        "animate_frame_interval":1,
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
        plt.show()
        
        if self.plot_kwargs["animate_orbits"]:
            self.animate_orbits()
             
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
        colors = plt.cm.viridis(np.linspace(0, 1, self.n))
        for i in range(self.n):
            ax.plot(self.bodies[:,0,i], self.bodies[:,1,i], self.bodies[:,2,i], color=colors[i], alpha=0.25)
            ax.plot(self.bodies[-1,0,i], self.bodies[-1,1,i], self.bodies[-1,2,i], 'o' ,label="Body " + str(i), color=colors[i])
    
    def add_centre_of_mass(self, fig, ax): 
        ax.plot(self.centreOfMass[:,0], self.centreOfMass[:,1], self.centreOfMass[:,2], color='black', alpha=0.25)
        ax.plot(self.centreOfMass[-1, 0], self.centreOfMass[-1,1], self.centreOfMass[-1,2], 'o', label="Centre of Mass", color='black')
    
    def plot_energy(self):
        total_energy = self.potentialEnergy + self.kineticEnergy
        energy_max_min = max(total_energy) - min(total_energy)
        print(energy_max_min)
        plt.plot(total_energy)
        plt.xlabel("Time")
        plt.ylabel("Total Energy (J)")
        plt.title("Total Energy of the System over Time")
        plt.show()
          
    # The below is generated code
    def animate_orbits(self):
        # Create a new figure for the animation
        fig = plt.figure()
        # Add 3D axes to the figure
        ax = plt.axes(projection='3d')

        # Create a list of line objects for each body with unique colors
        colors = plt.cm.viridis(np.linspace(0, 1, self.n))
        lines = [ax.plot([], [], [], color=colors[i], alpha=0.25)[0] for i in range(self.n)]
        # Create a list of point objects for each body with the same colors
        points = [ax.plot([], [], [], 'o', color=colors[i], alpha=1, label='Body ' + str(i))[0] for i in range(self.n)]
        # Create line and point objects for the centre of mass
        com_line, = ax.plot([], [], [], alpha=0.25, color='black')
        com_point, = ax.plot([], [], [], 'o', label="Centre of Mass", color='black')

        def init():
            # Set the limits for the 3D plot based on the bodies' positions
            ax.set_xlim3d([np.min(self.bodies[:, 0, :]), np.max(self.bodies[:, 0, :])])
            ax.set_ylim3d([np.min(self.bodies[:, 1, :]), np.max(self.bodies[:, 1, :])])
            ax.set_zlim3d([np.min(self.bodies[:, 2, :]), np.max(self.bodies[:, 2, :])])
            # Initialize the lines and points to be empty
            for line, point in zip(lines, points):
                line.set_data([], [])
                line.set_3d_properties([])
                point.set_data([], [])
                point.set_3d_properties([])
            # Initialize the centre of mass line and point to be empty
            com_line.set_data([], [])
            com_line.set_3d_properties([])
            com_point.set_data([], [])
            com_point.set_3d_properties([])
            # Return all line and point objects
            return lines + points + [com_line, com_point]

        def update(frame):
            # Update the lines and points for each body up to the current frame
            for i, (line, point) in enumerate(zip(lines, points)):
                line.set_data(self.bodies[:frame, 0, i], self.bodies[:frame, 1, i])
                line.set_3d_properties(self.bodies[:frame, 2, i])
                point.set_data(self.bodies[frame, 0, i], self.bodies[frame, 1, i])
                point.set_3d_properties(self.bodies[frame, 2, i])
            # Update the centre of mass line and point up to the current frame
            com_line.set_data(self.centreOfMass[:frame, 0], self.centreOfMass[:frame, 1])
            com_line.set_3d_properties(self.centreOfMass[:frame, 2])
            com_point.set_data(self.centreOfMass[frame, 0], self.centreOfMass[frame, 1])
            com_point.set_3d_properties(self.centreOfMass[frame, 2])
            # Return all updated line and point objects
            return lines + points + [com_line, com_point]

        # Create the animation using the update function and the number of frames
        ani = FuncAnimation(fig, update, frames=self.T, init_func=init, interval=self.plot_kwargs["animate_frame_interval"], blit=True)
        # Display the animation
        fig.legend()
        plt.show()
        
if __name__ == "__main__":
    import run