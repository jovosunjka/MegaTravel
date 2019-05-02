namespace LogSimulator.Model.Enum
{
    public enum StateType
    {
        NoAlarm = 0,
        LogError,
        UnsuccessfulLoginsOnDiffHostsWithSameIp,
        SameUsernameLoginOnDiffHostsWithDiffIp,
        AntivirusThreatsOnSameHost,
        LoginWithMaliciousIp
    }
}
