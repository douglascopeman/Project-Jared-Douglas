import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd
from matplotlib.colors import ListedColormap
from mpl_toolkits.axes_grid1 import make_axes_locatable
from multipledispatch import dispatch


class PerturbationPlot():

    def __init__(self, output_directory):
        self.output_directory = os.path.join(os.getcwd(), output_directory)
        self.settingsFilepath = os.path.join(self.output_directory, "perturbationSettings.csv")
        
        with open(self.settingsFilepath, 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.N = int(data[0])
            self.delta = float(data[1])
            self.p = int(data[2])
            self.plot_size = 2*self.p + 1
            
        self.axis_labels = np.round(np.arange((-self.p*self.delta), (self.p*self.delta+(self.delta*0.5)), self.delta), decimals=4)
        self.skip_no_labels = np.size(self.axis_labels)//10
        self.color_map = ["#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#00FFFF"]
        self.color_map_words = ["Reds", "Greens", "Blues", "Yellows", "Cyan"]
        
    def read_time(self, filename):
        with open(os.path.join(self.output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=float)
            self.time_matrix = np.zeros((self.plot_size, self.plot_size), dtype=float)
            self.time_matrix[:,:] = data

            assert np.shape(self.time_matrix)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.time_matrix)[0] == np.shape(self.time_matrix)[1], "Matrix is not square"
                
    def plot_time(self, filename):
        self.read_time(filename)

        self.df = pd.DataFrame(self.time_matrix, columns=-self.axis_labels, index=self.axis_labels)

        sns.heatmap(self.df.T, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        plt.title("Perturbation Plot")
        plt.xlabel(r"$\Delta x$")
        plt.ylabel(r"$\Delta y$")
        plt.show()

    def read_stop_codes(self, filename):
        with open(os.path.join(self.output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=str)
            self.stop_code_matrix = np.zeros((self.plot_size, self.plot_size), dtype=str)
            self.stop_code_matrix[:,:] = data

            assert np.shape(self.stop_code_matrix)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.stop_code_matrix)[0] == np.shape(self.stop_code_matrix)[1], "Matrix is not square"

    def plot_stop_codes(self, filename):
        self.read_stop_codes(filename)
        
        # Create a dataframe from the matrix
        df = pd.DataFrame(self.stop_code_matrix.T, columns=self.axis_labels, index=-self.axis_labels)

        # Get unique categories
        categories = df.stack().unique().tolist()
        category_map = {cat: str(i) for i, cat in enumerate(categories)}
        df = df.replace(category_map).astype(int)
        if len(self.color_map) < len(categories):
            raise ValueError("Not enough colours in custom colour map")
        cmap = ListedColormap(self.color_map[:len(categories)])

        
        # Plot the heatmap
        plt.figure(figsize=(10, 8))
        sns.heatmap(df, cmap=cmap, annot=False, fmt="s", square=True, cbar=False, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        
        patches = [plt.plot([], [], marker="s", ms=10, ls="", c=cmap.colors[i])[0] for i in range(len(categories))]
        plt.legend(patches, categories, loc="upper left", title="Categories", ncol=3, prop={'size': 8})
        
        plt.title("Category Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        plt.show()
        
    def read_stability(self, filename):
        with open(os.path.join(self.output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=int)
            self.stability_matrix = np.zeros((self.plot_size, self.plot_size), dtype=int)
            self.stability_matrix[:,:] = data
            
            assert np.shape(self.stability_matrix)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.stability_matrix)[0] == np.shape(self.stability_matrix)[1], "Matrix is not square"
            
    def plot_stability(self, filename):
        self.read_stability(filename)
        
        fig, ax = plt.subplots()
        
        df = pd.DataFrame(self.stability_matrix.T, columns=self.axis_labels, index=-self.axis_labels)
        
        sns.heatmap(df, cmap="Greys", annot=False, fmt="s", square=True, cbar=False, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        
        norm = plt.Normalize(vmin=self.stability_matrix.min(), vmax=self.stability_matrix.max())
        sm = plt.cm.ScalarMappable(cmap="Greys", norm=norm)
        sm.set_array([])
        cbar = plt.colorbar(sm, ax=ax, orientation="vertical")
        cbar.set_label("Stability Number", labelpad=1, rotation=90)
        cbar.ax.yaxis.set_label_position('left')
        
        plt.title("Stability Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        plt.show()
        

    @dispatch(str, str)
    def plot_stop_codes_gradient(self, time_filename, stop_filename):
        self.read_time(time_filename)
        self.read_stop_codes(stop_filename)

        # Set up the plot
        fig, ax = plt.subplots()
        fig.set_size_inches(8, 6)
        
        # Create a dataframe from the matrix
        df_stop = pd.DataFrame(self.stop_code_matrix.T, columns=self.axis_labels, index=-self.axis_labels)
        df_time = pd.DataFrame(self.time_matrix.T, columns=self.axis_labels, index=-self.axis_labels)

        # Get unique categories
        categories = df_stop.stack().unique().tolist()
        category_map = {cat: str(i) for i, cat in enumerate(categories)}
        df_stop = df_stop.replace(category_map).astype(int)
        if len(self.color_map) < len(categories):
            raise ValueError("Not enough colours in custom colour map")
        cmap = ListedColormap(self.color_map[:len(categories)])
        
        # Plot the heatmap
        plt.title("Category Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        
        sns.heatmap(df_stop, cmap=cmap, annot=False, fmt="s", square=True, cbar=False, ax=ax, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        sns.heatmap(df_time, cmap="Greys", alpha=0.5,fmt="s", cbar=False, ax=ax, square=True, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        
        divider = make_axes_locatable(ax)
        # Time colorbars
        norm = plt.Normalize(vmin=self.time_matrix.min(), vmax=self.time_matrix.max()) #find the range of values
        #then iterate through all categories other than completion and create a colorbar for each
        bars_made = 0
        for i in range(len(categories)):
            if (categories[i] == "X"): continue
            #set the colourmap correctly
            sm = plt.cm.ScalarMappable(cmap=self.color_map_words[i], norm=norm)
            sm.set_array([])
            #create the colorbar and place it in the right space
            #if the colorbar is the first, leave space for the stability colorbar ticks
            cax = divider.append_axes("right", size="5%", pad= 0.5 if (bars_made == 0) else 0.1)
            cbar = plt.colorbar(sm, cax=cax, orientation="vertical")
            cbar.ax.set_title(categories[i], pad=10)
            #if the colorbar is the first, we also label it
            if (bars_made == 0):
                cbar.set_label("Time Number", labelpad=1, rotation=90)
                cbar.ax.yaxis.set_label_position('left')
                is_label_set = True
            #if the colorbar is not the last, remove the ticks
            if (bars_made != len(categories) - 2): 
                cbar.set_ticks([])
            bars_made += 1
        
        plt.show()

    @dispatch(str, str, str)
    def plot_stop_codes_gradient(self, time_filename, stop_filename, stability_filename):
        self.read_time(time_filename)
        self.read_stop_codes(stop_filename)
        self.read_stability(stability_filename)

        # Set up the plot
        fig, ax = plt.subplots()
        fig.set_size_inches(8, 6)
        
        # Create a dataframe from the matrix
        df_stop = pd.DataFrame(self.stop_code_matrix.T, columns=self.axis_labels, index=-self.axis_labels)
        df_time = pd.DataFrame(self.time_matrix.T, columns=self.axis_labels, index=-self.axis_labels)
        df_stability = pd.DataFrame(self.stability_matrix.T, columns=self.axis_labels, index=-self.axis_labels)

        # Get unique categories
        categories = df_stop.stack().unique().tolist()
        category_map = {cat: str(i) for i, cat in enumerate(categories)}
        df_stop = df_stop.replace(category_map).astype(int)
        if len(self.color_map) < len(categories):
            raise ValueError("Not enough colours in custom colour map")
        cmap = ListedColormap(self.color_map[:len(categories)])
        
        # Plot the heatmap
        plt.title("Category Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        
        sns.heatmap(df_stop, cmap=cmap, annot=False, fmt="s", square=True, cbar=False, ax=ax, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        sns.heatmap(df_time, cmap="Greys", alpha=0.5,fmt="s", cbar=False, ax=ax, square=True, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        sns.heatmap(df_stability, cmap="Greys", alpha=0.5,fmt="s", cbar=False, ax=ax, square=True, xticklabels=self.skip_no_labels, yticklabels=self.skip_no_labels)
        
        # Stability colorbar
        norm = plt.Normalize(vmin=self.stability_matrix.min(), vmax=self.stability_matrix.max())
        sm = plt.cm.ScalarMappable(cmap="Greys", norm=norm)
        sm.set_array([])
        divider = make_axes_locatable(ax)
        cax = divider.append_axes("right", size="5%", pad=0.25)
        cbar = plt.colorbar(sm, cax=cax, orientation="vertical")
        cbar.set_label("Stability Number", labelpad=1, rotation=90)
        cbar.ax.yaxis.set_label_position('left')

        # Time colorbars
        norm = plt.Normalize(vmin=self.time_matrix.min(), vmax=self.time_matrix.max()) #find the range of values
        #then iterate through all categories other than completion and create a colorbar for each
        bars_made = 0
        for i in range(len(categories)):
            if (categories[i] == "X"): continue
            #set the colourmap correctly
            sm = plt.cm.ScalarMappable(cmap=self.color_map_words[i], norm=norm)
            sm.set_array([])
            #create the colorbar and place it in the right space
            #if the colorbar is the first, leave space for the stability colorbar ticks
            cax = divider.append_axes("right", size="5%", pad= 0.75 if (bars_made == 0) else 0.1)
            cbar = plt.colorbar(sm, cax=cax, orientation="vertical")
            cbar.ax.set_title(categories[i], pad=10)
            #if the colorbar is the first, we also label it
            if (bars_made == 0):
                cbar.set_label("Time Number", labelpad=1, rotation=90)
                cbar.ax.yaxis.set_label_position('left')
                is_label_set = True
            #if the colorbar is not the last, remove the ticks
            if (bars_made != len(categories) - 2): 
                cbar.set_ticks([])
            bars_made += 1
        
        plt.show()

    def count_stop_matrix(self, stop_filename):
        self.read_stop_codes(stop_filename)
        print("[X, V, D, E, C]")
        count = [np.count_nonzero(self.stop_code_matrix == 'X'),
                 np.count_nonzero(self.stop_code_matrix == 'V'),
                 np.count_nonzero(self.stop_code_matrix == 'D'),
                 np.count_nonzero(self.stop_code_matrix == 'E'),
                 np.count_nonzero(self.stop_code_matrix == 'C')
                 ]
        print(count)
        
    
        