namespace LogSimulator.Service.Interface
{
    public interface IAppSettings
    {
        string LogsFilePath { get; }

        string AlarmPossibility { get; }

        string SleepMilliseconds { get; }

        string AntivirusThreatsNum { get; }

        string IpAddress1 { get; }

        string IpAddress2 { get; }

        string IpAddress3 { get; }

        string MaliciousIpAddress { get; }

        string UnsuccessfulLoginAttemptNum { get; }

        string Username { get; }

        string LogSequencerCurrentValue { get; set; }

        string AntivirusThreatSequencerCurrentValue { get; set; }

        string MaliciousUsername { get; }

        string AttackRequestNum { get; }

        string Username2 { get; }
    }
}
