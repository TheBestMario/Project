# Get the directory of the script
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Definition

Write-Output "The script is located in: $SCRIPT_DIR"

# Function to check if a port is open
function TestPort {
    param (
        [string]$hostname,
        [int]$port
    )
    try {
        $tcpConnection = Test-NetConnection -ComputerName $hostname -Port $port
        return $tcpConnection.TcpTestSucceeded
    } catch {
        return $false
    }
}
$port = 8000
# Check if the database is accessible
if (TestPort -hostname "localhost" -port $port) {
    Write-Output "Database is accessible on port $port"
} else {
    Write-Output "Database is not accessible. Setting up a local Docker container with docker-compose..."

    # Navigate to the directory containing docker-compose.yml
    Push-Location -Path $SCRIPT_DIR

    # Run docker-compose to start the services
    docker-compose up -d

    # Wait for the SQL Server to start
    while (-not (TestPort -hostname "localhost" -port $port)) {
        Write-Output "Waiting for SQL Server to start..."
        Start-Sleep -Seconds 1
    }

    if (TestPort -hostname "localhost" -port $port) {
        Write-Output "Local Docker container with SQL Server is set up and accessible."

        # Read the schema from the .txt file
        $schema = Get-Content -Path "schema.txt" -Raw

        # Execute the schema on the database
        docker-compose exec -T calendarDB /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $env:DB_PASSWORD -Q $schema
    } else {
        Write-Output "Failed to set up the local Docker container with SQL Server."
        exit 1
    }

    # Return to the original directory
    Pop-Location
}