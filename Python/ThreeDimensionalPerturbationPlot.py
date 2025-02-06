import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd
from matplotlib.colors import ListedColormap
import matplotlib.animation as animation
from matplotlib.colors import LogNorm, Normalize


class ThreeDimensionalPerturbationPlot():

    def __init__(self, output_directory):
        self.output_directory = output_directory

    def read_data(self, is_energy):

        # Reading the settings file
        with open(os.path.join(self.output_directory, "3dperturbationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=',')
            self.N = int(data[0])
            self.delta_axis1 = float(data[1])
            self.delta_axis2 = float(data[2])
            self.p_axis1 = int(data[3])
            self.p_axis2 = int(data[4])
            self.plot_size_axis1 = 2*self.p_axis1 + 1
            if is_energy:
                self.plot_size_axis2 = 2*self.p_axis2 +1
            else:
                self.plot_size_axis2 = self.p_axis2
            # Axis 1 will be the dimension of the matrix in each csv file and axis 2
            # will be the number of csv files

            # Initialising the matrix variables
            self.time_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1, self.plot_size_axis2), dtype=int)
            self.stop_code_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1, self.plot_size_axis2), dtype=str)
        
        if is_energy:
            for i in range(-self.p_axis2, self.p_axis2):
                # Reading the timeMatrix
                with open(os.path.join(self.output_directory, "timeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=int)

                    self.time_matrix[:,:,i+self.p_axis2-1] = data
                # Reading the stopCodeMatrix
                with open(os.path.join(self.output_directory, "stopCodeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=str)
                    self.stop_code_matrix[:,:,i+self.p_axis2-1] = data
        else:
            for i in range(self.p_axis2):
                # Reading the timeMatrix
                with open(os.path.join(self.output_directory, "timeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=int)
                    self.time_matrix[:,:,i] = data
                # Reading the stopCodeMatrix
                with open(os.path.join(self.output_directory, "stopCodeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=str)
                    self.stop_code_matrix[:,:,i] = data

    def animate(self, is_energy=False):
        '''
        Creates an animation with each frame a different energy/angular momentum shift
        '''
        self.read_data(is_energy)

        fig, ax = plt.subplots(figsize=(10, 8))

        axis_labels = np.round(np.linspace(-self.p_axis1*self.delta_axis1, self.p_axis1*self.delta_axis1, self.plot_size_axis1),2)
        df = pd.DataFrame(self.time_matrix[:,:,0].T, columns=axis_labels, index=-axis_labels)   # We update this dataframe each frame, its used to ensure the axis labels are correct

        skip_no_labels = np.size(axis_labels)//10  # To declutter the axis labeling we only show 10 labels


        sns.heatmap(self.time_matrix[:,:,0].T, xticklabels=skip_no_labels, yticklabels=skip_no_labels, ax=ax,  norm=LogNorm(vmin=self.time_matrix.min()+1, vmax=self.time_matrix.max()))
        
        def update(frame):
            """Update function for animation."""
            ax.clear()
            df = pd.DataFrame(self.time_matrix[:, :, frame].T, columns=axis_labels, index=-axis_labels)
            sns.heatmap(df, cbar=False, xticklabels=skip_no_labels, yticklabels=skip_no_labels, ax=ax, norm=LogNorm(vmin=self.time_matrix.min()+1, vmax=self.time_matrix.max()))
    

        # Create the animation
        ani = animation.FuncAnimation(fig, update, frames=self.plot_size_axis2, interval=40)
        writervideo = animation.FFMpegWriter(fps=30) 
        # Save the animation to a file or display it
        ani.save('heatmap_animation.mp4', writer=writervideo)

    
    def scatter_plot(self, stop_code):
        self.read_data()
        print(self.delta_axis2)

        x = []
        y = []
        z = []

        for i in range(self.plot_size_axis1):
            for j in range(self.plot_size_axis1):
                for k in range(self.p_axis2):
                    if self.stop_code_matrix[i,j,k] == stop_code:
                        x.append(round((i-self.p_axis1)*self.delta_axis1,3))
                        y.append(round((j-self.p_axis1)*self.delta_axis1,3))
                        z.append(round((k-self.p_axis2)*self.delta_axis2,3))

        fig = plt.figure()
        ax = plt.axes(projection='3d') 
        ax.scatter3D(x,y,z)   
        ax.set_xlabel('X Axis')
        ax.set_ylabel('Y Axis')
        ax.set_zlabel('Z Axis')
        plt.show()
