param(
    [string]$Version = ((Select-String -LiteralPath "$PSScriptRoot\..\build.gradle" -Pattern "^version = '([^']+)'$" | Select-Object -First 1).Matches.Groups[1].Value)
)

$projectRoot = Resolve-Path "$PSScriptRoot\.."
$required = @(
    "ICUAC-$Version-zh.cn.jar",
    "ICUAC-$Version-en.us.jar"
)

$actual = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot "build\libs") -Filter '*.jar' | Select-Object -ExpandProperty Name)
$unexpected = @($actual | Where-Object { $_ -notin $required })
if ($unexpected.Count -gt 0) {
    throw "Unexpected release artifact(s): $($unexpected -join ', ')"
}

foreach ($name in $required) {
    $path = Join-Path $projectRoot "build\libs\$name"
    if (-not (Test-Path -LiteralPath $path)) {
        throw "Missing release artifact: $name"
    }
    $hash = Get-FileHash -LiteralPath $path -Algorithm SHA256
    Write-Output "$($hash.Hash.ToLower())  $name"
}
