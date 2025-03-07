import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd
from mpl_toolkits.axes_grid1 import make_axes_locatable
from matplotlib.colors import ListedColormap
import matplotlib.animation as animation
from matplotlib.colors import LogNorm, Normalize
import subprocess, sys
import Plotter


class ThreeDimensionalPerturbationPlot():

    def __init__(self, output_directory, is_energy = False):
        self.output_directory = output_directory
        self.is_energy = is_energy

        self.read_settings()

    def read_settings(self):
        # Reading the settings file
        with open(os.path.join(self.output_directory, "3dperturbationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=',')
            self.N = int(data[0])
            self.delta_axis1 = float(data[1])
            self.delta_axis2 = float(data[2])
            self.p_axis1 = int(data[3])
            self.p_axis2 = int(data[4])
            self.plot_size_axis1 = 2*self.p_axis1 + 1
            if self.is_energy:
                self.plot_size_axis2 = 2*self.p_axis2 +1
            else:
                self.plot_size_axis2 = self.p_axis2+1
            # Axis 1 will be the dimension of the matrix in each csv file and axis 2
            # will be the number of csv files

        self.color_map_blends = ["blend:#300,#E00", "blend:#003,#00E", "blend:#030,#0E0", "blend:#330,#CC0", "blend:#033,#0CC", "blend:#303,#C0C"]
        self.stable_color_blend = "blend:#FFF,#000" #Scale going other way as big numbers are less stable vs big time being better above




    def read_data(self, is_energy):
        # Initialising the matrix variables
        self.time_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1, self.plot_size_axis2), dtype=int)
        self.stop_code_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1, self.plot_size_axis2), dtype=str)
        self.stability_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1, self.plot_size_axis2), dtype=int)

        
        if is_energy:
            for i in range(-self.p_axis2, self.p_axis2):
                # Reading the timeMatrix
                with open(os.path.join(self.output_directory, "timeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=int)

                    self.time_matrix[:,:,i+self.p_axis2-1] = data.T
                # Reading the stopCodeMatrix
                with open(os.path.join(self.output_directory, "stopCodeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=str)
                    self.stop_code_matrix[:,:,i+self.p_axis2-1] = data.T
        else:
            for i in range(self.plot_size_axis2):
                # Reading the timeMatrix
                with open(os.path.join(self.output_directory, "timeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=int)
                    self.time_matrix[:,:,i] = data.T
                # Reading the stopCodeMatrix
                with open(os.path.join(self.output_directory, "stopCodeMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=str)
                    self.stop_code_matrix[:,:,i] = data.T
                # Reading the stabilityMatrix
                with open(os.path.join(self.output_directory, "stabilityMatrix" + str(i*self.delta_axis2) + ".csv"), 'r') as f:
                    data = np.loadtxt(f, delimiter=",", dtype=str)
                    self.stability_matrix[:,:,i] = data.T

    def read_data_dxdy_slice(self):
        '''
        To avoid reading in the full cube when trying to plot dxdy slices we read in only that slice
        '''
        # Initialising the matrix variables
        self.time_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1), dtype=int)
        self.stop_code_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1), dtype=str)
        self.stability_matrix = np.zeros((self.plot_size_axis1, self.plot_size_axis1), dtype=int)


        with open(os.path.join(self.output_directory, "timeMatrix" + str(self.slice*self.delta_axis2) + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=int)
            self.time_matrix[:,:] = data.T
        # Reading the stopCodeMatrix
        with open(os.path.join(self.output_directory, "stopCodeMatrix" + str(self.slice*self.delta_axis2) + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=str)
            self.stop_code_matrix[:,:] = data.T
        # Reading the stabilityMatrix
        with open(os.path.join(self.output_directory, "stabilityMatrix" + str(self.slice*self.delta_axis2) + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=str)
            self.stability_matrix[:,:] = data.T
            self.stability_matrix[self.stability_matrix >170000] = 0

    def animate(self, is_energy=False):
        '''
        Creates an animation with each frame a different energy/angular momentum shift
        '''
        self.read_data(is_energy) 

        fig, ax = plt.subplots(figsize=(10, 8))

        axis_labels = np.round(np.linspace(-self.p_axis1*self.delta_axis1, self.p_axis1*self.delta_axis1, self.plot_size_axis1),2)
        df = pd.DataFrame(self.time_matrix[:,:,0], columns=axis_labels, index=-axis_labels)   # We update this dataframe each frame, its used to ensure the axis labels are correct

        skip_no_labels = np.size(axis_labels)//10  # To declutter the axis labeling we only show 10 labels


        sns.heatmap(self.time_matrix[:,:,0], xticklabels=skip_no_labels, yticklabels=skip_no_labels, ax=ax,  norm=LogNorm(vmin=self.time_matrix.min()+1, vmax=self.time_matrix.max()))
        
        def update(frame):
            """Update function for animation."""
            ax.clear()
            df = pd.DataFrame(self.time_matrix[:, :, frame], columns=axis_labels, index=-axis_labels)
            sns.heatmap(df, cbar=False, xticklabels=skip_no_labels, yticklabels=skip_no_labels, ax=ax, norm=LogNorm(vmin=self.time_matrix.min()+1, vmax=self.time_matrix.max()))
    

        # Create the animation
        ani = animation.FuncAnimation(fig, update, frames=self.plot_size_axis2, interval=40)
        writervideo = animation.FFMpegWriter(fps=30) 
        # Save the animation to a file or display it
        ani.save('heatmap_animation.mp4', writer=writervideo)

    
    def scatter_plot(self, stop_code, is_energy=False):
        self.read_data(is_energy)

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


    def plot_stop_codes_gradient(self, df_time,df_stop, df_stability, categories, is_stability_only, x_label = r"$\Delta x$", y_label = r"$\Delta y$"):
        # Set up the plot
        fig, ax = plt.subplots()
        fig.set_size_inches(12,8)
        #ax.set_title("Category Heatmap")

        # Ensure all values less than 1 show up as 0
        df_time[df_time < 1] = 1
        
        # Get unique categories
        # categories = sorted(df_stop.stack().unique().tolist())
        category_map = {cat: str(i) for i, cat in enumerate(categories)}
        df_stop = df_stop.replace(category_map).astype(int)

        if is_stability_only:
            categories = ["X"]
            category_map = {"X":1}
        
        #normalise the two numeric matrices
        norm_time = LogNorm(vmin=1, vmax=17000)
        norm_stability = plt.Normalize(vmin=self.stability_matrix.min(), vmax=self.stability_matrix.max())
        
        # Adjusting the colour pallet if we are plotting only the stability points
        if is_stability_only:
            cmap_stability = sns.color_palette("rocket", as_cmap=True)
        else:
            cmap_stability = sns.color_palette(self.stable_color_blend, as_cmap=True)
        sns.heatmap(df_stability, cmap=cmap_stability, norm=norm_stability,fmt="s", cbar=False, cbar_kws={"shrink": 0.5}, ax=ax, square=True, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)

        # If only the stability is being plotted we ensure the non stable points are plotted white
        if is_stability_only:
            not_stable = df_stability[df_stability == 0]
            sns.heatmap(not_stable, cmap="Greys", annot=False, fmt="s", square=True, cbar=False, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)

        for key, cat in enumerate(categories):
            if cat == "X": continue
            
            if cat == "D":
                cmap_time = sns.color_palette("mako", as_cmap=True)
            elif cat == "V":
                cmap_time = sns.color_palette("rocket", as_cmap=True)
            elif cat == "F":
                cmap_time = sns.color_palette("blend:#000,#000", as_cmap=True)
            else:
                cmap_time = sns.color_palette(self.color_map_blends[key], as_cmap=True)
            df_time_mask = df_time.where(df_stop == key)
            heatmap = sns.heatmap(df_time_mask, cmap=cmap_time, norm=norm_time, fmt="s", cbar=False, ax=ax, square=True, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
            
        # Stability colorbar
        # sm_stability = plt.cm.ScalarMappable(cmap=cmap_stability, norm=norm_stability)
        # sm_stability.set_array([])
        # divider = make_axes_locatable(ax)
        # cax_stability = divider.append_axes("right", size="5%", pad=0.25)
        # cbar_stability = plt.colorbar(sm_stability, cax=cax_stability, orientation="vertical")
        # cbar_stability.ax.invert_yaxis()
        # cbar_stability.ax.yaxis.set_ticks_position('right')
        # cbar_stability.ax.yaxis.set_label_position('right')
        # cbar_stability.set_label("Stability Number", labelpad=1, rotation=90)
        # cbar_stability.ax.yaxis.set_label_position('left')

        # #then iterate through all categories other than completion and create a colorbar for each
        # bars_made = 0
        # colors_used = 0
        # for i in range(len(categories)):
        #     if (categories[i] == "X"): continue
        #     if (categories[i] == "F"): continue
        #     #set the colourmap correctly
        #     if colors_used == 0:
        #         cmap_time = sns.color_palette("mako", as_cmap=True)
        #     elif colors_used == 1:
        #         cmap_time = sns.color_palette("rocket", as_cmap=True)
        #     else:
        #         cmap_time = sns.color_palette(self.color_map_blends[colors_used], as_cmap=True)
        #     sm_time = plt.cm.ScalarMappable(cmap=cmap_time, norm=norm_time)
        #     colors_used += 1
        #     sm_time.set_array([])
        #     #create the colorbar and place it in the right space
        #     #if the colorbar is the first, leave space for the stability colorbar ticks
        #     cax_time = divider.append_axes("right", size="5%", pad= 0.75 if (bars_made == 0) else 0.1)
        #     cbar_time = plt.colorbar(sm_time, cax=cax_time, orientation="vertical")
        #     cbar_time.ax.set_title(categories[i], pad=10)
        #     #if the colorbar is the first, we also label it
        #     if (bars_made == 0):
        #         cbar_time.set_label("Time Number", labelpad=1, rotation=90)
        #         cbar_time.ax.yaxis.set_label_position('left')
        #     #if the colorbar is not the last, remove the ticks
        #     if (bars_made < 2): 
        #         cbar_time.set_ticks([])
        #     bars_made += 1

        ax.xaxis.set_tick_params(rotation=90)
        ax.yaxis.set_tick_params(rotation=0)

        ax.set_xlabel(x_label)
        ax.set_ylabel(y_label)

        # Section Used to Regsiter Double Click events and produce associated orbit
        coords = []

        def onclick(event):
            global ix, iy
            if event.inaxes != ax: return
            
            ix, iy = np.floor(event.xdata-self.p_axis1), np.ceil(-event.ydata+self.p_axis1)
            if event.dblclick or (event.button == 3):
                deltaScale = np.ceil(np.abs(np.log10(self.delta_axis1)))
                print (f'delta x = {ix * self.delta_axis1:.{int(deltaScale) + 1}f}, delta y = {iy * self.delta_axis1:.{int(deltaScale) + 1}f}')
                coords.append((ix, iy))
                print(ix, iy)
                command = '.\\JavaCompileAndRun.ps1 figureEight 16000 0.01 --integrator "yoshida" --perturbSingularAngular ' + str(self.slice) + ' ' + str(self.delta_axis2) + ' ' + str(int(ix)) + ' ' + str(int(iy)) + ' ' + str(self.delta_axis1) + ' --calculateEnergies --calculateCentreOfMass --useVariableTimestep --calculateShapeSpace --checkStopConditions'
                completed = subprocess.Popen(["powershell.exe",command], stdout=sys.stdout)
                print(completed.communicate())
                
                plotter = Plotter.Plotter("javasimulation\\Outputs", 
                        run_fast=True,
                        plot_fast=True,
                        close_all=False
                        )

                plotter.shape_space(save=True)
                plotter.plot(save=True)

            return coords
        cid = fig.canvas.mpl_connect('button_press_event', onclick)
        #plt.show()

        return plt


    def  plot_slice(self, slices, dxda = False, dyda = False, is_stability_only=False, save_dbl_click = False, is_energy=False, categories = ["D", "V", "E", "F", "X"]):
        '''
        Takes an array of integers (slices) and by default returns slices in angular momentum of the dxdy plane.
        Opptionally slices can be taken in delta y of the dxda plane, similarly slices can be taken in delta x of 
        the dyda of the plane.
        '''  

        if dxda or dyda:
            self.read_data(is_energy)   
        

        fig, ax = plt.subplots(figsize=(10, 8))

        # Standard dxdy axis labels
        axis_labels = np.round(np.linspace(-self.p_axis1*self.delta_axis1, self.p_axis1*self.delta_axis1, self.plot_size_axis1),2)
        self.skip_no_labels = np.size(axis_labels)//10  # To declutter the axis labeling we only show 10 labels
        
        # For plots with angular momentum as an axis we need to ensure we adjust the axis accordingly
        axis_labels_angular = np.round(np.linspace(-self.p_axis2*self.delta_axis2,0, self.plot_size_axis2),2)

        for slice in slices:
            self.slice = slice
            if dxda:
                df_time = pd.DataFrame(np.flip(self.time_matrix[slice,:,:].T), columns=axis_labels, index=-axis_labels_angular)   
                df_stop = pd.DataFrame(np.flip(self.stop_code_matrix[slice,:,:].T),columns=axis_labels, index=-axis_labels_angular)
                df_stability = pd.DataFrame(np.flip(self.stability_matrix[slice,:,:].T),columns=axis_labels, index=-axis_labels_angular)
                
                plot = self.plot_stop_codes_gradient(df_time, df_stop, df_stability, is_stability_only, categories, x_label=r"$\Delta x$", y_label=r"$\Delta L$")
                plot.savefig("Python/Figures/dxdaSlice" + str(np.round(slice*self.delta_axis1,4)) + ".png", format="png", dpi=300, bbox_inches='tight', pad_inches=0.2)     
            elif dyda:
                df_time = pd.DataFrame(np.flip(self.time_matrix[:,slice,:].T), columns=axis_labels, index=-axis_labels_angular)   
                df_stop = pd.DataFrame(np.flip(self.stop_code_matrix[:,slice,:].T),columns=axis_labels, index=-axis_labels_angular)
                df_stability = pd.DataFrame(np.flip(self.stability_matrix[:,slice,:].T),columns=axis_labels, index=-axis_labels_angular)
                
                plot = self.plot_stop_codes_gradient(df_time, df_stop, df_stability, is_stability_only, categories, x_label=r"$\Delta y$", y_label=r"$\Delta L$")
                plot.savefig("Python/Figures/dydaSlice" + str(np.round(slice*self.delta_axis1,4)) + ".png", format="png", dpi=300, bbox_inches='tight', pad_inches=0.2)     
            else:
                # Read in the data only for that slice, this only works for the dxdy slices
                self.read_data_dxdy_slice()

                df_time = pd.DataFrame(self.time_matrix[:,:], columns=axis_labels, index=-axis_labels)   
                df_stop = pd.DataFrame(self.stop_code_matrix[:,:],columns=axis_labels, index=-axis_labels)
                df_stability = pd.DataFrame(self.stability_matrix[:,:],columns=axis_labels, index=-axis_labels)
                
                plot = self.plot_stop_codes_gradient(df_time, df_stop, df_stability, is_stability_only, categories)
                plot.savefig("Python/Figures/dxdySlice" + str(np.round(slice*self.delta_axis2,4)) + ".png", format="png", dpi=300, bbox_inches='tight', pad_inches=0.2)     
