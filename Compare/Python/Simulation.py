import numpy as np
from numpy import linalg as LA
from itertools import combinations
from Body import Body
import Integrators
import os

class Simulation():
    
    def __init__(self, N, dt, bodies, **kwargs):
        self.bodies = bodies
        self.n = len(bodies)
        self.N = N
        self.dt = dt
        self.focus_body = None
        
        default_kwargs = {
                        "Integrator": Integrators.symplectic_euler,
                        "G":1,
                        "is_variable_dt":False,
                        "is_focus_on_body": False,
                        "stop_conditions": None,
                        "is_orbit_duration":False
                        }
        self.kwargs = default_kwargs | kwargs

    ###################################################
    # Simulation Calculations
    ###################################################

    def calculate_linear_momentum(self):
        p = [body.mass*body.velocity for body in self.bodies]
        p_norm = [LA.norm(q) for q in p]

        if self.kwargs["is_focus_on_body"]:
            if self.focus_body is not None:
                return p[self.focus_body]
            else:
                self.focus_body = p_norm.index(np.max(p_norm))
                return p[self.focus_body]
        else:
            return np.sum(p, axis=0)

    def calculate_angular_momentum(self):
        L = [body.mass * np.cross(body.position, body.velocity) for body in self.bodies]
        L_norm = [LA.norm(q) for q in L]
        if self.kwargs["is_focus_on_body"]:
            if self.focus_body is not None:
                return L[self.focus_body]
            else:
                self.focus_body = L_norm.index(np.max(L_norm))
                return L[self.focus_body]
        else:
            return np.sum(L, axis=0)

    def calculate_centre_of_mass(self, totalMass):
        summation = np.sum([body.mass * body.position for body in self.bodies], axis=0) 
        position = (1/totalMass) * summation
        return position
    
    def calculate_potential_energy(self):
        G = self.kwargs["G"]
        body_pairs = list(combinations(self.bodies, 2))
        potential_energy = np.sum([-G * body1.mass * body2.mass / LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])
        return potential_energy

    def calculate_kinetic_energy(self):
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in self.bodies])
        return kinetic_energy
    
    ###################################################
    # Run Model
    ###################################################

    def run(self):
        #-------------------- Initialise variables --------------------#
        stop_conditions = self.kwargs["stop_conditions"]
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        total_mass = np.sum([body.mass for body in self.bodies])
        centre_of_mass = np.zeros((self.N, 3), dtype=float)
        potential_energy = np.zeros((self.N), dtype=float)
        kinetic_energy = np.zeros((self.N), dtype=float)
        angular_momentum = np.zeros((self.N, 3), dtype=float)
        linear_momentum = np.zeros((self.N,3), dtype=float)
        G = self.kwargs["G"]
        is_variable_dt = self.kwargs["is_variable_dt"]
        initial_position = np.concatenate([np.copy(body.position) for body in self.bodies])
        orbit_duration = 0
        
        # Holding Initial Values for Error
        initial_potential_energy = self.calculate_potential_energy()
        initial_kinetic_energy = self.calculate_kinetic_energy()
        initial_angular_momentum = self.calculate_angular_momentum()
        initial_linear_momentum = self.calculate_linear_momentum()
        
        #-------------------- Main Time Loop --------------------#
        for i in range(0, self.N):
            centre_of_mass[i,:] = self.calculate_centre_of_mass(total_mass)
            potential_energy[i] = self.calculate_potential_energy()
            kinetic_energy[i] = self.calculate_kinetic_energy()
            angular_momentum[i,:] = self.calculate_angular_momentum()
            linear_momentum[i,:] = self.calculate_linear_momentum()
            for p in range(0,self.n):
                simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)

            # Checking if stop conditions are met
            if stop_conditions is not None:
                if i%10 == 1:
                    body_pairs = list(combinations(bodies, 2))
                    energy_error = (np.abs((kinetic_energy[i]-initial_kinetic_energy+potential_energy[i]-initial_potential_energy)/(initial_potential_energy+initial_kinetic_energy)))
                    max_relative_position = max([LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])

                    if stop_conditions['energy_error_bound'] < energy_error:
                        print("Simulation Terminated due to energy error bound exceded")
                        print("Energy errror is: ", energy_error)
                        print("Timestep reached: ", i, "\n")
                        break
                    if stop_conditions['variable_dt_bound'] > used_dt:
                        print("Simulation Terminated due to variable timestep bound exceded")
                        print("Variable Timestep is: ", used_dt)
                        print("Timestep reached: ", i, "\n")
                        break
                    if stop_conditions['distance_bound'] < max_relative_position:
                        print("Simulation Terminated due to distance bound exceded")
                        print("Max realtive distance between bodies is: ", max_relative_position)
                        print("Timestep reached: ", i, "\n")
                        break
            
            # Update position of all bodies
            bodies, used_dt = self.kwargs["Integrator"](bodies, self.dt, G, is_variable_dt)
            
            if self.kwargs["is_orbit_duration"]:
                current_positions = np.concatenate([body.position for body in self.bodies])
                orbit_error_bound = 0.08    # The maximum distance between bodies and their initial position that determins if it's an "orbit"
                if orbit_duration == 0 and LA.norm(current_positions-initial_position) < orbit_error_bound and i>10:
                    orbit_duration = i  # Set the orbit if one is not already set

        #-------------------- Write Data to CSVs --------------------#
        simulationSettings = np.array([self.N, self.dt, self.n, self.kwargs["G"], orbit_duration])
        path = os.path.join(os.getcwd(), "Python\\Outputs")
        np.savetxt(os.path.join(path, "simulationSettings.csv"), simulationSettings, delimiter=",")
        np.savetxt(os.path.join(path, "centreOfMass.csv"), centre_of_mass, delimiter=",")
        np.savetxt(os.path.join(path, "potentialEnergy.csv"), potential_energy, delimiter=",")
        np.savetxt(os.path.join(path, "kineticEnergy.csv"), kinetic_energy, delimiter=",")
        np.savetxt(os.path.join(path, "angularMomentum.csv"), angular_momentum, delimiter=",")
        np.savetxt(os.path.join(path, "linearMomentum.csv"), linear_momentum, delimiter=',')
        for i in range(self.n):
            np.savetxt(os.path.join(path, ("output"+ str(i)+ ".csv")), simulation[:,:,i], delimiter=",")

        print(orbit_duration)


    def run_fast(self):
        """
        A bare bones version of run(), only calculates body positions
        """
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        orbit_duration = 0.0
        for i in range(0, self.N):
            bodies, used_dt = self.kwargs["Integrator"](bodies, self.dt)
            for p, body in enumerate(bodies):
                simulation[i,:,p] = np.concatenate((body.position, body.velocity), axis=None)

        path = os.path.join(os.getcwd(), "Python\\Outputs")
        simulationSettings = np.array([self.N, self.dt, self.n, self.kwargs["G"], orbit_duration])
        np.savetxt(os.path.join(path, "simulationSettings.csv"), simulationSettings, delimiter=",")
        for p in range(self.n):
            np.savetxt(os.path.join(path, "output" + str(p) + ".csv"), simulation[:,:,p], delimiter=",")
