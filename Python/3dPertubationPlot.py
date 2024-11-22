import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body
import seaborn as sns
import pandas as pd
from matplotlib.colors import ListedColormap


class ThreeDimensionalPerturbationPlot():

    def __init__(self, output_directory, T):
        self.output_directory = output_directory
        self.T = T

    def read_data(self, filename):

        # Reading the perturbation matrix
        with open(os.path.join(self.output_directory, filename + ".csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.M = np.zeros((self.plot_size, self.plot_size), dtype=float)
            self.M[:,:] = data
