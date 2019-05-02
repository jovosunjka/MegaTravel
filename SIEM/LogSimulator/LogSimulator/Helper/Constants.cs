namespace LogSimulator.Helper
{
    public static class Constants
    {
        public const string State = "State";

        public const string StatesNamespace = "LogSimulator.State";

        public static class LogLevel
        {
            public const string ERROR = "ERROR";

            public const string WARN = "WARN";

            public const string INFO = "INFO";

            public const string TRACE = "TRACE";

            public const string DEBUG = "DEBUG";
        }

        public static class StateDescription
        {
            public const string NoAlarm = "No alarm";

            public const string LogError = "Produce some errors!";

            public const string UnsuccessfulLoginsOnDiffHostsWithSameIp = "N unsuccessful logins on different hosts with same ip";

            public const string SameUsernameLoginOnDiffHostsWithDiffIp = "Login with same username on different hosts with different ips";

            public const string AntivirusThreatsOnSameHost = "Antivirus threats on same host";

            public const string LoginWithMaliciousIp = "Login attempt from ip which is on malicious list";
        }
    }
}
