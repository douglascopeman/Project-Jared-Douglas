param ([Parameter(
                Mandatory=$True,
                Position = 0
                )
        ][int] $N = $(throw "N parameter is required for Java."), 
        [Parameter(
                Mandatory=$True,
                Position = 1
                )
        ][double] $dt = $(throw "dt parameter is required for Java."),
        [Parameter(
                Mandatory=$False,
                ValueFromRemainingArguments=$true,
                Position = 2
                )
        ][string[]] $listArgs
        )

Set-Location .\javasimulation
javac .\*.java -d .\
Copy-Item -Path .\Outputs\* -Destination .\javasimulation\Outputs -Recurse -Force
java javasimulation/Run $N $dt $listArgs
Copy-Item -Path .\javasimulation\Outputs\* -Destination .\Outputs -Recurse -Force
Set-Location ..
write-host "Done!"