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
        L_norm = [LA.norm(q) for q in L]

        return np.sum(L, axis=0)

    def calculate_potential_energy(self,bodies, G=1):
        body_pairs = list(combinations(bodies, 2))
        potential_energy = np.sum([-G * body1.mass * body2.mass / LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])
        return potential_energy

    def calculate_kinetic_energy(self, bodies):
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in bodies])
        return kinetic_energy
    

    def do_pertubation(self, i,j,delta):
        current_bodies = copy.deepcopy(self.bodies)
        # ------ This is only going to work for figure 8 ---------------
        current_bodies[0].position = self.bodies[0].position + [i*delta, j*delta,0]

        change_potential_energy = self.calculate_potential_energy(self.bodies) - self.calculate_potential_energy(current_bodies)
        change_in_speed = np.sign(change_potential_energy)*np.sqrt(np.abs(change_potential_energy))
        change_in_angular_momentum = self.calculate_angular_momentum(current_bodies)
        body_1_speed = change_in_speed + LA.norm(current_bodies[1].velocity)
        body_2_speed = change_in_speed + LA.norm(current_bodies[2].velocity)
         
        current_bodies[2].position = self.bodies[2].position + [-i*delta, -j*delta, 0]

        theta = np.arcsin(LA.norm(change_in_angular_momentum)/(LA.norm(current_bodies[1].position)*body_1_speed - LA.norm(current_bodies[2].position)*body_2_speed))

        body_1_position_angle = np.arctan((current_bodies[1].position[1])/(current_bodies[1].position[0]))
        body_2_position_angle = np.arctan((current_bodies[2].position[1])/(current_bodies[2].position[0]))

        current_bodies[1].velocity = np.array([(np.cos(theta + body_1_position_angle))*body_1_speed,(np.sin(theta + body_1_position_angle))*body_1_speed,0.0])
        current_bodies[2].velocity = np.array([(np.cos(theta + body_2_position_angle))*body_2_speed,(np.sin(theta + body_2_position_angle))*body_2_speed,0.0])

        return current_bodies

    def run(self):
        stop_matrix = np.zeros((2*self.p+1, 2*self.p+1), dtype=float)
        bodies = self.bodies

        original_energy = self.calculate_kinetic_energy(bodies) + self.calculate_potential_energy(bodies)
        original_CoM = self.calculate_centre_of_mass(bodies)

        for i in range(-self.p, self.p+1):
            for j in range(-self.p, self.p+1):
                current_bodies = self.do_pertubation(i,j, self.delta)
                current_energy = self.calculate_kinetic_energy(current_bodies) + self.calculate_potential_energy(current_bodies)
                current_CoM = self.calculate_centre_of_mass(current_bodies)

                #assert np.isclose(original_energy, current_energy)
                print(original_energy - current_energy)
                assert np.allclose(original_CoM, current_CoM)

                potential_energy = np.zeros((self.N), dtype=float)
                kinetic_energy = np.zeros((self.N), dtype=float)

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

