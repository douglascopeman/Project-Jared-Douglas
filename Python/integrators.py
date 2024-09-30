import numpy as np 
import Simulation

    
def symplecticEuler(bodies, acceleration, dt, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    
    if variable_dt_constant is None:
        for (i, body) in enumerate(bodies):
            body.velocity += dt * acceleration[i, :]
            body.position += dt * body.velocity  
    else:
        for (i, body) in enumerate(bodies):
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.velocity += dt * acceleration[i, :]
            body.position += dt * body.velocity      
    
    return bodies

def Euler(bodies, acceleration, dt):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for (i, body) in enumerate(bodies):
        body.position += dt * body.velocity  
        body.velocity += dt * acceleration[i, :]
            
    return bodies

def ThreeStepLeapFrog(bodies, acceleration, dt, N):
    """
    The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
    """
    halfVelocity = np.zeros((len(bodies), 3), dtype=float)
    for (i, body) in enumerate(bodies):
        halfVelocity[i, :] = body.velocity + acceleration[i,:]*dt/2
        body.position += halfVelocity*dt
    
    simulation = Simulation.Simulation(N, dt, bodies)
    acceleration[len(bodies), 3] = simulation.calculateAccelerations

    for (i, body)  in enumerate(bodies):
        body.velocity = halfVelocity[i,:] + acceleration[i,:] * dt/2

    print("Three step leapfrog")

    return bodies
