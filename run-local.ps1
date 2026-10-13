$ErrorActionPreference = 'Stop'

$projectRoot = $PSScriptRoot
$profilePath = [Environment]::GetFolderPath([Environment+SpecialFolder]::UserProfile)
$repositoryPath = Join-Path $profilePath '.m2\repository'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'A JDK is required. Install Java 17 or newer so both java and javac are available.'
}
if (-not (Test-Path $repositoryPath)) {
    throw "Maven dependencies were not found in $repositoryPath. This script uses cached dependencies and does not download them."
}

$jars = Get-ChildItem $repositoryPath -Recurse -File -Filter '*.jar' |
    Where-Object {
        $_.FullName -notmatch '\\org\\slf4j\\slf4j-api\\1\.7\.36\\' -and
        $_.Name -notmatch '(?i)(devtools|-test(?:-|\.))'
    } |
    ForEach-Object { $_.FullName }

if ($jars.Count -eq 0) {
    throw 'No cached dependency JARs were found. Run the Maven wrapper once with network access, or install Maven and resolve the project dependencies.'
}

$targetRoot = [IO.Path]::GetFullPath((Join-Path $projectRoot 'target'))
$classesPath = [IO.Path]::GetFullPath((Join-Path $targetRoot 'manual-run'))
if (-not $classesPath.StartsWith($targetRoot + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'The compiled classes path must stay inside the project target directory.'
}
if (Test-Path $classesPath) {
    Remove-Item -LiteralPath $classesPath -Recurse -Force
}
New-Item -ItemType Directory -Path $classesPath -Force | Out-Null

$sources = Get-ChildItem (Join-Path $projectRoot 'src\main\java') -Recurse -File -Filter '*.java' |
    ForEach-Object { $_.FullName }
$dependencyClasspath = $jars -join ';'
$runtimeClasspath = @($classesPath, (Join-Path $projectRoot 'src\main\resources')) + $jars -join ';'

Push-Location $projectRoot
try {
    & javac -cp $dependencyClasspath -d $classesPath $sources
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed with exit code $LASTEXITCODE."
    }

    Write-Host 'Starting the app at http://localhost:8080 (press Ctrl+C to stop).'
    & java -cp $runtimeClasspath com.csit321g2.galo_crud.DemoApplication
    if ($LASTEXITCODE -ne 0) {
        throw "The app exited with code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}
