$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$jdk = "C:\Users\spedi\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
$javac = "$jdk\bin\javac.exe"
$java = "$jdk\bin\java.exe"
$mysqlJar = "..\lib\mysql-connector-j-9.7.0.jar"
$flatlafJar = "..\lib\flatlaf-3.5.4.jar"
$classpath = "$mysqlJar;$flatlafJar"

if (-not (Test-Path $javac)) {
    Write-Host "Bundled JDK not found at $jdk, falling back to PATH javac/java" -ForegroundColor Yellow
    $javac = "javac"
    $java = "java"
}

if (-not (Test-Path $mysqlJar)) {
    Write-Host "ERROR: MySQL connector jar not found at $mysqlJar" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $flatlafJar)) {
    Write-Host "ERROR: FlatLaf jar not found at $flatlafJar" -ForegroundColor Red
    exit 1
}

Write-Host "Compiling..." -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path out | Out-Null
$files = Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -cp $classpath -d out $files
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Starting Car Reservation System..." -ForegroundColor Green
& $java -cp "out;$classpath" view.Main
