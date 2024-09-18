import numpy as np

class Body():
    
    def __init__(self, position=np.zeros(3, dtype=float), velocity=np.zeros(3, dtype=float), mass=1, plot_colour='w'):
        self.mass = mass
        self.position = position
        self.velocity = velocity
        self.plot_colour = plot_colour
        
    def kinetic_energies(self):
        '''Calculates the kinetic energy of the body at each timestep and returns the result as a numpy array'''
        kinetic_energy = np.zeros_like(self.velocities)
        for i, velocity in enumerate(self.velocities):
            T = np.dot(velocity, velocity) * self.mass / 2
            kinetic_energy[i] = T
        return kinetic_energy
            