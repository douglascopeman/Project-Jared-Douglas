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
        Set-Location .\javacompare
        # javac -source 11 -target 11 .\*.java -d .\
        javac --release 11 .\*.java -d .\
        $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
                java -cp . javasimulation/Run $javaArgs
        $stopwatch.Stop()
        $elapsed = $stopwatch.Elapsed
        write-host ""
        write-host "Done!"
        write-host "Elapsed time: $($elapsed.Hours)h $($elapsed.minutes)m $($elapsed.seconds)s."
} finally {
        Set-Location ..
}
