<#
.SYNOPSIS
Driver for Kidney Exchange program on PowerShell or PowerShell Core. Requires at least JRE 1.8.

.PARAMETER Seed
Set the RNG seed for the kidney exchange.

.PARAMETER Hospitals
Number of hospitals participating in the exchange.

.PARAMETER Pairs
Initial number of pairs per hospital.

.PARAMETER MaxSurgeries
Max surgeries that can be performed per hospital per time step.

.PARAMETER Rounds
Number of time steps to run the kidney exchange for.

.PARAMETER Algorithm
Matching algorithm to use for kidney exchange. Choose either ILP or Greedy.

.PARAMETER Test
Enable test output.

.PARAMETER DisableIncremental
Disable incremental setting.

.PARAMETER Quiet
Enable quiet mode.

#>
[CmdletBinding()]
param(
    [Parameter()]
    [int] $Seed,

    [Parameter()]
    [int] $Hospitals,

    [Parameter()]
    [int] $Pairs,

    [Parameter()]
    [int] $MaxSurgeries,

    [Parameter()]
    [int] $Rounds,

    [Parameter()]
    [ValidateSet( "Greedy", "ILP" )]
    [string] $Algorithm,

    [Parameter()]
    [switch] $Test,

    [Parameter()]
    [switch] $DisableIncremental,

    [Parameter()]
    [switch] $Quiet
)

$ErrorActionPreference = "Stop"

$java = Get-Command java
$jar = Join-Path $PSScriptRoot "out\artifacts\KidneyExchange_jar\KidneyExchange.jar"

if( -not (Test-Path $jar) ) {
    throw "Missing Kidney Exchange JAR file $jar. Please build the artifact with the IntelliJ project first."
}

$kidneyExchangeArgs = @()
if( $Seed ) { $kidneyExchangeArgs += @("-s", $Seed) }
if( $Hospitals ) { $kidneyExchangeArgs += @("-n", $Hospitals) }
if( $Pairs ) { $kidneyExchangeArgs += @("-p", $Pairs) }
if( $MaxSurgeries ) { $kidneyExchangeArgs += @("-m", $MaxSurgeries) }
if( $Rounds ) { $kidneyExchangeArgs += @("-r", $Rounds) }
if( $Algorithm ) { $kidneyExchangeArgs += @("-a", $Algorithm.ToUpper()) }
if( $Test ) { $kidneyExchangeArgs += @("-t") }
if( $DisableIncremental ) { $kidneyExchangeArgs += @("-i") }
if( $Quiet ) { $kidneyExchangeArgs += @("-q") }

& $java -jar $jar @kidneyExchangeArgs
