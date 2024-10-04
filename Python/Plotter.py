import matplotlib.pyplot as plt
from matplotlib import animation
from mpl_toolkits import mplot3d
from itertools import combinations
import numpy as np
import os
import Body
from matplotlib.animation import FuncAnimation

class Plotter():
    def __init__(self, outputDirectory, **kwargs):
        self.outputDirectory = outputDirectory
        defaultKwargs = {
                        "plot_3D":True,
                        "plot_centre_of_mass":False,
                        "plot_energy":False,
                        "plot_energy_error":False,
                        "plot_angular_momentum_error":False,
                        "plot_linear_momentum_error":False,
                        "animate_orbits":False,
                        "animate_save":False,
                        "animate_fps":30,
                        "runFast":False
                        }
        self.kwargs = defaultKwargs | kwargs
        
    def plot(self):
        self.read_data()
        
        ##### Orbit plot #####
        fig_orbits = plt.figure("Orbits")
        fig_orbits.set_size_inches(7.5, 7.5)
        
        if self.kwargs["plot_3D"]: 
            ax_orbits = plt.axes(projection='3d') 
        else: 
            ax_orbits = plt.axes()
            
        self.add_orbits(fig_orbits, ax_orbits)
        
        if self.kwargs["plot_centre_of_mass"]:
            self.add_centre_of_mass(fig_orbits, ax_orbits)
            
        fig_orbits.legend()
             
        #Other plots
        if self.kwargs["plot_energy"]:
            self.plot_energy()
        if self.kwargs["plot_energy_error"]:
            self.plot_energy_error()
        if self.kwargs["plot_angular_momentum_error"]:
            self.plot_angular_momentum_error()
        if self.kwargs["plot_linear_momentum_error"]:
            self.plot_linear_momentum_error()
    
        plt.show()
        
        #Animation
        if self.kwargs["animate_orbits"]:
            self.animate_orbits()
            
    def read_data(self):
        outputDirectory = os.path.join(os.getcwd(), self.outputDirectory)
        
        #Start by loading in the simulation settings to determine what to plot
        with open(os.path.join(outputDirectory, "simulationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.N = int(data[0])
            self.dt = data[1]
            self.n = int(data[2])
            self.G = float(data[3])
            
        #Then set an array up to hold the correct number of bodies and time steps
        self.bodies = np.zeros((self.N, 6, self.n))
        
        #Then load in the data for each body
        for i in range(self.n):
            with open(os.path.join(outputDirectory, "output" + str(i) + ".csv"), 'r') as f:
                data = np.loadtxt(f, delimiter=",")
                self.bodies[:,:,i] = data

        if not self.kwargs["runFast"]:
            if self.kwargs["plot_centre_of_mass"]:
                self.centreOfMass = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(outputDirectory, "centreOfMass.csv"), 'r') as f:
                    self.centreOfMass[:,:] = np.loadtxt(f, delimiter=",")
                    
            if self.kwargs["plot_energy"] or self.kwargs["plot_energy_error"]:
                self.potentialEnergy = np.zeros((self.N), dtype=float)
                self.kineticEnergy = np.zeros((self.N), dtype=float)
                with open(os.path.join(outputDirectory, "potentialEnergy.csv"), 'r') as f:
                    self.potentialEnergy[:] = np.loadtxt(f, delimiter=",")
                with open(os.path.join(outputDirectory, "kineticEnergy.csv"), 'r') as f:
                    self.kineticEnergy = np.loadtxt(f, delimiter=",")
            
            if self.kwargs["plot_angular_momentum_error"]:
                self.angularMomentum = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(outputDirectory, "angularMomentum.csv"), 'r') as f:
                    self.angularMomentum = np.loadtxt(f, delimiter=",")

            if self.kwargs["plot_linear_momentum_error"]:
                self.linearMomentum = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(outputDirectory, "linearMomentum.csv"), 'r') as f:
                    self.linearMomentum = np.loadtxt(f, delimiter=",")
            
            
    def determine_max_range(self, bodies):
        max_range = np.max(np.abs(np.min(bodies[:,0:3,:], axis=(0,1)), 
                                  np.max(bodies[:,0:3,:], axis=(0,1)))) * 1.1
        
        return max_range
        
    def add_orbits(self, fig, ax):
        colors = plt.cm.hsv(np.linspace(0.1, 1, self.n))
        max_range = self.determine_max_range(self.bodies)
        ax.set_xlim(-max_range, max_range)
        ax.set_ylim(-max_range, max_range)
        if ax.name == '3d':
            ax.set_zlim(-max_range, max_range)
            for i in range(self.n):
                ax.plot(self.bodies[:,0,i], self.bodies[:,1,i], self.bodies[:,2,i], color=colors[i], alpha=0.25)
                ax.plot(self.bodies[-1,0,i], self.bodies[-1,1,i], self.bodies[-1,2,i], 'o' ,label="Body " + str(i), color=colors[i])
        else:
            for i in range(self.n):
                ax.plot(self.bodies[:,0,i], self.bodies[:,1,i], color=colors[i], alpha=0.25)
                ax.plot(self.bodies[-1,0,i], self.bodies[-1,1,i], 'o' ,label="Body " + str(i), color=colors[i])
    
    def add_centre_of_mass(self, fig, ax): 
        if ax.name == '3d':
            ax.plot(self.centreOfMass[:,0], self.centreOfMass[:,1], self.centreOfMass[:,2], color='grey', alpha=0.25)
            ax.plot(self.centreOfMass[-1, 0], self.centreOfMass[-1,1], self.centreOfMass[-1,2], 'o', label="Centre of Mass", color='grey')
        else:
            ax.plot(self.centreOfMass[:,0], self.centreOfMass[:,1], color='grey', alpha=0.25)
            ax.plot(self.centreOfMass[-1, 0], self.centreOfMass[-1,1], 'o', label="Centre of Mass", color='grey')
    
    def plot_energy(self):
        fig_energy = plt.figure("Energy")
        ax_energy = fig_energy.add_subplot()
        total_energy = self.potentialEnergy + self.kineticEnergy
        ax_energy.plot(total_energy)
        ax_energy.set_xlabel("Time")
        ax_energy.set_ylabel("Total Energy (J)")
        ax_energy.set_title("Total Energy of the System over Time")
        
        
    def plot_energy_error(self):
        fig_energy_error = plt.figure("Energy Error")
        ax_energy_error = fig_energy_error.add_subplot()

        total_energy = self.potentialEnergy + self.kineticEnergy
        initial_energy = total_energy[0]
        energy_error = [np.abs((total_energy[t] - initial_energy)/initial_energy) for t in range(0,self.N)]
        ax_energy_error.plot(energy_error)
        ax_energy_error.set_xlabel("Time")
        ax_energy_error.set_ylabel("Energy (J)")
        ax_energy_error.set_title("Relative Energy Error of the System over Time")

    def plot_angular_momentum_error(self):
        fig_angular_momentum_error = plt.figure("Angular Momentum Error")
        ax_energy_error = fig_angular_momentum_error.add_subplot()

        initial_angular_momentum = self.angularMomentum[0]
        angular_momentum_error = [np.abs((self.angularMomentum[t] - initial_angular_momentum)/initial_angular_momentum) for t in range(0,self.N)]
        ax_energy_error.plot(angular_momentum_error)
        ax_energy_error.set_xlabel("Time")
        ax_energy_error.set_ylabel("Angular Momentum (kg-m^2/s)")
        ax_energy_error.set_title("Relative Angular Momentum Error of the System over Time")

    def plot_linear_momentum_error(self):
        fig_linear_momentum_error = plt.figure("Linear Momentum Error")
        ax_energy_error = fig_linear_momentum_error.add_subplot()

        initial_linear_momentum = self.linearMomentum[0]
        linear_momentum_error = [np.abs((self.linearMomentum[t] - initial_linear_momentum)/initial_linear_momentum) for t in range(0, self.N)]
        print(linear_momentum_error)
        ax_energy_error.plot(linear_momentum_error)
        ax_energy_error.set_xlabel("Time")
        ax_energy_error.set_ylabel("Linear Momentum (kg ms^-1)")
        ax_energy_error.set_title("Relative Linear Momentum Error of the System over Time")
          
    # The below is generated code
    def animate_orbits(self):
        # Create a new figure for the animation
        fig = plt.figure()
        fig.set_size_inches(7.5, 7.5)
        max_range = self.determine_max_range(self.bodies) * 1.1
        
        # Check if 3D plotting is enabled
        if self.kwargs["plot_3D"]:
            # Add 3D axes to the figure
            ax = plt.axes(projection='3d')
        else:
            # Add 2D axes to the figure
            ax = plt.axes()
            
        # Create a list of line objects for each body with unique colors
        colors = plt.cm.hsv(np.linspace(0.1, 1, self.n))
        if self.kwargs["plot_3D"]:
            lines = [ax.plot([], [], [], color=colors[i], alpha=0.5)[0] for i in range(self.n)]
            points = [ax.plot([], [], [], 'o', color=colors[i], alpha=1, label='Body ' + str(i))[0] for i in range(self.n)]
            com_line, = ax.plot([], [], [], alpha=0.5, color='grey')
            com_point, = ax.plot([], [], [], 'o', alpha=1, label="Centre of Mass", color='grey')
        else:
            lines = [ax.plot([], [], color=colors[i], alpha=0.5)[0] for i in range(self.n)]
            points = [ax.plot([], [], 'o', color=colors[i], alpha=1, label='Body ' + str(i))[0] for i in range(self.n)]
            com_line, = ax.plot([], [], alpha=0.5, color='grey')
            com_point, = ax.plot([], [], 'o', alpha=1, label="Centre of Mass", color='grey')

        def init():
            if self.kwargs["plot_3D"]:
                # Set the limits for the 3D plot based on the bodies' positions
                ax.set_xlim3d([-max_range, max_range])
                ax.set_ylim3d([-max_range, max_range])
                ax.set_zlim3d([-max_range, max_range])
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
            else:
                # Set the limits for the 2D plot based on the bodies' positions
                ax.set_xlim(-max_range, max_range)
                ax.set_ylim(-max_range, max_range)
                # Initialize the lines and points to be empty
                for line, point in zip(lines, points):
                    line.set_data([], [])
                    point.set_data([], [])
                # Initialize the centre of mass line and point to be empty
                com_line.set_data([], [])
                com_point.set_data([], [])
            # Return all line and point objects
            return lines + points + [com_line, com_point]

        def update(frame):
            if self.kwargs["plot_3D"]:
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
            else:
                # Update the lines and points for each body up to the current frame
                for i, (line, point) in enumerate(zip(lines, points)):
                    line.set_data(self.bodies[:frame, 0, i], self.bodies[:frame, 1, i])
                    point.set_data(self.bodies[frame, 0, i], self.bodies[frame, 1, i])
                # Update the centre of mass line and point up to the current frame
                com_line.set_data(self.centreOfMass[:frame, 0], self.centreOfMass[:frame, 1])
                com_point.set_data(self.centreOfMass[frame, 0], self.centreOfMass[frame, 1])
            # Return all updated line and point objects
            return lines + points + [com_line, com_point]

        # Create the animation using the update function and the number of frames
        ani = FuncAnimation(fig, update, frames=self.N, init_func=init, blit=True, interval=self.N/self.kwargs["animate_fps"])
        # Display the animation
        fig.legend()
        if self.kwargs["animate_save"]:
            video_writer = animation.FFMpegWriter(fps=self.kwargs["animate_fps"], bitrate=5000)
            ani.save("animation.mp4", writer=video_writer)
            print("Animation saved as 'animation.mp4'")
        else:
            plt.show()
        plt.close()
        
        
if __name__ == "__main__":
    import run