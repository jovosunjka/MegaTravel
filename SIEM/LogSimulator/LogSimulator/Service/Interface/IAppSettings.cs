namespace LogSimulator.Service.Interface
{
    public interface IAppSettings
    {
        string LogsFilePath { get; }

        string AlarmPossibility { get; }

        string SleepMilliseconds { get; }

        string AntivirusThreatsNum { get; }

        string HostAddress1 { get; }

        string HostAddress2 { get; }

        string HostAddress3 { get; }

        string IpAddress1 { get; }

        string IpAddress2 { get; }

        string IpAddress3 { get; }

        string MaliciousIpAddress { get; }

        string UnsuccessfulLoginAttemptNum { get; }

        string Username { get; }
    }
}
