import matplotlib.pyplot as plt
import numpy as np
import Body

class Plotter():
    def __init__(self, filename, **plot_kwargs):
        self.filename = filename
        defaultKwargs = {}
        self.plot_kwargs = defaultKwargs | plot_kwargs
        
    def read_data(self):
        self.bodies = []
        with open(self.filename, 'r') as f:
            self.timesteps = []
            self.bodies.append(Body())
            
        
        
    def calculate_total_energy(self):
        T = sum(body.kinetic_energies() for body in self.bodies)
        
        twice_U = 0.0
        for body in self.bodies:
            for other_body in self.bodies:
                if body is not other_body:
                    pass #TODO: Implement calculation to determine direction between bodies and then calculate the doubled U