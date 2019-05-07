namespace LogSimulator.Model.Enum
{
    public enum StateType
    {
        NoAlarm = 0,
        LogError,
        LoginAttemptWithCommonIp,
        LoginWithCommonIp,
        AntivirusThreats,
        LoginWithMaliciousIp
    }
}
