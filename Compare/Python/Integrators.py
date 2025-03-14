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
    temp_bodies, dt = (three_step_leapfrog(bodies_copy, temp_dt))   # We find the temporary state moving forwards

    temp_dt_backwards = get_variable_dt_helper(temp_bodies, variable_dt_constant)
    average_dt = (temp_dt+temp_dt_backwards)/2

    return average_dt

    
def symplectic_euler(bodies, dt, G=1, variable_dt=False):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    # Finding the acceleration of each body
    for body in bodies:
        body.calculate_acceleration(bodies)

    if variable_dt is True:
        dt = get_variable_dt(bodies, dt)

    for body in bodies:
        body.velocity += dt * body.acceleration   
        body.position += dt * body.velocity 
        
    return bodies, dt
            

def euler(bodies, dt, G=1, variable_dt = False):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for body in bodies:
        body.calculate_acceleration(bodies)
    
    for body in bodies:
        body.position += dt * body.velocity  
        body.velocity += dt * body.acceleration
            
    return bodies, dt

def three_step_leapfrog(bodies, dt, G=1, variable_dt = False):
    """
    The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
    """
    for body in bodies:
        body.calculate_acceleration(bodies)

    if variable_dt is True:
        dt = get_variable_dt(bodies, dt)

    half_velocity = np.zeros((len(bodies), 3), dtype=float)
    for (p, body) in enumerate(bodies):
        half_velocity[p, :] = body.velocity + body.acceleration * dt/2

    for (p, body)  in enumerate(bodies):
        body.position += half_velocity[p,:] * dt
    
    for body in bodies:
        body.calculate_acceleration(bodies)

    for (p, body)  in enumerate(bodies):
        body.velocity = half_velocity[p,:] + body.acceleration * dt/2

    return bodies, dt

def higher_order_helper(c, d, bodies, dt):
    for body in bodies:    
        body.position += c*dt*body.velocity

    for body in bodies:
        body.calculate_acceleration(bodies)

    for body in bodies:
        body.velocity += d*dt*body.acceleration
    return bodies

def yoshida(bodies, dt, G=1, variable_dt = False):
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
        bodies = higher_order_helper(Cs[i], Ds[i], bodies, dt)

    return bodies, dt

def forest_ruth(bodies, dt, G=1, variable_dt = False):
    #Initialising constants
    x = 1/6 * (2**(1/3) + 2**(-1/3)-1)
    Cs = [x+1/2, -x, -x, x+1/2]
    Ds = [2*x+1, -4*x-1, 2*x+1, 0]

    for i in range(0,4):
        bodies = higher_order_helper(Cs[i], Ds[i], bodies, dt)

    return bodies, dt