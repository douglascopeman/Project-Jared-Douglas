from Body import Body
import numpy as np

class orbit_examples:
    
    body0 = Body(np.array([1,0,0], dtype=float), np.array([0,0.5,0], dtype=float))
    body1 = Body(np.array([-1,0,0], dtype=float), np.array([0,-0.5,0], dtype=float))
    circular = [body0, body1]
    
    body0 = Body(np.array([1,0,0], dtype=float), np.array([0,0.6,0], dtype=float))
    body1 = Body(np.array([-1,0,0], dtype=float), np.array([0,-0.6,0], dtype=float))
    elliptical = [body0, body1]
    
    earth = Body(np.array([-5,5,0], dtype=float), np.array([-0.5,0,0], dtype=float), 10)
    moon =  Body(np.array([5,0,0], dtype=float), np.array([0.5,0,0], dtype=float), 10)
    offset_elliptical = [earth, moon]
    
    body0 = Body(np.array([0.97000436,-0.24308753,0], dtype=float), np.array([0.46620368,0.43236573,0], dtype=float))
    body1 = Body(np.array([-0.97000436,0.24308753,0], dtype=float), np.array([0.46620368,0.43236573,0], dtype=float))
    body2 = Body(np.array([0,0,0], dtype=float), np.array([-0.93240737,-0.86473146,0], dtype=float))
    figure_eight = [body0, body1, body2]
    
    body0 = Body(np.array([0.97000436,-0.24308753,0], dtype=float), np.array([0.46620368,0.43236573,5], dtype=float))
    body1 = Body(np.array([-0.97000436,0.24308753,0], dtype=float), np.array([0.46620368,0.43236573,5], dtype=float))
    body2 = Body(np.array([0,0,0], dtype=float), np.array([-0.93240737,-0.86473146,5], dtype=float))
    figure_eight_moving = [body0, body1, body2]
    
if __name__ == "__main__":
    import run