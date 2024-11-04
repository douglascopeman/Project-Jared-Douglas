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
        [switch] $perturbate,
        [Parameter(Mandatory=$False)]
        [float] $delta = 0.01,
        [Parameter(Mandatory=$False)]
        [int] $halfGridSize = 1
)

$javaArgs = @($Orbit, $N, $dt)
if ($integrator){
        $javaArgs += "-integrator " + $integrator
}

if ($useVariableTimestep) {
        $javaArgs += "-useVariableTimestep"
}
if ($checkStopConditions) {
        $javaArgs += "-checkStopConditions"
}
if ($calculateCenterOfMass) {
        $javaArgs += "-calculateCenterOfMass"
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
if ($perturbate) {
        $javaArgs += "-perturbate"
}
if ($delta) {
        $javaArgs += "-delta " + $delta
}
if ($halfGridSize) {
        $javaArgs += "-halfGridSize " + $halfGridSize
}

Set-Location .\javasimulation
javac .\*.java -d .\
# Copy-Item -Path .\Outputs\* -Destination .\javasimulation\Outputs -Recurse -Force
java -cp . javasimulation/Run $javaArgs
# Copy-Item -Path .\javasimulation\Outputs\* -Destination .\Outputs -Recurse -Force
Set-Location ..
write-host "Done!"