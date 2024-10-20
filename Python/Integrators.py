import numpy as np 
import Simulation
from numpy import linalg as LA
from itertools import combinations 
import copy

class Integrators():
    def __init__(self, bodies, dt **kwargs):
        self.bodies = bodies
        self.dt = dt

        defaultKwargs = {
            "G": 1,
            "is_variable_dt": False
        }
        self.kwargs = defaultKwargs | kwargs

    def get_variable_dt_helper(self, variable_dt_constant):
        """
        Calculates the desired timestep at the current frame
        """
        body_pairs = list(combinations(self.bodies, 2))
        max_relative_velocity = max([LA.norm(body1.velocity - body2.velocity) for body1, body2 in body_pairs])
        min_relative_position = min([LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])

        return variable_dt_constant * (min_relative_position / max_relative_velocity)

    def get_variable_dt(self, variable_dt_constant):
        bodies_copy = copy.deepcopy(self.bodies)
        temp_dt = self.get_variable_dt_helper(variable_dt_constant)  # We find the temporary timestep moving forwards
        temp_bodies = (self.threeStepLeapFrog(bodies_copy, temp_dt))   # We find the temporary state moving forwards

        temp_dt_backwards = self.get_variable_dt_helper(temp_bodies, variable_dt_constant)
        average_dt = (temp_dt+temp_dt_backwards)/2

        return average_dt

        
    def symplecticEuler(self):
        """
        The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
        """
        # Finding the acceleration of each body
        for body in self.bodies:
                body.calculate_acceleration(self.bodies)

        if self.kwargs["is_variable_dt"] is True:
            self.dt = self.get_variable_dt(self.bodies, self.dt)

        for body in self.bodies:
            body.velocity += self.dt * body.acceleration   
            body.position += self.dt * body.velocity 
            
        return self.bodies

                

    def Euler(self):
        """
        The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
        """
        for body in self.bodies:
            body.calculate_acceleration(self.bodies)
        
        for body in self.bodies:
            body.position += self.dt * body.velocity  
            body.velocity += self.dt * body.acceleration
                
        return self.bodies

    def threeStepLeapFrog(self):
        """
        The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
        """
        for body in self.bodies:
            body.calculate_acceleration(self.bodies)

        if self.kwargs["is_variable_dt"] is True:
            self.dt = self.get_variable_dt(self.bodies, self.dt)

        halfVelocity = np.zeros((len(self.bodies), 3), dtype=float)
        for (i, body) in enumerate(self.bodies):
            halfVelocity[i, :] = body.velocity + body.acceleration*self.dt/2

        for (i, body )  in enumerate(self.bodies):
            body.position += halfVelocity[i,:]*self.dt
        
        for body in self.bodies:
            body.calculate_acceleration(self.bodies)

        for (i, body)  in enumerate(self.bodies):
            body.velocity = halfVelocity[i,:] + body.acceleration * self.dt/2

        return self.bodies

    def higherOrderHelpers(self, c, d):
        for body in self.bodies:    
            body.position += c*self.dt*body.velocity

        for body in self.bodies:
            body.calculate_acceleration(self.bodies)

        for body in self.bodies:
            body.velocity += d*self.dt*body.acceleration
        return self.bodies

    def yoshida(self):
        # Initialising constants
        Cs = np.zeros(4)
        Ds = np.zeros(4)
        w0 = -(2**(1/3))/(2-(2**(1/3)))
        w1 = 1/(2-(2**(1/3)))
        Cs[0] = w1/2
        Cs[3] = w1/2
        Cs[1] = (w0+w1)/2
        Cs[2] = (w0+w1)/2
        Ds[0] = w1
        Ds[2] = w1
        Ds[1] = w0

        for i in range(0,4):
            self.bodies = self.higherOrderHelpers(Cs[i], Ds[i], self.bodies, self.dt)

        return self.bodies

    def forestRuth(self):
        #Initialising constants
        x = 1/6 * (2**(1/3) + 2**(-1/3)-1)
        Cs = [x+1/2, -x, -x, x+1/2]
        Ds = [2*x+1, -4*x-1, 2*x+1, 0]

        for i in range(0,4):
            self.bodies = self.higherOrderHelpers(Cs[i], Ds[i], self.bodies, self.dt)

        return self.bodies