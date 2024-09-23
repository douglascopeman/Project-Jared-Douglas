from Body import Body
import numpy as np

class orbit_examples:
    
    body0 = Body(np.array([1,0,0], dtype=float), np.array([0,0.5,0], dtype=float))
    body1 = Body(np.array([-1,0,0], dtype=float), np.array([0,-0.5,0], dtype=float))
    circular = [body0, body1]
    
    earth = Body(np.array([-5,5,0], dtype=float), np.array([-0.5,0,0], dtype=float), 10)
    moon =  Body(np.array([5,0,0], dtype=float), np.array([0.5,0,0], dtype=float), 10)
    offset_elliptical = [earth, moon]