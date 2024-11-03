import matplotlib.pyplot as plt
from matplotlib import animation
import numpy as np
import os
from matplotlib.animation import FuncAnimation
import seaborn as sns

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
                        "run_fast":False,
                        "x_label":"Time",
                        "save_plots":False,
                        "is_orbit_duration":False,
                        }
        self.kwargs = defaultKwargs | kwargs
        
    def plot(self):
        self.read_data()
        
        ##### Orbit plot #####
        
        def close_all(something):
            plt.close('all')
        
        fig_orbits = plt.figure("Orbits")
        fig_orbits.set_size_inches(7.5, 7.5)
        fig_orbits.canvas.mpl_connect('close_event', close_all)
        
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
            fig_energy = self.plot_energy()
            fig_energy.canvas.mpl_connect('close_event', close_all)
        if self.kwargs["plot_energy_error"]:
            fig_energy_error = self.plot_energy_error()
            fig_energy_error.canvas.mpl_connect('close_event', close_all)
        if self.kwargs["plot_angular_momentum_error"]:
            fig_angular_momentum_error = self.plot_angular_momentum_error()
            fig_angular_momentum_error.canvas.mpl_connect('close_event', close_all)
        if self.kwargs["plot_linear_momentum_error"]:
            fig_linear_momentum_error = self.plot_linear_momentum_error()
            fig_linear_momentum_error.canvas.mpl_connect('close_event', close_all)
    
        if self.kwargs["save_plots"]:
            fig_energy_error.savefig("Python/Outputs/Energy Error.png")
            fig_angular_momentum_error.savefig("Python/Outputs/Angular Momentum Error.png")
            fig_orbits.savefig("Python/Outputs/Orbits.png")
        plt.show()
        
        #Animation
        if self.kwargs["animate_orbits"]:
            self.animate_orbits()
            
    def read_data(self):
        output_directory = os.path.join(os.getcwd(), self.outputDirectory)
        
        #Start by loading in the simulation settings to determine what to plot
        with open(os.path.join(output_directory, "simulationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.N = int(data[0])
            self.dt = data[1]
            self.n = int(data[2])
            self.G = float(data[3])
            if self.kwargs["is_orbit_duration"]:
                self.orbit_duration = int(data[4])
            else:
                self.orbit_duration = 0.0

        # Setup the time axis for all plots
        self.time_axis = np.linspace(0,(self.N * self.dt), self.N)
        if self.orbit_duration != 0.0:
            self.time_axis = self.time_axis / (self.orbit_duration*self.dt)
            
        #Then set an array up to hold the correct number of bodies and time steps
        self.bodies = np.zeros((self.N, 6, self.n))
        
        #Then load in the data for each body
        for i in range(self.n):
            with open(os.path.join(output_directory, "output" + str(i) + ".csv"), 'r') as f:
                data = np.loadtxt(f, delimiter=",")
                self.bodies[:,:,i] = data

        if not self.kwargs["run_fast"]:
            if self.kwargs["plot_centre_of_mass"]:
                self.centre_of_mass = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(output_directory, "centreOfMass.csv"), 'r') as f:
                    self.centre_of_mass[:,:] = np.loadtxt(f, delimiter=",")
                    
            if self.kwargs["plot_energy"] or self.kwargs["plot_energy_error"]:
                self.potential_energy = np.zeros((self.N), dtype=float)
                self.kinetic_energy = np.zeros((self.N), dtype=float)
                with open(os.path.join(output_directory, "potentialEnergy.csv"), 'r') as f:
                    self.potential_energy[:] = np.loadtxt(f, delimiter=",")
                with open(os.path.join(output_directory, "kineticEnergy.csv"), 'r') as f:
                    self.kinetic_energy = np.loadtxt(f, delimiter=",")
            
            if self.kwargs["plot_angular_momentum_error"]:
                self.angular_momentum = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(output_directory, "angularMomentum.csv"), 'r') as f:
                    self.angular_momentum = np.loadtxt(f, delimiter=",")

            if self.kwargs["plot_linear_momentum_error"]:
                self.linear_momentum = np.zeros((self.N, 3), dtype=float)
                with open(os.path.join(output_directory, "linearMomentum.csv"), 'r') as f:
                    self.linear_momentum = np.loadtxt(f, delimiter=",") 
           
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
            ax.plot(self.centre_of_mass[:,0], self.centre_of_mass[:,1], self.centre_of_mass[:,2], color='grey', alpha=0.25)
            ax.plot(self.centre_of_mass[-1, 0], self.centre_of_mass[-1,1], self.centre_of_mass[-1,2], 'o', label="Centre of Mass", color='grey')
        else:
            ax.plot(self.centre_of_mass[:,0], self.centre_of_mass[:,1], color='grey', alpha=0.25)
            ax.plot(self.centre_of_mass[-1, 0], self.centre_of_mass[-1,1], 'o', label="Centre of Mass", color='grey')
    
    def plot_energy(self):
        fig_energy = plt.figure("Energy")
        ax_energy = fig_energy.add_subplot()
        total_energy = self.potential_energy + self.kinetic_energy
        ax_energy.plot(self.time_axis, total_energy)
        ax_energy.set_xlabel(self.kwargs["x_label"])
        ax_energy.set_ylabel("Total Energy (J)")
        ax_energy.set_title("Total Energy of the System over Time")
        return fig_energy
        
    def plot_energy_error(self):
        fig_energy_error = plt.figure("Energy Error")
        ax_energy_error = fig_energy_error.add_subplot()

        total_energy = self.potential_energy + self.kinetic_energy
        initial_energy = total_energy[0]
        energy_error = [np.abs((total_energy[t] - initial_energy)/initial_energy) for t in range(0,self.N)]
        ax_energy_error.plot(self.time_axis, energy_error)
        ax_energy_error.set_xlabel(self.kwargs["x_label"])
        ax_energy_error.set_ylabel("Energy (J)")
        ax_energy_error.set_title("Relative Energy Error of the System over Time")
        return fig_energy_error

    def plot_angular_momentum_error(self):
        fig_angular_momentum_error = plt.figure("Angular Momentum Error")
        ax_energy_error = fig_angular_momentum_error.add_subplot()

        initial_angular_momentum = self.angular_momentum[0]
        angular_momentum_error = [np.abs((self.angular_momentum[t] - initial_angular_momentum)/initial_angular_momentum) for t in range(0,self.N)]
        ax_energy_error.plot(self.time_axis, angular_momentum_error)
        ax_energy_error.set_xlabel(self.kwargs["x_label"])
        ax_energy_error.set_ylabel("Angular Momentum (kg-m^2/s)")
        ax_energy_error.set_title("Relative Angular Momentum Error of the System over Time")
        return fig_angular_momentum_error

    def plot_linear_momentum_error(self):
        fig_linear_momentum_error = plt.figure("Linear Momentum Error")
        ax_energy_error = fig_linear_momentum_error.add_subplot()

        initial_linear_momentum = self.linear_momentum[0]
        linear_momentum_error = [np.abs((self.linear_momentum[t] - initial_linear_momentum)/initial_linear_momentum) for t in range(0, self.N)]
        ax_energy_error.plot(self.time_axis, linear_momentum_error)
        ax_energy_error.set_xlabel(self.kwargs["x_label"])
        ax_energy_error.set_ylabel("Linear Momentum (kg ms^-1)")
        ax_energy_error.set_title("Relative Linear Momentum Error of the System over Time")
        return fig_linear_momentum_error
          
    # The below is mostly generated code
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
            if self.kwargs["plot_centre_of_mass"]:
                com_line, = ax.plot([], [], [], alpha=0.5, color='grey')
                com_point, = ax.plot([], [], [], 'o', alpha=1, label="Centre of Mass", color='grey')
        else:
            lines = [ax.plot([], [], color=colors[i], alpha=0.5)[0] for i in range(self.n)]
            points = [ax.plot([], [], 'o', color=colors[i], alpha=1, label='Body ' + str(i))[0] for i in range(self.n)]
            if self.kwargs["plot_centre_of_mass"]:
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
                if self.kwargs["plot_centre_of_mass"]:
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
                if self.kwargs["plot_centre_of_mass"]:
                    com_line.set_data([], [])
                    com_point.set_data([], [])
            # Return all line and point objects
            if self.kwargs["plot_centre_of_mass"]:
                return lines + points + [com_line, com_point]
            else:
                return lines + points

        def update(frame):
            if self.kwargs["plot_3D"]:
                # Update the lines and points for each body up to the current frame
                for i, (line, point) in enumerate(zip(lines, points)):
                    line.set_data(self.bodies[:frame, 0, i], self.bodies[:frame, 1, i])
                    line.set_3d_properties(self.bodies[:frame, 2, i])
                    point.set_data(self.bodies[frame, 0, i], self.bodies[frame, 1, i])
                    point.set_3d_properties(self.bodies[frame, 2, i])
                # Update the centre of mass line and point up to the current frame
                if self.kwargs["plot_centre_of_mass"]:
                    com_line.set_data(self.centre_of_mass[:frame, 0], self.centre_of_mass[:frame, 1])
                    com_line.set_3d_properties(self.centre_of_mass[:frame, 2])
                    com_point.set_data(self.centre_of_mass[frame, 0], self.centre_of_mass[frame, 1])
                    com_point.set_3d_properties(self.centre_of_mass[frame, 2])
            else:
                # Update the lines and points for each body up to the current frame
                for i, (line, point) in enumerate(zip(lines, points)):
                    line.set_data(self.bodies[:frame, 0, i], self.bodies[:frame, 1, i])
                    point.set_data(self.bodies[frame, 0, i], self.bodies[frame, 1, i])
                # Update the centre of mass line and point up to the current frame
                if self.kwargs["plot_centre_of_mass"]:
                    com_line.set_data(self.centre_of_mass[:frame, 0], self.centre_of_mass[:frame, 1])
                    com_point.set_data(self.centre_of_mass[frame, 0], self.centre_of_mass[frame, 1])
            # Return all updated line and point objects
            if self.kwargs["plot_centre_of_mass"]:
                return lines + points + [com_line, com_point]
            else:
                return lines + points

        # Create the animation using the update function and the number of frames
        ani = FuncAnimation(fig, update, frames=self.N, init_func=init, blit=True, interval=self.N/self.kwargs["animate_fps"])
        # Display the animation
        fig.legend()
        if self.kwargs["animate_save"]:
            video_writer = animation.FFMpegWriter(fps=self.kwargs["animate_fps"], bitrate=5000)
            ani.save("media\\animation.mp4", writer=video_writer)
            print("Animation saved as 'media\\animation.mp4'")
        else:
            plt.show()
        plt.close()


        
        
        
if __name__ == "__main__":
    import Testing