import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd
from matplotlib.colors import ListedColormap


class PerturbationPlot():

    def __init__(self, output_directory):
        self.output_directory = output_directory

    def plot(self, filename):
        self.read_data(filename)

        self.axis_labels = np.round(np.arange((-self.p*self.delta), (self.p*self.delta+(self.delta*0.5)), self.delta), decimals=4)
        self.df = pd.DataFrame(self.M, columns=-self.axis_labels, index=self.axis_labels)

        skip_no_labels = np.size(self.axis_labels)//10
        print(skip_no_labels)


        sns.heatmap(self.df.T, xticklabels=skip_no_labels, yticklabels=skip_no_labels)
        plt.title("Perturbation Plot")
        plt.xlabel(r"$\Delta x$")
        plt.ylabel(r"$\Delta y$")
        plt.show()


    def read_data(self, filename):
        '''
        Take the relevent csv files from the chosen output directory and read it in
        '''
        output_directory = os.path.join(os.getcwd(), self.output_directory)

        # Reading the Perturbation settings, csv file must contain 6 lines of numerical values
        with open(os.path.join(output_directory, "perturbationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.N = int(data[0])
            self.dt = float(data[1])
            self.n = int(data[2])
            self.delta = float(data[3])
            self.p = int(data[4])
            self.plot_size = 2*self.p + 1

        # Reading the perturbation matrix
        with open(os.path.join(output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.M = np.zeros((self.plot_size, self.plot_size), dtype=float)
            self.M[:,:] = data

            assert np.shape(self.M)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.M)[0] == np.shape(self.M)[1], "Matrix is not square"

    def plot_stop_codes(self, filename):
        self.read_stop_codes(filename)
        
        # Create axis labels
        self.axis_labels = np.round(np.arange((-self.p*self.delta), (self.p*self.delta+(self.delta*0.5)), self.delta), decimals=4)
        
        # Create a dataframe from the matrix
        df = pd.DataFrame(self.stopCodeMatrix.T, columns=self.axis_labels, index=-self.axis_labels)

        # Get unique categories
        categories = df.stack().unique().tolist()

        # Map categories to numeric values
        category_map = {cat: i for i, cat in enumerate(categories)}
        df = df.replace(category_map)
        
        cmap = ListedColormap(sns.color_palette("hls", len(categories)).as_hex())
        
        # fix label ticks
        
        skip_no_labels = np.size(self.axis_labels)//10
        
        # Plot the heatmap
        plt.figure(figsize=(10, 8))
        sns.heatmap(df, cmap=cmap, annot=False, fmt="s", square=True, cbar=False, xticklabels=skip_no_labels, yticklabels=skip_no_labels)
        
        patches = [plt.plot([], [], marker="s", ms=10, ls="", c=cmap.colors[i])[0] for i in range(len(categories))]
        plt.legend(patches, categories, loc="upper left", title="Categories", ncol=3, prop={'size': 8})
        
        plt.title("Category Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        plt.show()
        
        
    def read_stop_codes(self, filename):
        '''
        Take the relevant csv files from the chosen output directory and read it in
        '''
        output_directory = os.path.join(os.getcwd(), self.output_directory)

        # Reading the Perturbation settings, csv file must contain 6 lines of numerical values
        with open(os.path.join(output_directory, "perturbationSettings.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.N = int(data[0])
            self.dt = float(data[1])
            self.n = int(data[2])
            self.delta = float(data[3])
            self.p = int(data[4])
            self.plot_size = 2*self.p + 1

        # Reading the perturbation matrix
        with open(os.path.join(output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",", dtype=str)
            self.stopCodeMatrix = np.zeros((self.plot_size, self.plot_size), dtype=str)
            self.stopCodeMatrix[:,:] = data

            assert np.shape(self.stopCodeMatrix)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.stopCodeMatrix)[0] == np.shape(self.stopCodeMatrix)[1], "Matrix is not square"

    def plot_stop_codes_gradient(self, time_filename, stop_filename):
        self.read_data(time_filename)
        self.read_stop_codes(stop_filename)

        # Set up the plot
        fig, ax = plt.subplots()

        # Create axis labels
        self.axis_labels = np.round(np.arange((-self.p*self.delta), (self.p*self.delta+(self.delta*0.5)), self.delta), decimals=4)
        
        # Create a dataframe from the matrix
        df_stop = pd.DataFrame(self.stopCodeMatrix.T, columns=self.axis_labels, index=-self.axis_labels)
        df_time = pd.DataFrame(self.M.T, columns=self.axis_labels, index=-self.axis_labels)

        # Get unique categories
        categories = df_stop.stack().unique().tolist()

        # Map categories to numeric values
        category_map = {cat: i for i, cat in enumerate(categories)}
        df_stop = df_stop.replace(category_map)
        
        cmap = ListedColormap(sns.color_palette("hls", len(categories)).as_hex())
        
        # fix label ticks
        
        skip_no_labels = np.size(self.axis_labels)//10
        
        # Plot the heatmap
        sns.heatmap(df_stop, cmap=cmap, annot=False, fmt="s", square=True, cbar=False, ax=ax, xticklabels=skip_no_labels, yticklabels=skip_no_labels)
        sns.heatmap(df_time, cmap="Greys", alpha=0.5,fmt="s", cbar=True, ax=ax, square=True, xticklabels=skip_no_labels, yticklabels=skip_no_labels)
        
        patches = [plt.plot([], [], marker="s", ms=10, ls="", c=cmap.colors[i])[0] for i in range(len(categories))]
        plt.legend(patches, categories,bbox_to_anchor=(1.5,0.5), loc="right", title="Categories", ncol=1, prop={'size': 8})
        
        plt.title("Category Heatmap")
        plt.xlabel("Column")
        plt.ylabel("Row")
        plt.show()

    def count_stop_matrix(self, stop_filename):
        self.read_stop_codes(stop_filename)
        print("[X, V, D, E, C]")
        count = [np.count_nonzero(self.stopCodeMatrix == 'X'),
                 np.count_nonzero(self.stopCodeMatrix == 'V'),
                 np.count_nonzero(self.stopCodeMatrix == 'D'),
                 np.count_nonzero(self.stopCodeMatrix == 'E'),
                 np.count_nonzero(self.stopCodeMatrix == 'C')
                 ]
        print(count)