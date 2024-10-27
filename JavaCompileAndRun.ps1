param ($N = $(throw "N parameter is required for Cpp."), $dt = $(throw "dt parameter is required for Cpp."))

Set-Location .\javasimulation
javac .\*.java -d .\
Copy-Item -Path .\Outputs\* -Destination .\javasimulation\Outputs -Recurse -Force
java javasimulation/Run $N $dt
Copy-Item -Path .\javasimulation\Outputs\* -Destination .\Outputs -Recurse -Force
Set-Location ..
write-host "Done!"