# upload.ps1 — runs the WinSCP script in project root
$scriptPath = Join-Path $PSScriptRoot 'upload-winSCP.txt'
$winscpPaths = @(
    'C:\Program Files (x86)\WinSCP\winscp.com',
    'C:\Program Files\WinSCP\winscp.com'
)
$winscp = $winscpPaths | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $winscp) {
    Write-Error 'WinSCP not found. Install WinSCP or update the path inside this script.'
    exit 1
}
Write-Output "Running WinSCP: $winscp /script=$scriptPath"
& $winscp /script=$scriptPath
