param ([Parameter(
                Mandatory=$True,
                Position = 0
                )
        ]
        [string] $Orbit = $(throw "Orbit parameter is required for Java."), 
        [Parameter(
                Mandatory=$True,
                Position = 1
                )
        ]
        [string] $N = $(throw "N parameter is required for Java."),
        [Parameter(
                Mandatory=$True,
                Position = 2
                )
        ]
        [string] $dt = $(throw "dt parameter is required for Java."),

        [Parameter(Mandatory=$False)]
        [string] $integrator = "symplecticEuler",
        [Parameter(Mandatory=$False)]
        [switch] $useVariableTimestep,
        [Parameter(Mandatory=$False)]
        [switch] $checkStopConditions,
        [Parameter(Mandatory=$False)]
        [switch] $calculateCentreOfMass,
        [Parameter(Mandatory=$False)]
        [switch] $calculateEnergies,
        [Parameter(Mandatory=$False)]
        [switch] $calculateAngularMomentum,
        [Parameter(Mandatory=$False)]
        [switch] $calculateLinearMomentum,
        [Parameter(Mandatory=$False)]
        [switch] $findOrbitLength,
        [Parameter(Mandatory=$False)]
        [switch] $perturbPositions,
        [Parameter(Mandatory=$False)]
        [switch] $perturbVelocities,

        [Parameter(Mandatory=$False)]
        [float] $delta,
        [Parameter(Mandatory=$False)]
        [int] $halfGridSize,

        [Parameter(Mandatory=$False)]
        [float] $shiftEnergy
)

$javaArgs = @($Orbit, $N, $dt)
if ($integrator){
        $javaArgs += "-integrator " 
        $javaArgs += $integrator
}
if ($useVariableTimestep) {
        $javaArgs += "-useVariableTimestep"
}
if ($checkStopConditions) {
        $javaArgs += "-checkStopConditions"
}
if ($calculateCentreOfMass) {
        $javaArgs += "-calculateCentreOfMass"
}
if ($calculateEnergies) {
        $javaArgs += "-calculateEnergies"
}
if ($calculateAngularMomentum) {
        $javaArgs += "-calculateAngularMomentum"
}
if ($calculateLinearMomentum) {
        $javaArgs += "-calculateLinearMomentum"
}
if ($findOrbitLength) {
        $javaArgs += "-findOrbitLength"
}
if ($perturbPositions) {
        $javaArgs += "-perturbPositions"
}
if ($perturbVelocities) {
        $javaArgs += "-perturbVelocities"
}
if ($delta) {
        $javaArgs += "-delta " 
        $javaArgs += $delta
}
if ($halfGridSize) {
        $javaArgs += "-halfGridSize " 
        $javaArgs += $halfGridSize
} 
if ($shiftEnergy) {
        $javaArgs += "-shiftEnergy"
        $javaArgs += $shiftEnergy
}

Set-Location .\javasimulation
javac .\*.java -d .\
# Copy-Item -Path .\Outputs\* -Destination .\javasimulation\Outputs -Recurse -Force
java -cp . javasimulation/Run $javaArgs
# Copy-Item -Path .\javasimulation\Outputs\* -Destination .\Outputs -Recurse -Force
Set-Location ..
write-host "Done!"