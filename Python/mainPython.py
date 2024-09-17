import numpy as np

class Body:
    x = [0,0,0]
    dx = [0,0,0]
    mass = 1000

earth = Body()
moon =  Body()


def accelerationCalc(spaceData):
    n = len(spaceData)
    acceleration = np.zeros((n,3))
    for body in range(0,n):
        acceleration[body,:] = np.sum(body)

print("Hello World")

jl.println("A Julia Printout")