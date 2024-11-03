import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd


class PerturbationPlot():

    def __init__(self, output_directory):
        self.output_directory = output_directory

    def plot(self):
        self.read_data()

        self.axis_labels = np.round(np.arange((-self.p*self.delta), (self.p*self.delta+self.delta), self.delta), decimals=4)
        self.df = pd.DataFrame(self.M, columns=self.axis_labels, index=self.axis_labels)

        skip_no_labels = np.size(self.axis_labels)//10
        print(skip_no_labels)


        sns.heatmap(self.df, xticklabels=skip_no_labels, yticklabels=skip_no_labels)
        plt.title("Perturbation Plot")
        plt.xlabel("Delta x")
        plt.ylabel("Delta y")
        plt.show()


    def read_data(self):
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
        with open(os.path.join(output_directory, "perturbationMatrix.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.M = np.zeros((self.plot_size, self.plot_size), dtype=float)
            self.M[:,:] = data

            assert np.shape(self.M)[0] % 2 != 0, "Matrix is not of odd dimension"
            assert np.shape(self.M)[0] == np.shape(self.M)[1], "Matrix is not square"
