param ($N = $(throw "N parameter is required."), $dt = $(throw "dt parameter is required."))

Set-Location .\JavaSimulation
javac .\*.java
Set-Location ..
java javasimulation/Run $N $dt
write-host "Done!"