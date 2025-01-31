import numpy as np

class Body():
    
    def __init__(self, position=np.zeros(3, dtype=float), velocity=np.zeros(3, dtype=float), mass=1, G=1):
        self.mass = mass
        self.position = position
        self.velocity = velocity
        self.acceleration = np.zeros(3, dtype=float)
        self.G = G
            
    def calculate_acceleration(self, bodies):
        self.acceleration = np.zeros(3, dtype=float)
        for other_body in bodies:
            if other_body != self:
                direction = self.position - other_body.position
                self.acceleration += -self.G * other_body.mass * direction / (np.linalg.norm(direction)**3)
                
            