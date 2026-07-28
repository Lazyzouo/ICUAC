param(
    [string]$Version = ((Select-String -LiteralPath "$PSScriptRoot\..\build.gradle" -Pattern "^version = '([^']+)'$" | Select-Object -First 1).Matches.Groups[1].Value)
)

$projectRoot = Resolve-Path "$PSScriptRoot\.."
$required = @(
    "ICUAC-$Version.jar",
    "ICUAC-$Version-en_US.jar",
    "ICUAC-$Version-sources.jar"
)

foreach ($name in $required) {
    $path = Join-Path $projectRoot "build\libs\$name"
    if (-not (Test-Path -LiteralPath $path)) {
        throw "Missing release artifact: $name"
    }
    $hash = Get-FileHash -LiteralPath $path -Algorithm SHA256
    Write-Output "$($hash.Hash.ToLower())  $name"
}
