import numpy as np 
import Simulation
from numpy import linalg as LA
from itertools import combinations 
import copy

def get_variable_dt_helper(bodies, variable_dt_constant):
    """
    Calculates the desired timestep at the current frame
    """
    body_pairs = list(combinations(bodies, 2))
    max_relative_velocity = max([LA.norm(body1.velocity - body2.velocity) for body1, body2 in body_pairs])
    min_relative_position = min([LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])

    return variable_dt_constant * (min_relative_position / max_relative_velocity)

def get_variable_dt(bodies, variable_dt_constant):
    bodies_copy = copy.deepcopy(bodies)
    temp_dt = get_variable_dt_helper(bodies, variable_dt_constant)  # We find the temporary timestep moving forwards
    temp_bodies = (symplecticEuler(bodies_copy, temp_dt))   # We find the temporary state moving forwards

    temp_dt_backwards = get_variable_dt_helper(temp_bodies, variable_dt_constant)
    average_dt = (temp_dt+temp_dt_backwards)/2
    # print(average_dt)
    # print(temp_dt - temp_dt_backwards)
    # print("\n")
    return average_dt
    # bodies_copy_2 = copy.deepcopy(bodies)
    # temp_bodies2 = (symplecticEuler(bodies_copy_2, average_dt))
    # temp_dt_2 = get_variable_dt_helper(temp_bodies2, variable_dt_constant)
    # return (temp_dt_2+average_dt)/2

    
def symplecticEuler(bodies, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    # Finding the acceleration of each body
    for body in bodies:
            body.calculate_acceleration(bodies)

    if variable_dt_constant is not None:
        dt = get_variable_dt(bodies, variable_dt_constant)

    for body in bodies:
        body.velocity += dt * body.acceleration   
        body.position += dt * body.velocity 
        
    return bodies

def symplecticEulerHalfSteps(bodies, dt, G=1, variable_dt_constant=None):
    for body in bodies:
        body.calculate_acceleration(bodies)
        
    if variable_dt_constant is not None:
        for body in bodies:
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.velocity += dt/2 * body.acceleration
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.position += dt * body.velocity
        for body in bodies:
            body.calculate_acceleration(bodies)
        for body in bodies:
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.velocity += dt/2 * body.acceleration
    else:
        body.velocity += dt * body.acceleration
        body.position += dt * body.velocity
        
    return bodies
            

def Euler(bodies, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for body in bodies:
        body.calculate_acceleration(bodies)
    
    for body in bodies:
        body.position += dt * body.velocity  
        body.velocity += dt * body.acceleration
            
    return bodies

def threeStepLeapFrog(bodies, dt, G, variable_dt_constant=None):
    """
    The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
    """
    for body in bodies:
        body.calculate_acceleration(bodies)

    halfVelocity = np.zeros((len(bodies), 3), dtype=float)
    for (i, body) in enumerate(bodies):
        halfVelocity[i, :] = body.velocity + body.acceleration*dt/2

    for (i, body )  in enumerate(bodies):
        body.position += halfVelocity[i,:]*dt
    
    # acceleration = calculateAccelerations(bodies, G)
    for body in bodies:
        body.calculate_acceleration(bodies)

    for (i, body)  in enumerate(bodies):
        body.velocity = halfVelocity[i,:] + body.acceleration * dt/2

    return bodies

def higherOrderHelpers(c, d, bodies, dt):
    for body in bodies:    
        body.position += c*dt*body.velocity

    for body in bodies:
        body.calculate_acceleration(bodies)

    for body in bodies:
        body.velocity += d*dt*body.acceleration
    return bodies

def yoshida(bodies, dt, G, variable_dt_constatn = None):
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
        bodies = higherOrderHelpers(Cs[i], Ds[i], bodies, dt)

    return bodies