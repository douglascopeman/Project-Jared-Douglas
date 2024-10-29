import numpy as np
from numpy import linalg as LA
from itertools import combinations
from Body import Body
import Integrators
from Plotter import Plotter
import copy
import os

class Pertubation():

    def __init__(self, N, dt, bodies, p, delta, stop_conditions):
        self.bodies = bodies
        self.n = len(bodies)
        self.N = N
        self.dt = dt
        self.p = p
        self.delta = delta
        self.stop_conditions = stop_conditions

    
    def calculate_centre_of_mass(self, bodies):
        total_mass = np.sum([body.mass for body in bodies])
        summation = np.sum([body.mass * body.position for body in bodies], axis=0) 
        position = (1/total_mass) * summation
        return position
    
    def calculate_angular_momentum(self, bodies):
        L = [body.mass * np.cross(body.position, body.velocity) for body in bodies]
        return LA.norm(np.sum(L, axis=0))

    def calculate_potential_energy(self,bodies, G=1):
        body_pairs = list(combinations(bodies, 2))
        potential_energy = np.sum([-G * body1.mass * body2.mass / LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])
        return potential_energy

    def calculate_kinetic_energy(self, bodies):
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in bodies])
        return kinetic_energy
    

    def do_pertubation(self, i,j,delta, original_energy):
        '''
        Performs the pertubation to the right most body of the figure 8, this consists of a shift in the x and y direction.
        The Center of Mass position and velocity, the energy and the angular momentum is preserved by adjusting the position of
        the other two bodies and the velocity of all three.
        '''
        current_bodies = copy.deepcopy(self.bodies)

        # Perform the pertubation of body 0 and then adjust body 2 accordingly to preserve CoM positon
        current_bodies[0].position = self.bodies[0].position + [i*delta, j*delta,0]
        current_bodies[2].position = -current_bodies[0].position

        # Find the new magnatude of velocity required to preserve energy
        speed = np.sqrt((1/3)*(original_energy + 5/(2 * LA.norm(current_bodies[0].position))))

        # To preserve angular momentum the velocity of body 1 is x(-2) that of bodies 0 and 1
        current_bodies[0].velocity = (self.bodies[0].velocity / LA.norm(self.bodies[0].velocity))*speed
        current_bodies[2].velocity = (self.bodies[2].velocity / LA.norm(self.bodies[2].velocity))*speed
        current_bodies[1].velocity = (self.bodies[1].velocity / LA.norm(self.bodies[1].velocity))*(-2)*speed

        return current_bodies

    def run(self):
        # The matrix to be populated with the distance from completion of the simulation (0 means it reached the end)
        stop_matrix = np.zeros((2*self.p+1, 2*self.p+1), dtype=float)   # Always odd number of columns / rows
        bodies = self.bodies

        # Initialising the origianl properties of the simulation to be used for checking in the loops
        original_energy = self.calculate_kinetic_energy(bodies) + self.calculate_potential_energy(bodies)
        original_CoM = self.calculate_centre_of_mass(bodies)
        original_angular_momentum = self.calculate_angular_momentum(bodies)

        # Loop through all the pertubations required
        for i in range(-self.p, self.p+1):
            for j in range(-self.p, self.p+1):
                # We perform the pertubation on all but the original i=0, j=0 case
                if (i != 0 or j != 0):
                    current_bodies = self.do_pertubation(i,j, self.delta, original_energy)
                else:
                    current_bodies = copy.deepcopy(self.bodies)
                
                # Calculating the new perturbed properties of the simulaiton
                current_energy = self.calculate_kinetic_energy(current_bodies) + self.calculate_potential_energy(current_bodies)
                current_CoM = self.calculate_centre_of_mass(current_bodies)
                current_angular_momentum = self.calculate_angular_momentum(current_bodies)

                # An assertion error is thrown if the constants are not "equal" to the original simulaiton
                assert np.isclose(original_energy, current_energy)
                assert np.allclose(current_angular_momentum, original_angular_momentum)
                assert np.isclose(np.sum([body.velocity for body in bodies]),0)
                assert np.allclose(original_CoM, current_CoM)

                potential_energy = np.zeros((self.N), dtype=float)
                kinetic_energy = np.zeros((self.N), dtype=float)

                # The loop for the simulation 
                for k in range(0,self.N):
                    potential_energy[k] = self.calculate_potential_energy(current_bodies)
                    kinetic_energy[k] = self.calculate_kinetic_energy(current_bodies)

                    # Checking if stop conditions are met
                    if k%10 == 1:
                        body_pairs = list(combinations(current_bodies, 2))
                        energy_error = (np.abs((kinetic_energy[k]-kinetic_energy[0]+potential_energy[k]-potential_energy[0])/(potential_energy[0]+kinetic_energy[0])))
                        max_relative_position = max([LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])

                        if self.stop_conditions['energy_error_bound'] < energy_error:
                            print("Simulation Terminated due to energy error bound exceded")
                            print("Energy errror is: ", energy_error)
                            print("Timestep reached: ", k, "\n")
                            stop_matrix[i+self.p, j+self.p] = self.N - k
                            break
                        if self.stop_conditions['variable_dt_bound'] > used_dt:
                            print("Simulation Terminated due to variable timestep bound exceded")
                            print("Variable Timestep is: ", used_dt)
                            print("Timestep reached: ", k, "\n")
                            stop_matrix[i+self.p, j+self.p] = self.N - k
                            break
                        if self.stop_conditions['distance_bound'] < max_relative_position:
                            print("Simulation Terminated due to distance bound exceded")
                            print("Max realtive distance between bodies is: ", max_relative_position)
                            print("Timestep reached: ", k, "\n")
                            stop_matrix[i+self.p, j+self.p] = self.N - k
                            break
                    stop_matrix[i+self.p, j+self.p] = self.N - k -1

                    current_bodies, used_dt = Integrators.yoshida(current_bodies, self.dt)


        return stop_matrix

