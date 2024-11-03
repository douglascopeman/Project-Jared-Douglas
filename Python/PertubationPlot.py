import matplotlib.pyplot as plt
from matplotlib import animation
from itertools import combinations
import numpy as np
import os
import Body


class PertubationPlot():

    def __init__(self, output_directory):
        self.output_directory = output_directory

    def plot(self):
        self.read_data()

    def read_data(self):
        '''
        Take the relevent csv files from the chosen output directory and read it in
        '''
        output_directory = os.path.join(os.getcwd(), self.outputDirectory)
        with open(os.path.join(output_directory, "pertubationMatrix.csv"), 'r') as f:
            data = np.loadtxt(f, delimiter=",")
            self.M = int(data[0])
            self.dt = data[1]
            self.n = int(data[2])
            self.G = float(data[3])
            if self.kwargs["is_orbit_duration"]:
                self.orbit_duration = int(data[4])
            else:
                self.orbit_duration = 0.0
