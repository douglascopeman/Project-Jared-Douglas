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
        [Parameter(ValueFromRemainingArguments=$true)]
        [string[]] $remainingArgs
)

$javaArgs = @($Orbit, $N, $dt)
$javaArgs += $remainingArgs

try {
        Set-Location .\javasimulation
        javac .\*.java -d .\
        java -cp . javasimulation/Run $javaArgs
        write-host "Done!"
} finally {
        Set-Location ..
}
