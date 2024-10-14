param ($N = $(throw "N parameter is required for Cpp."), $dt = $(throw "dt parameter is required for Cpp."))

Set-Location .\JavaSimulation
javac .\*.java
Set-Location ..
java javasimulation/Run $N $dt
write-host "Done!"